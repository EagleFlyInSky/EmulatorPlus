package com.eagle.emulator.plus.overlay.azahar;

import java.util.HashMap;
import java.util.Map;

public class AzaharUtil {


    public static Map<Integer, String> getTypes() {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "original");
        map.put(1, "single_screen");
        map.put(2, "large_screen");
        map.put(3, "side_screen");
        map.put(4, "hybrid_screen");
        map.put(5, "custom_layout");
        return map;
    }

}
