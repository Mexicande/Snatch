package com.deerlive.zhuawawa.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.adapter.RecordZjRecyclerListAdapter;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.common.Contacts;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.model.DanmuMessage;
import com.deerlive.zhuawawa.utils.SPUtils;
import com.deerlive.zhuawawa.utils.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class RankFragment extends DialogFragment {


    @Bind(R.id.rank_list)
    RecyclerView rankList;
    @Bind(R.id.iv_off)
    ImageView ivOff;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private String mToken;

    private ArrayList<DanmuMessage> mRecordZjDate = new ArrayList<>();
    private RecordZjRecyclerListAdapter mRecordZjAdapter;

    public RankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rank, container, false);
        final Window window = getDialog().getWindow();
        if (window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(wlp);
        }
        ButterKnife.bind(this, view);
        mToken = SPUtils.getInstance().getString("token");
        initView();
        getDate(0);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Base_AlertDialog);
    }

    private void initView() {

        mRecordZjAdapter = new RecordZjRecyclerListAdapter(mRecordZjDate);
        LinearLayoutManager m = new LinearLayoutManager(getContext());
        m.setOrientation(LinearLayoutManager.VERTICAL);
        rankList.setLayoutManager(m);
        rankList.setAdapter(mRecordZjAdapter);
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getDate(mRecordZjDate.size());
            }
        });
    }
    private void getDate(int page) {
        Map<String, String> params = new HashMap<>();
        params.put("token", mToken);
        params.put("type", Contacts.ADD_TYPE);
        params.put("limit_begin", String.valueOf(page));
        params.put("limit_num", 10 + "");
        Api.getRanklsit(getActivity(), params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                JSONArray info = data.getJSONArray("info");
                mRecordZjDate.clear();
                if (refreshLayout.isLoading()) {
                    refreshLayout.finishLoadmore();
                }
                for (int i = 0; i < info.size(); i++) {
                    JSONObject t = info.getJSONObject(i);
                    DanmuMessage item1 = new DanmuMessage();
                    item1.setUid(t.getString("uid"));
                    item1.setUserName(t.getString("user_nicename"));
                    item1.setAvator(t.getString("avatar"));
                    item1.setMessageContent(t.getString("play_time"));
                    mRecordZjDate.add(item1);
                }
                mRecordZjAdapter.setNewData(mRecordZjDate);
            }

            @Override
            public void requestFailure(int code, String msg) {
                ToastUtils.showShort(msg);
                if (refreshLayout.isLoading()) {
                    refreshLayout.finishLoadmore();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
