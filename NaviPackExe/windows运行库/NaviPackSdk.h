#pragma once
#include "SDKProtocol.h"
namespace NaviPackSdk_Cpp {
	class NaviPackSdk
	{
	public:
		NaviPackSdk();
		~NaviPackSdk();


		/// 获得当前SDK的版本号    Get the current SDK version number
		/// @return 返回NaviPack对象的ID    Return the ID of the NaviPack object
		/// @note 属性值由3个部分组成: 主版本号(Bits 24 ~31), 子版本号(Bits 16 ~ 23), 编译号(Bits 0 ~ 15)    The attribute value consists of three parts: the major version number (Bits 24 ~ 31), the minor version number (Bits 16 ~ 23), the compiled number (Bits 0 ~ 15)
		int  GetSDKVersion();

		/// 获得当前NaviPack的版本号    Get the current NaviPack version number
		/// @return 返回NaviPack对象的ID    Return the ID of the NaviPack object
		/// @note 属性值由3个部分组成: 主版本号(Bits 24 ~31), 子版本号(Bits 16 ~ 23), 编译号(Bits 0 ~ 15)    The attribute value consists of three parts: the major version number (Bits 24 ~ 31), the minor version number (Bits 16 ~ 23), the compiled number (Bits 0 ~ 15)
		int  SetGetNaviPackVersion(int id);

		/// 创建一个NaviPack对象    Create a NaviPack object
		/// @return 返回NaviPack对象的ID >=0为创建成功    If return the ID of the NaviPack object is greater than or equal to 0，the creation succeeded 
		int  Create(ConnectType conType);

		/// 销毁NaviPack对象    Destroy the NaviPack object
		/// @param id 待销毁NaviPack对象的ID    The ID of the NaviPack object to be destroyed
		void  Destroy(int id);

		/// 打开NaviPack对象，该函数将根据不同的参数，来决定打开不同的设备    Open the NaviPack object, the function will be based on different parameters to decide to open a different device
		/// @param[in] id NaviPack对象ID   The ID of the NaviPack object
		/// @param[in] name 接口名称，如果是TCP/UDP协议，则填写对方的IP地址，如果是串口，则填写串口设备名称, Windows下为：////.//COM?, Linux下为/dev/ttyACM?    Interface name, if it is TCP/UDP protocol, then fill in its IP address, if it is serial port, then fill in the serial device name,the serial port in Windows environment is : ////.//COM?, in Linux environment is: /dev/ttyACM?
		/// @param[in] param 接口参数。如果是TCP/UDP协议，则填写对方的端口。如果是COM，则填写接口的波特率。    Interface parameters. If it is TCP / UDP protocol, then fill in the its port. If COM, then fill in the interface baud rate.特别声明的是，在linux系统中,使用的波特率应当是B115200之类的类型，在头文件termios.h中
		/// @return 返回值小于0，表示失败，等于0 表示成功   If the return value is less than 0, it means failure,equal to 0 means that success
		int  Open(int id, const char* name, int param);

		/// 重新打开之前被关闭的NaviPack接口    Reopen the NaviPack interface that was previously closed
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int  Reopen(int id);

		/// 关闭NaviPack接口    Close the NaviPack interface
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int  Close(int id);


		/// 设置从服务端传来消息数据的回调    Set the Callback for the message data from the server
		/// @param[in] id NaviPack 对象ID    The ID of the NaviPack object
		/// @param[in] deviceMsgCb 设备消息回调    Device Nomal message callback
		/// @param[in] errMsgCb	   设备错误消息回调    Device error message callback
		/// @param[in] lidarPackCb 雷达原始数据回调    The lidar raw data callback
		/// @return  返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int  SetCallback(int id, DeviceMsgCallBack deviceMsgCb, ErrorMsgCallBack errMsgCb, MapPackageCallBack mapPackCb, LidarPackageCallBack lidarPackCb);

		
		/// 读取载体的参数    Get the parameters of the carrier (NOT USE)
		/// @param[in] id NaviPack对象ID     The ID of the NaviPack object
		/// @param[out] param 参数指针    Parameter pointer
		/// @note CarrierParam 包含了NaviPack所在载体的最大运动速度，载体最大长宽，NaviPack安装位置，NaviPack安装朝向信息。
		///////CarrierParam contains the maximum speed of the carrier, the maximum length of the carrier, the NaviPack install location, NaviPack install orientation.
		/// @return 返回值小于0，表示失败，等于0 表示成功   If the return value is less than 0, it means failure,equal to 0 means that success
		int  GetCarrierParam(int id, CarrierParam *param);

