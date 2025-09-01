# 游戏梦工厂

## 软件

[游戏梦工厂](https://github.com/qingjun1991/GameDreamFactory)

主程序选最新 目前为 GameDreamFactory.2025-02-07.apk
数据包

- GameDreamFactory.2023-10-28.Windows.Linux.MacOS.Android.zip
- GameDreamFactory.2024-08-01.patch.zip
- GameDreamFactory.IntentStart.zip
  按顺序解压覆盖

### 软件使用

添加整合包

将整合包文件夹地址添加到 /storage/emulated/0/!GameDreamFactory/Games/games.cfg 中

```properties
[Games]
/storage/emulated/0/ROMs/mugen/索尼克战斗
```

## 功能

### 前端启动

前端配置请自行添加，或着去下载我的最新整合版 [前端配置](https://github.com/EagleFlyInSky/ES-DE-Custom)

在游戏文件夹内添加 XXX.mugen 文件即可

例如 `/storage/emulated/0/ROMs/mugen/索尼克战斗` 是配置的游戏目录，创建文件 `/storage/emulated/0/ROMs/mugen/索尼克战斗/索尼克战斗.mugen`


#### ES-DE

前端启动，将游戏整合文件夹改名 xxx.mugen 复制到 ROMs/mugen 文件夹下

`es_systems.xml`

```xml

<system>
    <name>mugen</name>
    <fullname>M.U.G.E.N Game Engine</fullname>
    <path>%ROMPATH%/mugen</path>
    <extension>.mugen</extension>
    <command label="GameDreamFactory (Standalone)">%EMULATOR_GAME-DREAM-FACTORY% %EXTRA_path%=%ROM%</command>
    <platform>mugen</platform>
    <theme>mugen</theme>
</system>
```

`es_find_rules.xml`

```xml

<emulator name="GAME-DREAM-FACTORY">
    <rule type="androidpackage">
        <entry>com.GameDreamFactoryAndroid/com.GameDreamFactoryAndroid.activities.MainActivity
        </entry>
    </rule>
</emulator>
```

#### Beacon

`players_stable.json`

```json
{
  "name": "Mugen",
  "shortname": "MUGEN",
  "extensions": "mugen",
  "launch": "am start -n com.GameDreamFactoryAndroid/com.GameDreamFactoryAndroid.activities.MainActivity -e path {dir_path}"
}
```