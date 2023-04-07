package wrake;

import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.jctools.queues.MpscArrayQueue;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Supplier;

public class WRake<R> {

    private final List<WTask<?>> tasks = new ArrayList<>(100);
    private final Queue<Node> readyQueue = new MpscArrayQueue<>(100);//thread-safe
    private final AtomicInteger total = new AtomicInteger(0);
    private final AtomicInteger finished = new AtomicInteger(0);
    private final AtomicReference<Optional<R>> taskTermRes = new AtomicReference<>(Optional.empty());
    //避免结束后仍被线程池任务唤醒影响下次编排
    private volatile boolean exit;
    private final AtomicReference<Throwable> throwable = new AtomicReference<>();
    private final AtomicLong waited = new AtomicLong(0);

    //构建node依赖网格
    private void buildGridAndReadyQueue() {
        readyQueue(buildGrid());
    }

    private void readyQueue(Map<WTask<?>, Node> tnMap) {
        for (Map.Entry<WTask<?>, Node> ety : tnMap.entrySet()) {
            if (CollectionUtils.isEmpty(ety.getValue().getPrevNodes())) {
                readyQueue.offer(ety.getValue());
            }
        }
    }

    private Map<WTask<?>, Node> buildGrid() {
        Map<WTask<?>, Node> tnMap = Maps.newHashMapWithExpectedSize(16);
        for (WTask<?> task : tasks) {
            Node node = tnMap.get(task);
            if (node != null) {
                continue;
            }

            node = new Node().setTask(task);
            tnMap.put(task, node);
            if (CollectionUtils.isEmpty(task.getDepends())) {
                continue;
            }
            for (WTask<?> depend : task.getDepends()) {
                Node dep = tnMap.computeIfAbsent(
                        depend, wTask -> new Node().setTask(depend)
                );
                node.addPrev(dep);
                dep.addNext(node);
            }
        }
        return tnMap;
    }


    public R fire() throws Throwable {
        return doFire(ForkJoinPool.commonPool(), null);
    }

    public R fire(long waitMs) throws Throwable {
        return doFire(ForkJoinPool.commonPool(), TimeUnit.MILLISECONDS.toNanos(waitMs));
    }

    public R fire(ExecutorService executor) throws Throwable {
        return doFire(executor, null);
    }

    //recommend
    public R fire(ExecutorService executor, long waitMs) throws Throwable {
        return doFire(executor, TimeUnit.MILLISECONDS.toNanos(waitMs));
    }

    public R fire(ExecutorService executor, long time, TimeUnit timeUnit) throws Throwable {
        return doFire(executor, timeUnit.toNanos(time));
    }

    private long anchor;

    private R doFire(ExecutorService executor, Long waitNs) throws Throwable {
        if (waitNs != null) {
            anchor = System.nanoTime();
        }
        try {
            //主线程充当扫描线程，线程池充当工作线程
            buildGridAndReadyQueue();
            do {
                while (!readyQueue.isEmpty()) {
                    dispatch(executor, readyQueue.poll());
                }
                doPark(waitNs, false);
            } while (!needBreak());
        } finally {
            doFinally(waitNs);
        }
        return getR();
    }

    private R getR() throws Throwable {
        if (throwable.get() != null) {
            throw throwable.get();
        }
        return getTaskTermRes();
    }

    private void doFinally(Long waitNs) {
        synchronized (this) {
            setExit();
            LockSupport.unpark(Thread.currentThread());
            doPark(waitNs, true);
        }
    }

    private boolean needBreak() {
        return finished() || termed() || existThrowable();
    }

    private void setExit() {
        this.exit = true;
    }

    private boolean isExit() {
        return exit;
    }

