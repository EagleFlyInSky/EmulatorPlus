# Beacon

## 软件

[Beacon]()

## 功能

### 启动命令参数扩展

`{file_context}` 读取文件内容

`{file_name}` 获取文件无后缀的名称

`{dir_path}` 获取文件所在文件夹的路径

`{dir_name}` 获取文件所在文件夹的名称

`{dir_uri}` 获取文件所在文件夹的uri格式路径

### 名称映射功能

因为 mame,mess 等一些核心不支持修改rom名称，扩展实现了文件名和中文名映射功能

`/storage/emulated/0/beacon/name_mapping` 文件夹下添加 ini 文件，可添加多个

示例
```properties
haidao = 海盗与骷髅宝藏
longgu = 勇闯龙谷
taikong = 太空军校生
TANQIU = 三维弹球
Wind and Cloud = 风云天下会
GS = 守护者之剑
zsf = 太极张三丰
shdfw = 上海大富翁
SWORDMAN = 天地劫 神魔至尊传
Rich4add = 大富翁4
```

该配置会在 Beacon 扫描添加游戏时生效，已经扫描到的，请删除文件夹后，重新添加文件夹扫描

### 文件排除功能

`/storage/emulated/0/beacon/ignore.txt` 文件中添加要排除的文件名（不包含后缀）

该配置会在 Beacon 扫描添加游戏时生效，排除掉配置的文件，主要用于去除一些bios之类的

示例 一行一个
```text
gdl-0006
gds-0014
gds-0020b
gds-0036f
neogeo
game
gamekin3
gameking
gm218
supracan
pgm
coh1000a
coh1000c
coh1000t
coh1000w
coh1001l
coh1002e
coh1002m
coh1002v
coh3002c
coh3002t
```


### 扩展ra核心

添加了 `mamemess` `fbneo_plus` 核心支持