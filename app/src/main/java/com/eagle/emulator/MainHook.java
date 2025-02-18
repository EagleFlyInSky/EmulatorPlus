package com.eagle.emulator;

import android.util.Log;

import com.eagle.emulator.hook.es.EsDeHook;
import com.eagle.emulator.hook.HookParams;
import com.eagle.emulator.hook.gal.TyranorHook;
import com.eagle.emulator.hook.rpg.AopAopHook;
import com.eagle.emulator.hook.rpg.JoiPlayHook;
import com.eagle.emulator.hook.rpg.MaldiVesHook;
import com.eagle.emulator.hook.windows.MoonlightHook;
import com.eagle.emulator.hook.windows.WinlatorHook;

import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        String packageName = lpparam.packageName;

        if (StrUtil.isBlank(packageName)) return;

        Log.d(HookParams.LOG_TAG, "启动 EmulatorPlus : " + packageName);

        switch (packageName) {
            case HookParams.ES_DE:
                EsDeHook.hook(lpparam);
                break;
            case HookParams.AOPAOP:
                AopAopHook.hook(lpparam);
                break;
            case HookParams.MALDIVES:
                MaldiVesHook.hook(lpparam);
                break;
            case HookParams.JOIPLAY:
                JoiPlayHook.hook(lpparam);
                break;
            case HookParams.TYRANOR:
                TyranorHook.hook(lpparam);
                break;
            case HookParams.MOONLIGHT:
            case HookParams.MOONLIGHT_DEBUG:
                MoonlightHook.hook(lpparam);
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
