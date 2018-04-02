package com.deerlive.zhuawawa.activity.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.common.Contacts;
import com.deerlive.zhuawawa.common.WebviewActivity;
import com.deerlive.zhuawawa.fragment.RankFragment;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.utils.ActivityUtils;
import com.deerlive.zhuawawa.utils.SPUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

public class AddSpeedActivity extends BaseActivity {

    @Bind(R.id.layout_phone)
    RelativeLayout layoutPhone;
    @Bind(R.id.bt_start)
    TextView btStart;
    @Bind(R.id.tv_spend)
    TextView tvSpend;
    @Bind(R.id.view)
    ImageView view;
    private String mToken;
    private int mBlance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToken = SPUtils.getInstance().getString("token");
        layoutPhone.setRotation(8);
        view.setRotation(-8);
        getDate();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_add_speed;
    }

    private void getDate() {
        Map<String, String> params = new HashMap<>();
        params.put("token", mToken);
        params.put("type", Contacts.ADD_TYPE);

        Api.getGameFree(this, params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                JSONObject info = data.getJSONObject("info");
                String number = info.getString("number");
                if ("0".equals(number)) {
                    btStart.setText(R.string.tv_start);
                } else {
                    btStart.setText(R.string.tv_start_fress);
                }
                String count = info.getString("count");
                mBlance = info.getInteger("balance");
                // tvCount.setText("当前共有" + count + "参赛");
                tvSpend.setText(mBlance + "金币/次");

            }

            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
            }
        });

    }

    @OnClick({R.id.bt_back, R.id.bt_share, R.id.bt_start, R.id.bt_ranking})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_back:
                finish();
                break;
            case R.id.bt_share:
                Bundle temp = new Bundle();
                temp.putString("type", Contacts.ATTENTION_TYPE);
                temp.putString("title", getResources().getString(R.string.yaoqing_me));
                temp.putString("jump", Api.URL_GAME_YAOQING + "&token=" + mToken);
                Intent intent = new Intent(this, WebviewActivity.class);
                intent.putExtras(temp);
                startActivityForResult(intent, Contacts.QUESTION_CODE);
                break;
            case R.id.bt_start:
                ActivityUtils.startActivity(AddChildActivity.class);

                //startGame();
                break;
            case R.id.bt_ranking:
                RankFragment rankFragment=new RankFragment();
                rankFragment.show(getSupportFragmentManager(),"rankFragment");
                //排行榜
                break;
            default:
                break;
        }
    }

    /**
     * 开始游戏
     */
    private void startGame() {
        Map<String, String> params = new HashMap<>();
        params.put("token", mToken);
        params.put("type", Contacts.ADD_TYPE);
        Api.setStartGame(this, params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {


                Intent intent = new Intent(AddSpeedActivity.this, AddChildActivity.class);
                intent.putExtra("blance", mBlance);
                startActivityForResult(intent, Contacts.QUESTION_CODE);
            }

            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Contacts.QUESTION_CODE) {
            if (resultCode == Contacts.RESULT_CODE) {
                getDate();
            }
        }
    }
}
