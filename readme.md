# EmulatorPlus

本项目为 ```xposed``` 项目。
功能为增强模拟器和ES-DE前端的适配。


## 目前适配

### JoiPlay

修正 id 生成，支持中文名 id，在添加游戏时游戏id等于游戏名称

> 这里说一下 joiplay 的配置文件，如果游戏文件在机身内存中，配置文件会在游戏文件夹内或游戏文件同目录下
> 如果在tf卡中，配置文件会在 /sdcard/Joiplay/games/游戏id 文件夹下
> 这里修改id支持中文，便于修改复制配置
> 注意 joiplay 的id是添加游戏时生成的，所以后续修改joiplay名称，不会修改id，建议添加游戏时确认游戏名称。

修改前端启动，通过名称对应来启动游戏

> 前端启动实现，目前通过游戏名称进行匹配启动
> 配置文件适配了，flash，easyrpg，rpgmaker 三个系统
> flash 可以直接将 .swf 文件放入 ROMs/flash 文件夹，需要flash文件名称和joiplay中的名称对应
> easyrpg和rpgmaker 需要将游戏文件夹，改名成 xxx.rpg, 然后将游戏文件夹移动到，ROMs/easyrpg 或 ROMs/rpgmaker中，也可以直接新建一个 xxx.rpg 文件

### Maldives

修改前端启动

> 配置文件适配了，easyrpg，rpgmaker 两个系统
> easyrpg和rpgmaker 需要将游戏文件夹，改名成 xxx.rpg, 然后将游戏文件夹移动到，ROMs/easyrpg 或 ROMs/rpgmaker中

### AopAop

> 配置文件适配了，easyrpg，rpgmaker 两个系统
> 文件夹形式，需要将游戏文件夹，改名成 xxx.aop, 然后将游戏文件夹移动到，ROMs/easyrpg 或 ROMs/rpgmaker中
> aop打包形式，需要将 xxx.aop.png 修改成 xxx.aop, 然后将游戏文件移动到，ROMs/easyrpg 或 ROMs/rpgmaker中


### Winlator

> 前端启动适配，通过 xxx.desktop 文件与winlator快捷方式名称匹配启动


### 游戏梦工厂

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

