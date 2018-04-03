package com.deerlive.zhuawawa.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.adapter.WeiQuRecyclerListAdapter;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.fragment.WeiQuDialogFragment;
import com.deerlive.zhuawawa.intf.OnRecyclerViewItemClickListener;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.intf.WeiQuInterface;
import com.deerlive.zhuawawa.model.GrabBean;
import com.deerlive.zhuawawa.utils.SPUtils;
import com.deerlive.zhuawawa.view.dialog.CashDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

public class WeiQuListActivity extends BaseActivity implements OnRecyclerViewItemClickListener,WeiQuInterface{

    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.iv_default)
    ImageView ivDefault;
    @Bind(R.id.duihuan)
    TextView duihuan;
    @Bind(R.id.tiqu)
    TextView tiqu;
    private String mToken;
    private CashDialog cashDialog;
    private ArrayList<GrabBean.InfoBean> mListData = new ArrayList();
    private WeiQuRecyclerListAdapter mAdapter = new WeiQuRecyclerListAdapter(this, mListData);

    public void goBack(View v) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tvTitle.setText(getResources().getString(R.string.wuqu_title));
        mToken = SPUtils.getInstance().getString("token");
        mRefreshLayout.autoRefresh();
        initGameList();
    }

    private void initGameList() {
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        //mRecyclerView.addItemDecoration(new SpaceItemDecoration(SizeUtils.dp2px(10)));
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getGameData(0);
            }
        });
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getGameData(mListData.size());
            }
        });
        mAdapter.setOnRecyclerViewItemClickListener(this);
    }

    private void getGameData(final int limit_begin) {

        Map<String,String> params=new HashMap<>();
        params.put("token", mToken);
        params.put("limit_begin", String.valueOf(limit_begin));
        params.put("limit_num", 10+"");
        Api.getNoTakenWawa(this, params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                if (limit_begin == 0) {
                    mListData.clear();
                }
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.finishRefresh();
                }
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.finishLoadmore();
                }
                GrabBean grabBean = JSON.parseObject(data.toString(), GrabBean.class);

                mListData.addAll(grabBean.getInfo());

                for(int i=0;i<grabBean.getInfo().size();i++){
                    grabBean.getInfo().get(i).setRemoteUid(0);
                }

                if (mListData.size() != 0) {
                    ivDefault.setVisibility(View.GONE);
                }else {
                    ivDefault.setVisibility(View.VISIBLE);

                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void requestFailure(int code, String msg) {
                if (mListData.size() == 0) {
                    ivDefault.setVisibility(View.VISIBLE);

                }
                if (limit_begin == 0) {
                    mListData.clear();
                    mAdapter.notifyDataSetChanged();
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

    @Override
    public int getLayoutResource() {
        return R.layout.activity_weiqu_list;
    }

    @Override
    public void onRecyclerViewItemClick(View view, int position) {

        int s = mListData.get(position).getRemoteUid();
        if (s == 1) {
            mListData.get(position).setRemoteUid(0);
            ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

            mAdapter.notifyItemChanged(position);
            duihuan.setBackgroundResource(R.drawable.prize_button);
            duihuan.setEnabled(true);
            for(int i=0;i<mListData.size();i++){
               if(mListData.get(i).getChange()==1&&mListData.get(i).getRemoteUid()==1){
                   duihuan.setBackgroundResource(R.drawable.iv_duihuan_press);
                   duihuan.setEnabled(false);

               }
            }

        }
        if (s == 0) {
            mListData.get(position).setRemoteUid(1);
            mAdapter.notifyItemChanged(position);

            if (mListData.get(position).getChange() == 0) {

                for(int i=0;i<mListData.size();i++){
                    if (mListData.get(i).getChange() == 1) {
                        if(mListData.get(i).getRemoteUid()==1){
                            mListData.get(i).setRemoteUid(0);
                            ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

                            mAdapter.notifyItemChanged(i);
                        }
                    }
                }

                duihuan.setBackgroundResource(R.drawable.prize_button);
                duihuan.setEnabled(true);

            }else if(mListData.get(position).getChange() == 1){
                for(int i=0;i<mListData.size();i++){
                    if (mListData.get(i).getChange() == 0) {
                        if(mListData.get(i).getRemoteUid()==1){
                            mListData.get(i).setRemoteUid(0);
                            ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

                            mAdapter.notifyItemChanged(i);
                        }

                    }
                }
                duihuan.setBackgroundResource(R.drawable.iv_duihuan_press);
                duihuan.setEnabled(false);
            }
        }
    }

    /**
     * 提取
     *
     * @param v
     */
    public void tiqu(View v) {
        final List<GrabBean.InfoBean> list = new ArrayList<>();
        for (GrabBean.InfoBean mgrab : mListData) {
            if (mgrab.getRemoteUid() == 1) {
                list.add(mgrab);
            }
        }
        if (!list.isEmpty()) {
            cashDialog = new CashDialog(this);
            cashDialog.setYesOnclickListener("是", new CashDialog.onYesOnclickListener() {
                @Override
                public void onYesClick() {
                    cashDialog.dismiss();

                    tiquDuihuan("1", list);
                }
            });
            cashDialog.setNoOnclickListener("否", new CashDialog.onNoOnclickListener() {
                @Override
                public void onNoClick() {
                    cashDialog.dismiss();
                }
            });
            cashDialog.show();
        } else if (mListData.size() != 0) {
            toast(getResources().getString(R.string.data_empty_error));
        }
    }

    /**
     * 兑换
     *
     * @param v
     */
    private List<GrabBean.InfoBean> Grabbiest = new ArrayList<>();
    public void duihuan(View v) {
        Grabbiest.clear();
        int price=0;
        for (GrabBean.InfoBean mgrab : mListData) {
            if (mgrab.getRemoteUid() == 1) {
                Grabbiest.add(mgrab);
                price=price+Integer.parseInt(mgrab.getExchange_price());
            }
        }
        if (!Grabbiest.isEmpty()) {
            WeiQuDialogFragment weiQuDialogFragment=WeiQuDialogFragment.
                    newInstance(String.valueOf(Grabbiest.size()),String.valueOf(price));
            weiQuDialogFragment.show(getSupportFragmentManager(),"weiQuDialogFragment");

        } else if (mListData.size() != 0) {
            toast(getResources().getString(R.string.data_empty_error));
        }
    }

    private void tiquDuihuan(String type, List<GrabBean.InfoBean> list) {

        GrabBean grabBean = new GrabBean();
        grabBean.setInfo(list);
        grabBean.setToken(mToken);
        grabBean.setType(type);
        String s = JSON.toJSONString(grabBean);
        JSONObject jsonObject = JSON.parseObject(s);

        Api.applyPostOrDuiHuanWaWa(this, jsonObject, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                toast(data.getString("descrp"));
                SPUtils.getInstance().put("balance", data.getString("balance"));
                getGameData(0);
            }

            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
            }
        });
    }


    @Override
    public void requestSure() {
        tiquDuihuan("0", Grabbiest);
    }

    @Override
    public void requestCancel() {

    }
}
