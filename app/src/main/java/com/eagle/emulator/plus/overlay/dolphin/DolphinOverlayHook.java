package com.eagle.emulator.plus.overlay.dolphin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.eagle.emulator.hook.HookParams;
import com.eagle.emulator.plus.overlay.OverlayConfig;
import com.eagle.emulator.plus.overlay.OverlayHook;

import java.nio.file.Paths;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class DolphinOverlayHook extends OverlayHook {


    public static final String HOOK_CLASS_NAME = "org.dolphinemu.dolphinemu.activities.EmulationActivity";

    public DolphinOverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam);

        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String configPath = Paths.get(absolutePath, "Android", "data", HookParams.DOLPHIN, "files", "overlay").toString();
        if (StrUtil.isNotBlank(configPath)) {
            config = new OverlayConfig(configPath);
        }

        hookClassName = HOOK_CLASS_NAME;

    }

    public void hook() {
        if (config != null) {
            Log.i(HookParams.LOG_TAG, "Hook遮罩方法开始：" + hookClassName);
            XposedHelpers.findAndHookMethod(hookClassName, lpparam.classLoader, "onResume", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    handler(param);
                }
            });
        }
    }

    @Override
    @SuppressLint("ResourceType")
    public View getView(Activity activity) {
        return activity.findViewById(2131362585);
    }

    @Override
    public String getName(Activity activity) {
        String[] selectedGames = activity.getIntent().getStringArrayExtra("SelectedGames");
        if (selectedGames != null) {
            return FileNameUtil.mainName(selectedGames[0]);
        } else {
            return null;
        }
    }
}
