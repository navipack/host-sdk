#pragma once
#include "SDKProtocol.h"
namespace NaviPackSdk_Cpp {
	class NaviPackSdk
	{
	public:
		NaviPackSdk();
		~NaviPackSdk();


		/// 获得当前SDK的版本号
		/// @return 返回NaviPack对象的ID
		/// @note 属性值由3个部分组成: 主版本号(Bits 24 ~31), 子版本号(Bits 16 ~ 23), 编译号(Bits 0 ~ 15)
		 int  GetSDKVersion();

		/// 获得当前NaviPack的版本号
		/// @return 返回NaviPack对象的ID
		/// @note 属性值由3个部分组成: 主版本号(Bits 24 ~31), 子版本号(Bits 16 ~ 23), 编译号(Bits 0 ~ 15)
		 int  SetGetNaviPackVersion(int id);

		/// 创建一个NaviPack对象
		/// @return 返回NaviPack对象的ID >=0为创建成功
		 int  Create(ConnectType conType);

		/// 销毁NaviPack对象
		/// @param id 待销毁NaviPack对象的ID
		 void  Destroy(int id);

		/// 打开NaviPack对象，该函数将根据不同的参数，来决定打开不同的设备
		/// @param[in] id NaviPack对象ID
		/// @param[in] name 接口名称，如果是TCP/UDP协议，则填写对方的IP地址，如果是串口，则填写串口设备名称, Windows下为：////.//COM?, Linux下为/dev/ttyACM?
		/// @param[in] param 接口参数。如果是TCP/UDP协议，则填写对方的端口。如果是COM，则填写接口的波特率。
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int  Open(int id, const char* name, int param);

		/// 重新打开之前被关闭的NaviPack接口
		/// @param[in] id NaviPack对象ID
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int  Reopen(int id);

		/// 关闭NaviPack接口
		/// @param[in] id NaviPack对象ID
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int  Close(int id);


		/// 设置从服务端传来消息数据的回调
		/// @param[in] id NaviPack 对象ID
		/// @param[in] deviceMsgCb 设备消息回调
		/// @param[in] errMsgCb	   设备错误消息回调
		/// @param[in] lidarPackCb 雷达原始数据回调
		/// @return  返回值小于0，表示失败，等于0 表示成功
		 int  SetCallback(int id, DeviceMsgCallBack deviceMsgCb, ErrorMsgCallBack errMsgCb, MapPackageCallBack mapPackCb, LidarPackageCallBack lidarPackCb);

		/// 读取载体的参数
		/// @param[in] id NaviPack对象ID
		/// @param[out] param 参数指针
		/// @note CarrierParam 包含了NaviPack所在载体的最大运动速度，载体最大长宽，NaviPack安装位置，NaviPack安装朝向信息。
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int  GetCarrierParam(int id, CarrierParam *param);

		/// 设置载体的参数
		/// @param[in] id NaviPack对象ID
		/// @param[in] param 参数指针
		/// @note CarrierParam 包含了NaviPack所在载体的最大运动速度，载体最大长宽，NaviPack安装位置，NaviPack安装朝向信息。
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int  SetCarrierParam(int id, CarrierParam *param);

		/// 读取NaviPack的参数
		/// @param[in] id NaviPack对象ID
		/// @param[out] param 参数指针
		/// @note NaviPackParam 包含了NaviPack系统的一些参数信息，包含是否自动更新地图，栅格大小等一些参数。
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int  GetNaviPackParam(int id, NaviPackParam *param);

		/// 设置载体的参数
		/// @param[in] id NaviPack对象ID
		/// @param[in] param 参数指针
		/// @note NaviPackParam 包含了NaviPack系统的一些参数信息，包含是否自动更新地图，栅格大小等一些参数。
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   SetNaviPackParam(int id, NaviPackParam *param);

		/// 设置目标点列表 可以一次设置多个目标点，设置完成后，NaviPack即进入自动导航状态，
		/// 并逐步遍历用户设置的导航点。在运动过程中，NaviPack将自动完成动态路径规划和壁障工作。
		/// @param[in] id NaviPack对象ID
		/// @param[in] position_x 路径点X坐标缓冲区 单位mm
		/// @param[in] position_y 路径点Y坐标缓冲区 单位mm
		/// @param[in] num 路径点数量				
		/// @param[in] phi 到达最后一个点的角度		单位豪弧
		/// @note SetTargets函数，只有在NaviPack完成定位，并载入地图后，才会有效。该函数的参数位置，是指世界坐标系下的位置信息。
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   SetTargets(int id, int position_x[], int position_y[], int num, int phi);

