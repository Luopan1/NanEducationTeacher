package com.test720.www.naneducationteacher.mineactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.classroomsdk.Tools;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.test720.www.naneducationteacher.R;
import com.test720.www.naneducationteacher.adapter.BaseRecyclerAdapter;
import com.test720.www.naneducationteacher.baseui.BaseToolbarActivity;
import com.test720.www.naneducationteacher.bean.StudyHomeBanner;
import com.test720.www.naneducationteacher.utils.ImageLoader;
import com.test720.www.naneducationteacher.view.VideoActivity;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;
import weiyicloud.com.eduhdsdk.interfaces.JoinmeetingCallBack;
import weiyicloud.com.eduhdsdk.interfaces.MeetingNotify;
import weiyicloud.com.eduhdsdk.message.RoomClient;

import static com.test720.www.naneducationteacher.NanEducationControl.liveDetail;
import static com.test720.www.naneducationteacher.NanEducationControl.parentimageUrl;
import static com.test720.www.naneducationteacher.NanEducationControl.tchDown;
import static com.test720.www.naneducationteacher.NanEducationControl.teacherlogin;

/**
 * 使用bundle传递title、type  type==1:是直播  type==2:是录播 根据情况不同更改UI界面和进入不同的播放界面
 */
public class CourseInfoActivity extends BaseToolbarActivity implements JoinmeetingCallBack, MeetingNotify {
    @BindView(R.id.descriptionImage)
    ImageView mDescriptionImage;
    @BindView(R.id.teacherPhoto)
    ImageView mTeacherPhoto;
    @BindView(R.id.teacherName)
    TextView mTeacherName;
    @BindView(R.id.cousePrices)
    TextView mCousePrices;
    @BindView(R.id.BaoMingCount)
    TextView mBaoMingCount;
    @BindView(R.id.courseDonation)
    TextView mCourseDonation;
    @BindView(R.id.courseDescription)
    TextView mCourseDescription;
    @BindView(R.id.teacherDescripition)
    TextView mTeacherDescripition;
    @BindView(R.id.courseOnTime)
    TextView mCourseOnTime;
    @BindView(R.id.courseTimeLength)
    TextView mCourseTimeLength;
    @BindView(R.id.coureSortKind)
    TextView mCoureSortKind;
    @BindView(R.id.CourseCanStudyMan)
    TextView mCourseCanStudyMan;

    BaseRecyclerAdapter<StudyHomeBanner> recommandAdapter;
    List<StudyHomeBanner> mLiveBroadcastList;
    @BindView(R.id.signUpCount)
    TextView mSignUpCount;
    @BindView(R.id.startClassOrWatchVideo)
    TextView mStartClassOrWatchVideo;
    private ImageLoader mImageLoader;
    private int mType;
    private String statue;

    @Override
    protected int getContentView() {
        return R.layout.activity_course_info;
    }

    @Override
    protected void initData() {
        //网络请求
        showLoadingDialog();
        CourseInfoOkGo();
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
        RoomClient.getInstance().regiestInterface(this,this);
        Intent intent = getIntent();
        mType = intent.getIntExtra("type", 0);
        statue = intent.getStringExtra("statue");


        initToobar(intent.getStringExtra("title"),R.mipmap.fanhui);
        setToolbarColor(R.color.white);
        setTitleColor(R.color.black);
        //judge
        if (mType == 1) {
            mSignUpCount.setVisibility(View.VISIBLE);
            if (statue.equals("已结束")) {
                mStartClassOrWatchVideo.setText("已结束");
                mStartClassOrWatchVideo.setEnabled(false);
                mStartClassOrWatchVideo.setBackgroundColor(getResources().getColor(R.color.black));
            } else {
                mStartClassOrWatchVideo.setText("上课");
                mStartClassOrWatchVideo.setEnabled(true);
                mStartClassOrWatchVideo.setBackgroundColor(getResources().getColor(R.color.base_color));
            }
        } else if (mType == 2) {
            mSignUpCount.setVisibility(View.GONE);
            mStartClassOrWatchVideo.setText("观看回放");
        }
    }

