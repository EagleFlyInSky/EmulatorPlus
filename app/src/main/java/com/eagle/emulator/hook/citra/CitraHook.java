package com.eagle.emulator.hook.citra;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.eagle.emulator.util.DexKitUtil;

import org.luckypray.dexkit.query.FindMethod;
import org.luckypray.dexkit.query.matchers.MethodMatcher;
import org.luckypray.dexkit.result.MethodData;

import java.lang.reflect.Modifier;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import lombok.SneakyThrows;

public class CitraHook {

    public static MethodData methodData;

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
                Class<?> dirClass = XposedHelpers.findClass(methodData.getClassName(), lpparam.classLoader);
                XposedHelpers.callStaticMethod(dirClass, methodData.getMethodName(), activity);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });

    }
}
