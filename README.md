# 工程说明
本工程包含android sdk及其使用demo

# 使用说明
使用时请将NaviPackExe/Navipack覆盖激光雷达系统版中的文件，可执行：

    adb push Navipack /data/shelly_robot

将AS工程编译，得到的APK可与上述文件配套使用
因为还在优化阶段，注意，不同版本的两个程序不一定兼容，请务必保证两者是配套使用的。

# 目录说明
Doc:包含android sdk的相关类的doc文档
navipacksdk:包含自己应用程序所要包含的module
NaviPackExe:目录中的可执行文件为与之对应的雷达套件板中运行的程序

#备注
wifi模块选型：
naviPack激光雷达套件暂时仅支持使用**8192cu**芯片的wifi模块。
推荐购买(或者同类产品)：
https://item.taobao.com/item.htm?spm=a230r.1.14.58.Z1QTOX&id=7915403646&ns=1&abbucket=17#detail

串口开启注意事项：
一定要确保自己的应用程序有权限来打开串口。
NaciPack接到上位机上的时候会再上位机上的/dev目录下生成一个ttyACMX的串口，要打开这个串口的APP至少对这个文件具有**RW**的权限。如果操作系统是自己编译，可以在app程序启动前执行chmod +rw /dev/ttyACM*的操作，当然如果有其他更好的办法也是OK的。