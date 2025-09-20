package com.eagle.emulator.hook.tools;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayDeque;
import java.util.Queue;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import de.robv.android.xposed.XposedBridge;

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


    public static ViewGroup findViewGroupByIndex(View view, Integer... indexes) {
        View getView = findViewByIndex(view, indexes);
        if (getView instanceof ViewGroup) {
            return (ViewGroup) getView;
        } else {
            return null;
        }
    }


    public static View findViewByIndex(View view, Integer... indexes) {
        Queue<Integer> queue = new ArrayDeque<>();

        // 入队 (添加元素到队尾)
        for (Integer index : indexes) {
            queue.offer(index);
        }

        return findViewByIndex(view, queue);

    }


    private static View findViewByIndex(View view, Queue<Integer> indexQueue) {

        if (view == null) {
            return null;
        }

        Integer index = indexQueue.poll();
        if (index == null) {
            return view;
        }


        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            View child = viewGroup.getChildAt(index);
            if (child == null) {
                return null;
            }
            return findViewByIndex(child, indexQueue);
        }

        return null;
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
            long length = parent.length();
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < length; i++) {
                buffer.append(" ");
            }
            String prefix = buffer.toString();
            String viewInfo = view.getClass().getSimpleName();
            int id = view.getId();
            if (id != -1) {
                String resourceName = null;
                try {
                    resourceName = view.getContext().getResources().getResourceName(id);
                } catch (Resources.NotFoundException ignored) {

                }
                if (StrUtil.isNotBlank(resourceName)) {
                    viewInfo += (":" + FileNameUtil.mainName(resourceName));
                } else {
                    viewInfo += (":" + id);
                }
            }
            String msg = StrUtil.format("{}=>{}", prefix, viewInfo);
            XposedBridge.log(msg);
//            view.setBackground(Drawable.createFromPath("/storage/emulated/0/Android/data/com.explusalpha.SaturnEmu/files/overlay/default.png"));
            return msg;
        });
    }


}
