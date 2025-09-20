package com.eagle.emulator.hook.rpg;

import android.os.Environment;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class JoiPlayPlugHook {

    /**
     * hookGamePad
     * 修改按键配置文件位置
     *
     * @param lpparam 参数
     */
    public static void hookGamePad(XC_LoadPackage.LoadPackageParam lpparam) {

        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();

        String className = String.class.getName();
        XposedHelpers.findAndHookMethod(className, lpparam.classLoader, "startsWith", className, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                //XposedBridge.log( "Hook按键配置");
                String prefix = (String) param.args[0];
                if (absolutePath.equals(prefix)) {
                    //XposedBridge.log( "配置文件:" + thisObject);
                    param.setResult(true);
                }
            }

        });
    }

}
