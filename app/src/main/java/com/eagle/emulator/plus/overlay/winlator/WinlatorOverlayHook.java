package com.eagle.emulator.plus.overlay.winlator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;

import com.eagle.emulator.hook.HookParams;
import com.eagle.emulator.hook.windows.WinlatorHook;
import com.eagle.emulator.plus.overlay.OverlayConfig;
import com.eagle.emulator.plus.overlay.OverlayHook;
import com.eagle.emulator.util.XposedUtil;

import java.nio.file.Paths;

import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class WinlatorOverlayHook extends OverlayHook {

    public static final String HOOK_CLASS_NAME = "com.winlator.XServerDisplayActivity";

    public WinlatorOverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam);

        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String configPath = Paths.get(absolutePath, "Android", "data", HookParams.WINLATOR, "files", "overlay").toString();
        if (StrUtil.isNotBlank(configPath)) {
            config = new OverlayConfig(configPath);
        }

        hookClassName = HOOK_CLASS_NAME;
    }

    @Override
    @SuppressLint("ResourceType")
    public View getView(Activity activity) {
        int resId = XposedUtil.getResourceId("FLXServerDisplay", lpparam);
        ViewGroup viewGroup = activity.findViewById(resId);
        return viewGroup.getChildAt(0);
    }

    @Override
    public String getName(Activity activity) {
        return activity.getIntent().getStringExtra(WinlatorHook.SHORTCUT_NAME);
    }
}
