# 盖世游戏

## 软件

包名 `com.xiaoji.egggame` 适配的是官方版

[盖世游戏](https://gamehub.xiaoji.com/zh-cn)

## 功能

### 前端启动

前端配置请自行添加，或着去下载我的最新整合版 [前端配置](https://github.com/EagleFlyInSky/ES-DE-Custom)

在盖世游戏软件内使用创建桌面快捷方式，会在 /sdcard/Download/eggGame/ 下生成 `xxx.egg` 快捷方式文件

#### ES-DE

将`egg`文件移动到 ROMs/windows

`es_systems.xml``

```xml
<system>
    <name>windows</name>
    <fullname>Microsoft Windows</fullname>
    <path>%ROMPATH%/windows</path>
    <!-- 注意添加对应的后缀  -->
    <extension>.desktop .shortcut .egg</extension>
    <!-- 盖世游戏 需要EmulatorPlus模块功能 -->
    <command label="EggGame (Standalone)">%EMULATOR_EGG-GAME% %ACTIVITY_CLEAR_TASK% %ACTIVITY_CLEAR_TOP% %DATA%=%ROM%</command>
    <!-- 省略其他模拟器配置 -->
</system>
```

`es_find_rules.xml`

```xml
<emulator name="EGG-GAME">
    <rule type="androidpackage">
        <entry>com.xiaoji.egggame/com.xj.app.SplashActivity</entry>
    </rule>
</emulator>
```

#### Beacon

`players_stable.json`

```json
{
  "name": "Windows",
  "shortname": "WIN",
  "extensions": "egg",
  "launch": "am start -n com.xiaoji.egggame/com.xj.app.SplashActivity -d {file_path}"
}
```

### 支持遮罩功能

适配遮罩功能，详情[遮罩配置](Overlay.md)

