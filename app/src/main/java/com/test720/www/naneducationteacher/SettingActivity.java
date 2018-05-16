package com.test720.www.naneducationteacher;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.kyleduo.switchbutton.SwitchButton;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.test720.www.naneducationteacher.allclass.SharePreferencesUtil;
import com.test720.www.naneducationteacher.baseui.BaseToolbarActivity;
import com.test720.www.naneducationteacher.mineactivity.CustomerServiceActivity;
import com.test720.www.naneducationteacher.settingactivity.AboutUsActivity;
import com.test720.www.naneducationteacher.settingactivity.ChangeTeacherIntroduceActivity;
import com.test720.www.naneducationteacher.utils.DataCleanManager;
import com.test720.www.naneducationteacher.utils.GlideLoader;
import com.test720.www.naneducationteacher.view.CircleImageView;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.test720.www.naneducationteacher.NanEducationControl.editUserInfo;
import static com.test720.www.naneducationteacher.NanEducationControl.parentimageUrl;
import static com.test720.www.naneducationteacher.NanEducationControl.teacherSetup;

public class SettingActivity extends BaseToolbarActivity {
    @BindView(R.id.user_head)
    CircleImageView mUserHead;
    @BindView(R.id.imageView2)
    ImageView mImageView2;
    @BindView(R.id.user_head_relative)
    RelativeLayout mUserHeadRelative;
    @BindView(R.id.phone)
    TextView mPhone;
    @BindView(R.id.userPhone)
    RelativeLayout mUserPhone;
    @BindView(R.id.nickName)
    TextView mNickName;
    @BindView(R.id.userNickName)
    RelativeLayout mUserNickName;
    @BindView(R.id.teacherIntroduceRe)
    RelativeLayout mTeacherIntroduceRe;
    @BindView(R.id.teacherIntroduce)
    TextView mTeacherIntroduce;
    @BindView(R.id.switchButton)
    SwitchButton mSwitchButton;
    @BindView(R.id.customNumber)
    TextView mCustomNumber;
    @BindView(R.id.aboutUs)
    RelativeLayout mAboutUs;
    @BindView(R.id.clearCache)
    LinearLayout mClearCache;
    @BindView(R.id.cache)
    TextView mCache;
    //头像
    private ImagePicker imagePicker;
    private ArrayList<ImageItem> headImages = new ArrayList<>();
    private static final int IMAGE_PICKER = 1;
    private static final int change = 2;
    private PopupWindow mPopWindow;
    private String mName;
    private String is_remind = "";
    private String imagePath = "";
    private File headFile;


