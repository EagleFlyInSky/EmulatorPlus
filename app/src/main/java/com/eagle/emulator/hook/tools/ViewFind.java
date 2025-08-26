package com.eagle.emulator.hook.tools;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.eagle.emulator.hook.HookParams;

public class ViewFind {

    // 递归遍历所有子视图的方法
    public static void findView(View view, ViewProcessor processor) {
        processor.process(view);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                findView(child, processor);
            }
        }
    }


    public static void logView(View view, String parent, ViewLog log) {
        String msg = log.log(view, parent);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                logView(child, msg, log);
            }
        }
    }


    public static void log(View rootView) {
        ViewFind.logView(rootView, "", (view, parent) -> {
            String msg = parent + ":" + view.getClass().getSimpleName() + view.getId();
//            view.setBackground(Drawable.createFromPath("/storage/emulated/0/Android/data/io.github.lime3ds.android/files/overlay/default.png"));
            Log.i(HookParams.LOG_TAG, msg);
            return msg;
        });
    }


}