    private void CourseInfoOkGo() {
        OkGo.post(liveDetail).tag(this).cacheKey("courseinfo").cacheMode(CacheMode.DEFAULT)
                .params("lid", getIntent().getStringExtra("lid"))
                .params("type", mType)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("==courseinfo", s);
                        JSONObject jsonObject = JSON.parseObject(s);
                        JSONObject data = jsonObject.getJSONObject("data");
                        cancleLoadingDialog();
                        if (jsonObject.getIntValue("code") == 1) {
                            if (data != null) {
                                Glide.with(CourseInfoActivity.this).load(parentimageUrl + data.getJSONObject("detail").getString("live_logo")).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.color.Blue).into(mDescriptionImage);
                                Glide.with(CourseInfoActivity.this).load(parentimageUrl + data.getJSONObject("detail").getString("head")).error(R.mipmap.touxiang).into(mTeacherPhoto);
                                mTeacherName.setText(data.getJSONObject("detail").getString("name"));
                                mBaoMingCount.setText("报名人数：" + data.getJSONObject("detail").getString("count"));
                                mSignUpCount.setText("报名人数：" + data.getJSONObject("detail").getString("count"));
                                mCourseDescription.setText(data.getJSONObject("detail").getString("content"));
                                mTeacherDescripition.setText(data.getJSONObject("detail").getString("teacher_content"));
                                mCourseOnTime.setText(data.getJSONObject("detail").getString("startime"));
                                mCourseTimeLength.setText(data.getJSONObject("detail").getString("cash_time"));
                                mCoureSortKind.setText(data.getJSONObject("detail").getString("tname"));
                                mCourseCanStudyMan.setText(data.getJSONObject("detail").getString("people") + "岁");
                                mCousePrices.setText(data.getJSONObject("detail").getString("price"));
                                if (data.getJSONObject("detail").getJSONArray("sponsor") != null) {
                                    JSONArray sponsor_array = data.getJSONObject("detail").getJSONArray("sponsor");
                                    String s_array = "";
                                    for (int i = 0; i < sponsor_array.size(); i++) {
                                        s_array = s_array + sponsor_array.get(i) + "\t";
                                    }
                                    mCourseDonation.setText(s_array);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        cancleLoadingDialog();
                        ShowToast(getString(R.string.timeout));
                        Log.e("==courseinfoerror", e.getMessage());
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }

    /**
     * 分享按钮的点击事件
     */

    public void RightOnClick() {
      /*  UMImage thumb =  new UMImage(this, R.mipmap.ic_launcher);
        thumb.compressStyle = UMImage.CompressStyle.SCALE;//大小压缩，默认为大小压缩，适合普通很大的图
        thumb.setThumb(thumb);  //缩略图
        UMWeb web = new UMWeb("http://www.baidu.com");
        web.setTitle("助教");//标题

        web.setThumb(thumb);
        web.setDescription("我在助教app里学习");
        new ShareAction(this)
                .withMedia(web)
                .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(shareListener)
                .open();*/
    }
    private UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {

        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }


    @OnClick({R.id.signUpCount, R.id.startClassOrWatchVideo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signUpCount:
                break;
            case R.id.startClassOrWatchVideo:
                if (mType == 1) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("userrole", 0); //
                    map.put("host", "global.talk-cloud.net"); //公网地址
                    map.put("port", 80);  //端口
                    map.put("serial", getIntent().getStringExtra("room")); //课堂号
                    map.put("nickname", "老师"); // 昵称
                    map.put("password", "123456");
                    RoomClient.getInstance().joinRoom(CourseInfoActivity.this, map);
                } else if (mType == 2) {
                    Bundle bundle = new Bundle();
                    bundle.putString("path", getIntent().getStringExtra("path"));
                    jumpToActivity(VideoActivity.class, bundle, false);
                }
                break;
        }
    }

    @Override
    public void onKickOut(int res) {
        if (res == RoomClient.Kickout_Repeat) {
            Toast.makeText(this, "有相同的身份进入  你已被踢出房间", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onWarning(int code) {
        if (code == 1) {
            ShowToast("打开失败 请稍候再试");
        }
        if (code == 2) {
            ShowToast("打开失败");
        }
    }

    @Override
    public void onClassBegin() {
        Toast.makeText(this,"已经上课了", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClassDismiss() {
        Toast.makeText(this, "已经下课了", Toast.LENGTH_LONG).show();
    }

    @Override
    public void callBack(int nRet) {
        Tools.HideProgressDialog();
        if (nRet == 0) {

        } else if (nRet == 100) {

        } else if (nRet == 101) {
            ShowToast("协议格式不正确");
        } else if (nRet == 4008) {
            ShowToast("进入失败");
        } else if (nRet == 4110) {
            ShowToast("课堂需要密码 创建错误");
        } else if (nRet == 4007) {
            ShowToast("课堂不存在");
        } else if (nRet == 3001) {
            ShowToast("服务器过期");
        } else if (nRet == 3002) {
            ShowToast("公司被冻结");
        } else if (nRet == 3003) {
            ShowToast("课堂被删除或过期");
        } else if (nRet == 4109) {
            ShowToast("Auth不正确");
        } else if (nRet == 4103) {
            ShowToast("课堂人数超限");
        } else if (nRet == 5006) {
            ShowToast("请您使用学员地址进入课堂");
        } else if (nRet == 4012) {
            ShowToast("该课堂需要密码，请输入密码");
        } else {
            errorTipDialog(this,
                    R.string.WaitingForNetwork);
        }
    }

    public void errorTipDialog(final Activity activity, int errorTipID) {

        AlertDialog.Builder build = new AlertDialog.Builder(activity);
        build.setTitle(getString(R.string.link_tip));
        build.setMessage(getString(errorTipID));
        build.setPositiveButton(getString(R.string.OK),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
        build.show();
    }

}
