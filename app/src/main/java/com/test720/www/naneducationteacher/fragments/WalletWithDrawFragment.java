package com.test720.www.naneducationteacher.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.test720.www.naneducationteacher.R;
import com.test720.www.naneducationteacher.NimApplication;
import com.test720.www.naneducationteacher.adapter.WithdrawaAdapter;
import com.test720.www.naneducationteacher.baseui.BaseFragment;
import com.test720.www.naneducationteacher.bean.WithDrawBean;

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

public class WalletWithDrawFragment extends BaseFragment {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private WithdrawaAdapter adapter;
    private List<WithDrawBean.DataBean.ListBean> list = new ArrayList<>();
    private LinearLayoutManager manager;


    public WalletWithDrawFragment() {
    }

    @Override
    protected void initView() {
        manager = new LinearLayoutManager(getActivity());
        adapter = new WithdrawaAdapter(R.layout.item_withdraw_item, list);
        adapter.bindToRecyclerView(mRecyclerView);
        //网络请求
        showLoadingDialog("加载...");
        WithDrawOkGo();
    }

    private void WithDrawOkGo() {
        OkGo.post(teacherMoney)
                .tag(this)
                .cacheKey("withdraw")
                .cacheMode(CacheMode.DEFAULT)
                .params("tcId", NimApplication.teacherId)
                .params("type", "1")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("==withdraw", s);
                        WithDrawBean withDrawBean = JSON.parseObject(s, WithDrawBean.class);
                        WithDrawBean.DataBean data = withDrawBean.getData();
                        cancleLoadingDialog();
                        if (withDrawBean.getCode() == 1) {
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
                        Log.e("==withdrawerror", e.getMessage());
                    }
                });
    }

    private void initRecycle() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);
        adapter.setEmptyView(R.layout.emptyview);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }
}
