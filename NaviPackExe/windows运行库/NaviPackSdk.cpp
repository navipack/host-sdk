#include "NaviPackSdk.h"
#include <windows.h>

#define DLLNAME "NaviPackSdk.dll"

using namespace NaviPackSdk_Cpp;


typedef int(WINAPI *GetSDKVersionFuncDef)();
typedef int(WINAPI *SetGetNaviPackVersionFuncDef)(int);
typedef int(WINAPI *CreateFuncDef)(ConnectType);
typedef int(WINAPI *DestroyFuncDef)(int);
typedef int(WINAPI *OpenFuncDef)(int,const char*,int);
typedef int(WINAPI *ReopenFuncDef)(int);
typedef int(WINAPI *CloseFuncDef)(int);
typedef int(WINAPI *SetCallbackFuncDef)(int,DeviceMsgCallBack, ErrorMsgCallBack, MapPackageCallBack, LidarPackageCallBack);
typedef int(WINAPI *GetCarrierParamFuncDef)(int, CarrierParam*);
typedef int(WINAPI *SetCarrierParamFuncDef)(int, CarrierParam*);
typedef int(WINAPI *GetNaviPackParamFuncDef)(int, NaviPackParam*);
typedef int(WINAPI *SetNaviPackParamFuncDef)(int, NaviPackParam*);
typedef int(WINAPI *SetTargetsFuncDef)(int,int*,int*,int,int);
typedef int(WINAPI *GetCurrentPathFuncDef)(int,int*,int*,int*);
typedef int(WINAPI *PixelToPositionFuncDef)(int,int,int,int*,int*);
typedef int(WINAPI *PositionToPixelFuncDef)(int, int, int, int*, int*);
typedef int(WINAPI *SetSpeedFuncDef)(int,int,int);
typedef int(WINAPI *AutoChargeFuncDef)(int);
typedef int(WINAPI *SetChargerPositionFuncDef)(int,int,int);
typedef int(WINAPI *StartMappingFuncDef)(int,int);
typedef int(WINAPI *StopMappingFuncDef)(int,int);
typedef int(WINAPI *GetMapListFuncDef)(int,int*);
typedef int(WINAPI *SaveCurrentMapToListFuncDef)(int,int);
typedef int(WINAPI *LoadMapFuncDef)(int,int);
typedef int(WINAPI *SetGetCurrentMapFuncDef)(int);
typedef int(WINAPI *LoadLocalMapFuncDef)(int,const char*,int);
typedef int(WINAPI *SaveMapToLocalFuncDef)(int,const char*,int);
typedef int(WINAPI *GetMapLayerFuncDef)(int, AlgMapData*,int);
typedef int(WINAPI *SetMapLayerFuncDef)(int,AlgMapData*,int);
typedef int(WINAPI *BitmapToMapLayerFuncDef)(unsigned char*,AlgMapData **);
typedef int(WINAPI *BmpFileToMapLayerFuncDef)(const char *, AlgMapData **);
typedef int(WINAPI *MapLayerToBmpFileFuncDef)(AlgMapData *, char * );
typedef int(WINAPI *ReadLocalMaplayerFuncDef)(const char* , AlgMapData **);
typedef int(WINAPI *GetSensorDataFuncDef)(int id, AlgSensorData *, int );
typedef int(WINAPI *GetStatusFuncDef)(int, AlgStatusRegister *);
typedef int(WINAPI *InitLocationFuncDef)(int);
typedef int(WINAPI *SetWiFiParamFuncDef)(int, const char *, const char *);
typedef int(WINAPI *SearchDeviceFuncDef)(int , char *, int );
typedef int(WINAPI *CheckConnectionFuncDef)();
typedef int(WINAPI *UpdateNaviPackFileFuncDef)(int, const char*);
typedef int(WINAPI *SetSelfStreamFuncDef)(int, char* , int);
typedef int(WINAPI *SetSaveMapFuncDef)(int, const char* , const char*);
typedef int(WINAPI *SendFileFuncDef)(int, int , const char* , const char*);
typedef int(WINAPI *SetChangeNaviPackModeFuncDef)(int,int);
typedef int(WINAPI *ImuCelibrateFuncDef)(int);


