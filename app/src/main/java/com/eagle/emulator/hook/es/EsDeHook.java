package com.eagle.emulator.hook.es;

import android.content.Intent;

import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class EsDeHook {


    public static final String HOOK_CLASS_NAME = "org.es_de.frontend.MainActivity";

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        hookStartLog(lpparam);
    }


    private static void hookStartLog(XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> mainActivityClass = XposedHelpers.findClass("android.app.Activity", lpparam.classLoader);

        XposedHelpers.findAndHookMethod(mainActivityClass, "startActivity", Intent.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Intent intent = (Intent) param.args[0];
                // 确认跳转 activity
                String className = intent.getComponent().getClassName();
                XposedBridge.log(StrUtil.format("class  :{}", className));
                XposedBridge.log(StrUtil.format("data   :{}", intent.getDataString()));
                XposedBridge.log(StrUtil.format("extras  :{}", intent.getExtras()));
            }
        });

    }


}
