package com.eagle.emulator.hook.rpg;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.util.Objects;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MaldiVesHook {

    public static final String CLASS_NAME = "net.miririt.maldives.RMMVActivity";
    public static final String HOOK_CLASS_NAME = "net.miririt.maldives.MainActivity";

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {

        Class<?> clazz = XposedHelpers.findClass(HOOK_CLASS_NAME, lpparam.classLoader);
        XposedHelpers.findAndHookMethod(clazz, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                // 获取执行对象
                Activity activity = (Activity) param.thisObject;

                // 获取 data 数据
                Uri uri = activity.getIntent().getData();
                if (uri == null) {
                    return;
                }

                // 将解析得到的值设置到Intent中
                Intent intent = new Intent();
                // 设置启动 Activity
                intent.setClassName(activity, CLASS_NAME);
                // 修正uri地址并赋值
                intent.putExtra("gameDirUri", fixUri(uri));

                // 获取游戏名
                String gameTitle = intent.getStringExtra("gameTitle");
                if (gameTitle == null) {
                    // 截取
                    gameTitle = getGameTitle(uri);
                }
                intent.putExtra("gameTitle", gameTitle);

                // 清除调用状态
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                // 启动 RMMVActivity
                activity.startActivity(intent);

            }
        });
    }

    public static String getGameTitle(Uri uri) {

        String filePath = uri.getLastPathSegment();

        // 找到最后一个斜杠的位置
        int lastSlashIndex = 0;
        if (filePath != null) {
            lastSlashIndex = filePath.lastIndexOf('/');
        }

        // 如果没有斜杠，则从最后一个反斜杠开始查找
        if (lastSlashIndex == -1) {
            lastSlashIndex = filePath.lastIndexOf('\\');
        }

        // 获取文件名
        String fileName = null;
        if (filePath != null) {
            fileName = filePath.substring(lastSlashIndex + 1);
        }

        // 找到最后一个点的位置
        int lastDotIndex = 0;
        if (fileName != null) {
            lastDotIndex = fileName.lastIndexOf('.');
        }

        // 如果没有点，则返回整个文件名
        if (lastDotIndex == -1) {
            lastDotIndex = fileName.length();
        }

        // 获取不带后缀的文件名
        return Objects.requireNonNull(fileName).substring(0, lastDotIndex);
    }

    public static Uri fixUri(Uri uri) {
        String uriString = uri.toString();
        // 提取 document 后面的路径部分
        String afterDocument = uriString.substring(uriString.indexOf("document/") + "document/".length());

        // 将 extractedPath 插入到 tree 和 document 之间
        int indexOfTree = uriString.indexOf("tree/");
        String newUri = uriString.substring(0, indexOfTree + 5) + afterDocument + "/document/" + afterDocument;

        return Uri.parse(newUri);
    }
}
