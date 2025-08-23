package com.eagle.emulator.hook.gal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.eagle.emulator.hook.HookParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class TyranorHook {

    public static final String HOOK_CLASS_NAME = "com.akira.tyranoemu.app.TyActivity";

    public static final String ARTEMIS = "Artemis";
    public static final String KR2 = "KR2";
    public static final String TYRANO = "Tyrano";
    public static final String RMMZ = "RMMZ";
    public static final String RPG = "RPG";
    public static final String VN = "VN";
    public static final String WEB_OTHER = "WebOther";

    public static final List<String> TYPES = CollUtil.newArrayList(ARTEMIS, KR2, TYRANO, RMMZ, RPG, VN, WEB_OTHER);


    public static String getType(String suffix) {

        String type = TYPES.stream().filter(suffix::equals).findFirst().orElse("");

        if (StrUtil.isNotBlank(type)) return type;

        type = TYPES.stream().map(String::toLowerCase).filter(suffix::equals).findFirst().orElse("");

        if (StrUtil.isNotBlank(type)) return type;

        type = TYPES.stream().map(String::toUpperCase).filter(suffix::equals).findFirst().orElse("");

        if (StrUtil.isNotBlank(type)) return type;

        return "";

    }


    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        // 传递参数
        Class<?> clazz = XposedHelpers.findClass(HOOK_CLASS_NAME, lpparam.classLoader);
        XposedHelpers.findAndHookMethod(clazz, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {

                Activity activity = (Activity) param.thisObject;
                if (!activity.getClass().getName().equals(HOOK_CLASS_NAME)) return;
                Intent intent = activity.getIntent();

                // 获取路径参数
                String path = intent.getDataString();
                if (StrUtil.isBlank(path)) return;
                Log.i(HookParams.LOG_TAG, "path: " + path);

                // 通过后缀判断游戏类型
                String name = FileNameUtil.getName(path);
                String suffix = FileNameUtil.getSuffix(name);
                String type = getType(suffix);
                if (StrUtil.isBlank(type)) {
                    Toast.makeText(activity, "文件后缀不是支持的游戏类型", Toast.LENGTH_LONG).show();
                    return;
                }

                // 修正游戏路径
                String gameDirPath = path;
                if (FileUtil.isFile(path)) {
                    gameDirPath = FileUtil.getParent(path, 1);
                }

                String game = getJsonObject(gameDirPath, type).toString();
                Log.i(HookParams.LOG_TAG, "game: " + game.replace("\\/", "/"));
                if (StrUtil.isNotBlank(game)) {
                    intent.putExtra("game", game);
                }
            }

            private JSONObject getJsonObject(String path, String type) throws JSONException {
                JSONObject json = new JSONObject();
                json.put("type", type);
                if (KR2.equals(type)) {
                    json.put("path", path + "/data.xp3");
                } else {
                    json.put("path", path);
                }
                json.put("id", 0);
                json.put("timestamp", 0);
                json.put("icon", "");
                json.put("name", FileNameUtil.getName(path));
                json.put("flags", 0);
                return json;
            }
        });
    }

}
