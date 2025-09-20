package com.eagle.emulator.plus.overlay;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameInfo {

    /**
     * 游戏名称，不同模拟器不同
     */
    private String name;

    /**
     * 布局类型 Azahar专用
     */
    private int screen;


    public GameInfo(String name) {
        this.name = name;
    }
}
