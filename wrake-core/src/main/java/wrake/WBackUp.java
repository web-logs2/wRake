package wrake;

import java.util.function.Predicate;

public class WBackUp<T> {

    private Predicate<T> predicate;
    private WTask<T> task;

    private WBackUp() {
    }

    public static <T> WBackUp<T> of(Predicate<T> predicate, WTask<T> task){
        return new WBackUp<T>().setPredicate(predicate).setTask(task);
    }

    public Predicate<T> getPredicate() {
        return predicate;
    }

    public WBackUp<T> setPredicate(Predicate<T> predicate) {
        this.predicate = predicate;
        return this;
    }

    public WTask<T> getTask() {
        return task;
    }

    public WBackUp<T> setTask(WTask<T> task) {
        this.task = task;
        return this;
    }

    @Override
    public String toString() {
        return "WBackup{" +
                "predicate=" + predicate +
                ", task=" + task +
                '}';
    }
}