		/// 设置载体的参数    Set the parameters of the carrier
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[in] param 参数指针    Parameter pointer
		/// @note CarrierParam 包含了NaviPack所在载体的最大运动速度，载体最大长宽，NaviPack安装位置，NaviPack安装朝向信息。
		///////CarrierParam contains the maximum speed of the carrier, the maximum length of the carrier, the NaviPack install location, NaviPack install orientation.
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int  SetCarrierParam(int id, CarrierParam *param);

		/// 读取NaviPack的参数    Get the parameters of the NaviPack (NOT USE)
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[out] param 参数指针    Parameter pointer
		/// @note NaviPackParam 包含了NaviPack系统的一些参数信息，包含是否自动更新地图，栅格大小等一些参数。
		//////NaviPackParam contains some parameters of the NaviPack system, including whether to automatically update the map, raster size and other parameters
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int  GetNaviPackParam(int id, NaviPackParam *param);

		/// 设置载体的参数    Set the parameters of the NaviPack (NOT USE)
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[in] param 参数指针    Parameter pointer
		/// @note NaviPackParam 包含了NaviPack系统的一些参数信息，包含是否自动更新地图，栅格大小等一些参数。
		//////NaviPackParam contains some parameters of the NaviPack system, including whether to automatically update the map, raster size and other parameters
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int   SetNaviPackParam(int id, NaviPackParam *param); 

		/// 设置目标点列表 可以一次设置多个目标点，设置完成后，NaviPack即进入自动导航状态，
		//////Set the target point list,you can set more than one target point.If set is complete, NaviPack will enter the automatic navigation state
		/// 并逐步遍历用户设置的导航点。在运动过程中，NaviPack将自动完成动态路径规划和壁障工作。
		//////Then step through the navigation points that user set. During the process, the NaviPack will automatically complete the dynamic path planning and barrier work.
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[in] position_x 路径点X坐标缓冲区 单位mm    The path point X coordinate buffer; unit: mm
		/// @param[in] position_y 路径点Y坐标缓冲区 单位mm    The path point Y coordinate buffer; unit: mm
		/// @param[in] num 路径点数量   	Number of the path points			
		/// @param[in] phi 到达最后一个点的角度		单位豪弧    The angle to the last point; unit : m arc
		/// @note SetTargets函数，只有在NaviPack完成定位，并载入地图后，才会有效。该函数的参数位置，是指世界坐标系下的位置信息。
		//////This function is valid only after the NaviPack has finished locating and loading the map. The function of the parameter position, refers to the world coordinate system under the location information.
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int   SetTargets(int id, int position_x[], int position_y[], int num, int phi);

		/// 获取当前已经设置的路径点    Gets the currently path point that set
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[out] position_x 路径点X坐标缓冲区    The path point X coordinate buffer
		/// @param[out] position_y 路径点Y坐标缓冲区    The path point Y coordinate buffer
		/// @param[out] num 路径点数量    Number of the path points
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int   GetCurrentPath(int id, int position_x[], int position_y[], int* num);

		/// 像素坐标与世界坐标之间的转换    The conversion from pixel coordinates to world coordinates (NOT USE)
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[in] pixel_y 路径点X坐标缓冲区    The path point X coordinate buffer
		/// @param[in] pixel_y 路径点Y坐标缓冲区    The path point Y coordinate buffer
		/// @param[out] position_x 路径点数量    Number of the path points X
		/// @param[out] position_y 路径点数量    Number of the path points Y
		/// @note 该函数用于将地图的像素坐标，转换为世界坐标系坐标    This function is used to convert the pixel coordinates of the map to world coordinate 
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int PixelToPosition(int id, int pixel_x, int pixel_y, int *position_x, int *position_y);

		/// 世界坐标与像素坐标之间的转换    The conversion from world coordinates to pixel coordinates (NOT USE)
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[out] pixel_y 路径点X坐标缓冲区    The path point X coordinate buffer
		/// @param[out] pixel_y 路径点Y坐标缓冲区    The path point Y coordinate buffer
		/// @param[in] position_x 路径点数量    Number of the path points X
		/// @param[in] position_y 路径点数量    Number of the path points Y
		/// @note 该函数用于将地图的像素坐标，转换为世界坐标系坐标    This function is used to convert the world coordinates of the map to pixel coordinate
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int   PositionToPixel(int id, int position_x, int position_y, int *pixel_x, int *pixel_y);

