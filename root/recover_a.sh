#!/bin/sh

# 恢复init_boot_a分区
dd if="/sdcard/root/init_boot_a.img" of=/dev/block/by-name/init_boot_a bs=1M