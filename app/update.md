# 2.5.7 beta
- 仍然没有排查出黑边问题。如果你有问题，请尽量把米窗设置界面截图、出现问题应用告诉我
- 修复 在有正在运行程序的情况下锁屏，米窗崩溃的问题 q220902.1
- 修复（待测试） 某些竖屏软件也会横屏的问题 q220902.2
- 修复（待测试） 尝试修复侧边栏在屏幕中间的问题 q220902.3
- 新增 通过活动打开侧边栏

# 2.5.8 beta
特别提醒：米窗为类原生制作，在任何定制系统上运行都有可能产生不可预知的问题
特别提醒：米窗为类原生制作，在任何定制系统上运行都有可能产生不可预知的问题
特别提醒：米窗为类原生制作，在任何定制系统上运行都有可能产生不可预知的问题
- 优化 横屏小窗挂起的位置 q220903.1
- 移除了兼容性模式选项
- 在Android 10设备上重新支持xp阻止跳出小窗
- 修复 在Android 10设备上小窗内屏幕旋转失效的问题 q220903.2

# 2.5.9 beta
- 修复 尝试修复记录的小窗大小异常的问题 q220904.1
- 优化 小窗缩放允许的范围更大
- 修复 调整dpi后导致负数dpi从而米窗崩溃的问题 q220904.2
- 优化 小窗内部旋转时，如果旋转后的尺寸大于屏幕尺寸，则进行调整 q220904.3
- 修复 挂起后再恢复小窗，小窗缩放异常的问题 q220904.5
- 优化 尝试更加跟手的缩放操作 q220904.6
- 优化 更小的底栏
- 新增 手动调整小窗方向 q220904.7
- 优化 尝试更加流畅的缩放动画 q220904.8

# 2.5.10
- 新增 前台服务保活

# 2.5.12
- 优化 支持多点触控

# 2.5.13
- 新增 支持从多任务界面打开小窗
- 优化 界面优化
- 修复 使用活动打开小窗选择界面时，可能产生无法关闭选择界面的情况
- 修复 部分设备上屏幕旋转引起的分辨率错误问题
- 修复 在部分情况（如在多任务界面切换屏幕方向）而导致无法识别屏幕旋转的问题

# 2.5.14
- 优化 一些界面优化
- 优化 如果小窗移动到屏幕外导致无法控制，可以尝试从侧边栏再次点击该应用以移动到屏幕中心 q220917.4
- 修复 屏幕旋转后QQ的缩放不正确的问题 q220910.1
- 优化 QQ和微信也支持缩放了 q220917.1
- 移除了引导界面，但是仍然可以自行查看 q220917.2
- 优化 锁屏后小窗的状态 q220917.3

# 2.5.15
- 新增 （可能）对Android 8-9进行支持
- 新增 （测试）自由调节小窗比例
- 新增 侧边栏透明度支持调整。侧边栏还会进行优化，敬请期待

# 2.5.16
- 修复 小窗挂起后比例有误的问题
- 修复 小窗在自由调整比例的情况下小窗内容比例可能不刷新的问题

# 2.5.17
- 修复 开启记录位置选项后触控错位的问题 q220925.3

# 2.5.18
- 修复 屏幕旋转后侧边栏不贴边的问题 q221208.1
- 优化 多任务打开米窗的方式更改为点击应用图标后发现 issue#7

# 2.5.19
- 优化 多任务打开米窗的样式 by duzhaokun123

## 探究
- 如果应用发生了crash，那么onConfigurationChanged是不会调用的，configuration都是不变化的
- 将触摸设置为一等公民，以支持多点触控，也可以看一下为什么那样，多点触控就不支持了... q220906.1