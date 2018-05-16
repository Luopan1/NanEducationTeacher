package com.test720.www.naneducationteacher.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.test720.www.naneducationteacher.R;
import com.test720.www.naneducationteacher.bean.EarnBean;

import java.util.List;

/**
 *
 * Created by wangshuai on 2017/11/10.
 */

public class EarnAdapter extends BaseQuickAdapter<EarnBean.DataBean.ListBean, BaseViewHolder> {
    public EarnAdapter(int layoutResId, @Nullable List<EarnBean.DataBean.ListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, EarnBean.DataBean.ListBean item) {
        helper.setText(R.id.title, item.getName())
                .setText(R.id.time, item.getTime())
                .setText(R.id.humanCount, item.getCount())
                .setText(R.id.money, String.valueOf(item.getMoney()) + "元");
    }
}
