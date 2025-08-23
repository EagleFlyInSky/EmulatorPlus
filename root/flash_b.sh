#!/bin/sh

# 刷入 b分区

dd if="/sdcard/root/patched.img" of=/dev/block/by-name/init_boot_b bs=1M