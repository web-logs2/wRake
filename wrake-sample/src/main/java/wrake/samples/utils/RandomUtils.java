package wrake.samples.utils;

import java.util.Random;

public class RandomUtils {

    private static final Random RANDOM = new Random();

    public static int nextInt(int start,int end){
        return start + RANDOM.nextInt(end - start);
    }

    public static boolean nextBool(){
        return RANDOM.nextBoolean();
    }

}