		/// 控制设备，以线速度v，角速度w运动。    control device move; linear speed: v; angular speed: w 
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[in] v 目标线速度    Target linear speed
		/// @param[in] w 目标角速度    Target angular speed
		/// @note 该函数，将会直接控制设备的运动。如果设备当前正在处于自动导航状态，则会退出自动导航状态。其可以用于手动遥控。
		//////This function will directly control the movement of the device. If the device is currently in an auto-navigation state, the auto-navigation state is exited. It can be used for manual remote control.
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int   SetSpeed(int id, int v, int w);

		/// 控制NaviPack进入自动回充状态    Controls the NaviPack to enter the auto-recharge state
		/// @param[in] id NaviPack对象ID     The ID of the NaviPack object
		/// @note 设备收到该命令后，将自动进入回充状态，NaviPack将控制载体自动运动到充电桩位置，
		//////After the device receives the command, it will enter the auto-recharge state, NaviPack will control the carrier automatically moves to the charging pile position,
		/// 并对准设备接触充电。该函数要求充电座的位置已经通过 SetChargerPosition 设置，或者载体曾经进行过充电（充电时，充电桩的位置，将会自动被记录）
		//////And align the device and then contact and charge. This function requires that the location of the cradle has been set by the function SetChargerPosition, or that the carrier has been charged (the position of the charging pile will be automatically recorded when charging)
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		/// @see SetChargerPosition
		int   AutoCharge(int id);

		/// 设置充电桩的位置    Set the position of the charging pile  (NOT USE)
		/// @param[in] id NaviPack对象ID   The ID of the NaviPack object
		/// @param[in] param 参数指针    Parameter pointer
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int   SetChargerPosition(int id, int position_x, int position_y);

		/// 进入建图模式    Start the mapping mode
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[in] mappingMode 建图模式，0 表示 手动建图，1 表示自动建图    Mapping mode, 0 for manual mapping, 1 for automatic mapping
		/// @note 该函数用于对环境进行建图。当选择手动建图时，则载体的运动，由上位机给出。当选择自动建图时，
		//////This function is used to map the environment. When you choose to manually mapping, the carrier will be controlled by the host computer.
		/// 载体的运动，由NaviPack自动控制，NaviPack将自动控制载体遍历整个环境。
		//////When you choose to automatically map, the carrier will automatic control by the NaviPack, NaviPack will automatically control the carrier traverses the whole environment.
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int   StartMapping(int id, int mappingMode = 0);

		/// 退出建图模式    Exit the mapping mode
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[in] save_flag 是否保存地图    Whether to save the map
		/// :-1,不保存，支持0~7地图文件夹编号，默认地图保存在0号地图文件夹下    
		//////-1, not saved, support map folder number 0 ~ 7, the default map stored in the map folder 0
		/// @note 退出建图模式后，系统进入IDLE状态。该函数的将停止建图，并根据save_flag的值，来决定是否将当前所建之地图，
		//////After exiting the mapping mode, the system enters the IDLE state. The function will stop mapping, and according to the value of save_flag to decide whether to save the current mapthat built,
		/// 保存下来。系统中，最多能够存储8个地图，超过的部分，将会被新的地图覆盖。
		//////The system will store up to 8 maps, and the overriding portion will be overwritten by the new map
		/// @return 返回值小于0，表示失败，大于等于零，表示建图成功，返回值，表示当前的地图保存ID。如未保存，且成功，则返回0。
		//////If the return value is less than 0, it means failure. If it is greater than or equal to 0, it means that the mapping is successful. The return value means the ID of the current saving map . If it is not saved and successful, it returnes 0.
		int   StopMapping(int id, int save_flag = 0);

		/// 读取NaviPack中的地图列表    Get the list of maps in the NaviPack (NOT USE)
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[out] id_buffer 用于存放地图ID的缓冲区。该缓冲区最大为8个。    The buffer used to hold the map ID. The maximum number of buffers is eight.
		/// @note 该函数将读取系统中保存的所有的地图ID。    This function will get the ID of all saved maps in the system
		/// @return 返回值小于0，表示失败，大于或等于零，表示当前系统中的地图个数。    
		//////If the return value is less than 0, it means failure, if it is greater than or equal to zero, it means the current number of maps in the system.
		int   GetMapList(int id, int *id_buffer);

