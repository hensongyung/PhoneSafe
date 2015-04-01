package com.hensonyung.phonesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hensonyung.phonesafe.domain.AppInfo;
import com.hensonyung.phonesafe.engine.AppInfoProvider;
import com.hensonyung.phonesafe.utils.DensityUtil;

import org.w3c.dom.Text;

import java.text.Format;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;


public class AppManagerActivity extends Activity implements View.OnClickListener{
    private TextView tv_avail_rom;
    private TextView tv_avail_sd;

    private ListView lv_app_manager;
    private LinearLayout ll_loading;

    //所有应用程序包的信息
    private List<AppInfo>appInfos;

    //用户应用程序的集合
    private List<AppInfo>userAppInfos;

    //系统应用程序的集合
    private List<AppInfo>systemAppInfos;

    //当前程序信息的状态
    TextView tv_status;

    //悬浮窗体
    private PopupWindow popupWindow;
    private AppManagerAdapter adapter;


    private LinearLayout ll_start;
    private LinearLayout ll_share;
    private LinearLayout ll_uninstall;

    //被点击的条目
    private AppInfo appInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
        tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);
        tv_status= (TextView) findViewById(R.id.tv_status);
        long sdsize = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
        long romsize = getAvailSpace(Environment.getDataDirectory().getAbsolutePath());
        tv_avail_sd.setText("sd卡可用" + android.text.format.Formatter.formatFileSize(this,sdsize));
        tv_avail_rom.setText("ROM可用" + android.text.format.Formatter.formatFileSize(this,romsize));

        lv_app_manager = (ListView) findViewById(R.id.lv_appManager);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

        fillData();


        lv_app_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                dismissPopupwindow();
                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem > userAppInfos.size()) {
                        tv_status.setText("系统程序" + systemAppInfos.size() + "个");
                    } else {
                        tv_status.setText("用户程序" + userAppInfos.size() + "个");
                    }
                }
            }
        });

        /**
         * 设置listview的点击事件
         *
         */
        lv_app_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position==0 || position==(userAppInfos.size()+1)){
                    return;
                }else if (position<=userAppInfos.size()){
                    appInfo = userAppInfos.get(position-1);
                }else {
                    appInfo = systemAppInfos.get(position-1-userAppInfos.size()-1);
                }
                dismissPopupwindow();

                View contentView = View.inflate(getApplicationContext(),R.layout.popup_app_item,null);
                ll_start = (LinearLayout) contentView.findViewById(R.id.ll_start);
                ll_share= (LinearLayout) contentView.findViewById(R.id.ll_share);
                ll_uninstall= (LinearLayout) contentView.findViewById(R.id.ll_uninstall);

                ll_start.setOnClickListener(AppManagerActivity.this);
                ll_share.setOnClickListener(AppManagerActivity.this);
                ll_uninstall.setOnClickListener(AppManagerActivity.this);

//                contentView.setText(appInfo.getPackname());
//                contentView.setTextColor(Color.BLACK);
                popupWindow = new PopupWindow(contentView,-2,-2);//-2等价wrapcontent,-1等价fillparent
                //动画效果的播放必须要求窗体有背景颜色
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int[]location = new int[2];
                view.getLocationInWindow(location);
                int dip = 60;
                int px = DensityUtil.dip2px(getApplicationContext(),dip);

                popupWindow.showAtLocation(parent, Gravity.LEFT|Gravity.TOP,px,location[1]);
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.3f,1.0f,0.3f,1.0f, Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0.5f);

                scaleAnimation.setDuration(200);
                AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f,1.0f);
                alphaAnimation.setDuration(200);
