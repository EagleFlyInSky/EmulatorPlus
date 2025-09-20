package com.eagle.emulator.plus.overlay.ppsspp;

import android.app.Activity;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;

import com.eagle.emulator.plus.overlay.BaseOverlayConfig;
import com.eagle.emulator.plus.overlay.OverlayHook;
import com.eagle.emulator.plus.overlay.ViewInfo;

import java.nio.file.Paths;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PpssppOverlayHook extends OverlayHook {

    public static final String HOOK_CLASS_NAME = "org.ppsspp.ppsspp.PpssppActivity";

    public PpssppOverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam, HOOK_CLASS_NAME);
    }

    @Override
    protected String getConfigPath() {
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return Paths.get(sdcardPath, "PSP", "overlay").toString();
    }

    protected void initConfig() {
        String configPath = getConfigPath();
        if (StrUtil.isNotBlank(configPath)) {
            config = new BaseOverlayConfig(configPath, false);
        }
    }

    @Override
    protected ViewInfo getViewInfo(Activity activity) {
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View view = viewGroup.getChildAt(0);
        return ViewInfo.builder().gameView(view).parentView(viewGroup).addImageView(true).build();
    }

    @Override
    protected String getName(Activity activity) {
        String dataString = activity.getIntent().getDataString();
        String decode = URLUtil.decode(dataString);
        return FileNameUtil.mainName(decode);
    }


    @Override
    public void hookPlus() {
        hookGameStart();
    }


    private void hookGameStart() {
        XposedHelpers.findAndHookMethod(hookClassName, lpparam.classLoader, "openContentUri", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                String str1 = (String) param.args[0];
                String decode = URLUtil.decode(str1);
                String str2 = (String) param.args[1];
                XposedBridge.log(StrUtil.format("str1:{}", decode));
            }
        });

//        XposedHelpers.findAndHookMethod(hookClassName, lpparam.classLoader, "contentUriGetFileInfo", String.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) {
//                Activity activity = (Activity) param.thisObject;
//                String str1 = (String) param.args[0];
//                String decode = URLUtil.decode(str1);
//                XposedBridge.log(StrUtil.format("str:{}", decode));
//            }
//        });
    }
}
