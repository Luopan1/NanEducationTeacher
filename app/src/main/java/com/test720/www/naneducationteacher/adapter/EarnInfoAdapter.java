package com.test720.www.naneducationteacher.adapter;

import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.test720.www.naneducationteacher.R;
import com.test720.www.naneducationteacher.bean.EarnBean;
import com.test720.www.naneducationteacher.view.CircleImageView;

import java.util.List;

import static com.test720.www.naneducationteacher.NanEducationControl.parentimageUrl;

/**
 * Created by wangshuai on 2017/11/10.
 */

public class EarnInfoAdapter extends BaseQuickAdapter<EarnBean.DataBean.ListBean.UserlistBean, BaseViewHolder> {
    public EarnInfoAdapter(int layoutResId, @Nullable List<EarnBean.DataBean.ListBean.UserlistBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, EarnBean.DataBean.ListBean.UserlistBean item) {
        CircleImageView user_head = (CircleImageView) helper.getView(R.id.user_head).findViewById(R.id.user_head);
        helper.setText(R.id.user_name, item.getName())
                .setText(R.id.money, item.getPrice() + "元")
                .setText(R.id.time, item.getTime());
        Glide.with(mContext).load(parentimageUrl + item.getHead()).error(R.mipmap.touxiang).diskCacheStrategy(DiskCacheStrategy.ALL).into(user_head);
    }
}