static HINSTANCE mDllInst = NULL;
static GetSDKVersionFuncDef GetSDKVersionFunc = NULL;
static SetGetNaviPackVersionFuncDef SetGetNaviPackVersionFunc = NULL;
static CreateFuncDef CreateFunc = NULL;
static DestroyFuncDef DestroyFunc = NULL;
static OpenFuncDef OpenFunc = NULL;
static ReopenFuncDef ReopenFunc = NULL;
static CloseFuncDef CloseFunc = NULL;
static SetCallbackFuncDef SetCallbackFunc = NULL;
static GetCarrierParamFuncDef GetCarrierParamFunc = NULL;
static SetCarrierParamFuncDef SetCarrierParamFunc = NULL;
static GetNaviPackParamFuncDef GetNaviPackParamFunc = NULL;
static SetNaviPackParamFuncDef SetNaviPackParamFunc = NULL;
static SetTargetsFuncDef  SetTargetsFunc= NULL;
static GetCurrentPathFuncDef GetCurrentPathFunc = NULL;
static PixelToPositionFuncDef  PixelToPositionFunc = NULL;
static PositionToPixelFuncDef PositionToPixelFunc = NULL;
static SetSpeedFuncDef  SetSpeedFunc = NULL;
static AutoChargeFuncDef  AutoChargeFunc = NULL;
static SetChargerPositionFuncDef SetChargerPositionFunc = NULL;
static StartMappingFuncDef StartMappingFunc = NULL;
static StopMappingFuncDef StopMappingFunc = NULL;
static GetMapListFuncDef  GetMapListFunc = NULL;
static SaveCurrentMapToListFuncDef  SaveCurrentMapToListFunc = NULL;
static LoadMapFuncDef LoadMapFunc = NULL;
static SetGetCurrentMapFuncDef SetGetCurrentMapFunc = NULL;
static LoadLocalMapFuncDef LoadLocalMapFunc = NULL;
static SaveMapToLocalFuncDef SaveMapToLocalFunc = NULL;
static GetMapLayerFuncDef  GetMapLayerFunc = NULL;
static SetMapLayerFuncDef SetMapLayerFunc = NULL;
static BitmapToMapLayerFuncDef BitmapToMapLayerFunc = NULL;
static BmpFileToMapLayerFuncDef BmpFileToMapLayerFunc = NULL;
static MapLayerToBmpFileFuncDef MapLayerToBmpFileFunc = NULL;
static ReadLocalMaplayerFuncDef ReadLocalMaplayerFunc = NULL;
static GetSensorDataFuncDef GetSensorDataFunc = NULL;
static GetStatusFuncDef GetStatusFunc = NULL;
static InitLocationFuncDef InitLocationFunc = NULL;
static SetWiFiParamFuncDef SetWiFiParamFunc = NULL;
static SearchDeviceFuncDef SearchDeviceFunc = NULL;
static CheckConnectionFuncDef CheckConnectionFunc = NULL;
static UpdateNaviPackFileFuncDef UpdateNaviPackFileFunc = NULL;
static SetSelfStreamFuncDef  SetSelfStreamFunc = NULL;
static SetSaveMapFuncDef  SetSaveMapFunc = NULL;
static SendFileFuncDef  SendFileFunc = NULL;
static SetChangeNaviPackModeFuncDef  SetChangeNaviPackModeFunc = NULL;
static ImuCelibrateFuncDef ImuCelibrateFunc = NULL;


NaviPackSdk::NaviPackSdk()
{
	Init();
}


NaviPackSdk::~NaviPackSdk()
{
}



