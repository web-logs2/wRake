package wrake.samples.scene6_benchmark;

import com.google.common.collect.Lists;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import wrake.WRake;
import wrake.WTask;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.openjdk.jmh.annotations.Mode.AverageTime;


@BenchmarkMode({
//        Throughput,
        AverageTime
})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 4, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@Threads(2)
public class Scene6_Benchmark {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Scene6_Benchmark.class.getSimpleName())
                .resultFormat(ResultFormatType.JSON)
                .build();

        new Runner(opt).run();
    }

    static ExecutorService wrakeExecutors = Executors.newFixedThreadPool(4);
    static ExecutorService completableFutureExecutors = Executors.newFixedThreadPool(4);

    @TearDown
    public void finish() {
        wrakeExecutors.shutdown();
        completableFutureExecutors.shutdown();
    }

    /*
     * start
     *
     *       1
     *     2   3
     *  4    5   6
     *     7   8
     *       9
     *
     * end
     */

    @Benchmark()
    public static Result<String> original() {
        try {
            String fun1 = Methods.fun("fun1");
            String fun2 = Methods.fun("fun2", fun1);
            String fun3 = Methods.fun("fun3", fun1);
            String fun4 = Methods.fun("fun4", fun2);
            String fun5 = Methods.fun("fun5", fun2, fun3);
            String fun6 = Methods.fun("fun6", fun3);
            String fun7 = Methods.fun("fun7", fun4, fun5);
            String fun8 = Methods.fun("fun8", fun5, fun6);
            String fun9 = Methods.fun("fun9", fun7, fun8);
            return Result.succ(fun9);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public static Result<String> wrake() {
        WRake<Result<String>> rake = new WRake<>();

        WTask<String> t1 = rake.defTask(() -> Methods.fun("fun1"));
        WTask<String> t2 = rake.defTask(() -> Methods.fun("fun2", t1.get())).defDepends(t1);
        WTask<String> t3 = rake.defTask(() -> Methods.fun("fun3", t1.get())).defDepends(t1);
        WTask<String> t4 = rake.defTask(() -> Methods.fun("fun4", t2.get())).defDepends(t2);
        WTask<String> t5 = rake.defTask(() -> Methods.fun("fun5", t2.get(), t3.get())).defDepends(t2, t3);
        WTask<String> t6 = rake.defTask(() -> Methods.fun("fun6", t3.get())).defDepends(t3);
        WTask<String> t7 = rake.defTask(() -> Methods.fun("fun7", t4.get(), t5.get())).defDepends(t4, t5);
        WTask<String> t8 = rake.defTask(() -> Methods.fun("fun8", t5.get(), t6.get())).defDepends(t5, t6);
        WTask<String> t9 = rake.defTask(() -> Methods.fun("fun9", t7.get(), t8.get())).defDepends(t7, t8);

        Result<String> res = null;
        try {
            res = rake.fire(wrakeExecutors,500);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        if (res != null) {
            return res;
        }

        return Result.succ(t9.get());
    }

    @Benchmark
    public static Result<String> wrakeDefaultPool() {
        WRake<Result<String>> rake = new WRake<>();

        WTask<String> t1 = rake.defTask(() -> Methods.fun("fun1"));
        WTask<String> t2 = rake.defTask(() -> Methods.fun("fun2", t1.get())).defDepends(t1);
        WTask<String> t3 = rake.defTask(() -> Methods.fun("fun3", t1.get())).defDepends(t1);
        WTask<String> t4 = rake.defTask(() -> Methods.fun("fun4", t2.get())).defDepends(t2);
        WTask<String> t5 = rake.defTask(() -> Methods.fun("fun5", t2.get(), t3.get())).defDepends(t2, t3);
        WTask<String> t6 = rake.defTask(() -> Methods.fun("fun6", t3.get())).defDepends(t3);
        WTask<String> t7 = rake.defTask(() -> Methods.fun("fun7", t4.get(), t5.get())).defDepends(t4, t5);
        WTask<String> t8 = rake.defTask(() -> Methods.fun("fun8", t5.get(), t6.get())).defDepends(t5, t6);
        WTask<String> t9 = rake.defTask(() -> Methods.fun("fun9", t7.get(), t8.get())).defDepends(t7, t8);

        Result<String> res = null;
        try {
            res = rake.fire();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        if (res != null) {
            return res;
        }

        return Result.succ(t9.get());
    }

    @Benchmark
    public static Result<String> completableFuture(){

        CompletableFuture<String> t1 = CompletableFuture.supplyAsync(() -> Methods.fun("fun1"), completableFutureExecutors);
        CompletableFuture<String> t2 = t1.thenComposeAsync(s -> CompletableFuture.supplyAsync(() -> Methods.fun("fun2", s)), completableFutureExecutors);
        CompletableFuture<String> t3 = t1.thenComposeAsync(s -> CompletableFuture.supplyAsync(() -> Methods.fun("fun3", s)), completableFutureExecutors);
        CompletableFuture<String> t4 = t2.thenComposeAsync(s -> CompletableFuture.supplyAsync(() -> Methods.fun("fun4", s)), completableFutureExecutors);
        CompletableFuture<String> t5 = t2.thenCombineAsync(t3, (s, s2) -> Methods.fun("fun5", s, s2), completableFutureExecutors);
        CompletableFuture<String> t6 = t3.thenComposeAsync(s -> CompletableFuture.supplyAsync(() -> Methods.fun("fun6", s)), completableFutureExecutors);
        CompletableFuture<String> t7 = t4.thenCombineAsync(t5, (s, s2) -> Methods.fun("fun7", s, s2), completableFutureExecutors);
        CompletableFuture<String> t8 = t5.thenCombineAsync(t6, (s, s2) -> Methods.fun("fun8", s, s2), completableFutureExecutors);
        CompletableFuture<String> t9 = t7.thenCombineAsync(t8, (s, s2) -> Methods.fun("fun9", s, s2), completableFutureExecutors);

        CompletableFuture<?>[] completableFutures = Lists.newArrayList(t1, t2, t3, t4, t5, t6, t7, t8, t9)
                .toArray(new CompletableFuture<?>[0]);
        CompletableFuture<Void> res = CompletableFuture.allOf(completableFutures);
        try {
            res.get();
            return Result.succ(t9.get());
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }


    @Benchmark
    public static Result<String> completableFutureDefaultPool(){

        CompletableFuture<String> t1 = CompletableFuture.supplyAsync(() -> Methods.fun("fun1"));
        CompletableFuture<String> t2 = t1.thenComposeAsync(s -> CompletableFuture.supplyAsync(() -> Methods.fun("fun2", s)));
        CompletableFuture<String> t3 = t1.thenComposeAsync(s -> CompletableFuture.supplyAsync(() -> Methods.fun("fun3", s)));
        CompletableFuture<String> t4 = t2.thenComposeAsync(s -> CompletableFuture.supplyAsync(() -> Methods.fun("fun4", s)));
        CompletableFuture<String> t5 = t2.thenCombineAsync(t3, (s, s2) -> Methods.fun("fun5", s, s2));
        CompletableFuture<String> t6 = t3.thenComposeAsync(s -> CompletableFuture.supplyAsync(() -> Methods.fun("fun6", s)));
        CompletableFuture<String> t7 = t4.thenCombineAsync(t5, (s, s2) -> Methods.fun("fun7", s, s2));
        CompletableFuture<String> t8 = t5.thenCombineAsync(t6, (s, s2) -> Methods.fun("fun8", s, s2));
        CompletableFuture<String> t9 = t7.thenCombineAsync(t8, (s, s2) -> Methods.fun("fun9", s, s2));


        CompletableFuture<?>[] completableFutures = Lists.newArrayList(t1, t2, t3, t4, t5, t6, t7, t8, t9)
                .toArray(new CompletableFuture<?>[0]);
        CompletableFuture<Void> res = CompletableFuture.allOf(completableFutures);
        try {
            res.get();
            return Result.succ(t9.get());
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }



}


