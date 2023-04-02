package wrake;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

public class WTask<T> {
    
    private String name;
    private Callable<T> callable;
    private List<WTask<?>> depends;
    /**
     * 初始状态
     */
    public static final int TASK_STATE_INIT = 0;
    /**
     * 条件终止
     */
    public static final int TASK_STATE_TERMINATE = 1;
    /**
     * 正常结束
     */
    public static final int TASK_STATE_FINISH = 2;
    /**
     * 异常结束
     */
    public static final int TASK_STATE_EXCEPTION = 3;

    private int taskState = TASK_STATE_INIT;
    private T result;
    private Object termRes;//强转，避免过多引入泛型
    private List<WBackUp<T>> backUps;
    private List<WTermination<T>> terminates;

    protected WTask() {
    }

    public int run() throws Throwable {
        try {
            doRun();
            handleBackup();
            handleTerm();
        } catch (Exception e) {
            setTaskState(TASK_STATE_EXCEPTION);
            throw e;
        }
        return getTaskState();
    }

    private void doRun() throws Exception {
        result = callable.call();
        setTaskState(TASK_STATE_FINISH);
    }

    private void handleBackup() throws Throwable {
        if (CollectionUtils.isEmpty(backUps)) {
            return;
        }
        for (WBackUp<T> backUp : backUps) {
            if (!backUp.getPredicate().test(result)) {
                continue;
            }
            handleBackup(backUp);
            break;
        }
    }

    //状态传递
    private void handleBackup(WBackUp<T> backUp) throws Throwable {
        int backState = backUp.getTask().run();
        switch (backState) {
            case TASK_STATE_INIT:
            case TASK_STATE_EXCEPTION:
                System.out.println("UnExpect BackUpState:" + backState);
                break;
            case TASK_STATE_TERMINATE://term状态传递
                setTermRes(backUp.getTask().getTermRes());
                setResult(null);
                setTaskState(TASK_STATE_TERMINATE);
                break;
            case TASK_STATE_FINISH://finish状态传递
                setTermRes(null);
                setResult(backUp.getTask().get());
                setTaskState(TASK_STATE_FINISH);
                break;
        }

    }

    public WTask<T> setName(String name) {
        this.name = name;
        return this;
    }

    public WTask<T> setCallable(Callable<T> callable) {
        this.callable = callable;
        return this;
    }

    protected void setResult(T result) {
        this.result = result;
    }

    protected void setTaskState(int taskState) {
        this.taskState = taskState;
    }

    protected int getTaskState() {
        return taskState;
    }

    protected void setTermRes(Object termRes) {
        this.termRes = termRes;
    }

    protected Object getTermRes() {
        return termRes;
    }

    private void handleTerm() {
        if (termed()) {
            return;
        }
        if (CollectionUtils.isEmpty(terminates)) {
            return;
        }
        for (WTermination<T> terminate : terminates) {
            if (!terminate.getPredicate().test(result)) {
                continue;
            }
            setTermRes(terminate.getTermRes().getRes());
            setTaskState(TASK_STATE_TERMINATE);
            break;
        }
    }

    private boolean termed() {
        return getTaskState() == TASK_STATE_TERMINATE || getTaskState() == TASK_STATE_EXCEPTION;
    }

    public WTask<T> defDepends(WTask<?>... task) {
        if (depends == null) {
            depends = Lists.newArrayListWithExpectedSize(8);
        }
        depends.addAll(Lists.newArrayList(task));
        return this;
    }

    protected List<WTask<?>> getDepends() {
        return depends;
    }

    public T get() {
        return result;
    }
    public WTask<T> defBackup(Predicate<T> predicate, WBackUpTask<T> backup) {
        if (backUps == null) {
            backUps = Lists.newArrayListWithExpectedSize(4);
        }
        backUps.add(WBackUp.of(predicate, backup));
        return this;
    }

    /**
     * 定义终止条件
     */
    public WTask<T> defTerm(Predicate<T> predicate, WRake.TermRes termRes) {
        if (terminates == null) {
            terminates = Lists.newArrayListWithExpectedSize(4);
        }
        terminates.add(new WTermination<T>().setPredicate(predicate).setTermRes(termRes));
        return this;
    }

    @Override
    public String toString() {
        return "WTask{" +
                "name='" + name + '\'' +
                ", taskState=" + taskState +
                ", result=" + result +
                ", termRes=" + termRes +
                '}';
    }
}
