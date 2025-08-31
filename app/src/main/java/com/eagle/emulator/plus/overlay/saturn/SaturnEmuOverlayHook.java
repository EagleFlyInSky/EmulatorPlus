package com.eagle.emulator.plus.overlay.saturn;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.eagle.emulator.HookParams;
import com.eagle.emulator.plus.overlay.BaseOverlayConfig;
import com.eagle.emulator.plus.overlay.OverlayHook;

import java.nio.file.Paths;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SaturnEmuOverlayHook extends OverlayHook {

    public static final String HOOK_CLASS_NAME = "com.imagine.BaseActivity";

    private Activity currentActivity;

    public SaturnEmuOverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam, HOOK_CLASS_NAME);
    }

    protected void initConfig() {
        String configPath = getConfigPath();
        if (StrUtil.isNotBlank(configPath)) {
            config = new BaseOverlayConfig(configPath, true);
        }
    }

    @Override
    protected String getConfigPath() {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return Paths.get(absolutePath, "Android", "data", HookParams.SATURN_EMU, "files", "overlay").toString();
    }


    @Override
    protected void setBackground(View view, String overlayImage) {
        if (view instanceof ViewGroup) {
            View overlay = new View(currentActivity);
            overlay.setBackground(Drawable.createFromPath(overlayImage));
            Log.i(HookParams.LOG_TAG, "设置背景");
            ((ViewGroup) view).addView(overlay, 1);
        }
    }

    @Override
    protected View getView(Activity activity) {
        this.currentActivity = activity;
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        ImageView overlayView = new ImageView(activity);
        overlayView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        viewGroup.addView(overlayView);
        return overlayView;
    }

    @Override
    protected String getName(Activity activity) {
        String dataString = activity.getIntent().getDataString();
        String decode = URLUtil.decode(dataString);
        return FileNameUtil.mainName(decode);
    }


    private void hookChangeFile() {
        XposedHelpers.findAndHookMethod(HOOK_CLASS_NAME, lpparam.classLoader, "onActivityResult", int.class, int.class, Intent.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Log.i(HookParams.LOG_TAG, "onActivityResult");
            }
        });
    }
}
