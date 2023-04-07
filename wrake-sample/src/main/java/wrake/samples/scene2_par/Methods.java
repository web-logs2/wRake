package wrake.samples.scene2_par;


import wrake.samples.utils.ThreadUtils;

public class Methods {
    public static String fun1() {
        System.out.println(Thread.currentThread() + " fun1");
        ThreadUtils.sleepQuite(100);
        return "fun1";
    }
    public static String fun2() {
        System.out.println(Thread.currentThread() + " fun2");
        ThreadUtils.sleepQuite(200);
        return "fun2";
    }
    public static String fun3() {
        System.out.println(Thread.currentThread() + " fun3");
        ThreadUtils.sleepQuite(300);
        return "fun3";
    }
    public static String fun4() {
        System.out.println(Thread.currentThread() + " fun4");
        ThreadUtils.sleepQuite(400);
        return "fun4";
    }
    public static String fun5() {
        System.out.println(Thread.currentThread() + " fun5");
        ThreadUtils.sleepQuite(500);
        return "fun5";
    }
    public static String fun6() {
        System.out.println(Thread.currentThread() + " fun6");
        ThreadUtils.sleepQuite(600);
        return "fun6";
    }
}
