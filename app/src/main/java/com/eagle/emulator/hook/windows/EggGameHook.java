package com.eagle.emulator.hook.windows;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ShortcutInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;

import com.eagle.emulator.util.XposedUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Set;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class EggGameHook {


    private static final String GROUP = "Shortcut";
    private static final String SPLASH_CLASS = "com.xj.app.SplashActivity";
    private static final String DETAIL_CLASS = "com.xj.landscape.launcher.ui.gamedetail.GameDetailActivity";


    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {

        hookActivity(lpparam);
        hookCreateShortcut(lpparam);
        hookGameDetail(lpparam);

    }

    private static void hookCreateShortcut(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedBridge.log("Hook快捷方式创建开始");
        XposedHelpers.findAndHookMethod("android.content.pm.ShortcutManager", lpparam.classLoader, "requestPinShortcut", ShortcutInfo.class, IntentSender.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("Hook快捷方式创建运行");
                super.beforeHookedMethod(param);
                ShortcutInfo shortcutInfo = (ShortcutInfo) param.args[0];
                Intent intent = shortcutInfo.getIntent();
                if (intent != null) {
                    Bundle extras = intent.getExtras();
                    XposedBridge.log("extras:" + extras);

                    String localAppName = intent.getStringExtra("localAppName");
                    String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    String linkPath = Paths.get(absolutePath, "Download", "eggGame", localAppName + ".egg").toString();

                    if (FileUtil.exist(linkPath)) {
                        FileUtil.del(linkPath);
                    }
                    FileUtil.newFile(linkPath);

                    if (extras == null) {
                        return;
                    }
                    Setting setting = new Setting(linkPath, StandardCharsets.UTF_8, false);
                    for (String key : extras.keySet()) {
                        Object obj = extras.get(key);
                        if (obj != null) {
                            String value = obj.toString();
                            setting.putByGroup(key, GROUP, value);
                        }
                    }
                    setting.store();
                }
            }
        });
    }


    public static void hookActivity(XC_LoadPackage.LoadPackageParam lpparam) {


        XposedBridge.log("Hook前端启动开始");
        XposedHelpers.findAndHookMethod(SPLASH_CLASS, lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("Hook前端启动运行");
                super.beforeHookedMethod(param);

                Activity activity = (Activity) param.thisObject;

                if (!activity.getClass().getName().equals(SPLASH_CLASS)) {
                    return;
                }

                Intent intent = activity.getIntent();

                String path = intent.getDataString();
                if (StrUtil.isBlank(path)) {
                    return;
                }

                if (!FileUtil.exist(path)) {
                    return;
                }

                Setting setting = new Setting(path, StandardCharsets.UTF_8, false);

                Set<String> keySet = setting.keySet(GROUP);
                Intent newIntent = new Intent();
                // 将解析得到的值设置到Intent中
                for (String key : keySet) {
                    String value = setting.get(GROUP, key);
                    newIntent.putExtra(key, value);
                }

                // 替换指向的类
                newIntent.setClassName(activity, DETAIL_CLASS);
                // 清除调用状态
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                activity.startActivity(newIntent);

            }
        });

    }

    public static void hookGameDetail(XC_LoadPackage.LoadPackageParam lpparam) {

        XposedBridge.log("Hook自动启动开始");
        XposedHelpers.findAndHookMethod(Activity.class, "onWindowFocusChanged", boolean.class, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                Activity activity = (Activity) param.thisObject;
                if (!activity.getClass().getName().equals(DETAIL_CLASS)) {
                    return;
                }

                boolean hasFocus = (Boolean) param.args[0];
                if (!hasFocus) {
                    return;
                }

                new Handler().postDelayed(() -> {
                    int resourceId = XposedUtil.getResourceId("download_cl", lpparam, activity);
                    View view = activity.findViewById(resourceId);
                    view.callOnClick();
                }, 300);


            }
        });


    }

}
