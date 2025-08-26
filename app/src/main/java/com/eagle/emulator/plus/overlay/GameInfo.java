package com.eagle.emulator.plus.overlay;

public class GameInfo {

    private String name;

    private int screen;


    public GameInfo(String name) {
        this.name = name;
    }

    public GameInfo(String name, int screen) {
        this.name = name;
        this.screen = screen;
    }

    public String getName() {
        return name;
    }

    public int getScreen() {
        return screen;
    }
}
