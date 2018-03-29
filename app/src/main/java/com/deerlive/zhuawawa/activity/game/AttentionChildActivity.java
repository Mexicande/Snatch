package com.deerlive.zhuawawa.activity.game;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.adapter.Attention_NumberAdapter;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.fragment.AdialogFragment;
import com.deerlive.zhuawawa.fragment.DefeateFragment;
import com.deerlive.zhuawawa.utils.Attention_ItemDecoration;
import com.deerlive.zhuawawa.utils.SizeUtils;
import com.deerlive.zhuawawa.utils.ToastUtils;
import com.deerlive.zhuawawa.view.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import butterknife.Bind;
import butterknife.OnClick;
import cn.iwgang.countdownview.CountdownView;

public class AttentionChildActivity extends BaseActivity {

    @Bind(R.id.bt_back)
    ImageView btBack;
    @Bind(R.id.layout_recycler)
    RecyclerView layoutRecycler;
    @Bind(R.id.count)
    CountdownView count;
    private Attention_NumberAdapter mAttention_NumberAdapter;
    private ArrayList<Integer>mArrays=new ArrayList<>();
    private int index=0;
    private int mRandomIndex=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {

        for(int i=1;i<=16;i++){
            mArrays.add(i);
        }
        mAttention_NumberAdapter=new Attention_NumberAdapter(null);

        layoutRecycler.setLayoutManager(new GridLayoutManager(this,4));
        layoutRecycler.addItemDecoration(new Attention_ItemDecoration(SizeUtils.dp2px(5)));
        layoutRecycler.setAdapter(mAttention_NumberAdapter);

        mAttention_NumberAdapter.addData(mArrays);

        mAttention_NumberAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int item = mAttention_NumberAdapter.getItem(position);
                if(item-index==1){
                    if(item==mRandomIndex*16){
                        setRandom(mRandomIndex);
                        mRandomIndex=mRandomIndex++;
                    }
                }else {
                    DefeateFragment defeateFragment= new DefeateFragment();
                    defeateFragment.show(getSupportFragmentManager(),"defeateFragment");
                }
                TextView viewByPosition = (TextView) mAttention_NumberAdapter.getViewByPosition(layoutRecycler, position, R.id.tv_number);
                viewByPosition.setBackgroundColor(getResources().getColor(R.color.blue_item));
                ((SimpleItemAnimator)layoutRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
               // mAttention_NumberAdapter.notifyItemChanged(position);
                index=item;
            }
        });

    }

    private void setRandom(int index ){
        Set<Integer> setlist=new HashSet<>();

        mArrays.clear();
       /* for(int i=1;i<=16;i++){
            int nu = new Random().nextInt(index * 16+1) + index * 16 - 15;
            setlist.add(nu);
        }*/
        for(;;){
            int nu = new Random().nextInt(index * 16) + index * 16+1 ;
            setlist.add(nu);
            if(setlist.size()==16){
                break;
            }
        }
        mArrays.addAll(setlist);
        mAttention_NumberAdapter.getData().clear();
        mAttention_NumberAdapter.setNewData(mArrays);
        mAttention_NumberAdapter.notifyDataSetChanged();
    }



    @Override
    public int getLayoutResource() {
        return R.layout.activity_attention_child;
    }

    @OnClick(R.id.bt_back)
    public void onViewClicked() {
        finish();
    }
}
