package com.eagle.emulator.plus.overlay.azahar;

import com.eagle.emulator.plus.overlay.BaseOverlayConfig;
import com.eagle.emulator.plus.overlay.GameInfo;
import com.eagle.emulator.plus.overlay.OverlayConfig;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class AzaharOverlayConfig implements OverlayConfig {

    private Map<Integer, BaseOverlayConfig> map;

    public AzaharOverlayConfig(String configPath) {
        Map<Integer, String> types = AzaharUtil.getTypes();
        HashMap<Integer, BaseOverlayConfig> hashMap = new HashMap<>();
        for (Map.Entry<Integer, String> entry : types.entrySet()) {
            Integer key = entry.getKey();
            String value = entry.getValue();
            String path = Paths.get(configPath, value).toAbsolutePath().toString();
            BaseOverlayConfig baseOverlayConfig = new BaseOverlayConfig(path);
            hashMap.put(key, baseOverlayConfig);
            this.map = hashMap;
        }
    }

    @Override
    public String getOverlayImage(GameInfo gameInfo) {
        int screen = gameInfo.getScreen();
        BaseOverlayConfig baseOverlayConfig = map.get(screen);
        if (baseOverlayConfig != null) {
            return baseOverlayConfig.getOverlayImage(gameInfo);
        }
        return null;
    }
}
