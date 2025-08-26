package com.eagle.emulator.plus.overlay.exagear;

import android.app.Activity;
import android.os.Environment;
import android.view.View;

import com.eagle.emulator.hook.windows.ExgearHook;
import com.eagle.emulator.plus.overlay.OverlayHook;
import com.eagle.emulator.util.XposedUtil;

import java.nio.file.Paths;
import java.util.function.Consumer;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ExagearOverlayHook extends OverlayHook {

    public static final String HOOK_CLASS_NAME = "com.termux.x11.MainActivity";

    public ExagearOverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam, HOOK_CLASS_NAME);
    }


    @Override
    protected String getConfigPath() {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return Paths.get(absolutePath, "Android", "data", "com.ludashi.benchmara", "files", "overlay").toString();
    }

    @Override
    protected View getView(Activity activity) {
        int resourceId = XposedUtil.getResourceId("mainView", lpparam, activity);
        return activity.findViewById(resourceId);
    }

    @Override
    protected String getName(Activity activity) {
        String currentName = ExgearHook.currentName;
        ExgearHook.currentName = null;
        return currentName;
    }

    @Override
    public void hookMethod(Consumer<XC_MethodHook.MethodHookParam> consumer) {
        XposedHelpers.findAndHookMethod("com.eltechs.axs.activities.XServerDisplayActivity", lpparam.classLoader, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                consumer.accept(param);
            }
        });
    }
}
