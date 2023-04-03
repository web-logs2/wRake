package wrake.samples.scene1_seq;

import wrake.samples.utils.ThreadUtils;

public class Methods {
    public static String fun1() {
        ThreadUtils.sleepQuite(100);
        return "fun1";
    }
    public static String fun2(String t1) {
        ThreadUtils.sleepQuite(200);
        return t1 + "+" + "fun2";
    }
    public static String fun3(String t1) {
        ThreadUtils.sleepQuite(300);
        return t1 + "+" +"fun3";
    }
    public static String fun4(String t1) {
        ThreadUtils.sleepQuite(400);
        return t1 + "+" +"fun4";
    }
    public static String fun5(String t1) {
        ThreadUtils.sleepQuite(500);
        return t1 + "+" +"fun5";
    }
    public static String fun6(String t1) {
        ThreadUtils.sleepQuite(600);
        return t1 + "+" +"fun6";
    }
}
