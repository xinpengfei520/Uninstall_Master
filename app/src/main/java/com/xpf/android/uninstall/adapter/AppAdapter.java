package com.xpf.android.uninstall.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xpf.android.uninstall.R;
import com.xpf.android.uninstall.bean.AppInfo;

import java.util.List;

/**
 * Created by Vance on 3/13/21 :)
 * Function:AppAdapter 适配器
 */
public class AppAdapter extends BaseAdapter {

    private static final String TAG = "AppAdapter";
    private final Context mContext;
    private List<AppInfo> mList;

    public AppAdapter(Context context, List<AppInfo> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        // 1.初始化 convertView
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_app, null);
            holder = new ViewHolder();

            // 2.获取 convertView 指定的每一个视图对象
            holder.ivItemIcon = convertView.findViewById(R.id.iv_item_icon);
            holder.tvItemName = convertView.findViewById(R.id.tv_item_name);
            holder.tvLetter = convertView.findViewById(R.id.tvLetter);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 3.获取指定位置的索引的数据
        AppInfo appInfo = mList.get(position);
        Log.e(TAG, "getView: mList hashCode -> " + mList.hashCode());

        // 4.给视图对象装配数据
        holder.ivItemIcon.setImageDrawable(appInfo.getIcon());
        holder.tvItemName.setText(appInfo.getAppName());

        // 5.获取 App 名称的首字母(需转成大写)
        String letter = appInfo.getPinyin().substring(0, 1).toUpperCase();
        holder.tvLetter.setText(letter);

        if (position == 0) {
            holder.tvLetter.setVisibility(View.VISIBLE);
        } else {
            // 前一条的首字母，如果上一个字母和当前字母一样就不显示，否则就显示
            String preLetter = mList.get(position - 1).getPinyin().substring(0, 1);
            if (preLetter.equals(letter)) {
                holder.tvLetter.setVisibility(View.GONE);
            } else {
                holder.tvLetter.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivItemIcon;
        TextView tvItemName;
        TextView tvLetter;
    }

    public void refresh(List<AppInfo> list) {
        this.mList = list;
        notifyDataSetChanged();
    }
}
