package com.test720.www.naneducationteacher.settingactivity;

import com.test720.www.naneducationteacher.R;
import com.test720.www.naneducationteacher.baseui.BaseToolbarActivity;

public class AboutUsActivity extends BaseToolbarActivity {


    @Override
    protected int getContentView() {
        return R.layout.activity_about_us;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    protected void initBase() {
        isShowBackImage = true;
        isShowToolBar = true;
    }

    @Override
    protected void initView() {
        initToobar("关于我们");
        setTitleColor(R.color.black);
        setToolbarColor(R.color.white);
    }
}