		/// 获取当前已经设置的路径点
		/// @param[in] id NaviPack对象ID
		/// @param[out] position_x 路径点X坐标缓冲区
		/// @param[out] position_y 路径点Y坐标缓冲区
		/// @param[out] num 路径点数量
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   GetCurrentPath(int id, int position_x[], int position_y[], int* num);

		/// 像素坐标与世界坐标之间的转换
		/// @param[in] id NaviPack对象ID
		/// @param[in] pixel_y 路径点X坐标缓冲区
		/// @param[in] pixel_y 路径点Y坐标缓冲区
		/// @param[out] position_x 路径点数量
		/// @param[out] position_y 路径点数量
		/// @note 该函数用于将地图的像素坐标，转换为世界坐标系坐标
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   PixelToPosition(int id, int pixel_x, int pixel_y, int *position_x, int *position_y);

		/// 世界坐标与像素坐标之间的转换
		/// @param[in] id NaviPack对象ID
		/// @param[out] pixel_y 路径点X坐标缓冲区
		/// @param[out] pixel_y 路径点Y坐标缓冲区
		/// @param[in] position_x 路径点数量
		/// @param[in] position_y 路径点数量
		/// @note 该函数用于将地图的像素坐标，转换为世界坐标系坐标
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   PositionToPixel(int id, int position_x, int position_y, int *pixel_x, int *pixel_y);

		/// 控制设备，以线速度v，角速度w运动。
		/// @param[in] id NaviPack对象ID
		/// @param[in] v 目标线速度
		/// @param[in] w 目标角速度
		/// @note 该函数，将会直接控制设备的运动。如果设备当前正在处于自动导航状态，则会退出自动导航状态。其可以用于手动遥控。
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   SetSpeed(int id, int v, int w);

		/// 控制NaviPack进入自动回充状态
		/// @param[in] id NaviPack对象ID
		/// @note 设备收到该命令后，将自动进入回充状态，NaviPack将控制载体自动运动到充电桩位置，
		/// 并对准设备接触充电。该函数要求充电座的位置已经通过 SetChargerPosition 设置，或者载体曾经进行过充电（充电时，充电桩的位置，将会自动被记录）
		/// @return 返回值小于0，表示失败，等于0 表示成功
		/// @see SetChargerPosition
		 int   AutoCharge(int id);

		/// 设置充电桩的位置
		/// @param[in] id NaviPack对象ID
		/// @param[in] param 参数指针
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   SetChargerPosition(int id, int position_x, int position_y);

		/// 进入建图模式
		/// @param[in] id NaviPack对象ID
		/// @param[in] mappingMode 建图模式，0 表示 手动建图，1 表示自动建图
		/// @note 该函数用于对环境进行建图。当选择手动建图时，则载体的运动，由上位机给出。当选择自动建图时，
		/// 载体的运动，由NaviPack自动控制，NaviPack将自动控制载体遍历整个环境。
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   StartMapping(int id, int mappingMode = 0);

		/// 退出建图模式
		/// @param[in] id NaviPack对象ID
		/// @param[in] save_flag 是否保存地图
		/// :-1,不保存，支持0~7地图文件夹编号，默认地图保存在0号地图文件夹下
		/// @note 退出建图模式后，系统进入IDLE状态。该函数的将停止建图，并根据save_flag的值，来决定是否将当前所建之地图，
		/// 保存下来。系统中，最多能够存储8个地图，超过的部分，将会被新的地图覆盖。
		/// @return 返回值小于0，表示失败，大于等于零，表示建图成功，返回值，表示当前的地图保存ID。如未保存，且成功，则返回0。
		 int   StopMapping(int id, int save_flag = 0);

		/// 读取NaviPack中的地图列表
		/// @param[in] id NaviPack对象ID
		/// @param[out] id_buffer 用于存放地图ID的缓冲区。该缓冲区最大为8个。
		/// @note 该函数将读取系统中保存的所有的地图ID。
		/// @return 返回值小于0，表示失败，大于或等于零，表示当前系统中的地图个数。
		 int   GetMapList(int id, int *id_buffer);

		/// 保存NaviPack正在使用的地图到NaviPack的地图列表中
		/// @param[in] id NaviPack对象ID
		/// @param[out] mapId 保存在地图列表重的ID
		/// @return 返回值小于0，表示失败
		 int   SaveCurrentMapToList(int id, int mapId);

