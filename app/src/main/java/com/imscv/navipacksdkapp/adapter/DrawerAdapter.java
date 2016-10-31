package com.imscv.navipacksdkapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

    public static final int LOAD_MAP = 0;
    public static final int START_BUILD_MAP = 1;
    public static final int SAVE_BUILD_MAP = 2;
    public static final int INIT_LOCATION = 3;
    public static final int MOVE_TO_POINT = 4;
    public static final int FREE_CONTROL = 5;
    public static final int UPDATE_NAVIPACK = 6;
    public static final int SAVE_CURRENT_MAP = 7;
    public static final int GO_ROUNDS = 8;           //只需要使用sdk的到点运动功能即可实现
    public static final int CHANGE_MODE = 9;           //使能或者禁用雷达数据转发
    public static final int GET_NAVIPACK_VERSION = 10;           //使能或者禁用雷达数据转发
    public static final int START_BUILD_MAP_AUTO = 11;

    private LayoutInflater mInflater = null;
    //ViewHolder静态类

    //存储侧滑菜单中的各项的数据
    private List<TuiCoolMenuItem> MenuItems = new ArrayList<TuiCoolMenuItem>();
    //构造方法中传过来的activity
    private Context context;

    // public Map<Integer, TuiCoolMenuItem> funAdapter = new HashMap<Integer, TuiCoolMenuItem>();


    //构造方法
    public DrawerAdapter(Context context) {

        this.context = context;

        MenuItems.add(new TuiCoolMenuItem(context.getString(R.string.adp_load_map), R.mipmap.option_delay));
        MenuItems.add(new TuiCoolMenuItem(context.getString(R.string.adp_start_build_map), R.mipmap.option_delay));
        MenuItems.add(new TuiCoolMenuItem(context.getString(R.string.adp_save_build_map), R.mipmap.option_delay));
        MenuItems.add(new TuiCoolMenuItem(context.getString(R.string.adp_init_location), R.mipmap.option_delay));
        MenuItems.add(new TuiCoolMenuItem(context.getString(R.string.adp_move_to_point), R.mipmap.option_delay));
        MenuItems.add(new TuiCoolMenuItem(context.getString(R.string.adp_cancle_control), R.mipmap.option_delay));
        MenuItems.add(new TuiCoolMenuItem(context.getString(R.string.adp_update_navipack), R.mipmap.option_delay));
        MenuItems.add(new TuiCoolMenuItem(context.getString(R.string.adp_save_use_map), R.mipmap.option_delay));
        MenuItems.add(new TuiCoolMenuItem(context.getString(R.string.adp_go_rounts), R.mipmap.option_delay));
        MenuItems.add(new TuiCoolMenuItem(context.getString(R.string.adp_change_lidar_modes), R.mipmap.option_delay));
        MenuItems.add(new TuiCoolMenuItem(context.getString(R.string.adp_get_version), R.mipmap.option_delay));
        MenuItems.add(new TuiCoolMenuItem(context.getString(R.string.adp_start_auto_build_map), R.mipmap.option_delay));


//        this.mInflater = LayoutInflater.from(context);


    }




    static class ViewHolder
    {
        public TextView title;
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

        // Log.d("getView", "getView:" + position + " view = " + convertView + "  " + getItem(position).menuTitle);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.menudrawer_item,null);
            viewHolder.title = (TextView)convertView.findViewById(R.id.itemTv);
            //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(getItem(position).menuTitle);
        return convertView;
    }


}

