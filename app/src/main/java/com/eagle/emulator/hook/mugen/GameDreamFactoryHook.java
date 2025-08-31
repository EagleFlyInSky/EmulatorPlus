package com.eagle.emulator.hook.mugen;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import cn.hutool.core.io.FileUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class GameDreamFactoryHook {


    public static final String HOOK_CLASS_NAME = "com.GameDreamFactoryAndroid.activities.MainActivity";

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {

        Class<?> clazz = XposedHelpers.findClass(HOOK_CLASS_NAME, lpparam.classLoader);
        XposedHelpers.findAndHookMethod(clazz, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                // 获取执行对象
                Activity activity = (Activity) param.thisObject;

                // 获取 data 数据
                String path = activity.getIntent().getStringExtra("path");
                if (path == null) {
                    return;
                }

                if (FileUtil.isFile(path)) {
                    path = FileUtil.getParent(path, 1);
                }

                activity.getIntent().setData(Uri.parse("GameDreamFactory: -open \"" + path + "\""));

                super.beforeHookedMethod(param);

            }
        });
    }


}
