package wrake.samples.scene5_multi_return;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import wrake.WRake;
import wrake.WTask;
import wrake.samples.utils.ThreadUtils;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scene5_multiReturn {


    public static void main(String[] args) {
        Scene5_multiReturn main = new Scene5_multiReturn();
        ExecutorService executorService = Executors.newFixedThreadPool(4);


        for (int i = 0; i < 100000; i++) {
            RandomHolder randomHolder = new RandomHolder();
            Result<String> res1 = main.original(randomHolder);
            Result<String> res2 = main.wRake(executorService, randomHolder);
            if (StringUtils.equals(JSON.toJSONString(res1), JSON.toJSONString(res2))) {
                System.out.println("ok, result is:" + res1);
            } else {
                System.out.println("not ok, res1:" + res1 + ", res2:" + res2);
            }
            ThreadUtils.sleepQuite(10);
        }
        executorService.shutdown();
    }

    private Result<String> original(RandomHolder randomHolder) {
        try {
            String fun1Res = Methods.fun1(randomHolder);//fun1无法修改，调用方使用多个api对变量赋值
            if (fun1Res == null) {
                fun1Res = Methods.fun1BackUp1(randomHolder);
            }

            String fun2Res = Methods.fun2(randomHolder);
            if (fun2Res == null) {
                fun2Res = Methods.fun2BackUp1(randomHolder);
                if (StringUtils.equals(fun2Res, "needNextVal")) {
                    fun2Res = Methods.fun2BackUp2(randomHolder);
                    if (fun2Res == null) {
                        return Result.fail("fun2BackUp2 Null");
                    }
                }
            }
            if (fun2Res == null) {
                return Result.fail("Fun2ResNull");
            }

            String fun3Res = Methods.fun3(randomHolder);
            String fun4Res = Methods.fun4(randomHolder, fun2Res);

            if (fun3Res == null && fun4Res == null) {
                return Result.fail("Fun3OrFun4ResNull");
            }

            String res = Methods.fun5(randomHolder, fun1Res, fun3Res, fun4Res);
            return Result.succ(res);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(e.getMessage());
        }
    }

    private Result<String> wRake(ExecutorService executorService, RandomHolder randomHolder) {
        WRake<Result<String>> wRake = new WRake<>();
        try {

            WTask<String> t1 = wRake
                    .defTask(() -> Methods.fun1(randomHolder))
                    .defBackup(Objects::isNull, wRake
                            .defBackUpTask(() -> Methods.fun1BackUp1(randomHolder)));

            WTask<String> t2 = wRake
                    .defTask(() -> Methods.fun2(randomHolder))
                    .defBackup(Objects::isNull, wRake
                            .defBackUpTask(() -> Methods.fun2BackUp1(randomHolder))
                            .defBackup(s -> StringUtils.equals(s, "needNextVal"), wRake
                                    .defBackUpTask(() -> Methods.fun2BackUp2(randomHolder))
                                    .defTerm(Objects::isNull, wRake.termResult(() -> Result.fail("fun2BackUp2 Null")))))
                    .defTerm(Objects::isNull, wRake.termResult(() -> Result.fail("Fun2ResNull")));

            WTask<String> t3 = wRake.defTask(() -> Methods.fun3(randomHolder));
            WTask<String> t4 = wRake.defTask(() -> Methods.fun4(randomHolder, t2.get()))
                    .defTerm(s -> t3.get() == null && s == null, wRake.termResult(() -> Result.fail("Fun3OrFun4ResNull")))
                    .defDepends(t2, t3)//条件判断里增加了t3，因此需要依赖t3
                    ;

            WTask<String> t5 = wRake
                    .defTask(() -> Methods.fun5(randomHolder, t1.get(), t3.get(), t4.get()))
                    .defDepends(t1, t3, t4);

            Result<String> fire = wRake.fire(executorService);
            if (fire != null) {
                return fire;
            }
            return Result.succ(t5.get());
        } catch (Throwable e) {
            e.printStackTrace();
            return Result.fail(e.getMessage());
        }
    }

}
