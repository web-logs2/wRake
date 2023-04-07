package wrake.samples.scene6_benchmark;

import wrake.samples.utils.ThreadUtils;

public class CompareJVM {

    public static void main(String[] args) {


        for (int i = 0; i < 1000; i++) {
            if (i%100==0) {
                System.out.println("original:" + i);
            }
            Scene6_Benchmark.original();
        }

        System.gc();
        System.out.println("original done ... sleep 10s");
        ThreadUtils.sleepQuite(10000);

        for (int i = 0; i < 1000; i++) {

            if (i%100==0) {
                System.out.println("wrake:" + i);
            }
            Scene6_Benchmark.wrake();
        }

        System.gc();
        System.out.println("wrake done ... sleep 10s");
        ThreadUtils.sleepQuite(10000);

        for (int i = 0; i < 1000; i++) {

            if (i%100==0) {
                System.out.println("completableFuture:" + i);
            }
            Scene6_Benchmark.completableFuture();
        }

    }
}
