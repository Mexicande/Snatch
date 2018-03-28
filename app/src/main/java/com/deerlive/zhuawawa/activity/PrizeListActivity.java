package com.deerlive.zhuawawa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.adapter.ReplaceAdapter;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.common.Contacts;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.model.GrabBean;
import com.deerlive.zhuawawa.utils.SPUtils;
import com.deerlive.zhuawawa.utils.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

public class PrizeListActivity extends BaseActivity {

    @Bind(R.id.layout_top_back)
    ImageView layoutTopBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.bt_capture)
    Button btCapture;
    private String mToken;
    private ReplaceAdapter mReplaceAdapter;
    private ArrayList<GrabBean.InfoBean> mArrays = new ArrayList<>();
    private int mPositon = 0;
    private int mBt=0;
    private int mDevice_id=0;
    public void goBack(View v) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToken = SPUtils.getInstance().getString("token");
        tvTitle.setText(R.string.title_prize);
        initView();
        getDate(0);
        setListener();
    }

    private void setListener() {
      /*  mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getDate(0);
            }
        });*/
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getDate(mArrays.size());
            }
        });
        mReplaceAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mPositon = position;
                GrabBean.InfoBean item = mReplaceAdapter.getItem(position);
                if (item.getRemoteUid() == 0) {
                    item.setRemoteUid(1);
                    mArrays.set(position, item);
                    ((SimpleItemAnimator) recyclerview.getItemAnimator()).setSupportsChangeAnimations(false);
                    mReplaceAdapter.notifyItemChanged(position);
                    for (int i = 0; i < mArrays.size(); i++) {
                        if (i != position && mArrays.get(i).getRemoteUid() == 1) {
                            mArrays.get(i).setRemoteUid(0);
                            mArrays.set(i, mArrays.get(i));
                            ((SimpleItemAnimator) recyclerview.getItemAnimator()).setSupportsChangeAnimations(false);
                            mReplaceAdapter.notifyItemChanged(i);
                        }
                    }

                }
            }
        });
    }


    private void initView() {

        mReplaceAdapter = new ReplaceAdapter(null);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(mReplaceAdapter);

    }

    private void getDate(final int limit_begin) {
        mDevice_id = getIntent().getIntExtra(Contacts.DEVICE_ID, 0);
        mBt = getIntent().getIntExtra("bt", 10);
        if (mBt != 10) {
            if (mBt == 1) {
                btCapture.setText("提取");
            }
        }
        Map<String, String> params = new HashMap<>();
        params.put("token", mToken);
        params.put("device_id", mDevice_id + "");
        params.put("limit_begin", String.valueOf(limit_begin));
        params.put("limit_num", 10 + "");

        Api.getDollList(this, params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                if (limit_begin == 0) {
                    mArrays.clear();
                }
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.finishLoadmore();
                }
                if (data != null) {
                    GrabBean grabBean = JSON.parseObject(data.toString(), GrabBean.class);
                    mArrays.addAll(grabBean.getInfo());
                    mReplaceAdapter.setNewData(mArrays);
                }
            }

            @Override
            public void requestFailure(int code, String msg) {
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.finishLoadmore();
                }
            }
        });
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_prize_list;
    }

    @OnClick(R.id.bt_capture)
    public void onViewClicked() {
        if (mPositon != 0) {
            if(mBt==1){
                doReplaceDoll();
            }else {
                Intent intent = new Intent();
                intent.putExtra("id", mReplaceAdapter.getItem(mPositon).getId());
                setResult(Contacts.RESULT_CODE, intent);
                finish();
            }
        } else {
            ToastUtils.showShort("您还没选择娃娃");
        }
    }

    private void doReplaceDoll() {
        Map<String, String> params = new HashMap<>();
        params.put("token", mToken);
        params.put("device_id", mDevice_id + "");
        params.put("doll_id", mArrays.get(mPositon).getId());
        Api.getReplacedoll(this, params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                String descrp = data.getString("descrp");
                ToastUtils.showShort(descrp);
                finish();
            }

            @Override
            public void requestFailure(int code, String msg) {
                ToastUtils.showShort(msg);
            }
        });

    }
}
