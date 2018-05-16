package com.classroomsdk;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.talkcloud.roomsdk.IRoomWhiteBoard;
import com.talkcloud.roomsdk.RoomControler;
import com.talkcloud.roomsdk.RoomManager;
import com.talkcloud.roomsdk.RoomUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WBFragment extends Fragment implements IRoomWhiteBoard, WBCallback, LocalControl {
    private View fragmentView;
    XWalkView xWalkView;
    private View.OnClickListener pageClickListener;
    private boolean candraw = false;
    private boolean isTouchable = false;
    private boolean isClassBegin = false;
    private ShareDoc currentFile;
    private int role = -1;
    private boolean mobilenameOnList = true;
    private boolean isPlayBack = false;

    public void setPlayBack(boolean playBack) {
        isPlayBack = playBack;
    }

    public void setMobilenameOnList(boolean mobilenameOnList) {
        this.mobilenameOnList = mobilenameOnList;
    }

    @SuppressLint("JavascriptInterface")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (fragmentView == null) {
            fragmentView = inflater.inflate(R.layout.fragment_white_pad, null);
            XWalkPreferences.setValue("enable-javascript", true);
            XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
            //置是否允许通过file url加载的Javascript可以访问其他的源,包括其他的文件和http,https等其他的源
            XWalkPreferences.setValue(XWalkPreferences.ALLOW_UNIVERSAL_ACCESS_FROM_FILE, true);

            //JAVASCRIPT_CAN_OPEN_WINDOW
            XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);
            // enable multiple windows.
            XWalkPreferences.setValue(XWalkPreferences.SUPPORT_MULTIPLE_WINDOWS, true);

            xWalkView = (XWalkView) fragmentView.findViewById(R.id.xwalkWebView);
            xWalkView.setZOrderOnTop(false);
//            web_white_pad = (WebView) fragmentView.findViewById(R.id.web_white_pad);
            XWalkSettings webs = xWalkView.getSettings();
            webs.setJavaScriptEnabled(true);
            webs.setCacheMode(WebSettings.LOAD_DEFAULT);
            webs.setDomStorageEnabled(true);
            webs.setDatabaseEnabled(true);
//            webs.setAppCacheEnabled(true);
            webs.setAllowFileAccess(true);
//            webs.setSavePassword(true);
            webs.setSupportZoom(false);
            webs.setBuiltInZoomControls(false);

            webs.setLoadWithOverviewMode(false);
            webs.setJavaScriptCanOpenWindowsAutomatically(true);
//            webs.setAppCacheEnabled(true);
            webs.setLoadWithOverviewMode(true);
            webs.setDomStorageEnabled(true);
            webs.setUseWideViewPort(true);
            webs.setMediaPlaybackRequiresUserGesture(false);
            webs.setSupportSpatialNavigation(true);
            webs.setAllowFileAccessFromFileURLs(true);
//            webs.setPluginState(WebSettings.PluginState.ON);
//            webs.setRenderPriority(WebSettings.RenderPriority.HIGH);


            webs.setLayoutAlgorithm(XWalkSettings.LayoutAlgorithm.NORMAL);
            webs.setUseWideViewPort(true);

            xWalkView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//            xWalkView.setHorizontalScrollbarOverlay(false);
            xWalkView.setHorizontalScrollBarEnabled(false);
            JSWhitePadInterface.getInstance().setWBCallBack(this);
            xWalkView.addJavascriptInterface(JSWhitePadInterface.getInstance(), "JSWhitePadInterface");
//            if(mobilenameOnList){
//            xWalkView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//            }else{
            xWalkView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//            }
            xWalkView.requestFocus();
            xWalkView.setOnClickListener(pageClickListener);


//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                web_white_pad.setWebContentsDebuggingEnabled(true);
            Locale locale;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = LocaleList.getDefault().get(0);
            } else {
                locale = Locale.getDefault();
            }
            String language = locale.getLanguage() + "-" + locale.getCountry();

            String lan = null;
            if (locale.equals(Locale.TAIWAN)) {
                lan = "tw";
            } else if (locale.equals(Locale.SIMPLIFIED_CHINESE)) {
                lan = "ch";
            } else if (locale.equals(Locale.ENGLISH) || locale.toString().equals("en_US".toString())) {
                lan = "en";
            }
