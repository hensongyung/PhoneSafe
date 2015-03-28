package com.hensonyung.phonesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hensonyung.phonesafe.R;

/**
 * Created by wengjiasheng on 2015/3/21.
 */
public class SettingClickView extends RelativeLayout {
    private CheckBox cb_status;
    private TextView tv_desc;
    private TextView tv_title;

    private String desc_on;
    private String desc_off;
    private void iniView(Context context){
        View.inflate(context, R.layout.setting_click_view,this);

        tv_desc = (TextView) this.findViewById(R.id.tv_desc);
        tv_title = (TextView) this.findViewById(R.id.tv_title);

    }

    public SettingClickView(Context context) {
        super(context);
        iniView(context);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        iniView(context);
        String title =attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","title");
        desc_on =attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","desc_on");
        desc_off =attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","desc_off");

        tv_title.setText(title);
        setDesc(desc_off);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iniView(context);
    }



    public void setChecked(boolean checked){
        if (checked){
            setDesc(desc_on);
        }else {
            setDesc(desc_off);
        }

    }

    public void setDesc(String text){
        tv_desc.setText(text);
    }

    //设置组合控件的标题
    public void setTitle(String title){
        tv_title.setText(title);
    }
}
