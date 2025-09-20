package com.eagle.emulator.plus.overlay.dolphin;

import android.app.Activity;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;

import com.eagle.emulator.HookParams;
import com.eagle.emulator.hook.tools.ViewFind;
import com.eagle.emulator.plus.overlay.OverlayHook;
import com.eagle.emulator.plus.overlay.ViewInfo;
import com.eagle.emulator.util.HookUtil;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Consumer;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.URLUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
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
        ViewGroup content = activity.findViewById(android.R.id.content);
        ViewGroup viewGroup = ViewFind.findViewGroupByIndex(content, 0, 0, 0);
        if (viewGroup == null) {
            return null;
        }
        View overlayView = viewGroup.getChildAt(1);
        View gameView = viewGroup.getChildAt(0);

        return ViewInfo.builder().overlayView(overlayView).gameView(gameView).build();
    }


    @Override
    public String getName(Activity activity) {
        String[] selectedGames = activity.getIntent().getStringArrayExtra("SelectedGames");
        XposedBridge.log("SelectedGames" + Arrays.toString(selectedGames));
        if (selectedGames != null) {
            String selectedGame = selectedGames[0];
            return FileNameUtil.mainName(URLUtil.decode(selectedGame));
        } else {
            return null;
        }
    }

    @Override
    public void hookPlus() {
        HookUtil.hookToastShowByPrefix("短按返回键进入菜单。");
    }
}
