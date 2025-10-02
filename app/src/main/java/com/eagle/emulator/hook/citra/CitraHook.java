package com.eagle.emulator.hook.citra;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.eagle.emulator.util.DexKitUtil;

import org.luckypray.dexkit.query.FindMethod;
import org.luckypray.dexkit.query.matchers.MethodMatcher;
import org.luckypray.dexkit.result.MethodData;

import java.lang.reflect.Modifier;

import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import lombok.SneakyThrows;

public class CitraHook {

    public static MethodData methodData;

    public static boolean hasClass(XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> clazz = XposedHelpers.findClass("org.citra.emu.ui.EmulationActivity", lpparam.classLoader);
        return clazz != null;
    }

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {


        DexKitUtil.addFind(dexKitBridge -> {
            MethodMatcher methodMatcher = MethodMatcher.create().modifiers(Modifier.PUBLIC | Modifier.STATIC).paramCount(1).paramTypes(Context.class).usingEqStrings("citra-emu").returnType(void.class);
            methodData = dexKitBridge.findMethod(FindMethod.create().excludePackages("android", "androidx", "com.google", "org.citra.emu").matcher(methodMatcher)).single();
        });

        DexKitUtil.runFind(lpparam);


        XposedHelpers.findAndHookMethod("org.citra.emu.ui.EmulationActivity", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @SneakyThrows
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Activity activity = (Activity) param.thisObject;
                Intent intent = activity.getIntent();
                String gameId = intent.getStringExtra("GameId");
                if (StrUtil.isBlank(gameId)) {
                    Class<?> dirClass = XposedHelpers.findClass(methodData.getClassName(), lpparam.classLoader);
                    XposedHelpers.callStaticMethod(dirClass, methodData.getMethodName(), activity);
                }
            }
        });

    }
}
