package com.eagle.emulator.plus.overlay;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.eagle.emulator.HookParams;

import org.luckypray.dexkit.DexKitBridge;
import org.luckypray.dexkit.query.FindField;
import org.luckypray.dexkit.query.matchers.FieldMatcher;
import org.luckypray.dexkit.result.FieldData;

import java.util.Arrays;
import java.util.function.Consumer;

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

        initConfig();

        if (dexkit) {
            System.loadLibrary("dexkit");
            initFindField();
        }
    }

    protected void initConfig() {
        String configPath = getConfigPath();
        if (StrUtil.isNotBlank(configPath)) {
            config = new BaseOverlayConfig(configPath);
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

    public final void hook() {
        if (config != null) {
            Log.i(HookParams.LOG_TAG, "Hook遮罩方法开始：" + hookClassName);
            hookMethod(param -> {
                Log.i(HookParams.LOG_TAG, "Hook遮罩方法运行");
                handler(param);
            });
            hookPlus();
        } else {
            Log.i(HookParams.LOG_TAG, "遮罩配置为空");
        }
    }

    public void hookPlus() {
    }


    public void hookMethod(Consumer<XC_MethodHook.MethodHookParam> consumer) {
        XposedHelpers.findAndHookMethod(hookClassName, lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                consumer.accept(param);
            }
        });
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

            GameInfo gameInfo = getGameInfo(activity);
            if (gameInfo == null) {
                return;
            }

            String overlayImage = config.getOverlayImage(gameInfo);

            String name = gameInfo.getName();

            if (StrUtil.isNotBlank(overlayImage)) {
                Log.i(HookParams.LOG_TAG, name + ":" + overlayImage);
                setBackground(view, overlayImage);
            }
        } else {
            Log.i(HookParams.LOG_TAG, "view : 空");
        }
    }

    protected void setBackground(View view, String overlayImage) {
        Drawable drawable = Drawable.createFromPath(overlayImage);
        if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }

    protected abstract String getConfigPath();

    protected abstract View getView(Activity activity);

    protected abstract String getName(Activity activity);

    protected GameInfo getGameInfo(Activity activity) {
        return new GameInfo(getName(activity));
    }


}
