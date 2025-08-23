package com.eagle.emulator.plus.overlay.joiplay;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class RpgOverlayHook extends JoiPlayOverlayHook {

    public static final String HOOK_CLASS_NAME = "cyou.joiplay.runtime.rpgmaker.MainActivity";

    public RpgOverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam);
        hookClassName = HOOK_CLASS_NAME;
    }

    @Override
    public View getView(Activity activity) {
        FrameLayout frameLayout = activity.findViewById(android.R.id.content);
        RelativeLayout relativeLayout = (RelativeLayout) frameLayout.getChildAt(0);
        return relativeLayout.getChildAt(0);
    }
}
