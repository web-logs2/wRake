package wrake.samples.scene3_mix;


import wrake.WRake;
import wrake.WTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 任务之间，混合依赖，并行+串行
 *
 */
public class Scene3_mix {


    public static void main(String[] args) throws Throwable {
        ExecutorService executorService = Executors.newFixedThreadPool(54);

        for (int i = 0; i < 100; i++) {
            doTest(executorService);
        }

        executorService.shutdown();

    }

    private static void doTest(ExecutorService executorService) throws Throwable {
        long start = System.currentTimeMillis();

        WRake<Void> wRake = new WRake<>();

        WTask<String> t1 = wRake.defTask(Methods::fun1).setName("t1");
        WTask<String> t2 = wRake.defTask(Methods::fun2).setName("t2");
        WTask<String> t3 = wRake.defTask(Methods::fun3).setName("t3");
        WTask<String> t4 = wRake.defTask(() -> Methods.fun4(t2.get(), t3.get())).defDepends(t2, t3).setName("t4");
        WTask<String> t5 = wRake.defTask(() -> Methods.fun5(t3.get())).defDepends(t3).setName("t5");
        WTask<String> t6 = wRake.defTask(() -> Methods.fun6(t1.get())).defDepends(t1).setName("t6");
        WTask<String> t7 = wRake.defTask(() -> Methods.fun7(t1.get(), t4.get(), t5.get())).defDepends(t1, t4, t5).setName("t7");
        WTask<String> t8 = wRake.defTask(() -> Methods.fun8(t5.get())).defDepends(t5).setName("t8");
        WTask<String> t9 = wRake.defTask(() -> Methods.fun9(t7.get())).defDepends(t7).setName("t9");
        WTask<String> t10 = wRake.defTask(() -> Methods.fun10(t7.get(), t8.get())).defDepends(t7, t8).setName("t10");


        wRake.fire(executorService);

        System.out.println(System.currentTimeMillis() - start + " ms");
        System.out.println(t1.get());
        System.out.println(t2.get());
        System.out.println(t3.get());
        System.out.println(t4.get());
        System.out.println(t5.get());
        System.out.println(t6.get());
        System.out.println(t7.get());
        System.out.println(t8.get());
        System.out.println(t9.get());
        System.out.println(t10.get());

        System.out.println("done ...");
    }

}
