package com.eagle.emulator.hook.windows;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.eagle.emulator.HookParams;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ReflectUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ExgearHook {


    public static final String PACKAGE_NAME = "com.ludashi.benchmara";
    public static final String CLASS_NAME = PACKAGE_NAME + ".activities.EDStartupActivity";

    public static final String HOOK_CLASS_NAME = PACKAGE_NAME + ".activities.EDMainActivity";
    public static final String CONTAINER_ID = "container_id";
    public static final String ABSOLUTE_PATH = "desktop_file_absolute_path";

    /**
     * 数据传递
     */
    public static String currentName;


    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        hookParam(lpparam);
        hookStart(lpparam);
    }


    public static void hookStart(XC_LoadPackage.LoadPackageParam lpparam) {

        Class<?> clazz = XposedHelpers.findClass(HOOK_CLASS_NAME, lpparam.classLoader);
        XposedHelpers.findAndHookMethod(clazz, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Log.i(HookParams.LOG_TAG, "Hook开始");
                hook(param);
            }

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            private void hook(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;

                List<?> nodeList = getNodeList(activity);

                // 清空文件夹
                String dirPath = "/storage/emulated/0/exagear/desktop/";
                FileUtil.clean(dirPath);

                for (Object o : nodeList) {
                    Object link = XposedHelpers.getObjectField(o, "mLink");
                    if (link != null) {
                        String name = (String) XposedHelpers.getObjectField(link, "name");

                        File path = (File) XposedHelpers.getObjectField(link, "linkFile");
                        String absolutePath = path.getAbsolutePath();

                        Object guestCont = XposedHelpers.getObjectField(link, "guestCont");
                        Long id = (Long) XposedHelpers.getObjectField(guestCont, "mId");

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(CONTAINER_ID, id);
                        jsonObject.put(ABSOLUTE_PATH, absolutePath);

                        String text = jsonObject.toString();
                        String filePath = dirPath + name + ".desktop";
                        File file = FileUtil.newFile(filePath);
                        FileUtil.writeString(text, file, StandardCharsets.UTF_8);
                        Log.i(HookParams.LOG_TAG, "写入快捷方式：" + filePath + "\n" + text);
                    }
                }
            }

            @SuppressWarnings("unused")
            private Object findShortcut(Activity activity, String shortcutName) throws Throwable {
                List<?> list = getNodeList(activity);

                Object shortcut = null;
                for (Object o : list) {
                    Log.i(HookParams.LOG_TAG, o.toString());
                    Object link = XposedHelpers.getObjectField(o, "mLink");
                    String name = (String) XposedHelpers.getObjectField(link, "name");

                    if (shortcutName.equals(name)) {
                        return link;
                    }
                }
                return shortcut;
            }

            private List<?> getNodeList(Activity activity) throws IllegalAccessException, InvocationTargetException {
                Class<?> mainClass = XposedHelpers.findClass(HOOK_CLASS_NAME, lpparam.classLoader);
                Method getSupportFragmentManager = ReflectUtil.getMethod(mainClass, "getSupportFragmentManager");
                Object fragmentManager = getSupportFragmentManager.invoke(activity);

                Class<?> fragmentManagerClass = XposedHelpers.findClass("android.support.v4.app.FragmentManagerImpl", lpparam.classLoader);
                Method getFragments = ReflectUtil.getMethod(fragmentManagerClass, "getFragments");
                Object fragmentList = getFragments.invoke(fragmentManager);

                List<?> fragments = Convert.toList(fragmentList);

                String fragmentClassName = PACKAGE_NAME + ".fragments.ChooseXDGLinkFragment";
                Object fragment = fragments.stream().filter(e -> fragmentClassName.equals(e.getClass().getName())).findFirst().get();

                Class<?> fragmentClass = XposedHelpers.findClass(fragmentClassName, lpparam.classLoader);
                Method getRootNodeContent = ReflectUtil.getMethod(fragmentClass, "getRootNodeContent");
                getRootNodeContent.setAccessible(true);
                Object result = getRootNodeContent.invoke(fragment);
                return Convert.toList(result);
            }
        });
    }


    public static void hookParam(XC_LoadPackage.LoadPackageParam lpparam) {

        Class<?> startClass = XposedHelpers.findClass("com.eltechs.axs.activities.StartupActivity", lpparam.classLoader);

        XposedHelpers.findAndHookMethod(startClass, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                // 获取执行对象
                Activity activity = (Activity) param.thisObject;
                // 确认是对象类型
                String name = activity.getClass().getName();
                if (!name.equals(CLASS_NAME)) {
                    return;
                }

                String data = activity.getIntent().getDataString();
                if (FileUtil.exist(data)) {
                    String text = FileUtil.readString(data, StandardCharsets.UTF_8);
                    JSONObject jsonObject = new JSONObject(text);
                    activity.getIntent().putExtra(CONTAINER_ID, jsonObject.getLong(CONTAINER_ID));
                    String path = jsonObject.getString(ABSOLUTE_PATH);
                    activity.getIntent().putExtra(ABSOLUTE_PATH, path);
                    ExgearHook.currentName = FileNameUtil.mainName(path);
                }
            }
        });

        Class<?> mainClass = XposedHelpers.findClass(HOOK_CLASS_NAME, lpparam.classLoader);
        Class<?> linkClass = XposedHelpers.findClass(PACKAGE_NAME + ".XDGLink", lpparam.classLoader);

        XposedHelpers.findAndHookMethod(mainClass, "onXDGLinkSelected", linkClass, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                // 获取执行对象
                Activity activity = (Activity) param.thisObject;
                Log.i(HookParams.LOG_TAG, "onXDGLinkSelected :" + activity.getClass().getName());
                // 确认是对象类型
                String name = activity.getClass().getName();
                if (!name.equals(HOOK_CLASS_NAME)) {
                    return;
                }

                Object link = param.args[0];
                File file = (File) XposedHelpers.getObjectField(link, "linkFile");
                String path = file.getAbsolutePath();
                ExgearHook.currentName = FileNameUtil.mainName(path);

            }
        });

    }


}
