package com.test720.www.naneducationteacher.adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.test720.www.naneducationteacher.R;
import com.test720.www.naneducationteacher.bean.MineLiveBean;
import com.test720.www.naneducationteacher.view.CircleImageView;

import java.util.List;

import static com.test720.www.naneducationteacher.NanEducationControl.parentimageUrl;

/**
 * Created by wangshuai on 2017/11/9.
 */

public class MineLiveAdapter extends BaseQuickAdapter<MineLiveBean.DataBean.ListBean, BaseViewHolder> {
    public MineLiveAdapter(int layoutResId, @Nullable List<MineLiveBean.DataBean.ListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MineLiveBean.DataBean.ListBean item) {
        ImageView courseImage = (ImageView) helper.getView(R.id.courseImage).findViewById(R.id.courseImage);
        TextView courseKind = (TextView) helper.getView(R.id.courseKind).findViewById(R.id.courseKind);
        TextView money = (TextView) helper.getView(R.id.money).findViewById(R.id.money);
        CircleImageView teacherPhoto = (CircleImageView) helper.getView(R.id.teacherPhoto).findViewById(R.id.teacherPhoto);
        //设置数据
        String tName = null;
        if (item.getName().length()>4){
          tName= item.getName().substring(0,4)+"...";
        }
        helper.setText(R.id.studyCourseName, item.getLive_title())
                .setText(R.id.teacherName, tName)
                .setText(R.id.money, item.getPrice());
        Glide.with(mContext).load(parentimageUrl + item.getLive_logo()).error(R.color.Blue).diskCacheStrategy(DiskCacheStrategy.ALL).into(courseImage);
        Glide.with(mContext).load(parentimageUrl + item.getHead()).error(R.mipmap.touxiang).diskCacheStrategy(DiskCacheStrategy.ALL).into(teacherPhoto);
        if (item.getLivetype().equals("预告")) {
            courseKind.setText("预告");
            courseKind.setBackgroundColor(ContextCompat.getColor(mContext, R.color.shallow_blue));
        } else if (item.getLivetype().equals("免费")) {
            courseKind.setText("免费");
            courseKind.setBackgroundColor(ContextCompat.getColor(mContext, R.color.shallow_red));
            Drawable drawableleft = mContext.getResources().getDrawable(R.mipmap.jiagehui);
            money.setCompoundDrawablesWithIntrinsicBounds(drawableleft, null, null, null);
            money.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
        } else if (item.getLivetype().equals("进行中")) {
            courseKind.setText("进行中");
            courseKind.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Green));
        } else if (item.getLivetype().equals("已结束")) {
            courseKind.setText("已结束");
            courseKind.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
        } else if (item.getLivetype().equals("回放")) {
            courseKind.setText("回放");
            courseKind.setBackgroundColor(ContextCompat.getColor(mContext, R.color.huifang));
        }
    }
}
