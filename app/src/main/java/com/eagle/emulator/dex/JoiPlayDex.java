package com.eagle.emulator.dex;

import com.eagle.emulator.util.DexKitUtil;

import org.luckypray.dexkit.query.FindMethod;
import org.luckypray.dexkit.query.matchers.MethodMatcher;
import org.luckypray.dexkit.result.FieldData;
import org.luckypray.dexkit.result.MethodData;

import java.lang.reflect.Modifier;

import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class JoiPlayDex {


    public static FieldData gameField;

    public static FieldData layoutField;

    public static MethodData methodData;


    public static void init(XC_LoadPackage.LoadPackageParam lpparam) {

        String declaredClassName = "cyou.joiplay.joiplay.html.HTMLActivity";
        DexKitUtil.addFind(dexKitBridge -> layoutField = DexKitUtil.findFieldSingle(declaredClassName, "android.widget.FrameLayout", dexKitBridge, lpparam));
        DexKitUtil.addFind(dexKitBridge -> gameField = DexKitUtil.findFieldSingle(declaredClassName, "cyou.joiplay.commons.models.Game", dexKitBridge, lpparam));
        DexKitUtil.addFind(dexKitBridge -> {
            MethodMatcher methodMatcher = MethodMatcher.create().modifiers(Modifier.PUBLIC | Modifier.STATIC).paramCount(1).paramTypes(String.class).usingEqStrings("title", "[^A-Za-z0-9]").usingNumbers(32).returnType(String.class);
            methodData = dexKitBridge.findMethod(FindMethod.create().searchPackages("cyou.joiplay.joiplay.utilities").matcher(methodMatcher)).single();
        });

        DexKitUtil.runFind(lpparam);

        XposedBridge.log(StrUtil.format("gameField：{}", gameField));
        XposedBridge.log(StrUtil.format("layoutField：{}", layoutField));
        XposedBridge.log(StrUtil.format("methodData：{}", methodData));
    }


}
