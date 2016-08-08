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

