package com.test720.www.naneducationteacher;

import android.webkit.WebView;

import com.test720.www.naneducationteacher.baseui.BaseToolbarActivity;
import com.test720.www.naneducationteacher.view.ProgressWebview;

import butterknife.BindView;

public class H5Activity extends BaseToolbarActivity {

    @BindView(R.id.webView)
    ProgressWebview mWebView;

    @Override
    protected int getContentView() {
        return R.layout.activity_h5;
    }

    @Override
    protected void initData() {
        mWebView.loadUrl(getIntent().getStringExtra("url"));
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
        setTitleColor(R.color.white);
        setToolbarColor(R.color.base_color);
        initToobar("详情");
    }
}
