package com.test720.www.naneducationteacher.mineactivity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.test720.www.naneducationteacher.NimApplication;
import com.test720.www.naneducationteacher.R;
import com.test720.www.naneducationteacher.adapter.MineLiveAdapter;
import com.test720.www.naneducationteacher.baseui.BaseToolbarActivity;
import com.test720.www.naneducationteacher.bean.MineLiveBean;
import com.test720.www.naneducationteacher.utils.DensityUtil;
import com.test720.www.naneducationteacher.view.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

import static com.test720.www.naneducationteacher.NanEducationControl.teacherLiveList;

public class MineLiveBroadCastActivty extends BaseToolbarActivity {
    @BindView(R.id.liveBroadCastRecyclerView)
    RecyclerView mLiveBroadCastRecyclerView;
    @BindView(R.id.minelive_refresh)
    TwinklingRefreshLayout minelive_refresh;
    private MineLiveAdapter adapter;
    private List<MineLiveBean.DataBean.ListBean> list = new ArrayList<>();
    private GridLayoutManager manager;

    @Override
    protected int getContentView() {
        return R.layout.activity_mine_live_broad_cast_activty;
    }

    @Override
    protected void initData() {
        //recycle
        manager = new GridLayoutManager(this, 2);
        adapter = new MineLiveAdapter(R.layout.item_live_item, list);
        adapter.bindToRecyclerView(mLiveBroadCastRecyclerView);
        mLiveBroadCastRecyclerView.addItemDecoration(new SpaceItemDecoration(0, DensityUtil.dip2px(context, 15), DensityUtil.dip2px(context, 15)));
        showLoadingDialog();
        MineLivBroadeOkGo();
    }

    private void MineLivBroadeOkGo() {
        OkGo.post(teacherLiveList)
                .tag(this)
                .cacheKey("minelive")
                .cacheMode(CacheMode.DEFAULT)
                .params("tcId", NimApplication.teacherId)
                .params("type", "1")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        minelive_refresh.finishRefreshing();
                        Log.e("==minelive", s);
                        MineLiveBean mineLiveBean = JSON.parseObject(s, MineLiveBean.class);
                        cancleLoadingDialog();
                        if (mineLiveBean.getCode() == 1) {
                            if (mineLiveBean.getData() != null) {
                                //列表
                                list.clear();
                                list.addAll(mineLiveBean.getData().getList());
                                initRecycle();
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        cancleLoadingDialog();
                        ShowToast(getString(R.string.timeout));
                        Log.e("==mineliveerror", e.getMessage());
                    }
                });
    }

    private void initRecycle() {
        mLiveBroadCastRecyclerView.setHasFixedSize(true);
        mLiveBroadCastRecyclerView.setLayoutManager(manager);
        mLiveBroadCastRecyclerView.setAdapter(adapter);
        adapter.setEmptyView(R.layout.emptyview);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("title", list.get(position).getLive_title());
                bundle.putString("lid", list.get(position).getLid());
                bundle.putInt("type", 1);
                bundle.putString("room", list.get(position).getRoom_mun());
                bundle.putString("statue", list.get(position).getLivetype());
                jumpToActivity(CourseInfoActivity.class, bundle, false);
            }
        });
    }

    @Override
    protected void setListener() {
        minelive_refresh.setEnableLoadmore(false);
        minelive_refresh.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                MineLivBroadeOkGo();
            }
        });
    }

    @Override
    protected void initBase() {
        isShowToolBar = true;
        isShowBackImage = true;
    }

    @Override
    protected void initView() {
        initToobar("直播");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }
}
