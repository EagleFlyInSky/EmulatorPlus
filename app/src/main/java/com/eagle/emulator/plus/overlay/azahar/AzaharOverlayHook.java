package com.eagle.emulator.plus.overlay.azahar;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.eagle.emulator.HookParams;
import com.eagle.emulator.plus.overlay.GameInfo;
import com.eagle.emulator.plus.overlay.OverlayHook;
import com.eagle.emulator.util.HookUtil;
import com.eagle.emulator.util.XposedUtil;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.setting.Setting;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AzaharOverlayHook extends OverlayHook {

    public static final String HOOK_CLASS_NAME = "org.citra.citra_emu.activities.EmulationActivity";

    private Activity currentActivity;

    private String currentGame;

    public AzaharOverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam, HOOK_CLASS_NAME);
    }

    @Override
    protected String getConfigPath() {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return Paths.get(absolutePath, "Android", "data", HookParams.AZAHAR, "files", "overlay").toString();
    }

    @Override
    protected void initConfig() {
        String configPath = getConfigPath();
        if (StrUtil.isNotBlank(configPath)) {
            config = new AzaharOverlayConfig(configPath);
        }
    }

    @Override
    public void hookMethod(Consumer<XC_MethodHook.MethodHookParam> consumer) {
        XposedHelpers.findAndHookMethod(hookClassName, lpparam.classLoader, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                consumer.accept(param);
            }
        });
    }

    @Override
    protected View getView(Activity activity) {
        this.currentActivity = activity;
        int resourceId = XposedUtil.getResourceId("surface_input_overlay", lpparam, activity);
        return activity.findViewById(resourceId);
    }

    @Override
    protected String getName(Activity activity) {

        String dataString = activity.getIntent().getDataString();

        if (StrUtil.isNotBlank(dataString)) {
            String decode = URLUtil.decode(dataString);
            String name = FileNameUtil.mainName(decode);
            this.currentGame = name;
            return name;
        }

        Bundle extras = activity.getIntent().getExtras();
        if (extras != null) {
            Object game = extras.getParcelable("game");
            String filename = (String) ReflectUtil.getFieldValue(game, "filename");
            String name = FileNameUtil.mainName(filename);
            this.currentGame = name;
            return name;
        }
        return "";
    }

    @Override
    protected GameInfo getGameInfo(Activity activity) {
        int code = getCode("SCREEN_LAYOUT");
        XposedBridge.log("code : " + code);
        return new GameInfo(getName(activity), code);
    }

    private int getCode(String name) {
        Object setting = getSettingByIntSetting(name);
        return ReflectUtil.invoke(setting, "getInt");
    }

    private void setCode(String name, int code) {
        Object setting = getSettingByIntSetting(name);
        ReflectUtil.invoke(setting, "setInt", code);
    }


    private Object getSettingByIntSetting(String name) {
        String s = "org.citra.citra_emu.features.settings.model.IntSetting";
        Class<?> settingClass = XposedHelpers.findClass(s, lpparam.classLoader);
        Field field = ReflectUtil.getField(settingClass, name);
        return ReflectUtil.getFieldValue(null, field);
    }

    @Override
    public void hookPlus() {
        HookUtil.hookToastShowByResourceName("emulation_menu_help", lpparam);
        hookChangeScreen();
        hookGameStart();
    }

    @Override
    protected void setLayout(View view, String overlayImage) {

    }

    private void hookChangeScreen() {
        Class<?> utilClass = XposedHelpers.findClass("org.citra.citra_emu.display.ScreenAdjustmentUtil", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(utilClass, "changeScreenOrientation", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                int code = (int) param.args[0];
                XposedBridge.log("code : " + code);

                View view = getView(currentActivity);
                String overlayImage = config.getOverlayImage(new GameInfo(currentGame, code));

                XposedBridge.log(currentGame + ":" + overlayImage);

                if (StrUtil.isNotBlank(overlayImage)) {
                    view.setBackground(Drawable.createFromPath(overlayImage));
                    Object util = param.thisObject;
                    Object settings = ReflectUtil.getFieldValue(util, "settings");
                    changeLayout(overlayImage, settings);
                } else {
                    view.setBackground(null);
                }
            }
        });

    }

    private void hookGameStart() {
        XposedHelpers.findAndHookMethod(hookClassName, lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {

                Activity activity = (Activity) param.thisObject;
                GameInfo gameInfo = getGameInfo(activity);
                String overlayImage = config.getOverlayImage(gameInfo);

                Object viewModel = ReflectUtil.invoke(activity, "getSettingsViewModel");
                Object settings = ReflectUtil.invoke(viewModel, "getSettings");
                changeLayout(overlayImage, settings);

            }
        });
    }

    private void changeLayout(String overlayImage, Object settings) {
        if (StrUtil.isBlank(overlayImage)) {
            return;
        }
        String settingPath = overlayImage.replace("png", "ini");
        XposedBridge.log("配置路径 ：" + settingPath);

        if (FileUtil.exist(settingPath)) {
            Setting setting = new Setting(FileUtil.file(settingPath), StandardCharsets.UTF_8, false);
            XposedBridge.log("读取配置 ：" + setting);
            List<String> keys = Arrays.asList("LANDSCAPE_TOP_X", "LANDSCAPE_TOP_Y", "LANDSCAPE_TOP_WIDTH", "LANDSCAPE_TOP_HEIGHT", "LANDSCAPE_BOTTOM_X", "LANDSCAPE_BOTTOM_Y", "LANDSCAPE_BOTTOM_WIDTH", "LANDSCAPE_BOTTOM_HEIGHT");

            for (String key : keys) {
                int value = setting.getInt(key);
                setCode(key, value);
                Object intSetting = getSettingByIntSetting(key);
                ReflectUtil.invoke(settings, "saveSetting", intSetting, "config");
            }

            Class<?> libClass = XposedHelpers.findClass("org.citra.citra_emu.NativeLibrary", lpparam.classLoader);
            Field field = ReflectUtil.getField(libClass, "INSTANCE");
            Object lib = ReflectUtil.getFieldValue(null, field);
            ReflectUtil.invoke(lib, "reloadSettings");
        }


    }
}
