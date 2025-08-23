#!/bin/sh

# 备份 init_boot_a 和 init_boot_b
mkdir -p /sdcard/root
dd if=/dev/block/by-name/init_boot_a of="/sdcard/root/init_boot_a.img" bs=1M
dd if=/dev/block/by-name/init_boot_b of="/sdcard/root/init_boot_b.img" bs=1M