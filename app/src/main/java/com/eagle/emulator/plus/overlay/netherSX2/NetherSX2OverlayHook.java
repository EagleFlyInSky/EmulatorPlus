package com.eagle.emulator.plus.overlay.netherSX2;

import android.app.Activity;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import com.eagle.emulator.hook.HookParams;
import com.eagle.emulator.plus.overlay.OverlayHook;

import org.luckypray.dexkit.DexKitBridge;
import org.luckypray.dexkit.result.FieldData;

import java.nio.file.Paths;

import cn.hutool.core.io.file.FileNameUtil;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class NetherSX2OverlayHook extends OverlayHook {

    public static final String HOOK_CLASS_NAME = "xyz.aethersx2.android.EmulationActivity";

    private FieldData viewField;

    public NetherSX2OverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam, HOOK_CLASS_NAME, true);
    }

    @Override
    protected String getConfigPath() {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return Paths.get(absolutePath, "Android", "data", HookParams.NETHERSX2, "files", "overlay").toString();
    }

    @Override
    protected void initField(DexKitBridge bridge) {
        viewField = findField(hookClass, "xyz.aethersx2.android.EmulationSurfaceView", bridge);
    }

    @Override
    public View getView(Activity activity) {
        return getField(activity, viewField);
    }

    @Override
    public String getName(Activity activity) {
        String bootPath = activity.getIntent().getStringExtra("bootPath");
        String path = Uri.parse(bootPath).getPath();
        return FileNameUtil.mainName(path);
    }


}
