package wrake.samples.scene4_mix_random;


import wrake.samples.utils.RandomUtils;
import wrake.samples.utils.ThreadUtils;
import wrake.samples.utils.MixUtils;

public class Methods {


    public static String fun1(String... dep) {
        ThreadUtils.sleepQuite(RandomUtils.nextInt(1,2));

        String base = "fun1";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return MixUtils.join(dep, "+") + "+" + base;
    }

    public static void main(String[] args) {
        int i = RandomUtils.nextInt(1, 1);
        System.out.println(i);
    }

    public static String fun2(String... dep) {
        String base = "fun2";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return MixUtils.join(dep, "+") + "+" + base;
    }

    public static String fun3(String... dep) {
        ThreadUtils.sleepQuite(RandomUtils.nextInt(1,2));

        String base = "fun3";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return MixUtils.join(dep, "+") + "+" + base;
    }

    public static String fun4(String... dep) {
        ThreadUtils.sleepQuite(RandomUtils.nextInt(1,2));

        String base = "fun4";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return MixUtils.join(dep, "+") + "+" + base;
    }

    public static String fun5(String... dep) {
        ThreadUtils.sleepQuite(RandomUtils.nextInt(1,2));

        String base = "fun5";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return MixUtils.join(dep, "+") + "+" + base;
    }

    public static String fun6(String... dep) {
        ThreadUtils.sleepQuite(RandomUtils.nextInt(1,2));

        String base = "fun6";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return MixUtils.join(dep, "+") + "+" + base;
    }

    public static String fun7(String... dep) {
        ThreadUtils.sleepQuite(RandomUtils.nextInt(1,2));

        String base = "fun7";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return MixUtils.join(dep, "+") + "+" + base;
    }

    public static String fun8(String... dep) {
        ThreadUtils.sleepQuite(RandomUtils.nextInt(1,2));

        String base = "fun8";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return MixUtils.join(dep, "+") + "+" + base;
    }

    public static String fun9(String... dep) {
        ThreadUtils.sleepQuite(RandomUtils.nextInt(1,2));

        String base = "fun9";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return MixUtils.join(dep, "+") + "+" + base;
    }

    public static String fun10(String... dep) {
        ThreadUtils.sleepQuite(RandomUtils.nextInt(1,2));

        String base = "fun10";
        if (dep == null || dep.length <= 0) {
            return base;
        }
        return MixUtils.join(dep, "+") + "+" + base;
    }
}
