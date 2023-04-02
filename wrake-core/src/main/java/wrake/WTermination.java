package wrake;

import java.util.function.Predicate;

public class WTermination<T> {

    private Predicate<T> predicate;
    private WRake.TermRes termRes;

    public WRake.TermRes getTermRes() {
        return termRes;
    }

    public WTermination<T> setTermRes(WRake.TermRes termRes) {
        this.termRes = termRes;
        return this;
    }

    public Predicate<T> getPredicate() {
        return predicate;
    }

    public WTermination<T> setPredicate(Predicate<T> predicate) {
        this.predicate = predicate;
        return this;
    }
}
