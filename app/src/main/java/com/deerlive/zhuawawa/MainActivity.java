package com.deerlive.zhuawawa;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.deerlive.zhuawawa.activity.ChargeActivity;
import com.deerlive.zhuawawa.activity.GameListActivity;
import com.deerlive.zhuawawa.activity.PlayerActivity;
import com.deerlive.zhuawawa.activity.RecordStoreActivity;
import com.deerlive.zhuawawa.activity.SettingActivity;
import com.deerlive.zhuawawa.activity.UserCenterActivity;
import com.deerlive.zhuawawa.adapter.GameRecyclerListAdapter;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.common.Contacts;
import com.deerlive.zhuawawa.common.WebviewActivity;
import com.deerlive.zhuawawa.fragment.AdialogFragment;
import com.deerlive.zhuawawa.intf.OnRecyclerViewItemClickListener;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.model.DeviceAndBanner;
import com.deerlive.zhuawawa.model.GameListBean;
import com.deerlive.zhuawawa.model.PopupBean;
import com.deerlive.zhuawawa.utils.ActivityUtils;
import com.deerlive.zhuawawa.utils.SPUtils;
import com.deerlive.zhuawawa.utils.SizeUtils;
import com.deerlive.zhuawawa.utils.TimeUtils;
import com.deerlive.zhuawawa.utils.ToastUtils;
import com.deerlive.zhuawawa.view.SpaceItemDecoration;
import com.deerlive.zhuawawa.view.update.AppUpdateUtils;
import com.deerlive.zhuawawa.view.update.CProgressDialogUtils;
import com.deerlive.zhuawawa.view.update.OkGoUpdateHttpUtil;
import com.deerlive.zhuawawa.view.update.UpdateAppBean;
import com.deerlive.zhuawawa.view.update.UpdateAppManager;
import com.deerlive.zhuawawa.view.update.UpdateCallback;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;


