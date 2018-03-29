package com.deerlive.zhuawawa.activity.game;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.utils.ActivityUtils;

import butterknife.Bind;
import butterknife.OnClick;

public class AttentionActivity extends BaseActivity {

    @Bind(R.id.bt_share)
    ImageView btShare;
    @Bind(R.id.tv_spend)
    TextView tvSpend;
    @Bind(R.id.bt_start)
    RelativeLayout btStart;
    @Bind(R.id.tv_count)
    TextView tvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_attention;
    }

    @OnClick({R.id.bt_share, R.id.bt_start,R.id.bt_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_share:
                break;
            case R.id.bt_start:
                ActivityUtils.startActivity(AttentionChildActivity.class);
                break;
            case R.id.bt_back:
                finish();
                break;
            default:
                break;
        }
    }
}
