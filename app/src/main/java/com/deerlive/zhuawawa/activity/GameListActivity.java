package com.deerlive.zhuawawa.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.activity.game.AddSpeedActivity;
import com.deerlive.zhuawawa.activity.game.AttentionActivity;
import com.deerlive.zhuawawa.adapter.GameAdapter;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.common.WebviewActivity;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.model.GameListBean;
import com.deerlive.zhuawawa.utils.ActivityUtils;
import com.deerlive.zhuawawa.utils.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.HashMap;

import butterknife.Bind;

public class GameListActivity extends BaseActivity {

    @Bind(R.id.layout_top_back)
    ImageView layoutTopBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    private GameAdapter mGameAdapter;

    public void goBack(View v) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvTitle.setText(R.string.title_game);
        initView();
        initDate();
        setListener();
    }

    private void initDate() {

        Api.setGetGame(this, new HashMap<String, String>(), new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.finishRefresh();
                }
                    GameListBean gameListBean = JSON.parseObject(data.toString(), GameListBean.class);
                    if (gameListBean.getInfo()!=null){
                        mGameAdapter.setNewData(gameListBean.getInfo());
                    }
            }

            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.finishRefresh();
                }
            }
        });
    }

    private void setListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initDate();
            }
        });
        mGameAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GameListBean.InfoBean item = mGameAdapter.getItem(position);
                if("2".equals(item.getStyle())){
                    Bundle temp = new Bundle();
                    temp.putString("title", item.getName());
                    temp.putString("jump", item.getUrl());
                    ActivityUtils.startActivity(temp, WebviewActivity.class);
                }else {
                    if("1".equals(item.getType())){
                        ActivityUtils.startActivity(AddSpeedActivity.class);
                    }else {
                        ActivityUtils.startActivity(AttentionActivity.class);
                    }
                }
            }
        });
    }

    private void initView() {
        mGameAdapter=new GameAdapter(null);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(mGameAdapter);

    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_game_list;
    }
}
