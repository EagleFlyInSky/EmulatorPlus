package com.eagle.emulator.plus.overlay;

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
import de.robv.android.xposed.XposedBridge;

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

    private boolean emptyNameForDefault = true;

    public BaseOverlayConfig(String defaultPath, boolean emptyNameForDefault) {
        this(defaultPath);
        this.emptyNameForDefault = emptyNameForDefault;
    }

    public BaseOverlayConfig(String configPath) {
        XposedBridge.log(StrUtil.format("配置文件夹：{}", configPath));

        if (!FileUtil.exist(configPath)) {
            FileUtil.mkdir(configPath);
        }

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
        } else {
            FileUtil.mkdir(groupDir);
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
        } else {
            FileUtil.mkdir(gameDir);
        }

        //logInfo();

    }

    public void logInfo() {
        XposedBridge.log(StrUtil.format("默认配置：{}", defaultPath));
        XposedBridge.log(StrUtil.format("分组配置：{}", groupMapping.toString()));
        XposedBridge.log(StrUtil.format("独立配置：{}", gameMapping.toString()));
    }


    @Override
    public String getOverlayImage(GameInfo gameInfo) {

        String name = gameInfo.getName();

        if (StrUtil.isNotBlank(name)) {
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
        }

        // 名称为空，并设置为false
        if (StrUtil.isBlank(name) && !this.emptyNameForDefault) {
            return null;
        }

        if (StrUtil.isNotBlank(defaultPath)) {
            return defaultPath;
        }

        return null;
    }

}
