package com.eagle.emulator.dex;

import com.eagle.emulator.plus.overlay.netherSX2.NetherSX2OverlayHook;
import com.eagle.emulator.util.DexKitUtil;

import org.luckypray.dexkit.result.FieldData;

import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class NetherSX2Dex {

    public static FieldData viewField;


    public static void init(XC_LoadPackage.LoadPackageParam lpparam) {

        DexKitUtil.addFind(bridge -> viewField = DexKitUtil.findFieldSingle(NetherSX2OverlayHook.HOOK_CLASS_NAME, "xyz.aethersx2.android.EmulationSurfaceView", bridge, lpparam));

        DexKitUtil.runFind(lpparam);

        XposedBridge.log(StrUtil.format("viewField：{}", viewField));
    }
}
