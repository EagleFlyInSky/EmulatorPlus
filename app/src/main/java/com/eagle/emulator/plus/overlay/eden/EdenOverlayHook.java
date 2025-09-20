package com.eagle.emulator.plus.overlay.eden;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.View;

import com.eagle.emulator.HookParams;
import com.eagle.emulator.plus.overlay.OverlayHook;
import com.eagle.emulator.plus.overlay.ViewInfo;
import com.eagle.emulator.util.XposedUtil;

import java.nio.file.Paths;
import java.util.function.Consumer;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class EdenOverlayHook extends OverlayHook {

    public static final String HOOK_CLASS_NAME = "org.yuzu.yuzu_emu.activities.EmulationActivity";

    public EdenOverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam, HOOK_CLASS_NAME);
    }

    @Override
    protected String getConfigPath() {
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return Paths.get(sdcardPath, "Android", "data", HookParams.EDEN, "files", "overlay").toString();
        //return Paths.get(sdcardPath, "PSP", "overlay").toString();
    }

    @Override
    public void hookMethod(Consumer<XC_MethodHook.MethodHookParam> consumer) {
        XposedHelpers.findAndHookMethod(hookClassName, lpparam.classLoader, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                consumer.accept(param);
            }
        });
    }

    @Override
    protected ViewInfo getViewInfo(Activity activity) {

        int surfaceInputOverlay = XposedUtil.getResourceId("surface_input_overlay", lpparam, activity);
        View view = activity.findViewById(surfaceInputOverlay);

        int surfaceEmulation = XposedUtil.getResourceId("emulation_container", lpparam, activity);
        View gameView = activity.findViewById(surfaceEmulation);

        return ViewInfo.builder().overlayView(view).gameView(gameView).build();
    }


    @Override
    public String getName(Activity activity) {

        String dataString = activity.getIntent().getDataString();
        if (StrUtil.isNotBlank(dataString)) {
            String decode = URLUtil.decode(dataString);
            return FileNameUtil.mainName(decode);
        }

        Bundle extras = activity.getIntent().getExtras();
        if (extras != null) {
            Parcelable game = extras.getParcelable("game");
            String path = (String) ReflectUtil.getFieldValue(game, "path");
            String decode = URLUtil.decode(path);
            return FileNameUtil.mainName(decode);
        }

        return null;
    }
}
