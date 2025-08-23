# AopAopPLus

该项目为xposed模块，功能为使AopAop支持前端启动


## 安装

### root

- 基础 ***Magisk***，***LSPosed***
- 安装 ***AopAop***
- 安装 ***AopAopPlus*** 模块并启动

### 非root

- 基础 ***Shizuku***，***LSPatch***
- 使用 ***LSPatch*** 修补 ***AopAopPlus*** 模块到 ***AopAop*** 软件中
- 安装修补后的 ***AopAop*** 软件

关于 ***Magisk***，***LSPosed***，***Shizuku***，***LSPatch*** 具体教程网上搜索


## es-de 适配

### 配置

#### 添加模拟器
es_find_rules.xml
```xml
<emulator name="AOPAOP">
    <!-- rpgmaker emulator AOPAOP -->
    <rule type="androidpackage">
        <entry>com.aopaop.app/com.aopaop.app.module.common.SplashActivity</entry>
    </rule>
</emulator>
```

#### 添加平台配置

目前 ***es-de*** 只适配了 ***easyrpg***， 没有适配 ***rpgmaker***
可以在 ***easyrpg*** 中修改，也可自定义 ***rpgmaker*** 平台

关键配置
- 在 system > extension 下添加对 aop 游戏格式的支持
- 添加 AopAop (Standalone) 配置

```xml
<system>
    <!--  ******  -->
    <extension>.joiplay .JOIPLAY .rpg .RPG .aop .AOP</extension>
    <command label="AopAop (Standalone)">%EMULATOR_AOPAOP% %ACTION%=android.intent.action.VIEW %DATA%=%ROM%</command>
</system>
```

样例

easyrpg
```xml
<system>
    <name>easyrpg</name>
    <fullname>EasyRPG Game Engine</fullname>
    <path>%ROMPATH%/easyrpg</path>
    <extension>.easyrpg .zip .ZIP .joiplay .JOIPLAY .rpg .RPG .aop .AOP</extension>
    <command label="EasyRPG">%EMULATOR_RETROARCH% %EXTRA_CONFIGFILE%=/storage/emulated/0/Android/data/%ANDROIDPACKAGE%/files/retroarch.cfg %EXTRA_LIBRETRO%=easyrpg_libretro_android.so %EXTRA_ROM%=%ROM%</command>
    <command label="Joiplay (Standalone)">%EMULATOR_JOIPLAY% %ACTION%=android.intent.action.MAIN %ACTIVITY_CLEAR_TASK% %ACTIVITY_CLEAR_TOP% %EXTRA_id%=%INJECT%=%BASENAME%.joiplay</command>
    <command label="MaldiVes (Standalone)">%EMULATOR_MALDIVES% %ACTION%=android.intent.action.VIEW %DATA%=%ROMSAF%</command>
    <command label="AopAop (Standalone)">%EMULATOR_AOPAOP% %ACTION%=android.intent.action.VIEW %DATA%=%ROM%</command>
    <platform>easyrpg</platform>
    <theme>easyrpg</theme>
</system>
```

rpgmaker
```xml
<system>
    <name>rpgmaker</name>
    <fullname>RPG Maker</fullname>
    <path>%ROMPATH%/rpgmaker</path>
    <extension>.joiplay .JOIPLAY .rpg .RPG .aop .AOP</extension>
    <command label="Joiplay (Standalone)">%EMULATOR_JOIPLAY% %ACTION%=android.intent.action.MAIN %EXTRA_id%=%INJECT%=%BASENAME%.joiplay</command>
    <command label="MaldiVes (Standalone)">%EMULATOR_MALDIVES% %ACTION%=android.intent.action.VIEW %DATA%=%ROMSAF%</command>
    <command label="AopAop (Standalone)">%EMULATOR_AOPAOP% %ACTION%=android.intent.action.VIEW %DATA%=%ROM%</command>
    <platform>rpgmaker</platform>
    <theme>easyrpg</theme>
</system>
```

### 游戏安装

AopAop支持两种格式的游戏

#### 文件夹

将游戏文件夹名修改成 ***xxx.aop***

放置到 ***es-de*** 的 ***Roms*** 文件夹下对应的系统文件夹下

#### AopAop打包格式

***AopAop*** 打包的游戏格式为 ***xxx.aop.png*** 修改为 ***xxx.aop***

放置到 ***es-de*** 的 ***Roms*** 文件夹下对应的系统文件夹下

