#include <stdio.h>
#include "NaviPackSdk.h"
#include <windows.h>

using namespace NaviPackSdk_Cpp;

void OnGetDeviceMsg(int id, int msgType, int msgCode, void* param)
{
	printf("OnGetDeviceMsg %d %d %d \n", id, msgType, msgCode);
}

void OnGetErrorMsg(s32 id, s32 errorLevel, s32 errorCode, char* msg)
{
	printf("OnGetErrorMsg %s\n", msg);
}


int main()
{
	NaviPackSdk naviPackSdk;
	int r;
	printf(".............NaviPackSdk .............\n");
	printf(".............version:%d .............\n", naviPackSdk.GetSDKVersion());
	int naviId = naviPackSdk.Create(TCP_CONNECT);
	if (naviId < 0)
	{
		printf("create navipack sdk interface failed!\n");
		return -1;
	}

	r = naviPackSdk.Open(naviId,"127.0.0.1",9977);
	if (r < 0)
	{
		printf("connect to server failed\n");
		return -1;
	}

	naviPackSdk.SetCallback(naviId, OnGetDeviceMsg, OnGetErrorMsg,NULL,NULL);

	while (true)
	{
		naviPackSdk.SetSpeed(naviId,0,0);
		Sleep(1000);
	}

	return 0;
}