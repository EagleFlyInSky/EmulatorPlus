package com.eagle.emulator.plus.overlay.m64;

import android.app.Activity;
import android.os.Environment;
import android.view.View;

import com.eagle.emulator.HookParams;
import com.eagle.emulator.plus.overlay.OverlayHook;
import com.eagle.emulator.plus.overlay.ViewInfo;
import com.eagle.emulator.util.XposedUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.URLUtil;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class M64OverlayHook extends OverlayHook {

    public static final String HOOK_CLASS_NAME = "paulscode.android.mupen64plusae.game.GameActivity";

    public M64OverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam, HOOK_CLASS_NAME);
    }

    @Override
    protected String getConfigPath() {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return Paths.get(absolutePath, "Android", "data", HookParams.M64, "files", "overlay").toString();
    }

    @Override
    protected ViewInfo getViewInfo(Activity activity) {
        int resourceId = XposedUtil.getResourceId("gameOverlay", lpparam, activity);
        View overlayView = activity.findViewById(resourceId);
        return ViewInfo.builder().overlayView(overlayView).build();
    }

    @Override
    protected View getView(Activity activity) {
        int resourceId = XposedUtil.getResourceId("gameOverlay", lpparam, activity);
        return activity.findViewById(resourceId);
    }

    @Override
    protected String getName(Activity activity) {
        String path = (String) ReflectUtil.getFieldValue(activity, "mRomPath");
        String decode = URLUtil.decode(path, StandardCharsets.UTF_8);
        return FileUtil.mainName(decode);
    }
}
