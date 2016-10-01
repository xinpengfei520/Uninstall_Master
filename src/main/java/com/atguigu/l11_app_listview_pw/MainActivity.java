package com.atguigu.l11_app_listview_pw;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用的步骤分析：
 * 1.布局的设置
 * 2.9patch图片的制作
 * 拉伸“上”和 “左”：负责将图片分为9份。 1区：复制。2区：拉伸。 3区：不变
 * 拉伸“下”和“右”：负责决定显示文本内容的区域。 默认情况下，只有1区可以显示内容
 * 3.PopupWindow的创建和显示
 * 4.给ListView设置滑动事件的监听
 * 5.给显示的PopupWindow设置启动动画
 * 6.设置popupView中具体LinearLayout的点击事件
 * 7.使用selector + shape
 */
public class MainActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView lv_app;
    private AppAdapter adapter;
    private List<AppInfo> list;

    //声明PopupWindow
    private PopupWindow popupWindow;
    //声明PopupWindow对应的视图
    private View popupView;

    //声明缩放动画
    private ScaleAnimation scaleAnimation;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv_app = (ListView) findViewById(R.id.lv_app);

        //初始化集合数据
        list = getAllAppInfos();

        // 初始化Adapter
        adapter = new AppAdapter();
        //显示列表
        lv_app.setAdapter(adapter);
        //给ListView中的item设置点击事件的监听
        lv_app.setOnItemClickListener(this);

        //给ListView设置滑动事件的监听
        lv_app.setOnScrollListener(new AbsListView.OnScrollListener() {
            /*
			SCROLL_STATE_IDLE:空闲状态
			SCROLL_STATE_TOUCH_SCROLL:滑动的状态
			SCROLL_STATE_FLING:快速滑动的状态
			 */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.e("TAG", "onScrollStateChanged()  scrollState = " + scrollState);

                //当开始滑动时，设置popupwindow消失
                if(scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    if(popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                Log.e("TAG", "onScroll()");
            }
        });

    }

    /*
     * 得到手机中所有应用信息的列表 AppInfo Drawable icon String appName String packageName
	 */
    protected List<AppInfo> getAllAppInfos() {

        List<AppInfo> list = new ArrayList<AppInfo>();
        // 得到应用的packgeManager
        PackageManager packageManager = getPackageManager();
        // 创建一个主界面的intent
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 得到包含应用信息的列表
        List<ResolveInfo> ResolveInfos = packageManager.queryIntentActivities(
                intent, 0);
        // 遍历
        for (ResolveInfo ri : ResolveInfos) {
            // 得到包名
            String packageName = ri.activityInfo.packageName;
            // 得到图标
            Drawable icon = ri.loadIcon(packageManager);
            // 得到应用名称
            String appName = ri.loadLabel(packageManager).toString();
            // 封装应用信息对象
            AppInfo appInfo = new AppInfo(icon, appName, packageName);
            // 添加到list
            list.add(appInfo);
        }
        return list;
    }

    /**
     * 点击ListView中具体某一个item时的回调方法
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //获取点击位置的应用包名
        packageName = list.get(position).getPackageName();
        Log.e("TAG", packageName);

        if(popupWindow == null) {
            popupView = View.inflate(MainActivity.this, R.layout.pw_linearlayout,null);
            //参数2,3：指明popupwindow的宽度和高度
            popupWindow = new PopupWindow(popupView,view.getWidth()-140,view.getHeight());

            //设置背景图片， 必须设置，不然动画没作用
            popupWindow.setBackgroundDrawable(new BitmapDrawable());

            //创建缩放动画(默认从左上角开始)
            scaleAnimation = new ScaleAnimation(0,1,0,1,Animation.ABSOLUTE,0,Animation.ABSOLUTE,0);
            scaleAnimation.setDuration(500);

            //通过popupView获取其内部的三个Linearlayout,并分别设置监听
            popupView.findViewById(R.id.ll_pw_uninstall).setOnClickListener(this);
            popupView.findViewById(R.id.ll_pw_run).setOnClickListener(this);
            popupView.findViewById(R.id.ll_pw_share).setOnClickListener(this);

        }

        //在重新显示之前，设置popupwindow的销毁
        if(popupWindow.isShowing()) {
            popupWindow.dismiss();
        }

        //显示
        popupWindow.showAsDropDown(view,140,-view.getHeight());

        //设置动画
        popupView.startAnimation(scaleAnimation);
    }

    /**
     * 点击popupView中具体的linearlayout的回调方法
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_pw_uninstall :
//                Toast.makeText(MainActivity.this, "卸载", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();

                Intent intent = new Intent("android.intent.action.UNINSTALL_PACKAGE");
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);

                break;
            case R.id.ll_pw_run :
//                Toast.makeText(MainActivity.this, "运行", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();

                PackageManager manager = this.getPackageManager();
                Intent intent2 = manager.getLaunchIntentForPackage(packageName);
                startActivity(intent2);

                break;
            case R.id.ll_pw_share :
                Toast.makeText(MainActivity.this, "分享", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        list = getAllAppInfos();//重新获取所有，相当于更新内存中的数据
        adapter.notifyDataSetChanged();//刷新显示
        Log.e("TAG", "onResume()");
    }

    /**
     * AppAdapter适配器
     */
    private class AppAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list == null ? null : list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //1.初始化convertView
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.item_app, null);
            }
            //2.获取指定位置的索引的数据
            AppInfo appInfo = list.get(position);
            //3.获取convertView指定的每一个视图对象
            ImageView iv_item_icon = (ImageView) convertView.findViewById(R.id.iv_item_icon);
            TextView tv_item_name = (TextView) convertView.findViewById(R.id.tv_item_name);

            //4.给视图对象装配数据
            iv_item_icon.setImageDrawable(appInfo.getIcon());
            tv_item_name.setText(appInfo.getAppName());

            return convertView;
        }
    }
}
