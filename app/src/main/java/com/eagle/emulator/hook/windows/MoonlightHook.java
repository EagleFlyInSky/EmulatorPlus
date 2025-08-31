package com.eagle.emulator.hook.windows;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.nio.charset.StandardCharsets;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
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

                Setting setting = new Setting(FileUtil.file(path), StandardCharsets.UTF_8, false);

                String appId = setting.getStr("AppId");
                if (StrUtil.isNotBlank(appId)) intent.putExtra("AppId", appId);

                String uuid = setting.getStr("UUID");
                if (StrUtil.isNotBlank(uuid)) intent.putExtra("UUID", uuid);

                String appName = setting.getStr("AppName");
                if (StrUtil.isNotBlank(appName)) intent.putExtra("AppName", appName);

                Boolean hdr = setting.getBool("HDR");
                if (ObjUtil.isNotEmpty(hdr)) intent.putExtra("HDR", hdr);
            }
        });

    }
}