		/// 保存NaviPack正在使用的地图到NaviPack的地图列表中    Save the map that are using to the NaviPack map list
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[out] mapId 保存在地图列表重的ID    The ID that is saved in the map list 
		/// @return 返回值小于0，表示失败    If the return value is less than 0, it means failure
		int   SaveCurrentMapToList(int id, int mapId); (NOT USE)

		/// 指定NaviPack载入指定的地图    Load the specified map (NOT USE)
		/// @param[in] id NaviPack对象ID     The ID of the NaviPack object
		/// @param[in] map_id 指定地图的ID：0~7    The ID of the specified map: 0~7
		/// @note 大部分与地图相关的函数，需要载入地图后，才可以工作。    Most of the map-related functions need to load the map before they can work
		/// @return 返回值小于0，表示失败，等于0 表示成功     If the return value is less than 0, it means failure,equal to 0 means that success
		int   LoadMap(int id, int map_id);

		/// 设置获取当前使用的地图    Set to get the current map
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @note 当连接成功或者需要更新本机地图时，可以调用该函数来通知NaviPack来上传地图
		//////when connect succeed or when you want to update the native map,you can calle this function to notify the NaviPack to upload a map 
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int   SetGetCurrentMap(int id);

		/// 载入本地的地图文件到NaviPack    Load the local map file to the NaviPack
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[in] local_map_path 本地地图文件路径    The local map file path
		/// @param[in] map_id 设置本地地图对应的NaviPack上地图的ID   Sets the ID of the map on the NaviPack that corresponds to the local map 
		/// @note 该函数用于下载上位机本地的地图文件到NaviPack，并将其在NaviPack里面的地图ID，
		//////This function is used to download the local host computer's  map file to the NaviPack and set its map ID in the NaviPack to the map_id value.
		/// 设置成map_id值。大部分与地图相关的函数，需要载入地图后，才可以工作。
		//////Most of the map-related functions need to load the map before they can work
		/// @return 返回值小于0，表示失败，等于0 表示成功     If the return value is less than 0, it means failure,equal to 0 means that success
		int   LoadLocalMap(int id, const char* local_map_path, int map_id);

		/// 保存当前的NaviPack上运行的地图到上位机本地    Save the map that the NaviPack running to the local host computer (NOT USE)
		/// @param[in] id NaviPack对象ID     The ID of the NaviPack object
		/// @param[in] local_map_path 本地地图文件路径    The local map file path
		/// @param[in] picture_flag 决定在保存地图文件的同时，是否将不同图层的数据，转为相应地bmp图片文件。1 表示保存，0表示不保存
		//////Decide whether to save the data of different layers into the corresponding bmp picture file when saving the map file. 1 for saving, 0 for not saving
		/// @note 该函数将当前NaviPack上运行的地图，保存到上位机本地，文件扩展名为.npmap。同时，如果picture_flag设置为1，
		//////This function will save NaviPack's currently running map to local host computer with file extension .npmap. At the same time, if the picture_flag is 
		/// 其将会而外将激光雷达数据图层以及超声波数据图层保存为bmp格式的图片，方便用户查看。
		//////set to 1, it will save the lidar data layer and the ultrasonic data layer as pictures in bmp format for users to view.
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int   SaveMapToLocal(int id, const char * local_map_path, int picture_flag);

		/// 读取NaviPack所建地图的图层数据    Get the NaviPack map data
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[out] map_data MapData结构体，用于保存地图数据    MapData structure, used to save the map data
		/// @param[in] map_type 不同的地图类型。可以是激光雷达图层、超声波图层、碰撞图层等，自定义图层，组合图层等
		//////Different map types. It can be a lidar layer, an ultrasonic layer, a collision layer, a custom layer, a combination layer, etc.
		/// @note 地图数据，在传输过程中将会对数据进行压缩处理，可以选择不同的map_type，返回不同的地图
		//////Map data will be compressed during the transfer, you can choose a different map_type, return to a different map
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int   GetMapLayer(int id, AlgMapData *map_data, int map_type);

		/// 更新NaviPack所使用的地图图层    Update the map layer used by NaviPack
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[in] map_data MapData结构体，用于保存地图数据    MapData structure, used to save the map data
		/// @param[in] map_type 不同的地图类型。可以是激光雷达图层、超声波图层、碰撞图层等，自定义图层，组合图层等
		////////////Different map types. It can be a lidar layer, an ultrasonic layer, a collision layer, a custom layer, a combination layer, etc.
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int   SetMapLayer(int id, AlgMapData *map_data, int map_type);

