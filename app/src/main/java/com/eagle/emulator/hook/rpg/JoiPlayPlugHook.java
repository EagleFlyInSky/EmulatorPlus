package com.eagle.emulator.hook.rpg;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.eagle.emulator.hook.HookParams;
import com.eagle.emulator.plus.overlay.OverlayConfig;
import com.eagle.emulator.plus.overlay.joiplay.HtmlOverlayHook;
import com.eagle.emulator.plus.overlay.joiplay.RpgOverlayHook;
import com.eagle.emulator.plus.overlay.joiplay.RuffleOverlayHook;

import java.nio.file.Paths;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class JoiPlayPlugHook {


    public static final String HOOK_RUFFLE_CLASS_NAME = "cyou.joiplay.runtime.ruffle.MainActivity";

    public static final String HOOK_RPGMAKER_CLASS_NAME = "cyou.joiplay.runtime.rpgmaker.MainActivity";

    public static void hookRuffle(XC_LoadPackage.LoadPackageParam lpparam) {


        XposedHelpers.findAndHookMethod(HOOK_RUFFLE_CLASS_NAME, lpparam.classLoader, "onResume", new XC_MethodHook() {

            private OverlayConfig config;

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Log.i(HookParams.LOG_TAG, "Hook背景开始");
                // 获取执行对象
                Activity activity = (Activity) param.thisObject;
                // 确认是对象类型
                String name = activity.getClass().getName();
                if (!name.equals(HOOK_RUFFLE_CLASS_NAME)) {
                    return;
                }

                String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                String overlayPath = Paths.get(sdcardPath, "JoiPlay", "overlay").toString();
                Log.i(HookParams.LOG_TAG, "path:" + overlayPath);
                if (config == null) {
                    config = new OverlayConfig(overlayPath);
                }

                View gameView = (View) ReflectUtil.getFieldValue(activity, "mSurfaceView");
                Object game = ReflectUtil.invoke(activity, "getGame");
                String title = (String) ReflectUtil.getFieldValue(game, "title");
                Log.i(HookParams.LOG_TAG, "title:" + title);

                String overlayImage = config.getOverlayImage(title);
                Log.i(HookParams.LOG_TAG, "overlayImage:" + overlayImage);

                if (StrUtil.isNotBlank(overlayImage)) {
                    gameView.setBackground(Drawable.createFromPath(overlayImage));
                }
            }
        });

    }

    public static void hookRpgMaker(XC_LoadPackage.LoadPackageParam lpparam) {


        XposedHelpers.findAndHookMethod(HOOK_RPGMAKER_CLASS_NAME, lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {

            private OverlayConfig config;


            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Log.i(HookParams.LOG_TAG, "Hook背景开始");
                // 获取执行对象
                Activity activity = (Activity) param.thisObject;
                // 确认是对象类型
                String name = activity.getClass().getName();
                if (!name.equals(HOOK_RPGMAKER_CLASS_NAME)) {
                    return;
                }

                String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                String overlayPath = Paths.get(sdcardPath, "JoiPlay", "overlay").toString();
                Log.i(HookParams.LOG_TAG, "path:" + overlayPath);
                if (config == null) {
                    config = new OverlayConfig(overlayPath);
                }

                FrameLayout frameLayout = activity.findViewById(android.R.id.content);
                RelativeLayout relativeLayout = (RelativeLayout) frameLayout.getChildAt(0);
                View gameView = relativeLayout.getChildAt(0);

                Object game = ReflectUtil.getFieldValue(activity, "game");
                String title = (String) ReflectUtil.getFieldValue(game, "title");
                Log.i(HookParams.LOG_TAG, "title:" + title);

                String overlayImage = config.getOverlayImage(title);
                Log.i(HookParams.LOG_TAG, "overlayImage:" + overlayImage);

                if (StrUtil.isNotBlank(overlayImage)) {
                    gameView.setBackground(Drawable.createFromPath(overlayImage));
                }
            }

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

            }
        });

    }


    public static void hookHtmlOverlay(XC_LoadPackage.LoadPackageParam lpparam){
        new HtmlOverlayHook(lpparam).hook();
    }

    public static void hookRuffleOverlay(XC_LoadPackage.LoadPackageParam lpparam){
        RuffleOverlayHook ruffleOverlayHook = new RuffleOverlayHook(lpparam);
        ruffleOverlayHook.hook();
    }

    public static void hookRpgOverlay(XC_LoadPackage.LoadPackageParam lpparam){
        RpgOverlayHook rpgOverlayHook = new RpgOverlayHook(lpparam);
        rpgOverlayHook.hook();
    }


    /**
     * hookGamePad
     * 修改按键配置文件位置
     *
     * @param lpparam 参数
     */
    public static void hookGamePad(XC_LoadPackage.LoadPackageParam lpparam) {

        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();

        String className = String.class.getName();
        XposedHelpers.findAndHookMethod(className, lpparam.classLoader, "startsWith", className, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                //Log.i(HookParams.LOG_TAG, "Hook按键配置");

                String thisObject = (String) param.thisObject;
                String prefix = (String) param.args[0];
                if (absolutePath.equals(prefix)) {
                    //Log.i(HookParams.LOG_TAG, "配置文件:" + thisObject);
                    param.setResult(true);
                }
            }

        });
    }

}
