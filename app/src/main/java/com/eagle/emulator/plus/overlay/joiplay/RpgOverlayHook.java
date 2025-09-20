package com.eagle.emulator.plus.overlay.joiplay;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.eagle.emulator.hook.tools.ViewFind;
import com.eagle.emulator.plus.overlay.ViewInfo;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class RpgOverlayHook extends JoiPlayOverlayHook {

    public static final String HOOK_CLASS_NAME = "cyou.joiplay.runtime.rpgmaker.MainActivity";

    public RpgOverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam, HOOK_CLASS_NAME);
    }


    @Override
    protected ViewInfo getViewInfo(Activity activity) {
        FrameLayout content = activity.findViewById(android.R.id.content);
        ViewGroup viewGroup = ViewFind.findViewGroupByIndex(content, 0);
        View overlayView = ViewFind.findViewByIndex(viewGroup, 1);
        View gameView = ViewFind.findViewByIndex(viewGroup, 0);
        return ViewInfo.builder().overlayView(overlayView).gameView(gameView).build();
    }

    @Override
    public View getView(Activity activity) {
        FrameLayout frameLayout = activity.findViewById(android.R.id.content);
        RelativeLayout relativeLayout = (RelativeLayout) frameLayout.getChildAt(0);
        return relativeLayout.getChildAt(0);
    }
}
