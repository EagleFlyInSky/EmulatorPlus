package com.eagle.emulator.plus.overlay.saturn;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;

import com.eagle.emulator.HookParams;
import com.eagle.emulator.hook.tools.ViewFind;
import com.eagle.emulator.plus.overlay.BaseOverlayConfig;
import com.eagle.emulator.plus.overlay.OverlayHook;
import com.eagle.emulator.plus.overlay.ViewInfo;

import java.nio.file.Paths;
import java.util.function.Consumer;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 目前无效
 */
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
        View decorView = activity.getWindow().getDecorView();
        ViewFind.log(decorView);
        ViewGroup content = activity.findViewById(android.R.id.content);
        return ViewInfo.builder().parentView(content).addImageView(true).build();
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
                XposedBridge.log("onActivityResult");
            }
        });
    }
}
