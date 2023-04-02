package wrake.samples.scene4_mix_random;

import wrake.WRake;
import wrake.WTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 任务之间，混合依赖，并行+串行
 */
public class Scene4_mixRandom {


    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            try {
                System.out.println("--------------begin--------------");
                doRake(executorService);
            } catch (Throwable e) {
//                e.printStackTrace();
            }finally {
                System.out.println("-------------- end --------------");
            }
        }
        System.out.println("end cost:" + (System.nanoTime() - start) + " ns");

        long start2 = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
//            doSeq(executorService);
        }
        System.out.println("end cost:" + (System.nanoTime() - start2) + " ns");

        executorService.shutdown();
    }

    private static void doRake(ExecutorService executorService) throws Throwable {
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

        Void res = wRake.fire(executorService);

        System.out.println(System.currentTimeMillis() - start + " ms");

        try {
            assert (t1.get().equals("fun1"));
            assert (t2.get().equals("fun2"));
            assert (t3.get().equals("fun3"));
            assert (t4.get().equals("fun2+fun3+fun4"));
            assert (t5.get().equals("fun3+fun5"));
            assert (t6.get().equals("fun1+fun6"));
            assert (t7.get().equals("fun1+fun2+fun3+fun4+fun3+fun5+fun7"));
            assert (t8.get().equals("fun3+fun5+fun8"));
            assert (t9.get().equals("fun1+fun2+fun3+fun4+fun3+fun5+fun7+fun9"));
            assert (t10.get().equals("fun1+fun2+fun3+fun4+fun3+fun5+fun7+fun3+fun5+fun8+fun10"));
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private static void doSeq(ExecutorService executorService) {
        long start = System.currentTimeMillis();

        String t1 = Methods.fun1();
        String t2 = Methods.fun2();
        String t3 = Methods.fun3();
        String t4 = Methods.fun4(t2, t3);
        String t5 = Methods.fun5(t3);
        String t6 = Methods.fun6(t1);
        String t7 = Methods.fun7(t1, t4, t5);
        String t8 = Methods.fun8(t5);
        String t9 = Methods.fun9(t7);
        String t10 = Methods.fun10(t7,t8);


        System.out.println(System.currentTimeMillis() - start + " ms");

        try {
            assert (t1.equals("fun1"));
            assert (t2.equals("fun2"));
            assert (t3.equals("fun3"));
            assert (t4.equals("fun2+fun3+fun4"));
            assert (t5.equals("fun3+fun5"));
            assert (t6.equals("fun1+fun6"));
            assert (t7.equals("fun1+fun2+fun3+fun4+fun3+fun5+fun7"));
            assert (t8.equals("fun3+fun5+fun8"));
            assert (t9.equals("fun1+fun2+fun3+fun4+fun3+fun5+fun7+fun9"));
            assert (t10.equals("fun1+fun2+fun3+fun4+fun3+fun5+fun7+fun3+fun5+fun8+fun10"));
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

}
