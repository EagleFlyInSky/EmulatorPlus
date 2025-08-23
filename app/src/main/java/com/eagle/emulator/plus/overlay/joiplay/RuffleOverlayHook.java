package com.eagle.emulator.plus.overlay.joiplay;

import android.app.Activity;
import android.view.View;

import cn.hutool.core.util.ReflectUtil;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class RuffleOverlayHook extends JoiPlayOverlayHook {

    public static final String HOOK_CLASS_NAME = "cyou.joiplay.runtime.ruffle.MainActivity";

    public RuffleOverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam);
        hookClassName = HOOK_CLASS_NAME;
    }

    @Override
    public View getView(Activity activity) {
        return (View) ReflectUtil.getFieldValue(activity, "mSurfaceView");
    }
}
