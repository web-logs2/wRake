package wrake.samples.scene5_multi_return;


import com.google.common.collect.Maps;
import wrake.samples.utils.RandomUtils;

import java.util.Map;

public class RandomHolder {

    private final Map<String, Boolean> boolMap = Maps.newHashMapWithExpectedSize(32);
    private final Map<String, Integer> sleepTimeMap = Maps.newHashMapWithExpectedSize(32);

    public boolean bool(String key) {
        return boolMap.computeIfAbsent(key, integer -> RandomUtils.nextBool());
    }

    public Integer sleep(String key, int max) {
        return sleepTimeMap.computeIfAbsent(key, integer -> RandomUtils.nextInt(0, max));
    }

    protected void clear() {
        boolMap.clear();
        sleepTimeMap.clear();
    }
}
