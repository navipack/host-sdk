package com.imscv.navipacksdkapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.imscv.navipacksdk.NaviPackSdk;
import com.imscv.navipacksdk.inf.OpenDeviceListener;
import com.imscv.navipacksdk.module.SelfStream;
import com.imscv.navipacksdk.tools.StringOperate;
import com.imscv.navipacksdkapp.model.Device;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final String TAG = "RadarSdk";

    private int mId = -1;

    private NaviPackSdk mNaviPackSdk = null;
    private Button switchButton, searchButton, connectButtonTCP, connectButtonSerial;
    private EditText editText;
    private ListView listView;

    private String deviceName = "192.168.23.190";
    private int deviceParam = 115200;
    private NaviPackSdk.ConnectTypeEnum mConnectType = NaviPackSdk.ConnectTypeEnum.SERIAL_CON;

    private ArrayList<Device> devices = new ArrayList<>();
    private DeviceAdapter adapter = new DeviceAdapter();

    private Handler mHandler = new Handler();
    private Device mDevice;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "LoginActivity:");


            initView();
            initPara();
            switchButton.setText("Manual");

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public void initView() {
        switchButton = (Button) findViewById(R.id.switch_button);
        switchButton.setOnClickListener(onClickListener);

        searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(onClickListener);

        connectButtonTCP = (Button) findViewById(R.id.connect_button_tcp);
        connectButtonTCP.setOnClickListener(onClickListener);

        connectButtonSerial = (Button) findViewById(R.id.connect_button_serial);
        connectButtonSerial.setOnClickListener(onClickListener);

        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(itemClickListener);

        editText = (EditText) findViewById(R.id.edittext);
        editText.setText(deviceName);
    }

    public void initPara() {
        mNaviPackSdk = NaviPackSdk.getInstance();
        Log.e(TAG,"writeFileToSDwriteFileToSDwriteFileToSDwriteFileToSD");
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            connectDevice(devices.get(position));
        }
    };

    private void showManualView() {
        switchButton.setText("Auto");
        listView.setVisibility(View.INVISIBLE);
        connectButtonTCP.setVisibility(View.VISIBLE);
        connectButtonSerial.setVisibility(View.VISIBLE);
        editText.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.INVISIBLE);
    }

    private void showAutoView() {
        switchButton.setText("Manual");
        listView.setVisibility(View.VISIBLE);
        connectButtonTCP.setVisibility(View.INVISIBLE);
        connectButtonSerial.setVisibility(View.INVISIBLE);
        editText.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.VISIBLE);
    }

    private void connectDevice(final Device device) {
        mDevice = device;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mNaviPackSdk.open(device.id, device.fileName, device.param,openDeviceListener);
            }
        }, 100);

    }

    private OpenDeviceListener openDeviceListener = new OpenDeviceListener() {
        @Override
        public void onOpenSuccess(boolean isSuccess) {
            if (isSuccess) {
                Toast.makeText(MainActivity.this, "连接成功!", Toast.LENGTH_SHORT).show();
                showControlView(mDevice);
            } else {
                Toast.makeText(MainActivity.this, "连接失败！", Toast.LENGTH_SHORT).show();
                mNaviPackSdk.destroy(mDevice.id);
            }
        }
    };

    private void showControlView(Device device) {
        Intent intent = new Intent(MainActivity.this, DrawerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("device", device);
        intent.putExtras(bundle);
        intent.putExtra("handlerID", mId);
        intent.putExtra("isUseTcp", mConnectType == NaviPackSdk.ConnectTypeEnum.TCP_CON ? true : false);
        MainActivity.this.startActivity(intent);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.switch_button:
                    if (switchButton.getText().equals("Auto")) {
                        showAutoView();
                    } else {
                        showManualView();
                    }
                    break;
                case R.id.search_button:
                    //
                    devices.clear();
                    adapter.notifyDataSetChanged();
                    break;
                case R.id.connect_button_tcp:
                    mConnectType = NaviPackSdk.ConnectTypeEnum.TCP_CON;
                    mId = mNaviPackSdk.createHandler(mConnectType);
                    deviceName = editText.getText().toString();
                    connectDevice(new Device(deviceName, deviceParam, mId));
                    break;
                case R.id.connect_button_serial:
                    mConnectType = NaviPackSdk.ConnectTypeEnum.SERIAL_CON;
                    mId = mNaviPackSdk.createHandler(mConnectType);

                    deviceName = editText.getText().toString();
                    connectDevice(new Device(deviceName, deviceParam, mId));
                    break;
            }
        }
    };

    public void updataSerchList(final int uuid, final String ip, final int port) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Device device = new Device(ip, port, uuid);
                devices.add(device);
                adapter.notifyDataSetChanged();
            }
        });
    }


    public class DeviceAdapter extends BaseAdapter {

        TextView ipTextView, uuidTextView;

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                convertView = inflater.inflate(R.layout.item_device, null);
                ipTextView = (TextView) convertView.findViewById(R.id.ip_textview);
                uuidTextView = (TextView) convertView.findViewById(R.id.uuid_textview);
            }
            Device device = devices.get(position);
            ipTextView.setText("fileName Address: " + device.fileName);
            uuidTextView.setText("id: " + device.id);
            return convertView;
        }
    }
}
