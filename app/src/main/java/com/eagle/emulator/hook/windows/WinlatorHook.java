package com.eagle.emulator.hook.windows;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.eagle.emulator.hook.HookParams;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class WinlatorHook {


    public static final String CLASS_NAME = "com.winlator.XServerDisplayActivity";
    public static final String HOOK_CLASS_NAME = "com.winlator.MainActivity";


    public static boolean hasClass(XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> clazz = XposedHelpers.findClass(HOOK_CLASS_NAME, lpparam.classLoader);
        Log.i(HookParams.LOG_TAG, "class" + clazz.getName());
        return clazz != null;
    }


    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {

        Class<?> clazz = XposedHelpers.findClass(HOOK_CLASS_NAME, lpparam.classLoader);
        XposedHelpers.findAndHookMethod(clazz, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Log.i(HookParams.LOG_TAG, "Hook开始");
                hook(param);
            }

            private void hook(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;

                String shortcutName = activity.getIntent().getStringExtra("shortcutName");
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

                Log.i(HookParams.LOG_TAG, "id:" + id + " path:" + path);

                Intent intent = new Intent();
                // 将解析得到的值设置到Intent中
                intent.putExtra("container_id", id);
                intent.putExtra("shortcut_path", path);
                // 替换指向的类
                intent.setClassName(activity, CLASS_NAME);
                // 清除调用状态
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                activity.startActivity(intent);

            }

            private Object findShortcut(Activity activity, String shortcutName) throws Throwable {
                List<?> list = getShortcutList(activity);

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

            private List<?> getShortcutList(Activity activity) throws IllegalAccessException, InstantiationException, InvocationTargetException {
                Class<?> managerClass = XposedHelpers.findClass("com.winlator.container.ContainerManager", lpparam.classLoader);
                Constructor<?> constructor = XposedHelpers.findConstructorBestMatch(managerClass, Context.class);
                Object manager = constructor.newInstance(activity.getBaseContext());

                Object shortcuts;
                Method method = ReflectUtil.getMethod(managerClass, "loadShortcuts");
                if (method!=null){
                    method = ReflectUtil.getMethod(managerClass, "loadShortcuts");
                    shortcuts = method.invoke(manager);
                }else {
                    Class<?> shortcutClass = XposedHelpers.findClass("com.winlator.container.Shortcut", lpparam.classLoader);
                    method = ReflectUtil.getMethod(managerClass, "loadShortcuts", shortcutClass);
                    shortcuts = method.invoke(manager, (Object) null);
                }

                return Convert.toList(shortcuts);
            }
        });
    }

}
