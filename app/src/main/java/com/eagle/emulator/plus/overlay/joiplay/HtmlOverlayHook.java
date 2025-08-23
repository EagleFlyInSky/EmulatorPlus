package com.eagle.emulator.plus.overlay.joiplay;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.eagle.emulator.hook.HookParams;

import org.luckypray.dexkit.DexKitBridge;
import org.luckypray.dexkit.query.FindField;
import org.luckypray.dexkit.query.matchers.FieldMatcher;
import org.luckypray.dexkit.result.FieldData;

import java.util.Arrays;

import cn.hutool.core.util.ReflectUtil;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HtmlOverlayHook extends JoiPlayOverlayHook {

    static {
        System.loadLibrary("dexkit");
    }

    public static final String HOOK_CLASS_NAME = "cyou.joiplay.joiplay.html.HTMLActivity";

    private FieldData gameField;

    private FieldData layoutField;


    public HtmlOverlayHook(XC_LoadPackage.LoadPackageParam lpparam) {
        super(lpparam);
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
            gameField = findField(lpparam, activityClass, "cyou.joiplay.commons.models.Game", bridge);
            layoutField = findField(lpparam, activityClass, "android.widget.FrameLayout", bridge);

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
        FrameLayout frameLayout = getField(activity, layoutField);
        return frameLayout != null ? frameLayout.getChildAt(1) : null;
    }

    @Override
    public String getName(Activity activity) {
        Object game = getField(activity, gameField);
        if (game != null) {
            return (String) ReflectUtil.getFieldValue(game, "title");
        } else {
            return null;
        }
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
