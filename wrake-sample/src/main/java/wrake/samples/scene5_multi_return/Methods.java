package wrake.samples.scene5_multi_return;

import wrake.samples.utils.ThreadUtils;

import static wrake.samples.utils.MixUtils.join;

public class Methods {


    public static String fun1(RandomHolder randomHolder, String... dep) {
        ThreadUtils.sleepQuite(randomHolder.sleep("fun1", 5));
        if (randomHolder.bool("fun1")) {
            return "fun1Res";
        }
        return null;
    }

    public static String fun2(RandomHolder randomHolder, String... dep) {
        ThreadUtils.sleepQuite(randomHolder.sleep("fun2", 5));
        if (randomHolder.bool("fun2")) {
            return "fun2Res";
        }
        return null;
    }

    public static String fun3(RandomHolder randomHolder, String... dep) {
        ThreadUtils.sleepQuite(randomHolder.sleep("fun3", 5));
        if (randomHolder.bool("fun3")) {
            return null;
        }
        String base = "fun3";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return join(dep, "+") + "+" + base;
    }

    public static String fun4(RandomHolder randomHolder, String... dep) {
        ThreadUtils.sleepQuite(randomHolder.sleep("fun4", 5));
        if (randomHolder.bool("fun4")) {
            return null;
        }
        String base = "fun4";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return join(dep, "+") + "+" + base;
    }

    public static String fun5(RandomHolder randomHolder, String... dep) {
        ThreadUtils.sleepQuite(randomHolder.sleep("fun5", 5));
        String base = "fun5";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return join(dep, "+") + "]+" + base;
    }

    public static String fun6(RandomHolder randomHolder, String... dep) {
        ThreadUtils.sleepQuite(randomHolder.sleep("fun6", 5));
        String base = "fun6";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return join(dep, "+") + "+" + base;
    }

    public static String fun1BackUp1(RandomHolder randomHolder) {
        return "fun1BackUp1";
    }

    public static String fun2BackUp1(RandomHolder randomHolder) {
        if (randomHolder.bool("fun2BackUp1")) {
            return null;
        }
        if (randomHolder.bool("fun2BackUp1Exception")) {
            throw new RuntimeException("fun2BackUp1Exception");
        }
        if (randomHolder.bool("fun2BackUp1_2")) {
            return "needNextVal";
        }
        return "fun2BackUp1";
    }

    public static String fun2BackUp2(RandomHolder randomHolder) {
        if (randomHolder.bool("fun2BackUp2")) {
            return null;
        }
        return "fun2BackUp2";
    }
}