    private void doPark(Long waitNs, boolean selfPark) {
        if (waitNs == null || selfPark) {
            LockSupport.park(this);
        } else {
            long waitTime = waitNs - waited.addAndGet(System.nanoTime() - anchor);
            if (waitTime <= 0) {
                throw new RuntimeException("waited timeout. waited:" + waited.get() + " >= max:" + waitNs + " ns");
            }
            long waitStart = System.nanoTime();
            LockSupport.parkNanos(this, waitTime);
            anchor = System.nanoTime();
            long parkedTime = waited.addAndGet(anchor - waitStart);
            if (parkedTime > waitNs) {
                throw new RuntimeException("waited timeout. waited:" + parkedTime + " >= max:" + waitNs + " ns");
            }
        }
    }

    private boolean finished() {
        return finished.get() == total.get();
    }

    private boolean termed() {
        return taskTermRes.get() != Optional.empty();
    }

    private void dispatch(ExecutorService executor, Node node) {
        Thread thread = Thread.currentThread();
        executor.submit(() -> {
            try {
                runNode(node, thread);
            } catch (Throwable e) {
                throwable.compareAndSet(null, e);
                unPark(thread);
            }
        });

    }

    private void runNode(Node node, Thread thread) throws Throwable {
        boolean needUnPark = true;
        try {
            if (termed() || isExit() || existThrowable()) {
                // 中断：至少有过一次唤醒，主线程会检验中断并退出，此次不需要执行且不需要唤醒
                // 退出：主线程已经退出，不需要执行且唤醒。
                // 异常：至少有过一次唤醒，主线程会检验异常并退出，此次不需要执行且不需要唤醒
                needUnPark = false;
                return;
            }
            Boolean runRes = node.run(this);
            if (runRes == null) {
                // 抢占失败
                needUnPark = false;
                return;
            }
            if (!runRes) {
                // 执行失败：中断或异常，此时需要唤醒，且直接返回
                return;
            }
            if (isExit()) {
                needUnPark = false;
                return;
            }
            needUnPark = incrAndQueue(node);
        } catch (Throwable e) {
            // 抛出异常时，在调用层再唤醒
            needUnPark = false;
            throw e;
        } finally {
            if (needUnPark) {
                unPark(thread);
            }
        }
    }

    private boolean existThrowable() {
        return throwable.get() != null;
    }

    private void unPark(Thread thread) {
        if (!isExit()) {
            synchronized (this) {
                if (!isExit()) {
                    LockSupport.unpark(thread);
                }
            }
        }
    }

    private boolean incrAndQueue(Node node) {
        if (finished.incrementAndGet() == total.get()) {
            return true;
        }
        Set<Node> ns = node.getNextNodes();
        boolean needUnPark = false;
        if (ns != null) {
            for (Node n : ns) {
                if (!n.isFinish() && n.isReady()) {
                    readyQueue.add(n);
                    needUnPark = true;
                }
            }
        }
        return needUnPark;
    }

    public <T> WTask<T> defTask(@Nonnull Callable<T> callable) {
        WTask<T> task = new WTask<T>().setCallable(callable);
        tasks.add(task);
        total.incrementAndGet();
        return task;
    }

    public <T> WBackUpTask<T> defBackUpTask(@Nonnull Callable<T> callable) {
        WBackUpTask<T> task = new WBackUpTask<>();
        task.setCallable(callable);
        return task;
    }

    /**
     * 使用wRake.termResult生成实例
     */
    public static class TermRes {
        private Object res;

        private TermRes() {
        }

        public TermRes(Object res) {
            this.res = res;
        }

        public Object getRes() {
            return res;
        }

        public TermRes setRes(Object res) {
            this.res = res;
            return this;
        }
    }

    public TermRes termResult(Supplier<R> supplier) {
        return new TermRes().setRes(supplier.get());
    }

    private R getTaskTermRes() {
        return taskTermRes.get().orElse(null);
    }

    protected void casTaskTermRes(R termRes) {
        this.taskTermRes.compareAndSet(Optional.empty(), Optional.ofNullable(termRes));
    }

}
