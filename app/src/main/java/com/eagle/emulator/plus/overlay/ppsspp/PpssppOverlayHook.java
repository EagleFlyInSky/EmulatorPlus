package com.eagle.emulator.plus.overlay.ppsspp;

import android.app.Activity;
import android.os.Environment;
import android.view.View;

import com.eagle.emulator.plus.overlay.OverlayHook;

import java.nio.file.Paths;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PpssppOverlayHook extends OverlayHook {

    public static final String HOOK_CLASS_NAME = "org.dolphinemu.dolphinemu.activities.EmulationActivity";

    public PpssppOverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam, HOOK_CLASS_NAME);
    }

    @Override
    protected String getConfigPath() {
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return Paths.get(sdcardPath, "PSP", "overlay").toString();
    }

    @Override
    protected View getView(Activity activity) {
        return null;
    }

    @Override
    protected String getName(Activity activity) {
        return "";
    }
}
