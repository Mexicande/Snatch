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
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.utils.ActivityUtils;
import com.deerlive.zhuawawa.utils.SPUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 注意力大挑战
 */
public class AttentionActivity extends BaseActivity {

    @Bind(R.id.bt_share)
    ImageView btShare;
    @Bind(R.id.tv_spend)
    TextView tvSpend;
    @Bind(R.id.bt_start)
    RelativeLayout btStart;
    @Bind(R.id.tv_count)
    TextView tvCount;
    @Bind(R.id.tv_start)
    TextView tvStart;
    private String mToken;
    private int mBlance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToken = SPUtils.getInstance().getString("token");

        getDate();
    }


    private void getDate() {
        Map<String, String> params = new HashMap<>();
        params.put("token", mToken);
        params.put("type", Contacts.ATTENTION_TYPE);

        Api.getGameFree(this, params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                JSONObject info = data.getJSONObject("info");
                String number = info.getString("number");
                if("0".equals(number)){
                    tvStart.setText(R.string.tv_start);
                }else {
                    tvStart.setText(R.string.tv_start_fress);
                }
                String count = info.getString("count");
                mBlance = info.getInteger("balance");
                tvCount.setText("当前共有" + count + "参赛");
                tvSpend.setText(mBlance + "金币/次");

            }

            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
            }
        });

    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_attention;
    }

    @OnClick({R.id.bt_share, R.id.bt_start, R.id.bt_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_share:
                Bundle temp = new Bundle();
                temp.putString("type", Contacts.ATTENTION_TYPE);
                temp.putString("title", getResources().getString(R.string.yaoqing_me));
                temp.putString("jump", Api.URL_GAME_YAOQING + "&token=" + mToken);
                Intent intent=new Intent(this,WebviewActivity.class);
                intent.putExtras(temp);
                startActivityForResult(intent, Contacts.QUESTION_CODE);
                break;
            case R.id.bt_start:
                startGame();
                break;
            case R.id.bt_back:
                finish();
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
        params.put("type", Contacts.ATTENTION_TYPE);
        Api.setStartGame(this, params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {

                Intent intent = new Intent(AttentionActivity.this, AttentionChildActivity.class);
                intent.putExtra("blance",mBlance);
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
