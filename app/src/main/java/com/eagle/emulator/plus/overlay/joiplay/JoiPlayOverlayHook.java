package com.eagle.emulator.plus.overlay.joiplay;

import android.app.Activity;
import android.os.Environment;

import com.eagle.emulator.plus.overlay.OverlayConfig;
import com.eagle.emulator.plus.overlay.OverlayHook;

import java.nio.file.Paths;

import cn.hutool.core.util.ReflectUtil;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public abstract class JoiPlayOverlayHook extends OverlayHook {

    public JoiPlayOverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam);
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String configPath = Paths.get(sdcardPath, "JoiPlay", "overlay").toString();
        config = new OverlayConfig(configPath);
    }


    @Override
    public String getName(Activity activity) {
        Object game = ReflectUtil.getFieldValue(activity, "game");
        return (String) ReflectUtil.getFieldValue(game, "title");
    }
}
