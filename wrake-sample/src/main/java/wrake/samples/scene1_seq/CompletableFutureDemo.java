package wrake.samples.scene1_seq;

import wrake.WRake;
import wrake.WTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CompletableFutureDemo {
    public static void main(String[] args) {
        // 原始数据集
        CopyOnWriteArrayList<Integer> taskList = new CopyOnWriteArrayList();
        taskList.add(1);
        taskList.add(2);
        taskList.add(3);
        taskList.add(4);

        completableFuture(taskList);

        System.out.println("-----");

        wRake(taskList);
    }

    private static void wRake(CopyOnWriteArrayList<Integer> taskList) {

        try {
            List<Character> resultList = new ArrayList<>();

            WRake<Void> rake = new WRake<>();

            List<WTask<Character>> tasks = taskList.stream()
                    .map(integer -> {
                        WTask<Integer> t1 = rake.defTask(() -> calcASCII(integer));
                        return rake.defTask(() -> (char) t1.get().intValue()).defDepends(t1);
                    }).collect(Collectors.toList());

            rake.fire(Executors.newFixedThreadPool(4));

            tasks.forEach(characterWTask -> resultList.add(characterWTask.get()));

            System.out.println("完成！result=" + resultList);

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private static void completableFuture(CopyOnWriteArrayList<Integer> taskList) {
        // 结果集
        List<Character> resultList = new ArrayList<>();
        //线程池，可容纳四个线程
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        CompletableFuture[] cfs = taskList.stream()
                // 第一阶段
                .map(integer -> CompletableFuture.supplyAsync(
                                () -> calcASCII(integer), executorService)
                        .thenApply(i -> (char) (i.intValue()))
                        .whenComplete((ch, e) -> {
                            resultList.add(ch);
                        })
                ).toArray(CompletableFuture[]::new);
        // 封装后无返回值，必须自己whenComplete()获取
        CompletableFuture.allOf(cfs).join();//future.get()

        System.out.println("完成！result=" + resultList);
    }

    // 计算i的ASCII值
    public static Integer calcASCII(Integer i) {
        try {
            if (i == 1) {
                Thread.sleep(5000);
            } else {
                Thread.sleep(1000);
            }
            //数字 -> A-D对应的ascii
            i = i + 64;
            System.out.println("【阶段1】线程" + Thread.currentThread().getName()
                    + "执行完毕，" + "已将" + i
                    + "转为了A(或B或C或D)对应的ASCII" + i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return i;
    }
}