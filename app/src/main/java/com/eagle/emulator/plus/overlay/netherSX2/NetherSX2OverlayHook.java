package com.eagle.emulator.plus.overlay.netherSX2;

import android.app.Activity;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import com.eagle.emulator.HookParams;
import com.eagle.emulator.dex.NetherSX2Dex;
import com.eagle.emulator.plus.overlay.OverlayHook;
import com.eagle.emulator.plus.overlay.ViewInfo;

import java.nio.file.Paths;

import cn.hutool.core.io.file.FileNameUtil;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class NetherSX2OverlayHook extends OverlayHook {

    public static final String HOOK_CLASS_NAME = "xyz.aethersx2.android.EmulationActivity";

    public NetherSX2OverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam, HOOK_CLASS_NAME);
    }

    @Override
    protected String getConfigPath() {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return Paths.get(absolutePath, "Android", "data", HookParams.NETHERSX2, "files", "overlay").toString();
    }

    @Override
    protected ViewInfo getViewInfo(Activity activity) {
        ViewInfo viewInfo = new ViewInfo();
        View view = getField(activity, NetherSX2Dex.viewField);
        viewInfo.setGameView(view);
        viewInfo.setAddImageView(true);
        return viewInfo;
    }

    @Override
    public String getName(Activity activity) {
        String bootPath = activity.getIntent().getStringExtra("bootPath");
        String path = Uri.parse(bootPath).getPath();
        return FileNameUtil.mainName(path);
    }


}
