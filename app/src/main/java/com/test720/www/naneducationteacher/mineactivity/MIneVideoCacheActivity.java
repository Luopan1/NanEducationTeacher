package com.test720.www.naneducationteacher.mineactivity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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

import static com.test720.www.naneducationteacher.NanEducationControl.liveDetail;
import static com.test720.www.naneducationteacher.NanEducationControl.teacherLiveList;

public class MIneVideoCacheActivity extends BaseToolbarActivity {


    @BindView(R.id.videocacheRecyclerView)
    RecyclerView mVideocacheRecyclerView;
    private GridLayoutManager manager;
    private List<MineLiveBean.DataBean.ListBean> list = new ArrayList<>();
    private MineLiveAdapter adapter;


    @Override
    protected int getContentView() {
        return R.layout.activity_mine_video_cache;
    }

    @Override
    protected void initData() {
        //recycle
        manager = new GridLayoutManager(this, 2);
        adapter = new MineLiveAdapter(R.layout.item_live_item, list);
        adapter.bindToRecyclerView(mVideocacheRecyclerView);
        mVideocacheRecyclerView.addItemDecoration(new SpaceItemDecoration(0, DensityUtil.dip2px(context, 15), DensityUtil.dip2px(context, 15)));
        showLoadingDialog();
        MineLivCacheOkGo();
    }

    private void MineLivCacheOkGo() {
        OkGo.post(teacherLiveList)
                .tag(this)
                .cacheKey("minelivecache")
                .cacheMode(CacheMode.DEFAULT)
                .params("tcId", NimApplication.teacherId)
                .params("type", "2")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
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
                        Log.e("==minelivecacheerror", e.getMessage());
                    }
                });
    }

    private void initRecycle() {
        mVideocacheRecyclerView.setHasFixedSize(true);
        mVideocacheRecyclerView.setLayoutManager(manager);
        mVideocacheRecyclerView.setAdapter(adapter);
        adapter.setEmptyView(R.layout.emptyview);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("title", list.get(position).getLid());
                bundle.putInt("type", 2);
                bundle.putString("lid", list.get(position).getLid());
                bundle.putString("path",list.get(position).getBack_url());
                jumpToActivity(CourseInfoActivity.class, bundle, false);
            }
        });
    }

    private void setAdapter() {
        /*if (mBaseRecyclerAdapter == null) {
            mBaseRecyclerAdapter = new BaseRecyclerAdapter<Integer>(this, mLists, R.layout.item_live_item) {
                @Override
                public void convert(BaseRecyclerHolder holder, Integer item, int position, boolean isScrolling) {
                    mSizeUtils.setLayoutSize(holder.getView(R.id.courseImage), 325, 210);
                }
            };
            mVideocacheRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            mVideocacheRecyclerView.addItemDecoration(new SpaceItemDecoration(0, DensityUtil.dip2px(context, 15), DensityUtil.dip2px(context, 15)));
            mVideocacheRecyclerView.setAdapter(mBaseRecyclerAdapter);


            mBaseRecyclerAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(RecyclerView parent, View view, int position) {
                    Bundle bundle = new Bundle();
                    bundle.putString("title", "杨老师教你学英语");
                    bundle.putInt("type", 2);
                    jumpToActivity(CourseInfoActivity.class, bundle, false);
                }
            });

        } else {
            mBaseRecyclerAdapter.notifyDataSetChanged();
        }*/
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initBase() {
        isShowBackImage = true;
        isShowToolBar = true;
    }

    @Override
    protected void initView() {
        initToobar("录播");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }
}
