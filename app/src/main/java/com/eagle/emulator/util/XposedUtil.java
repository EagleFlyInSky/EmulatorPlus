package com.eagle.emulator.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AndroidAppHelper;
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

    @SuppressLint("DiscouragedApi")
    public static int getResourceId(String name, XC_LoadPackage.LoadPackageParam lpparam) {
        Resources res = AndroidAppHelper.currentApplication().getResources();
        String packageName = lpparam.packageName;
        return res.getIdentifier(name, "id", packageName);
    }

    @SuppressLint("DiscouragedApi")
    public static int getResourceId2(String name, XC_LoadPackage.LoadPackageParam lpparam, Activity activity) {
        Resources res = activity.getResources();
        return res.getIdentifier(name, "id", lpparam.packageName);
    }
}
