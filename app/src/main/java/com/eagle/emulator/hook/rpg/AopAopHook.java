package com.eagle.emulator.hook.rpg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.eagle.emulator.dex.AopAopDex;

import java.io.Serializable;
import java.util.List;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import lombok.SneakyThrows;

public class AopAopHook {

    public static final String CLASS_NAME = "com.aopaop.app.module.game.local.GamePlayerBridgeActivity";
    public static final String HOOK_CLASS_NAME_M = "com.aopaop.app.module.common.MainActivity";
    public static final String HOOK_CLASS_NAME_S = "com.aopaop.app.module.common.SplashActivity";

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {

        Class<?> splashActivityClass = XposedHelpers.findClass("android.app.Activity", lpparam.classLoader);

        XposedHelpers.findAndHookMethod(splashActivityClass, "startActivity", Intent.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }

            @SuppressLint("SdCardPath")
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Activity activity = (Activity) param.thisObject;
                // 确认是对象类型
                if (!activity.getClass().getName().equals(HOOK_CLASS_NAME_S)) {
                    return;
                }

                Intent intent = (Intent) param.args[0];

                // 确认跳转 activity
                ComponentName component = intent.getComponent();
                if (component == null) {
                    return;
                }
                String className = intent.getComponent().getClassName();
                if (!className.equals(HOOK_CLASS_NAME_M)) {
                    return;
                }

                // 获取启动路径
                String gamePath = activity.getIntent().getDataString();
                if (StrUtil.isBlank(gamePath)) {
                    return;
                }

                // 路径适配
                if (gamePath.startsWith("/storage/emulated/0/")) {
                    gamePath = gamePath.replace("/storage/emulated/0/", "/sdcard/");
                }

                XposedBridge.log(StrUtil.format("启动路径：{}", gamePath));

                // 检查文件是否存在
                if (!FileUtil.exist(gamePath)) {
                    Toast.makeText(activity, gamePath + " 文件不存在", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 文件夹格式路径修正
                if (FileUtil.getSuffix(gamePath).equals("aopaop")) {
                    gamePath = FileUtil.getParent(gamePath, 1);
                }

                // 获取本地游戏列表
                List<?> list = getList();

                // 匹配游戏
                Object game = null;
                for (Object o : list) {
                    String path = ReflectUtil.getFieldValue(o, "gamePath").toString();
                    if (path.equals(gamePath)) {
                        game = o;
                    }
                }
                if (game == null) {
                    XposedBridge.log(StrUtil.format("{}：未匹配到游戏", gamePath));
                    return;
                }
                XposedBridge.log(game.toString());

                // 将解析得到的值设置到Intent中
                Bundle bundle = new Bundle();
                bundle.putSerializable("extra_local_game_bean", (Serializable) game);
                intent.putExtras(bundle);

                // 替换指向的类
                intent.setClassName(activity, CLASS_NAME);

                // 清除调用状态
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);


            }

            /**
             * 获取游戏列表
             * @return 游戏列表
             */
            private List<?> getList() {
                Object boxStore = findStore();

                Class<?> gameEntityClass = XposedHelpers.findClass("com.aopaop.app.entity.game.LocalGameEntity", lpparam.classLoader);
                Object box = ReflectUtil.invoke(boxStore, "boxFor", gameEntityClass);
                Object queryResult = ReflectUtil.invoke(box, "query");
                Object buildResult = ReflectUtil.invoke(queryResult, "build");
                Object listObject = ReflectUtil.invoke(buildResult, "find");
                return Convert.toList(listObject);
            }

            @SneakyThrows
            private Object findStore() {
                return ReflectUtil.getStaticFieldValue(AopAopDex.boxStoreFieldData.getFieldInstance(lpparam.classLoader));
            }


        });


    }


}
