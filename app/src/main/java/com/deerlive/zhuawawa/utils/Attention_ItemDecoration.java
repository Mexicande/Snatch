package com.deerlive.zhuawawa.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by fengjh on 16/7/31.
 */
public class Attention_ItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public Attention_ItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
      /*  outRect.left = 3;
        outRect.top = 0;
        outRect.bottom = 20;
        outRect.right = 3;*/
        if(parent.getChildLayoutPosition(view)%2 == 0){
            outRect.right = 10;
            outRect.top = 0;
            outRect.bottom = 20;
            outRect.left = 10;
        }else{
            outRect.left = 10;
            outRect.top = 0;
            outRect.bottom = 20;
            outRect.right = 10;
        }
        if(parent.getChildLayoutPosition(view)<4){
            outRect.top = 20;
        }



      /*  if(space!=0){
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.left = 0;
                outRect.bottom = 20;
                outRect.right = 0;
                outRect.top = 0;
            }
        }*/
    }
}