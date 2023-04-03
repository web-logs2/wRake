package wrake;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Node {

    public static final int STATE_INIT = 0;
    public static final int STATE_RUNNING = 1;
    public static final int STATE_FINISH = 2;
    private final AtomicInteger state = new AtomicInteger(STATE_INIT);
    private WTask<?> task;
    private Set<Node> prevNodes;
    private final AtomicInteger finishedPrev = new AtomicInteger();
    private Set<Node> nextNodes;

    /**
     * 执行任务，并返回执行结果
     * @param wRake
     * @return null抢占失败，true正常执行，false异常执行
     * @param <R>
     * @throws Throwable
     */
    public <R> Boolean run(WRake<R> wRake) throws Throwable {
        if (!state.compareAndSet(STATE_INIT, STATE_RUNNING)) {
            return null;
        }
        try {
            int taskState = task.run();
            if (needTerm(taskState)) {
                @SuppressWarnings("unchecked")
                R termRes = (R) task.getTermRes();
                wRake.casTaskTermRes(termRes);
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        } catch (Throwable throwable) {
            state.set(STATE_FINISH);
            throw throwable;
        } finally {
            state.set(STATE_FINISH);
            markFinishForNextNodes(nextNodes);
        }
    }

    private void markFinishForNextNodes(Set<Node> nextNodes) {
        if (nextNodes == null) {
            return;
        }
        nextNodes.forEach(Node::incFinishPreNodeCount);
    }

    private void incFinishPreNodeCount() {
        finishedPrev.incrementAndGet();
    }

    private boolean needTerm(int taskState) {
        return taskState == WTask.TASK_STATE_TERMINATE || taskState == WTask.TASK_STATE_EXCEPTION;
    }

    public WTask<?> getTask() {
        return task;
    }

    private String name;

    public String getName() {
        return name;
    }

    public Node setName(String name) {
        this.name = name;
        return this;
    }

    public int getState() {
        return state.get();
    }

    public Set<Node> getPrevNodes() {
        return prevNodes;
    }

    public Set<Node> getNextNodes() {
        return nextNodes;
    }

    public Node setTask(WTask<?> task) {
        this.task = task;
        return this;
    }


    public boolean isReady() {
        if (prevNodes == null) {
            return true;
        }
        return prevNodes.size() == finishedPrev.get();
    }

    public boolean isFinish() {
        return this.state.get() == STATE_FINISH;
    }

    public void addPrev(Node pre) {
        if (prevNodes == null) {
            prevNodes = Sets.newHashSetWithExpectedSize(8);
        }
        prevNodes.add(pre);
    }

    public void addNext(Node next) {
        if (nextNodes == null) {
            nextNodes = Sets.newHashSetWithExpectedSize(8);
        }
        nextNodes.add(next);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equal(task, node.task);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(task);
    }
}
