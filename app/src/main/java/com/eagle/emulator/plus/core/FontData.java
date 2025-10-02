package com.eagle.emulator.plus.core;

import java.util.Map;

import lombok.Data;

@Data
public class FontData {
    private String defaultFont;
    private Map<String, String> gameFonts;
}
