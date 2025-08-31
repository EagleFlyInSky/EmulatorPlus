package com.eagle.emulator.hook.beacon;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.eagle.emulator.HookParams;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.setting.Setting;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class BeaconHook {

    private static final Map<String, String> MAPPINGS = new HashMap<>();

    private static final Set<String> IGNORES = new HashSet<>();


    static {
        // 初始化名称映射关系
        String path = "/storage/emulated/0/beacon";
        String dirPath = Paths.get(path, "name_mapping").toString();
        if (FileUtil.exist(dirPath) && FileUtil.isDirectory(dirPath)) {
            File[] ls = FileUtil.ls(dirPath);
            List<File> list = ListUtil.toList(ls);
            for (File file : list) {

                if (FileNameUtil.getSuffix(FileUtil.getName(file)).equals("ini")) {

                    Setting setting = new Setting(file, StandardCharsets.UTF_8, false);

                    List<String> groups = setting.getGroups();
                    if (CollUtil.isNotEmpty(groups)) {
                        for (String group : groups) {
                            int groupSize = setting.keySet(group).size();
                            if (groupSize > 0) {
                                for (String key : setting.keySet(group)) {
                                    String value = setting.get(group, key);
                                    MAPPINGS.put(key, value);
                                }
                            }
                        }
                    }
                }
            }
        }
        // 初始化忽略文件
        File file = new File(path, "ignore.txt");
        if (FileUtil.exist(file)) {
            List<String> ignores = FileUtil.readLines(file, StandardCharsets.UTF_8);
            List<String> filter = CollUtil.filter(ignores, e -> {
                String trim = StrUtil.trim(e);
                return !trim.startsWith("#");
            });
            IGNORES.addAll(filter);
        }
    }


    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        Log.i(HookParams.LOG_TAG, "Hook准备");
        hookStartLog(lpparam);
        hookRetroarchCores(lpparam);
        hookSave(lpparam);
        hookGameStart(lpparam);
    }

    /**
     * hook 打印启动参数 便于调试
     */
    private static void hookStartLog(XC_LoadPackage.LoadPackageParam lpparam) {

        Class<?> mainActivityClass = XposedHelpers.findClass("androidx.core.content.ContextCompat", lpparam.classLoader);

        XposedHelpers.findAndHookMethod(mainActivityClass, "startActivity", Context.class, Intent.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Intent intent = (Intent) param.args[1];
                // 确认跳转 activity
                String className = intent.getComponent().getClassName();
                Log.i(HookParams.LOG_TAG, "class  :" + className);
                Log.i(HookParams.LOG_TAG, "data   :" + intent.getDataString());
                Log.i(HookParams.LOG_TAG, "extras :" + intent.getExtras());
            }
        });

    }

    /**
     * hook 添加retroarch核心
     */
    private static void hookRetroarchCores(XC_LoadPackage.LoadPackageParam lpparam) {

        XposedHelpers.findAndHookMethod("com.radikal.gamelauncher.utils.EmulatorUtilsKt", lpparam.classLoader, "getAllRetroarchCores", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Object result = param.getResult();
                Set<String> set = Convert.toSet(String.class, result);
                set.add("mamemess");
                set.add("fbneo_plus");
                List<String> list = ListUtil.toList(set);
                ListUtil.sortByPinyin(list);
                param.setResult(list);
            }

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });

    }

    /**
     * hook 游戏保存 替换名称 忽略游戏
     */
    private static void hookSave(XC_LoadPackage.LoadPackageParam lpparam) {

        Class<?> gameRepositoryClass = XposedHelpers.findClass("com.radikal.gamelauncher.data.game.GameRepository", lpparam.classLoader);
        Class<?> gameClass = XposedHelpers.findClass("com.radikal.gamelauncher.data.game.Game", lpparam.classLoader);
        Class<?> continuationClass = XposedHelpers.findClass("kotlin.coroutines.Continuation", lpparam.classLoader);


        XposedHelpers.findAndHookMethod(gameRepositoryClass, "save", gameClass, Set.class, continuationClass, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                if (ignore(param)) {
                    param.setResult(null);
                    return;
                }

                mapping(param);

            }

            private boolean ignore(MethodHookParam param) {

                if (CollUtil.isNotEmpty(IGNORES)) {
                    Object object = param.args[0];
                    String name = ReflectUtil.getFieldValue(object, "name").toString();
                    Set<?> gameFileSet = (Set<?>) param.args[1];

                    boolean isIgnore = gameFileSet.stream().allMatch(e -> {
                        String file = e.toString();

                        String decode = URLUtil.decode(file);
                        String fileName = FileUtil.mainName(decode);

                        return CollUtil.contains(IGNORES, fileName);
                    });
                    if (isIgnore) {
                        Log.i(HookParams.LOG_TAG, "文件忽略：" + name);
                    }
                    return isIgnore;
                }

                return false;

            }

            private void mapping(MethodHookParam param) {

                if (CollUtil.isNotEmpty(MAPPINGS)) {
                    Object object = param.args[0];
                    String name = ReflectUtil.getFieldValue(object, "name").toString();

                    String mappingName = MAPPINGS.get(name);
                    if (StrUtil.isNotBlank(mappingName)) {
                        Log.i(HookParams.LOG_TAG, "游戏名称映射：" + name + "->" + mappingName);
                        ReflectUtil.setFieldValue(object, "name", mappingName);
                    }
                }
            }
        });

    }

    /**
     * 扩展命令参数
     */
    private static void hookGameStart(XC_LoadPackage.LoadPackageParam lpparam) {

        final String FILE_CONTEXT = "{file_context}";

        final String FILE_NAME = "{file_name}";

        final String DIR_PATH = "{dir_path}";

        final String DIR_NAME = "{dir_name}";

        final String DIR_URI = "{dir_uri}";

        Class<?> launchClass = XposedHelpers.findClass("com.radikal.gamelauncher.domain.GetLaunchGameIntentUseCase", lpparam.classLoader);
        Class<?> typeClass = XposedHelpers.findClass("com.radikal.gamelauncher.model.FileHandleType", lpparam.classLoader);


        XposedHelpers.findAndHookMethod(launchClass, "addLaunchConfig", Intent.class, String.class, typeClass, String.class, String.class, Uri.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Log.i(HookParams.LOG_TAG, "Hook参数开始");

                String amStartCommand = param.args[3].toString();
                Log.i(HookParams.LOG_TAG, "amStartCommand:" + amStartCommand);
                String romPath = (String) param.args[4];
                Log.i(HookParams.LOG_TAG, "romPath:" + romPath);
                Uri romUri = (Uri) param.args[5];
                Log.i(HookParams.LOG_TAG, "romUri:" + romUri.getPath());

                Log.i(HookParams.LOG_TAG, "启动命令:" + amStartCommand);

                if (amStartCommand.contains(FILE_CONTEXT)) {
                    String context = FileUtil.readString(romPath, StandardCharsets.UTF_8);
                    amStartCommand = amStartCommand.replace(FILE_CONTEXT, context);
                }

                if (amStartCommand.contains(FILE_NAME)) {
                    String name = FileUtil.mainName(romPath);
                    amStartCommand = amStartCommand.replace(FILE_NAME, name);
                }

                if (amStartCommand.contains(DIR_PATH)) {
                    String dir = FileUtil.getParent(romPath, 1);
                    amStartCommand = amStartCommand.replace(DIR_PATH, dir);
                }

                if (amStartCommand.contains(DIR_NAME)) {
                    String dir = FileUtil.getParent(romPath, 1);
                    String dirName = FileUtil.mainName(dir);
                    amStartCommand = amStartCommand.replace(DIR_NAME, dirName);
                }

                if (amStartCommand.contains(DIR_URI)) {
                    String path = romUri.getPath();
                    String parent = FileUtil.getParent(path, 1);
                    amStartCommand = amStartCommand.replace(DIR_URI, parent);
                }

                Log.i(HookParams.LOG_TAG, "启动命令:" + amStartCommand);

                // 修改参数
                param.args[3] = amStartCommand;

            }
        });


    }


}
