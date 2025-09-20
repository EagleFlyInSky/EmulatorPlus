package com.eagle.emulator.dex;

import com.eagle.emulator.plus.overlay.aopaop.AopAopOverlayHook;
import com.eagle.emulator.util.DexKitUtil;

import org.luckypray.dexkit.query.FindClass;
import org.luckypray.dexkit.query.FindField;
import org.luckypray.dexkit.query.matchers.ClassMatcher;
import org.luckypray.dexkit.query.matchers.FieldMatcher;
import org.luckypray.dexkit.query.matchers.FieldsMatcher;
import org.luckypray.dexkit.result.ClassData;
import org.luckypray.dexkit.result.FieldData;

import java.lang.reflect.Modifier;

import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AopAopDex {

    public static FieldData boxStoreFieldData;

    public static FieldData gameFieldData;

    public static void init(XC_LoadPackage.LoadPackageParam lpparam) {

        DexKitUtil.addFind(bridge -> {
            FieldsMatcher fieldsMatcher = FieldsMatcher.create().add(FieldMatcher.create().modifiers(Modifier.PUBLIC | Modifier.STATIC).type("io.objectbox.BoxStore")).count(1);
            ClassMatcher classMatcher = ClassMatcher.create().fields(fieldsMatcher);
            FindClass matcher = FindClass.create().matcher(classMatcher);
            ClassData storeClassData = bridge.findClass(matcher).single();
            boxStoreFieldData = storeClassData.getFields().get(0);
        });

        DexKitUtil.addFind(bridge -> {
            Class<?> fieldClass = XposedHelpers.findClass("com.aopaop.app.entity.game.LocalGameEntity", lpparam.classLoader);
            FieldMatcher fieldMatcher = FieldMatcher.create().type(fieldClass).declaredClass(AopAopOverlayHook.HOOK_CLASS_NAME);
            FindField matcher = FindField.create().matcher(fieldMatcher);
            gameFieldData = bridge.findField(matcher).get(0);
        });

        DexKitUtil.runFind(lpparam);

        XposedBridge.log(StrUtil.format("boxStoreFieldData：{}", boxStoreFieldData));
        XposedBridge.log(StrUtil.format("gameFieldData：{}", boxStoreFieldData));
    }
}