void NaviPackSdk_Cpp::NaviPackSdk::Init()
{
	if (mDllInst == NULL)
	{
		mDllInst = LoadLibrary(DLLNAME);
		if (mDllInst)
		{
			if (!(GetSDKVersionFunc = (GetSDKVersionFuncDef)GetProcAddress(mDllInst, "GetSDKVersion"))) return ;
			if (!(SetGetNaviPackVersionFunc = (SetGetNaviPackVersionFuncDef)GetProcAddress(mDllInst, "SetGetNaviPackVersion"))) return;
			if (!(CreateFunc = (CreateFuncDef)GetProcAddress(mDllInst, "Create"))) return;
			if (!(DestroyFunc = (DestroyFuncDef)GetProcAddress(mDllInst, "Destroy"))) return;
			if (!(OpenFunc = (OpenFuncDef)GetProcAddress(mDllInst, "Open"))) return;
			if (!(ReopenFunc = (ReopenFuncDef)GetProcAddress(mDllInst, "Reopen"))) return;
			if (!(CloseFunc = (CloseFuncDef)GetProcAddress(mDllInst, "Close"))) return;
			if (!(SetCallbackFunc = (SetCallbackFuncDef)GetProcAddress(mDllInst, "SetCallback"))) return;
			if (!(GetCarrierParamFunc = (GetCarrierParamFuncDef)GetProcAddress(mDllInst, "GetCarrierParam"))) return;
			if (!(SetCarrierParamFunc = (SetCarrierParamFuncDef)GetProcAddress(mDllInst, "SetCarrierParam"))) return;
			if (!(GetNaviPackParamFunc = (GetNaviPackParamFuncDef)GetProcAddress(mDllInst, "GetNaviPackParam"))) return;
			if (!(SetNaviPackParamFunc = (SetNaviPackParamFuncDef)GetProcAddress(mDllInst, "SetNaviPackParam"))) return;
			if (!(SetTargetsFunc = (SetTargetsFuncDef)GetProcAddress(mDllInst, "SetTargets"))) return;
			if (!(GetCurrentPathFunc = (GetCurrentPathFuncDef)GetProcAddress(mDllInst, "GetCurrentPath"))) return;
			if (!(PixelToPositionFunc = (PixelToPositionFuncDef)GetProcAddress(mDllInst, "PixelToPosition"))) return;
			if (!(PositionToPixelFunc = (PositionToPixelFuncDef)GetProcAddress(mDllInst, "PositionToPixel"))) return;
			if (!(SetSpeedFunc = (SetSpeedFuncDef)GetProcAddress(mDllInst, "SetSpeed"))) return;
			if (!(AutoChargeFunc = (AutoChargeFuncDef)GetProcAddress(mDllInst, "AutoCharge"))) return;
			if (!(SetChargerPositionFunc = (SetChargerPositionFuncDef)GetProcAddress(mDllInst, "SetChargerPosition"))) return;
			if (!(StartMappingFunc = (StartMappingFuncDef)GetProcAddress(mDllInst, "StartMapping"))) return;
			if (!(StopMappingFunc = (StopMappingFuncDef)GetProcAddress(mDllInst, "StopMapping"))) return;
			if (!(GetMapListFunc = (GetMapListFuncDef)GetProcAddress(mDllInst, "GetMapList"))) return;
			if (!(SaveCurrentMapToListFunc = (SaveCurrentMapToListFuncDef)GetProcAddress(mDllInst, "SaveCurrentMapToList"))) return;
			if (!(LoadMapFunc = (LoadMapFuncDef)GetProcAddress(mDllInst, "LoadMap"))) return;
			if (!(SetGetCurrentMapFunc = (CloseFuncDef)GetProcAddress(mDllInst, "SetGetCurrentMap"))) return;
			if (!(LoadLocalMapFunc = (LoadLocalMapFuncDef)GetProcAddress(mDllInst, "LoadLocalMap"))) return;
			if (!(SaveMapToLocalFunc = (SaveMapToLocalFuncDef)GetProcAddress(mDllInst, "SaveMapToLocal"))) return;
			if (!(GetMapLayerFunc = (GetMapLayerFuncDef)GetProcAddress(mDllInst, "GetMapLayer"))) return;
			if (!(SetMapLayerFunc = (SetMapLayerFuncDef)GetProcAddress(mDllInst, "SetMapLayer"))) return;
			if (!(BitmapToMapLayerFunc = (BitmapToMapLayerFuncDef)GetProcAddress(mDllInst, "BitmapToMapLayer"))) return;
			if (!(BmpFileToMapLayerFunc = (BmpFileToMapLayerFuncDef)GetProcAddress(mDllInst, "BmpFileToMapLayer"))) return;
			if (!(MapLayerToBmpFileFunc = (MapLayerToBmpFileFuncDef)GetProcAddress(mDllInst, "MapLayerToBmpFile"))) return;
			if (!(ReadLocalMaplayerFunc = (ReadLocalMaplayerFuncDef)GetProcAddress(mDllInst, "ReadLocalMaplayer"))) return;
			if (!(GetSensorDataFunc = (GetSensorDataFuncDef)GetProcAddress(mDllInst, "GetSensorData"))) return;
			if (!(GetStatusFunc = (GetStatusFuncDef)GetProcAddress(mDllInst, "GetStatus"))) return;
			if (!(InitLocationFunc = (CloseFuncDef)GetProcAddress(mDllInst, "InitLocation"))) return;
			if (!(SetWiFiParamFunc = (SetWiFiParamFuncDef)GetProcAddress(mDllInst, "SetWiFiParam"))) return;
			if (!(SearchDeviceFunc = (SearchDeviceFuncDef)GetProcAddress(mDllInst, "SearchDevice"))) return;
			if (!(CheckConnectionFunc = (CheckConnectionFuncDef)GetProcAddress(mDllInst, "CheckConnection"))) return;
			if (!(UpdateNaviPackFileFunc = (UpdateNaviPackFileFuncDef)GetProcAddress(mDllInst, "UpdateNaviPackFile"))) return;
			if (!(SetSelfStreamFunc = (SetSelfStreamFuncDef)GetProcAddress(mDllInst, "SetSelfStream"))) return;
			if (!(SetSaveMapFunc = (SetSaveMapFuncDef)GetProcAddress(mDllInst, "SetSaveMap"))) return;
			if (!(SendFileFunc = (SendFileFuncDef)GetProcAddress(mDllInst, "SendFile"))) return;
			if (!(SetChangeNaviPackModeFunc = (SetChangeNaviPackModeFuncDef)GetProcAddress(mDllInst, "SetChangeNaviPackMode"))) return;
			if (!(ImuCelibrateFunc = (ImuCelibrateFuncDef)GetProcAddress(mDllInst, "ImuCelibrate"))) return;

		}
	}
}

