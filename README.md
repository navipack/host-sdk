# 工程说明
本工程包含android sdk及其使用demo

# 使用说明
使用时请将NaviPackExe/Navipack覆盖激光雷达系统版中的文件，可执行：

    adb root
    adb push NaviPack /data/shelly_robot
如果要更新配置文件，可执行:

    adb push NaviPackParam.ini /data/shelly_robot/config

##注意：
当前版本，请务必保证sdk与NaviPack版本使用一致，因为现在还处于开发阶段，稳定版本应该在十一月份左右推出，在此之前请配对使用，带来的不变敬请谅解。每个版本的对应NaviPack程序在NaviPackExe中可以找到。

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

#wifi使用配置说明
##指令配置

 1. 当前为开发者提供指令的配置方式。配置步骤如下：
 2. 执行 adb shell 进入系统
 3. 执行 su 切换用户为超级用户
 4. 执行 wifi_start.out ssid password来配置网络(ssid和password别搞错了)
 5. 执行后10秒左右执行netcfg看是否有设备为wlan并是否存在ip地址，如果有则成功。
 6. 否则重启，再来一次上述指令。

##串口配置

 1. 使用sdk连接navipack
 2. 使用setWifiParam()接口配置wifi，具体参考demo程序

#串口开启注意事项：
一定要确保自己的应用程序有权限来打开串口。
NaciPack接到上位机上的时候会再上位机上的/dev目录下生成一个ttyACMX的串口，要打开这个串口的APP至少对这个文件具有**RW**的权限。如果操作系统是自己编译，可以在app程序启动前执行chmod +rw /dev/ttyACM*的操作，当然如果有其他更好的办法也是OK的。

#配置文件使用

    ;本配置文件包含了激光雷达套件所需要配置的信息
    ;背景知识：载体的质点默认在两个驱动轮中心，载体位姿即是质点位姿；
    ;载体坐标系零点在质点，正前方为X方向，Y方向符合右手法则；
    ;各个传感器参数都是相对载体坐标系。
    [ChassisParam]
    ;车的形状参数，用外接几何形状的顶点表示
    ;可以是三边形、四边形，及多边形。不建议采用多边形表示
    ;chassisShapeParamNum：外接几何形状的顶点个数
    chassisShapeParamNum=4
    ;*****************车子的形状参数X(相对轮轴中心)
    chassisShapeParamX=-200,200,200,-200
    ;*****************车子的形状参数Y(相对轮轴中心)
    chassisShapeParamY=-200,-200,200,200
    ;是否有IMU传感器：1表示有，0表示无
    ;****************重点参数注意：（推荐使用1，此时采用雷达上陀螺仪计算角度，否则会采用底盘的编码器计算角度，如果确保自己的编码器输出数据非常准，且轮子不打滑则推荐将参数置为0）
    bhasIMUSensor=1
    ;没有IMU情况下，需要该轮间距参数
    ;****************重点参数
    WheelDistance=232
    [UltraSensorParam]
    ;超声波传感器个数
    ultrasoundSensorNum=8
    ;相对轮轴中心安装位置
    ultrasoundSensorX=222,178,62,0,0,0,62,178
    ultrasoundSensorY=0,116,164,0,0,0,-164,-116
    ;超声波水平朝向
    ;如果不是水平朝向，需要将测量数据投影到水平方向
    ultrasoundSensorOrientationAngle=0,45,90,0,0,0,270,315
    ;超声波用于NaviPack的最小视距（不建议修改）
    ultrasoundSensorMinMeasureDistance=30
    ;超声波用于NaviPack的最大视距（不建议修改）
    ultrasoundSensorMaxMeasureDistance=600
    ;超声波视场角
    ultrasoundSensorFOV=15
    [DropSensorParam]
    ;跌落传感器个数
    dropSensorNum=4
    ;跌落传感器相对轮轴中心安装位置
    dropSensorX=50,150,150,50
    dropSensorY=-150,-50,50,150
    [CollisionSensorParam]
    ;碰撞传感器开关量个数
    collisionSensorNum=2
    ;碰撞传感器相对轮轴中心安装位置
    collisionSensorX=113,113
    collisionSensorY=-113,113
    [IrSensorParam]
    ;红外传感器开关量个数
    irSensorNum=4
    ;红外传感器相对轮轴中心安装位置
    irSensorX=60,178,178,60
    irSensorY=-178,-60,60,178
    [LidarSensorParam]
    ;激光雷达传感器相对轮轴中心安装位置
    ;******************重点参数
    lidarSensorX=50
    lidarSensorY=0
    ;激光雷达尖角朝向
    ;******************重点参数
    lidarSensorOrientationAngle=90
    ;盲区距离，设置为雷达到载体边缘的最远距离
    ;该值不能小于雷达的机械盲区距离
    lidarBlindRadius=200
    ;设置使用的有效距离，超过有效距离的数据认为是无效数据
    ;该值不能大于雷达的机械可测量距离
    lidarValidDistance=8000
    ;遮挡区域数
    ;******************重点参数
    lidarShelterNum=4
    ;设置遮挡区域，以0度开始，逆时针，依次设置遮挡区域（头跟尾不允许遮挡）
    ;******************重点参数
    lidarShelterBegin=45,135,216,282
    lidarShelterEnd=78,144,225,315
    [NaviPackParam]
    ;载体允许的最大线速度
    maxLineVelocity=500
    ;载体运动时允许的最小线速度
    minLineVelocity=100
    ;载体允许的最大角速度
    maxAngularVelocity=45
    ;载体允许的最小角速度
    minAngularVelocity=0
    ;使能超声波传感器数据用于Navipack
    ;*****************重点参数
    enableUltrasound=0
    ;使能开关量传感器（跌落、碰撞）数据用于Navipack
    ;*****************重点参数
    enableSwitchSensor=0
    ;使能多传感器融合数据用于导航(默认打开)
    enableUsingTotalMapForNavigation=1


##参数说明
见上面文件的备注

    
#更新记录
##Ver 3.0.5

 1. 增加遮挡参数配置 
 2. 使用来打自带陀螺仪 
 3. 限制建图时候的速度
 4. 优化了运动规划
 5. 利用多传感器融合进行避障
 4. sdk接口增加巡逻功能(使用原接口实现)
 5. sdk demo重新布局 
 5. 不建议使用自主建图功能
 6. 手动控制的速度控制优化