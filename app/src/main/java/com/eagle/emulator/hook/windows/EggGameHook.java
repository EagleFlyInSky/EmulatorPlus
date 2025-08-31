package com.eagle.emulator.hook.windows;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ShortcutInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.eagle.emulator.HookParams;
import com.eagle.emulator.util.XposedUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Set;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class EggGameHook {


    private static final String GROUP = "Shortcut";

    private static final String SPLASH_CLASS = "com.xj.app.SplashActivity";
    private static final String DETAIL_CLASS = "com.xj.landscape.launcher.ui.gamedetail.GameDetailActivity";


    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {

        hookActivity(lpparam);
        hookCreateShortcut(lpparam);

    }

    private static void hookCreateShortcut(XC_LoadPackage.LoadPackageParam lpparam) {
        Log.i(HookParams.LOG_TAG, "Hook快捷方式创建开始");
        XposedHelpers.findAndHookMethod("android.content.pm.ShortcutManager", lpparam.classLoader, "requestPinShortcut", ShortcutInfo.class, IntentSender.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.i(HookParams.LOG_TAG, "Hook快捷方式创建运行");
                super.beforeHookedMethod(param);
                ShortcutInfo shortcutInfo = (ShortcutInfo) param.args[0];
                Intent intent = shortcutInfo.getIntent();
                if (intent != null) {
                    Bundle extras = intent.getExtras();
                    Log.i(HookParams.LOG_TAG, "extras:" + extras);

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


        Log.i(HookParams.LOG_TAG, "Hook前端启动开始");
        XposedHelpers.findAndHookMethod(SPLASH_CLASS, lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.i(HookParams.LOG_TAG, "Hook前端启动运行");
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

                activity.startActivity(newIntent);

            }
        });

    }

    public static void hookGameDetail(XC_LoadPackage.LoadPackageParam lpparam) {

        Log.i(HookParams.LOG_TAG, "Hook Detail 开始");


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


                viewClick(activity);

                View decorView = activity.getWindow().getDecorView();
                decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // 布局完成时调用
                        decorView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        Log.i(HookParams.LOG_TAG, "=======布局回调");
                    }
                });


            }

            private void viewClick(Activity activity) {
                int resourceId = XposedUtil.getResourceId("download_cl", lpparam, activity);
                Log.i(HookParams.LOG_TAG, "resourceId:" + resourceId);
                View view = activity.findViewById(resourceId);

                Log.i(HookParams.LOG_TAG, "view:" + view);
                Log.i(HookParams.LOG_TAG, "view:" + view.getId());

                boolean b = view.hasOnClickListeners();
                Log.i(HookParams.LOG_TAG, "view click listener " + b);

            }
        });

        XposedHelpers.findAndHookMethod(View.class, "onWindowFocusChanged", boolean.class, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                View view = (View) param.thisObject;

                int resourceId = XposedUtil.getResourceId("download_cl", lpparam, view.getContext());
                if (view.getId() != resourceId) {
                    return;
                }

                boolean hasFocus = (Boolean) param.args[0];
                if (!hasFocus) {
                    return;
                }
                Log.i(HookParams.LOG_TAG, "=======view focus");


            }
        });

//        XposedHelpers.findAndHookMethod("com.xj.landscape.launcher.adapter.GameDetailAdapter", lpparam.classLoader, "onBindViewHolder", holderClass, int.class, List.class, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                Log.i(HookParams.LOG_TAG, "hook bind");
//                super.afterHookedMethod(param);
//                Object adapter = param.thisObject;
//                Activity activity = (Activity) ReflectUtil.getFieldValue(adapter, "a");
//                int resourceId = XposedUtil.getResourceId("download_cl", lpparam, activity);
//                Log.i(HookParams.LOG_TAG, "resourceId:" + resourceId);
//                View view = activity.findViewById(resourceId);
//                Log.i(HookParams.LOG_TAG, "view:" + view);
//                Log.i(HookParams.LOG_TAG, "view:" + view.getId());
//                view.performClick();
//            }
//        });


        XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "setOnClickListener", "android.view.View$OnClickListener", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {

                View view = (View) param.thisObject;
                View.OnClickListener originalListener = (View.OnClickListener) param.args[0];

                int resourceId = XposedUtil.getResourceId("download_cl", lpparam, view.getContext());
                if (view.getId() != resourceId) {
                    return;
                }
                Log.i(HookParams.LOG_TAG, "=======设置点击事件" + originalListener);
//                XposedUtil.logStackTrace();
//                Log.i(HookParams.LOG_TAG, "Hook view" + view.getId());
            }
        });

    }

    public static void hookWine(XC_LoadPackage.LoadPackageParam lpparam) {

        String wineClass = "com.xj.winemu.WineActivity";

        Log.i(HookParams.LOG_TAG, "Hook Wine 开始");
        XposedHelpers.findAndHookMethod(wineClass, lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.i(HookParams.LOG_TAG, "Hook Wine 运行");
                super.beforeHookedMethod(param);

                Activity activity = (Activity) param.thisObject;

                if (!activity.getClass().getName().equals(wineClass)) {
                    return;
                }

                Intent intent = activity.getIntent();

                Bundle extras = intent.getExtras();
                Log.i(HookParams.LOG_TAG, "extras：" + extras);

                Set<String> keySet = extras.keySet();
                for (String key : keySet) {
                    String value = intent.getStringExtra(key);
                    Log.i(HookParams.LOG_TAG, key + " ：" + value);
                }


            }
        });

    }

    public static void hookSetup(XC_LoadPackage.LoadPackageParam lpparam) {

        String setupClass = "com.xj.winemu.PcEmuSetupDialog";

        Log.i(HookParams.LOG_TAG, "Hook Setup 开始");
        XposedHelpers.findAndHookMethod(setupClass, lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedUtil.logStackTrace();
                super.beforeHookedMethod(param);

                Dialog dialog = (Dialog) param.thisObject;

                if (!dialog.getClass().getName().equals(setupClass)) {
                    return;
                }
                Log.i(HookParams.LOG_TAG, "Hook Setup 运行");


            }
        });

    }

}
