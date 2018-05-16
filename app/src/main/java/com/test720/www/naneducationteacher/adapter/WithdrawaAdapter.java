package com.test720.www.naneducationteacher.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.test720.www.naneducationteacher.R;
import com.test720.www.naneducationteacher.bean.WithDrawBean;

import java.util.List;

/**
 * Created by wangshuai on 2017/11/10.
 */

public class WithdrawaAdapter extends BaseQuickAdapter<WithDrawBean.DataBean.ListBean, BaseViewHolder> {
    public WithdrawaAdapter(int layoutResId, @Nullable List<WithDrawBean.DataBean.ListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WithDrawBean.DataBean.ListBean item) {
        helper.setText(R.id.money, item.getMoney() + "元")
                .setText(R.id.time, item.getTime())
                .setText(R.id.statue, item.getCash_type());
    }
}