//                contentView.startAnimation(scaleAnimation);
                AnimationSet set = new AnimationSet(false);
                set.addAnimation(scaleAnimation);
                set.addAnimation(alphaAnimation);
                contentView.startAnimation(set);
            }
        });

    }

    private void dismissPopupwindow() {
        //关闭旧窗体
        if (popupWindow!=null&&popupWindow.isShowing()){
            popupWindow.dismiss();
            popupWindow =null;
        }
    }

    private void fillData(){
        ll_loading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
                userAppInfos = new ArrayList<AppInfo>();
                systemAppInfos = new ArrayList<AppInfo>();
                for (AppInfo info : appInfos){
                    if (info.isUserApp()){
                        userAppInfos.add(info);
                    }else {
                        systemAppInfos.add(info);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter ==null){
                            adapter = new AppManagerAdapter();
                            lv_app_manager.setAdapter(adapter);
                        }else {
                            adapter.notifyDataSetChanged();
                        }

                        ll_loading.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }.start();

    }

    //布局对应的点击事件
    @Override
    public void onClick(View v) {
        dismissPopupwindow();
        switch (v.getId()){
            case R.id.ll_start:
                startApplication();
                break;
            case R.id.ll_share:
                shareApplication();
                break;
            case R.id.ll_uninstall:
                if (appInfo.isUserApp()) {
                    uninstallApplication();
                }else {
                    Toast.makeText(this,"NO Root",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void shareApplication() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"推荐你使用一款软件"+appInfo.getName());
        startActivity(intent);
    }

    private void uninstallApplication() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setAction("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:"+appInfo.getPackname()));
        startActivityForResult(intent, 0);
//        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fillData();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startApplication() {
        //查询应用程序入口的activity
        PackageManager pm = getPackageManager();
//        Intent intent = new Intent();
//        intent.setAction("android.intent.action.MAIN");
//        intent.addCategory("android.intent.category.LAUNCHER");
////        查询全部有启动能力的activity
//        List<ResolveInfo> infos =pm.queryIntentActivities(intent,PackageManager.GET_INTENT_FILTERS);
//
        Intent intent =pm.getLaunchIntentForPackage(appInfo.getPackname());
        if (intent !=null) {
            startActivity(intent);
        }else {
            Toast.makeText(this,"不能启动该应用",Toast.LENGTH_SHORT).show();
        }
     }

    private class AppManagerAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return userAppInfos.size()+1+systemAppInfos.size()+1;
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
            AppInfo appInfo;

            if (position ==0){
                TextView tv= new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("用户程序"+userAppInfos.size() + "个");
                return tv;
            }else if (position==userAppInfos.size()+1){
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("系统程序"+systemAppInfos.size()+"个");
                return tv;
            }else if (position <= userAppInfos.size()){
                appInfo = userAppInfos.get(position -1);

            }else {
                appInfo = systemAppInfos.get(position-1-userAppInfos.size()-1);
            }

            View view;
            ViewHolder holder;
            if (convertView != null&&convertView instanceof RelativeLayout){
                //合适的类型去复用
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }else {
                view = View.inflate(getApplicationContext(),R.layout.list_item_appinfo,null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
                holder.tv_name= (TextView) view.findViewById(R.id.tv_name);
                holder.tv_location = (TextView) view.findViewById(R.id.tv_location);
                view.setTag(holder);
            }
//            appInfo = appInfos.get(position);
            holder.iv_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_name.setText(appInfo.getName());
            if (appInfo.isInRom()){
                holder.tv_location.setText("手机存储");
            }else {
                holder.tv_location.setText("sd卡");
            }


            return view;
        }
    }


    static class ViewHolder{
        TextView tv_name;
        TextView tv_location;
        ImageView iv_icon;

    }

    /**
     * 获取某个目录的可用空间
     * @param path
     * @return
     */
    private long getAvailSpace(String path){
        StatFs statFs = new StatFs(path);
        statFs.getBlockCount(); //获取分区的个数
        long size =statFs.getBlockSize(); //获取分区的大小
        long count = statFs.getAvailableBlocks();  //可用区块的个数

        return size*count;
    }

    @Override
    protected void onDestroy() {
        dismissPopupwindow();
        super.onDestroy();
    }
}