//            }
            if (Config.isWhiteBoardTest) {
//              xWalkView.loadUrl("http://192.168.1.182:8020/phone_demo/index.html#/?ts="+System.currentTimeMillis());
//                  xWalkView.loadUrl("http://192.168.1.12:8020/phone_demo/index.html#/?ts="+System.currentTimeMillis());
//            web_white_pad.loadUrl("http://192.168.1.11:8020/phone_demo/index.html#/?ts="+System.currentTimeMillis());
//                xWalkView.loadUrl("http://192.168.1.17/static/phone_demo/index.html#/?ts="+System.currentTimeMillis());//17
                xWalkView.loadUrl("http://192.168.1.251/index.html#/?languageType=" + lan);//建行
                xWalkView.loadUrl("http://192.168.1.182:9403/publish/index.html#/mobileApp?ts="+System.currentTimeMillis());//react 重构

            } else {
                xWalkView.loadUrl("file:///android_asset/phone_demo/index.html#/?languageType=" + lan);
            }

//            web_white_pad.setWebViewClient(new WebViewClient() {
//                @Override
//                public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                    view.loadUrl(url);
//                    return false;
//                }
//
//                @Override
//                public void onPageFinished(WebView view, String url) {
//                    super.onPageFinished(view, url);
//                }
//            });
            xWalkView.setUIClient(new XWalkUIClient(xWalkView) {
                @Override
                protected Object getBridge() {
                    return super.getBridge();
                }

                @Override
                public void onPageLoadStarted(XWalkView view, String url) {
                    super.onPageLoadStarted(view, url);
                }

                @Override
                public void onPageLoadStopped(XWalkView view, String url, LoadStatus status) {
                    super.onPageLoadStopped(view, url, status);
                }
            });

            xWalkView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {

//                        case MotionEvent.ACTION_DOWN:
//
//                        case MotionEvent.ACTION_MOVE:

                        case MotionEvent.ACTION_UP:
                            if (!candraw || !isTouchable) {
                                xWalkView.callOnClick();
                            } else {
                                return v.onTouchEvent(event);
                            }

                            break;
                        default:
                            break;
                    }
                    return false;
                }

            });
        } else {
            ViewGroup parent = (ViewGroup) fragmentView.getParent();
            if (parent != null) {
                parent.removeView(fragmentView);
            }
        }
        return fragmentView;
    }

    public void setWebWhitePadOnClickListener(View.OnClickListener padOnClickListener) {
        this.pageClickListener = padOnClickListener;
    }

