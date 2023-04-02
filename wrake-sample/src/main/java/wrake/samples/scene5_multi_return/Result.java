package wrake.samples.scene5_multi_return;

public class Result <T>{

    private T data;
    private boolean succ;
    private String failMsg;

    public static <T> Result<T> succ(T t) {
        return new Result<T>().setData(t).setSucc(true);
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<T>().setData(null).setSucc(false).setFailMsg(msg);
    }

    public T getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    public boolean isSucc() {
        return succ;
    }

    public Result<T> setSucc(boolean succ) {
        this.succ = succ;
        return this;
    }

    public String getFailMsg() {
        return failMsg;
    }

    public Result<T> setFailMsg(String failMsg) {
        this.failMsg = failMsg;
        return this;
    }

    @Override
    public String toString() {
        return "Result{" +
                "data=" + data +
                ", succ=" + succ +
                ", failMsg='" + failMsg + '\'' +
                '}';
    }


}
