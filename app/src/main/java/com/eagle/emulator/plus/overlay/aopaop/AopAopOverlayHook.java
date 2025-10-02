package com.eagle.emulator.plus.overlay.aopaop;

import android.app.Activity;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;

import com.eagle.emulator.HookParams;
import com.eagle.emulator.dex.AopAopDex;
import com.eagle.emulator.hook.tools.ViewFind;
import com.eagle.emulator.plus.overlay.OverlayHook;
import com.eagle.emulator.plus.overlay.ViewInfo;
import com.eagle.emulator.util.DexKitUtil;

import java.nio.file.Paths;

import cn.hutool.core.util.ReflectUtil;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AopAopOverlayHook extends OverlayHook {

    public static final String HOOK_CLASS_NAME = "com.aopaop.app.module.game.local.GamePlayerWebViewActivity";

    public AopAopOverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam, HOOK_CLASS_NAME);
    }

    @Override
    protected String getConfigPath() {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return Paths.get(absolutePath, "Android", "data", HookParams.AOPAOP, "files", "overlay").toString();
    }

    @Override
    protected ViewInfo getViewInfo(Activity activity) {
        ViewGroup content = activity.findViewById(android.R.id.content);
        ViewGroup viewGroup = ViewFind.findViewGroupByIndex(content, 0, 0);
        if (viewGroup == null) {
            return null;
        }
        View overlayView = viewGroup.getChildAt(6);
        View gameView = viewGroup.getChildAt(0);

        return ViewInfo.builder().overlayView(overlayView).gameView(gameView).build();
    }

    @Override
    public String getName(Activity activity) {
        Object game = DexKitUtil.getField(activity, AopAopDex.gameFieldData);
        return (String) ReflectUtil.getFieldValue(game, "gameName");
    }
}
