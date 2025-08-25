package com.eagle.emulator.plus.overlay.dolphin;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.eagle.emulator.hook.HookParams;
import com.eagle.emulator.plus.overlay.OverlayHook;

import java.nio.file.Paths;

import cn.hutool.core.io.file.FileNameUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class DolphinOverlayHook extends OverlayHook {


    public static final String HOOK_CLASS_NAME = "org.dolphinemu.dolphinemu.activities.EmulationActivity";

    public DolphinOverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam, HOOK_CLASS_NAME);
    }

    @Override
    protected String getConfigPath() {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return Paths.get(absolutePath, "Android", "data", HookParams.DOLPHIN, "files", "overlay").toString();
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
    public View getView(Activity activity) {

        ViewGroup content = activity.findViewById(android.R.id.content);
        if (content == null) {
            return null;
        }
        ViewGroup layout1 = (ViewGroup) content.getChildAt(0);
        if (layout1 == null) {
            return null;
        }
        ViewGroup layout2 = (ViewGroup) layout1.getChildAt(0);
        if (layout2 == null) {
            return null;
        }
        ViewGroup layout3 = (ViewGroup) layout2.getChildAt(0);
        if (layout3 == null) {
            return null;
        }
        return layout3.getChildAt(1);
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
