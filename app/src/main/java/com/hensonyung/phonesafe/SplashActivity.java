package com.hensonyung.phonesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.hensonyung.phonesafe.utils.StreamTools;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class SplashActivity extends Activity{
    private static final String TAG ="SplashActivity" ;
    private TextView tv_splash_version;
    private String description;
    private TextView tv_update_info;

    private static final int SHOW_UPDATE_DIALOG=0;
    private static final int ENTER_HOME = 1;
    private static final int URL_ERROR= 2;
    private static final int NETWORK_ERROR=3;
    private static final int JSON_ERROR =4;

    private String apkurl;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sp = getSharedPreferences("config",MODE_PRIVATE);

        tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
        tv_splash_version.setText("Version:" +getVersionName());

        tv_update_info = (TextView) findViewById(R.id.tv_update_info);

        boolean update = sp.getBoolean("update",false);

        //拷贝数据库
        copyDB();

        if (update){
            checkUpdate();
        }else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    enterHome();
                }
            },2000);
        }

        AlphaAnimation aa = new AlphaAnimation(0.2f,1.0f);
        aa.setDuration(1000);
        findViewById(R.id.rl_root_splash).startAnimation(aa);
        boolean shortcut = sp.getBoolean("shortcut",false);
        if (!shortcut) {
            installShortCut();
        }else {
            Toast.makeText(this,"已创建快捷键",Toast.LENGTH_SHORT).show();
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_UPDATE_DIALOG:// 显示升级的对话框
                    Log.i(TAG, "显示升级的对话框");
                    showUpdateDialog();
                    break;
                case ENTER_HOME:// 进入主页面
                    enterHome();
                    break;

                case URL_ERROR:// URL错误
                    enterHome();
                    Toast.makeText(getApplicationContext(), "URL错误", Toast.LENGTH_SHORT).show();

                    break;

                case NETWORK_ERROR:// 网络异常
                    enterHome();
                    Toast.makeText(SplashActivity.this, "网络异常",Toast.LENGTH_SHORT).show();
                    break;

                case JSON_ERROR:// JSON解析出错
                    enterHome();
                    Toast.makeText(SplashActivity.this, "JSON解析出错", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };



    private void checkUpdate() {

        new Thread() {
            public void run() {
                // URLhttp://192.168.1.254:8080/updateinfo.html

                Message mes = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {

                    URL url = new URL("1");
                    // 联网
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(4000);
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        // 联网成功
                        InputStream is = conn.getInputStream();
                        // 把流转成String
                        String result = StreamTools.readFromStream(is);
                        Log.i(TAG, "联网成功了" + result);
                        // json解析
                        JSONObject obj = new JSONObject(result);
                        // 得到服务器的版本信息
                        String version = (String) obj.get("version");

                        description = (String) obj.get("description");
                        apkurl = (String) obj.get("apkurl");

                        // 校验是否有新版本
                        if (getVersionName().equals(version)) {
                            // 版本一致，没有新版本，进入主页面
                            mes.what = ENTER_HOME;
                        } else {
                            // 有新版本，弹出一升级对话框
                            mes.what = SHOW_UPDATE_DIALOG;

                        }

                    }

                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    mes.what = URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    mes.what = NETWORK_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    mes.what = JSON_ERROR;
                } finally {

                    long endTime = System.currentTimeMillis();
                    // 我们花了多少时间
                    long dTime = endTime - startTime;
                    // 2000
                    if (dTime < 2000) {
                        try {
                            Thread.sleep(2000 - dTime);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    handler.sendMessage(mes);
                }

            };
        }.start();

    }

    private String getVersionName(){
        PackageManager pm = getPackageManager();
        try {
           PackageInfo info = pm.getPackageInfo(getPackageName(), 0);

            return info.versionName; //得到版本名称

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }

    }

    protected void showUpdateDialog() {
        //this = Activity.this
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("提示升级");
//		builder.setCancelable(false);//强制升级
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                //进入主页面
                enterHome();
                dialog.dismiss();

            }
        });
        builder.setMessage(description);
        builder.setPositiveButton("立刻升级", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 下载APK，并且替换安装
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    // sdcard存在
                    // afnal
                    FinalHttp finalhttp = new FinalHttp();
                    finalhttp.download(apkurl, Environment
                                    .getExternalStorageDirectory().getAbsolutePath()+"/mobilesafe2.0.apk",
                            new AjaxCallBack<File>() {

                                @Override
                                public void onFailure(Throwable t, int errorNo,
                                                      String strMsg) {
                                    t.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
                                    super.onFailure(t, errorNo, strMsg);
                                }

                                @Override
                                public void onLoading(long count, long current) {
                                    // TODO Auto-generated method stub
                                    super.onLoading(count, current);
                                    tv_update_info.setVisibility(View.VISIBLE);
                                    //当前下载百分比
                                    int progress = (int) (current * 100 / count);
                                    tv_update_info.setText("下载进度："+progress+"%");
                                }

                                @Override
                                public void onSuccess(File t) {
                                    // TODO Auto-generated method stub
                                    super.onSuccess(t);
                                    installAPK(t);
                                }
                                /**
                                 * 安装APK
                                 * @param t
                                 */
                                private void installAPK(File t) {
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.VIEW");
                                    intent.addCategory("android.intent.category.DEFAULT");
                                    intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");

                                    startActivity(intent);

                                }


                            });
                } else {
                    Toast.makeText(getApplicationContext(), "没有sdcard，请安装上在试",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
        builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                enterHome();// 进入主页面
            }
        });
        builder.show();

    }

    protected void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        // 关闭当前页面
        finish();

    }

    private void copyDB(){
        //只要拷贝一次就不用再拷贝了。
        try {
            File file = new File(getFilesDir(),"address.db");
            if (file.exists()&&file.length()>0){
                System.out.println("stand by");
            }else {

                InputStream is = getAssets().open("address.db");

                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                is.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("can't find ");
        }
    }

    //创建快捷图标
    private void installShortCut(){
        SharedPreferences.Editor editor = sp.edit();

        //发送广播意图
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //快捷方式要包含3个重要信息，名称、图标、干什么事情
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"手机小卫士");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher));
        //桌面点击图标的意图
        Intent shortCutIntent = new Intent();
        shortCutIntent.setAction("android.intent.action.MAIN");
        shortCutIntent.addCategory("android.intent.category.LAUNCHER");
        shortCutIntent.setClassName(getPackageName(),"com.hensonyung.phonesafe.SplashActivity");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,shortCutIntent);
        sendBroadcast(intent);

        editor.putBoolean("shortcut",true);
        editor.commit();

    }
}
