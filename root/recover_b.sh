#!/bin/sh

# 恢复init_boot_a分区
dd if="/sdcard/root/init_boot_b.img" of=/dev/block/by-name/init_boot_b bs=1M