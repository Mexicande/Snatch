package com.deerlive.zhuawawa.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.common.Contacts;
import com.deerlive.zhuawawa.common.WebviewActivity;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.utils.ActivityUtils;
import com.deerlive.zhuawawa.utils.AppUtils;
import com.deerlive.zhuawawa.utils.LogUtils;
import com.deerlive.zhuawawa.utils.SPUtils;
import com.deerlive.zhuawawa.utils.ToastUtils;
import com.deerlive.zhuawawa.view.supertextview.SuperTextView;
import com.deerlive.zhuawawa.view.update.AppUpdateUtils;
import com.deerlive.zhuawawa.view.update.CProgressDialogUtils;
import com.deerlive.zhuawawa.view.update.OkGoUpdateHttpUtil;
import com.deerlive.zhuawawa.view.update.UpdateAppBean;
import com.deerlive.zhuawawa.view.update.UpdateAppManager;
import com.deerlive.zhuawawa.view.update.UpdateCallback;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

public class SettingActivity extends BaseActivity {
    @Bind(R.id.checkbox_bgm)
    SuperTextView checkboxBgm;
    @Bind(R.id.checkbox_yinxiao)
    SuperTextView checkboxYinxiao;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.bt_version)
    SuperTextView btVersion;
    private String token;

    public void goBack(View v) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tvTitle.setText("设置");
        token = SPUtils.getInstance().getString("token");
        if ("1".equals(SPUtils.getInstance().getString("bgm"))) {
            checkboxBgm.setSwitchIsChecked(true);
        } else {
            checkboxBgm.setSwitchIsChecked(false);
        }
        if ("1".equals(SPUtils.getInstance().getString("yinxiao"))) {
            checkboxYinxiao.setSwitchIsChecked(true);
        } else {
            checkboxYinxiao.setSwitchIsChecked(false);
        }
        setListener();

    }

    private void setListener() {
        String versionName = AppUtils.getAppVersionName();
        btVersion.setRightString("v"+versionName);

        checkboxBgm.setSwitchCheckedChangeListener(new SuperTextView.OnSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SPUtils.getInstance().put("bgm", "1");
                } else {
                    SPUtils.getInstance().put("bgm", "0");
                }
            }
        });
        checkboxYinxiao.setSwitchCheckedChangeListener(new SuperTextView.OnSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SPUtils.getInstance().put("yinxiao", "1");
                } else {
                    SPUtils.getInstance().put("yinxiao", "0");
                }
            }
        });

    }

    public void logout(View v) {
        SPUtils.getInstance().remove("token");
        ActivityUtils.finishAllActivities();
        ActivityUtils.startActivity(LoginActivity.class);
    }

    public void gamehelp(View v) {
        Bundle temp = new Bundle();
        temp.putString("title", getResources().getString(R.string.set_helps));
        temp.putString("jump", Api.URL_GAME_HELP + "&token=" + token);
        ActivityUtils.startActivity(temp, WebviewActivity.class);
    }

    public void yaoqing(View v) {
        Bundle temp = new Bundle();
        temp.putString("title", getResources().getString(R.string.yaoqing_me));
        temp.putString("jump", Api.URL_GAME_YAOQING + "&token=" + token);
        ActivityUtils.startActivity(temp, WebviewActivity.class);
    }

    public void yaoqingma(View v) {
        Bundle temp = new Bundle();
        temp.putString("title", getResources().getString(R.string.yaoqing_input));
        temp.putString("jump", Api.URL_GAME_YAOQINGMA + "&token=" + token);
        ActivityUtils.startActivity(temp, WebviewActivity.class);
    }

    public void feedback(View v) {
        Bundle temp = new Bundle();
        temp.putString("title", getResources().getString(R.string.feadback));
        temp.putString("jump", Api.URL_GAME_FEEDBACK + "&token=" + token);
        ActivityUtils.startActivity(temp, WebviewActivity.class);
    }

    public void checkUpdate(View v) {
        int  versionCode = AppUpdateUtils.getVersionCode(this);
        updateDiy(versionCode,this);
    }
    private void updateDiy(int versionCode, final Activity context) {
        Map<String, String> params = new HashMap<String, String>();
        final int finalVersionCode = versionCode;
        new UpdateAppManager
                .Builder()
                .setActivity(context)
                //必须设置，实现httpManager接口的对象
                .setHttpManager(new OkGoUpdateHttpUtil())
                //必须设置，更新地址
                .setUpdateUrl(Api.GET_UPDATE)
                //以下设置，都是可选
                .setPost(true)
                //不显示通知栏进度条
//                .dismissNotificationProgress()
                //是否忽略版本
//                .showIgnoreVersion()
                //添加自定义参数，默认version=1.0.0（app的versionName）；apkKey=唯一表示（在AndroidManifest.xml配置）
                .setParams(params)
                //设置点击升级后，消失对话框，默认点击升级后，对话框显示下载进度
                .hideDialogOnDownloading(false)
                //设置头部，不设置显示默认的图片，设置图片后自动识别主色调，然后为按钮，进度条设置颜色
                //为按钮，进度条设置颜色。
                //.setThemeColor(0xffffac5d)
                //设置apk下砸路径，默认是在下载到sd卡下/Download/1.0.0/test.apk
//                .setTargetPath(path)
                //设置appKey，默认从AndroidManifest.xml获取，如果，使用自定义参数，则此项无效
//                .setAppKey("ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f")
                .build()
                //检测是否有新版本
                .checkNewApp(new UpdateCallback() {
                    /**
                     * 解析json,自定义协议
                     *
                     * @param json 服务器返回的json
                     * @return UpdateAppBean
                     */
                    @Override
                    protected UpdateAppBean parseJson(String json) {

                        UpdateAppBean updateAppBean = new UpdateAppBean();
                        try {
                            JSONObject info = JSON.parseObject(json);
                            JSONObject jsonObject = info.getJSONObject("info");

                            Integer code = info.getInteger("code");
                            if(code== Contacts.UPDATE_ERROR){
                                ToastUtils.showShort("已是最新版本！");

                            }
                            int size = Integer.parseInt(jsonObject.getString("size"));
                            Double i = (double) size / 1024/1024;
                            DecimalFormat df = new DecimalFormat("0.0");
                            String format = df.format(i);
                            int versioncode = Integer.parseInt(jsonObject.getString("versioncode"));
                            String update="No";
                            if(versioncode> finalVersionCode){
                                update="Yes";
                            }else {
                                ToastUtils.showShort("已是最新版本！");
                            }
                            updateAppBean
                                    //（必须）是否更新Yes,No
                                    .setUpdate(update)
                                    //（必须）新版本号，
                                    .setNewVersion(jsonObject.getString("versionname"))
                                    //（必须）下载地址
                                    .setApkFileUrl(jsonObject.getString("url"))
                                    //测试下载路径是重定向路径
//                                    .setApkFileUrl("http://openbox.mobilem.360.cn/index/d/sid/3282847")
                                    //（必须）更新内容
//                                    .setUpdateLog(jsonObject.optString("update_log"))
                                    //测试内容过度
//                                    .setUpdateLog("测试")
                                    .setUpdateLog(jsonObject.getString("updatecontent"))
//                                    .setUpdateLog("今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说\r\n")
                                    //大小，不设置不显示大小，可以不设置
                                    .setTargetSize(String.valueOf(format)+"M")
                                    //是否强制更新，可以不设置
                                    .setConstraint(jsonObject.getBoolean("isForce"))
                                    //设置md5，可以不设置
                                    .setNewMd5(jsonObject.getString("md5"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return updateAppBean;

                    }

                    @Override
                    protected void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
                        updateAppManager.showDialogFragment();
                    }
                    /**
                     * 网络请求之前
                     */
                    @Override
                    public void onBefore() {
                        // CProgressDialogUtils.showProgressDialog(MainActivity.this);

                    }
                    /**
                     * 网路请求之后
                     */
                    @Override
                    public void onAfter() {
                        CProgressDialogUtils.cancelProgressDialog(context);

                    }
                });



    }





    @Override
    public int getLayoutResource() {
        return R.layout.activity_setting;
    }


}