import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import cn.bingoogolapple.bgabanner.BGABanner;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    private ArrayList<DeviceAndBanner.InfoBean.DeviceBean> mGameData = new ArrayList();
    private ArrayList<DeviceAndBanner.BannerBean.PicBean> mBannerData = new ArrayList();
    private String token;
    private GameRecyclerListAdapter mGameAdapter = new GameRecyclerListAdapter(this, mGameData);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        token = SPUtils.getInstance().getString("token");
        int versionCode = AppUpdateUtils.getVersionCode(this);
        updateDiy(versionCode, this);
        initGameList();
        initBanner();
        mRefreshLayout.autoRefresh();
        initData();
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
                                showAdvertising();
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
                                showAdvertising();
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



    /**
     * 广告弹窗
     *
     */
    public void showAdvertising() {
        HashMap<String ,String>params=new HashMap<>();
        params.put("token",token);
        Api.getDialog(this,params, new OnRequestDataListener() {
                    @Override
                    public void requestSuccess(int code, JSONObject data) {

                        JSONObject list = data.getJSONObject("info");
                        String status = list.getString("status");
                        if("0".equals(status)){

                            long advertTime = SPUtils.getInstance().getLong("AdvertTime",1111111111111L);
                            boolean today = TimeUtils.isToday(advertTime);
                            if (!today) {
                                PopupBean popupBean = JSON.parseObject(list.toString(), PopupBean.class);
                                AdialogFragment adialogFragment= AdialogFragment.newInstance(popupBean);
                                adialogFragment.show(getSupportFragmentManager(),"adialogFragment");
                                long timeMillis = System.currentTimeMillis();
                                SPUtils.getInstance().put("AdvertTime", timeMillis);
                            }

                        }
                    }

                    @Override
                    public void requestFailure(int code, String msg) {

                    }
                });

    }




    public void userCenter(View v) {
        ActivityUtils.startActivity(UserCenterActivity.class);
    }

    public void setCenter(View v) {
        ActivityUtils.startActivity(SettingActivity.class);
    }

    private void initData() {
        getGameData(0);

    }
    private DeviceAndBanner deviceAndBanner;
    private void getGameData(final int limit_begin) {
        Map<String,String>params=new HashMap<>();
        params.put("limit_begin", String.valueOf(limit_begin));
        params.put("limit_num", 10+"");
        params.put("token", token);
        Api.getGameList(this, params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                if (limit_begin == 0) {
                    mGameData.clear();
                }
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.finishRefresh();
                }
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.finishLoadmore();
                }
                 deviceAndBanner = JSON.parseObject(data.toString(), DeviceAndBanner.class);

                mGameData.addAll(deviceAndBanner.getInfo().getDevice());
                mGameAdapter.notifyDataSetChanged();
                mBannerData.clear();
                mBannerData.addAll(deviceAndBanner.getBanner().getPic());
                mConvenientBanner.setData(mBannerData,null);
            }

            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
                if (limit_begin == 0) {
                    mGameData.clear();
                    mGameAdapter.notifyDataSetChanged();
                }
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.finishRefresh();
                }
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.finishLoadmore();
                }
            }
        });
    }



    private void initGameList() {
        final GridLayoutManager manager = new GridLayoutManager(this, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mGameAdapter.haveHeaderView() && position == 0) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(SizeUtils.dp2px(5)));
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mGameAdapter);
        mGameAdapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onRecyclerViewItemClick(View view, int position) {
                Bundle d = new Bundle();
                d.putSerializable("item", mGameData.get(position));
                ActivityUtils.startActivity(d, PlayerActivity.class);
            }
        });

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData();
            }
        });
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getGameData(mGameData.size());
            }
        });
    }

    LinearLayout layoutInvite;
    LinearLayout layoutIntegral;
    LinearLayout layoutCharge;
    private BGABanner mConvenientBanner;

    private void initBanner() {
        LinearLayout temp = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_home_banner, null);
        mConvenientBanner = (BGABanner) temp.findViewById(R.id.convenientBanner);
        layoutInvite = (LinearLayout) temp.findViewById(R.id.layout_invite);
        layoutIntegral = (LinearLayout) temp.findViewById(R.id.layout_integral);
        layoutCharge = (LinearLayout) temp.findViewById(R.id.layout_charge);
        mGameAdapter.addHeaderView(temp);


        mConvenientBanner.setAdapter(new BGABanner.Adapter<ImageView, DeviceAndBanner.BannerBean.PicBean>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, DeviceAndBanner.BannerBean.PicBean model, int position) {

                Glide.with(MainActivity.this)
                        .load(model.getImg())
                        .centerCrop()
                        .into(itemView);

            }
        });
        mConvenientBanner.setDelegate(new BGABanner.Delegate<ImageView, DeviceAndBanner.BannerBean.PicBean>() {
            @Override
            public void onBannerItemClick(BGABanner banner, ImageView itemView, DeviceAndBanner.BannerBean.PicBean model, int position) {
                if (mBannerData.get(position).getUrl()!=null&&!mBannerData.get(position).getUrl().isEmpty()) {
                    DeviceAndBanner.BannerBean.PicBean b = mBannerData.get(position);
                    Bundle temp = new Bundle();
                    temp.putString("title", b.getTitle());
                    temp.putString("jump", b.getUrl());
                    ActivityUtils.startActivity(temp, WebviewActivity.class);
                }


            }
        });



        layoutInvite.setOnClickListener(this);
        layoutIntegral.setOnClickListener(this);
        layoutCharge.setOnClickListener(this);
    }




    @Override
    public int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.layout_invite:
               ActivityUtils.startActivity(GameListActivity.class);
                break;
            case  R.id.layout_integral:
                ActivityUtils.startActivity(RecordStoreActivity.class);
                break;
            case  R.id.layout_charge:
                ActivityUtils.startActivity(ChargeActivity.class);
                break;
            default:
                break;
        }
    }
    private long mLastBackTime = 0;
    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mLastBackTime) < 1000) {
            finish();
        } else {
            mLastBackTime = System.currentTimeMillis();
            ToastUtils.showShort( "再按一次退出");
        }


    }
}
