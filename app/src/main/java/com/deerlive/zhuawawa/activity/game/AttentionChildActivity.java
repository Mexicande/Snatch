package com.deerlive.zhuawawa.activity.game;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.adapter.Attention_NumberAdapter;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.common.Contacts;
import com.deerlive.zhuawawa.fragment.DefeateFragment;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.utils.Attention_ItemDecoration;
import com.deerlive.zhuawawa.utils.SizeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import butterknife.Bind;
import butterknife.OnClick;
import cn.iwgang.countdownview.CountdownView;

public class AttentionChildActivity extends BaseActivity implements DefeateFragment.DefeateInputListener , DialogInterface.OnDismissListener {

    @Bind(R.id.bt_back)
    ImageView btBack;
    @Bind(R.id.layout_recycler)
    RecyclerView layoutRecycler;
    @Bind(R.id.count)
    CountdownView count;
    @Bind(R.id.tv_countdown)
    TextView tvCountdown;
    private Attention_NumberAdapter mAttention_NumberAdapter;
    private ArrayList<Integer> mArrays = new ArrayList<>();
    private HashSet<Integer> setlist=new HashSet<>();
    private int index = 0;
    private int mRandomIndex = 1;
    private int mBlance;
    private int mTime;
    private Random mRandom=new Random();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBlance = getIntent().getIntExtra("blance", 0);

        getTime();
        initView();
        startCountDownTime();
        setListener();
    }
    private void startCountDownTime() {
        if(tvCountdown.getVisibility()==View.GONE){
            tvCountdown.setVisibility(View.VISIBLE);
        }
        /**
         * 最简单的倒计时类，实现了官方的CountDownTimer类（没有特殊要求的话可以使用）
         * 即使退出activity，倒计时还能进行，因为是创建了后台的线程。
         * 有onTick，onFinsh、cancel和start方法
         */
        CountDownTimer timer = new CountDownTimer(3 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //每隔countDownInterval秒会回调一次onTick()方法
                tvCountdown.setText((int) (millisUntilFinished/1000)+1+"");
            }

            @Override
            public void onFinish() {
                ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(AttentionChildActivity.this, R.anim.zoom_down);
                tvCountdown.startAnimation(scaleAnimation);
                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        tvCountdown.setVisibility(View.GONE);
                        mAttention_NumberAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                int item = mAttention_NumberAdapter.getItem(position);

                                TextView viewByPosition = (TextView) mAttention_NumberAdapter.getViewByPosition(layoutRecycler, position, R.id.tv_number);
                                viewByPosition.setBackgroundColor(getResources().getColor(R.color.blue_item));
                                ((SimpleItemAnimator) layoutRecycler.getItemAnimator()).setSupportsChangeAnimations(false);

                                if (item - index == 1) {
                                    if (item == mRandomIndex * 16) {
                                        mRandomIndex++;
                                        setRandom(mRandomIndex);
                                    }
                                } else {
                                    DefeateFragment defeateFragment = DefeateFragment.newInstance(mBlance);
                                    defeateFragment.show(getSupportFragmentManager(), "defeateFragment");
                                }
                               /* TextView viewByPosition = (TextView) mAttention_NumberAdapter.getViewByPosition(layoutRecycler, position, R.id.tv_number);
                                viewByPosition.setBackgroundColor(getResources().getColor(R.color.blue_item));
                                ((SimpleItemAnimator) layoutRecycler.getItemAnimator()).setSupportsChangeAnimations(false);*/
                                //mAttention_NumberAdapter.notifyItemChanged(position);
                                index = item;
                            }
                        });
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }
        };
        timer.start();// 开始计时
    }
    private void setListener() {
        count.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                DefeateFragment defeateFragment = DefeateFragment.newInstance(mBlance);
                defeateFragment.show(getSupportFragmentManager(), "defeateFragment");
            }
        });
    }

    /**
     * 游戏时间
     */
    private void getTime() {
        Map<String, String> params = new HashMap<>();
        params.put("type", Contacts.ATTENTION_TYPE);

        Api.getGameTime(this, params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                JSONObject info = data.getJSONObject("info");
                mTime = info.getInteger("times");
                setCountTime();
            }

            @Override
            public void requestFailure(int code, String msg) {

            }
        });

    }

    private void setCountTime() {
        count.start(mTime * 1000);
    }


    private void initView() {

        for (int i = 1; i <= 16; i++) {
            mArrays.add(i);
        }
        mAttention_NumberAdapter = new Attention_NumberAdapter(null);

        layoutRecycler.setLayoutManager(new GridLayoutManager(this, 4));
        layoutRecycler.addItemDecoration(new Attention_ItemDecoration(SizeUtils.dp2px(5)));
        layoutRecycler.setAdapter(mAttention_NumberAdapter);
        mAttention_NumberAdapter.addData(mArrays);



    }

    private void reInit() {
        mArrays.clear();
        for (int i = 1; i <= 16; i++) {
            mArrays.add(i);
        }
        mAttention_NumberAdapter.setNewData(mArrays);
        setCountTime();
        index=0;
        mRandomIndex=1;
    }

    private void setRandom(int index) {
       // setlist = new HashSet<>();
       // setlist.clear();
        if(!mArrays.isEmpty()){
            mArrays.clear();
        }
        for (; ; ) {
            int nu = mRandom.nextInt(index * 16-(index-1)*16) + (index-1) * 16+1 ;
            mArrays.add(nu);
            for (int i = 0; i < mArrays.size() - 1; i++) {
                for (int j = mArrays.size() - 1; j > i; j--) {
                    if (mArrays.get(j).equals(mArrays.get(i))) {
                        mArrays.remove(j);
                    }
                }
            }
            if (mArrays.size() == 16) {
                break;
            }
        }
        mAttention_NumberAdapter.setNewData(mArrays);
    }


    @Override
    public int getLayoutResource() {
        return R.layout.activity_attention_child;
    }

    @OnClick(R.id.bt_back)
    public void onViewClicked() {
        setResult(Contacts.RESULT_CODE);
        finish();
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        reInit();
    }


    @Override
    public void onDefeateComplete() {
        finish();

    }
}
