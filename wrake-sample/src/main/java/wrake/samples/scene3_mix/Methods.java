package wrake.samples.scene3_mix;

import wrake.samples.utils.ThreadUtils;

import static wrake.samples.utils.MixUtils.join;


public class Methods {
    public static String fun1(String... dep) {
        ThreadUtils.sleepQuite(100);
        String base = "fun1";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return join(dep, "+") + "+" + base;
    }

    public static String fun2(String... dep) {
        ThreadUtils.sleepQuite(50);
        String base = "fun2";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return join(dep, "+") + "+" + base;
    }

    public static String fun3(String... dep) {
        ThreadUtils.sleepQuite(100);
        String base = "fun3";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return join(dep, "+") + "+" + base;
    }

    public static String fun4(String... dep) {
        ThreadUtils.sleepQuite(50);
        String base = "fun4";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return join(dep, "+") + "+" + base;
    }

    public static String fun5(String... dep) {
        ThreadUtils.sleepQuite(50);
        String base = "fun5";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return join(dep, "+") + "+" + base;
    }

    public static String fun6(String... dep) {
        ThreadUtils.sleepQuite(50);
        String base = "fun6";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return join(dep, "+") + "+" + base;
    }

    public static String fun7(String... dep) {
        ThreadUtils.sleepQuite(50);
        String base = "fun7";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return join(dep, "+") + "+" + base;
    }

    public static String fun8(String... dep) {
        ThreadUtils.sleepQuite(50);
        String base = "fun8";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return join(dep, "+") + "+" + base;
    }

    public static String fun9(String... dep) {
        ThreadUtils.sleepQuite(100);
        String base = "fun9";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return join(dep, "+") + "+" + base;
    }

    public static String fun10(String... dep) {
        ThreadUtils.sleepQuite(150);
        String base = "fun10";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return join(dep, "+") + "+" + base;
    }
}
