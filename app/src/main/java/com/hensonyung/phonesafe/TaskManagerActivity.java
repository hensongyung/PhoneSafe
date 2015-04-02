package com.hensonyung.phonesafe;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hensonyung.phonesafe.domain.TaskInfo;
import com.hensonyung.phonesafe.engine.TaskInfoProvider;
import com.hensonyung.phonesafe.utils.SystemInfoUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class TaskManagerActivity extends Activity {
    private TextView tv_process_count;
    private TextView tv_men_info;
    private TextView tv_status;

    private LinearLayout ll_loading;
    private ListView lv_task_manager;

    private List<TaskInfo> allTaskInfos;
    private List<TaskInfo> userTaskInfos;
    private List<TaskInfo> systemTaskInfos;

    private TaskManagerAdapter adapter;

    private int processCount;
    private long availMem;
    private long totalMem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        tv_men_info = (TextView) findViewById(R.id.tv_mem_info);
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        ll_loading= (LinearLayout) findViewById(R.id.ll_loading);
        lv_task_manager = (ListView) findViewById(R.id.lv_task_manager);
        tv_status = (TextView) findViewById(R.id.tv_status);
        setTitle();

        fillData();

        lv_task_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userTaskInfos!=null&systemTaskInfos!=null){
                    if (firstVisibleItem>=(userTaskInfos.size()+1)){
                        tv_status.setText("系统进程:" +systemTaskInfos.size() +"个");
                    }else {
                        tv_status.setText("用户进程:" +userTaskInfos.size() +"个");
                    }
                }
            }
        });

        lv_task_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskInfo taskInfo;
                if (position==0){
                    return;
                }else if (position == (userTaskInfos.size()+1)){

                    return;
                }else if (position<=userTaskInfos.size()){
                    taskInfo = userTaskInfos.get(position-1);

                }else {
                    taskInfo= systemTaskInfos.get(position-1-userTaskInfos.size()-1);
                }
                //屏蔽自己程序的点击事件
                if (getPackageName().equals(taskInfo.getPackname())){
                    return;
                }

                viewHolder holder = (viewHolder) view.getTag();
                if (taskInfo.isChecked()){
                    taskInfo.setChecked(false);
                    holder.cb_status.setChecked(false);
                }else {
                    taskInfo.setChecked(true);
                    holder.cb_status.setChecked(true);
                }
            }
        });
    }

    private void setTitle() {
        processCount =SystemInfoUtils.getRunningProcessCount(this);
        availMem=SystemInfoUtils.getAvailMem(this);
        totalMem=SystemInfoUtils.getTotalMem(this);
        tv_process_count.setText("运行中的进程"+processCount+"个");
        tv_men_info.setText("剩余/总内存"+ Formatter.formatFileSize(this,availMem) +"/"+
        Formatter.formatFileSize(this,totalMem));
    }

    private void fillData() {
        ll_loading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                allTaskInfos = TaskInfoProvider.getTaskInfo(getApplicationContext());
                userTaskInfos = new ArrayList<TaskInfo>();
                systemTaskInfos = new ArrayList<TaskInfo>();
                for (TaskInfo info: allTaskInfos){
                    if (info.isUserTask()){
                        userTaskInfos.add(info);
                    }else {
                        systemTaskInfos.add(info);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ll_loading.setVisibility(View.INVISIBLE);
                        if (adapter==null){
                            adapter = new TaskManagerAdapter();
                            lv_task_manager.setAdapter(adapter);
                        }else {
                            adapter.notifyDataSetChanged();

                        }

                    }
                });
            }
        }.start();
    }

    private class TaskManagerAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            SharedPreferences sp =getSharedPreferences("config",MODE_PRIVATE);

            if (sp.getBoolean("showsystem",false)){
                return userTaskInfos.size()+1 +systemTaskInfos.size()+1;
            }else {
                return userTaskInfos.size()+1;
            }

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
            TaskInfo taskInfo;
            if (position==0){
                TextView textView = new TextView(getApplicationContext());
                textView.setBackgroundColor(Color.GRAY);
                textView.setTextColor(Color.WHITE);
                textView.setText("用户进程"+userTaskInfos.size()+"个");
                return textView;
            }else if (position == (userTaskInfos.size()+1)){
                TextView textView = new TextView(getApplicationContext());
                textView.setBackgroundColor(Color.GRAY);
                textView.setTextColor(Color.WHITE);
                textView.setText("系统进程"+systemTaskInfos.size()+"个");
                return textView;
            }else if (position<=userTaskInfos.size()){
                taskInfo = userTaskInfos.get(position-1);

            }else {
                taskInfo= systemTaskInfos.get(position-1-userTaskInfos.size()-1);
            }

            View view;
            viewHolder holder;
            if (convertView!=null&&convertView instanceof RelativeLayout){
                view = convertView;
                holder = (viewHolder) view.getTag();
            }else {
                view = View.inflate(getApplicationContext(),R.layout.list_item_taskinfo,null);
                holder = new viewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_task_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_task_name);
                holder.tv_memsize = (TextView) view.findViewById(R.id.tv_task_memsize);
                holder.cb_status = (CheckBox) view.findViewById(R.id.cb_status);
                view.setTag(holder);
            }
            holder.iv_icon.setImageDrawable(taskInfo.getIcon());
            holder.tv_name.setText(taskInfo.getName());
            holder.tv_memsize.setText("内存占用" + Formatter.formatFileSize(getApplicationContext(),taskInfo.getMemSize()));
            holder.cb_status.setChecked(taskInfo.isChecked());
            if (getPackageName().equals(taskInfo.getPackname())){
                holder.cb_status.setVisibility(View.INVISIBLE);
            }else {
                holder.cb_status.setVisibility(View.VISIBLE);
            }
            return view;
        }
    }
    static class viewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_memsize;
        CheckBox cb_status;
    }


    public void selectAll(View view){
        for (TaskInfo info :allTaskInfos){
            if (getPackageName().equals(info.getPackname())){
                continue;
            }
            info.setChecked(true);
        }
        adapter.notifyDataSetChanged();

    }

    public void selectOppo(View view){
        for (TaskInfo info:allTaskInfos){
            if (getPackageName().equals(info.getPackname())){
                continue;
            }
            info.setChecked(!info.isChecked());
        }
        adapter.notifyDataSetChanged();
    }

    public void killAll(View view){
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int count = 0;
        long savedMem = 0;
        List<TaskInfo> killedTaskInfos = new ArrayList<>();
        for (TaskInfo info :allTaskInfos){
            if (info.isChecked()){
                am.killBackgroundProcesses(info.getPackname());
                if (info.isUserTask()){
                    userTaskInfos.remove(info);
                }else {
                    systemTaskInfos.remove(info);
                }
                killedTaskInfos.add(info);
                count++;
                savedMem+=info.getMemSize();
            }
        }

        allTaskInfos.removeAll(killedTaskInfos);
        adapter.notifyDataSetChanged();
        Toast.makeText(this,"结束了"+count+"个进程,释放了"+
                Formatter.formatFileSize(this,savedMem)+"的内存",Toast.LENGTH_SHORT).show();

        processCount-=count;
        availMem +=savedMem;
        tv_process_count.setText("运行中的进程"+processCount+"个");
        tv_men_info.setText("剩余/总内存:" +Formatter.formatFileSize(this,availMem)+"/"+
                Formatter.formatFileSize(this,totalMem));
    }

    public void enterSetting(View view){
        Intent intent = new Intent(TaskManagerActivity.this,TaskSettingActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        adapter.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
