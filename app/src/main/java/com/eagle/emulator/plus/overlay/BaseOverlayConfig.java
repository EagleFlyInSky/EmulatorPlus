package com.eagle.emulator.plus.overlay;

import android.util.Log;

import com.eagle.emulator.hook.HookParams;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;

public class BaseOverlayConfig implements OverlayConfig {


    /**
     * 默认图片
     */
    private String defaultPath;

    /**
     * 分组配置
     */
    private final Map<String, String> groupMapping = new HashMap<>();

    /**
     * 游戏配置
     */
    private final Map<String, String> gameMapping = new HashMap<>();


    public static final String DEFAULT = "default.png";
    public static final String GROUP = "group";
    public static final String GAME = "game";

    public BaseOverlayConfig(String configPath) {

        Log.i(HookParams.LOG_TAG, "configPath：" + configPath);

        // 初始化默认图片
        String path = Paths.get(configPath, DEFAULT).toString();
        if (FileUtil.exist(path)) {
            defaultPath = path;
        }

        // 初始化分组配置
        String groupDir = Paths.get(configPath, GROUP).toString();
        // 判断文件夹是否存在
        if (FileUtil.exist(groupDir)) {
            File[] files = FileUtil.file(groupDir).listFiles(e -> FileNameUtil.getSuffix(e).equals("png"));
            List<File> imagePaths = CollUtil.toList(files);
            for (File imagePath : imagePaths) {
                String txtPath = imagePath.getPath().replace("png", "txt");
                if (FileUtil.exist(txtPath)) {
                    List<String> games = FileUtil.readLines(txtPath, StandardCharsets.UTF_8);
                    if (CollUtil.isNotEmpty(games)) {
                        for (String game : games) {
                            groupMapping.put(game, imagePath.getPath());
                        }
                    }
                }
            }
        }

        // 初始化游戏独立配置
        String gameDir = Paths.get(configPath, GAME).toString();
        // 判断文件夹是否存在
        if (FileUtil.exist(gameDir)) {
            File[] files = FileUtil.file(gameDir).listFiles(e -> FileNameUtil.getSuffix(e).equals("png"));
            List<File> imagePaths = CollUtil.toList(files);
            for (File imagePath : imagePaths) {
                if (FileNameUtil.getSuffix(imagePath).equals("png")) {
                    String gameName = FileNameUtil.mainName(imagePath);
                    gameMapping.put(gameName, imagePath.getPath());
                }
            }
        }

        //logInfo();

    }

    public void logInfo() {
        Log.i(HookParams.LOG_TAG, "defaultPath：" + defaultPath);
        Log.i(HookParams.LOG_TAG, "groupMapping：" + groupMapping);
        Log.i(HookParams.LOG_TAG, "gameMapping：" + gameMapping);
    }


    @Override
    public String getOverlayImage(GameInfo gameInfo) {

        String name = gameInfo.getName();
        if (CollUtil.isNotEmpty(gameMapping)) {
            String path = gameMapping.get(name);
            if (StrUtil.isNotBlank(path)) {
                return path;
            }
        }

        if (CollUtil.isNotEmpty(groupMapping)) {
            String path = groupMapping.get(name);
            if (StrUtil.isNotBlank(path)) {
                return path;
            }
        }

        if (StrUtil.isNotBlank(defaultPath)) {
            return defaultPath;
        }

        return null;
    }

}