int NaviPackSdk_Cpp::NaviPackSdk::GetSDKVersion()
{
	if (GetSDKVersionFunc)
	{
		return GetSDKVersionFunc();
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::SetGetNaviPackVersion(int id)
{
	if (SetGetNaviPackVersionFunc)
	{
		return SetGetNaviPackVersionFunc(id);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::Create(ConnectType conType)
{
	if (CreateFunc)
	{
		return CreateFunc(conType);
	}
	return -1;
}

void NaviPackSdk_Cpp::NaviPackSdk::Destroy(int id)
{
	if (DestroyFunc)
	{
		DestroyFunc(id);
	}
}

int NaviPackSdk_Cpp::NaviPackSdk::Open(int id, const char * name, int param)
{
	if (OpenFunc)
	{
		return OpenFunc(id,name,param);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::Reopen(int id)
{
	if (ReopenFunc)
	{
		return ReopenFunc(id);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::Close(int id)
{
	if (CloseFunc)
	{
		return CloseFunc(id);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::SetCallback(int id, DeviceMsgCallBack deviceMsgCb, ErrorMsgCallBack errMsgCb, MapPackageCallBack mapPackCb, LidarPackageCallBack lidarPackCb)
{
	if (SetCallbackFunc)
	{
		return SetCallbackFunc(id, deviceMsgCb, errMsgCb, mapPackCb, lidarPackCb);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::GetCarrierParam(int id, CarrierParam * param)
{
	if (GetCarrierParamFunc)
	{
		return GetCarrierParamFunc(id,param);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::SetCarrierParam(int id, CarrierParam * param)
{
	if (SetCarrierParamFunc)
	{
		return SetCarrierParamFunc(id,param);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::GetNaviPackParam(int id, NaviPackParam * param)
{
	if (GetNaviPackParamFunc)
	{
		return GetNaviPackParamFunc(id,param);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::SetNaviPackParam(int id, NaviPackParam * param)
{
	if (SetNaviPackParamFunc)
	{
		return SetNaviPackParamFunc(id,param);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::SetTargets(int id, int position_x[], int position_y[], int num, int phi)
{
	if (SetTargetsFunc)
	{
		return SetTargetsFunc(id,position_x,position_y,num,phi);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::GetCurrentPath(int id, int position_x[], int position_y[], int * num)
{
	if (GetCurrentPathFunc)
	{
		return GetCurrentPathFunc(id, position_x, position_y,num);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::PixelToPosition(int id, int pixel_x, int pixel_y, int * position_x, int * position_y)
{
	if (PixelToPositionFunc)
	{
		return PixelToPositionFunc(id,pixel_x,pixel_y,position_x,position_y);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::PositionToPixel(int id, int position_x, int position_y, int * pixel_x, int * pixel_y)
{
	if (PositionToPixelFunc)
	{
		return PositionToPixelFunc(id,position_x,position_y,pixel_x,pixel_y);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::SetSpeed(int id, int v, int w)
{
	if (SetSpeedFunc)
	{
		return SetSpeedFunc(id,v,w);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::AutoCharge(int id)
{
	if (AutoChargeFunc)
	{
		return AutoChargeFunc(id);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::SetChargerPosition(int id, int position_x, int position_y)
{
	if (SetChargerPositionFunc)
	{
		return SetChargerPositionFunc(id,position_x,position_y);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::StartMapping(int id, int mappingMode)
{
	if (StartMappingFunc)
	{
		return StartMappingFunc(id, mappingMode);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::StopMapping(int id, int save_flag)
{
	if (StopMappingFunc)
	{
		return StopMappingFunc(id, save_flag);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::GetMapList(int id, int * id_buffer)
{
	if (GetMapListFunc)
	{
		return GetMapListFunc(id, id_buffer);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::SaveCurrentMapToList(int id, int mapId)
{
	if (SaveCurrentMapToListFunc)
	{
		return SaveCurrentMapToListFunc(id,mapId);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::LoadMap(int id, int map_id)
{
	if (LoadMapFunc)
	{
		return LoadMapFunc(id, map_id);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::SetGetCurrentMap(int id)
{
	if (SetGetCurrentMapFunc)
	{
		return SetGetCurrentMapFunc(id);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::LoadLocalMap(int id, const char * local_map_path, int map_id)
{
	if (LoadLocalMapFunc)
	{
		return LoadLocalMapFunc(id, local_map_path,map_id);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::SaveMapToLocal(int id, const char * local_map_path, int picture_flag)
{
	if (SaveMapToLocalFunc)
	{
		return SaveMapToLocalFunc(id, local_map_path, picture_flag);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::GetMapLayer(int id, AlgMapData * map_data, int map_type)
{
	if (GetMapLayerFunc)
	{
		return GetMapLayerFunc(id, map_data, map_type);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::SetMapLayer(int id, AlgMapData * map_data, int map_type)
{
	if (SetMapLayerFunc)
	{
		return SetMapLayerFunc(id,map_data,map_type);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::BitmapToMapLayer(unsigned char * bitmap, AlgMapData ** map)
{
	if (BitmapToMapLayerFunc)
	{
		return BitmapToMapLayerFunc(bitmap, map);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::BmpFileToMapLayer(const char * file_path, AlgMapData ** map)
{
	if (BmpFileToMapLayerFunc)
	{
		return BmpFileToMapLayerFunc(file_path,map);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::MapLayerToBmpFile(AlgMapData * map, char * file_path)
{
	if (MapLayerToBmpFileFunc)
	{
		return MapLayerToBmpFileFunc(map, file_path);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::ReadLocalMaplayer(const char * fileName, AlgMapData ** map)
{
	if (ReadLocalMaplayerFunc)
	{
		return ReadLocalMaplayerFunc(fileName, map);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::GetSensorData(int id, AlgSensorData * sensor_data, int sensorType)
{
	if (GetSensorDataFunc)
	{
		return GetSensorDataFunc(id,sensor_data, sensorType);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::GetStatus(int id, AlgStatusRegister * status)
{
	if (GetStatusFunc)
	{
		return GetStatusFunc(id, status);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::InitLocation(int id)
{
	if (InitLocationFunc)
	{
		return InitLocationFunc(id);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::SetWiFiParam(int id, const char * ssid, const char * password)
{
	if (SetWiFiParamFunc)
	{
		return SetWiFiParamFunc(id,ssid,password);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::SearchDevice(int id, char * device_list, int timeout)
{
	if (SearchDeviceFunc)
	{
		return SearchDeviceFunc(id, device_list, timeout);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::CheckConnection()
{
	if (CheckConnectionFunc)
	{
		return CheckConnectionFunc();
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::UpdateNaviPackFile(int id, const char * fileName)
{
	if (UpdateNaviPackFileFunc)
	{
		return UpdateNaviPackFileFunc(id,fileName);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::SetSelfStream(int id, char * buf, int bufLen)
{
	if (SetSelfStreamFunc)
	{
		return SetSelfStreamFunc(id,buf, bufLen);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::SetSaveMap(int id, const char * filePath, const char * fileName)
{
	if (SetSaveMapFunc)
	{
		return SetSaveMapFunc(id, filePath, fileName);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::SendFile(int id, int type, const char * filePath, const char * fileName)
{
	if (SendFileFunc)
	{
		return SendFileFunc(id,type,filePath,fileName);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::SetChangeNaviPackMode(int id, int mode)
{
	if (SetChangeNaviPackModeFunc)
	{
		return SetChangeNaviPackModeFunc(id,mode);
	}
	return -1;
}

int NaviPackSdk_Cpp::NaviPackSdk::ImuCelibrate(int id)
{
	if (ImuCelibrateFunc)
	{
		return ImuCelibrateFunc(id);
	}
	return 0;
}