package com.eagle.emulator.hook.windows;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.eagle.emulator.util.IniUtil;

import org.json.JSONObject;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MoonlightHook {


    public static final String HOOK_CLASS_NAME = "com.limelight.ShortcutTrampoline";

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {


        Class<?> clazz = XposedHelpers.findClass(HOOK_CLASS_NAME, lpparam.classLoader);
        XposedHelpers.findAndHookMethod(clazz, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Activity activity = (Activity) param.thisObject;
                Intent intent = activity.getIntent();
                String path = intent.getDataString();
                if (StrUtil.isBlank(path)) return;

                JSONObject info = IniUtil.parseIni(path);

                String appId = info.getString("AppId");
                if (StrUtil.isNotBlank(appId)) intent.putExtra("AppId",appId);

                String uuid = info.getString("UUID");
                if (StrUtil.isNotBlank(uuid)) intent.putExtra("UUID",uuid);

                String appName = info.getString("AppName");
                if (StrUtil.isNotBlank(appName)) intent.putExtra("AppName",appName);

                Boolean hdr = info.getBoolean("HDR");
                if (ObjUtil.isNotEmpty(hdr)) intent.putExtra("HDR",hdr);
            }
        });

    }
}
