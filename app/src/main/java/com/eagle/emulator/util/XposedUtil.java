package com.eagle.emulator.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import java.util.Arrays;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
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

    public static void logIntent(Activity activity) {
        Intent intent = activity.getIntent();
        XposedBridge.log("class  :" + activity.getClass().getName());
        XposedBridge.log("data   :" + intent.getDataString());
        XposedBridge.log("extras :" + intent.getExtras());
    }

    public static void logExtras(Activity activity) {
        Bundle extras = activity.getIntent().getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                XposedBridge.log("extras: " + key + "--" + extras.get(key));
            }
        }
    }




    public static void printClassHierarchy(Object obj) {
        XposedBridge.log("类的继承关系:");
        printClassHierarchyRecursive(obj.getClass(), 0);
    }

    private static void printClassHierarchyRecursive(Class<?> clazz, int depth) {
        // 打印当前类（带缩进）
        String indent = "  ".repeat(depth);
        System.out.println(indent + "├─ " + clazz.getSimpleName() + " (" + clazz.getName() + ")");

        // 递归打印父类
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && !superClass.equals(Object.class)) {
            printClassHierarchyRecursive(superClass, depth + 1);
        }

        // 打印实现的接口
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces.length > 0) {
            String interfaceIndent = "  ".repeat(depth + 1);
            for (Class<?> interfaceClass : interfaces) {
                XposedBridge.log(interfaceIndent + "├─ interface " + interfaceClass.getSimpleName());
            }
        }
    }

    public static Activity getActivityFromView(View view, ClassLoader classLoader) {
        try {
            // 获取 Context
            Object context = view.getContext();

            // 直接检查是否是 Activity
            if (context instanceof Activity) {
                return (Activity) context;
            }

            if (context instanceof ContextWrapper) {
                // 递归获取基类 Context
                Object currentContext = context;
                while (currentContext instanceof ContextWrapper) {
                    Object baseContext = ((ContextWrapper) currentContext).getBaseContext();

                    if (baseContext instanceof Activity) {
                        return (Activity) baseContext;
                    }

                    // 防止无限循环
                    if (baseContext == currentContext) {
                        break;
                    }

                    currentContext = baseContext;
                }
            }

        } catch (Exception e) {
            XposedBridge.log("获取Activity异常");
            XposedBridge.log(e);
        }

        return null;
    }
}
