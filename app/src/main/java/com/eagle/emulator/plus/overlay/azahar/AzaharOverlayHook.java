package com.eagle.emulator.plus.overlay.azahar;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.eagle.emulator.hook.HookParams;
import com.eagle.emulator.plus.overlay.GameInfo;
import com.eagle.emulator.plus.overlay.OverlayHook;
import com.eagle.emulator.util.HookUtil;
import com.eagle.emulator.util.XposedUtil;

import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.function.Consumer;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XC_MethodHook;
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
        String s = "org.citra.citra_emu.features.settings.model.IntSetting";
        Class<?> settingClass = XposedHelpers.findClass(s, lpparam.classLoader);
        Field field = ReflectUtil.getField(settingClass, "SCREEN_LAYOUT");
        Object setting = ReflectUtil.getFieldValue(null, field);
        int code = ReflectUtil.invoke(setting, "getInt");
        Log.i(HookParams.LOG_TAG, "code : " + code);
        return new GameInfo(getName(activity), code);
    }

    @Override
    public void hookPlus() {
        HookUtil.hookToastShowByResourceName("emulation_menu_help", lpparam);
        hookChangeScreen();
    }

    private void hookChangeScreen() {
        Class<?> utilClass = XposedHelpers.findClass("org.citra.citra_emu.display.ScreenAdjustmentUtil", lpparam.classLoader);

        XposedHelpers.findAndHookMethod(utilClass, "changeScreenOrientation", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                int code = (int) param.args[0];
                Log.i(HookParams.LOG_TAG, "code : " + code);

                View view = getView(currentActivity);
                String overlayImage = config.getOverlayImage(new GameInfo(currentGame, code));

                Log.i(HookParams.LOG_TAG, currentGame + ":" + overlayImage);

                if (StrUtil.isNotBlank(overlayImage)) {
                    view.setBackground(Drawable.createFromPath(overlayImage));
                } else {
                    view.setBackground(null);
                }
            }
        });
    }
}
