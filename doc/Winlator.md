# Winlator

## 软件

包名 `com.winlator` 适配的是官方版

[Winlator](https://github.com/brunodev85/winlator)

## 功能

### 前端启动

前端配置请自行添加，或着去下载我的最新整合版 [前端配置](https://github.com/EagleFlyInSky/ES-DE-Custom)

前端启动适配，通过 xxx.desktop 文件与winlator快捷方式名称匹配启动

#### ES-DE

将`desktop`文件移动到 ROMs/windows

`es_systems.xml`

```xml
<system>
    <name>windows</name>
    <fullname>Microsoft Windows</fullname>
    <path>%ROMPATH%/windows</path>
    <!-- 注意添加对应的后缀  -->
    <extension>.desktop .shortcut .egg</extension>
    <!-- 需要EmulatorPlus模块功能 -->
    <command label="Winlator (Standalone)">%EMULATOR_WINLATOR% %ACTIVITY_CLEAR_TASK% %ACTIVITY_CLEAR_TOP% %EXTRA_shortcut_path%=%ROM%</command>
    <!-- 省略其他模拟器配置 -->
</system>
```

`es_find_rules.xml`

```xml
<emulator name="WINLATOR">
    <rule type="androidpackage">
        <entry>com.winlator/com.winlator.MainActivity</entry>
    </rule>
</emulator>
```

#### Beacon

`players_stable.json`

```json
{
  "name": "Windows",
  "shortname": "WIN",
  "extensions": "desktop",
  "launch": "am start -n com.winlator/.MainActivity -e shortcut_path {file_path}"
}
```

### 支持遮罩功能

适配遮罩功能，详情[遮罩配置](Overlay.md)

