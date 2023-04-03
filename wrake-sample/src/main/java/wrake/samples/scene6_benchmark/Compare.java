package wrake.samples.scene6_benchmark;

public class Compare {


    public static void main(String[] args) {
        for (int i = 0; i < 1; i++) {
//            long start = System.currentTimeMillis();
            System.out.println("1:" + Scene6_Benchmark.original());
//            System.out.println(System.currentTimeMillis() - start + " ms");
            System.out.println("2:" + Scene6_Benchmark.wrake());
            System.out.println("3:" + Scene6_Benchmark.completableFuture());
        }
    }
}
