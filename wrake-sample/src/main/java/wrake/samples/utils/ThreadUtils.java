package wrake.samples.utils;

import java.util.concurrent.TimeUnit;

public class ThreadUtils {


    public static void sleepQuite(long ms){
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
