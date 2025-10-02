package com.eagle.emulator.util;

import android.app.Activity;

import org.luckypray.dexkit.DexKitBridge;
import org.luckypray.dexkit.query.FindField;
import org.luckypray.dexkit.query.matchers.FieldMatcher;
import org.luckypray.dexkit.result.FieldData;
import org.luckypray.dexkit.result.FieldDataList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import cn.hutool.core.util.ReflectUtil;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class DexKitUtil {


    static {
        initDexKit();
    }

    private static final List<Consumer<DexKitBridge>> consumers = new ArrayList<>();

    private static XC_LoadPackage.LoadPackageParam lpparam;


    private static void initDexKit() {

        try {
            System.loadLibrary("dexkit");
        } catch (Exception e) {
            XposedBridge.log("dexkit加载异常");
            XposedBridge.log(e);
        }

    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(Activity activity, FieldData fieldData) {
        try {
            Field fieldInstance = fieldData.getFieldInstance(lpparam.classLoader);
            return (T) ReflectUtil.getFieldValue(activity, fieldInstance);
        } catch (NoSuchFieldException e) {
            XposedBridge.log(e);
            return null;
        }
    }

    public static void addFind(Consumer<DexKitBridge> consumer) {
        consumers.add(consumer);
    }

    public static void runFind(XC_LoadPackage.LoadPackageParam lpparam) {
        DexKitUtil.lpparam = lpparam;
        String apkPath = lpparam.appInfo.sourceDir;
        try (DexKitBridge bridge = DexKitBridge.create(apkPath)) {
            for (Consumer<DexKitBridge> consumer : consumers) {
                consumer.accept(bridge);
            }
            consumers.clear();
        } catch (Throwable e) {
            XposedBridge.log(e);
        }

    }


    public static FieldData findFieldSingle(String declaredClassName, String fieldClassName, DexKitBridge bridge, XC_LoadPackage.LoadPackageParam lpparam) {
        return findFields(declaredClassName, fieldClassName, bridge, lpparam).single();
    }

    public static FieldData findFieldFirst(String declaredClassName, String fieldClassName, DexKitBridge bridge, XC_LoadPackage.LoadPackageParam lpparam) {
        return findFields(declaredClassName, fieldClassName, bridge, lpparam).get(0);
    }

    public static FieldDataList findFields(String declaredClassName, String fieldClassName, DexKitBridge bridge, XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> declaredClass = XposedHelpers.findClass(declaredClassName, lpparam.classLoader);
        Class<?> fieldClass = XposedHelpers.findClass(fieldClassName, lpparam.classLoader);
        return findFields(declaredClass, fieldClass, bridge);
    }

    public static FieldDataList findFields(Class<?> declaredClass, Class<?> fieldClass, DexKitBridge bridge) {
        FieldMatcher fieldMatcher = FieldMatcher.create().type(fieldClass).declaredClass(declaredClass);
        FindField matcher = FindField.create().matcher(fieldMatcher);
        return bridge.findField(matcher);
    }


    public static FieldData findFieldSingle(Class<?> declaredClass, Class<?> fieldClass, DexKitBridge bridge) {
        return findFields(declaredClass, fieldClass, bridge).single();
    }


    public static FieldData findFieldFirst(Class<?> declaredClass, Class<?> fieldClass, DexKitBridge bridge) {
        return findFields(declaredClass, fieldClass, bridge).get(0);
    }


}
