package com.eagle.emulator;

import android.content.res.AssetManager;
import android.content.res.Resources;

import com.eagle.emulator.dex.AopAopDex;
import com.eagle.emulator.dex.JoiPlayDex;
import com.eagle.emulator.dex.NetherSX2Dex;
import com.eagle.emulator.hook.citra.CitraHook;
import com.eagle.emulator.hook.es.EsDeHook;
import com.eagle.emulator.hook.gal.TyranorHook;
import com.eagle.emulator.hook.mugen.GameDreamFactoryHook;
import com.eagle.emulator.hook.rpg.AopAopHook;
import com.eagle.emulator.hook.rpg.JoiPlayHook;
import com.eagle.emulator.hook.rpg.MaldiVesHook;
import com.eagle.emulator.hook.windows.EggGameHook;
import com.eagle.emulator.hook.windows.ExgearHook;
import com.eagle.emulator.hook.windows.MoonlightHook;
import com.eagle.emulator.hook.windows.WinlatorHook;
import com.eagle.emulator.plus.overlay.aopaop.AopAopOverlayHook;
import com.eagle.emulator.plus.overlay.azahar.AzaharOverlayHook;
import com.eagle.emulator.plus.overlay.cemu.CemuOverlayHook;
import com.eagle.emulator.plus.overlay.citron.CitronOverlayHook;
import com.eagle.emulator.plus.overlay.dolphin.DolphinOverlayHook;
import com.eagle.emulator.plus.overlay.eden.EdenOverlayHook;
import com.eagle.emulator.plus.overlay.exagear.ExagearOverlayHook;
import com.eagle.emulator.plus.overlay.joiplay.HtmlOverlayHook;
import com.eagle.emulator.plus.overlay.joiplay.RpgOverlayHook;
import com.eagle.emulator.plus.overlay.joiplay.RuffleOverlayHook;
import com.eagle.emulator.plus.overlay.m64.M64OverlayHook;
import com.eagle.emulator.plus.overlay.netherSX2.NetherSX2OverlayHook;
import com.eagle.emulator.plus.overlay.ppsspp.PpssppOverlayHook;
import com.eagle.emulator.plus.overlay.saturn.SaturnEmuOverlayHook;
import com.eagle.emulator.plus.overlay.winlator.WinlatorOverlayHook;

import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookZygoteInit, IXposedHookLoadPackage {

    private static Resources MODULE_RESOURCES = null;

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {

        // 获取模块的Resources
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            XposedHelpers.callMethod(assetManager, "addAssetPath", startupParam.modulePath);
            Resources superRes = Resources.getSystem();
            MODULE_RESOURCES = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        } catch (Exception e) {
            XposedBridge.log("无法获取模块资源");
            XposedBridge.log(e);
        }
    }

    public static Resources getModuleResources() {
        return MODULE_RESOURCES;
    }


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {

        try {
            handle(lpparam);
        } catch (Exception e) {
            XposedBridge.log(e);
        }

    }

    private static void handle(XC_LoadPackage.LoadPackageParam lpparam) {
        String packageName = lpparam.packageName;

        if (StrUtil.isBlank(packageName)) return;

        XposedBridge.log(StrUtil.format("启动 EmulatorPlus : {}", packageName));

        switch (packageName) {
            // 跳过系统项
            case HookParams.WEB_VIEW:
                break;
            //前端
            case HookParams.ES_DE:
                EsDeHook.hook(lpparam);
                break;
            //模拟器
            case HookParams.CITRA_MMJ:
                CitraHook.hook(lpparam);
                break;
            case HookParams.CEMU:
                new CemuOverlayHook(lpparam).hook();
                break;
            case HookParams.AZAHAR:
                new AzaharOverlayHook(lpparam).hook();
                break;
            case HookParams.NETHERSX2:
                NetherSX2Dex.init(lpparam);
                new NetherSX2OverlayHook(lpparam).hook();
                break;
            case HookParams.DOLPHIN:
                new DolphinOverlayHook(lpparam).hook();
                break;
            case HookParams.M64:
                new M64OverlayHook(lpparam).hook();
                break;
            case HookParams.SATURN_EMU:
                new SaturnEmuOverlayHook(lpparam).hook();
                break;
            case HookParams.AOPAOP:
                AopAopDex.init(lpparam);
                AopAopHook.hook(lpparam);
                new AopAopOverlayHook(lpparam).hook();
                break;
            case HookParams.MALDIVES:
                MaldiVesHook.hook(lpparam);
                break;
            case HookParams.JOIPLAY:
                JoiPlayDex.init(lpparam);
                JoiPlayHook.hook(lpparam);
                new HtmlOverlayHook(lpparam).hook();
                break;
            case HookParams.JOIPLAY_RUFFLE:
                new RuffleOverlayHook(lpparam).hook();
                break;
            case HookParams.JOIPLAY_RPGMAKER:
                new RpgOverlayHook(lpparam).hook();
                break;
            case HookParams.TYRANOR:
                TyranorHook.hook(lpparam);
                break;
            case HookParams.MOONLIGHT:
            case HookParams.MOONLIGHT_DEBUG:
                MoonlightHook.hook(lpparam);
                break;
            case HookParams.GAME_DREAM_FACTORY:
                GameDreamFactoryHook.hook(lpparam);
                break;
            case HookParams.EXAGEAR:
                ExgearHook.hook(lpparam);
                new ExagearOverlayHook(lpparam).hook();
                break;
            case HookParams.WINLATOR:
                WinlatorHook.hook(lpparam);
                new WinlatorOverlayHook(lpparam).hook();
                break;
            case HookParams.EGG_GAME:
                EggGameHook.hook(lpparam);
                break;
            case HookParams.PPSSPP:
                new PpssppOverlayHook(lpparam).hook();
                break;
            case HookParams.CITRON:
                new CitronOverlayHook(lpparam).hook();
                break;
            case HookParams.EDEN:
                new EdenOverlayHook(lpparam).hook();
                break;
            default:
                if (CitraHook.hasClass(lpparam)) {
                    CitraHook.hook(lpparam);
                }
        }
    }


}
