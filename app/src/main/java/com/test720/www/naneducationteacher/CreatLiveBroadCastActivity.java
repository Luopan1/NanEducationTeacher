package com.test720.www.naneducationteacher;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.apkfuns.logutils.LogUtils;
import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.test720.www.naneducationteacher.baseui.BaseToolbarActivity;
import com.test720.www.naneducationteacher.bean.CreateBroadBean;
import com.test720.www.naneducationteacher.utils.GlideLoader;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import org.angmarch.views.NiceSpinner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.test720.www.naneducationteacher.NanEducationControl.createLive;
import static com.test720.www.naneducationteacher.NanEducationControl.liveTypeList;

public class CreatLiveBroadCastActivity extends BaseToolbarActivity {
    @BindView(R.id.chatRoomNumber)
    EditText mChatRoomNumber;
    @BindView(R.id.priece)
    EditText mPriece;
    @BindView(R.id.courseTitle)
    EditText mCourseTitle;
    @BindView(R.id.startTime)
    TextView mStartTime;
    @BindView(R.id.endTime)
    TextView mEndTime;
    @BindView(R.id.courseImage)
    ImageView mCourseImage;
    @BindView(R.id.comfirmCreate)
    Button mComfirmCreate;
    @BindView(R.id.courseSpinner)
    NiceSpinner mCourseSpinner;
    @BindView(R.id.gradeSpinner)
    NiceSpinner mGradeSpinner;
    @BindView(R.id.startAge)
    EditText mStartAge;
    @BindView(R.id.endAge)
    EditText mEndAge;
    @BindView(R.id.courseImageInfo)
    ImageView mCourseImageInfo;
    @BindView(R.id.couseIntrocuce)
    EditText mCouseIntrocuce;
    @BindView(R.id.isOpenVideoCache)
    CheckBox mIsOpenVideoCache;
    @BindView(R.id.countNumber)
    EditText mCountNumber;
    private ArrayList<ImageItem> imageList = new ArrayList<>();
    private ArrayList<ImageItem> CourseInfoImageLists = new ArrayList<>();
    private static final int IMAGE_PICKER = 1;
    public long startTime;
    public long endTime;
    int type = 0;
    private List<CreateBroadBean.DataBean.ListBean> parentlist = new ArrayList<>();
    private List<CreateBroadBean.DataBean.ListBean.ZidBean> sonlist = new ArrayList<>();
    private String imagePath1 = "", imagePath2 = "";
    private String typeId = "", typeZid = "";

    @Override
    protected int getContentView() {
        return R.layout.activity_creat_live_broad_cast;
    }

    @Override
    protected void initData() {
        showLoadingDialog();
        CreateBroadOkGo();
    }

