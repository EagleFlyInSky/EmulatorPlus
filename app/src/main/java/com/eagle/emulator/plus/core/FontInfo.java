package com.eagle.emulator.plus.core;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FontInfo {
    private String title;
    private String fontPath;
    private boolean defaultFont;
    private boolean currentFont;
}
