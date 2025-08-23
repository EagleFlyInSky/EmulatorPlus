
# JoiPlay

## 软件

[JoiPlay软件](https://www.patreon.com/joiplay)

## 功能

### ID生成

修正 id 生成，支持中文名 id，在添加游戏时游戏id等于游戏名称

> 这里说一下 joiplay 的配置文件，如果游戏文件在机身内存中，配置文件会在游戏文件夹内或游戏文件同目录下

> 如果在tf卡中，配置文件会在 /sdcard/Joiplay/games/游戏id 文件夹下

> 这里修改id支持中文，便于修改复制配置

> 注意 joiplay 的id是添加游戏时生成的，所以后续修改joiplay名称，不会修改id，建议添加游戏时确认游戏名称。

适配前端启动，通过名称对应来启动游戏

### 前端启动

前端启动实现，目前通过游戏名称进行匹配启动

#### ES-DE 适配

> 配置文件适配了，flash，easyrpg，rpgmaker 三个系统

> flash 可以直接将 .swf 文件放入 ROMs/flash 文件夹，需要flash文件名称和joiplay中的名称对应

> easyrpg和rpgmaker 需要将游戏文件夹，改名成 xxx.joi, 然后将游戏文件夹移动到，ROMs/easyrpg 或 ROMs/rpgmaker中，也可以直接新建一个 xxx.joi 文件

#### Beacon 适配

自动适配joiplay软件，只需要建立和软件内名称相同的 xxx.joi 文件即可

### 适配遮罩

该功能适配了Joiplay主体和Ruffle，RPGMaker插件，其他插件未适配
使用 LSPosed 要勾选主体和插件，内嵌模式需要修改主体和插件

适配遮罩功能，详情[遮罩配置](Overlay.md)

