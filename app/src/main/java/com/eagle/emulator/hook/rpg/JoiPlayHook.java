package com.eagle.emulator.hook.rpg;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.eagle.emulator.hook.HookParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class JoiPlayHook {

    public static final String CLASS_NAME = "cyou.joiplay.joiplay.activities.ShortcutActivity";
    public static final String HOOK_CLASS_NAME = "cyou.joiplay.joiplay.activities.SplashActivity";


    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        hookId(lpparam);
        hookStart(lpparam);
    }

    /**
     * hookId 生成
     * 支持中文
     *
     * @param lpparam 参数
     */
    private static void hookId(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("cyou.joiplay.joiplay.utilities.t", lpparam.classLoader, "g", "java.lang.String", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Object title = param.args[0];
                param.setResult(title.toString());
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });

    }

    private static void hookStart(XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> splashActivityClass = XposedHelpers.findClass("android.app.Activity", lpparam.classLoader);

        XposedHelpers.findAndHookMethod(splashActivityClass, "startActivity", Intent.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                hook(param);
            }
        });
    }

    private static void hook(XC_MethodHook.MethodHookParam param) throws JSONException {
        Log.i(HookParams.LOG_TAG, "Hook开始");
        // 获取执行对象
        Activity activity = (Activity) param.thisObject;
        // 确认是对象类型
        String name = activity.getClass().getName();
        if (!name.equals(HOOK_CLASS_NAME)) {
            return;
        }
        // 获取 data 数据
        Intent intent = (Intent) param.args[0];
        String title = activity.getIntent().getStringExtra("title");
        if (StrUtil.isBlank(title)) {
            return;
        }

        String gameId = findId(activity, title);
        if (StrUtil.isBlank(gameId)) return;

        // 将解析得到的值设置到Intent中
        intent.putExtra("id", gameId);
        // 替换指向的类
        intent.setClassName(activity, CLASS_NAME);
        // 清除调用状态
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

    }

    private static String findId(Activity activity, String title) throws JSONException {
        String jsonString = readJson(activity);
        if (StrUtil.isBlank(jsonString)) {
            return null;
        }

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONObject map = jsonObject.getJSONObject("map");
        List<JSONObject> list = new ArrayList<>();
        for (Iterator<String> it = map.keys(); it.hasNext(); ) {
            String key = it.next();
            JSONObject value = map.getJSONObject(key);
            list.add(value);
        }

        String findId = "";
        for (JSONObject object : list) {
            Log.i(HookParams.LOG_TAG, "游戏:" + object.toString());
            String gameId = object.getString("id");
            String gameTitle = object.getString("title");
            if (StrUtil.isNotBlank(gameTitle) && gameTitle.equals(title)) {
                findId = gameId;
                break;
            }
        }
        return findId;
    }

    private static String readJson(Activity activity) {
        // 获取应用的内部存储目录
        File internalStorageDir = activity.getFilesDir();
        // 构建文件路径
        File file = new File(internalStorageDir, "configuration/games.json");
        return FileUtil.readString(file, StandardCharsets.UTF_8);
    }


}
