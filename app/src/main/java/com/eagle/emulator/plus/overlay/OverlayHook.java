package com.eagle.emulator.plus.overlay;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.eagle.emulator.hook.HookParams;

import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public abstract class OverlayHook {


    protected XC_LoadPackage.LoadPackageParam lpparam;
    protected String hookClassName;
    protected OverlayConfig config;

    public OverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        this.lpparam = lpparam;
    }

    public void hook() {
        if (config != null) {
            Log.i(HookParams.LOG_TAG, "Hook遮罩方法开始：" + hookClassName);
            XposedHelpers.findAndHookMethod(hookClassName, lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    handler(param);
                }
            });
        }
    }


    protected void handler(XC_MethodHook.MethodHookParam param) {
        Log.i(HookParams.LOG_TAG, "Hook遮罩开始");
        // 获取执行对象
        Activity activity = (Activity) param.thisObject;
        // 确认是对象类型
        String className = activity.getClass().getName();
        if (!className.equals(hookClassName)) {
            return;
        }

        View view = getView(activity);
        String name = getName(activity);
        String overlayImage = config.getOverlayImage(name);
        Log.i(HookParams.LOG_TAG, name + ":" + overlayImage);

        if (StrUtil.isNotBlank(overlayImage)) {
            view.setBackground(Drawable.createFromPath(overlayImage));
        }
    }


    public abstract View getView(Activity activity);

    public abstract String getName(Activity activity);


}
