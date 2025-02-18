package com.eagle.emulator.hook.es;

import android.content.Intent;
import android.util.Log;

import com.eagle.emulator.hook.HookParams;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class EsDeHook {


    public static final String HOOK_CLASS_NAME = "org.es_de.frontend.MainActivity";

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        hookStart(lpparam);
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
                Log.i(HookParams.LOG_TAG, "class  :" + className);
                Log.i(HookParams.LOG_TAG, "data   :" + intent.getDataString());
                Log.i(HookParams.LOG_TAG, "extras :" + intent.getExtras());
            }
        });

    }

    private static void hookStart(XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> mainActivityClass = XposedHelpers.findClass(HOOK_CLASS_NAME, lpparam.classLoader);

        XposedHelpers.findAndHookMethod(mainActivityClass, "launchGame", String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, HashMap.class, HashMap.class, HashMap.class, List.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String data = (String) param.args[5];
                String systemPath = (String) param.args[6];
                String path = fix((String) param.args[7]);

                Log.i(HookParams.LOG_TAG, "data:" + data);

                String romsafKey = "%ROMSAF%";
                String romKey = "%ROM%";
                String romProviderKey = "%ROMPROVIDER%";

                ArrayList<String> keys = CollUtil.newArrayList(romKey, romsafKey, romProviderKey);
                if (CollUtil.contains(keys, data)) return;

                String new_data = data;

                String quotationKey = "%QUOTATION%";
                if (StrUtil.contains(data, quotationKey)) {
                    new_data = data.replace(quotationKey, "\"");
                }

                Log.i(HookParams.LOG_TAG, "new_data:" + new_data);
                if (StrUtil.contains(new_data, romKey)) {
                    new_data = new_data.replace(romKey, path);
                } else if (StrUtil.contains(new_data, romsafKey)) {
                    Method getSAFFileURI = XposedHelpers.findMethodBestMatch(mainActivityClass, "getSAFFileURI", String.class, String.class);
                    String romsaf = ReflectUtil.invoke(null, getSAFFileURI, path, systemPath);
                    Log.i(HookParams.LOG_TAG, "romsaf:" + romsaf);
                    if (StrUtil.isNotBlank(romsaf)) {
                        new_data = new_data.replace(romsafKey, romsaf);
                    }
                } else if (StrUtil.contains(new_data, romProviderKey)) {
                    Class<?> bclass = XposedHelpers.findClass("androidx.core.content.b", lpparam.classLoader);

                    Class<?> beanClass = XposedHelpers.findClass("org.libsdl.app.SDLActivity", lpparam.classLoader);
                    Field tField = ReflectUtil.getField(beanClass, "t");
                    Object t = ReflectUtil.getStaticFieldValue(tField);

                    Method method = ReflectUtil.getMethod(bclass, "getUriForFile", tField.getType(), String.class, File.class);
                    String romProvider = ReflectUtil.invokeStatic(method, t, "org.es_de.frontend.files", new File(path)).toString() ;

                    Log.i(HookParams.LOG_TAG, "romProvider:" + romProvider);
                    if (StrUtil.isNotBlank(romProvider)) {
                        new_data = new_data.replace(romProviderKey, romProvider);
                    }
                }
                Log.i(HookParams.LOG_TAG, "new_data:" + new_data);

                param.args[5] = new_data;

            }

            public void bak(MethodHookParam param) {
                String packageName = (String) param.args[0];
                String className = (String) param.args[1];
                String action = (String) param.args[2];
                String category = (String) param.args[3];
                String dataType = (String) param.args[4];
                String data = (String) param.args[5];
                String systemPath = (String) param.args[6];
                String path = (String) param.args[7];
            }

            public String fix(String path) {
                String result = path;
                try {
                    result = new File(path).getCanonicalPath();
                } catch (Exception ignored) {
                }

                return result;
            }


        });
    }

}
