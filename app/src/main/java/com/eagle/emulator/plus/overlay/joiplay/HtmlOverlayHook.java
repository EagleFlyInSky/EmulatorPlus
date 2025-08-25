package com.eagle.emulator.plus.overlay.joiplay;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import org.luckypray.dexkit.DexKitBridge;
import org.luckypray.dexkit.result.FieldData;

import cn.hutool.core.util.ReflectUtil;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HtmlOverlayHook extends JoiPlayOverlayHook {


    public static final String HOOK_CLASS_NAME = "cyou.joiplay.joiplay.html.HTMLActivity";

    private FieldData gameField;

    private FieldData layoutField;


    public HtmlOverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam, HOOK_CLASS_NAME, true);
    }

    @Override
    protected void initField(DexKitBridge bridge) {
        gameField = findField(hookClass, "cyou.joiplay.commons.models.Game", bridge);
        layoutField = findField(hookClass, "android.widget.FrameLayout", bridge);
    }

    @Override
    public View getView(Activity activity) {
        FrameLayout frameLayout = getField(activity, layoutField);
        return frameLayout != null ? frameLayout.getChildAt(1) : null;
    }

    @Override
    public String getName(Activity activity) {
        Object game = getField(activity, gameField);
        if (game != null) {
            return (String) ReflectUtil.getFieldValue(game, "title");
        } else {
            return null;
        }
    }


}
