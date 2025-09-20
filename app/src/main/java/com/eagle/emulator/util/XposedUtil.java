package com.eagle.emulator.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import java.util.Arrays;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedUtil {

    public static int getResourceId(String name, XC_LoadPackage.LoadPackageParam lpparam, Context context) {
        return getResourceId(name, "id", lpparam, context);
    }

    public static int getResourceId(String name, String type, XC_LoadPackage.LoadPackageParam lpparam, Context context) {
        Resources res = context.getResources();
        return res.getIdentifier(name, type, lpparam.packageName);
    }

    public static void logStackTrace() {
        StackTraceElement[] stackTrace = ThreadUtil.getStackTrace();
        XposedBridge.log(StrUtil.format("调用栈：{}", Arrays.toString(stackTrace)));
    }

    public static void logExtras(Activity activity) {
        Bundle extras = activity.getIntent().getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                XposedBridge.log("extras: " + key + "--" + extras.get(key));
            }
        }
    }
}