    @Override
    protected int getContentView() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initData() {
        //网络请求
        showLoadingDialog();
        OkGo.post(teacherSetup)
                .tag(this)
                .cacheKey("setting")
                .cacheMode(CacheMode.DEFAULT)
                .params("tcId", NimApplication.teacherId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("==setting", s);
                        JSONObject jsonObject = JSON.parseObject(s);
                        cancleLoadingDialog();
                        if (jsonObject.getIntValue("code") == 1) {
                            if (jsonObject.getJSONObject("data") != null) {
                                if (jsonObject.getJSONObject("data").getJSONObject("userInfo") != null) {
                                    mCustomNumber.setText(jsonObject.getJSONObject("data").getJSONObject("userInfo").getString("agentPhone"));
                                    Glide.with(SettingActivity.this).load(parentimageUrl + jsonObject.getJSONObject("data").getJSONObject("userInfo").getString("head")).
                                            error(R.mipmap.icon_head_portrait).into(mUserHead);
                                    mPhone.setText(jsonObject.getJSONObject("data").getJSONObject("userInfo").getString("username"));
                                    mNickName.setText(jsonObject.getJSONObject("data").getJSONObject("userInfo").getString("name"));
                                    mTeacherIntroduce.setText(jsonObject.getJSONObject("data").getJSONObject("userInfo").getString("teacher_content"));
                                    Log.e("==mCustomNumber", mCustomNumber.getText().toString());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        cancleLoadingDialog();
                        ShowToast(getString(R.string.timeout));
                        Log.e("==settingerror", e.getMessage());
                    }
                });
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
        initToobar(R.mipmap.fanhui, "设置", "保存");
        try {
            mCache.setText(DataCleanManager.getTotalCacheSize(SettingActivity.this));
        } catch (Exception e) {
            ShowToast("获取程序缓存大小失败");
        }

        if (SharePreferencesUtil.getIsRemid(this)) {
            mSwitchButton.setChecked(true);
        } else {
            mSwitchButton.setChecked(false);
        }
    }


    /**
     * 第三方拍照和裁剪
     */
    private void initImagePicker() {
        imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setSaveRectangle(false); //是否按矩形区域保存
        imagePicker.setSelectLimit(1);//选中数量限制
        imagePicker.setCrop(true);     //允许裁剪（单选才有效）
        imagePicker.setStyle(CropImageView.Style.CIRCLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素

    }

    @OnClick({R.id.user_head_relative, R.id.userPhone, R.id.teacherIntroduceRe, R.id.switchButton, R.id.customNumber, R.id.aboutUs, R.id.userNickName, R.id.clearCache})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_head_relative:
                AndPermission.with(this)
                        .permission(Permission.CAMERA)
                        .requestCode(200)
                        .callback(listener)
                        .start();
                break;
            case R.id.userNickName:
                showChangeNickNamePop();
                break;
            case R.id.teacherIntroduceRe:
                Intent intent1 = new Intent(this, ChangeTeacherIntroduceActivity.class);
                startActivityForResult(intent1, change);
                break;
            case R.id.switchButton:
                if (mSwitchButton.isChecked()) {
                    is_remind = "1";
                    mSwitchButton.setChecked(true);
                } else {
                    is_remind = "0";
                    mSwitchButton.setChecked(false);
                }
                break;
            case R.id.customNumber:
                showPopWindos();
                break;
            case R.id.aboutUs:
                jumpToActivity(AboutUsActivity.class, false);
                break;
            case R.id.clearCache:
                new AlertDialog.Builder(SettingActivity.this).setTitle("温馨提示")//设置对话框标题
                        .setMessage("清除缓存可能会导致图片加载失败 请重新打开app")//设置显示的内容
                        .setCancelable(true)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                DataCleanManager.clearAllCache(SettingActivity.this);
                                try {
                                    mCache.setText(DataCleanManager.getTotalCacheSize(SettingActivity.this));
                                } catch (Exception e) {
                                    ShowToast("获取程序缓存大小失败");
                                }
                            }
                        }).show();
                break;
        }
    }

    public void showChangeNickNamePop() {
        View parent = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        View popView = null;
        popView = View.inflate(this, R.layout.pop_nick_name, null);
        final EditText et_nickName = (EditText) popView.findViewById(R.id.editTextNickName);
        TextView tv_commit = (TextView) popView.findViewById(R.id.YES);
        TextView tv_cancle = (TextView) popView.findViewById(R.id.cancle);
        et_nickName.setText(mNickName.getText().toString().trim());
        et_nickName.setSelection(mNickName.getText().length());// 光标移动到最后
        tv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mName = et_nickName.getText().toString().trim();
                if (mName.isEmpty()) {
                    ShowToast("昵称不能为空");
                    return;
                }
                if (mName.length() > 5) {
                    ShowToast("5个字符以内");
                    return;
                }
                mNickName.setText(mName);
                mPopWindow.dismiss();
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
            }
        });


        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        mPopWindow = new PopupWindow(popView, width, height);
        mPopWindow.setAnimationStyle(R.style.AnimBottom);
        mPopWindow.setFocusable(true);
        mPopWindow.update();
        mPopWindow.setOutsideTouchable(true);// 设置允许在外点击消失
        ColorDrawable dw = new ColorDrawable(0x30000000);
        mPopWindow.setBackgroundDrawable(dw);
        mPopWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }

    private void showPopWindos() {
        View parent = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        View popView = null;
        popView = View.inflate(this, R.layout.pop_call_custom, null);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        mPopWindow = new PopupWindow(popView, width, height);

        TextView cancle = (TextView) popView.findViewById(R.id.cancle);
        TextView confirm = (TextView) popView.findViewById(R.id.confirm);
        final TextView tv_number = (TextView) popView.findViewById(R.id.phoneNumber);
        tv_number.setText("拨打" + mCustomNumber.getText().toString().trim());
        //取消按钮
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
            }
        });
        //确定按钮
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCustomNumber.getText().toString().trim()));
                if (ActivityCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
                mPopWindow.dismiss();
            }
        });

        mPopWindow.setFocusable(true);
        mPopWindow.update();
        mPopWindow.setOutsideTouchable(true);// 设置允许在外点击消失
        ColorDrawable dw = new ColorDrawable(0x30000000);
        mPopWindow.setBackgroundDrawable(dw);
        mPopWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }


    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //头像
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                headImages = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (headImages.size() != 0) {
                    Glide.with(this).load(headImages.get(0).path).asBitmap().placeholder(R.mipmap.icon_head_portrait).into(mUserHead);
                    imagePath = headImages.get(0).path;
                } else
                    imagePath = "";
            }
        }
        //内容
        if (requestCode == change && resultCode == 3) {
            if (data != null) {
                mTeacherIntroduce.setText(data.getStringExtra("mTeacherIntroduce"));
            }
        }
    }

    @Override
    public void RightOnClick() {
        super.RightOnClick();
        showLoadingDialog();
        SaveOkGo();
    }


    @Override
    public void LeftOnClick() {
        final MaterialDialog dialog = new MaterialDialog(SettingActivity.this);
        dialog.content("是否保存更改信息")//
                .contentTextSize(14)
                .titleTextSize(16)
                .btnText("确定", "取消")//
                .showAnim(new BounceTopEnter())//
                .show();
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                showLoadingDialog();
                SaveOkGo();
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        final MaterialDialog dialog = new MaterialDialog(SettingActivity.this);
        dialog.content("是否保存更改信息")//
                .contentTextSize(14)
                .titleTextSize(16)
                .btnText("确定", "取消")//
                .showAnim(new BounceTopEnter())//
                .show();
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                showLoadingDialog();
                SaveOkGo();
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                finish();
            }
        });
    }

    private void SaveOkGo() {
        HttpParams httpParams = new HttpParams();
        if (imagePath.isEmpty()) {
            httpParams.put("tcId", NimApplication.teacherId);
            httpParams.put("name", mNickName.getText().toString());
            httpParams.put("is_remind", is_remind);
            httpParams.put("content", mTeacherIntroduce.getText().toString().trim());
        } else {
            headFile = new File(imagePath);
            httpParams.put("tcId", NimApplication.teacherId);
            httpParams.put("name", mNickName.getText().toString());
            httpParams.put("is_remind", is_remind);
            httpParams.put("content", mTeacherIntroduce.getText().toString().trim());
            httpParams.put("header", headFile);
        }
        OkGo.post(editUserInfo)
                .tag(this)
                .cacheKey("setting")
                .cacheMode(CacheMode.DEFAULT)
                .params(httpParams)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("==setting", s);
                        JSONObject jsonObject = JSON.parseObject(s);
                        cancleLoadingDialog();
                        ShowToast(jsonObject.getString("msg"));
                        if (is_remind.endsWith("1")) {
                            SharePreferencesUtil.saveIsRemind(SettingActivity.this, true);
                        } else {
                            SharePreferencesUtil.saveIsRemind(SettingActivity.this, false);
                        }
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        cancleLoadingDialog();
                        ShowToast(getString(R.string.timeout));
                        Log.e("==settingerror", e.getMessage());
                    }
                });
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。
            // 这里的requestCode就是申请时设置的requestCode。
            // 和onActivityResult()的requestCode一样，用来区分多个不同的请求。
            if (requestCode == 200) {
                initImagePicker();
                Intent intent = new Intent(SettingActivity.this, ImageGridActivity.class);
                startActivityForResult(intent, IMAGE_PICKER);
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。
            if (requestCode == 200) {
                // TODO ...
                ShowToast("权限申请失败");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }
}
