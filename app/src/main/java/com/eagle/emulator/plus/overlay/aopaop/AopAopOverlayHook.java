package com.eagle.emulator.plus.overlay.aopaop;

import android.app.Activity;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;

import com.eagle.emulator.hook.HookParams;
import com.eagle.emulator.plus.overlay.OverlayHook;

import org.luckypray.dexkit.DexKitBridge;
import org.luckypray.dexkit.result.FieldData;

import java.nio.file.Paths;

import cn.hutool.core.util.ReflectUtil;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AopAopOverlayHook extends OverlayHook {

    public static final String HOOK_CLASS_NAME = "com.aopaop.app.module.game.local.GamePlayerWebViewActivity";

    public FieldData gameField;

    public AopAopOverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam, HOOK_CLASS_NAME, true);
    }

    @Override
    protected String getConfigPath() {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return Paths.get(absolutePath, "Android", "data", HookParams.AOPAOP, "files", "overlay").toString();
    }

    @Override
    protected void initField(DexKitBridge bridge) {
        gameField = findFieldFirst(hookClass, "com.aopaop.app.entity.game.LocalGameEntity", bridge);
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
        return layout2.getChildAt(6);
    }

    @Override
    public String getName(Activity activity) {
        Object game = getField(activity, gameField);
        return (String) ReflectUtil.getFieldValue(game, "gameName");
    }
}
