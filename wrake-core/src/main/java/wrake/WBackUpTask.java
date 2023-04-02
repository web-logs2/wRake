package wrake;

import java.util.function.Predicate;

public class WBackUpTask<T> extends WTask<T>{

    protected WBackUpTask() {
    }

    @Override
    public WBackUpTask<T> defDepends(WTask<?>... task) {
        super.defDepends(task);
        return this;
    }

    @Override
    public WBackUpTask<T> defBackup(Predicate<T> predicate, WBackUpTask<T> backup) {
        super.defBackup(predicate, backup);
        return this;
    }

    @Override
    public WBackUpTask<T> defTerm(Predicate<T> predicate, WRake.TermRes termRes) {
        super.defTerm(predicate, termRes);
        return this;
    }

}

