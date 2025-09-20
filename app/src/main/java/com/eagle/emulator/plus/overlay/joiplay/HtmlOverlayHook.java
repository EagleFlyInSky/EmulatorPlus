package com.eagle.emulator.plus.overlay.joiplay;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import com.eagle.emulator.dex.JoiPlayDex;
import com.eagle.emulator.hook.tools.ViewFind;
import com.eagle.emulator.plus.overlay.ViewInfo;

import org.luckypray.dexkit.result.FieldData;

import cn.hutool.core.util.ReflectUtil;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HtmlOverlayHook extends JoiPlayOverlayHook {


    public static final String HOOK_CLASS_NAME = "cyou.joiplay.joiplay.html.HTMLActivity";

    private FieldData gameField;

    private FieldData layoutField;


    public HtmlOverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam, HOOK_CLASS_NAME);
    }

    @Override
    protected ViewInfo getViewInfo(Activity activity) {
        FrameLayout frameLayout = getField(activity, JoiPlayDex.layoutField);
        View overlayView = ViewFind.findViewByIndex(frameLayout, 1);
        View gameView = ViewFind.findViewByIndex(frameLayout, 0);
        return ViewInfo.builder().overlayView(overlayView).gameView(gameView).build();
    }

    @Override
    public String getName(Activity activity) {
        Object game = getField(activity, JoiPlayDex.gameField);
        if (game != null) {
            return (String) ReflectUtil.getFieldValue(game, "title");
        } else {
            return null;
        }
    }


}
