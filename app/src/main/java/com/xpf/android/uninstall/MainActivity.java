package com.xpf.android.uninstall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.xpf.android.uninstall.adapter.AppAdapter;
import com.xpf.android.uninstall.bean.AppInfo;
import com.xpf.android.uninstall.utils.PinYinUtils;
import com.xpf.android.uninstall.utils.Utils;
import com.xpf.android.uninstall.widget.QuickIndexView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    // TODO: 3/21/21 ① 顶部字母吸附效果，吸附后字母颜色变化；② 按压震动效果；
    private static final String TAG = "MainActivity";

    private ListView mListView;
    private QuickIndexView quickIndexView;
    private TextView tvIndex;

    private AppAdapter mAdapter;
    private final List<AppInfo> mList = new ArrayList<>();

    // 声明 PopupWindow
    private PopupWindow popupWindow;
    // 声明 PopupWindow 对应的视图
    private View popupView;
    // 声明缩放动画
    private ScaleAnimation scaleAnimation;
    private String packageName;
    private AppPackageReceiver mAppPackageReceiver;
    private Handler mHandler;

    private static class AppPackageReceiver extends BroadcastReceiver {

        private static final String TAG = "AppPackageReceiver";
        private final WeakReference<MainActivity> weakReference;

        public AppPackageReceiver(MainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "AppPackageReceiver onReceive():" + intent.getDataString() + ",action:" + intent.getAction());
            if ("android.intent.action.PACKAGE_REMOVED".equals(intent.getAction())) {
                Toast.makeText(context, "有应用被卸载了", Toast.LENGTH_SHORT).show();

                MainActivity activity = weakReference.get();

                if (activity != null) {
                    activity.refreshList();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.lvApp);
        quickIndexView = findViewById(R.id.quickIndexView);
        tvIndex = findViewById(R.id.tvIndex);

        mHandler = new Handler(Looper.getMainLooper());

        Log.e(TAG, "onCreate1: mList hashCode -> " + mList.hashCode());
        // 初始化集合数据
        mList.addAll(getAllAppList());
        Log.e(TAG, "onCreate2: mList hashCode -> " + mList.hashCode());
        // 初始化Adapter
        mAdapter = new AppAdapter(this, mList);
        // 显示列表
        mListView.setAdapter(mAdapter);

        initListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcast();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mAppPackageReceiver);
    }

    /**
     * 安卓 8.0 后很多广播做了限制，必须要动态注册
     */
    private void registerBroadcast() {
        mAppPackageReceiver = new AppPackageReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addDataScheme("package");
        // 应用卸载的广播
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        registerReceiver(mAppPackageReceiver, intentFilter);
    }

    private void initListener() {
        // 给 ListView 中的 item 设置点击事件的监听
        mListView.setOnItemClickListener(this);

        // 给 ListView 设置滑动事件的监听
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            /*
			SCROLL_STATE_IDLE:空闲状态
			SCROLL_STATE_TOUCH_SCROLL:滑动的状态
			SCROLL_STATE_FLING:快速滑动的状态
			 */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //Log.e(TAG, "onScrollStateChanged()  scrollState = " + scrollState);

                // 当开始滑动时，设置 popupWindow 消失
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    dismissPopupWindow();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d(TAG, "onScroll()");
            }
        });

        // 设置按下字母变化的监听
        quickIndexView.setOnTextChangeListener(new MyOnTextChangeListener());
    }

    private class MyOnTextChangeListener implements QuickIndexView.OnTextChangeListener {

        @Override
        public void onTextChange(String letter) {
            updateLetter(letter);
            updateList(letter);
        }
    }

    /**
     * A~Z
     *
     * @param letter
     */
    private void updateList(String letter) {
        for (int i = 0; i < mList.size(); i++) {
            String listWord = mList.get(i).getPinyin().substring(0, 1);
            if (letter.equals(listWord)) {
                mListView.setSelection(i);
                break;
            }
        }
    }

    private void updateLetter(String letter) {
        tvIndex.setVisibility(View.VISIBLE);
        tvIndex.setText(letter);

        // 把所有的消息移除
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(() -> {
            // 在主线程
            Log.d(TAG, Thread.currentThread().getName() + "---");
            tvIndex.setVisibility(View.GONE);
        }, 500);
    }

    private void dismissPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    /*
     * 得到手机中所有应用信息的列表 AppInfo Drawable icon String appName String packageName
     */
    protected List<AppInfo> getAllAppList() {
        List<AppInfo> list = new ArrayList<>();
        // 得到应用的 packageManager
        PackageManager packageManager = getPackageManager();
        // 创建一个主界面的intent
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 得到包含应用信息的列表
        List<ResolveInfo> ResolveInfos = packageManager.queryIntentActivities(intent, 0);

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

        // 先将字母开头的和其他字符例如数字等先分开
        List<AppInfo> letterList = new ArrayList<>();
        List<AppInfo> otherList = new ArrayList<>();

        for (AppInfo appInfo : list) {
            if (PinYinUtils.isLetter(appInfo.getPinyin().substring(0, 1))) {
                letterList.add(appInfo);
            } else {
                otherList.add(appInfo);
            }
        }

        // 将获取到字母集合的数据按字母序排序
        Collections.sort(letterList, (o1, o2) -> o1.getPinyin().substring(0, 1).compareToIgnoreCase(o2.getPinyin().substring(0, 1)));

        // 将其他字符放到字母集合后面
        letterList.addAll(otherList);

        return letterList;

    }

    /**
     * 点击ListView中具体某一个item时的回调方法
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 获取点击位置的应用包名
        packageName = mList.get(position).getPackageName();
        Log.e(TAG, packageName);

        if (popupWindow == null) {
            popupView = View.inflate(MainActivity.this, R.layout.pw_linearlayout, null);
            //参数2,3：指明popupwindow的宽度和高度
            popupWindow = new PopupWindow(popupView, view.getWidth() - 140, view.getHeight());

            //设置背景图片， 必须设置，不然动画没作用
            popupWindow.setBackgroundDrawable(new BitmapDrawable());

            //创建缩放动画(默认从左上角开始)
            scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0);
            scaleAnimation.setDuration(500);

            //通过popupView获取其内部的三个 LinearLayout,并分别设置监听
            popupView.findViewById(R.id.ll_pw_uninstall).setOnClickListener(this);
            popupView.findViewById(R.id.ll_pw_run).setOnClickListener(this);
            popupView.findViewById(R.id.ll_pw_share).setOnClickListener(this);
        }

        //在重新显示之前，设置 popupWindow 的销毁
        dismissPopupWindow();

        //显示
        popupWindow.showAsDropDown(view, 140, -view.getHeight());

        //设置动画
        popupView.startAnimation(scaleAnimation);
    }

    /**
     * 点击popupView中具体的 linearLayout 的回调方法
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        dismissPopupWindow();

        switch (v.getId()) {
            case R.id.ll_pw_uninstall:
                if (!Utils.isCurrentApp(packageName)) {
                    Intent intent = new Intent("android.intent.action.UNINSTALL_PACKAGE");
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "对不起，不能卸载当前应用！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_pw_run:
                PackageManager manager = this.getPackageManager();
                Intent intent2 = manager.getLaunchIntentForPackage(packageName);
                startActivity(intent2);
                break;
            case R.id.ll_pw_share:
                Toast.makeText(MainActivity.this, "分享", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 刷新显示，注意这里的刷新有两种方式
     */
    private void refreshList() {
        // 方式一：这种方式因为重新获取的 list 是不同的对象，所以要传递过去
        //mList = getAllAppInfos();
        //mAdapter.refresh(mList);

        // 方式二：这种方式是 list 和 adapter 已经绑定了，还是同一个 List，我们只是修改它里面的数据，修改后可以直接刷新
        mList.clear();
        mList.addAll(getAllAppList());
        Log.e(TAG, "refreshList: mList hashCode -> " + mList.hashCode());
        mAdapter.notifyDataSetChanged();
    }

}