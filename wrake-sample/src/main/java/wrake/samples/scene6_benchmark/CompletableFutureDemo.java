package wrake.samples.scene6_benchmark;

import com.google.common.collect.Lists;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureDemo {


    public static void main(String[] args) throws ExecutionException, InterruptedException {

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
        res.get();

        System.out.println(t9.get());
    }
}
