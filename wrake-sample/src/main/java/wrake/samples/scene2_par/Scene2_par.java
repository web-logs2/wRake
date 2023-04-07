package wrake.samples.scene2_par;


import wrake.WRake;
import wrake.WTask;

import java.util.concurrent.*;

/**
 * 任务之间，没有依赖关系，可以并行执行
 */
public class Scene2_par {


    public static void main(String[] args) throws Throwable {
        for (int i = 0; i < 100; i++) {
            doTest();
        }
    }

    private static void doTest() throws Throwable {
        long start = System.currentTimeMillis();

        WRake<Void> wRake = new WRake<>();

        WTask<String> t1 = wRake.defTask(Methods::fun1);
        WTask<String> t2 = wRake.defTask(Methods::fun2);
        WTask<String> t3 = wRake.defTask(Methods::fun3);
        WTask<String> t4 = wRake.defTask(Methods::fun4);
        WTask<String> t5 = wRake.defTask(Methods::fun5);
        WTask<String> t6 = wRake.defTask(Methods::fun6);


        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                1,
                8,//1，主线程页会执行任务
                1, TimeUnit.MINUTES,
                new SynchronousQueue<>(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        wRake.fire(pool);

        System.out.println(System.currentTimeMillis() - start + " ms");
        System.out.println(t1.get());
        System.out.println(t2.get());
        System.out.println(t3.get());
        System.out.println(t4.get());
        System.out.println(t5.get());
        System.out.println(t6.get());

        System.out.println("done ...");
    }

}
