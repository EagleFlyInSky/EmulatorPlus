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
/storage/emulated/0/ROMs/mugen/索尼克战斗.mugen
```

## 功能

### 前端启动

提醒，未后续适配Beacon 可能会修改实现方案，望谅解

#### ES-DE

前端启动，将游戏整合文件夹改名 xxx.mugen 复制到 ROMs/mugen 文件夹下

#### Beacon

目前Beacon不支持文件夹扫描，等待之后适配