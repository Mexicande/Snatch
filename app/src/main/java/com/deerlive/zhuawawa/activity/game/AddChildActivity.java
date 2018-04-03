package com.deerlive.zhuawawa.activity.game;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.deerlive.zhuawawa.MyApplication;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.common.Contacts;
import com.deerlive.zhuawawa.fragment.ClearedFragment;
import com.deerlive.zhuawawa.fragment.DefeateFragment;
import com.deerlive.zhuawawa.intf.DialogListener;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.utils.SPUtils;
import com.deerlive.zhuawawa.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 加减运算
 */
public class AddChildActivity extends BaseActivity implements DialogListener, DialogInterface.OnDismissListener{

    @Bind(R.id.bt_back)
    ImageView btBack;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.progressbar)
    ProgressBar progressbar;
    @Bind(R.id.operation)
    TextView operation;
    @Bind(R.id.result)
    TextView result;
    @Bind(R.id.tv_order)
    TextView tvOrder;
    @Bind(R.id.iv_count)
    ImageView ivCount;
    @Bind(R.id.layout_time)
    RelativeLayout layoutTime;
    @Bind(R.id.layout_game)
    RelativeLayout layoutGame;
    @Bind(R.id.tv_text)
    TextView tvText;
    private int time;
    private int mIndexPass=1;
    private int mResult;
    private int mProgress=0;

    private Random mRandom= new Random();
    private int mBlance;
    private Runnable mRunnable;
    private Runnable mRunnableTime;
    private boolean isStopped=true;
    private int mPass=30;
    private String mToken;
    private int errorTime;
    private int customsIndex=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToken = SPUtils.getInstance().getString("token");

        mBlance = getIntent().getIntExtra("blance", 0);
        initView();
        createAnimationThread();
        getTime();
    }

    private void game() {
        mIndexPass=2;
        setCountTime();
        int nu1=0;
        int nu2=0;
        if(mIndexPass<=5){
            nu1 = mRandom.nextInt(10+1) + 1;
            nu2 = mRandom.nextInt(10+1) + 1;
        }else if(mIndexPass<=10){
            nu1 = mRandom.nextInt(10+1) + 1;
            nu2 = mRandom.nextInt(20+1) + 1;
        }else if(mIndexPass<=15){
            nu1 = mRandom.nextInt(20+1) + 1;
            nu2 = mRandom.nextInt(20+1) + 1;
        }else if(mIndexPass<=20){
            nu1 = mRandom.nextInt(30+1) + 1;
            nu2 = mRandom.nextInt(30+1) + 1;
        }else if(mIndexPass<=mPass){
            nu1 = mRandom.nextInt(40+1) + 10;
            if(nu1%2==0){
                nu1++;
            }
            nu2 = mRandom.nextInt(40+1) + 10;
            if(nu2%2==0){
                nu2++;
            }
        }
        int opera = mRandom.nextInt(2);
        if(opera==0){
            operation.setText(nu1+"-"+nu2);
            mResult=nu1-nu2;
        }else {
            operation.setText(nu1+"+"+nu2);
            mResult=nu1+nu2;
        }
        int resultOpera = mRandom.nextInt(2);
        if(resultOpera==0){
            result.setText(nu1-nu2+"");
        }else {
            result.setText(nu1+nu2+"");
        }
    }

    private void initView() {
        tvText.setTextSize(16);

    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_add_child;
    }

    @OnClick({R.id.bt_back, R.id.iv_sure, R.id.iv_error})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_back:
                setResult(Contacts.RESULT_CODE);
                finish();
                break;
            case R.id.iv_sure:
                getResult(true);

                break;
            case R.id.iv_error:
                getResult(false);

                break;
            default:
                break;
        }
    }

    private void getResult(boolean flag) {
        String s = result.getText().toString();
        if(String.valueOf(mResult).equals(s)==flag){
            mIndexPass++;
            if(mIndexPass>mPass){
                customsIndex=2;
                errorTime=progressbar.getProgress();
                isStopped=false;
                ClearedFragment clearedFragment=ClearedFragment.newInstance(mBlance);
                clearedFragment.show(getSupportFragmentManager(), "clearedFragment");
            }else {
                game();
            }
        }else {
            isStopped=false;
            errorTime=progressbar.getProgress();
            DefeateFragment defeateFragment = DefeateFragment.newInstance(mBlance);
            defeateFragment.show(getSupportFragmentManager(), "defeateFragment");
        }
    }

    private Handler myHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    final int[] arrayId = {0, R.mipmap.nu_three, R.mipmap.nu_two, R.mipmap.nu_one};
                    int index = (int) msg.obj;

                    //设置ImageView控件中显示的图片
                    ivCount.setImageResource(arrayId[index]);

                    //设置图片的缩放比例
                    ivCount.setScaleX(0);
                    ivCount.setScaleY(0);

                    //设置x方向上的缩放动画
                    ObjectAnimator oa1 = ObjectAnimator.ofFloat(ivCount, "scaleX", 0, 1);
                    oa1.setDuration(500);

                    //设置y方向上的缩放动画
                    ObjectAnimator oa2 = ObjectAnimator.ofFloat(ivCount, "scaleY", 0, 1);
                    oa2.setDuration(500);

                    //创建动画师集合
                    AnimatorSet set = new AnimatorSet();

                    //设置所有的动画一起播放
                    set.playTogether(oa1, oa2);

                    //播放动画
                    set.start();
                    break;
                default:
                    break;
            }


        }
    };
    //创建播放倒计时动画的线程
    public void createAnimationThread() {
        isStopped=true;
        mRunnableTime = new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 4; i++) {
                    if (i < 4) {
                        //创建消息对象
                        Message message = myHandler.obtainMessage();

                        //设置消息对象携带的数据
                        message.obj = i;
                        message.what = 1;

                        //将消息发送到主线程的消息处理器
                        myHandler.sendMessage(message);
                        //暂停500毫秒
                        SystemClock.sleep(500);
                    } else {
                        SystemClock.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                layoutTime.setVisibility(View.GONE);
                                layoutGame.setVisibility(View.VISIBLE);
                                setCountTime();
                                game();
                            }
                        });
                    }
                }

            }
        };
        //启动子线程
        MyApplication.getThreadPool().executeTask(mRunnableTime);
    }
    /**
     * 游戏时间
     */
    private void getTime() {
        Map<String, String> params = new HashMap<>();
        params.put("type", Contacts.ADD_TYPE);

        Api.getGameTime(this, params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                JSONObject info = data.getJSONObject("info");
                time = info.getInteger("times");
                mPass = info.getInteger("pass");
                tvText.setText("连续答对"+mPass+"道题即挑战成功");
            }

            @Override
            public void requestFailure(int code, String msg) {

            }
        });

    }

    /**
     * 倒计时
     */
    private void setCountTime() {
        tvTime.setText(time + "s");
        progressbar.setMax(time);
        isStopped=true;
        mProgress=0;
        progressbar.setProgress(mProgress);
        mRunnable = new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1000);
                while (isStopped&&mProgress<=time){
                    mProgress++;
                    progressbar.setProgress(mProgress);
                    SystemClock.sleep(1000);

                }
                if(progressbar.getProgress()==time&&isStopped){
                    errorTime=progressbar.getProgress();
                    isStopped=false;
                    DefeateFragment defeateFragment = DefeateFragment.newInstance(mBlance);
                    defeateFragment.show(getSupportFragmentManager(), "defeateFragment");
                }
            }
        };
         MyApplication.getThreadPool().executeTask(mRunnable);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        startGame();
    }

    private void reInit() {
        layoutGame.setVisibility(View.GONE);
        layoutTime.setVisibility(View.VISIBLE);
        mIndexPass=1;
        mResult=0;
        isStopped=true;
        mProgress=0;
        createAnimationThread();
        getTime();
    }

    @Override
    public void onDefeateComplete() {
        isStopped=false;
        setResult(Contacts.RESULT_CODE);
        sendRecord();
        finish();
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

                reInit();

            }

            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeMessages(1);
        MyApplication.getThreadPool().removeTask(mRunnable);
        MyApplication.getThreadPool().removeTask(mRunnableTime);
    }


    @Override
    public void onBackPressed() {
        isStopped=false;
        sendRecord();
        myHandler.removeMessages(1);
        finish();
    }

    /**
     * 游戏数
     */
    private void sendRecord() {
        Map<String, String> params = new HashMap<>();
        params.put("token", mToken);
        params.put("type", Contacts.ADD_TYPE);
        params.put("times", errorTime+"");
        params.put("pass", mIndexPass+"");
        params.put("status", customsIndex+"");
        Api.getGameRecord(this, params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {


            }

            @Override
            public void requestFailure(int code, String msg) {

            }
        });

    }
}