    private void CreateBroadOkGo() {
        OkGo.post(liveTypeList)
                .tag(this)
                .cacheKey("creatbroad")
                .cacheMode(CacheMode.DEFAULT)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("==creatbroad", s);
                        CreateBroadBean createBroadBean = JSON.parseObject(s, CreateBroadBean.class);
                        CreateBroadBean.DataBean data = createBroadBean.getData();
                        cancleLoadingDialog();
                        if (createBroadBean.getCode() == 1) {
                            if (data != null && data.getList() != null) {
                                //父列表
                                parentlist.clear();
                                parentlist.addAll(data.getList());
                                List<String> dataset = new ArrayList<>();
                                for (int i = 0; i < parentlist.size(); i++) {
                                    dataset.add(parentlist.get(i).getName());
                                    mCourseSpinner.attachDataSource(dataset);
                                }
                                if (parentlist.size()>0){
                                    typeId = parentlist.get(0).getTid();
                                }
                                if (parentlist.size()>0){
                                    sonlist.clear();
                                    sonlist.addAll(parentlist.get(0).getZid());
                                    List<String> gradeLists = new ArrayList<>();
                                    if (sonlist.size() == 0) {
                                        gradeLists.add("");
                                        mGradeSpinner.attachDataSource(gradeLists);
                                    } else {
                                        for (int i = 0; i < sonlist.size(); i++) {
                                            gradeLists.add(sonlist.get(i).getName());
                                            mGradeSpinner.attachDataSource(gradeLists);
                                        }
                                    }
                                    if (sonlist.size()>1){
                                        typeZid = sonlist.get(0).getTid();//课程类型二级ID
                                    }

                                }


                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        cancleLoadingDialog();
                        ShowToast(getString(R.string.timeout));
                        Log.e("==creatbroaderror", e.getMessage());
                    }
                });
    }

    @Override
    protected void setListener() {
        mCourseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO: 2017/11/7 选中后将"课程"或者"年级"的赋值给顶替为这个选中的值
                typeId = parentlist.get(position).getTid();//课程类型一级ID
                sonlist.clear();
                sonlist.addAll(parentlist.get(position).getZid());
                List<String> gradeLists = new ArrayList<>();
                if (sonlist.size() == 0) {
                    gradeLists.add("");
                    mGradeSpinner.attachDataSource(gradeLists);
                } else {
                    for (int i = 0; i < sonlist.size(); i++) {
                        gradeLists.add(sonlist.get(i).getName());
                        mGradeSpinner.attachDataSource(gradeLists);
                    }
                }
                if (sonlist.size()>1){
                    typeZid = sonlist.get(0).getTid();//课程类型二级ID
                }
                Log.e("==gradeLists", gradeLists.toString());
                Log.e("==sonlist", sonlist.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (parentlist.size()>0){
                    typeId = parentlist.get(0).getTid();
                }
            }
        });


        mGradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO: 2017/11/7 选中后将"课程"或者"年级"的赋值给顶替为这个选中的值
                typeZid = sonlist.get(position).getTid();//课程类型二级ID
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (sonlist.size()>1){
                    typeZid = sonlist.get(0).getTid();//课程类型二级ID
                }
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
        initToobar("创建直播间");
    }


    private void initImagePicker(int size) {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(size);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素

    }

    @OnClick({R.id.startTime, R.id.endTime, R.id.courseImage, R.id.comfirmCreate, R.id.courseImageInfo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startTime:
                //时间选择器
                TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        //选中事件回调
                        startTime = date.getTime();
                        LogUtils.e(startTime);
                        mStartTime.setText(getTime(date));
                    }
                }).setContentSize(15).build();
                pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
                pvTime.show();
                break;
            case R.id.endTime:
                //时间选择器
                TimePickerView pvEndTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        //选中事件回调
                        endTime = date.getTime();
                        LogUtils.e((endTime - startTime) / 1000 + "_____" + endTime);
                        mEndTime.setText(" ");
                        if ((endTime - startTime) / 1000 >= 30 * 60) {
                            mEndTime.setText(getTime(date));
                        } else {
                            ShowToast("直播时间要大于30分钟");
                        }
                    }
                }).setContentSize(15).build();
                pvEndTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
                pvEndTime.show();
                break;
            case R.id.courseImage:
                AndPermission.with(this)
                        .permission(Permission.CAMERA)
                        .requestCode(200)
                        .callback(listener)
                        .start();
                break;
            case R.id.courseImageInfo:
                AndPermission.with(this)
                        .permission(Permission.CAMERA)
                        .requestCode(300)
                        .callback(listener)
                        .start();
                break;
            case R.id.comfirmCreate:
                if (editTextIsEmpty(mPriece) || editTextIsEmpty(mCourseTitle)
                        || textViewIsEmpty(mStartTime) || textViewIsEmpty(mEndTime) || editTextIsEmpty(mStartAge)
                        || editTextIsEmpty(mEndAge) || editTextIsEmpty(mCouseIntrocuce)
                        || CourseInfoImageLists.size() == 0 || imageList.size() == 0) {
                    ShowToast("请填完直播间信息");
                    return;
                }
                showLoadingDialog();
                CreateComfirmOkGo();
                //                jumpToActivity(WelcomeActivity.class, false);
                break;
        }
    }

    private void CreateComfirmOkGo() {
        HttpParams httpParams = new HttpParams();
        httpParams.put("logo", new File(imagePath1));
        httpParams.put("img", new File(imagePath2));
        httpParams.put("tcId", NimApplication.teacherId);
        httpParams.put("typeId", typeId);
        httpParams.put("typeZid", typeZid);
        httpParams.put("startime", mStartTime.getText().toString().trim());
        httpParams.put("endtime", mEndTime.getText().toString().trim());
        httpParams.put("people", mStartAge.getText().toString().trim() + "-" + mEndAge.getText().toString().trim());
        httpParams.put("price", mPriece.getText().toString().trim());
        httpParams.put("title", mCourseTitle.getText().toString().trim());
        httpParams.put("content", mCouseIntrocuce.getText().toString().trim());
        httpParams.put("room_count",mCountNumber.getText().toString().trim());
        httpParams.put("back_type","1");
        Log.e("==createcomfirm", httpParams.toString());
        OkGo.post(createLive)
                .tag(this)
                .cacheKey("createcomfirm")
                .cacheMode(CacheMode.DEFAULT)
                .params(httpParams)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("==createcomfirm", s);
                        cancleLoadingDialog();
                        JSONObject jsonObject = JSON.parseObject(s);
                        ShowToast(jsonObject.getString("msg"));
                        if (jsonObject.getIntValue("code") == 1) {
                            finish();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        cancleLoadingDialog();
                        ShowToast(getString(R.string.timeout));
                        Log.e("==createcomfirmerror", e.getMessage());
                    }
                });
    }

    @SuppressWarnings("unchecked")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER && type == 1) {
                imageList = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (imageList.size() != 0) {
                    Glide.with(this).load(imageList.get(0).path).asBitmap().placeholder(R.mipmap.default_image).into(mCourseImage);
                    imagePath1 = imageList.get(0).path;
                } else
                    imagePath1 = "";
            }
            if (data != null && requestCode == IMAGE_PICKER && type == 2) {
                CourseInfoImageLists = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (CourseInfoImageLists.size() != 0) {
                    Glide.with(this).load(CourseInfoImageLists.get(0).path).asBitmap().placeholder(R.mipmap.default_image).into(mCourseImageInfo);
                    imagePath2 = CourseInfoImageLists.get(0).path;
                } else
                    imagePath2 = CourseInfoImageLists.get(0).path;
            }
            Log.e("==imagePath1", imagePath1 + "=========" + imagePath2);
        }
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    //
    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。
            // 这里的requestCode就是申请时设置的requestCode。
            // 和onActivityResult()的requestCode一样，用来区分多个不同的请求。
            if (requestCode == 200) {
                type = 1;
                initImagePicker(1);
                Intent intent = new Intent(CreatLiveBroadCastActivity.this, ImageGridActivity.class);
                intent.putExtra(ImageGridActivity.EXTRAS_IMAGES, imageList);
                startActivityForResult(intent, IMAGE_PICKER);
            } else if (requestCode == 300) {
                type = 2;
                initImagePicker(1);
                Intent intent = new Intent(CreatLiveBroadCastActivity.this, ImageGridActivity.class);
                intent.putExtra(ImageGridActivity.EXTRAS_IMAGES, CourseInfoImageLists);
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