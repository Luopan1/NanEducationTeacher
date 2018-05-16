package com.test720.www.naneducationteacher.mineactivity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.test720.www.naneducationteacher.R;
import com.test720.www.naneducationteacher.adapter.EarnInfoAdapter;
import com.test720.www.naneducationteacher.baseui.BaseToolbarActivity;
import com.test720.www.naneducationteacher.bean.EarnBean;

import java.util.List;

import butterknife.BindView;

public class EarnInfoActivity extends BaseToolbarActivity {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private LinearLayoutManager manager;
    private EarnInfoAdapter adapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_earn_info;
    }

    @Override
    protected void initData() {
        //上个界面传的list
        List<EarnBean.DataBean.ListBean.UserlistBean> list = (List<EarnBean.DataBean.ListBean.UserlistBean>) getIntent().getSerializableExtra("list");
        manager = new LinearLayoutManager(this);
        adapter = new EarnInfoAdapter(R.layout.item_earninfo_item, list);
        adapter.bindToRecyclerView(mRecyclerView);
    }

    @Override
    protected void setListener() {
        initRecycle();
    }

    private void initRecycle() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void initBase() {
        isShowBackImage = true;
        isShowToolBar = true;
    }

    @Override
    protected void initView() {
        initToobar("人员");
    }
}
