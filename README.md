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
推荐购买：
https://item.taobao.com/item.htm?spm=a230r.1.14.58.Z1QTOX&id=7915403646&ns=1&abbucket=17#detail

