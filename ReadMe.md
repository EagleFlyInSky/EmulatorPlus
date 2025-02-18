# EmulatorPlus

本项目为 ```xposed``` 项目。
功能为增强模拟器和ES-DE前端的适配。

## ES-DE 自定义配置

[配置](https://github.com/EagleFlyInSky/ES-DE-Custom)


## 目前适配

### JoiPlay

#### 软件

[JoiPlay软件](https://www.patreon.com/joiplay)

修正 id 生成，支持中文名 id，在添加游戏时游戏id等于游戏名称

> 这里说一下 joiplay 的配置文件，如果游戏文件在机身内存中，配置文件会在游戏文件夹内或游戏文件同目录下

> 如果在tf卡中，配置文件会在 /sdcard/Joiplay/games/游戏id 文件夹下

> 这里修改id支持中文，便于修改复制配置

> 注意 joiplay 的id是添加游戏时生成的，所以后续修改joiplay名称，不会修改id，建议添加游戏时确认游戏名称。

适配前端启动，通过名称对应来启动游戏

> 前端启动实现，目前通过游戏名称进行匹配启动

> 配置文件适配了，flash，easyrpg，rpgmaker 三个系统

> flash 可以直接将 .swf 文件放入 ROMs/flash 文件夹，需要flash文件名称和joiplay中的名称对应

> easyrpg和rpgmaker 需要将游戏文件夹，改名成 xxx.rpg, 然后将游戏文件夹移动到，ROMs/easyrpg 或 ROMs/rpgmaker中，也可以直接新建一个 xxx.rpg 文件

### Maldives

#### 软件

[Maldives软件](https://play.google.com/store/apps/details?id=net.miririt.maldivesplayer&hl=en_US)

#### 功能

适配前端启动

> 配置文件适配了，easyrpg，rpgmaker 两个系统

> easyrpg和rpgmaker 需要将游戏文件夹，改名成 xxx.rpg, 然后将游戏文件夹移动到，ROMs/easyrpg 或 ROMs/rpgmaker中

### AopAop

[AopAop软件](https://aopaop.com/)

#### 功能

适配前端启动

> 配置文件适配了，easyrpg，rpgmaker 两个系统

> 文件夹形式，需要将游戏文件夹，改名成 xxx.aop, 然后将游戏文件夹移动到，ROMs/easyrpg 或 ROMs/rpgmaker中

> aop打包形式，需要将 xxx.aop.png 修改成 xxx.aop, 然后将游戏文件移动到，ROMs/easyrpg 或 ROMs/rpgmaker中


### Winlator

#### 软件

[Winlator](https://github.com/brunodev85/winlator)

[Winlator-Amod 阿飞](https://github.com/afeimod/winlator-mod)

[Winlator-Cmod](https://github.com/coffincolors/winlator)

[Winlator-WB64](https://github.com/winebox64/winlator)

[Winlator-Glib](https://github.com/longjunyu2/winlator)

[静言思之-SZ](https://space.bilibili.com/44943638)

[J大](https://space.bilibili.com/470546807)

#### 功能

适配前端启动
> 前端启动适配，通过 xxx.desktop 文件与winlator快捷方式名称匹配启动， 将文件移动到 ROMs/windows


### 游戏梦工厂

### 软件

[游戏梦工厂](https://github.com/qingjun1991/GameDreamFactory)

主程序选最新 目前为 GameDreamFactory.2025-02-07.apk
数据包
- GameDreamFactory.2023-10-28.Windows.Linux.MacOS.Android.zip
- GameDreamFactory.2024-08-01.patch.zip
- GameDreamFactory.IntentStart.zip
按顺序解压覆盖

#### 软件使用

添加整合包

将整合包文件夹地址添加到 /storage/emulated/0/!GameDreamFactory/Games/games.cfg 中

```properties
[Games]
/storage/emulated/0/ROMs/mugen/索尼克战斗.mugen
```

#### 功能

适配前端启动
> 前端启动，将游戏整合文件夹改名 xxx.mugen 复制到 ROMs/mugen 文件夹下


### Monnlight

快捷方式内容

```properties
AppId=1921102828
UUID=F341B505-46C5-3025-6A1D-3DAFB47109C8
AppName=测试
HDR=false
```

### Tyranor

测试中

## 模块使用方案

### Root方案

未完成

### 非Root方案

[NoRoot.md](doc%2FNoRoot.md)

