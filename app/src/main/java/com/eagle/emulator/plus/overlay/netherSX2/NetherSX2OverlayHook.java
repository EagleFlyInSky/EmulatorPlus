package com.eagle.emulator.plus.overlay.netherSX2;

import android.app.Activity;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.eagle.emulator.hook.HookParams;
import com.eagle.emulator.plus.overlay.OverlayConfig;
import com.eagle.emulator.plus.overlay.OverlayHook;

import org.luckypray.dexkit.DexKitBridge;
import org.luckypray.dexkit.query.FindField;
import org.luckypray.dexkit.query.matchers.FieldMatcher;
import org.luckypray.dexkit.result.FieldData;

import java.nio.file.Paths;
import java.util.Arrays;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class NetherSX2OverlayHook extends OverlayHook {

    static {
        System.loadLibrary("dexkit");
    }

    public static final String HOOK_CLASS_NAME = "xyz.aethersx2.android.EmulationActivity";

    private FieldData viewField;

    public NetherSX2OverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam);
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String configPath = Paths.get(absolutePath, "Android", "data", HookParams.NETHERSX2, "files", "overlay").toString();
        if (StrUtil.isNotBlank(configPath)) {
            config = new OverlayConfig(configPath);
        }

        hookClassName = HOOK_CLASS_NAME;
        initFindField(lpparam);
    }

    /**
     * 使用 dexkit 定位属性 突破混淆
     */
    private void initFindField(XC_LoadPackage.LoadPackageParam lpparam) {
        String apkPath = lpparam.appInfo.sourceDir;
        try (DexKitBridge bridge = DexKitBridge.create(apkPath)) {

            Class<?> activityClass = XposedHelpers.findClass(HOOK_CLASS_NAME, lpparam.classLoader);
            viewField = findField(lpparam, activityClass, "xyz.aethersx2.android.EmulationSurfaceView", bridge);

        } catch (Throwable e) {
            Log.e(HookParams.LOG_TAG, e.toString());
            Log.e(HookParams.LOG_TAG, Arrays.toString(e.getStackTrace()));
        }
    }

    private FieldData findField(XC_LoadPackage.LoadPackageParam lpparam, Class<?> activityClass, String className, DexKitBridge bridge) {
        Class<?> fieldClass = XposedHelpers.findClass(className, lpparam.classLoader);
        FieldMatcher fieldMatcher = FieldMatcher.create().type(fieldClass).declaredClass(activityClass);
        FindField matcher = FindField.create().matcher(fieldMatcher);
        return bridge.findField(matcher).single();
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


    @SuppressWarnings("unchecked")
    private <T> T getField(Activity activity, FieldData fieldData) {
        try {
            return (T) ReflectUtil.getFieldValue(activity, fieldData.getFieldInstance(lpparam.classLoader));
        } catch (NoSuchFieldException e) {
            Log.e(HookParams.LOG_TAG, e.toString());
            Log.e(HookParams.LOG_TAG, Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

}
