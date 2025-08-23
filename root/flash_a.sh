#!/bin/sh

# 刷入 a分区

dd if="/sdcard/root/patched.img" of=/dev/block/by-name/init_boot_a bs=1M