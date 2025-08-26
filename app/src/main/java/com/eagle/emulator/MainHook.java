package com.eagle.emulator;

import android.util.Log;

import com.eagle.emulator.hook.HookParams;
import com.eagle.emulator.hook.beacon.BeaconHook;
import com.eagle.emulator.hook.es.EsDeHook;
import com.eagle.emulator.hook.gal.TyranorHook;
import com.eagle.emulator.hook.mugen.GameDreamFactoryHook;
import com.eagle.emulator.hook.rpg.AopAopHook;
import com.eagle.emulator.hook.rpg.JoiPlayHook;
import com.eagle.emulator.hook.rpg.JoiPlayPlugHook;
import com.eagle.emulator.hook.rpg.MaldiVesHook;
import com.eagle.emulator.hook.windows.ExgearHook;
import com.eagle.emulator.hook.windows.MoonlightHook;
import com.eagle.emulator.hook.windows.WinlatorHook;
import com.eagle.emulator.plus.overlay.aopaop.AopAopOverlayHook;
import com.eagle.emulator.plus.overlay.azahar.AzaharOverlayHook;
import com.eagle.emulator.plus.overlay.dolphin.DolphinOverlayHook;
import com.eagle.emulator.plus.overlay.exagear.ExagearOverlayHook;
import com.eagle.emulator.plus.overlay.joiplay.HtmlOverlayHook;
import com.eagle.emulator.plus.overlay.joiplay.RpgOverlayHook;
import com.eagle.emulator.plus.overlay.joiplay.RuffleOverlayHook;
import com.eagle.emulator.plus.overlay.netherSX2.NetherSX2OverlayHook;
import com.eagle.emulator.plus.overlay.winlator.WinlatorOverlayHook;

import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {

        try {
            handle(lpparam);
        } catch (Exception e) {
            Log.e(HookParams.LOG_TAG, "代码异常", e);
        }

    }

    private static void handle(XC_LoadPackage.LoadPackageParam lpparam) {
        String packageName = lpparam.packageName;

        if (StrUtil.isBlank(packageName)) return;

        Log.d(HookParams.LOG_TAG, "启动 EmulatorPlus : " + packageName);

        switch (packageName) {
            case HookParams.ES_DE:
                EsDeHook.hook(lpparam);
                break;
            case HookParams.BEACON:
                BeaconHook.hook(lpparam);
                break;
            case HookParams.AZAHAR:
                new AzaharOverlayHook(lpparam).hook();
                break;
            case HookParams.NETHERSX2:
                new NetherSX2OverlayHook(lpparam).hook();
                break;
            case HookParams.DOLPHIN:
                new DolphinOverlayHook(lpparam).hook();
                break;
            case HookParams.AOPAOP:
                AopAopHook.hook(lpparam);
                new AopAopOverlayHook(lpparam).hook();
                break;
            case HookParams.MALDIVES:
                MaldiVesHook.hook(lpparam);
                break;
            case HookParams.JOIPLAY:
                JoiPlayHook.hook(lpparam);
                new HtmlOverlayHook(lpparam).hook();
                break;
            case HookParams.WEB_VIEW:
                break;
            case HookParams.JOIPLAY_RUFFLE:
                JoiPlayPlugHook.hookGamePad(lpparam);
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
                // 前端启动
                WinlatorHook.hook(lpparam);
                // 遮罩
                new WinlatorOverlayHook(lpparam).hook();
                break;
            default:
                // winlator hook
                if (WinlatorHook.hasClass(lpparam)) {
                    WinlatorHook.hook(lpparam);
                    break;
                }
        }
    }
}
