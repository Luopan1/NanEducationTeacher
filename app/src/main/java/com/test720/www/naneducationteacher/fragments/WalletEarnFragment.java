package com.test720.www.naneducationteacher.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.test720.www.naneducationteacher.R;
import com.test720.www.naneducationteacher.NimApplication;
import com.test720.www.naneducationteacher.adapter.EarnAdapter;
import com.test720.www.naneducationteacher.baseui.BaseFragment;
import com.test720.www.naneducationteacher.bean.EarnBean;
import com.test720.www.naneducationteacher.mineactivity.EarnInfoActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

import static com.test720.www.naneducationteacher.NanEducationControl.teacherMoney;

/**
 *
 * Created by LuoPan on 2017/11/7.
 */

public class WalletEarnFragment extends BaseFragment {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private EarnAdapter adapter;
    private List<EarnBean.DataBean.ListBean> list = new ArrayList<>();
    private LinearLayoutManager manager;

    @Override
    protected void initView() {
        manager = new LinearLayoutManager(getActivity());
        adapter = new EarnAdapter(R.layout.item_earn_item, list);
        adapter.bindToRecyclerView(mRecyclerView);
        //网络请求
        showLoadingDialog("加载...");
        EarnOkGo();
    }

    private void EarnOkGo() {
        OkGo.post(teacherMoney)
                .tag(this)
                .cacheKey("withdraw")
                .cacheMode(CacheMode.DEFAULT)
                .params("tcId", NimApplication.teacherId)
                .params("type", "2")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("==earn", s);
                        EarnBean earnBean = JSON.parseObject(s, EarnBean.class);
                        EarnBean.DataBean data = earnBean.getData();
                        cancleLoadingDialog();
                        if (earnBean.getCode() == 1) {
                            if (data.getList() != null) {
                                //列表
                                list.clear();
                                list.addAll(data.getList());
                                initRecycle();
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        cancleLoadingDialog();
                        ShowToast("网络请求失败或超时，请稍后重试...");
                        Log.e("==earnerror", e.getMessage());
                    }
                });
    }

    private void initRecycle() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);
        adapter.setEmptyView(R.layout.emptyview);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<EarnBean.DataBean.ListBean.UserlistBean> userlist = list.get(position).getUserlist();
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", (Serializable) userlist);
                jumpToActivity(EarnInfoActivity.class, bundle, false);
            }
        });
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected int setLayoutResID() {
        return R.layout.fragment_wallet_wihtdraws;
    }
}
