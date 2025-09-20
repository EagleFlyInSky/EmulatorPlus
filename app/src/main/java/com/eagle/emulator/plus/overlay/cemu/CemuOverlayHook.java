package com.eagle.emulator.plus.overlay.cemu;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.eagle.emulator.HookParams;
import com.eagle.emulator.plus.overlay.OverlayHook;
import com.eagle.emulator.plus.overlay.ViewInfo;
import com.eagle.emulator.util.XposedUtil;

import java.nio.file.Paths;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class CemuOverlayHook extends OverlayHook {

    public static final String HOOK_CLASS_NAME = "info.cemu.cemu.emulation.EmulationActivity";

    public CemuOverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam, HOOK_CLASS_NAME);
    }

    @Override
    protected String getConfigPath() {
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return Paths.get(sdcardPath, "Android", "data", HookParams.EDEN, "files", "overlay").toString();
    }

    @Override
    protected ViewInfo getViewInfo(Activity activity) {

        int overlay = XposedUtil.getResourceId("input_overlay", lpparam, activity);
        View overlayView = activity.findViewById(overlay);

        int game = XposedUtil.getResourceId("main_canvas", lpparam, activity);
        View gameView = activity.findViewById(game);

        return ViewInfo.builder().overlayView(overlayView).gameView(gameView).build();
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
            String launchPath = extras.getString("info.cemu.cemu.LaunchPath");
            String decode = URLUtil.decode(launchPath);
            return FileNameUtil.mainName(decode);
        }

        return null;
    }
}
