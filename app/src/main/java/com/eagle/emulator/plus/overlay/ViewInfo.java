package com.eagle.emulator.plus.overlay;

import android.view.View;
import android.view.ViewGroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewInfo {

    /**
     * 游戏View 调整布局使用
     */
    private View gameView;

    /**
     * 设置背景图View
     */
    private View overlayView;

    /**
     * 父级ViewGroup 添加图片View使用
     */
    private ViewGroup parentView;

    /**
     * 是否添加图片view作为遮罩
     */
    private boolean addImageView;

    /**
     * 图片view索引 null为不设置
     */
    private Integer imageViewIndex;


}
