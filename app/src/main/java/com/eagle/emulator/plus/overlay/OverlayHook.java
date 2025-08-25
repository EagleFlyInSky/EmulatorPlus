package com.eagle.emulator.plus.overlay;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.eagle.emulator.hook.HookParams;

import org.luckypray.dexkit.DexKitBridge;
import org.luckypray.dexkit.query.FindField;
import org.luckypray.dexkit.query.matchers.FieldMatcher;
import org.luckypray.dexkit.result.FieldData;

import java.util.Arrays;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public abstract class OverlayHook {

    protected XC_LoadPackage.LoadPackageParam lpparam;
    protected String hookClassName;
    protected Class<?> hookClass;
    protected OverlayConfig config;

    public OverlayHook(XC_LoadPackage.LoadPackageParam lpparam, String hookClassName) {
        this(lpparam, hookClassName, false);
    }

    public OverlayHook(XC_LoadPackage.LoadPackageParam lpparam, String hookClassName, boolean dexkit) {
        this.lpparam = lpparam;
        this.hookClassName = hookClassName;
        this.hookClass = XposedHelpers.findClass(hookClassName, lpparam.classLoader);

        String configPath = getConfigPath();
        if (StrUtil.isNotBlank(configPath)) {
            config = new OverlayConfig(configPath);
        }

        if (dexkit) {
            System.loadLibrary("dexkit");
            initFindField();
        }
    }


    protected FieldData findField(Class<?> activityClass, String className, DexKitBridge bridge) {
        Class<?> fieldClass = XposedHelpers.findClass(className, lpparam.classLoader);
        FieldMatcher fieldMatcher = FieldMatcher.create().type(fieldClass).declaredClass(activityClass);
        FindField matcher = FindField.create().matcher(fieldMatcher);
        return bridge.findField(matcher).single();
    }


    protected FieldData findFieldFirst(Class<?> activityClass, String className, DexKitBridge bridge) {
        Class<?> fieldClass = XposedHelpers.findClass(className, lpparam.classLoader);
        FieldMatcher fieldMatcher = FieldMatcher.create().type(fieldClass).declaredClass(activityClass);
        FindField matcher = FindField.create().matcher(fieldMatcher);
        return bridge.findField(matcher).get(0);
    }

    /**
     * 使用 dexkit 定位属性 突破混淆
     */
    private void initFindField() {
        String apkPath = lpparam.appInfo.sourceDir;
        try (DexKitBridge bridge = DexKitBridge.create(apkPath)) {
            initField(bridge);
        } catch (Throwable e) {
            Log.e(HookParams.LOG_TAG, e.toString());
            Log.e(HookParams.LOG_TAG, Arrays.toString(e.getStackTrace()));
        }
    }

    protected void initField(DexKitBridge bridge) {

    }

    @SuppressWarnings("unchecked")
    protected <T> T getField(Activity activity, FieldData fieldData) {
        try {
            return (T) ReflectUtil.getFieldValue(activity, fieldData.getFieldInstance(lpparam.classLoader));
        } catch (NoSuchFieldException e) {
            Log.e(HookParams.LOG_TAG, e.toString());
            Log.e(HookParams.LOG_TAG, Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    public void hook() {
        if (config != null) {
            Log.i(HookParams.LOG_TAG, "Hook遮罩方法开始：" + hookClassName);
            XposedHelpers.findAndHookMethod(hookClassName, lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.i(HookParams.LOG_TAG, "Hook遮罩方法运行");
                    super.afterHookedMethod(param);
                    handler(param);
                }
            });
        }
    }


    protected void handler(XC_MethodHook.MethodHookParam param) {
        // 获取执行对象
        Activity activity = (Activity) param.thisObject;
        // 确认是对象类型
        String className = activity.getClass().getName();
        if (!className.equals(hookClassName)) {
            return;
        }

        View view = getView(activity);
        if (view != null) {
            Log.i(HookParams.LOG_TAG, "view :" + view.getClass().getSimpleName() + "-" + view.getId());
            String name = getName(activity);
            String overlayImage = config.getOverlayImage(name);
            Log.i(HookParams.LOG_TAG, name + ":" + overlayImage);

            if (StrUtil.isNotBlank(overlayImage)) {
                view.setBackground(Drawable.createFromPath(overlayImage));
            }
        } else {
            Log.i(HookParams.LOG_TAG, "view : 空");
        }
    }

    protected abstract String getConfigPath();

    protected abstract View getView(Activity activity);

    protected abstract String getName(Activity activity);


}