		/// 位图数据转地图图层    Bitmap data to map layer (NOT USE)
		/// @param[in] bitmap 8bit 灰度位图数据缓冲区，其并非一个完成的bmp图片文件，而是图像文件中实际的图像数据。
		//////8bit grayscale bitmap data buffer, which is not a complete bmp picture file, but the actual image file image data
		/// @param[in] w 位图数据宽度   The bitmap data width
		/// @param[in] h 位图数据高度   The bitmap data height 
		/// @param[out] map 生成的图层数据    The generated layer data
		/// @note Bitmap数据为8位灰度地图数据，128表示未知，0，表示障碍物，255 表示空地。该函数申请了新的内存，
		/////Bitmap data is 8-bit grayscale map data, 128 is unknown, 0 is an obstacle, and 255 is open space.The function apply for new memory, 
		/// 并将其填充后返回给用户，用户使用完成后，需要使用主动释放内存。
		//////and fill it back to the user, the user is finished, you need to use the active release memory.
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int   BitmapToMapLayer(unsigned char *bitmap, AlgMapData **map);

		/// 位图文件转地图图层    Bitmap file to map layer (NOT USE)
		/// @param[in] file_path 生成的 8bit 灰度位图bmp文件所在路径。   The resulting 8bit bitmap file path
		/// @param[out] map 图层数据缓冲区    Layer data buffer
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int   BmpFileToMapLayer(const char * file_path, AlgMapData **map);

		/// 地图图层转位图文件    Map layer to the map file (NOT USE)
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[in] map 图层数据缓冲区    Layer data buffer
		/// @param[in] file_path 生成的 8bit 灰度位图bmp文件所在路径。    The resulting 8bit bitmap file path
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int   MapLayerToBmpFile(AlgMapData *map, char * file_path);

		///读取本地存储的地图图层数据    Read the local map layer data   (NOT USE)
		/// @param[in] fileName 图层数据文件    The layer data file
		/// @param[out] map 生成的图层数据    The generated layer data
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int   ReadLocalMaplayer(const char* fileName, AlgMapData **map);

		/// 读取传感器实时数据    Get sensor real-time data
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[in] sensorType 传感器类型    the sensor type
		/// correlative enum sensorType
		/// @param[out] sensor_data SensorData 结构体，用于存储传感器相对载体坐标数据及载体全局位姿态
		//////SensorData structure, used to store the sensor relative to the carrier coordinate data and carrier global position pose
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int   GetSensorData(int id, AlgSensorData *sensor_data, int sensorType);

		/// 读取系统状态信息    Get the system status
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[out] status StatusRegister 结构体，用于存储数据    StatusRegister structure,used to store data
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int   GetStatus(int id, AlgStatusRegister *status);

		/// 强制NaviPack重新进行初始定位    Force NaviPack to re-initialize
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[out] status StatusRegister 结构体，用于存储数据    StatusRegister structure,used to store data
		/// @note 该功能正确执行的前提是，地图已经载入    This function works correctly if the map is already loaded
		/// @return 返回值小于0，表示失败，等于0 表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int   InitLocation(int id);

		/// 设置WIFI参数     Set the WIFI parameter 
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[in] ssid WIFI SSID    
		/// @param[in] password WIFI 密码   WIFI password
		/// @note 设置了SSID和WIFI之后，系统将主动建立TCP/UDP服务器（现有的硬件需要插入USB WIFI 网卡才可以使用WIFI连接）。
		//////After set the SSID and WIFI, the system will automatically establish a TCP/UDP server (the existing hardware need to insert a USB WIFI card)
		/// @return 返回值小于0，表示失败，等于0 表示成功   If the return value is less than 0, it means failure,equal to 0 means that success
		int   SetWiFiParam(int id, const char * ssid, const char * password);

