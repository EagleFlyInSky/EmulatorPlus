package com.eagle.emulator.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.eagle.emulator.HookParams;

import java.util.Arrays;

import cn.hutool.core.thread.ThreadUtil;
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
        Log.i(HookParams.LOG_TAG, "调用栈：" + Arrays.toString(stackTrace));
    }
}