		/// 指定NaviPack载入指定的地图
		/// @param[in] id NaviPack对象ID
		/// @param[in] map_id 指定地图的ID：0~7
		/// @note 大部分与地图相关的函数，需要载入地图后，才可以工作。
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   LoadMap(int id, int map_id);

		/// 设置获取当前使用的地图
		/// @param[in] id NaviPack对象ID
		/// @note 当连接成功或者需要更新本机地图时，可以调用该函数来通知NaviPack来上传地图
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   SetGetCurrentMap(int id);

		/// 载入本地的地图文件到NaviPack
		/// @param[in] id NaviPack对象ID
		/// @param[in] local_map_path 本地地图文件路径
		/// @param[in] map_id 设置本地地图对应的NaviPack上地图的ID
		/// @note 该函数用于下载上位机本地的地图文件到NaviPack，并将其在NaviPack里面的地图ID，
		/// 设置成map_id值。大部分与地图相关的函数，需要载入地图后，才可以工作。
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   LoadLocalMap(int id, const char* local_map_path, int map_id);

		/// 保存当前的NaviPack上运行的地图到上位机本地
		/// @param[in] id NaviPack对象ID
		/// @param[in] local_map_path 本地地图文件路径
		/// @param[in] picture_flag 决定在保存地图文件的同时，是否将不同图层的数据，转为相应地bmp图片文件。1 表示保存，0表示不保存
		/// @note 该函数将当前NaviPack上运行的地图，保存到上位机本地，文件扩展名为.npmap。同时，如果picture_flag设置为1，
		/// 其将会而外将激光雷达数据图层以及超声波数据图层保存为bmp格式的图片，方便用户查看。
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   SaveMapToLocal(int id, const char * local_map_path, int picture_flag);

		/// 读取NaviPack所建地图的图层数据
		/// @param[in] id NaviPack对象ID
		/// @param[out] map_data MapData结构体，用于保存地图数据
		/// @param[in] map_type 不同的地图类型。可以是激光雷达图层、超声波图层、碰撞图层等，自定义图层，组合图层等
		/// @note 地图数据，在传输过程中将会对数据进行压缩处理，可以选择不同的map_type，返回不同的地图
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   GetMapLayer(int id, AlgMapData *map_data, int map_type);

		/// 更新NaviPack所使用的地图图层
		/// @param[in] id NaviPack对象ID
		/// @param[in] map_data MapData结构体，用于保存地图数据
		/// @param[in] map_type 不同的地图类型。可以是激光雷达图层、超声波图层、碰撞图层等，自定义图层，组合图层等
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   SetMapLayer(int id, AlgMapData *map_data, int map_type);

		/// 位图数据转地图图层
		/// @param[in] bitmap 8bit 灰度位图数据缓冲区，其并非一个完成的bmp图片文件，而是图像文件中实际的图像数据。
		/// @param[in] w 位图数据宽度
		/// @param[in] h 位图数据高度
		/// @param[out] map 生成的图层数据
		/// @note Bitmap数据为8位灰度地图数据，128表示未知，0，表示障碍物，255 表示空地。该函数申请了新的内存，
		/// 并将其填充后返回给用户，用户使用完成后，需要使用主动释放内存。
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   BitmapToMapLayer(unsigned char *bitmap, AlgMapData **map);

		/// 位图文件转地图图层
		/// @param[in] file_path 生成的 8bit 灰度位图bmp文件所在路径。
		/// @param[out] map 图层数据缓冲区
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   BmpFileToMapLayer(const char * file_path, AlgMapData **map);

		/// 地图图层转位图文件
		/// @param[in] id NaviPack对象ID
		/// @param[in] map 图层数据缓冲区
		/// @param[in] file_path 生成的 8bit 灰度位图bmp文件所在路径。
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   MapLayerToBmpFile(AlgMapData *map, char * file_path);

		///读取本地存储的地图图层数据
		/// @param[in] fileName 图层数据文件
		/// @param[out] map 生成的图层数据
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   ReadLocalMaplayer(const char* fileName, AlgMapData **map);

		/// 读取传感器实时数据
		/// @param[in] id NaviPack对象ID
		/// @param[in] sensorType 传感器类型
		/// correlative enum sensorType
		/// @param[out] sensor_data SensorData 结构体，用于存储传感器相对载体坐标数据及载体全局位姿态
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   GetSensorData(int id, AlgSensorData *sensor_data, int sensorType);

