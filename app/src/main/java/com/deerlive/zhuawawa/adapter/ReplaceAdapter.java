package com.deerlive.zhuawawa.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.common.GlideCircleTransform;
import com.deerlive.zhuawawa.model.GrabBean;

import java.util.List;

/**
 * Created by apple on 2018/3/27.
 */

public class ReplaceAdapter extends BaseQuickAdapter<GrabBean.InfoBean,BaseViewHolder> {

    public ReplaceAdapter( List<GrabBean.InfoBean> data) {
        super(R.layout.item_record_wq, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GrabBean.InfoBean item) {

        helper.setText(R.id.zq_name,item.getName())
                .setText(R.id.zq_time,item.getRemark())
                .setChecked(R.id.checkbox_select, item.getRemoteUid() != 0);
        Glide.with(mContext).load(item.getImg())
                .error(R.mipmap.logo)
                .transform(new GlideCircleTransform(mContext))
                .into((ImageView)helper.getView(R.id.zq_avator));

    }
}
