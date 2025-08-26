package com.eagle.emulator.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;

import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedUtil {

    @SuppressLint("PrivateApi")
    public static Context getSystemContext() {
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getMethod("currentActivityThread");
            Object activityThread = currentActivityThreadMethod.invoke(null);

            Method getSystemContextMethod = activityThreadClass.getMethod("getSystemContext");
            return (Context) getSystemContextMethod.invoke(activityThread);
        } catch (Exception e) {
            XposedBridge.log(e);
            return null;
        }
    }

    public static int getResourceId(String name, XC_LoadPackage.LoadPackageParam lpparam, Context context) {
        return getResourceId(name, "id", lpparam, context);
    }

    public static int getResourceId(String name, String type, XC_LoadPackage.LoadPackageParam lpparam, Context context) {
        Resources res = context.getResources();
        return res.getIdentifier(name, type, lpparam.packageName);
    }
}
