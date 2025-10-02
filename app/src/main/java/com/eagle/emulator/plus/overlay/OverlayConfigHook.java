package com.eagle.emulator.plus.overlay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eagle.emulator.MainHook;
import com.eagle.emulator.R;
import com.eagle.emulator.plus.core.FontListAdapter;
import com.eagle.emulator.plus.overlay.azahar.OnSwipeTouchListener;

import de.robv.android.xposed.XC_MethodHook;
import lombok.Getter;


@Getter
public class OverlayConfigHook {

    protected LinearLayout configView;

    protected XC_MethodHook.MethodHookParam param;

    protected Activity activity;
    private boolean isViewOpen = false;
    private LayoutInflater layoutInflater;

    private final String gameName;

    @SuppressLint("InflateParams")
    public OverlayConfigHook(XC_MethodHook.MethodHookParam param, String gameName) {
        this.param = param;
        this.gameName = gameName;

        Activity activity = (Activity) param.thisObject;
        this.activity = activity;

        intInflater();
        View view = layoutInflater.inflate(R.layout.overlay_view, null);
        if (view instanceof LinearLayout) {
            configView = (LinearLayout) view;
        }
        configView.setTranslationY(getScreenHeight());

        ViewGroup content = activity.findViewById(android.R.id.content);
        content.addView(configView);

        TextView gameTitle = activity.findViewById(R.id.gameTitle);
        gameTitle.setText(gameName);

        ImageButton backButton = activity.findViewById(R.id.back);
        backButton.setOnClickListener(v -> swipeDown());

        initContent();
    }

    protected void initContent() {

        RecyclerView recyclerView = new RecyclerView(activity.getBaseContext());
        // 创建并设置适配器
        FontListAdapter fontListAdapter = new FontListAdapter(layoutInflater,this);
        recyclerView.setAdapter(fontListAdapter);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        recyclerView.setLayoutParams(params);

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);

        configView.addView(recyclerView);

    }

    private void intInflater() {
        Resources moduleResources = MainHook.getModuleResources();
        LayoutInflater inflater = LayoutInflater.from(activity);
        Context wrappedContext = new ContextWrapper(activity) {
            @Override
            public Resources getResources() {
                return moduleResources;
            }

            @Override
            public Resources.Theme getTheme() {
                return moduleResources.newTheme();
            }
        };
        this.layoutInflater = inflater.cloneInContext(wrappedContext);
    }



    public void swipeUp() {
        if (!isViewOpen) {
            configView.animate().translationY(0).setDuration(300).start();
            isViewOpen = true;
        }
    }

    public void swipeDown() {
        if (isViewOpen) {
            configView.animate().translationY(getScreenHeight()).setDuration(300).start();
            isViewOpen = false;
        }
    }


    private int getScreenHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            WindowMetrics windowMetrics = windowManager.getCurrentWindowMetrics();
            return windowMetrics.getBounds().height();
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.heightPixels;
        }
    }

    private void addTouchArea(Activity activity, ViewGroup content) {
        View view = new View(activity);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
        params.gravity = Gravity.BOTTOM;
        view.setLayoutParams(params);
        view.setBackgroundColor(Color.GRAY);

        // 设置手势监听器
        view.setOnTouchListener(new OnSwipeTouchListener(activity) {
            @Override
            public void onSwipeUp() {
                if (!isViewOpen) {
                    view.animate().translationY(0).setDuration(300).start();
                    isViewOpen = true;
                }
            }

            @Override
            public void onSwipeDown() {
                if (isViewOpen) {
                    view.animate().translationY(view.getHeight()).setDuration(300).start();
                    isViewOpen = false;
                }
            }
        });

        content.addView(view);
    }


}
