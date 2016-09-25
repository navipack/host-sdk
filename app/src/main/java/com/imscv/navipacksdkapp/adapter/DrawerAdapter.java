package com.imscv.navipacksdkapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.imscv.navipacksdkapp.R;
import com.imscv.navipacksdkapp.model.TuiCoolMenuItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dell on 2016/9/20.
 */
public class DrawerAdapter extends BaseAdapter {


    //存储侧滑菜单中的各项的数据
    List<TuiCoolMenuItem> MenuItems = new ArrayList<TuiCoolMenuItem>();
    //构造方法中传过来的activity
    Context context;

    public Map<Integer, TuiCoolMenuItem> funAdapter = new HashMap<Integer, TuiCoolMenuItem>();

    public static final int LOAD_MAP = 0;
    public static final int START_BUILD_MAP = 1;
    public static final int STOP_BUILD_MAP = 2;
    public static final int INIT_LOCATION = 3;
    public static final int MOVE_TO_POINT = 4;
    public static final int FREE_CONTROL = 5;
    public static final int UPDATE_NAVIPACK = 6;
    public static final int SAVE_CURRENT_MAP = 7;
    public static final int GO_ROUNDS = 8;           //只需要使用sdk的到点运动功能即可实现

    //构造方法
    public DrawerAdapter(Context context) {

        this.context = context;

        funAdapter.put(LOAD_MAP, new TuiCoolMenuItem("载入地图",R.mipmap.option_delay));
        funAdapter.put(START_BUILD_MAP, new TuiCoolMenuItem("开始建图",R.mipmap.option_delay));
        funAdapter.put(STOP_BUILD_MAP, new TuiCoolMenuItem("保存建图",R.mipmap.option_delay));
        funAdapter.put(INIT_LOCATION, new TuiCoolMenuItem("初始定位",R.mipmap.option_delay));
        funAdapter.put(MOVE_TO_POINT, new TuiCoolMenuItem("到点运动",R.mipmap.option_delay));
        funAdapter.put(FREE_CONTROL, new TuiCoolMenuItem("取消控制",R.mipmap.option_delay));
        funAdapter.put(UPDATE_NAVIPACK, new TuiCoolMenuItem("升级套件",R.mipmap.option_delay));
        funAdapter.put(SAVE_CURRENT_MAP, new TuiCoolMenuItem("保存地图",R.mipmap.option_delay));
        funAdapter.put(GO_ROUNDS, new TuiCoolMenuItem("定点巡逻",R.mipmap.option_delay));



        for (int i = 0; i < funAdapter.size(); i++) {
            MenuItems.add(funAdapter.get(i));
        }

    }

    public void changeText(int postion, String text) {
        MenuItems.get(postion).menuTitle = text;
    }

    @Override
    public int getCount() {

        return MenuItems.size();

    }

    @Override
    public TuiCoolMenuItem getItem(int position) {

        return MenuItems.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.menudrawer_item, parent, false);
            ((TextView) view).setText(getItem(position).menuTitle);
          //  ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(getItem(position).menuIcon, 0, 0, 0) ;
        }
        return view;
    }

}

