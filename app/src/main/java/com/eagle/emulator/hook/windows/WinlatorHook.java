package com.eagle.emulator.hook.windows;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class WinlatorHook {


    public static final String CLASS_NAME = "com.winlator.XServerDisplayActivity";

    public static final String HOOK_CLASS_NAME = "com.winlator.MainActivity";
    public static final String SHORTCUT_NAME = "shortcut_name";


    public static boolean hasClass(XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> clazz = XposedHelpers.findClass(HOOK_CLASS_NAME, lpparam.classLoader);
        XposedBridge.log("class：" + clazz.getName());
        return clazz != null;
    }


    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        hookStart(lpparam);
        hookParam(lpparam);
    }

    private static void hookParam(XC_LoadPackage.LoadPackageParam lpparam) {

        Class<?> mainActivityClass = XposedHelpers.findClass("android.app.Activity", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(mainActivityClass, "startActivity", Intent.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                // 获取执行对象
                Activity activity = (Activity) param.thisObject;
                // 确认是对象类型
                String className = activity.getClass().getName();
                if (!className.equals(HOOK_CLASS_NAME)) {
                    return;
                }

                Intent intent = (Intent) param.args[0];
                String shortcutPath = intent.getStringExtra("shortcut_path");

                Object shortcut = findShortcut(activity, shortcutPath);
                String name = (String) ReflectUtil.getFieldValue(shortcut, "name");

                intent.putExtra(SHORTCUT_NAME, name);

            }

            private Object findShortcut(Activity activity, String shortcutPath) throws Throwable {
                List<?> list = getShortcutList(activity, lpparam.classLoader);

                Object shortcut = null;
                for (Object o : list) {
                    File file = (File) XposedHelpers.getObjectField(o, "file");
                    String path = file.getPath();
                    if (path.equals(shortcutPath)) {
                        shortcut = o;
                        break;
                    }
                }
                return shortcut;
            }
        });
    }

    private static void hookStart(XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> clazz = XposedHelpers.findClass(HOOK_CLASS_NAME, lpparam.classLoader);
        XposedHelpers.findAndHookMethod(clazz, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("Hook开始");
                hook(param);
            }

            private void hook(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;

                String shortcut_path = activity.getIntent().getStringExtra("shortcut_path");
                if (shortcut_path == null) {
                    return;
                }
                String shortcutName = FileUtil.mainName(shortcut_path);
                XposedBridge.log("shortcutName:" + shortcutName);
                if (StrUtil.isBlank(shortcutName)) {
                    return;
                }

                Object shortcut = findShortcut(activity, shortcutName);
                if (shortcut == null) {
                    return;
                }

                Object container = XposedHelpers.getObjectField(shortcut, "container");
                int id = XposedHelpers.getIntField(container, "id");

                Object file = XposedHelpers.getObjectField(shortcut, "file");
                String path = (String) XposedHelpers.callMethod(file, "getPath");

                XposedBridge.log("id:" + id + " path:" + path);

                Intent intent = new Intent();
                // 将解析得到的值设置到Intent中
                intent.putExtra("container_id", id);
                intent.putExtra("shortcut_path", path);
                intent.putExtra(SHORTCUT_NAME, shortcutName);
                // 替换指向的类
                intent.setClassName(activity, CLASS_NAME);
                // 清除调用状态
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                activity.startActivity(intent);

            }

            private Object findShortcut(Activity activity, String shortcutName) throws Throwable {
                List<?> list = getShortcutList(activity, lpparam.classLoader);

                Object shortcut = null;
                for (Object o : list) {
                    String name = (String) XposedHelpers.getObjectField(o, "name");
                    if (name.equals(shortcutName)) {
                        shortcut = o;
                        break;
                    }
                }
                return shortcut;
            }


        });
    }


    private static List<?> getShortcutList(Activity activity, ClassLoader classLoader) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Class<?> managerClass = XposedHelpers.findClass("com.winlator.container.ContainerManager", classLoader);
        Constructor<?> constructor = XposedHelpers.findConstructorBestMatch(managerClass, Context.class);
        Object manager = constructor.newInstance(activity.getBaseContext());

        Object shortcuts;
        Method method = ReflectUtil.getMethod(managerClass, "loadShortcuts");
        if (method != null) {
            method = ReflectUtil.getMethod(managerClass, "loadShortcuts");
            shortcuts = method.invoke(manager);
        } else {
            Class<?> shortcutClass = XposedHelpers.findClass("com.winlator.container.Shortcut", classLoader);
            method = ReflectUtil.getMethod(managerClass, "loadShortcuts", shortcutClass);
            shortcuts = method.invoke(manager, (Object) null);
        }

        return Convert.toList(shortcuts);
    }

}