		/// 搜索网络中的设备    Search the devices on the network
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[out] device_list 设备名称列表，用于保存设备连接名称和参数，如果是网络连接，则名称格式为ip:port,如192.168.0.2:7896，如果是串口，则为port:bandrate，如 /dev/ttyACM0:115200。各设备名称之间，采用;隔开。
		//////Device name list,it used to save the device connection name and parameters, if the network connection, the name format is ip: port, such as 192.168.0.2:7896, if it is a serial port, the port: bandrate, such as /dev/ttyACM0:115200 . Between the device names, use;separated.
		/// @param[in] timeout 超时值   
		/// @note device_list的内存由用户负责创建和销毁，最大内存大小为256。对于网络连接的设备来说，本功能，是通过发送UDP广播来实现的，因此，需要被搜索的设备和搜索设备在同一个网段。对于串口连接的设备来说，在Android/linux系统里面，将搜索/dev/ttyACM开头的所有设备，并尝试连接和通讯，在windows系统里面，将主动搜索所有系统串口，并尝试连接和通讯。
		//////The memory for device_list is created and destroyed by the user, with a maximum memory size of 256. For a network-connected device, this function is achieved by sending UDP broadcasts. Therefore, the device to be searched and the search device need to be in the same network segment.
		//////For the device connected to the serial port, in the Android / linux system, which will search /dev/ttyACM at the beginning of all the equipment and try to connect and communicate. In the windows system it will automatically search all system serial port and try to connect and communicate. 
		/// @return 返回值小于0，表示失败，大于等于零，表示搜索到的的NaviPack设备的数量。     If the return value is less than 0, it means failure,If it is greater than or equal to 0, it means the number of NaviPack devices searched.
		int   SearchDevice(int id, char *device_list, int timeout);

		/// 检查通讯是否正常    Check the connection (NOT USE)
		/// @return 返回值小于0，表示失败，等于0表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int   CheckConnection();

		/// 升级NaviPack程序    Upgrade the NaviPack program
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[in] fileName 要传输的文件名    The name of the file to transfer
		/// @return 返回值小于0，表示失败，等于0表示成功     If the return value is less than 0, it means failure,equal to 0 means that success
		int   UpdateNaviPackFile(int id, const char* fileName);

		/// 设置由控制器厂商自定义的协议数据    Set the self protocol data  by the controller manufacturer
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[in] fileName 要传输的数据缓存区    The data buffer to transfer
		/// @return 返回值小于0，表示失败，等于0表示成功    If the return value is less than 0, it means failure,equal to 0 means that success
		int   SetSelfStream(int id, char* buf, int bufLen);

		/// 设置NaviPack打包当前的地图并发送到本地保存    Set the NaviPack to save the current map and send it to the local
		/// @param[in] id NaviPack对象ID    The ID of the NaviPack object
		/// @param[in] id filePath 要保存的文件的路径    The path to the file to be saved
		/// @param[in] id fileName 要保存的文件的名称    The name of the file to be saved
		/// @return 返回值小于0./N，表示失败，等于0表示成功     If the return value is less than 0, it means failure,equal to 0 means that success
		int   SetSaveMap(int id, const char* filePath, const char* fileName);

		/// 发送本地的文件到NaviPack    Send local files to NaviPack
		/// @param[in] id		NaviPack对象ID    The ID of the NaviPack object
		/// @param[in] type		发送的文件类型    Send the file type
		/// @param[in] filePath 要发送的文件的路径   The path to the file to send
		/// @param[in] fileName 要发送的文件的名称   The name of the file to send
		/// @return 返回值小于0，表示失败，等于0表示成功     If the return value is less than 0, it means failure,equal to 0 means that success
		int   SendFile(int id, int type, const char* filePath, const char* fileName);

		/// 更改NaviPack套件的运行模式    Change the operating mode of the NaviPack 
		/// @param[in] id		NaviPack对象ID    The ID of the NaviPack object
		/// @param[in] mode		运行模式 0 表示默认模式也是导航模式 1 表示雷达数据转的发模式 
		//////Operation mode .0 means that the default mode, that is, navigation mode. 1 means that the radar data transmission mode
		/// @return 返回值小于0，表示失败，等于0表示成功     If the return value is less than 0, it means failure,equal to 0 means that success
		int   SetChangeNaviPackMode(int id, int mode);

		/// 手动进行IMU的矫正    Manual IMU calibration    
		/// @param[in] id		NaviPack对象ID        The ID of the NaviPack object
		/// @return 返回值小于0，表示失败，等于0表示成功         If the return value is less than 0, it means failure,equal to 0 means that success
		int ImuCalibrate(int id);

		/// 发送自己的传感器数据到NaviPack    Send your own sensor data to the NaviPack
		/// @param[in] id				NaviPack对象ID        The ID of the NaviPack object
		/// @param[in] sensorData		传感器数据的统一格式    A unified format for sensor data
		/// @return 返回值小于0，表示失败，等于0表示成功     If the return value is less than 0, it means failure,equal to 0 means that success    
		int SendUnifiedSensorInfo(int id, UnifiedSensorInfo sensorData);
	private:
		void Init();
	};



}