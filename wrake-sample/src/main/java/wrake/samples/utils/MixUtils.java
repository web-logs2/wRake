package wrake.samples.utils;

public class MixUtils {

    public static String join(String[] array, String split) {
        if (array.length == 0) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < array.length; ++i) {
                if (i > 0) {
                    sb.append(split);
                }

                sb.append(array[i]);
            }

            return sb.toString();
        }
    }


}
