package com.eagle.emulator.util;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.List;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

public class IniUtil {


    public static JSONObject parseIni(String path) throws Throwable {
        List<String> lines = FileUtil.readLines(path, StandardCharsets.UTF_8);
        JSONObject object = new JSONObject();
        for (String line : lines) {
            if (StrUtil.contains(line, "=")) {
                String[] split = line.split("=");
                String key = split[0];
                String value = split[1];
                object.put(key, value);
            }
        }
        return object;
    }

}