		/// 读取系统状态信息
		/// @param[in] id NaviPack对象ID
		/// @param[out] status StatusRegister 结构体，用于存储数据
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   GetStatus(int id, AlgStatusRegister *status);

		/// 强制NaviPack重新进行初始定位
		/// @param[in] id NaviPack对象ID
		/// @param[out] status StatusRegister 结构体，用于存储数据
		/// @note 该功能正确执行的前提是，地图已经载入
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   InitLocation(int id);

		/// 设置WIFI参数
		/// @param[in] id NaviPack对象ID
		/// @param[in] ssid WIFI SSID
		/// @param[in] password WIFI 密码
		/// @note 设置了SSID和WIFI之后，系统将主动建立TCP/UDP服务器（现有的硬件需要插入USB WIFI 网卡才可以使用WIFI连接）。
		/// @return 返回值小于0，表示失败，等于0 表示成功
		 int   SetWiFiParam(int id, const char * ssid, const char * password);

		/// 搜索网络中的设备
		/// @param[in] id NaviPack对象ID
		/// @param[out] device_list 设备名称列表，用于保存设备连接名称和参数，如果是网络连接，则名称格式为ip:port,如192.168.0.2:7896，如果是串口，则为port:bandrate，如 /dev/ttyACM0:115200。各设备名称之间，采用;隔开。
		/// @param[in] timeout 超时值
		/// @note device_list的内存由用户负责创建和销毁，最大内存大小为256。对于网络连接的设备来说，本功能，是通过发送UDP广播来实现的，因此，需要被搜索的设备和搜索设备在同一个网段。对于串口连接的设备来说，在Android/linux系统里面，将搜索/dev/ttyACM开头的所有设备，并尝试连接和通讯，在windows系统里面，将主动搜索所有系统串口，并尝试连接和通讯。
		/// @return 返回值小于0，表示失败，大于等于零，表示搜索到的的NaviPack设备的数量。
		 int   SearchDevice(int id, char *device_list, int timeout);

		/// 检查通讯是否正常
		/// @return 返回值小于0，表示失败，等于0表示成功
		 int   CheckConnection();

		/// 升级NaviPack程序
		/// @param[in] id NaviPack对象ID
		/// @param[in] fileName 要传输的文件名
		/// @return 返回值小于0，表示失败，等于0表示成功
		 int   UpdateNaviPackFile(int id, const char* fileName);

		/// 设置由控制器厂商自定义的协议数据
		/// @param[in] id NaviPack对象ID
		/// @param[in] fileName 要传输的数据缓存区
		/// @return 返回值小于0，表示失败，等于0表示成功
		 int   SetSelfStream(int id, char* buf, int bufLen);

		/// 设置NaviPack打包当前的地图并发送到本地保存
		/// @param[in] id NaviPack对象ID
		/// @param[in] id filePath 要保存的文件的路径
		/// @param[in] id fileName 要保存的文件的名称
		/// @return 返回值小于0./N，表示失败，等于0表示成功
		 int   SetSaveMap(int id, const char* filePath, const char* fileName);

		/// 发送本地的文件到NaviPack
		/// @param[in] id		NaviPack对象ID
		/// @param[in] type		发送的文件类型
		/// @param[in] filePath 要发送的文件的路径
		/// @param[in] fileName 要发送的文件的名称
		/// @return 返回值小于0，表示失败，等于0表示成功
		 int   SendFile(int id, int type, const char* filePath, const char* fileName);

		/// 更改NaviPack套件的运行模式
		/// @param[in] id		NaviPack对象ID
		/// @param[in] mode		运行模式 0 表示默认模式也是导航模式 1 表示雷达数据转的发模式
		/// @return 返回值小于0，表示失败，等于0表示成功
		 int   SetChangeNaviPackMode(int id, int mode);

		 /// 手动进行IMU的矫正
		 /// @param[in] id		NaviPack对象ID
		 /// @return 返回值小于0，表示失败，等于0表示成功
		 int ImuCalibrate(int id);

		 /// 发送自己的传感器数据到NaviPack
		 /// @param[in] id				NaviPack对象ID
		 /// @param[in] sensorData		传感器数据的统一格式
		 /// @return 返回值小于0，表示失败，等于0表示成功
		 int SendUnifiedSensorInfo(int id, UnifiedSensorInfo sensorData);
		private:
			void Init();
	};



}
