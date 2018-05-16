package com.test720.www.naneducationteacher.fragments;

import android.content.Intent;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.test720.www.naneducationteacher.CreatLiveBroadCastActivity;
import com.test720.www.naneducationteacher.H5Activity;
import com.test720.www.naneducationteacher.NimApplication;
import com.test720.www.naneducationteacher.R;
import com.test720.www.naneducationteacher.baseui.BaseFragment;
import com.test720.www.naneducationteacher.bean.Banner;
import com.test720.www.naneducationteacher.login.LoginActivity;
import com.test720.www.naneducationteacher.utils.NetImageLoaderHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.test720.www.naneducationteacher.NanEducationControl.parentimageUrl;
import static com.test720.www.naneducationteacher.NanEducationControl.tchBanner;
import static com.test720.www.naneducationteacher.NanEducationControl.teacherDetail;

/**
 * Created by LuoPan on 2017/11/6.
 */

public class CreatLiveBroadCastFragment extends BaseFragment {
    @BindView(R.id.createLiveBroadCast)
    Button mCreateLiveBroadCast;
    @BindView(R.id.banner)
    ConvenientBanner mBanner;
    private Banner mBnanerBean;

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        OkGo.post(tchBanner).tag(this).params(" tcId", NimApplication.teacherId) .execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                mBnanerBean = JSON.parseObject(s,Banner.class);
                if (mBnanerBean.getCode()==1) {
                    List<String> imagsList = new ArrayList<>();
                    for (int i = 0; i < mBnanerBean.getData().getList().size() ; i++) {
                        imagsList.add(parentimageUrl+ mBnanerBean.getData().getList().get(i).getBa_img());
                    }
                    int[] dots = {R.mipmap.circle1, R.mipmap.circle2};
                    mBanner.setPointViewVisible(true);//设置小圆点可见
                    mBanner.setPageIndicator(dots);//设置小圆点
                    mBanner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
                    mBanner.setManualPageable(true);//手动滑动
                    if (mBanner.isTurning()) {
                        mBanner.startTurning(3000);//自动轮滑
                    }

                    mBanner.setPages(new CBViewHolderCreator<NetImageLoaderHolder>() {
                        @Override
                        public NetImageLoaderHolder createHolder() {
                            return new NetImageLoaderHolder();
                        }
                    }, imagsList);

                }

            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                cancleLoadingDialog();
                ShowToast(getString(R.string.timeout));
            }
        });
    }

    @Override
    protected void setListener() {
        mBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (mBnanerBean.getData().getList().get(position).getTarget_type().equals("1")) {

                } else if (mBnanerBean.getData().getList().get(position).getTarget_type().equals("2")) {
                    Intent intent = new Intent(getActivity(), H5Activity.class);
                    intent.putExtra("url", teacherDetail + "/detailId/" + mBnanerBean.getData().getList().get(position).getBa_id());
                    startActivity(intent);

                } else if (mBnanerBean.getData().getList().get(position).getTarget_type().equals("3")) {
                    Intent intent = new Intent(getActivity(), H5Activity.class);
                    intent.putExtra("url", mBnanerBean.getData().getList().get(position).getBa_id());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected int setLayoutResID() {
        return R.layout.fragemnt_creat_livebroadcast;
    }


    @OnClick(R.id.createLiveBroadCast)
    public void onClick() {
        if (NimApplication.teacherId.equals("")){
            ShowToast("请先登录");
            jumpToActivity(LoginActivity.class,false);
        }else {
            jumpToActivity(CreatLiveBroadCastActivity.class, false);
        }
    }
}
