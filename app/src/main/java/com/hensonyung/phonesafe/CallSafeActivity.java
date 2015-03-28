package com.hensonyung.phonesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hensonyung.phonesafe.db.dao.BlackNumberDao;
import com.hensonyung.phonesafe.domain.BlackNumberInfo;

import java.util.List;


public class CallSafeActivity extends Activity {
    private ListView lv_callsms_safe;
    private List<BlackNumberInfo> infos;
    private BlackNumberDao dao;

    private CallSmsSafeAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe);
        lv_callsms_safe = (ListView) findViewById(R.id.lv_callsms_safe);
        dao = new BlackNumberDao(this);
        infos = dao.findAll();
        adapter = new CallSmsSafeAdapter();
        lv_callsms_safe.setAdapter(adapter);
    }

    private class CallSmsSafeAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return infos.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            //减少内存中view对象创建的对数
            if (convertView ==null){
                 view = View.inflate(getApplicationContext(),R.layout.list_item_callsms,null);
                //减少子孩子创建的次数——内存中对象的地址,
                holder = new ViewHolder();
                holder.tv_blacknumber = (TextView) view.findViewById(R.id.tv_blacknumber);
                holder.tv_blockmode= (TextView) view.findViewById(R.id.tv_blockmode);
                holder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
                //放到父亲的口袋
                view.setTag(holder);
            }else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            //减少子孩子创建的次数——内存中对象的地址,

            holder.tv_blacknumber.setText(infos.get(position).getNumber());
            String mode = infos.get(position).getMode();
            if ("1".equals(mode)){
                holder.tv_blockmode.setText("电话拦截");
            }else if ("2".equals(mode)){
                holder.tv_blockmode.setText("短信拦截");
            }else {
                holder.tv_blockmode.setText("全部拦截");
            }

            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   AlertDialog.Builder builder = new AlertDialog.Builder(CallSafeActivity.this);
                    builder.setTitle("警告");
                    builder.setMessage("确定要删除吗?");
                    builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dao.delete(infos.get(position).getNumber());
                            //更新界面
                            infos.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                    });

                    builder.setNegativeButton("取消",null);
                    builder.show();
                }
            });

            return view;
        }
    }

    //view的容器，记录孩子的内存地址，相当于一个记事本
    //static字节码只加载一次
    static class ViewHolder{
        TextView tv_blacknumber;
        TextView tv_blockmode;
        ImageView iv_delete;
    }


    private EditText et_number;
    private CheckBox cb_phone;
    private CheckBox cb_sms;
    private Button bt_ok;
    private Button bt_cancel;
    public void addBlackNumber(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View contentView = View.inflate(this,R.layout.dialog_add_blacknumber,null);

        et_number = (EditText) contentView.findViewById(R.id.et_blacknumber);
        cb_phone = (CheckBox) contentView.findViewById(R.id.cb_phone);
        cb_sms= (CheckBox) contentView.findViewById(R.id.cb_sms);
        bt_cancel= (Button) contentView.findViewById(R.id.cancel);
        bt_ok = (Button) contentView.findViewById(R.id.ok);

        dialog.setView(contentView,0,0,0,0);
        dialog.show();
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blacknumber =et_number.getText().toString().trim();
                if (TextUtils.isEmpty(blacknumber)){
                    Toast.makeText(getApplicationContext(),"黑名单号码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                String mode ="3";
                if (cb_phone.isChecked()&&cb_sms.isChecked()){
                    mode = "3";
                }else if (cb_phone.isChecked()){
                    mode = "1";
                }else if (cb_sms.isChecked()){
                    mode = "2";
                }else {
                    Toast.makeText(getApplicationContext(),"请选择拦截模式",Toast.LENGTH_SHORT).show();
                    return;
                }
                //数据加到数据库
                dao.add(blacknumber,mode);
                //更新listview的内容
                BlackNumberInfo info = new BlackNumberInfo();
                info.setNumber(blacknumber);
                info.setMode(mode);
                infos.add(0,info);
                adapter.notifyDataSetChanged();

                dialog.dismiss();
            }
        });
    }

}
