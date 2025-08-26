package com.eagle.emulator.util;

import android.content.Context;
import android.widget.Toast;

import cn.hutool.core.util.ReflectUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookUtil {


    public static void hookToastShow(String msg) {
        XposedHelpers.findAndHookMethod(Toast.class, "show", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Toast toast = (Toast) param.thisObject;
                String text = (String) ReflectUtil.getFieldValue(toast, "mText");
                if (text.equals(msg)) {
                    param.setResult(null);
                }
            }
        });
    }

    public static void hookToastShowByResourceName(String resName, XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(Toast.class, "show", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Toast toast = (Toast) param.thisObject;
                Context context = (Context) ReflectUtil.getFieldValue(toast, "mContext");
                String text = (String) ReflectUtil.getFieldValue(toast, "mText");

                int resourceId = XposedUtil.getResourceId(resName, "string", lpparam, context);
                String string = context.getString(resourceId);

                if (text.equals(string)) {
                    param.setResult(null);
                }
            }
        });
    }
}
