package com.eagle.emulator.plus.overlay;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.luckypray.dexkit.result.FieldData;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public abstract class OverlayHook {

    protected XC_LoadPackage.LoadPackageParam lpparam;
    protected String hookClassName;
    protected Class<?> hookClass;
    protected OverlayConfig config;

    public OverlayHook(XC_LoadPackage.LoadPackageParam lpparam, String hookClassName) {
        this(lpparam, hookClassName, false);
    }

    public OverlayHook(XC_LoadPackage.LoadPackageParam lpparam, String hookClassName, boolean dexkit) {
        this.lpparam = lpparam;
        this.hookClassName = hookClassName;
        this.hookClass = XposedHelpers.findClass(hookClassName, lpparam.classLoader);

        initConfig();
    }

    protected void initConfig() {
        String configPath = getConfigPath();
        if (StrUtil.isNotBlank(configPath)) {
            config = new BaseOverlayConfig(configPath);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T getField(Activity activity, FieldData fieldData) {
        try {
            Field fieldInstance = fieldData.getFieldInstance(lpparam.classLoader);
            return (T) ReflectUtil.getFieldValue(activity, fieldInstance);
        } catch (NoSuchFieldException e) {
            XposedBridge.log(e);
            return null;
        }
    }

    public final void hook() {
        if (config != null) {
            XposedBridge.log(StrUtil.format("Hook遮罩方法开始：{}", hookClassName));
            hookMethod(param -> {
                XposedBridge.log(StrUtil.format("Hook遮罩方法运行"));
                handler(param);
            });
            hookPlus();
        } else {
            XposedBridge.log(StrUtil.format("遮罩配置为空"));
        }
    }

    public void hookPlus() {
    }


    public void hookMethod(Consumer<XC_MethodHook.MethodHookParam> consumer) {
        XposedHelpers.findAndHookMethod(hookClassName, lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                consumer.accept(param);
            }
        });
    }


    protected void handler(XC_MethodHook.MethodHookParam param) {
        // 获取执行对象
        Activity activity = (Activity) param.thisObject;
        // 确认是对象类型
        String className = activity.getClass().getName();
        if (!className.equals(hookClassName)) {
            return;
        }

        getView(activity);

        // 获取当前游戏信息
        GameInfo gameInfo = getGameInfo(activity);
        if (gameInfo == null) {
            return;
        }

        // 通过游戏信息获取遮罩图片
        String overlayImage = config.getOverlayImage(gameInfo);
        if (StrUtil.isNotBlank(overlayImage)) {
            String name = gameInfo.getName();
            String format = StrUtil.format("{}：{}", name, overlayImage);
            XposedBridge.log(format);
            ViewInfo viewInfo = getViewInfo(activity);
            overlay(viewInfo, overlayImage);
        }

    }

    protected void overlay(ViewInfo viewInfo, String overlayImage) {
        setOverlay(viewInfo, overlayImage);
        View gameView = viewInfo.getGameView();
        if (gameView != null) {
            setLayout(gameView, overlayImage);
        }
    }

    protected void setLayout(View view, String overlayImage) {

        String settingPath = overlayImage.replace("png", "ini");
        XposedBridge.log(StrUtil.format("视图：{}", view.getClass().getName()));
        XposedBridge.log(StrUtil.format("配置路径：{}", settingPath));

        if (FileUtil.exist(settingPath)) {
            // 加载配置文件
            Setting setting = new Setting(FileUtil.file(settingPath), StandardCharsets.UTF_8, false);
            XposedBridge.log(StrUtil.format("读取配置：{}", setting.toString()));

            Integer left = setting.getInt("LEFT", 0);
            Integer top = setting.getInt("TOP", 0);
            Integer right = setting.getInt("RIGHT", 0);
            Integer bottom = setting.getInt("BOTTOM", 0);

            XposedBridge.log(StrUtil.format("获取配置 左：{}，上：{}，右：{},下：{}", left, top, right, bottom));

            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) layoutParams;
                // 设置四个方向的外边距
                marginParams.setMargins(left, top, right, bottom);
                view.setLayoutParams(marginParams);
            }
        }
    }

    protected void setOverlay(ViewInfo viewInfo, String overlayImage) {
        Drawable drawable = Drawable.createFromPath(overlayImage);
        // 是否添加遮罩图层
        if (viewInfo.isAddImageView()) {
            ViewGroup parentView = viewInfo.getParentView();
            if (parentView == null) {
                View gameView = viewInfo.getGameView();
                if (gameView == null) {
                    XposedBridge.log("游戏视图和父级视图为空");
                    return;
                }
                ViewParent parent = gameView.getParent();
                if (parent instanceof ViewGroup) {
                    parentView = (ViewGroup) parent;
                } else {
                    XposedBridge.log("无法获取父级视图");
                    return;
                }
            }
            // 创建图片视图
            ImageView overlayView = new ImageView(parentView.getContext());
            overlayView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            overlayView.setImageDrawable(drawable);
            // 判断添加索引
            if (viewInfo.getImageViewIndex() == null) {
                parentView.addView(overlayView);
            } else {
                parentView.addView(overlayView, viewInfo.getImageViewIndex());
            }
        } else {
            View overlayView = viewInfo.getOverlayView();
            if (overlayView == null) {
                XposedBridge.log("遮罩视图为空");
                return;
            }
            overlayView.setBackground(drawable);
        }
    }


    protected abstract String getConfigPath();


    /**
     * 获取View信息
     *
     * @param activity 活动
     * @return View信息
     */
    protected ViewInfo getViewInfo(Activity activity) {
        ViewInfo viewInfo = new ViewInfo();
        View view = getView(activity);
        viewInfo.setOverlayView(view);
        viewInfo.setGameView(view);
        return viewInfo;
    }

    /**
     * 获取View
     *
     * @param activity 活动
     * @return View信息
     */
    protected View getView(Activity activity) {
        return null;
    }

    /**
     * 获取游戏信息
     *
     * @param activity 活动
     * @return 游戏信息
     */
    protected GameInfo getGameInfo(Activity activity) {
        return new GameInfo(getName(activity));
    }

    /**
     * 获取游戏名称
     *
     * @param activity 活动
     * @return 游戏名称
     */
    protected String getName(Activity activity) {
        return null;
    }


}