//    public void setWBCallBack(WBStateCallBack wbCallBack) {
//        this.callBack = wbCallBack;
//    }

    public void setDrawable(boolean candraw) {
        this.candraw = candraw;
        if (xWalkView != null) {
            xWalkView.loadUrl("javascript:GLOBAL.phone.drawPermission(" + candraw + ")");
        }
    }

    public void setWBTouchable(boolean isTouchable) {
        this.isTouchable = isTouchable;
    }

    @Override
    public void onFileList(Object o) {
        ArrayList<ShareDoc> docList = new ArrayList<ShareDoc>();
        try {
            JSONArray jsobjs = new JSONArray(o.toString());
            for (int i = 0; i < jsobjs.length(); i++) {
                JSONObject jsobj = jsobjs.optJSONObject(i);
                ShareDoc doc = new ShareDoc();

                doc.setPdfpath(jsobj.optString("pdfpath"));
                doc.setFilepath(jsobj.optString("filepath"));
                doc.setAnimation(jsobj.optInt("animation"));
                doc.setStatus(jsobj.optInt("status"));
                doc.setDownloadpath(jsobj.optString("downloadpath"));
                doc.setPagenum(jsobj.optInt("pagenum"));
                doc.setUploadusername(jsobj.optString("uploadusername"));
                doc.setNewfilename(jsobj.optString("newfilename"));
                doc.setUploaduserid(jsobj.optInt("uploaduserid"));
                doc.setSwfpath(jsobj.optString("swfpath"));
                doc.setFileid(jsobj.optInt("fileid"));
                doc.setIsconvert(jsobj.optInt("isconvert"));
                doc.setSize(jsobj.optInt("size"));
                doc.setCompanyid(jsobj.optInt("companyid"));
                doc.setFileserverid(jsobj.optInt("fileserverid"));
                doc.setUploadtime(jsobj.optString("uploadtime"));
                doc.setActive(jsobj.optInt("active"));
                doc.setFilename(jsobj.optString("filename"));
                doc.setFiletype(jsobj.optString("filetype"));
                doc.setCurrentPage(1);
                doc.setCurrentTime(jsobj.optDouble("currenttime"));
                doc.setType(jsobj.optInt("type"));
                doc.setMedia(getIsMedia(doc.getFilename()));
                doc.setFileprop(jsobj.optInt("fileprop"));
                doc.setDynamicPPT(doc.getFileprop() == 2);
                doc.setGeneralFile(doc.getFileprop() == 0);
                doc.setH5Docment(doc.getFileprop() == 3);
//                if(doc.getFileprop() != 1){
                WhiteBoradManager.getInstance().addDocList(doc);
//                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean getIsMedia(String filename) {
        boolean isMedia = false;
        if (filename.toLowerCase().endsWith(".mp3")
                || filename.toLowerCase().endsWith("mp4")
                || filename.toLowerCase().endsWith("webm")
                || filename.toLowerCase().endsWith("ogg")
                || filename.toLowerCase().endsWith("wav")) {
            isMedia = true;
        }
        return isMedia;
    }

    @Override
    public boolean onRemoteMsg(boolean add, String id, String name, long ts, Object data, String fromID) {
        if (add) {
            if (name.equals("ClassBegin")) {
                isClassBegin = true;
                JSWhitePadInterface.isClassbegin = true;

                if (role == 0) {
                    JSONObject jsdata = Packager.pageSendData(currentFile);
                    RoomManager.getInstance().pubMsg("ShowPage", "DocumentFilePage_ShowPage", "__allExceptSender", jsdata.toString(), true, null, null);
                }
                sendRoomTypeWB();

            } else if (id.equals("DocumentFilePage_ShowPage")) {
                currentFile = WhiteBoradManager.getInstance().getCurrentFileDoc();
                String strdata = (String) data;
                try {
                    JSONObject jsmdata = new JSONObject(strdata);
                    currentFile = Packager.pageDoc(jsmdata);
                    WhiteBoradManager.getInstance().setCurrentFileDoc(currentFile);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (name.equals("DocumentChange")) {
                String strdata = (String) data;
                ShareDoc doc = new ShareDoc();
                try {
                    JSONObject jsmdata = new JSONObject(strdata);
                    boolean isdel = Tools.isTure(jsmdata.get("isDel"));
                    doc = Packager.pageDoc(jsmdata);
                    if (!isClassBegin && doc.getFileid() == RoomManager.getInstance().currentMediaStreamFileId) {
                        RoomManager.getInstance().unPublishMedia();
                    }
                    WhiteBoradManager.getInstance().onRoomFileChange(doc, isdel, false, isClassBegin);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (name.equals("ClassBegin")) {
                isClassBegin = false;
                JSWhitePadInterface.isClassbegin = false;
                clearLcAllData();
                WhiteBoradManager.getInstance().resumeFileList();
                if (WhiteBoradManager.getInstance().getDefaultFileDoc() == null) {
                    if (WhiteBoradManager.getInstance().getDocList().size() > 1) {
                        currentFile = WhiteBoradManager.getInstance().getDocList().get(1);
                        WhiteBoradManager.getInstance().setCurrentFileDoc(currentFile);
                    } else {
                        currentFile = WhiteBoradManager.getInstance().getDocList().get(0);
                        WhiteBoradManager.getInstance().setCurrentFileDoc(currentFile);
                    }
                } else {
                    currentFile = WhiteBoradManager.getInstance().getDefaultFileDoc();
                    WhiteBoradManager.getInstance().setCurrentFileDoc(currentFile);
                }
                JSONObject jsobj = new JSONObject();
                JSONObject resumedasta = Packager.pageSendData(currentFile);
                try {
                    jsobj.put("data", resumedasta.toString());
                    jsobj.put("name", "ShowPage");
                    jsobj.put("id", "DocumentFilePage_ShowPage");
                    xWalkView.loadUrl("javascript:GLOBAL.phone.receivePhoneByTriggerEvent('publish-message-received'," + jsobj.toString() + ")");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }


                //                web_white_pad.loadUrl("javascript:GLOBAL.phone.pageTurningPermission(" + true + ")");
            }
        }
        JSONObject jsobj = new JSONObject();
        try {
            jsobj.put("id", id);
            jsobj.put("ts", ts);
            jsobj.put("data", data == null ? null : data.toString());
            jsobj.put("name", name);
            jsobj.put("fromID", fromID);

            if (add) {
                xWalkView.loadUrl("javascript:GLOBAL.phone.receivePhoneByTriggerEvent('publish-message-received'," + jsobj.toString() + ")");
            } else {
                xWalkView.loadUrl("javascript:GLOBAL.phone.receivePhoneByTriggerEvent('delete-message-received'," + jsobj.toString() + ")");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (name.equals("WBPageCount") || name.equals("SharpsChange")) {
            return true;
        }
        return false;
    }

    private void sendRoomTypeWB() {
        JSONObject jsobj = new JSONObject();
        try {
            jsobj.put("roomType", RoomManager.getInstance().getRoomType());
            if (xWalkView != null)
                xWalkView.loadUrl("javascript:GLOBAL.phone.oneToMany (" + jsobj.toString() + ")");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendJoinRoomDataToWB() {
        JSONObject jsobj = new JSONObject();
        try {
            jsobj.put("peerid", RoomManager.getInstance().getMySelf().peerId);
            jsobj.put("nickname", RoomManager.getInstance().getMySelf().nickName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (xWalkView != null) {
            xWalkView.loadUrl("javascript:GLOBAL.phone.joinRoom(" + jsobj.toString() + ")");
        }
    }

    private static HashMap<String, Object> toHashMap(String str) {
        HashMap<String, Object> data = new HashMap<String, Object>();
        // 将json字符串转换成jsonObject
        try {
            JSONObject jsonObject = new JSONObject(str);
            Iterator it = jsonObject.keys();
            // 遍历jsonObject数据，添加到Map对象
            while (it.hasNext()) {
                String key = String.valueOf(it.next());
                Object value = jsonObject.get(key);
                data.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void onRemoteMsgList(Object o) {
        Log.d("xiao", o.toString());
        ArrayList as = (ArrayList) o;
        JSONObject jsdata = new JSONObject();
        for (int i = 0; i < as.size(); i++) {
            JSONObject js = mapToJson((Map<String, Object>) as.get(i));
            String id = js.optString("id");
            Object data = js.opt("data");
            try {
                jsdata.put(id, js);
                if ("ClassBegin".equals(js.optString("name"))) {
                    isClassBegin = true;
                    JSWhitePadInterface.isClassbegin = true;
                    sendRoomTypeWB();

                } else if (id.equals("DocumentFilePage_ShowPage")) {
                    currentFile = WhiteBoradManager.getInstance().getCurrentFileDoc();
                    JSONObject jsmdata = null;
                    if (data instanceof JSONObject) {
                        jsmdata = (JSONObject) data;
                    } else if (data instanceof String) {
                        String strdata = (String) data;
                        jsmdata = new JSONObject(strdata);
                    }
                    currentFile = Packager.pageDoc(jsmdata);
                    WhiteBoradManager.getInstance().setCurrentFileDoc(currentFile);
                    WhiteBoradManager.getInstance().getDocList();
                } else if (id.equals("WBPageCount")) {
                    JSONObject jsmdata = null;
                    if (data instanceof JSONObject) {
                        jsmdata = (JSONObject) data;
                    } else if (data instanceof String) {
                        String strdata = (String) data;
                        jsmdata = new JSONObject(strdata);
                    }
                    WhiteBoradManager.getInstance().getDocList().get(0).setPagenum(jsmdata.optInt("totalPage"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        xWalkView.loadUrl("javascript:GLOBAL.phone.receivePhoneByTriggerEvent('message-list-received'," + jsdata + ")");
        if (!jsdata.has("DocumentFilePage_ShowPage")) {
            if (WhiteBoradManager.getInstance().getDefaultFileDoc() == null) {
                if (WhiteBoradManager.getInstance().getDocList().size() > 1) {
                    currentFile = WhiteBoradManager.getInstance().getDocList().get(1);
                    WhiteBoradManager.getInstance().setCurrentFileDoc(currentFile);
                } else {
                    currentFile = WhiteBoradManager.getInstance().getDocList().get(0);
                    WhiteBoradManager.getInstance().setCurrentFileDoc(currentFile);
                }
            } else {
                currentFile = WhiteBoradManager.getInstance().getDefaultFileDoc();
                WhiteBoradManager.getInstance().setCurrentFileDoc(currentFile);
            }
            JSONObject jsobj = new JSONObject();
            JSONObject data = Packager.pageSendData(currentFile);
            try {
                jsobj.put("data", data.toString());
                jsobj.put("name", "ShowPage");
                jsobj.put("id", "DocumentFilePage_ShowPage");
                xWalkView.loadUrl("javascript:GLOBAL.phone.receivePhoneByTriggerEvent('publish-message-received'," + jsobj.toString() + ")");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }


        }
        setTurnPagePermission(RoomManager.getInstance().getMySelf().role == 0 || (RoomManager.getInstance().getMySelf().role == 2 && RoomControler.isStudentCanTurnPage()));
    }

    @Override
    public void roomManagerUserPublished() {
        sendPlayingListToWB();
    }

    @Override
    public void roomManagerUserUnPublished() {
        sendPlayingListToWB();
    }

    @Override
    public void roomManagerUserLeft() {
        sendPlayingListToWB();
    }

    private void sendPlayingListToWB() {
        JSONObject jsusers = new JSONObject();
        for (RoomUser u : RoomManager.getInstance().getUsers().values()) {
            if (u.publishState > 0 && u.role == 2) {
                try {
                    jsusers.put(u.peerId, u.nickName);
                    if (xWalkView != null) {
                        xWalkView.loadUrl("javascript:GLOBAL.phone.userSelector (" + jsusers.toString() + ")");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            xWalkView.requestFocus();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void localChangeDoc() {
        JSONObject jsobj = new JSONObject();
        currentFile = WhiteBoradManager.getInstance().getCurrentFileDoc();
        JSONObject data = Packager.pageSendData(currentFile);
        try {
            jsobj.put("data", data.toString());
            jsobj.put("name", "ShowPage");
            jsobj.put("id", "DocumentFilePage_ShowPage");
            xWalkView.loadUrl("javascript:GLOBAL.phone.receivePhoneByTriggerEvent('publish-message-received'," + jsobj.toString() + ")");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject mapToJson(Map<String, Object> map) {
        JSONObject jsb = new JSONObject(map);
        return jsb;
    }

    @Override
    public void sendBoardData(String js) {
        Log.d("xiao", js);
        try {
            JSONObject jsobj = new JSONObject(js);
            String msgName = jsobj.optString("signallingName");
            String msgId = jsobj.optString("id");
            String toId = jsobj.optString("toID");
            String data = jsobj.optString("data");
            String associatedMsgID = jsobj.optString("associatedMsgID");
            String associatedUserID = jsobj.optString("associatedUserID");
            boolean save = jsobj.optBoolean("do_not_save", false);
            if (jsobj.has("do_not_save")) {
                save = false;
            } else {
                save = true;
            }

            JSONObject jsdata = new JSONObject(data);
            if (msgId.equals("DocumentFilePage_ShowPage")) {
                currentFile = Packager.pageDoc(jsdata);
                WhiteBoradManager.getInstance().addDocList(currentFile);
                WhiteBoradManager.getInstance().setCurrentFileDoc(currentFile);
                WhiteBoradManager.getInstance().getDocList();
            }
//            if (!isClassBegin||(RoomManager.getInstance().getMySelf().role == 2&&msgId.equals("DocumentFilePage_ShowPage"))) {
//                return;
//            }
            int myrole = RoomManager.getInstance().getMySelf().role;
            //������һ�����Ծ���dataӦ����jsonobject����������ֻ�ܷ���String���ǶԵġ�
            if (isClassBegin && msgId.equals("DocumentFilePage_ShowPage") && (myrole == 0 || (myrole == 2 && candraw))) {
                RoomManager.getInstance().pubMsg(msgName, msgId, toId, data, save, associatedMsgID, associatedUserID);
            } else if (isClassBegin && !msgId.equals("DocumentFilePage_ShowPage")) {
                RoomManager.getInstance().pubMsg(msgName, msgId, toId, data, save, associatedMsgID, associatedUserID);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    @Override
    public void deleteBoardData(String js) {
        Log.d("xiao", js);
        try {
            JSONObject jsobj = new JSONObject(js);
            String msgName = jsobj.optString("signallingName");
            String msgId = jsobj.optString("id");
//            String toId = jsobj.optString("toID");
            String toId = jsobj.optString("toID");
            String data = jsobj.optString("data");
//            Map<String,Object> datamap = new HashMap<String,Object>();
//            if(data!=null){
//                datamap =  toMap(data);
//            }
            boolean save = true;
            //������һ�����Ծ���dataӦ����jsonobject����������ֻ�ܷ���String���ǶԵġ�
            RoomManager.getInstance().delMsg(msgName, msgId, toId, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageFinished() {
        Log.d("xiao", "onPageFinished: ");

        final JSONObject j = new JSONObject();
        try {
            j.put("mClientType", 3);
            j.put("deviceType", 1);
            JSONObject jsServiceUrl = new JSONObject();
            jsServiceUrl.put("address", "http://" + WhiteBoradManager.getInstance().getFileServierUrl());
            jsServiceUrl.put("port", WhiteBoradManager.getInstance().getFileServierPort());
            j.put("serviceUrl", jsServiceUrl);
            j.put("addPagePermission", false);
            j.put("playback", isPlayBack);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    xWalkView.loadUrl("javascript:GLOBAL.phone.setInitPageParameterFormPhone(" + j + ")");

                }
            });
        }

        WhiteBoradManager.getInstance().onPageFinished();

    }

    public void clearLcAllData() {
        if (xWalkView != null) {
            xWalkView.loadUrl("javascript:GLOBAL.phone.clearLcAllData()");
        }
    }

    @Override
    public void fullScreenToLc(boolean isFull) {
        WhiteBoradManager.getInstance().fullScreenToLc(isFull);
    }

    @Override
    public void onJsPlay(String url, boolean isvideo, long fileid) {
        WhiteBoradManager.getInstance().onWBMediaPublish(url, isvideo, fileid);
    }

    public void setAddPagePermission(boolean canAdd) {
        JSONObject js = new JSONObject();
        try {
            js.put("addPagePermission", canAdd);
            xWalkView.loadUrl("javascript:GLOBAL.phone.changeInitPageParameterFormPhone(" + js + ")");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setTurnPagePermission(boolean canTrun) {
        xWalkView.loadUrl("javascript:GLOBAL.phone.pageTurningPermission(" + canTrun + ")");
    }

    public void setToolBarMode(int mode) {
        this.role = mode;
        JSONObject js = new JSONObject();
        try {
            js.put("role", mode);
            xWalkView.loadUrl("javascript:GLOBAL.phone.changeInitPageParameterFormPhone(" + js + ")");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public void wbRequestFoucs(boolean isvi) {
//        if (xWalkView != null) {
//            if (isvi)
//                xWalkView.onResume();
//            else
//                xWalkView.onPause();
//        }
//    }

    @Override
    public void onStop() {
        super.onStop();
        if (getActivity().isFinishing()) {
            xWalkView.removeAllViews();
            xWalkView.onDestroy();
        }
    }

    public void closeNewPptVideo() {
        if (xWalkView != null) {
            xWalkView.loadUrl("javascript:GLOBAL.phone.closeNewPptVideo()");
        }
    }

    public void dispatchEvent(String type) {
        JSONObject js = new JSONObject();
        try {
            js.put("type", type);
            xWalkView.loadUrl("javascript:GLOBAL.phone.dispatchEvent(" + js + ")");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
