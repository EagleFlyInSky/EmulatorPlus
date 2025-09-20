package com.eagle.emulator.plus.overlay.winlator;

import android.app.Activity;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;

import com.eagle.emulator.HookParams;
import com.eagle.emulator.hook.windows.WinlatorHook;
import com.eagle.emulator.plus.overlay.OverlayHook;
import com.eagle.emulator.plus.overlay.ViewInfo;
import com.eagle.emulator.util.XposedUtil;

import java.nio.file.Paths;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class WinlatorOverlayHook extends OverlayHook {

    public static final String HOOK_CLASS_NAME = "com.winlator.XServerDisplayActivity";

    public WinlatorOverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam, HOOK_CLASS_NAME);
    }


    @Override
    protected String getConfigPath() {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return Paths.get(absolutePath, "Android", "data", HookParams.WINLATOR, "files", "overlay").toString();
    }


    @Override
    protected ViewInfo getViewInfo(Activity activity) {
        int resId = XposedUtil.getResourceId("FLXServerDisplay", lpparam, activity);
        ViewGroup viewGroup = activity.findViewById(resId);
        View gameView = viewGroup.getChildAt(0);

        return ViewInfo.builder().gameView(gameView).parentView(viewGroup).addImageView(true).imageViewIndex(1).build();
    }


    @Override
    public String getName(Activity activity) {
        return activity.getIntent().getStringExtra(WinlatorHook.SHORTCUT_NAME);
    }
}
