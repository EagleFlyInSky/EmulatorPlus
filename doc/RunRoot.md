# odin2 root 方案

## 介绍

### 脚本说明

backup.sh 备份

flash_a.sh 刷入A区
flash_b.sh 刷入B区

recover_a.sh 恢复A区
recover_b.sh 恢复B区


### 文件说明

执行 backup.sh 后会生成
init_boot_a.img A区镜像
init_boot_b.img B区镜像

使用 root管理工具（Magisk，SukiSU等） 修补A区或B区镜像后
将修补的镜像重命名 patched.img 复制到这里
patched.img 修补后的镜像


## root方法

以下的脚本执行，建议使用掌机的 `以root身份运行脚本` 功能

1. 执行 `backup.sh` 备份A区B区镜像
2. 使用root管理工具（Magisk，SukiSU等）修补A区或B区镜像
3. 将修补后的镜像重命名为 `patched.img` 复制到脚本目录
4. 执行 `flash_a.sh` 或 `flash_b.sh` 刷入修补后的镜像，`flash_all.sh` 同时将`patched.img`刷入两个分区

## 恢复方法

执行 `recover_a.sh` 或 `recover_b.sh` 恢复备份的镜像

这里适用于ota更新时，先执行恢复方法，再重新进行root方法。OTA升级有时会更新img文件，建议每次OTA后重新执行一遍，上次生成的文件删除，不要用到下一次执行。

强烈建议，执行 backup.sh 后将 init_boot_a.img init_boot_b.img 文件单独备份，以备不时之需。