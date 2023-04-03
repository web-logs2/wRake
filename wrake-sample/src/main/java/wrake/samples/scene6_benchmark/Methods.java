package wrake.samples.scene6_benchmark;

import wrake.samples.utils.ThreadUtils;
import org.apache.commons.lang3.StringUtils;

public class Methods {

    public static String fun(String methodName, String... results) {
        ThreadUtils.sleepQuite(5);
        if (results != null && results.length > 0) {
            methodName = (pre(results) + "+" + methodName);
        }
        return methodName;
    }

    private static String pre(String[] results) {
        return StringUtils.join(results, "+");
    }

}
