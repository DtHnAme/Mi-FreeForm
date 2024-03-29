# 米窗2.5版本常见问题（持续更新）

## 1.米窗打开部分界面时一闪而过，从此侧边栏、悬浮窗再也打不开了
如果你使用的是<b>前台保活模式</b>，并且打开的界面如果包含<b>系统设置、授权界面、部分设备的分身选择等重要的系统界面</b>，就会出现上述问题。解决方案：使用无障碍保活

## 2.米窗点击某个界面后全屏
尝试使用Xposed勾选系统框架并重启，Android 12+可以尝试勾选米窗中“使用Shizuku/Sui阻止应用跳出小窗到全屏”选项

## 3.无法通过米窗打开小窗/卡在引导/打开小窗闪退
产生该问题的原因可能是系统中设置过Vulkan。具体请见引导界面文字描述

## 4.打开小窗后，应用界面不能填满小窗，表现为类似Windows窗口
前往系统设置-开发者选项，关闭“强制使用桌面模式”

## 5.米窗掉后台
一些定制ROM可能产生该问题，建议为米窗加锁。米窗使用无障碍或前台服务保活，如果这两个服务都被杀掉的话，请考虑是否是系统问题？或尝试切换保活方式

## 6.界面缩放过大/过小
米窗支持单独对横屏/竖屏界面缩放调整，请分别调整

## 7.屏幕旋转后界面显示异常
某些低性能设备上可能产生该问题，如果出现该问题，可以尝试关闭米窗设置中“在屏幕旋转时修改小窗缩放比例”选项