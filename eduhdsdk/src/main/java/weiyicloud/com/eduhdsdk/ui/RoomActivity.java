package weiyicloud.com.eduhdsdk.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.classroomsdk.ShareDoc;
import com.classroomsdk.WBFragment;
import com.classroomsdk.WBStateCallBack;
import com.classroomsdk.WhiteBoradManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.talkcloud.roomsdk.IRoomManagerCbk;
import com.talkcloud.roomsdk.RoomControler;
import com.talkcloud.roomsdk.RoomManager;
import com.talkcloud.roomsdk.RoomUser;
import com.talkcloud.roomsdk.Stream;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.EglBase;
import org.webrtc.SurfaceViewRenderer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import weiyicloud.com.eduhdsdk.R;
import weiyicloud.com.eduhdsdk.adapter.ChatListAdapter;
import weiyicloud.com.eduhdsdk.adapter.FileListAdapter;
import weiyicloud.com.eduhdsdk.adapter.MediaListAdapter;
import weiyicloud.com.eduhdsdk.adapter.MemberListAdapter;
import weiyicloud.com.eduhdsdk.entity.ChatData;
import weiyicloud.com.eduhdsdk.entity.MoveVideoInfo;
import weiyicloud.com.eduhdsdk.entity.TimeMessage;
import weiyicloud.com.eduhdsdk.entity.VideoItem;
import weiyicloud.com.eduhdsdk.interfaces.TranslateCallback;
import weiyicloud.com.eduhdsdk.message.BroadcastReceiverMgr;
import weiyicloud.com.eduhdsdk.message.RoomClient;
import weiyicloud.com.eduhdsdk.tools.SoundPlayUtils;
import weiyicloud.com.eduhdsdk.tools.Tools;
import weiyicloud.com.eduhdsdk.tools.Translate;


public class RoomActivity extends FragmentActivity implements IRoomManagerCbk, View.OnClickListener, WBStateCallBack, TranslateCallback {

    WBFragment wbFragment;
    //视频块
    LinearLayout vdi_teacher;
    LinearLayout vdi_stu_in_sd;

    VideoItem teacherItem;
    VideoItem stu_in_sd;

    TextView txt_all_mute;
    TextView txt_all_send_gift;

    //顶部工具条
    ImageView img_back;
    LinearLayout lin_back_and_name;
    ImageView img_close;
    TextView txt_room_name;
    ImageView img_file_list;
    ImageView img_media_list;
    ImageView img_member_list;
    LinearLayout lin_top_buttons;
    TextView txt_class_begin;
    //聊天区域
    RelativeLayout rel_chat_part;
    TextView txt_chat_input;
    TextView txt_send_msg;
    ListView list_chat;
    //时间
    TextView txt_hour;
    TextView txt_min;
    TextView txt_ss;
    TextView txt_mao_01;
    TextView txt_mao_02;
    //MP3音频
    ImageView img_disk;
    LinearLayout lin_audio_control;
    ImageView img_play_mp3;
    TextView txt_mp3_name;
    TextView txt_mp3_time;
    SeekBar sek_mp3;
    ImageView img_voice_mp3;
    SeekBar sek_voice_mp3;
    ImageView img_close_mp3;
    LinearLayout lin_control_tool;
    ImageView img_back_play_back;
    ArrayList<VideoItem> videoItems = new ArrayList<VideoItem>();
    HashMap<String, Boolean> playingMap = new HashMap<String, Boolean>();
    public static HashSet<String> publishSet = new HashSet<String>();
    public static HashSet<String> pandingSet = new HashSet<String>();
    List<RoomUser> playingList = Collections.synchronizedList(new ArrayList<RoomUser>());
    ArrayList<RoomUser> memberList = new ArrayList<RoomUser>();
    ArrayList<ChatData> chatList = new ArrayList<ChatData>();

    MemberListAdapter memberListAdapter = new MemberListAdapter(this, memberList, publishSet);
    FileListAdapter fileListAdapter;
    MediaListAdapter mediaListAdapter;
    ChatListAdapter chlistAdapter;

    PowerManager pm;
    PowerManager.WakeLock mWakeLock;
    private GestureDetector gestureScanner;

    public static int myRrole = -1;
    private int roomType = -1;
    public static boolean isClassBegin = false;
    private long localTime;
    private long classStartTime;
    private long serviceTime;
    Timer timerAddTime;
    Timer timerbefClassbegin;

    //    public static boolean isExit = false;
    Animation operatingAnim;
    //    Timer t;

    private String host = "";
    private int port = 80;
    private String nickname = "";
    private String userid = "";
    private String serial = "";
    private String password = "";
    private int userrole = -1;
    private String path = "";
    private int type = -1;

    private String param = "";
    private String domain = "";
    private String mobilename = "";
    private boolean mobilenameNotOnList = true;
    private AsyncHttpClient client = new AsyncHttpClient();

    private TextView txt_hand_up;
    private LinearLayout lin_hand_and_photo;
    private TextView txt_send_up_photo;
    private View v_students;
    private RelativeLayout rel_students;

    //需要计算尺寸的布局
    RelativeLayout rel_tool_bar;
    LinearLayout lin_time;
    LinearLayout lin_menu;
    LinearLayout lin_main;
    RelativeLayout rel_w;
    RelativeLayout rel_tool_bar_play_back;

    FragmentManager fragmentManager;
    FragmentTransaction ft;
    VideoFragment videofragment;
    FrameLayout fm_video_container;
    RelativeLayout rel_parent;
    RelativeLayout rel_wb_container;

    //    LinearLayout lin_self_av_control;
    //    TextView txt_video;
    //    TextView txt_audio;

    //回放使用的控件
    private RelativeLayout rel_play_back;
    private ImageView img_play_back;
    private SeekBar sek_play_back;
    private TextView txt_play_back_time;


    private boolean isInRoom = false;
    private final int REQUEST_CODED = 3;
    private boolean canClassDissMiss = false;
    int tipnum = 0;
    private boolean isZoom = false;
    private ArrayList<TimeMessage> timeMessages = new ArrayList<TimeMessage>();

    public static int maxVideo = 6;
    private boolean isAudioShow = false;
    private boolean isVideoShow = false;

    private boolean processVideo = false;
    private ArrayList<String> unpublishlist = new ArrayList<String>();
    AudioManager audioManager;
    Timer checkNetTimer = null;
    int netBreakCount = 0;
    public static boolean isFromList;
    public static boolean isPause;
    private String mp3Duration = "00:00";
    private Stream stream;
    private double vol = 0.5;
    private static boolean isMediaMute = false;
    private String sendgiftlock = "sendgiftlock";
    private boolean isSending = false;
    Timer tSendGift = null;
    private boolean isPlayBackPlay = true;
    private boolean isEnd = false;

    private ScreenFragment screenFragment;

    private PopupWindow popupWindowPhoto;

    JSONObject videoarr = null;
    Iterator<String> sIterator = null;
    Map<String, MoveVideoInfo> stuMoveInfoMap = new HashMap<String, MoveVideoInfo>();
    boolean isPauseLocalVideo = false;
    boolean isOpenCamera = false;
    NotificationManager nm = null;
    NotificationCompat.Builder mBuilder = null;
    private PopupWindow mPopupWindow;
    private PopupWindow mWindow;
    private FrameLayout mWb_content;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_room);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initTimeMessage();
        resultActivityBackApp();
        mWb_content = (FrameLayout) findViewById(R.id.wb_container);
        lin_control_tool = (LinearLayout) findViewById(R.id.lin_control_tool);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back_play_back = (ImageView) findViewById(R.id.img_back_play_back);
        lin_back_and_name = (LinearLayout) findViewById(R.id.lin_back_and_name);
        img_close = (ImageView) findViewById(R.id.img_close);
        txt_room_name = (TextView) findViewById(R.id.txt_pad_name);
        img_file_list = (ImageView) findViewById(R.id.img_file_list);
        img_media_list = (ImageView) findViewById(R.id.img_media_list);
        img_member_list = (ImageView) findViewById(R.id.img_member_list);
        lin_top_buttons = (LinearLayout) findViewById(R.id.lin_top_buttons);
        txt_class_begin = (TextView) findViewById(R.id.txt_class_begin);
        txt_all_mute = (TextView) findViewById(R.id.txt_all_mute);
        txt_all_send_gift = (TextView) findViewById(R.id.txt_all_send_gift);
        rel_chat_part = (RelativeLayout) findViewById(R.id.rel_chat_part);
        txt_chat_input = (TextView) findViewById(R.id.txt_chat_input);
        txt_send_msg = (TextView) findViewById(R.id.txt_send_msg);
        list_chat = (ListView) findViewById(R.id.list_chat);
        txt_hour = (TextView) findViewById(R.id.txt_hour);
        txt_min = (TextView) findViewById(R.id.txt_min);
        txt_ss = (TextView) findViewById(R.id.txt_ss);
        txt_hand_up = (TextView) findViewById(R.id.txt_hand_up);
        lin_hand_and_photo = (LinearLayout) findViewById(R.id.lin_hand_and_photo);
        txt_send_up_photo = (TextView) findViewById(R.id.txt_send_up_photo);

        v_students = (View) findViewById(R.id.v_student);
        rel_students = (RelativeLayout) findViewById(R.id.rel_students);
        rel_tool_bar = (RelativeLayout) findViewById(R.id.rel_tool_bar);
        rel_tool_bar_play_back = (RelativeLayout) findViewById(R.id.rel_tool_bar_play_back);

        lin_time = (LinearLayout) findViewById(R.id.lin_time);
        lin_menu = (LinearLayout) findViewById(R.id.lin_menu);
        lin_main = (LinearLayout) findViewById(R.id.lin_main);
        rel_w = (RelativeLayout) findViewById(R.id.rel_w);
        rel_parent = (RelativeLayout) findViewById(R.id.rel_parent);
        rel_wb_container = (RelativeLayout) findViewById(R.id.rel_wb_container);
        txt_mao_01 = (TextView) findViewById(R.id.txt_mao_01);
        txt_mao_02 = (TextView) findViewById(R.id.txt_mao_02);

        rel_play_back = (RelativeLayout) findViewById(R.id.rel_play_back);
        img_play_back = (ImageView) findViewById(R.id.img_play_back);
        sek_play_back = (SeekBar) findViewById(R.id.sek_play_back);
        txt_play_back_time = (TextView) findViewById(R.id.txt_play_back_time);

        fm_video_container = (FrameLayout) findViewById(R.id.video_container);

        //        lin_self_av_control = (LinearLayout) findViewById(R.id.lin_self_av_control);
        //        txt_video = (TextView) findViewById(R.id.txt_video);
        //        txt_audio = (TextView) findViewById(R.id.txt_audio);


        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        getExData();
        initVideoItem();
        audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 75, 0);
        initMediaView();
        initPlayBackView();


        RoomManager.getInstance().globalInitialize(getApplicationContext());
        RoomManager.getInstance().setCallbBack(this);

        Translate.getInstance().setCallback(this);

        chlistAdapter = new ChatListAdapter(chatList, this);
        list_chat.setAdapter(chlistAdapter);


        //        img_back.setOnClickListener(this);
        lin_back_and_name.setOnClickListener(this);
        img_close.setOnClickListener(this);
        img_file_list.setOnClickListener(this);
        img_media_list.setOnClickListener(this);
        img_member_list.setOnClickListener(this);
        txt_class_begin.setOnClickListener(this);
        txt_class_begin.setBackgroundResource(R.drawable.round_back_red_black);
        txt_class_begin.setClickable(false);
        txt_all_mute.setOnClickListener(this);
        txt_all_send_gift.setOnClickListener(this);
        txt_send_msg.setOnClickListener(this);
        txt_chat_input.setOnClickListener(this);
        img_back_play_back.setOnClickListener(this);
        txt_send_up_photo.setOnClickListener(this);
        //        txt_video.setOnClickListener(this);
        //        txt_audio.setOnClickListener(this);

        operatingAnim = AnimationUtils.loadAnimation(RoomActivity.this, R.anim.disk_aim);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        memberListAdapter = new MemberListAdapter(this, memberList, publishSet);
        fileListAdapter = new FileListAdapter(this, WhiteBoradManager.getInstance().getDocList(), serial);
        mediaListAdapter = new MediaListAdapter(this, WhiteBoradManager.getInstance().getMediaList(), serial);

        checkNetTimer = new Timer();
        checkNetTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (!isNetworkAvailable(RoomActivity.this) && isInRoom && netBreakCount == 10) {
                    RoomActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            wbFragment.setDrawable(false);
                            wbFragment.clearLcAllData();
                        }
                    });
                }
                netBreakCount++;
            }
        }, 0, 1000);
        Tools.ShowProgressDialog(this, getResources().getString(R.string.joining_classroom));
        wbFragment = new WBFragment();
        wbFragment.setMobilenameOnList(mobilenameNotOnList);
        if (path != null && !path.isEmpty()) {
            wbFragment.setPlayBack(true);
        }
        WhiteBoradManager.getInstance().setWBCallBack(this);
        WhiteBoradManager.getInstance().setFileServierUrl(host);
        WhiteBoradManager.getInstance().setFileServierPort(port);
        WhiteBoradManager.getInstance().setLocalControl(wbFragment);
        mFragmentManager = getSupportFragmentManager();
        mFt = mFragmentManager.beginTransaction();

        if (!wbFragment.isAdded()) {
            mFt.add(R.id.wb_container, wbFragment);
            mFt.commit();
        }
        RoomManager.getInstance().setWhiteBoard(wbFragment);
        SoundPlayUtils.init(this);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        doLayout();
    }

    private void initTimeMessage() {
        timeMessages.clear();
        String one = "<font color='#FFD700'>1</font>";
        TimeMessage tms1 = new TimeMessage();
        tms1.message = getString(R.string.classroom_tip_01_01) + "<font color='#FFD700'>1</font>" + getString(R.string.classroom_tip_01_02);
        TimeMessage tms2 = new TimeMessage();
        tms2.message = getString(R.string.classroom_tip_02_01) + "<font color='#FFD700'>1</font>" + getString(R.string.classroom_tip_02_02);
        TimeMessage tms3 = new TimeMessage();
        tms3.message = getString(R.string.classroom_tip_03_01) + "<font color='#FFD700'>1</font>" + getString(R.string.classroom_tip_03_02);
        TimeMessage tms4 = new TimeMessage();
        tms4.message = getString(R.string.classroom_tip_04_01) + "<font color='#FFD700'>3</font>" + getString(R.string.classroom_tip_04_02) + "<font color='#FFD700'>2</font>" + getString(R.string.classroom_tip_04_03);
        TimeMessage tms5 = new TimeMessage();
        tms5.message = getString(R.string.classroom_tip_05);
        tms5.hasKonwButton = false;
        timeMessages.add(tms1);
        timeMessages.add(tms2);
        timeMessages.add(tms3);
        timeMessages.add(tms4);
        timeMessages.add(tms5);
    }

    private void initMediaView() {
        //mp3
        img_disk = (ImageView) findViewById(R.id.img_disk);
        lin_audio_control = (LinearLayout) findViewById(R.id.lin_audio_control);
        img_play_mp3 = (ImageView) findViewById(R.id.img_play_mp3);
        txt_mp3_name = (TextView) findViewById(R.id.txt_mp3_name);
        txt_mp3_time = (TextView) findViewById(R.id.txt_mp3_time);
        sek_mp3 = (SeekBar) findViewById(R.id.sek_mp3);
        img_voice_mp3 = (ImageView) findViewById(R.id.img_voice_mp3);
        sek_voice_mp3 = (SeekBar) findViewById(R.id.sek_voice_mp3);
        img_close_mp3 = (ImageView) findViewById(R.id.img_close_mp3);

        img_close_mp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoomManager.getInstance().unPublishMedia();
                lin_audio_control.setVisibility(View.INVISIBLE);
            }
        });
        img_play_mp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stream != null) {
                    if (MediaListAdapter.isPublish) {
                        RoomManager.getInstance().playMedia((Boolean) stream.attrMap.get("pause") == null ? false : (Boolean) stream.attrMap.get("pause"));
                    } else {
                        ShareDoc media = WhiteBoradManager.getInstance().getCurrentMediaDoc();
                        WhiteBoradManager.getInstance().setCurrentMediaDoc(media);
                        String strSwfpath = media.getSwfpath();
                        int pos = strSwfpath.lastIndexOf('.');
                        strSwfpath = String.format("%s-%d%s", strSwfpath.substring(0, pos), 1, strSwfpath.substring(pos));
                        String url = "http://" + WhiteBoradManager.getInstance().getFileServierUrl() + ":" + WhiteBoradManager.getInstance().getFileServierPort() + strSwfpath;
                        if (isClassBegin) {
                            RoomManager.getInstance().publishMedia(url, com.classroomsdk.Tools.isMp4(media.getFilename()), media.getFilename(), media.getFileid(), "__all");
                        } else {
                            RoomManager.getInstance().publishMedia(url, com.classroomsdk.Tools.isMp4(media.getFilename()), media.getFilename(), media.getFileid(), RoomManager.getInstance().getMySelf().peerId);
                        }
                    }
                }
            }
        });
        sek_mp3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int pro = 0;
            boolean isfromUser = false;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    pro = progress;
                    isfromUser = fromUser;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double currenttime = 0;
                if (isfromUser && stream != null) {
                    currenttime = ((double) pro / (double) seekBar.getMax()) * (int) stream.attrMap.get("duration");
                    RoomManager.getInstance().seekMedia((long) currenttime);
                }
            }
        });
        img_voice_mp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMediaMute) {
                    RoomManager.getInstance().setRemoteStreamAudioVolume(vol);
                    img_voice_mp3.setImageResource(R.drawable.btn_volume_pressed);
                    sek_voice_mp3.setProgress((int) (vol * 100));
                } else {
                    RoomManager.getInstance().setRemoteStreamAudioVolume(0);
                    img_voice_mp3.setImageResource(R.drawable.btn_mute_pressed);
                    sek_voice_mp3.setProgress(0);
                }
                isMediaMute = !isMediaMute;
            }
        });
        sek_voice_mp3.setProgress((int) (vol * 100));
        sek_voice_mp3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float vol = (float) progress / (float) seekBar.getMax();
                if (vol > 0) {
                    img_voice_mp3.setImageResource(R.drawable.btn_volume_pressed);
                } else {
                    img_voice_mp3.setImageResource(R.drawable.btn_mute_pressed);
                }
                //                isMediaMute = !(progress>0);
                RoomManager.getInstance().setRemoteStreamAudioVolume(vol);
                if (fromUser) {
                    RoomActivity.this.vol = vol;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initPlayBackView() {
        rel_play_back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        sek_play_back.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    this.progress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                long pos = (long) (((double) progress / 100) * (endtime - starttime) + starttime);
                if (isEnd) {
                    img_play_back.setImageResource(R.drawable.btn_pause_normal);
                    RoomManager.getInstance().playPlayback();
                    isPlayBackPlay = !isPlayBackPlay;
                }
                isEnd = false;
                RoomManager.getInstance().seekPlayback(pos);
            }
        });
        img_play_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlayBackPlay) {
                    RoomManager.getInstance().pausePlayback();
                    img_play_back.setImageResource(R.drawable.btn_play_normal);
                } else {
                    if (isEnd) {
                        RoomManager.getInstance().seekPlayback(starttime);
                        currenttime = starttime;
                        RoomManager.getInstance().playPlayback();
                        img_play_back.setImageResource(R.drawable.btn_pause_normal);
                        isEnd = false;
                    } else {
                        RoomManager.getInstance().playPlayback();
                        img_play_back.setImageResource(R.drawable.btn_pause_normal);
                    }
                }
                isPlayBackPlay = !isPlayBackPlay;
            }
        });
    }

    private void showChatEditPop() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final int wid = dm.widthPixels;
        final int hid = dm.heightPixels;
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.chat_edit_pop, null);

        final PopupWindow popupWindow = new PopupWindow(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView txt_close_chat = (TextView) contentView.findViewById(R.id.txt_close_chat);
        TextView txt_send = (TextView) contentView.findViewById(R.id.txt_send);
        final EditText edt_input = (EditText) contentView.findViewById(R.id.edt_input);
        txt_close_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        txt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = edt_input.getText().toString().trim();
                if (msg != null && !msg.isEmpty()) {
                    RoomManager.getInstance().sendMessage(msg, 0);
                    edt_input.setText("");
                    popupWindow.dismiss();
                }
            }
        });
        edt_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_DONE){
                    String msg = edt_input.getText().toString().trim();
                    if (msg != null && !msg.isEmpty()) {
                        RoomManager.getInstance().sendMessage(msg, 0);
                        edt_input.setText("");
                        popupWindow.dismiss();
                    }
                }
                return true;
            }
        });
        popupWindow.setContentView(contentView);

        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //这里给它设置了弹出的时间，
        //软键盘不会挡着popupwindow
        imm.toggleSoftInput(1000, InputMethodManager.HIDE_NOT_ALWAYS);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.showAtLocation(findViewById(R.id.mainLayout), Gravity.TOP, 0, 150);
    }

    @Override
    protected void onStart() {
        //        if (isPauseLocalVideo) {
        //            RoomManager.getInstance().resumeLocalCamera();
        //            isPauseLocalVideo = !isPauseLocalVideo;
        //        }
        if (RoomManager.getInstance().getMySelf() != null) {
            nm.cancel(2);
            //            RoomManager.getInstance().delMsg("userEnterBackGround", "userEnterBackGround__"+RoomManager.getInstance().getMySelf().peerId, "__allSuperUsers", new HashMap<String, Object>());
            if (RoomManager.getInstance().getMySelf().role == 2) {
                RoomManager.getInstance().changeUserProperty(RoomManager.getInstance().getMySelf().peerId, "__all", "isInBackGround", false);
            }
        }
        isOpenCamera = false;


        mWakeLock.acquire();

        super.onStart();
        if (isInRoom) {
            initViewByRoomTypeAndTeacher();
        }
        doPlayVideo(null);

        if (RoomManager.getInstance().getMySelf() == null) {
            return;
        }
        if (RoomManager.getInstance().getMySelf().publishState == 1 || RoomManager.getInstance().getMySelf().publishState == 3) {
            RoomManager.getInstance().enableSendMyVoice(true);
        }
        RoomManager.getInstance().enableOtherAudio(false);
        RoomManager.getInstance().setMuteAllStream(false);
        closeSpeaker();
      /*  RoomManager.getInstance().useLoudSpeaker(true);*/

    }

    private int currVolume = 0;

    /**
     * 打开扬声器
     */
    private void openSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if (!audioManager.isSpeakerphoneOn()) {
                //setSpeakerphoneOn() only work when audio mode set to MODE_IN_CALL.
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStop() {
        if (!isFinishing()) {
            //            if (!isPauseLocalVideo) {
            //                RoomManager.getInstance().pauseLocalCamera();
            //                isPauseLocalVideo = !isPauseLocalVideo;
            //            }
            //            RoomManager.getInstance().enableSendMyVoice(false);
            //            RoomManager.getInstance().enableOtherAudio(true);
            //            RoomManager.getInstance().setMuteAllStream(true);
            nm.notify(2, mBuilder.build());
            //            RoomManager.getInstance().pubMsg("userEnterBackGround","userEnterBackGround__"+RoomManager.getInstance().getMySelf().peerId,"__allSuperUsers",new HashMap<String,Object>(),true,null,RoomManager.getInstance().getMySelf().peerId);
            if (RoomManager.getInstance().getMySelf().role == 2) {
                RoomManager.getInstance().changeUserProperty(RoomManager.getInstance().getMySelf().peerId, "__all", "isInBackGround", true);
            }
        }
        mWakeLock.release();
        if (isFinishing()) {
            if (timerAddTime != null) {
                timerAddTime.cancel();
                timerAddTime = null;
            }
            if (timerbefClassbegin != null) {
                timerbefClassbegin.cancel();
                timerbefClassbegin = null;
            }
            if (checkNetTimer != null) {
                checkNetTimer.cancel();
                checkNetTimer = null;
            }
            timeMessages.clear();
        }
        super.onStop();
    }

    /**
     * 双击判断
     */
    private int count;
    private long firClick;
    private long secClick;
    private boolean isFullScreen = false;

    private void initVideoItem() {
        vdi_teacher = (LinearLayout) findViewById(R.id.vdi_teacher);
        vdi_stu_in_sd = (LinearLayout) findViewById(R.id.vdi_stu_in_sd);
        teacherItem = new VideoItem();
        teacherItem.parent = vdi_teacher;
        teacherItem.sf_video = (SurfaceViewRenderer) vdi_teacher.findViewById(R.id.sf_video);
        teacherItem.sf_video.init(EglBase.create().getEglBaseContext(), null);
        teacherItem.img_mic = (ImageView) vdi_teacher.findViewById(R.id.img_mic);
        teacherItem.img_pen = (ImageView) vdi_teacher.findViewById(R.id.img_pen);
        teacherItem.img_hand = (ImageView) vdi_teacher.findViewById(R.id.img_hand_up);
        teacherItem.txt_name = (TextView) vdi_teacher.findViewById(R.id.txt_name);
        teacherItem.txt_gift_num = (TextView) vdi_teacher.findViewById(R.id.txt_gift_num);
        teacherItem.rel_group = (RelativeLayout) vdi_teacher.findViewById(R.id.rel_group);
        teacherItem.img_video_back = (ImageView) vdi_teacher.findViewById(R.id.img_video_back);
        teacherItem.lin_gift = (LinearLayout) vdi_teacher.findViewById(R.id.lin_gift);
        teacherItem.lin_gift.setVisibility(View.INVISIBLE);
        teacherItem.lin_name_label = (LinearLayout) vdi_teacher.findViewById(R.id.lin_name_label);
        teacherItem.rel_video_label = (RelativeLayout) vdi_teacher.findViewById(R.id.rel_video_label);
        stu_in_sd = new VideoItem();
        stu_in_sd.parent = vdi_stu_in_sd;
        stu_in_sd.sf_video = (SurfaceViewRenderer) vdi_stu_in_sd.findViewById(R.id.sf_video);
        stu_in_sd.sf_video.init(EglBase.create().getEglBaseContext(), null);
        stu_in_sd.img_cam = (ImageView) vdi_stu_in_sd.findViewById(R.id.img_cam);
        stu_in_sd.img_mic = (ImageView) vdi_stu_in_sd.findViewById(R.id.img_mic);
        stu_in_sd.img_pen = (ImageView) vdi_stu_in_sd.findViewById(R.id.img_pen);
        stu_in_sd.img_hand = (ImageView) vdi_stu_in_sd.findViewById(R.id.img_hand_up);
        stu_in_sd.txt_name = (TextView) vdi_stu_in_sd.findViewById(R.id.txt_name);
        stu_in_sd.txt_gift_num = (TextView) vdi_stu_in_sd.findViewById(R.id.txt_gift_num);
        stu_in_sd.rel_group = (RelativeLayout) vdi_stu_in_sd.findViewById(R.id.rel_group);
        stu_in_sd.img_video_back = (ImageView) vdi_stu_in_sd.findViewById(R.id.img_video_back);
        stu_in_sd.lin_gift = (LinearLayout) vdi_stu_in_sd.findViewById(R.id.lin_gift);
        stu_in_sd.lin_name_label = (LinearLayout) vdi_stu_in_sd.findViewById(R.id.lin_name_label);
        stu_in_sd.rel_video_label = (RelativeLayout) vdi_stu_in_sd.findViewById(R.id.rel_video_label);
    }

    private void setTeacherFullScreen() {
        teacherItem.sf_video.bringToFront();
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        final int wid = dm.widthPixels;
        final int hid = dm.heightPixels;

        RelativeLayout.LayoutParams stu_param_menu = (RelativeLayout.LayoutParams) teacherItem.sf_video.getLayoutParams();
        stu_param_menu.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        stu_param_menu.height = hid / 11 * 9;


        RelativeLayout.LayoutParams stu_parent_menu = (RelativeLayout.LayoutParams) teacherItem.rel_group.getLayoutParams();
        stu_parent_menu.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        stu_parent_menu.height = hid / 11 * 9;

        LinearLayout.LayoutParams stu_video_label_menu = (LinearLayout.LayoutParams) teacherItem.rel_video_label.getLayoutParams();
        stu_video_label_menu.width = LinearLayout.LayoutParams.MATCH_PARENT;
        stu_video_label_menu.height = hid / 11 * 9;


        LinearLayout.LayoutParams stu_par_menu = (LinearLayout.LayoutParams) teacherItem.parent.getLayoutParams();
        stu_par_menu.width = LinearLayout.LayoutParams.MATCH_PARENT;
        stu_par_menu.height = LinearLayout.LayoutParams.MATCH_PARENT;

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) teacherItem.lin_name_label.getLayoutParams();
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        layoutParams.height = hid / 11 * 2;
        teacherItem.lin_name_label.setLayoutParams(layoutParams);
        teacherItem.txt_name.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);


        teacherItem.parent.setLayoutParams(stu_par_menu);
        teacherItem.sf_video.setLayoutParams(stu_param_menu);
        teacherItem.rel_group.setLayoutParams(stu_parent_menu);
        teacherItem.rel_video_label.setLayoutParams(stu_video_label_menu);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) lin_menu.getLayoutParams();
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = LinearLayout.LayoutParams.MATCH_PARENT;
        lin_menu.setLayoutParams(params);
        lin_main.setVisibility(View.GONE);

        teacherItem.rel_video_label.bringChildToFront(teacherItem.rel_group);
        teacherItem.rel_video_label.updateViewLayout(teacherItem.rel_group, teacherItem.rel_group.getLayoutParams());


        for ( int i=0;i<videoItems.size();i++){
            videoItems.get(i).sf_video.setVisibility(View.GONE);
        }



    }

    private void initViewByRoomTypeAndTeacher() {
        if (roomType == 0) {//一对一
            lin_control_tool.setVisibility(View.GONE);
            rel_students.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
            params.weight = 1.0f;

            if (myRrole == 0) {//老师
                txt_chat_input.setVisibility(View.VISIBLE);
                lin_top_buttons.setVisibility(View.VISIBLE);
                if (!RoomControler.isShowClassBeginButton())
                    txt_class_begin.setVisibility(View.VISIBLE);
                lin_hand_and_photo.setVisibility(View.GONE);
                //                lin_self_av_control.setVisibility(View.GONE);
                rel_play_back.setVisibility(View.INVISIBLE);
            } else if (myRrole == 2) {//学生
                txt_chat_input.setVisibility(View.VISIBLE);
                lin_top_buttons.setVisibility(View.INVISIBLE);
                txt_class_begin.setVisibility(View.GONE);
                lin_hand_and_photo.setVisibility(View.VISIBLE);
                txt_send_up_photo.setVisibility(View.VISIBLE);
                txt_hand_up.setVisibility(View.GONE);
                rel_play_back.setVisibility(View.INVISIBLE);
            } else if (myRrole == 4) {
                if (!RoomControler.isShowClassBeginButton())
                    txt_class_begin.setVisibility(View.GONE);
                txt_chat_input.setVisibility(View.GONE);
                lin_top_buttons.setVisibility(View.INVISIBLE);
                lin_hand_and_photo.setVisibility(View.GONE);
                rel_play_back.setVisibility(View.INVISIBLE);
            } else if (myRrole == -1) {
                lin_top_buttons.setVisibility(View.INVISIBLE);
                rel_play_back.setVisibility(View.VISIBLE);
                lin_time.setVisibility(View.GONE);
            }
        } else {//一对多
            vdi_stu_in_sd.setVisibility(View.GONE);
            if (myRrole == 0) {//老师
                //                lin_control_tool.setVisibility(View.VISIBLE);
                lin_top_buttons.setVisibility(View.VISIBLE);
                if (!RoomControler.isShowClassBeginButton())
                    txt_class_begin.setVisibility(View.VISIBLE);
                lin_hand_and_photo.setVisibility(View.GONE);
                txt_chat_input.setVisibility(View.VISIBLE);
                rel_play_back.setVisibility(View.INVISIBLE);
                //                lin_self_av_control.setVisibility(View.GONE);
            } else if (myRrole == 2) {//学生
                lin_control_tool.setVisibility(View.GONE);
                lin_top_buttons.setVisibility(View.INVISIBLE);
                txt_class_begin.setVisibility(View.GONE);
                lin_hand_and_photo.setVisibility(View.VISIBLE);
                txt_chat_input.setVisibility(View.VISIBLE);
                rel_play_back.setVisibility(View.INVISIBLE);
                //                lin_self_av_control.setVisibility(View.VISIBLE);
            } else if (myRrole == 4) {
                if (!RoomControler.isShowClassBeginButton())
                    txt_class_begin.setVisibility(View.GONE);
                txt_chat_input.setVisibility(View.GONE);
                lin_top_buttons.setVisibility(View.INVISIBLE);
                lin_hand_and_photo.setVisibility(View.GONE);
                rel_play_back.setVisibility(View.INVISIBLE);
            } else if (myRrole == -1) {
                lin_hand_and_photo.setVisibility(View.GONE);
                lin_top_buttons.setVisibility(View.INVISIBLE);
                rel_play_back.setVisibility(View.VISIBLE);
                lin_time.setVisibility(View.GONE);
            }
        }
        doLayout();
        wbFragment.setAddPagePermission(myRrole == 0 && isClassBegin);
    }

    private void doPlayVideo(String peerId) {

        if (isZoom || TextUtils.isEmpty(peerId)) {
            return;
        }

        teacherItem.rel_group.setVisibility(View.INVISIBLE);
        stu_in_sd.rel_group.setVisibility(View.INVISIBLE);
        stu_in_sd.lin_gift.setVisibility(View.INVISIBLE);

        getPlayingList();
        boolean hasAssistant = false;
        for (int i = 0; i < playingList.size(); i++) {
            final RoomUser user = playingList.get(i);
            if (user == null) {
                return;
            }
            if (roomType == 0) {
                //1对1
                if (user.role == 0) {//老师
                    doTeacherVideoPlay(user);
                } else if (user.role == 2) {//学生
                    stu_in_sd.rel_group.setVisibility(View.VISIBLE);
                    stu_in_sd.lin_gift.setVisibility(View.VISIBLE);
                    stu_in_sd.txt_name.setText(user.nickName);
                    if (user.disablevideo) {
                        stu_in_sd.img_cam.setImageResource(R.drawable.icon_video_disable);
                    } else {
                        stu_in_sd.img_cam.setImageResource(R.drawable.icon_video);
                        if (user.publishState == 0 || user.publishState == 1 || user.publishState == 4) {
                            stu_in_sd.img_cam.setVisibility(View.INVISIBLE);
                        } else {
                            stu_in_sd.img_cam.setVisibility(View.VISIBLE);
                        }
                    }

                    if (user.disableaudio) {
                        stu_in_sd.img_mic.setImageResource(R.drawable.icon_audio_disable);
                    } else {
                        stu_in_sd.img_mic.setImageResource(R.drawable.icon_audio);
                        if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
                            stu_in_sd.img_mic.setVisibility(View.INVISIBLE);
                        } else {
                            stu_in_sd.img_mic.setVisibility(View.VISIBLE);
                        }
                    }
                    if (user.publishState > 1 && user.publishState < 4 && !user.disablevideo) {
                        stu_in_sd.sf_video.setVisibility(View.VISIBLE);
                        RoomManager.getInstance().playVideo(user.peerId, stu_in_sd.sf_video);
                        Log.e("xiao", "playSEC+playlistSize = " + playingList.size() + "peerid = " + user.peerId);
                    } else {
                        stu_in_sd.sf_video.setVisibility(View.INVISIBLE);
                    }
                } else if (user.role == 1) {//助教一对一显示
                    hasAssistant = true;
                    do1vsnStudentPlayVideo(user, peerId != null);
                }
            } else {//1对多
                if (user.role == 0) {
                    doTeacherVideoPlay(user);
                } else {
                    do1vsnStudentPlayVideo(user, peerId != null);
                }
            }


        }
        if (RoomManager.getInstance().getRoomType() == 0) {
            rel_students.setVisibility(hasAssistant ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void do1vsnStudentPlayVideo(RoomUser user, boolean force) {
        boolean hasSit = false;
        int sitpos = -1;
        for (int i = 0; i < videoItems.size(); i++) {
            if (videoItems.get(i).peerid.equals(user.peerId)) {
                hasSit = true;
                sitpos = i;
            }
        }
        if (!hasSit) {
            final VideoItem stu = new VideoItem();
            final LinearLayout vdi_stu = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.video_item, null);
            stu.parent = vdi_stu;
            stu.sf_video = (SurfaceViewRenderer) vdi_stu.findViewById(R.id.sf_video);
            stu.sf_video.init(EglBase.create().getEglBaseContext(), null);
            stu.sf_video.setZOrderMediaOverlay(true);
            stu.img_cam = (ImageView) vdi_stu.findViewById(R.id.img_cam);
            stu.img_mic = (ImageView) vdi_stu.findViewById(R.id.img_mic);
            stu.img_pen = (ImageView) vdi_stu.findViewById(R.id.img_pen);
            stu.img_hand = (ImageView) vdi_stu.findViewById(R.id.img_hand_up);
            stu.txt_name = (TextView) vdi_stu.findViewById(R.id.txt_name);
            stu.txt_gift_num = (TextView) vdi_stu.findViewById(R.id.txt_gift_num);
            stu.rel_group = (RelativeLayout) vdi_stu.findViewById(R.id.rel_group);
            stu.img_video_back = (ImageView) vdi_stu.findViewById(R.id.img_video_back);
            stu.lin_gift = (LinearLayout) vdi_stu.findViewById(R.id.lin_gift);
            stu.lin_name_label = (LinearLayout) vdi_stu.findViewById(R.id.lin_name_label);
            stu.rel_video_label = (RelativeLayout) vdi_stu.findViewById(R.id.rel_video_label);
            stu.peerid = user.peerId;
            stu.txt_name.setText(user.nickName);

            if (!isClassBegin) {
                stu.img_pen.setVisibility(View.INVISIBLE);
                stu.img_hand.setVisibility(View.INVISIBLE);
                stu.img_mic.setVisibility(View.INVISIBLE);
            }

            if (user.peerId.equals(RoomManager.getInstance().getMySelf().peerId)) {
                if (isClassBegin) {
                    stu.img_hand.setVisibility(View.VISIBLE);
                }
            } else {
                stu.img_hand.setVisibility(View.GONE);
            }
            if (user.disablevideo) {
                stu.img_cam.setImageResource(R.drawable.icon_video_disable);
            } else {
                stu.img_cam.setImageResource(R.drawable.icon_video);
                if (user.publishState == 0 || user.publishState == 1 || user.publishState == 4) {
                    stu.img_cam.setVisibility(View.INVISIBLE);
                } else {
                    if (isClassBegin) {
                        stu.img_cam.setVisibility(View.VISIBLE);
                    }
                }
            }

            if (user.disableaudio) {
                stu.img_mic.setImageResource(R.drawable.icon_audio_disable);
            } else {
                stu.img_mic.setImageResource(R.drawable.icon_audio);
                if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
                    stu.img_mic.setVisibility(View.INVISIBLE);
                } else {
                    if (isClassBegin) {
                        stu.img_mic.setVisibility(View.VISIBLE);
                    }
                }
            }

            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            final int wid = dm.widthPixels;
            final int hid = dm.heightPixels - getStatusBarHeight();

            vdi_stu.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (userrole == 4) {
                        return false;
                    }
                    stu.canMove = true;
                    vdi_stu.setAlpha(0.5f);
                    return false;
                }
            });


            vdi_stu.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (userrole == 4) {
                        return false;
                    }
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            if (!stu.isMoved) {
                                stu.oldX = event.getRawX();
                                stu.oldY = event.getRawY() - rel_tool_bar.getHeight() - lin_time.getHeight();
                            }

                            if (!stu.canMove) {
                                return false;
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (!stu.canMove) {
                                return false;
                            }
                            if (Math.abs(stu.oldX - event.getRawX()) < 20 && Math.abs(stu.oldY - event.getRawY()) < 20) {
                                return false;
                            }
                            if (stu.isMoved && stu.isSefMoved) {
                                if (vdi_stu.getLeft() - (stu.oldX - event.getRawX()) - vdi_stu.getWidth() / 2 >= 0 && vdi_stu.getRight() - (stu.oldX - event.getRawX()) <= rel_students.getRight() + vdi_stu.getWidth() / 2) {
                                    vdi_stu.setTranslationX(-(stu.oldX - event.getRawX()) - vdi_stu.getWidth() / 2);
                                }
                                float rawY = event.getRawY() - rel_tool_bar.getHeight() - lin_time.getHeight();
                                if (vdi_stu.getTop() - (stu.oldY - rawY) - vdi_stu.getHeight() / 2 >= 0 && vdi_stu.getBottom() - (stu.oldY - rawY) <= rel_students.getBottom() + vdi_stu.getHeight() / 2) {
                                    vdi_stu.setTranslationY(-(stu.oldY - rawY) - vdi_stu.getHeight() / 2);
                                }
                            } else {
                                if (vdi_stu.getLeft() - (stu.oldX - event.getRawX()) >= 0 && vdi_stu.getRight() - (stu.oldX - event.getRawX()) <= rel_students.getRight()) {
                                    vdi_stu.setTranslationX(-(stu.oldX - event.getRawX()));
                                }
                                float rawY = event.getRawY() - rel_tool_bar.getHeight() - lin_time.getHeight();
                                if (vdi_stu.getTop() - (stu.oldY - rawY) >= 0 && vdi_stu.getBottom() - (stu.oldY - rawY) <= rel_students.getBottom()) {
                                    vdi_stu.setTranslationY(-(stu.oldY - rawY));
                                }
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            vdi_stu.setAlpha(1f);
                            if (!stu.canMove) {
                                return false;
                            }
                            stu.canMove = false;
                            int[] loca = new int[2];
                            stu.parent.getLocationInWindow(loca);
                            stu.newX = loca[0];
                            stu.newY = loca[1];
                            if (stu.newY < lin_audio_control.getTop()) {
                                stu.isMoved = true;
                            } else {
                                stu.isMoved = false;
                                stu.isSefMoved = false;
                            }
                            do1vsnStudentVideoLayout();
                            if (userrole == 0) {
                                sendStudentMove();
                            }
                            break;


                    }
                    return true;
                }
            });
            if (user.role == 2) {
                stu.lin_gift.setVisibility(View.VISIBLE);
            } else {
                stu.lin_gift.setVisibility(View.INVISIBLE);
            }
            videoItems.add(stu);
            rel_students.addView(vdi_stu);
            do1vsnStudentVideoLayout();
            if (user.publishState > 1 && user.publishState < 4 && !MediaListAdapter.isPublish || (stream != null && !stream.isVideo())) {
                stu.sf_video.setVisibility(View.VISIBLE);
                RoomManager.getInstance().playVideo(user.peerId, stu.sf_video);
                Log.e("xiao", "playvideo-------peerid=" + user.peerId);
            } else {
                stu.sf_video.setVisibility(View.INVISIBLE);
            }
        } else if (force) {
            if (sitpos != -1) {
                if (user.publishState > 1 && user.publishState < 4 && !MediaListAdapter.isPublish || (stream != null && !stream.isVideo())) {
                    videoItems.get(sitpos).sf_video.setVisibility(View.VISIBLE);
                    videoItems.get(sitpos).img_cam.setVisibility(View.VISIBLE);
                    videoItems.get(sitpos).sf_video.setVisibility(View.VISIBLE);
                    RoomManager.getInstance().playVideo(user.peerId, videoItems.get(sitpos).sf_video);
                } else {
                    videoItems.get(sitpos).sf_video.setVisibility(View.INVISIBLE);
                    videoItems.get(sitpos).img_cam.setVisibility(View.INVISIBLE);
                }
            }
        }
        for (int i = 0; i < videoItems.size(); i++) {
            final VideoItem it = videoItems.get(i);
            it.parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (!it.isMoved) {
                        if (stuMoveInfoMap.containsKey(it.peerid) && stuMoveInfoMap.get(it.peerid) != null) {
                            String peerId = RoomManager.getInstance().getMySelf().peerId;
                            MoveVideoInfo mi = stuMoveInfoMap.get(it.peerid);
                            moveStudent(it.peerid, mi.top, mi.left, mi.isDrag);
                            stuMoveInfoMap.remove(it.peerid);
                        }
                        it.parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }

    }


    private boolean studentIsFullScreen = false;

    private void setStudentFullScreen(VideoItem stu) {

        RelativeLayout.LayoutParams stu_param_menu = (RelativeLayout.LayoutParams) stu.sf_video.getLayoutParams();
        stu_param_menu.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        stu_param_menu.height = RelativeLayout.LayoutParams.MATCH_PARENT;


        RelativeLayout.LayoutParams stu_parent_menu = (RelativeLayout.LayoutParams) stu.rel_group.getLayoutParams();
        stu_parent_menu.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        stu_parent_menu.height = RelativeLayout.LayoutParams.MATCH_PARENT;

        LinearLayout.LayoutParams stu_video_label_menu = (LinearLayout.LayoutParams) stu.rel_video_label.getLayoutParams();
        stu_video_label_menu.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        stu_video_label_menu.height = RelativeLayout.LayoutParams.MATCH_PARENT;


        ViewGroup.LayoutParams stu_par_menu = stu.parent.getLayoutParams();
        stu_par_menu.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        stu_par_menu.height = RelativeLayout.LayoutParams.MATCH_PARENT;

        ViewGroup.LayoutParams params = rel_students.getLayoutParams();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        rel_students.setLayoutParams(params);

        ViewGroup.LayoutParams params1 = lin_main.getLayoutParams();
        params1.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        lin_main.setLayoutParams(params1);


        LinearLayout.LayoutParams w_param = (LinearLayout.LayoutParams) rel_w.getLayoutParams();
        w_param.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        w_param.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        rel_w.setLayoutParams(w_param);


        rel_wb_container.setVisibility(View.GONE);
        stu.parent.setLayoutParams(stu_par_menu);
        stu.sf_video.setLayoutParams(stu_param_menu);
        stu.rel_group.setLayoutParams(stu_parent_menu);
        stu.rel_video_label.setLayoutParams(stu_video_label_menu);
        lin_menu.setVisibility(View.GONE);


    }

    private long[] mHits = new long[2];

    private boolean doubleClick_2() {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();//获取手机开机时间
        if (mHits[mHits.length - 1] - mHits[0] < 1000) {
            /**双击的业务逻辑*/
            return true;
        }
        return false;
    }


    private void sendStudentMove() {
        try {
            JSONObject data = new JSONObject();
            JSONObject moveData = new JSONObject();
            for (int i = 0; i < videoItems.size(); i++) {
                VideoItem it = videoItems.get(i);
                JSONObject md = new JSONObject();
                if (it.isMoved) {
                    double wid = rel_students.getWidth() - it.parent.getWidth();
                    double hid = rel_students.getHeight() - v_students.getHeight() - it.parent.getHeight();
                    int[] loca = new int[2];
                    it.parent.getLocationOnScreen(loca);
                    double top = (loca[1] - rel_students.getTop() - rel_tool_bar.getHeight()) / hid;
                    double left = loca[0] / wid;
                    md.put("top", top);
                    md.put("left", left);
                    md.put("isDrag", true);
                } else {
                    md.put("top", 0);
                    md.put("left", 0);
                    md.put("isDrag", false);
                }
                moveData.put(it.peerid, md);
            }
            data.put("otherVideoStyle", moveData);


            RoomManager.getInstance().pubMsg("videoDraghandle", "videoDraghandle", "__allExceptSender", data.toString(), true, null, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getY(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void moveStudent(String peerid, float top, float left, boolean isDrag) {


        for (int i = 0; i < videoItems.size(); i++) {
            VideoItem it = videoItems.get(i);
            if (videoItems.get(i).peerid.equals(peerid)) {
                if (isDrag) {
                    int wid = rel_students.getWidth() - it.parent.getWidth();
                    int hid = rel_students.getHeight() - it.parent.getHeight() - rel_tool_bar.getHeight() - lin_time.getHeight();
                    top = top * hid;
                    left = left * wid;
                    int[] loca = new int[2];
                    if (!it.isMoved) {
                        it.parent.getLocationInWindow(loca);
                        it.oldX = loca[0];
                        it.oldY = loca[1] - rel_tool_bar.getHeight() - lin_time.getHeight();
                    }
                    it.isMoved = isDrag;
                    it.isSefMoved = true;

                    it.parent.setTranslationX(-(it.oldX - left));
                    it.parent.setTranslationY(-(it.oldY - top));
                    it.isMoved = true;
                } else {
                    it.isMoved = false;
                }
            }
        }
        do1vsnStudentVideoLayout();
    }

    private void doUnPlayVideo(RoomUser user) {

        if (roomType == 0) {
            //1对1
            if (user.role == 0) {//老师
                doTeacherVideoUnPlay(user);
            } else if (user.role == 1) {
                do1vsnStudentUnPlayVideo(user);
            } else if (user.role == 2) {//学生
                stu_in_sd.rel_group.setVisibility(View.INVISIBLE);
            }
        } else {//1对多
            if (user.role == 0) {
                doTeacherVideoUnPlay(user);
            } else {
                do1vsnStudentUnPlayVideo(user);
            }
        }
    }

    private void do1vsnStudentUnPlayVideo(RoomUser user) {
        for (int i = 0; i < videoItems.size(); i++) {
            if (videoItems.get(i).peerid.equals(user.peerId)) {
                videoItems.get(i).sf_video.release();
                rel_students.removeView(videoItems.get(i).parent);
                videoItems.remove(i);
                do1vsnStudentVideoLayout();
            }
        }
    }

    private void doTeacherVideoPlay(final RoomUser user) {
        teacherItem.rel_group.setVisibility(View.VISIBLE);
        if (user.publishState > 1 && user.publishState < 4 && !user.disablevideo) {
            teacherItem.sf_video.setVisibility(View.VISIBLE);
            RoomManager.getInstance().playVideo(user.peerId, teacherItem.sf_video);
        } else {
            teacherItem.sf_video.setVisibility(View.INVISIBLE);
        }

        teacherItem.txt_name.setText(user.nickName);
        vdi_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClassBegin) {
                    showTeacherControlPop(user);
                }
            }
        });
    }

    private void doTeacherVideoUnPlay(final RoomUser user) {
        if (teacherItem.rel_group.getVisibility() == View.VISIBLE) {
            //            if (RoomManager.getInstance().getUser(user.peerId) != null) {
            //                RoomManager.getInstance().unPlayVideo(user.peerId);
            //            }
            teacherItem.sf_video.setVisibility(View.INVISIBLE);
        }
    }

    private void showStudentControlPop(final RoomUser user, final int index) {
        if (!(myRrole == 0) && !user.peerId.endsWith(RoomManager.getInstance().getMySelf().peerId)) {
            return;
        }
        if (user.peerId.endsWith(RoomManager.getInstance().getMySelf().peerId) && !RoomControler.isAllowStudentControlAV()) {
            return;
        }
        if (roomType != 0 && index >= videoItems.size()) {
            return;
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final int wid = dm.widthPixels;
        final int hid = dm.heightPixels;
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pop_student_control, null);
        ImageView img_down_arr = (ImageView) contentView.findViewById(R.id.down_arr);
        ImageView img_right_arr = (ImageView) contentView.findViewById(R.id.right_arr);
        LinearLayout lin_candraw = (LinearLayout) contentView.findViewById(R.id.lin_candraw);
        LinearLayout lin_up_sd = (LinearLayout) contentView.findViewById(R.id.lin_up_sd);
        LinearLayout lin_audio = (LinearLayout) contentView.findViewById(R.id.lin_audio);
        LinearLayout lin_gift = (LinearLayout) contentView.findViewById(R.id.lin_gift);
        LinearLayout lin_scall = (LinearLayout) contentView.findViewById(R.id.lin_scall);
        final ImageView img_candraw = (ImageView) contentView.findViewById(R.id.img_candraw);
        final ImageView img_up_sd = (ImageView) contentView.findViewById(R.id.img_up_sd);
        final ImageView img_audio = (ImageView) contentView.findViewById(R.id.img_audio);
        final ImageView img_scall = (ImageView) contentView.findViewById(R.id.image_scall);

        final TextView txt_candraw = (TextView) contentView.findViewById(R.id.txt_candraw);
        final TextView txt_up_sd = (TextView) contentView.findViewById(R.id.txt_up_sd);
        final TextView txt_audio = (TextView) contentView.findViewById(R.id.txt_audio);
        TextView txt_gift = (TextView) contentView.findViewById(R.id.txt_gift);
        final TextView text_scall = (TextView) contentView.findViewById(R.id.text_scall);
        LinearLayout lin_video_control = (LinearLayout) contentView.findViewById(R.id.lin_video_control);
        final ImageView img_video_control = (ImageView) contentView.findViewById(R.id.img_camera);
        final TextView txt_video = (TextView) contentView.findViewById(R.id.txt_camera);
        if (user.role == 1) {
            lin_candraw.setVisibility(View.GONE);
            lin_gift.setVisibility(View.GONE);

        } else {
            if (user.peerId.equals(RoomManager.getInstance().getMySelf().peerId)) {
                lin_candraw.setVisibility(View.GONE);
                lin_gift.setVisibility(View.GONE);
                lin_up_sd.setVisibility(View.GONE);
            } else {
                lin_candraw.setVisibility(View.VISIBLE);
                lin_gift.setVisibility(View.VISIBLE);
                lin_up_sd.setVisibility(View.VISIBLE);
            }
        }
        if (userrole == 0) {
            //视频永久不显示2017-10-11

            lin_video_control.setVisibility(View.GONE);
        }

        if (user.publishState == 0 || user.publishState == 1 || user.publishState == 4) {
            img_video_control.setImageResource(R.drawable.icon_control_camera_01);
            txt_video.setText(R.string.video_on);
        } else {
            img_video_control.setImageResource(R.drawable.icon_control_camera_02);
            txt_video.setText(R.string.video_off);
        }
        //        }
        if (user.disableaudio) {
            lin_audio.setVisibility(View.GONE);
        } else {
            lin_audio.setVisibility(View.VISIBLE);
            if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
                img_audio.setImageResource(R.drawable.icon_control_audio);
                txt_audio.setText(R.string.open_audio);
            } else {
                img_audio.setImageResource(R.drawable.icon_control_mute);
                txt_audio.setText(R.string.close_audio);
            }
        }

        if (user.properties.containsKey("candraw")) {
            boolean candraw = Tools.isTure(user.properties.get("candraw"));
            if (candraw) {
                //可以画图
                img_candraw.setImageResource(R.drawable.icon_control_tools_02);
                txt_candraw.setText(R.string.no_candraw);
            } else {
                //不可以画图
                img_candraw.setImageResource(R.drawable.icon_control_tools_01);
                txt_candraw.setText(R.string.candraw);

            }
        } else {
            //没给过画图权限
            img_candraw.setImageResource(R.drawable.icon_control_tools_01);
            txt_candraw.setText(R.string.candraw);
        }

        if (user.publishState > 0) {//只要视频开启就是上台
            img_up_sd.setImageResource(R.drawable.icon_control_down);
            txt_up_sd.setText(R.string.down_std);
        } else {
            img_up_sd.setImageResource(R.drawable.icon_control_up);
            txt_up_sd.setText(R.string.up_std);
        }
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setContentView(contentView);
        } else if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }

        lin_scall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!studentIsFullScreen) {
                    setStudentFullScreen(videoItems.get(index));
                    lin_menu.setVisibility(View.GONE);
                    rel_wb_container.setVisibility(View.GONE);
                    studentIsFullScreen = true;
                    img_scall.setImageDrawable(getResources().getDrawable(R.drawable.nemediacontroller_scale02));
                    text_scall.setText("缩小");
                } else {
                    doLayout();
                    do1vsnStudentVideoLayout();
                    lin_menu.setVisibility(View.VISIBLE);
                    rel_wb_container.setVisibility(View.VISIBLE);
                    studentIsFullScreen = false;
                    img_scall.setImageDrawable(getResources().getDrawable(R.drawable.nemediacontroller_scale01));
                    text_scall.setText("放大");
                }
                mPopupWindow.dismiss();
            }
        });
        lin_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
                    img_audio.setImageResource(R.drawable.icon_control_mute);
                    txt_audio.setText(R.string.open_audio);
                    RoomManager.getInstance().changeUserPublish(user.peerId, user.publishState == 0 || user.publishState == 4 ? 1 : 3);
                    RoomManager.getInstance().changeUserProperty(user.peerId, "__all", "raisehand", false);
                } else {
                    img_audio.setImageResource(R.drawable.icon_control_audio);
                    txt_audio.setText(R.string.close_audio);
                    RoomManager.getInstance().changeUserPublish(user.peerId, user.publishState == 3 ? 2 : 4);
                }
                mPopupWindow.dismiss();
            }
        });

        lin_candraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.properties.containsKey("candraw")) {
                    boolean candraw = Tools.isTure(user.properties.get("candraw"));
                    if (candraw) {
                        //不可以画图
                        img_candraw.setImageResource(R.drawable.icon_control_tools_01);
                        txt_candraw.setText(R.string.candraw);
                        RoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", false);

                    } else {
                        //可以画图
                        img_candraw.setImageResource(R.drawable.icon_control_tools_02);
                        txt_candraw.setText(R.string.no_candraw);
                        RoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", true);
                    }
                } else {
                    //可以画图
                    img_candraw.setImageResource(R.drawable.icon_control_tools_02);
                    txt_candraw.setText(R.string.no_candraw);
                    RoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", true);
                }
                mPopupWindow.dismiss();
            }
        });

        lin_gift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                long giftnumber = 0;
                //                if (user.properties.containsKey("giftnumber")) {
                //                    giftnumber = user.properties.get("giftnumber") instanceof Integer ? (int) user.properties.get("giftnumber") : (long) user.properties.get("giftnumber");
                //                }
                //                giftnumber++;
                //                RoomManager.getInstance().changeUserProperty(user.peerId, "__all", "giftnumber", giftnumber);
                HashMap<String, RoomUser> receiverMap = new HashMap<String, RoomUser>();
                receiverMap.put(user.peerId, user);
                sendGift(receiverMap);
                mPopupWindow.dismiss();
            }
        });

        lin_up_sd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.publishState > 0) {//只要视频开启就是上台
                    img_up_sd.setImageResource(R.drawable.icon_control_up);
                    txt_up_sd.setText(R.string.up_std);
                    RoomManager.getInstance().changeUserPublish(user.peerId, 0);
                    if (user.role != 1) {
                        RoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", false);
                    }

                } else {
                    img_up_sd.setImageResource(R.drawable.icon_control_down);
                    txt_up_sd.setText(R.string.down_std);
                    RoomManager.getInstance().changeUserPublish(user.peerId, 3);
                    RoomManager.getInstance().changeUserProperty(user.peerId, "__all", "raisehand", false);
                }
                mPopupWindow.dismiss();
            }
        });
        lin_video_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.publishState == 0 || user.publishState == 1 || user.publishState == 4) {
                    img_video_control.setImageResource(R.drawable.icon_control_camera_02);
                    txt_video.setText(R.string.video_off);
                    RoomManager.getInstance().changeUserPublish(user.peerId, user.publishState == 0 || user.publishState == 4 ? 2 : 3);
                } else {
                    img_video_control.setImageResource(R.drawable.icon_control_camera_01);
                    txt_video.setText(R.string.video_on);
                    RoomManager.getInstance().changeUserPublish(user.peerId, user.publishState == 2 ? 4 : 1);
                }
                mPopupWindow.dismiss();
            }
        });


        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);

        if (roomType == 0) {

            if (user.role == 1) {
                img_down_arr.setVisibility(View.VISIBLE);
                img_right_arr.setVisibility(View.GONE);
                RelativeLayout.LayoutParams arr_param = (RelativeLayout.LayoutParams) img_down_arr.getLayoutParams();
                arr_param.setMargins((videoItems.get(index).parent.getMeasuredWidth() / 2), 0, 0, 0);
                img_down_arr.setLayoutParams(arr_param);
                mPopupWindow.showAsDropDown(videoItems.get(index).parent, -videoItems.get(index).parent.getMeasuredWidth() / 2 - 20, -videoItems.get(index).parent.getHeight() - videoItems.get(index).parent.getHeight() / 2);
            } else {
                img_down_arr.setVisibility(View.GONE);
                img_right_arr.setVisibility(View.VISIBLE);
                int x = 0;
                if (userrole == 0) {
                    x = -vdi_stu_in_sd.getMeasuredWidth();
                } else {
                    x = -vdi_stu_in_sd.getMeasuredWidth() / 4 * 3;
                }
                mPopupWindow.showAsDropDown(vdi_stu_in_sd, x, -vdi_stu_in_sd.getMeasuredHeight() / 2 - vdi_stu_in_sd.getMeasuredHeight() / 4);
            }
        } else {
            img_down_arr.setVisibility(View.VISIBLE);
            img_right_arr.setVisibility(View.GONE);
            RelativeLayout.LayoutParams arr_param = (RelativeLayout.LayoutParams) img_down_arr.getLayoutParams();
            if (index == 0) {
                arr_param.setMargins((videoItems.get(index).parent.getMeasuredWidth() / 2), 0, 0, 0);
            } else if (index > 0 && index < 6) {
                arr_param.setMargins(videoItems.get(index).parent.getMeasuredWidth(), 0, 0, 0);
            }
            img_down_arr.setLayoutParams(arr_param);
            mPopupWindow.showAsDropDown(videoItems.get(index).parent, -videoItems.get(index).parent.getMeasuredWidth() / 2 - 20,
                    -videoItems.get(index).parent.getHeight());
        }
    }

    private void showTeacherControlPop(final RoomUser user) {
        if (!(myRrole == 0)) {
            return;
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final int wid = dm.widthPixels;
        final int hid = dm.heightPixels;
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pop_av_control, null);
        //        contentView.setBackgroundColor(Color.BLUE);
        LinearLayout lin_video_control = (LinearLayout) contentView.findViewById(R.id.lin_video_control);
        LinearLayout lin_audio_control = (LinearLayout) contentView.findViewById(R.id.lin_audio_control);
        LinearLayout lin_scall = (LinearLayout) contentView.findViewById(R.id.lin_scall);

        final ImageView img_video_control = (ImageView) contentView.findViewById(R.id.img_camera);
        final ImageView img_audio_control = (ImageView) contentView.findViewById(R.id.img_audio);
        final ImageView img_scall = (ImageView) contentView.findViewById(R.id.img_scall);

        final TextView txt_video = (TextView) contentView.findViewById(R.id.txt_camera);
        final TextView txt_audio = (TextView) contentView.findViewById(R.id.txt_audio);
        final TextView txt_scall = (TextView) contentView.findViewById(R.id.txt_scall);

        if (mWindow==null){
            mWindow = new PopupWindow(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mWindow.setContentView(contentView);
        }
        if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
            img_audio_control.setImageResource(R.drawable.icon_control_audio);
            txt_audio.setText(R.string.open_audio);
        } else {
            img_audio_control.setImageResource(R.drawable.icon_control_mute);
            txt_audio.setText(R.string.close_audio);
        }
        if (user.publishState == 0 || user.publishState == 1 || user.publishState == 4) {
            img_video_control.setImageResource(R.drawable.icon_control_camera_01);
            txt_video.setText(R.string.video_on);
        } else {
            img_video_control.setImageResource(R.drawable.icon_control_camera_02);
            txt_video.setText(R.string.video_off);
        }


        lin_scall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFullScreen) {
                    setTeacherFullScreen();
                    rel_wb_container.setVisibility(View.GONE);
                    isFullScreen = true;
                    img_scall.setImageDrawable(getResources().getDrawable(R.drawable.nemediacontroller_scale02));
                    txt_scall.setText("缩小");
                } else {
                    doLayout();
                    rel_wb_container.setVisibility(View.VISIBLE);
                    isFullScreen = false;
                    lin_main.setVisibility(View.VISIBLE);
                    img_scall.setImageDrawable(getResources().getDrawable(R.drawable.nemediacontroller_scale01));
                    txt_scall.setText("放大");
                }
                mWindow.dismiss();
            }
        });
        lin_video_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.publishState == 0 || user.publishState == 1 || user.publishState == 4) {
                    img_video_control.setImageResource(R.drawable.icon_control_camera_02);
                    txt_video.setText(R.string.video_off);
                    RoomManager.getInstance().changeUserPublish(user.peerId, user.publishState == 0 || user.publishState == 4 ? 2 : 3);
                } else {
                    img_video_control.setImageResource(R.drawable.icon_control_camera_01);
                    txt_video.setText(R.string.video_on);
                    RoomManager.getInstance().changeUserPublish(user.peerId, user.publishState == 2 ? 4 : 1);
                }
                mWindow.dismiss();
            }
        });
        lin_audio_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
                    img_audio_control.setImageResource(R.drawable.icon_control_mute);
                    txt_audio.setText(R.string.close_audio);
                    RoomManager.getInstance().changeUserPublish(user.peerId, user.publishState == 0 || user.publishState == 4 ? 1 : 3);
                } else {
                    img_audio_control.setImageResource(R.drawable.icon_control_audio);
                    txt_audio.setText(R.string.open_audio);
                    RoomManager.getInstance().changeUserPublish(user.peerId, user.publishState == 3 ? 2 : 4);
                }
                mWindow.dismiss();
            }
        });
        mWindow.setBackgroundDrawable(new BitmapDrawable());
        mWindow.setFocusable(true);
        mWindow.setOutsideTouchable(true);
        //        popupWindow.showAtLocation(findViewById(R.id.mainLayout), Gravity.RIGHT,0,0);
        mWindow.showAsDropDown(vdi_teacher, -vdi_teacher.getMeasuredWidth() / 2 - 20, -vdi_teacher.getMeasuredHeight() / 2 - vdi_teacher.getMeasuredHeight() / 4);
    }

    private void getPlayingList() {
        playingList.clear();

        for (String p : playingMap.keySet()) {
            //            if (playingMap.get(p)) {
            RoomUser u = RoomManager.getInstance().getUser(p);
            if (playingList.size() <= maxVideo && u != null) {
                playingList.add(u);
            }
            //            }
        }

    }

    private void getExData() {
        Bundle bundle = getIntent().getExtras();
        host = bundle.getString("host");
        port = bundle.getInt("port");
        nickname = bundle.getString("nickname");

        if (bundle.containsKey("param")) {
            param = bundle.getString("param");
        } else {
            serial = bundle.getString("serial");
            password = bundle.getString("password");
            userid = bundle.getString("userid");
            userrole = bundle.getInt("userrole");
        }
        domain = bundle.getString("domain");
        if (bundle.containsKey("path")) {
            path = bundle.getString("path");
        }
        if (bundle.containsKey("type")) {
            type = bundle.getInt("type");
        }
        try {
            String brand = android.os.Build.MODEL;
            mobilename = bundle.getString("mobilename");
            if (mobilename != null && !mobilename.isEmpty()) {
                JSONArray mNames = new JSONArray(mobilename);
                for (int i = 0; i < mNames.length(); i++) {
                    if (brand.toLowerCase().equals(mNames.optString(i).toLowerCase())) {
                        mobilenameNotOnList = false;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.lin_back_and_name || id == R.id.img_back_play_back) {
            showExitDialog();
        } else if (id == R.id.img_close) {
            try {
                long nowTime = System.currentTimeMillis();
                long endTime = RoomManager.getInstance().getRoomProperties().getLong("endtime") * 1000;
                long proTime = endTime - nowTime;
                if (proTime > 3 * 60 * 1000) {
                    return;
                }
                //                RoomManager.getInstance().delMsg("ClassBegin", "ClassBegin", "__all", new HashMap<String, Object>());
                sendClassDissToPhp();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.img_file_list) {
            showList(2);
        } else if (id == R.id.img_media_list) {
            showList(3);
        } else if (id == R.id.img_member_list) {
            showList(1);
        } else if (id == R.id.txt_class_begin) {
            if (isClassBegin) {
                try {
                    if (canClassDissMiss || !RoomManager.getInstance().getRoomProperties().getString("companyid").equals("10035")) {
                        showClassDissMissDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                lin_audio_control.setVisibility(View.INVISIBLE);

                try {
                    long expires = RoomManager.getInstance().getRoomProperties().getLong("endtime") + 5 * 60;
                    RoomManager.getInstance().pubMsg("ClassBegin", "ClassBegin", "__all", new HashMap<String, Object>(), true, expires);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendClassBeginToPhp();

            }
        } else if (id == R.id.txt_all_mute) {
            getMemberList();
            for (int i = 0; i < memberList.size(); i++) {
                RoomUser u = memberList.get(i);
                Log.e("xiao", u.role + "");
                if (u.role == 2) {
                    if (u.publishState == 3) {
                        RoomManager.getInstance().changeUserPublish(u.peerId, 2);
                    } else if (u.publishState == 1) {
                        RoomManager.getInstance().changeUserPublish(u.peerId, 4);
                    }
                }
            }
            //            txt_all_mute.setBackgroundResource(R.drawable.round_back_red_black);
            //            txt_all_mute.setClickable(false);
        } else if (id == R.id.txt_all_send_gift) {
            HashMap<String, RoomUser> receiverMap = new HashMap<String, RoomUser>();
            for (RoomUser u : RoomManager.getInstance().getUsers().values()) {
                if (u.role == 2) {
                    receiverMap.put(u.peerId, u);
                }
            }
            if (receiverMap.size() != 0) {
                sendGift(receiverMap);
            }
        } else if (id == R.id.txt_send_msg) {
            //            String msg = edt_chat_input.getText().toString().trim();
            //            if (msg != null && !msg.isEmpty()) {
            //                RoomManager.getInstance().sendMessage(msg);
            //            }
        } else if (id == R.id.txt_chat_input) {
            showChatEditPop();
        } else if (id == R.id.txt_video) {
            RoomUser user = RoomManager.getInstance().getMySelf();
            RoomManager.getInstance().disableLocalVideo(!user.disablevideo);

        } else if (id == R.id.txt_audio) {
            RoomUser user = RoomManager.getInstance().getMySelf();
            if (!user.disableaudio) {
                RoomManager.getInstance().changeUserProperty(RoomManager.getInstance().getMySelf().peerId, "__all", "raisehand", false);
            }
            RoomManager.getInstance().disableLocalAudio(!user.disableaudio);
        } else if (id == R.id.txt_send_up_photo) {
            showPhotoControlPop();
        }
    }

    private void showPhotoControlPop() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final int wid = dm.widthPixels;
        final int hid = dm.heightPixels;
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pop_photo_control, null);
        TextView txt_camera = (TextView) contentView.findViewById(R.id.txt_camera);
        TextView txt_selectphoto = (TextView) contentView.findViewById(R.id.txt_selectphoto);
        TextView txt_cacel = (TextView) contentView.findViewById(R.id.txt_cacel);
        popupWindowPhoto = new PopupWindow(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindowPhoto.setContentView(contentView);
        txt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPauseLocalVideo) {
                    RoomManager.getInstance().pauseLocalCamera();
                    isPauseLocalVideo = !isPauseLocalVideo;
                }
                isOpenCamera = true;
                //                openCamera();
                popupWindowPhoto.dismiss();
            }
        });
        txt_selectphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlbum();
                popupWindowPhoto.dismiss();
            }
        });
        txt_cacel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowPhoto.dismiss();
            }
        });
        popupWindowPhoto.setBackgroundDrawable(new BitmapDrawable());
        popupWindowPhoto.setFocusable(true);
        popupWindowPhoto.setOutsideTouchable(true);
        popupWindowPhoto.showAsDropDown(txt_send_up_photo, -txt_send_up_photo.getMeasuredWidth() + txt_send_up_photo.getMeasuredWidth() / 3, -txt_send_up_photo.getMeasuredHeight() * 2 - txt_send_up_photo.getMeasuredHeight() / 2);
    }

    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, ALBUM_IMAGE);

    }

    public static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    private static final int ALBUM_IMAGE = 2; //相册
    public static File tempFile;
    private Uri imageUri;

    public void openCamera() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            String filename = timeStampFormat.format(new Date());
            tempFile = new File(Environment.getExternalStorageDirectory(),
                    filename + ".jpg");
            if (currentapiVersion < 24) {
                // 从文件中创建uri
                imageUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            } else {
                //兼容android7.0 使用共享文件的形式
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());
                //检查是否有存储权限，以免崩溃
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    Log.e("mxl", "存储权限没有开启");
                    return;
                }
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri;
        switch (requestCode) {
            case PHOTO_REQUEST_CAREMA:
                if (isPauseLocalVideo) {
                    RoomManager.getInstance().resumeLocalCamera();
                    isPauseLocalVideo = !isPauseLocalVideo;
                }
                isOpenCamera = false;
                if (resultCode == RESULT_CANCELED) {
                    Log.e("mxl", "取消了");
                    return;
                }
                if (resultCode == RESULT_OK) {
                    Log.e("mxl", "拍照OK");
                    if (data != null) {
                        uri = data.getData();
                    } else {
                        uri = imageUri;
                    }
                    if (!TextUtils.isEmpty(uri.toString())) {
                        try {
                            String path = getRealFilePath(this, getFileUri(uri));
                            WhiteBoradManager.getInstance().uploadRoomFile(
                                    RoomManager.getInstance().getRoomProperties().getString("serial"), path);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case ALBUM_IMAGE:
                if (resultCode == RESULT_OK) {
                    String imagePath = null;
                    try {
                        if (Build.VERSION.SDK_INT >= 19) {
                            imagePath = getImageAfterKitKat(data);
                        } else {
                            imagePath = getImageBeforeKitKat(data);
                        }
                        if (!TextUtils.isEmpty(imagePath)) {
                            WhiteBoradManager.getInstance().uploadRoomFile(
                                    RoomManager.getInstance().getRoomProperties().getString("serial"), imagePath);
                        } else {
                            Toast.makeText(this, "图片选择失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public String getImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(this, uri, null);
        return imagePath;
    }

    @TargetApi(19)
    public String getImageAfterKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri,则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];  //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(this, contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果不是document类型的Uri,则使用普通方式处理
            imagePath = getImagePath(this, uri, null);
        } else {
            imagePath = getFileUri(uri).toString();
        }
        return imagePath;
    }

    public static String getImagePath(Context context, Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    private Uri getFileUri(Uri uri) {
        if (uri.getScheme().equals("file")) {
            String path = uri.getEncodedPath();
            Log.d("mxl", "path1 is " + path);
            if (path != null) {
                path = Uri.decode(path);
                Log.d("mxl", "path2 is " + path);
                ContentResolver cr = getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(")
                        .append(MediaStore.Images.ImageColumns.DATA)
                        .append("=")
                        .append("'" + path + "'")
                        .append(")");
                Cursor cur = cr.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.ImageColumns._ID},
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur
                        .moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    //do nothing
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/"
                                    + index);
                    Log.d("mxl", "uri_temp is " + uri_temp);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }

    @Override
    public void roomManagerRoomJoined() {
        netBreakCount = 0;
        //        registerIt();
        WhiteBoradManager.getInstance().setUserrole(RoomManager.getInstance().getMySelf().role);
        Tools.HideProgressDialog();
        netBreakCount = 0;
        txt_send_up_photo.setEnabled(false);
        txt_hand_up.setEnabled(false);
        for (RoomUser u : RoomManager.getInstance().getUsers().values()) {
            changeUserState(u);
        }
        try {
            maxVideo = RoomManager.getInstance().getRoomProperties().getInt("maxvideo") > 6 ? 6 : RoomManager.getInstance().getRoomProperties().getInt("maxvideo");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("emm", "RoomJoined");
        isInRoom = true;
        //        if (t != null) {
        //            t.cancel();
        //            t = null;
        //        }

        String peerid = RoomManager.getInstance().getMySelf().peerId;
        //        getGiftNum(RoomManager.getInstance().getRoomProperties().optString("serial"), peerid);
        Log.d("emm", "getGiftNum");

        closeSpeaker();
        //RoomManager.getInstance().useLoudSpeaker(true);
        myRrole = RoomManager.getInstance().getMySelf().role;
        if (myRrole == 0)
            RoomManager.getInstance().pubMsg("UpdateTime", "UpdateTime", "__all", null, false, null, null);
        roomType = RoomManager.getInstance().getRoomType();
        Log.e("xiao", "roomtype = " + roomType);
        String roomname = RoomManager.getInstance().getRoomName();
        roomname = StringEscapeUtils.unescapeHtml4(roomname);
        RoomManager.getInstance().getMySelf().nickName = StringEscapeUtils.unescapeHtml4(RoomManager.getInstance().getMySelf().nickName);
        txt_room_name.setText(roomname);
        initViewByRoomTypeAndTeacher();
        //        setDisableState();
        txt_hand_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RoomManager.getInstance().getMySelf().properties.containsKey("raisehand")) {
                    boolean israisehand = Tools.isTure(RoomManager.getInstance().getMySelf().properties.get("raisehand"));
                    if (israisehand) {
                        txt_hand_up.setText(R.string.raise); //同意了，或者拒绝了
                        RoomManager.getInstance().changeUserProperty(RoomManager.getInstance().getMySelf().peerId, "__all", "raisehand", false);
                    } else {
                        txt_hand_up.setText(R.string.no_raise);
                        RoomManager.getInstance().changeUserProperty(RoomManager.getInstance().getMySelf().peerId, "__all", "raisehand", true);
                    }
                } else {
                    txt_hand_up.setText(R.string.no_raise);
                    RoomManager.getInstance().changeUserProperty(RoomManager.getInstance().getMySelf().peerId, "__all", "raisehand", true);
                }
            }
        });

        txt_all_mute.setBackgroundResource(R.drawable.round_back_red_black);
        txt_all_mute.setClickable(false);

        wbFragment.setToolBarMode(RoomManager.getInstance().getMySelf().role);
        wbFragment.setTurnPagePermission(myRrole == 0 || (RoomManager.getInstance().getMySelf().role == 2 && RoomControler.isStudentCanTurnPage()));
        wbFragment.sendJoinRoomDataToWB();
        playSelfBeforeClassBegin();
        doLayout();

    }

    private void playSelfBeforeClassBegin() {
        RoomUser me = RoomManager.getInstance().getMySelf();
        me.publishState = 3;
        if (myRrole == 0) {
            doTeacherVideoPlay(me);
            if (stu_in_sd.rel_group != null && !isClassBegin) {
                stu_in_sd.rel_group.setVisibility(View.INVISIBLE);
            }
        } else if (myRrole == 2) {
            if (roomType == 0) {
                if (stu_in_sd.sf_video != null) {
                    if (!isClassBegin) {
                        teacherItem.rel_group.setVisibility(View.INVISIBLE);
                        stu_in_sd.rel_group.setVisibility(View.VISIBLE);
                        stu_in_sd.img_cam.setVisibility(View.INVISIBLE);
                        stu_in_sd.img_mic.setVisibility(View.INVISIBLE);
                        stu_in_sd.img_hand.setVisibility(View.INVISIBLE);
                        stu_in_sd.img_pen.setVisibility(View.INVISIBLE);
                    }
                    stu_in_sd.sf_video.setVisibility(View.VISIBLE);
                    RoomManager.getInstance().playVideo(me.peerId, stu_in_sd.sf_video);
                }
            } else {
                if (!isClassBegin) {
                    teacherItem.rel_group.setVisibility(View.INVISIBLE);
                }
                do1vsnStudentPlayVideo(me, me.peerId != null);
            }
        }
    }

    private void unPlaySelfAfterClassBegin() {
        RoomUser me = RoomManager.getInstance().getMySelf();
        me.publishState = 0;
        if (roomType == 0) {
            RoomManager.getInstance().unPlayVideo(RoomManager.getInstance().getMySelf().peerId);
            stu_in_sd.rel_group.setVisibility(View.INVISIBLE);
        } else if (myRrole == 2) {
            do1vsnStudentUnPlayVideo(me);
        }
    }

    @Override
    public void roomManagerRoomLeaved() {
        removeVideoFragment();
        romoveScreenFragment();
        wbFragment.clearLcAllData();
        playingMap.clear();
        playingList.clear();
        publishSet.clear();
        isClassBegin = false;
        myRrole = -1;
        Log.d("emm", "RoomLeaved");
        isInRoom = false;
        mediaListAdapter.setLocalfileid(-1);
        RoomManager.isMediaPublishing = false;
        isWBMediaPlay = false;
        MediaListAdapter.isPublish = false;
        MediaListAdapter.isPlay = false;

        if (RoomClient.getInstance().isExit()) {

            clear();
            finish();
        } else {
            Tools.ShowProgressDialog(this, getString(R.string.connected));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (wbFragment != null) {
                        wbFragment.setDrawable(false);
                        wbFragment.dispatchEvent("room-disconnected");
                    }
                }
            });
            if (popupWindowPhoto != null) {
                popupWindowPhoto.dismiss();
            }
            txt_send_up_photo.setBackgroundResource(R.drawable.round_back_red_black);
            txt_send_up_photo.setEnabled(false);
        }
        img_disk.clearAnimation();
        img_disk.setVisibility(View.INVISIBLE);
        lin_audio_control.setVisibility(View.INVISIBLE);
        //        unregisterIt();

    }

    @Override
    public void roomManagerDidFailWithError(int i) {
        Log.d("emm", "DidFailWithError");
        RoomClient.getInstance().joinRoomcallBack(i);
    }

    @Override
    public void roomManagerUserJoined(RoomUser user, boolean inList) {
        if (inList && user.role != 4) {
            if (user.role == 0 && RoomManager.getInstance().getMySelf().role == 0 ||
                    (RoomManager.getInstance().getRoomType() == 0 && user.role == RoomManager.getInstance().getMySelf().role)) {
                RoomManager.getInstance().evictUser(user.peerId);
            }
        }
        user.nickName = StringEscapeUtils.unescapeHtml4(user.nickName);
        changeUserState(user);
        ChatData ch = new ChatData();
        ch.setState(1);
        ch.setInOut(true);
        ch.setStystemMsg(true);
        ch.setUser(user);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        ch.setTime(str);
        if (user.role != 4) {
            chatList.add(ch);
        }
        chlistAdapter.notifyDataSetChanged();
        list_chat.setSelection(chlistAdapter.getCount());
        getMemberList();
        memberListAdapter.notifyDataSetChanged();

        if (user.properties.containsKey("isInBackGround") && RoomManager.getInstance().getMySelf().role != 2) {
            if (user == null) {
                return;
            }
            boolean isinback = Tools.isTure(user.properties.get("isInBackGround"));
            ChatData ch2 = new ChatData();
            ch.setState(2);
            ch.setHold(isinback);
            ch.setStystemMsg(true);
            ch.setUser(user);
            SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm");
            Date curDate2 = new Date(System.currentTimeMillis());//获取当前时间
            String str2 = formatter2.format(curDate2);
            ch.setTime(str2);
            if (user.role != 4) {
                chatList.add(ch2);
            }
            chlistAdapter.notifyDataSetChanged();
            list_chat.setSelection(chlistAdapter.getCount());
            getMemberList();
            memberListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void roomManagerUserLeft(RoomUser RoomUser) {
        stuMoveInfoMap.remove(RoomUser.peerId);
        ChatData ch = new ChatData();
        ch.setState(1);
        ch.setInOut(false);
        ch.setStystemMsg(true);
        ch.setUser(RoomUser);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        ch.setTime(str);
        if (RoomUser.role != 4) {
            chatList.add(ch);
        }
        chlistAdapter.notifyDataSetChanged();
        list_chat.setSelection(chlistAdapter.getCount());
        publishSet.remove(RoomUser.peerId);
        getMemberList();
        memberListAdapter.notifyDataSetChanged();
        doUnPlayVideo(RoomUser);
        changeUserState(RoomUser);
        if (roomType == 0 && RoomUser.role == 2) {
            stu_in_sd.txt_name.setText("");
            stu_in_sd.txt_gift_num.setText(0 + "");
        }
    }

    @Override
    public void roomManagerUserChanged(RoomUser RoomUser, Map<String, Object> map) {
        pandingSet.remove(RoomUser.peerId);
        if (playingMap.containsKey(RoomUser.peerId) && RoomUser.publishState > 0) {
            playingMap.put(RoomUser.peerId, RoomUser.publishState > 1 && RoomUser.publishState < 4);
            getMemberList();
            memberListAdapter.notifyDataSetChanged();
            if (map.containsKey("publishstate")) {
                if (RoomUser.publishState > 0) {
                    Log.e("xiao", "userchange");
                    doPlayVideo(RoomUser.peerId);
                } else {
                    doUnPlayVideo(RoomUser);
                }
            }
        }
        changeUserState(RoomUser);
        checkRaiseHands();
        if (isClassBegin) {

            if (RoomManager.getInstance().getMySelf().publishState == 2 || RoomManager.getInstance().getMySelf().publishState == 0 || RoomManager.getInstance().getMySelf().publishState == 4) {
                if (RoomManager.getInstance().getMySelf().disableaudio) {
                    txt_hand_up.setBackgroundResource(R.drawable.round_back_red_black);
                    txt_hand_up.setClickable(false);
                } else {
                    txt_hand_up.setBackgroundResource(R.drawable.round_back_red);
                    txt_hand_up.setClickable(true);
                }
            } else {
                txt_hand_up.setBackgroundResource(R.drawable.round_back_red_black);
                txt_hand_up.setClickable(false);
            }

            //            setDisableState();

            if (RoomManager.getInstance().getMySelf().properties.containsKey("raisehand")) {
                boolean israisehand = Tools.isTure(RoomManager.getInstance().getMySelf().properties.get("raisehand"));
                if (israisehand) {
                    txt_hand_up.setText(R.string.no_raise);
                    txt_hand_up.setEnabled(true);
                } else {
                    txt_hand_up.setText(R.string.raise); //同意了，或者拒绝了
                    txt_hand_up.setEnabled(true);
                }
            } else {
                txt_hand_up.setText(R.string.raise); //还没举手
                txt_hand_up.setEnabled(true);
            }

            if (map.containsKey("isInBackGround") && RoomManager.getInstance().getMySelf().role != 2) {
                if (RoomUser == null) {
                    return;
                }
                boolean isinback = Tools.isTure(map.get("isInBackGround"));
                ChatData ch = new ChatData();
                ch.setState(2);
                ch.setHold(isinback);
                ch.setStystemMsg(true);
                ch.setUser(RoomUser);
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                ch.setTime(str);
                if (RoomUser.role != 4) {
                    chatList.add(ch);
                }
                chlistAdapter.notifyDataSetChanged();
                list_chat.setSelection(chlistAdapter.getCount());
                getMemberList();
                memberListAdapter.notifyDataSetChanged();
            }

            if (map.containsKey("giftnumber")) {
                SoundPlayUtils.play(1);
                if (roomType == 0) {
                    showGiftAim(stu_in_sd);
                } else if (RoomUser.role == 2) {
                    for (int i = 0; i < videoItems.size(); i++) {
                        if (RoomUser.peerId.equals(videoItems.get(i).peerid)) {
                            showGiftAim(videoItems.get(i));
                            break;
                        }
                    }
                }
            }
            if (RoomUser.peerId.equals(RoomManager.getInstance().getMySelf().peerId) && map.containsKey("candraw") && !(myRrole == 0)) {
                boolean candraw = Tools.isTure(map.get("candraw"));
                wbFragment.setDrawable(candraw);
                if (candraw) {
                    txt_send_up_photo.setBackgroundResource(R.drawable.round_back_red);
                    txt_send_up_photo.setEnabled(true);
                    txt_hand_up.setClickable(true);
                    wbFragment.setTurnPagePermission(true);
                } else {
                    if (popupWindowPhoto != null) {
                        popupWindowPhoto.dismiss();
                    }
                    txt_send_up_photo.setBackgroundResource(R.drawable.round_back_red_black);
                    txt_send_up_photo.setEnabled(false);
                    txt_hand_up.setClickable(false);
                    wbFragment.setTurnPagePermission(myRrole == 0 || (RoomManager.getInstance().getMySelf().role == 2 && RoomControler.isStudentCanTurnPage()));
                }
            }
        }
        checkMute();
        memberListAdapter.notifyDataSetChanged();
    }


    private void checkMute() {
        boolean isMute = true;
        for (RoomUser u : RoomManager.getInstance().getUsers().values()) {
            if ((u.publishState == 1 || u.publishState == 3) && u.role == 2) {
                isMute = false;
            }
        }
        if (isMute) {
            txt_all_mute.setBackgroundResource(R.drawable.round_back_red_black);
            txt_all_mute.setClickable(false);
        } else {
            txt_all_mute.setBackgroundResource(R.drawable.round_back_red);
            txt_all_mute.setClickable(true);
        }
    }

    @Override
    public void roomManagerUserPublished(RoomUser RoomUser) {
        if (RoomUser.publishState > 0) {
            //            if (roomType == 0) {
            //                if (RoomUser.role != 1)
            //                    playingMap.put(RoomUser.peerId, RoomUser.publishState > 1 && RoomUser.publishState < 4);
            //            } else {
            playingMap.put(RoomUser.peerId, RoomUser.publishState > 1 && RoomUser.publishState < 4);
            //            }
        }
        pandingSet.remove(RoomUser.peerId);
        if (RoomUser.role == 2) {
            if (RoomUser.publishState >= 1)
                publishSet.add(RoomUser.peerId);
        }

        getPlayingList();
        Log.e("xiao", "userpublish");
        doPlayVideo(RoomUser.peerId);
        changeUserState(RoomUser);
        memberListAdapter.notifyDataSetChanged();
        sendStudentMove();
        doLayout();
    }

    @Override
    public void roomManagerUserUnPublished(RoomUser RoomUser) {
        playingMap.remove(RoomUser.peerId);
        publishSet.remove(RoomUser.peerId);
        stuMoveInfoMap.remove(RoomUser.peerId);
        //        if(RoomUser.publishState<=1)
        //            RoomManager.getInstance().unPlayVideo(RoomUser.peerId);
        //        if (RoomUser.publishState <= 0) {
        doUnPlayVideo(RoomUser);
        //        }
        //        }

        /*if(processVideo)
        {
            unpublishlist.add(RoomUser.peerId);
            return;
        }*/
        //processVideo = true;


        changeUserState(RoomUser);
        memberListAdapter.notifyDataSetChanged();
        sendStudentMove();
        doLayout();
    }

    private void unPlayAll() {
        if (roomType != 0) {
            for (int i = 0; i < playingList.size(); i++) {
                RoomUser u = playingList.get(i);
                if (u.role == 2) {
                    RoomManager.getInstance().unPlayVideo(u.peerId);
                }
            }
        }
    }

    @Override
    public void roomManagerSelfEvicted() {
        RoomClient.getInstance().setExit(true);
        RoomClient.getInstance().kickout(RoomClient.Kickout_Repeat);
    }

    @Override
    public void roomManagerMessageReceived(RoomUser RoomUser, String s, int type, long ts) {
        ChatData ch = new ChatData();
        ch.setUser(RoomUser);
        ch.setStystemMsg(false);
        if (type == 0) {
            ch.setMessage(s);
            ch.setTrans(false);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            Date curDate = null;
            if (StringUtils.isEmpty(path)) {
                curDate = new Date(System.currentTimeMillis());//获取当前时间
            } else {
                curDate = new Date(ts);
            }
            String str = formatter.format(curDate);
            ch.setTime(str);
            chatList.add(ch);
            chlistAdapter.notifyDataSetChanged();
            list_chat.setSelection(chlistAdapter.getCount());
        }

    }

    @Override
    public void roomManagerOnRemoteMsg(boolean add, String id, String name, long ts, Object data, boolean inList, String fromid) {
        if (add) {
            OnRemotePubMsg(id, name, ts, data, inList);
        } else {
            OnRemoteDelMsg(id, name, ts, data, inList);
        }
    }

    @Override
    public void roomManagerOnMediaStatus(RoomUser RoomUser, Map<String, String> map) {

    }

    @Override
    public void roomManagerMediaPublish(Stream stream) {
        if (wbFragment != null) {
            wbFragment.closeNewPptVideo();
        }
        this.stream = stream;
        MediaListAdapter.isPublish = true;
        MediaListAdapter.isPlay = false;
        RoomManager.isMediaPublishing = false;
        isWBMediaPlay = false;
        isMediaMute = false;
        Object objfileid = stream.attrMap.get("fileid");
        long fileid = -1;
        if (objfileid != null) {
            if (objfileid instanceof String) {
                fileid = Long.valueOf(objfileid.toString());
            } else if (objfileid instanceof Number) {
                fileid = ((Number) objfileid).longValue();
            }
        }
        mediaListAdapter.setLocalfileid(fileid);
        if (stream.isVideo()) {
            readyForPlayVideo(stream);
        } else {
            if (myRrole == 0) {
                lin_audio_control.setVisibility(View.VISIBLE);
                img_disk.setVisibility(View.INVISIBLE);
            } else {
                lin_audio_control.setVisibility(View.INVISIBLE);
                img_disk.setVisibility(View.VISIBLE);
                boolean ispause = stream.attrMap.get("pause") == null ? false : (boolean) stream.attrMap.get("pause");
                if (ispause) {
                    img_disk.clearAnimation();
                } else {
                    img_disk.startAnimation(operatingAnim);
                }
            }
            img_voice_mp3.setImageResource(R.drawable.btn_volume_pressed);
            vol = 0.5;
            sek_voice_mp3.setProgress((int) (vol * 100));
            int da = (int) stream.attrMap.get("duration");
            SimpleDateFormat formatter = new SimpleDateFormat("mm:ss ");
            Date daDate = new Date(da);
            mp3Duration = formatter.format(daDate);
            txt_mp3_time.setText("00:00" + "/" + mp3Duration);
            if (txt_mp3_name != null) {
                txt_mp3_name.setText((String) stream.attrMap.get("filename"));
            }
        }
    }

    @Override
    public void roomManagerMediaUnPublish(Stream stream) {
        mediaListAdapter.setLocalfileid(-1);
        mp3Duration = "00:00";
        if (stream.isVideo()) {
            removeVideoFragment();
        } else {
            lin_audio_control.setVisibility(View.INVISIBLE);
            img_disk.clearAnimation();
            img_disk.setVisibility(View.INVISIBLE);
        }
        if (MediaListAdapter.isPlay) {
            MediaListAdapter.isPlay = false;
            ShareDoc media = WhiteBoradManager.getInstance().getCurrentMediaDoc();
            mediaListAdapter.setLocalfileid(media.getFileid());
            WhiteBoradManager.getInstance().setCurrentMediaDoc(media);
            String strSwfpath = media.getSwfpath();
            int pos = strSwfpath.lastIndexOf('.');
            strSwfpath = String.format("%s-%d%s", strSwfpath.substring(0, pos), 1, strSwfpath.substring(pos));
            String url = "http://" + WhiteBoradManager.getInstance().getFileServierUrl() + ":" + WhiteBoradManager.getInstance().getFileServierPort() + strSwfpath;
            RoomManager.isMediaPublishing = true;
            if (isClassBegin) {
                RoomManager.getInstance().publishMedia(url, com.classroomsdk.Tools.isMp4(media.getFilename()), media.getFilename(), media.getFileid(), "__all");
            } else {
                RoomManager.getInstance().publishMedia(url, com.classroomsdk.Tools.isMp4(media.getFilename()), media.getFilename(), media.getFileid(), RoomManager.getInstance().getMySelf().peerId);
            }
        }
        if (isWBMediaPlay) {
            //            int pos = this.url.lastIndexOf('.');
            //            this.url = String.format("%s-%d%s", url.substring(0, pos), 1, url.substring(pos));
            //            String url = "http://" + WhiteBoradManager.getInstance().getFileServierUrl() + ":" + WhiteBoradManager.getInstance().getFileServierPort() + this.url;
            RoomManager.isMediaPublishing = true;
            if (isClassBegin) {
                RoomManager.getInstance().publishMedia(url, isvideo, "", fileid, "__all");
            } else {
                RoomManager.getInstance().publishMedia(url, isvideo, "", fileid, RoomManager.getInstance().getMySelf().peerId);
            }
            isWBMediaPlay = false;
        }
        MediaListAdapter.isPlay = false;
        MediaListAdapter.isPublish = false;

    }

    @Override
    public void roomManagerUpdateAttributeStream(Stream stream, long pos, boolean isPlay) {
        if (stream.isVideo()) {
            if (videofragment == null) {
                readyForPlayVideo(stream);
                if (wbFragment != null) {
                    wbFragment.closeNewPptVideo();
                }
                this.stream = stream;
                MediaListAdapter.isPublish = true;
                MediaListAdapter.isPlay = false;
                RoomManager.isMediaPublishing = false;
                isWBMediaPlay = false;
                isMediaMute = false;
                Object objfileid = stream.attrMap.get("fileid");
                long fileid = -1;
                if (objfileid != null) {
                    if (objfileid instanceof String) {
                        fileid = Long.valueOf(objfileid.toString());
                    } else if (objfileid instanceof Number) {
                        fileid = ((Number) objfileid).longValue();
                    }
                }
                mediaListAdapter.setLocalfileid(fileid);

            } else {
                videofragment.controlMedia(stream, pos, isPlay);
            }

        } else {

            if (sek_mp3 != null) {
                int curtime = (int) ((double) pos / (int) stream.attrMap.get("duration") * 100);
                sek_mp3.setProgress(curtime);

            }
            if (img_play_mp3 != null) {
                if (!isPlay) {
                    img_play_mp3.setImageResource(R.drawable.btn_pause_normal);
                    if (!(myRrole == 0)) {
                        img_disk.startAnimation(operatingAnim);
                    }
                } else {
                    img_play_mp3.setImageResource(R.drawable.btn_play_normal);
                    if (!(myRrole == 0)) {
                        img_disk.clearAnimation();
                    }
                }
            }
            if (txt_mp3_time != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("mm:ss ");
                Date curDate = new Date(pos);//获取当前时间
                Date daDate = new Date((int) stream.attrMap.get("duration"));
                String strcur = formatter.format(curDate);
                String strda = formatter.format(daDate);
                txt_mp3_time.setText(strcur + "/" + strda);
            }
            if (txt_mp3_name != null) {
                txt_mp3_name.setText((String) stream.attrMap.get("filename"));
            }
        }
    }

    @Override
    public void roomManagerRoomConnected(RoomUser roomUser) {
        getGiftNum(serial, roomUser.peerId);
    }

    @Override
    public void roomManagerPlayBackClearAll() {
        if (chatList != null) {
            chatList.clear();
        }
        if (chlistAdapter != null) {
            chlistAdapter.notifyDataSetChanged();
        }
        if (wbFragment != null) {
            wbFragment.clearLcAllData();
            wbFragment.dispatchEvent("room-playback-clear_all");
        }
    }

    @Override
    public void roomManagerPlayBackUpdateTime(long currenttime) {
        if (isEnd) {
            return;
        }
        this.currenttime = currenttime;
        double pos = (double) (currenttime - starttime) / (double) (endtime - starttime);
        sek_play_back.setProgress((int) (pos * 100));

        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss ");
        Date curDate = new Date(currenttime - starttime);//获取当前时间
        Date daDate = new Date(endtime - starttime);
        String strcur = formatter.format(curDate);
        String strda = formatter.format(daDate);
        txt_play_back_time.setText(strcur + "/" + strda);
    }

    long starttime;
    long endtime;
    long currenttime;

    @Override
    public void roomManagerPlayBackDuration(long starttime, long endtime) {
        this.starttime = starttime;
        this.endtime = endtime;
    }

    @Override
    public void roomManagerPlayBackEnd() {
        isPlayBackPlay = false;
        img_play_back.setImageResource(R.drawable.btn_play_normal);
        sek_play_back.setProgress(0);
        RoomManager.getInstance().pausePlayback();
        isEnd = true;
    }

    @Override
    public void roomManagerCameraLost() {

    }

    @Override
    public void roomManagerPublishConnectFailed() {

    }

    @Override
    public void roomManagerScreenPublish(Stream stream) {
        this.stream = stream;
        MediaListAdapter.isPublish = true;
        if (wbFragment != null) {
            wbFragment.closeNewPptVideo();
        }
        for (int i = 0; i < videoItems.size(); i++) {
            videoItems.get(i).sf_video.setZOrderMediaOverlay(false);
            videoItems.get(i).sf_video.setVisibility(View.INVISIBLE);
        }
        screenFragment = ScreenFragment.getInstance();
        screenFragment.setStream(stream);
        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        if (!screenFragment.isAdded()) {
            ft.replace(R.id.video_container, screenFragment);
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    public void roomManagerScreenUnPublish(Stream stream) {
        romoveScreenFragment();
    }

    @Override
    public void onCapturerStopped() {
        if (isOpenCamera) {
            openCamera();
        }
    }

    @Override
    public void onCapturerStarted(boolean b) {

    }

    @Override
    public void onPublishError(int i) {
        String msg = i == 1 ? getString(R.string.udp_alert) : getString(R.string.fire_wall_alert);
        Tools.ShowAlertDialog(this, msg);
    }

    @Override
    public void onSubError(int i) {
        String msg = i == 1 ? getString(R.string.udp_alert) : getString(R.string.fire_wall_alert);
        Tools.ShowAlertDialog(this, msg);
    }

    private void romoveScreenFragment() {
        MediaListAdapter.isPublish = false;
        screenFragment = ScreenFragment.getInstance();
        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        if (screenFragment.isAdded()) {
            ft.remove(screenFragment);
            ft.commitAllowingStateLoss();
        }
        if (!isZoom) {
            for (int i = 0; i < videoItems.size(); i++) {
                videoItems.get(i).sf_video.setZOrderMediaOverlay(true);
                videoItems.get(i).sf_video.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onPageFinished() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] pers = new String[2];
            if (!(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    pers[0] = Manifest.permission.CAMERA;
                }
            }


            if (!(checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    for (int i = 0; i < pers.length; i++) {
                        if (pers[i] == null) {
                            pers[i] = Manifest.permission.RECORD_AUDIO;
                        }
                    }
                }
            }
            if (pers[0] != null) {
                requestPermissions(pers, REQUEST_CODED);
            } else {
                joinRoom();
            }
        } else {
            joinRoom();
        }
    }

    @Override
    public void onRoomDocChange() {
        WhiteBoradManager.getInstance().getDocList();
        WhiteBoradManager.getInstance().getMediaList();
        fileListAdapter.notifyDataSetChanged();
        mediaListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onWhiteBoradZoom(final boolean isZoom) {
        this.isZoom = isZoom;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isZoom) {
                    for (int i = 0; i < videoItems.size(); i++) {
                        videoItems.get(i).sf_video.setZOrderMediaOverlay(false);
                        videoItems.get(i).sf_video.setVisibility(View.INVISIBLE);
                    }
                    lin_menu.setVisibility(View.GONE);
                    rel_students.setVisibility(View.GONE);
                    lin_time.setVisibility(View.GONE);
                    lin_audio_control.setVisibility(View.GONE);
                    rel_tool_bar.setVisibility(View.GONE);
                    LinearLayout.LayoutParams main_param = (LinearLayout.LayoutParams) lin_main.getLayoutParams();
                    main_param.width = LinearLayout.LayoutParams.MATCH_PARENT;

                    main_param.height = LinearLayout.LayoutParams.MATCH_PARENT;
                    lin_main.setLayoutParams(main_param);
                    LinearLayout.LayoutParams w_param = (LinearLayout.LayoutParams) rel_w.getLayoutParams();
                    main_param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    main_param.height = LinearLayout.LayoutParams.MATCH_PARENT;
                    rel_w.setLayoutParams(w_param);
                    LinearLayout.LayoutParams rel_wb_param = (LinearLayout.LayoutParams) rel_wb_container.getLayoutParams();
                    rel_wb_param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    rel_wb_param.height = LinearLayout.LayoutParams.MATCH_PARENT;
                    rel_wb_container.setLayoutParams(rel_wb_param);
                } else {
                    for (int i = 0; i < videoItems.size(); i++) {
                        videoItems.get(i).sf_video.setZOrderMediaOverlay(true);
                        videoItems.get(i).sf_video.setVisibility(View.VISIBLE);
                    }
                    lin_menu.setVisibility(View.VISIBLE);
                    if (roomType != 0) {
                        rel_students.setVisibility(View.VISIBLE);
                    }
                    lin_time.setVisibility(View.VISIBLE);
                    rel_tool_bar.setVisibility(View.VISIBLE);
                    if (myRrole == 0 && MediaListAdapter.isPublish && !com.classroomsdk.Tools.isMp4(WhiteBoradManager.getInstance().getCurrentMediaDoc().getFilename())) {
                        lin_audio_control.setVisibility(View.VISIBLE);
                    } else {
                        lin_audio_control.setVisibility(View.INVISIBLE);
                    }

                    doLayout();
                    if (RoomManager.getInstance().getMySelf() != null && !TextUtils.isEmpty(RoomManager.getInstance().getMySelf().peerId)) {
                        doPlayVideo(RoomManager.getInstance().getMySelf().peerId);
                    }
                    if (!isClassBegin) {
                        playSelfBeforeClassBegin();
                    }
                   /* doPlayVideo(null);*/
                }
            }
        });

    }

    private String url;
    private boolean isvideo;
    private long fileid;
    private boolean isWBMediaPlay = false;

    @Override
    public void onWhiteBoradMediaPublish(String url, boolean isvideo, long fileid) {
        this.url = url;
        this.isvideo = isvideo;
        this.fileid = fileid;
        isWBMediaPlay = true;
    }

    public void joinRoom() {
        Log.d("classroomsdk", "Start!");


        HashMap<String, Object> params = new HashMap<String, Object>();
        if (!param.isEmpty())
            params.put("param", param);

        params.put("userid", userid);
        params.put("password", password);
        params.put("serial", serial);
        params.put("userrole", userrole);
        params.put("nickname", nickname);

        params.put("mobilenameOnList", mobilenameNotOnList);
        if (domain != null && !domain.isEmpty())
            params.put("domain", domain);

        if (path != null && !path.isEmpty()) {
            params.put("path", path);
            if (type != -1)
                params.put("type", type);
            Log.e("TAG++++++", params.toString());
            RoomManager.getInstance().joinPlayBackRoom(host, port, nickname, params, new HashMap<String, Object>(), true);
        } else {
            int ret = RoomManager.getInstance().joinRoom(host, port, nickname, params, new HashMap<String, Object>(), true);
            Log.e("TAG++else", params.toString());
            Log.e("TAG++else", host + "__" + port + "nickname");
            if (ret != RoomManager.ERR_OK) {
                Log.e("RoomActivity", "nonono");
            }
        }
        //        getGiftNumJoinRoom(serial, userid, nickname, params);

    }

    private void checkRaiseHands() {
        int count = 0;
        for (RoomUser u : RoomManager.getInstance().getUsers().values()) {
            if (u.role == 2) {
                if (u.properties.containsKey("raisehand")) {
                    boolean israisehand = Tools.isTure(u.properties.get("raisehand"));
                    if (israisehand) {
                        count++;
                    }
                }
            }
        }
        if (count > 0) {
            img_member_list.setImageResource(R.drawable.icon_users_normal_raise_hand);
        } else {
            img_member_list.setImageResource(R.drawable.img_member_list);
        }

    }

    private void clear() {
        isClassBegin = false;
        myRrole = -1;
        isFromList = false;
        RoomClient.getInstance().setExit(false);
        playingMap.clear();
        playingList.clear();
        pandingSet.clear();
        chatList.clear();
        RoomManager.getInstance().setCallbBack(null);
        RoomManager.getInstance().setWhiteBoard(null);
        teacherItem.sf_video.release();
        stu_in_sd.sf_video.release();
        for (int i = 0; i < videoItems.size(); i++) {
            videoItems.get(i).sf_video.release();
        }
        RoomClient.getInstance().setExit(false);
        WhiteBoradManager.getInstance().clear();
        RoomManager.getInstance().useLoudSpeaker(false);
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    public void showExitDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.remind);
        builder.setMessage(R.string.logouts);
        builder.setPositiveButton(R.string.sure,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RoomClient.getInstance().setExit(true);
                        RoomManager.getInstance().leaveRoom();
                    }
                }).setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showClassDissMissDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.remind);
        builder.setMessage(R.string.make_sure_class_dissmiss);
        builder.setPositiveButton(R.string.sure,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RoomManager.getInstance().delMsg("ClassBegin", "ClassBegin", "__all", new HashMap<String, Object>());
                        RoomManager.getInstance().delMsg("__AllAll", "__AllAll", "__none", new HashMap<String, Object>());
                        txt_class_begin.setVisibility(View.GONE);
                        sendClassDissToPhp();
                    }
                }).setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /***
     *
     * @param type 1，人员列表  3，媒体列表 2,文档列表
     */
    private void showList(int type) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final int wid = dm.widthPixels;
        final int hid = dm.heightPixels;
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_pop, null);
        //        contentView.setBackgroundColor(Color.BLUE);
        ListView list = (ListView) contentView.findViewById(R.id.list);
        TextView txt_topic = (TextView) contentView.findViewById(R.id.topic);


        PopupWindow popupWindow = new PopupWindow(findViewById(R.id.mainLayout), wid / 3, hid);
        popupWindow.setContentView(contentView);
        if (type == 1) {
            getMemberList();
            txt_topic.setText(getString(R.string.userlist) + "（" + memberList.size() + "）");
            list.setAdapter(memberListAdapter);
            memberListAdapter.notifyDataSetChanged();
        } else if (type == 2) {
            txt_topic.setText(getString(R.string.doclist) + "（" + WhiteBoradManager.getInstance().getDocList().size() + "）");
            fileListAdapter.setPop(popupWindow);
            list.setAdapter(fileListAdapter);
            WhiteBoradManager.getInstance().getDocList();
            fileListAdapter.notifyDataSetChanged();
        } else if (type == 3) {
            txt_topic.setText(getString(R.string.medialist) + "（" + WhiteBoradManager.getInstance().getMediaList().size() + "）");
            mediaListAdapter.setPop(popupWindow);
            list.setAdapter(mediaListAdapter);
            WhiteBoradManager.getInstance().getMediaList();
            mediaListAdapter.notifyDataSetChanged();
        }
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(findViewById(R.id.mainLayout), Gravity.RIGHT, 0, 0);
    }

    private void getMemberList() {
        memberList.clear();
        for (RoomUser u : RoomManager.getInstance().getUsers().values()) {
            if (!u.peerId.equals(RoomManager.getInstance().getMySelf().peerId) && (u.role == 2 || u.role == 1)) {
                if (u.role == 1) {
                    memberList.add(0, u);
                } else {
                    memberList.add(u);
                }
            }
        }
    }

    private void OnRemotePubMsg(String id, String name, long ts, Object data, boolean inList) {

        if (name.equals("ClassBegin")) {
            if (isClassBegin) {
                return;
            }
            isClassBegin = true;
            try {
                char ops = RoomManager.getInstance().getRoomProperties().getString("chairmancontrol").charAt(23);
                if (myRrole == 0) {
                    RoomManager.getInstance().changeUserPublish(RoomManager.getInstance().getMySelf().peerId, 3);
                } else if (ops == '1' && publishSet.size() < maxVideo && myRrole == 2) {
                    RoomManager.getInstance().changeUserPublish(RoomManager.getInstance().getMySelf().peerId, 3);
                }
                try {
                    if (!RoomManager.getInstance().getRoomProperties().getString("companyid").equals("10035")) {
                        txt_class_begin.setBackgroundResource(R.drawable.round_back_red);
                        txt_class_begin.setText(R.string.classdismiss);
                        txt_class_begin.setClickable(true);
                    } else {
                        txt_class_begin.setBackgroundResource(R.drawable.round_back_red_black);
                        txt_class_begin.setText(R.string.classdismiss);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (myRrole == 0 && roomType != 0) {
                    lin_control_tool.setVisibility(View.VISIBLE);
                }
                wbFragment.setAddPagePermission(myRrole == 0 && isClassBegin);
                wbFragment.setTurnPagePermission(myRrole == 0 || (RoomManager.getInstance().getMySelf().role == 2 && RoomControler.isStudentCanTurnPage()));
                if (myRrole == 0) {
                    wbFragment.setDrawable(true);
                    wbFragment.localChangeDoc();
                }
                initViewByRoomTypeAndTeacher();
                //                if(!isMeTeacher){
                //                    lin_self_av_control.setVisibility(View.VISIBLE);
                //                }
                if (myRrole == 4 || myRrole == 0) {
                    if (!RoomControler.isShowClassBeginButton()) {
                        //                        if (RoomControler.isAutoClassBegin()) {
                        //                            txt_class_begin.setVisibility(View.GONE);
                        //                        } else {
                        txt_class_begin.setVisibility(View.VISIBLE);
                        //                        }
                    } else
                        txt_class_begin.setVisibility(View.GONE);
                } else {
                    txt_class_begin.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            classStartTime = ts;
            RoomManager.getInstance().pubMsg("UpdateTime", "UpdateTime", RoomManager.getInstance().getMySelf().peerId, null, false, null, null);
            if (timerbefClassbegin != null) {
                timerbefClassbegin.cancel();
                timerbefClassbegin = null;
            }
            if (myRrole == 0 && !inList) {
                RoomManager.getInstance().unPublishMedia();
            }
            if (roomType == 0 && userrole == 2 && RoomControler.isAutoHasDraw()) {
                RoomManager.getInstance().changeUserProperty(RoomManager.getInstance().getMySelf().peerId, "__all", "candraw", true);
            }
            unPlaySelfAfterClassBegin();
            RoomClient.getInstance().onClassBegin();
        } else if (name.equals("UpdateTime")) {
            if (isClassBegin) {
                if (timerbefClassbegin != null) {
                    timerbefClassbegin.cancel();
                    timerbefClassbegin = null;
                }
                serviceTime = ts;
                localTime = serviceTime - classStartTime;
                if (timerAddTime == null) {
                    timerAddTime = new Timer();
                    timerAddTime.schedule(new AddTime(), 1000, 1000);
                }
            } else {
                if (timerbefClassbegin == null && !isClassBegin && !getfinalClassBeginMode()) {
                    timerbefClassbegin = new Timer();
                    timerbefClassbegin.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        long nowTime = System.currentTimeMillis() / 1000;
                                        long startTime = RoomManager.getInstance().getRoomProperties().getLong("starttime");
                                        long proTime = startTime - nowTime;
                                        if (proTime == 60 && !timeMessages.get(0).isShowed) {
                                            showTimeTipPop(timeMessages.get(0));
                                        }
                                        if (proTime <= -60 && !timeMessages.get(1).isShowed) {
                                            int overtime = Math.abs((int) (proTime / 60));
                                            timeMessages.get(1).message = getString(R.string.classroom_part_01) + "<font color='#FFD700'>" + overtime + "</font> " + getString(R.string.classroom_part_02);
                                            showTimeTipPop(timeMessages.get(1));
                                        }
                                        if (proTime <= 60) {
                                            txt_class_begin.setBackgroundResource(R.drawable.round_back_red);
                                            txt_class_begin.setText(R.string.classbegin);
                                            txt_class_begin.setClickable(true);
                                            Log.e("xiao", "proTime<=60 proTime = " + proTime);

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                    }, 500, 1000);
                } else if (!isClassBegin) {
                    if (myRrole == 0 && getfinalClassBeginMode()) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        try {
                            long expires = RoomManager.getInstance().getRoomProperties().getLong("endtime") + 5 * 60;
                            RoomManager.getInstance().pubMsg("ClassBegin", "ClassBegin", "__all", new HashMap<String, Object>(), true, expires);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        if (name.equals("ShowPage")) {
            mediaListAdapter.notifyDataSetChanged();
            fileListAdapter.notifyDataSetChanged();
        }
        if (name.equals("StreamFailure")) {
            Map<String, Object> mapdata = null;
            if (data instanceof String) {
                String str = (String) data;
                try {
                    JSONObject js = new JSONObject(str);
                    mapdata = toMap(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                mapdata = (Map<String, Object>) data;
            }
            String stupeerid = (String) mapdata.get("studentId");
            pandingSet.remove(stupeerid);
            memberListAdapter.setPubFailUserId(stupeerid);
            memberListAdapter.notifyDataSetChanged();

        }
        if (name.equals("videoDraghandle")) {

            JSONObject mapdata = null;
            if (data instanceof String) {
                String str = (String) data;
                try {
                    mapdata = new JSONObject(str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                mapdata = new JSONObject((Map<String, Object>) data);
            }
            videoarr = mapdata.optJSONObject("otherVideoStyle");
            sIterator = videoarr.keys();

            while (sIterator.hasNext()) {
                // 获得key
                String peerid = sIterator.next();
                // 根据key获得value, value也可以是JSONObject,JSONArray,使用对应的参数接收即可
                JSONObject videoinfo = videoarr.optJSONObject(peerid);
                float left = (float) videoinfo.optDouble("left");
                float top = (float) videoinfo.optDouble("top");
                boolean isDrag = Tools.isTure(videoinfo.opt("isDrag"));
                MoveVideoInfo mi = new MoveVideoInfo();
                mi.top = top;
                mi.left = left;
                mi.isDrag = isDrag;
                stuMoveInfoMap.put(peerid, mi);
                if (inList) {
                    continue;
                }
                moveStudent(peerid, top, left, isDrag);
            }
        }
    }


    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    private void OnRemoteDelMsg(String id, String name, long ts, Object data, boolean inList) {
        if (name.equals("ClassBegin")) {
            isClassBegin = false;
            try {
                char ops = RoomManager.getInstance().getRoomProperties().getString("chairmancontrol").charAt(23);
                //                if (ops == '1')
                RoomManager.getInstance().changeUserPublish(RoomManager.getInstance().getMySelf().peerId, 0);
                lin_control_tool.setVisibility(View.GONE);
                txt_class_begin.setVisibility(View.GONE);
                teacherItem.txt_name.setText("");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //            if(!isMeTeacher){
            //                lin_self_av_control.setVisibility(View.VISIBLE);
            //            }

            localTime = 0;
            if (timerAddTime != null) {
                timerAddTime.cancel();
                timerAddTime = null;
            }
            try {
                if (!RoomManager.getInstance().getRoomProperties().getString("companyid").equals("10035")) {
                    if (userrole == 0) {
                        txt_class_begin.setBackgroundResource(R.drawable.round_back_red);
                        txt_class_begin.setText(R.string.classbegin);
                        txt_class_begin.setClickable(true);
                        txt_class_begin.setVisibility(View.VISIBLE);
                        wbFragment.setDrawable(false);
                        txt_hour.setText("00");
                        txt_min.setText("00");
                        txt_ss.setText("00");
                    } else {
                        RoomClient.getInstance().setExit(true);
                        RoomManager.getInstance().leaveRoom();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            playSelfBeforeClassBegin();
            RoomClient.getInstance().onClassDismiss();
        }
    }

    private void sendClassDissToPhp() {
        if (!(myRrole == 0)) {
            return;
        }
        String webFun_controlroom = "http://" + host + ":" + port + "/ClientAPI" + "/roomover";
        RequestParams params = new RequestParams();
        try {
            params.put("act", 3);
            if (RoomControler.isAutoClassDissMiss()) {
                params.put("endsign", 1);
            }
            params.put("serial", RoomManager.getInstance().getRoomProperties().get("serial"));
            params.put("companyid", RoomManager.getInstance().getRoomProperties().get("companyid"));
            client.post(webFun_controlroom, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                    try {
                        int nRet = response.getInt("result");
                        if (nRet == 0) {
                            //                            RoomManager.getInstance().delMsg("ClassBegin", "ClassBegin", "__all", new HashMap<String, Object>());
                            //                            txt_class_begin.setVisibility(View.GONE);
                        } else {
                            Log.e("demo", "下课接口调用失败，失败数据：");
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("emm", "error=" + throwable.toString());
                    //                RoomClient.getInstance().joinRoomcallBack(-1);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void readyForPlayVideo(Stream stream) {
        //        goneAllVideo();
        for (int i = 0; i < videoItems.size(); i++) {
            videoItems.get(i).sf_video.setZOrderMediaOverlay(false);
            videoItems.get(i).sf_video.setVisibility(View.INVISIBLE);
        }
        videofragment = VideoFragment.getInstance();
        videofragment.setStream(stream);
        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        if (!videofragment.isAdded()) {
            ft.replace(R.id.video_container, videofragment);
            ft.commitAllowingStateLoss();
        }
        //        fm_video_container.bringToFront();
    }

    public void removeVideoFragment() {
        videofragment = VideoFragment.getInstance();
        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        if (videofragment.isAdded()) {
            mediaListAdapter.setLocalfileid(-1);
            ft.remove(videofragment);
            ft.commitAllowingStateLoss();
        }
        if (!isZoom) {
            for (int i = 0; i < videoItems.size(); i++) {
                videoItems.get(i).sf_video.setZOrderMediaOverlay(true);
                videoItems.get(i).sf_video.setVisibility(View.VISIBLE);
            }
        }
    }

    private void changeUserState(final RoomUser user) {
        getPlayingList();
        //        for (int i = 0; i < playingList.size(); i++) {
        //            final RoomUser user = playingList.get(i);
        if (user.role == 0) {
            teacherItem.img_pen.setVisibility(View.INVISIBLE);
            teacherItem.img_hand.setVisibility(View.INVISIBLE);

            if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
                teacherItem.img_mic.setVisibility(View.INVISIBLE);
            } else {
                teacherItem.img_mic.setVisibility(View.VISIBLE);
            }
        }

        if (roomType == 0) {
            stu_in_sd.peerid = user.peerId;
            if (!user.peerId.equals(RoomManager.getInstance().getMySelf().peerId) && user.role == RoomManager.getInstance().getMySelf().role) {
                return;
            }
            //小班课
            if (user.role == 2) {
                stu_in_sd.img_cam.setVisibility(View.VISIBLE);
                if (user.disablevideo) {
                    stu_in_sd.img_cam.setImageResource(R.drawable.icon_video_disable);
                } else {
                    stu_in_sd.img_cam.setImageResource(R.drawable.icon_video);
                    if (user.publishState == 0 || user.publishState == 1 || user.publishState == 4) {
                        stu_in_sd.img_cam.setVisibility(View.INVISIBLE);
                    } else {
                        stu_in_sd.img_cam.setVisibility(View.VISIBLE);
                    }
                }
                stu_in_sd.img_mic.setVisibility(View.VISIBLE);
                if (user.disableaudio) {
                    stu_in_sd.img_mic.setImageResource(R.drawable.icon_audio_disable);
                } else {
                    stu_in_sd.img_mic.setImageResource(R.drawable.icon_audio);
                    if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
                        stu_in_sd.img_mic.setVisibility(View.INVISIBLE);
                    } else {
                        stu_in_sd.img_mic.setVisibility(View.VISIBLE);
                    }
                }


                if (user.properties.containsKey("candraw")) {
                    boolean candraw = Tools.isTure(user.properties.get("candraw"));
                    if (candraw) {
                        stu_in_sd.img_pen.setVisibility(View.VISIBLE);//可以画图
                    } else {
                        stu_in_sd.img_pen.setVisibility(View.INVISIBLE);//不可以画图
                    }
                } else {
                    stu_in_sd.img_pen.setVisibility(View.INVISIBLE);//没给过画图权限
                }

                if (user.properties.containsKey("raisehand")) {
                    boolean israisehand = Tools.isTure(user.properties.get("raisehand"));
                    if (israisehand) {
                        stu_in_sd.img_hand.setVisibility(View.VISIBLE);//正在举手
                    } else {
                        stu_in_sd.img_hand.setVisibility(View.INVISIBLE);//同意了，或者拒绝了
                    }
                } else {
                    stu_in_sd.img_hand.setVisibility(View.INVISIBLE);//还没举手
                }

                if (user.properties.containsKey("giftnumber")) {
                    long giftnumber = user.properties.get("giftnumber") instanceof Integer ? (int) user.properties.get("giftnumber") : (long) user.properties.get("giftnumber");
                    //                        showGiftAim(stu_in_sd);
                    stu_in_sd.txt_gift_num.setText(giftnumber + "");
                }

                stu_in_sd.rel_group.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showStudentControlPop(user, -1);
                    }
                });
            }

            if (user.role == 1) {
                for (int i = 0; i < videoItems.size(); i++) {
                    final int finalI = i;
                    videoItems.get(i).parent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RoomUser user = RoomManager.getInstance().getUser(videoItems.get(finalI).peerid);
                            showStudentControlPop(user, finalI);
                        }
                    });

                    if (user.peerId.equals(videoItems.get(i).peerid)) {
                        if (user.disableaudio) {
                            videoItems.get(i).img_mic.setImageResource(R.drawable.icon_audio_disable);
                        } else {
                            videoItems.get(i).img_mic.setImageResource(R.drawable.icon_audio);
                            if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
                                videoItems.get(i).img_mic.setVisibility(View.INVISIBLE);
                            } else {
                                videoItems.get(i).img_mic.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        } else {
            if (user.role == 2 || user.role == 1) {
                for (int i = 0; i < videoItems.size(); i++) {
                    if (user.peerId.equals(videoItems.get(i).peerid)) {
                        videoItems.get(i).img_cam.setVisibility(View.VISIBLE);
                        if (user.disablevideo) {
                            videoItems.get(i).img_cam.setImageResource(R.drawable.icon_video_disable);
                        } else {
                            videoItems.get(i).img_cam.setImageResource(R.drawable.icon_video);
                            if (user.publishState == 0 || user.publishState == 1 || user.publishState == 4) {
                                videoItems.get(i).img_cam.setVisibility(View.INVISIBLE);
                            } else {
                                videoItems.get(i).img_cam.setVisibility(View.VISIBLE);
                            }
                        }
                        videoItems.get(i).img_mic.setVisibility(View.VISIBLE);
                        if (user.disableaudio) {
                            videoItems.get(i).img_mic.setImageResource(R.drawable.icon_audio_disable);
                        } else {
                            videoItems.get(i).img_mic.setImageResource(R.drawable.icon_audio);
                            if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
                                videoItems.get(i).img_mic.setVisibility(View.INVISIBLE);
                            } else {
                                videoItems.get(i).img_mic.setVisibility(View.VISIBLE);
                            }
                        }
                        if (user.properties.containsKey("candraw")) {
                            boolean candraw = Tools.isTure(user.properties.get("candraw"));
                            if (candraw) {
                                videoItems.get(i).img_pen.setVisibility(View.VISIBLE);//可以画图
                            } else {
                                videoItems.get(i).img_pen.setVisibility(View.INVISIBLE);//不可以画图
                            }
                        } else {
                            videoItems.get(i).img_pen.setVisibility(View.INVISIBLE);//没给过画图权限
                        }

                        if (user.properties.containsKey("raisehand")) {
                            boolean israisehand = Tools.isTure(user.properties.get("raisehand"));
                            if (israisehand) {
                                if (RoomManager.getInstance().getMySelf().role == 0) {
                                    if (user.role == 2) {
                                        videoItems.get(i).img_hand.setVisibility(View.VISIBLE);//正在举手
                                    }
                                }
                                if (RoomManager.getInstance().getMySelf().role == 2) {
                                    videoItems.get(i).img_hand.setVisibility(View.GONE);//正在举手
                                }
                                /*if (user.peerId.equals(RoomManager.getInstance().getMySelf().peerId)) {

                                }*/
                            } else {
                                videoItems.get(i).img_hand.setVisibility(View.INVISIBLE);//同意了，或者拒绝了
                            }
                        } else {
                            videoItems.get(i).img_hand.setVisibility(View.INVISIBLE);//还没举手
                        }

                        if (user.properties.containsKey("giftnumber")) {
                            long giftnumber = user.properties.get("giftnumber") instanceof Integer ? (int) user.properties.get("giftnumber") : (long) user.properties.get("giftnumber");
                            videoItems.get(i).txt_gift_num.setText(giftnumber + "");
                            //                    showGiftAim(videoItems.get(i - teachermark));
                        }

                    }
                }
                for (int i = 0; i < videoItems.size(); i++) {
                    final int finalI = i;
                    videoItems.get(i).parent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RoomUser user = RoomManager.getInstance().getUser(videoItems.get(finalI).peerid);
                            showStudentControlPop(user, finalI);
                        }
                    });
                }
            }
        }

    }

    long mLastTime = 0;
    long mCurTime = 0;

    private void getGiftNum(String roomNum, final String peerId) {

        String url = "http://" + host + ":" + port + "/ClientAPI/getgiftinfo";
        RequestParams params = new RequestParams();
        params.put("serial", roomNum);
        params.put("receiveid", peerId);

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                try {
                    int nRet = response.getInt("result");
                    final Map<String, Object> pert = new HashMap<String, Object>();
                    if (nRet == 0) {
                        JSONArray infos = response.optJSONArray("giftinfo");
                        JSONObject info = infos.getJSONObject(0);
                        long gifnum = info.optInt("giftnumber", 0);
                        pert.put("giftnumber", gifnum);
                    } else {
                        pert.put("giftnumber", 0L);
                    }
                    Log.d("emm", "gifnum = " + pert.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pert != null) {
                                if (RoomManager.getInstance().getMySelf().properties != null) {
                                    RoomManager.getInstance().getMySelf().properties.putAll(pert);
                                } else {
                                    RoomManager.getInstance().getMySelf().properties = new HashMap<>(pert);
                                }
                            }
                            RoomManager.getInstance().connected();
                        }
                    });


                } catch (JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RoomManager.getInstance().connected();
                        }
                    });

                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("emm", "error=" + throwable.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RoomManager.getInstance().connected();
                    }
                });
                //                RoomClient.getInstance().joinRoomcallBack(-1);
            }
        });
    }

    public void getGiftNumJoinRoom(String roomNum, final String peerId, final String nickname, final Map<String, Object> paramsMap) {

        String url = "http://" + host + ":" + port + "/ClientAPI/getgiftinfo";
        final RequestParams params = new RequestParams();
        params.put("serial", roomNum);
        params.put("receiveid", peerId);
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                try {
                    int nRet = response.getInt("result");
                    final Map<String, Object> pert = new HashMap<String, Object>();
                    if (nRet == 0) {
                        JSONArray infos = response.optJSONArray("giftinfo");
                        JSONObject info = infos.getJSONObject(0);
                        long gifnum = info.optInt("giftnumber");
                        if (peerId == null || peerId.isEmpty()) {
                            pert.put("giftnumber", 0);
                            //                            NotificationCenter.getInstance().postNotificationName(UpdataGiftNum,gifnum);
                        } else {
                            pert.put("giftnumber", gifnum);
                        }

                    }
                    Log.d("emm", "gifnum = " + pert.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RoomManager.getInstance().setTestServer("192.168.1.57", 8890);
                            int ret = RoomManager.getInstance().joinRoom(host, port, nickname, paramsMap, pert, true);
                            if (ret != RoomManager.ERR_OK) {
                                Log.e("RoomActivity", "nonono");
                            }
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("emm", "error=" + throwable.toString());
                //                RoomClient.getInstance().joinRoomcallBack(-1);
            }
        });
    }

    @Override
    public void onResult(final int index, final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (chatList.size() > index) {
                    chatList.get(index).setTrans(true);
                    chatList.get(index).setTrans(result);
                    chlistAdapter.notifyDataSetChanged();
                    list_chat.setSelection(index);
                }
            }
        });

    }

    private void showGiftAim(VideoItem item) {
        //初始化 Translate动画
        final ImageView img_gift = new ImageView(this);
        img_gift.setImageResource(R.drawable.ico_gift);
        RelativeLayout.LayoutParams relparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relparam.addRule(RelativeLayout.CENTER_IN_PARENT);
        img_gift.setLayoutParams(relparam);
        rel_parent.addView(img_gift);


        int[] loca = new int[2];
        item.lin_gift.getLocationInWindow(loca);
       /* if (item.lin_gift.getVisibility() != View.VISIBLE) {
            return;
        }*/
        float x = loca[0];
        float y = loca[1];
        //        int[] giftLoca = new int[2];
        //        img_gift.getLocationOnScreen(giftLoca);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final int wid = dm.widthPixels;
        final int hid = dm.heightPixels;
        float gx = wid / 2 - img_gift.getWidth();
        float gy = hid / 2 - img_gift.getHeight();

        float dx = x - gx;
        float dy = y - gy;
        TranslateAnimation translateAnimation = new TranslateAnimation(0, dx, 0, dy);
        //        translateAnimation.setFillAfter(true);
        //初始化 Alpha动画
        //初始化
        //        ScaleAnimation scaleAnimationBig = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f);
        //        scaleAnimationBig.setStartOffset(1000);
        ScaleAnimation scaleAnimation = new ScaleAnimation(4.0f, 0.1f, 4.0f, 0.1f);
        //scaleAnimation.setFillAfter(true);
        //动画集
        AnimationSet set = new AnimationSet(true);
        //        set.setFillAfter(true);
        set.setFillBefore(false);
        //        set.addAnimation(scaleAnimationBig);
        set.addAnimation(scaleAnimation);
        set.addAnimation(translateAnimation);

        //设置动画时间 (作用到每个动画)
        set.setDuration(3000);
        img_gift.clearAnimation();
        set.cancel();
        img_gift.startAnimation(set);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                img_gift.clearAnimation();
                Log.i("oooooooo", "lllllllllll");
                img_gift.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void doLayout() {
        if (isZoom) {
            return;
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final int wid = dm.widthPixels;
        final int hid = dm.heightPixels;
        final int disWid = 2048;
        final int disHid = 1536;
        //顶部工具栏
        LinearLayout.LayoutParams tool_bar_param = (LinearLayout.LayoutParams) rel_tool_bar.getLayoutParams();
        tool_bar_param.width = LinearLayout.LayoutParams.MATCH_PARENT;
        tool_bar_param.height = (int) (hid * ((double) 110 / (double) disHid));
        rel_tool_bar.setLayoutParams(tool_bar_param);
        RelativeLayout.LayoutParams play_back_tool = (RelativeLayout.LayoutParams) rel_tool_bar_play_back.getLayoutParams();
        play_back_tool.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        play_back_tool.height = (int) (hid * ((double) 110 / (double) disHid));
        rel_tool_bar_play_back.setLayoutParams(play_back_tool);
        //左边白板加六路学生视频
        LinearLayout.LayoutParams main_param = (LinearLayout.LayoutParams) lin_main.getLayoutParams();
        main_param.width = (int) (wid * ((double) 1580 / (double) disWid));
        main_param.height = LinearLayout.LayoutParams.MATCH_PARENT;
        lin_main.setLayoutParams(main_param);
        //右边老师视频加聊天
        LinearLayout.LayoutParams menu_param = (LinearLayout.LayoutParams) lin_menu.getLayoutParams();
        menu_param.width = (int) (wid * ((double) 468 / (double) disWid));
        menu_param.height = LinearLayout.LayoutParams.MATCH_PARENT;
        lin_menu.setLayoutParams(menu_param);

        LinearLayout.LayoutParams w_param = (LinearLayout.LayoutParams) rel_w.getLayoutParams();
        main_param.width = (int) (wid * ((double) 1580 / (double) disWid));
        main_param.height = (int) (hid * ((double) 1426 / (double) disHid));
        rel_w.setLayoutParams(w_param);

        LinearLayout.LayoutParams time_param = (LinearLayout.LayoutParams) lin_time.getLayoutParams();
        time_param.width = LinearLayout.LayoutParams.MATCH_PARENT;
        time_param.height = (int) (hid * ((double) 115 / (double) disHid));
        lin_time.setLayoutParams(time_param);


        LinearLayout.LayoutParams lin_audio_control_param = (LinearLayout.LayoutParams) lin_audio_control.getLayoutParams();
        lin_audio_control_param.width = LinearLayout.LayoutParams.MATCH_PARENT;
        lin_audio_control_param.height = (int) (hid * ((double) 115 / (double) disHid));
        lin_audio_control.setLayoutParams(lin_audio_control_param);

        RelativeLayout.LayoutParams students_param = new RelativeLayout.LayoutParams(0, 0);
        students_param.width = (int) (wid * ((double) 1580 / (double) disWid));
        students_param.height = (int) (students_param.width * ((double) 264 / (double) 1580)) + 20;
        students_param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        if (videoItems.size() > 0) {
            v_students.setLayoutParams(students_param);
            v_students.setVisibility(View.VISIBLE);
        } else {
            v_students.setVisibility(View.GONE);
        }
        RelativeLayout.LayoutParams rel_student_param = (RelativeLayout.LayoutParams) rel_students.getLayoutParams();
        rel_student_param.topMargin = time_param.height;
        rel_students.setLayoutParams(rel_student_param);

        LinearLayout.LayoutParams rel_wb_param = (LinearLayout.LayoutParams) rel_wb_container.getLayoutParams();

        if (videoItems.size() > 0) {
            rel_wb_param.height = (hid - tool_bar_param.height - tool_bar_param.height - lin_audio_control_param.height - students_param.height);
        } else {
            rel_wb_param.height = (hid - tool_bar_param.height - tool_bar_param.height - lin_audio_control_param.height);
        }

        if ((rel_wb_param.height * ((double) 16 / (double) 9)) > (wid - (wid * ((double) 468 / (double) disWid)))) {
            rel_wb_param.width = (int) (wid - (wid * ((double) 468 / (double) disWid)));
            rel_wb_param.height = (int) (rel_wb_param.width * ((double) 9 / (double) 16));
        } else {
            rel_wb_param.width = (int) (rel_wb_param.height * ((double) 16 / (double) 9));
        }
        rel_wb_param.width = (int) (rel_wb_param.height * ((double) 16 / (double) 9));
        rel_wb_param.gravity = Gravity.CENTER;
        rel_wb_container.setLayoutParams(rel_wb_param);

        //        //menu上的视频
        RelativeLayout.LayoutParams stu_param_menu = (RelativeLayout.LayoutParams) teacherItem.sf_video.getLayoutParams();
        stu_param_menu.width = (int) (wid * ((double) 428 / (double) disWid));
        stu_param_menu.height = (int) (stu_param_menu.width * (double) 3 / (double) 4);

        RelativeLayout.LayoutParams stu_parent_menu = (RelativeLayout.LayoutParams) teacherItem.rel_group.getLayoutParams();
        stu_parent_menu.width = (int) (wid * ((double) 428 / (double) disWid));
        stu_parent_menu.height = (int) (stu_parent_menu.width * (double) 3 / (double) 4);

        LinearLayout.LayoutParams stu_video_label_menu = (LinearLayout.LayoutParams) teacherItem.rel_video_label.getLayoutParams();
        stu_video_label_menu.width = (int) (wid * ((double) 428 / (double) disWid));
        stu_video_label_menu.height = (int) ((stu_video_label_menu.width * (double) 3 / (double) 4));

        LinearLayout.LayoutParams stu_name_menu = (LinearLayout.LayoutParams) teacherItem.lin_name_label.getLayoutParams();
        stu_name_menu.width = (int) (wid * ((double) 428 / (double) disWid));
        stu_name_menu.height = (int) (stu_name_menu.width * ((double) 44 / (double) 428));

        LinearLayout.LayoutParams stu_par_menu = (LinearLayout.LayoutParams) teacherItem.parent.getLayoutParams();
        stu_par_menu.width = (int) (wid * ((double) 428 / (double) disWid));
        stu_par_menu.height = (int) ((stu_par_menu.width * ((double) 364 / (double) 428)));


        teacherItem.parent.setLayoutParams(stu_par_menu);
        teacherItem.sf_video.setLayoutParams(stu_param_menu);
        teacherItem.rel_group.setLayoutParams(stu_parent_menu);
        teacherItem.rel_video_label.setLayoutParams(stu_video_label_menu);
        teacherItem.lin_name_label.setLayoutParams(stu_name_menu);

        stu_in_sd.parent.setLayoutParams(stu_par_menu);
        stu_in_sd.sf_video.setLayoutParams(stu_param_menu);
        stu_in_sd.rel_group.setLayoutParams(stu_parent_menu);
        stu_in_sd.rel_video_label.setLayoutParams(stu_video_label_menu);
        stu_in_sd.lin_name_label.setLayoutParams(stu_name_menu);

    }

    private void do1vsnStudentVideoLayout() {
        if (videoItems.size() == 0) {
            return;
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final int wid = dm.widthPixels;
        final int hid = dm.heightPixels;
        final int disWid = 2048;
        final int disHid = 1536;

        for (int i = 0; i < videoItems.size(); i++) {
            if (!videoItems.get(i).isLayoutd) {
                videoItems.get(i).oldX = videoItems.get(i).parent.getLeft();
                videoItems.get(i).oldY = videoItems.get(i).parent.getTop();
                videoItems.get(i).isLayoutd = true;
            }
        }
        ArrayList<VideoItem> notMoveVideoItems = new ArrayList<VideoItem>();
        ArrayList<VideoItem> movedVideoItems = new ArrayList<VideoItem>();
        for (int i = 0; i < videoItems.size(); i++) {
            if (!videoItems.get(i).isMoved) {
                notMoveVideoItems.add(videoItems.get(i));
            } else {
                movedVideoItems.add(videoItems.get(i));
            }
        }
        for (int i = 0; i < notMoveVideoItems.size(); i++) {
            //这里我用RelativeLayout布局为列，其他布局设置方法一样，只需改变布局名就行
            RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) notMoveVideoItems.get(i).parent.getLayoutParams();
            layout.width = (int) (wid * ((double) 240 / disWid));
            layout.height = (int) ((layout.width * ((double) 224 / (double) 240)));
            //获得button控件的位置属性，需要注意的是，可以将button换成想变化位置的其它控件
            layout.topMargin = 20;
            layout.bottomMargin = 20;
            layout.leftMargin = 10 * (i + 1) + ((int) (wid * ((double) 240 / disWid))) * i;
            layout.rightMargin = 10;
            layout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            //设置button的新位置属性,left，top，right，bottom
            notMoveVideoItems.get(i).parent.setTranslationX(0);
            notMoveVideoItems.get(i).parent.setTranslationY(0);
            notMoveVideoItems.get(i).parent.setLayoutParams(layout);
            //将新的位置加入button控件中
            LinearLayout.LayoutParams linparam = (LinearLayout.LayoutParams) notMoveVideoItems.get(i).rel_video_label.getLayoutParams();
            linparam.width = (int) (wid * ((double) 240 / disWid));
            linparam.height = (int) ((linparam.width * (double) 3 / (double) 4));
            notMoveVideoItems.get(i).rel_video_label.setLayoutParams(linparam);
            LinearLayout.LayoutParams stu_name = (LinearLayout.LayoutParams) notMoveVideoItems.get(i).lin_name_label.getLayoutParams();
            stu_name.width = (int) (wid * ((double) 240 / disWid));
            stu_name.height = (int) (stu_name.width * ((double) 44 / (double) 240));
            notMoveVideoItems.get(i).lin_name_label.setLayoutParams(stu_name);
            notMoveVideoItems.get(i).isZoomd = false;
        }


    }


    class AddTime extends TimerTask {

        @Override
        public void run() {
            serviceTime += 1;
            localTime = serviceTime - classStartTime;
            showTime();
        }
    }

    private void showTime() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String H = "";
                String M = "";
                String S = "";
                long temps = localTime;
                long tempm = temps / 60;
                long temph = tempm / 60;
                long sec = temps - tempm * 60;
                tempm = tempm - temph * 60;
                H = temph == 0 ? "00" : temph >= 10 ? temph + "" : "0" + temph;
                M = tempm == 0 ? "00" : tempm >= 10 ? tempm + "" : "0" + tempm;
                S = sec == 0 ? "00" : sec >= 10 ? sec + "" : "0" + sec;
                txt_hour.setText(H);
                txt_min.setText(M);
                txt_ss.setText(S);
                try {
                    if (RoomManager.getInstance().getRoomProperties() == null) {
                        return;
                    }
                    long nowTime = System.currentTimeMillis() / 1000;
                    long endTime = RoomManager.getInstance().getRoomProperties().getLong("endtime");
                    long startTime = RoomManager.getInstance().getRoomProperties().getLong("starttime");
                    long proTime = endTime - startTime;
                    boolean isstart = false;
                    if (localTime == 0) {
                        proTime = startTime - nowTime;
                        isstart = false;
                    } else {
                        proTime = endTime - serviceTime;
                        isstart = true;
                    }
                    if (RoomManager.getInstance().getRoomProperties().getString("companyid").equals("10035")) {
                        if (proTime <= 60) {
                            txt_class_begin.setBackgroundResource(R.drawable.round_back_red);
                            txt_class_begin.setClickable(true);
                            Log.e("xiao", "showtime");
                            canClassDissMiss = true;
                        }
                        if (isstart && proTime < -5 * 60) {//自动下课
                            RoomManager.getInstance().delMsg("ClassBegin", "ClassBegin", "__all", new HashMap<String, Object>());
                            sendClassDissToPhp();
                        }

                        if (isstart && proTime <= 60 && !timeMessages.get(2).isShowed) {
                            showTimeTipPop(timeMessages.get(2));
                            txt_hour.setTextColor(RoomActivity.this.getResources().getColor(R.color.time_yel));
                            txt_min.setTextColor(RoomActivity.this.getResources().getColor(R.color.time_yel));
                            txt_ss.setTextColor(RoomActivity.this.getResources().getColor(R.color.time_yel));
                            txt_mao_01.setTextColor(RoomActivity.this.getResources().getColor(R.color.time_yel));
                            txt_mao_02.setTextColor(RoomActivity.this.getResources().getColor(R.color.time_yel));
                        }
                        if (isstart && proTime <= -3 * 60 && !timeMessages.get(3).isShowed) {
                            showTimeTipPop(timeMessages.get(3));
                            txt_hour.setTextColor(RoomActivity.this.getResources().getColor(R.color.time_red));
                            txt_min.setTextColor(RoomActivity.this.getResources().getColor(R.color.time_red));
                            txt_ss.setTextColor(RoomActivity.this.getResources().getColor(R.color.time_red));
                            txt_mao_01.setTextColor(RoomActivity.this.getResources().getColor(R.color.time_red));
                            txt_mao_02.setTextColor(RoomActivity.this.getResources().getColor(R.color.time_red));
                        }
                        if (isstart && proTime <= -5 * 60 + 10 && !timeMessages.get(4).isShowed) {
                            showTimeTipPop(timeMessages.get(4));
                            txt_hour.setTextColor(RoomActivity.this.getResources().getColor(R.color.time_red));
                            txt_min.setTextColor(RoomActivity.this.getResources().getColor(R.color.time_red));
                            txt_ss.setTextColor(RoomActivity.this.getResources().getColor(R.color.time_red));
                            txt_mao_01.setTextColor(RoomActivity.this.getResources().getColor(R.color.time_red));
                            txt_mao_02.setTextColor(RoomActivity.this.getResources().getColor(R.color.time_red));
                        }
                    } else {
                        txt_class_begin.setBackgroundResource(R.drawable.round_back_red);
                        txt_class_begin.setClickable(true);
                        Log.e("xiao", "showtime");
                        canClassDissMiss = true;
                    }

                    //                    showTimeTipPop(proTime,isstart);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void showTimeTipPop(final TimeMessage tms) {
        if (!(myRrole == 0) || getfinalClassBeginMode()) {
            return;
        }
        try {
            if (!RoomManager.getInstance().getRoomProperties().getString("companyid").equals("10035")) {
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tms.isShowed = true;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final int wid = dm.widthPixels;
        final int hid = dm.heightPixels;
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.time_tip_pop, null);
        final TextView txt_tip = (TextView) contentView.findViewById(R.id.txt_tip);
        final TextView txt_i_know = (TextView) contentView.findViewById(R.id.txt_i_know);
        txt_tip.setText(Html.fromHtml(tms.message));
        final PopupWindow popupWindow = new PopupWindow(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(contentView);
        txt_i_know.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        if (tms.hasKonwButton) {
            txt_i_know.setVisibility(View.VISIBLE);
            final Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (tms.count5 == 0) {
                                t.cancel();
                                if (!isFinishing()) {
                                    popupWindow.dismiss();
                                }
                            }
                            txt_i_know.setText(getResources().getString(R.string.i_konw) + tms.count5 + "'");
                            tms.count5--;
                        }
                    });

                }
            }, 1000, 1000);
        } else {
            tms.count5 = 10;
            txt_i_know.setVisibility(View.GONE);
            final Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (tms.count5 == 0) {
                                t.cancel();
                                popupWindow.dismiss();
                            }
                            txt_tip.setText(tms.message + " " + tms.count5 + "'");
                            tms.count5--;
                        }
                    });

                }
            }, 1000, 1000);
        }


        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    popupWindow.showAsDropDown(lin_time, txt_hour.getWidth() * 4, -lin_time.getMeasuredHeight());
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        joinRoom();
        if (grantResults.length == 0 || permissions.length == 0) {
            return;
        }

        for (int i = 0; i < permissions.length; i++) {
            if (Manifest.permission.CAMERA.equals(permissions[i])) {
                int grantResult = grantResults[0];
                boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
                if (!granted) {
                    Tools.ShowAlertDialog(this, getString(R.string.camera_hint));
                    RoomClient.getInstance().warning(1);
                }
            }
            if (Manifest.permission.RECORD_AUDIO.equals(permissions[i])) {
                int grantResult = grantResults[0];
                boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
                if (!granted) {
                    Tools.ShowAlertDialog(this, getString(R.string.mic_hint));
                    RoomClient.getInstance().warning(2);
                }
            }
        }
    }

    /**
     * 检测当的网络（WLAN、3G/2G）状态
     *
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    private void sendGift(final HashMap<String, RoomUser> userMap) {
        synchronized (sendgiftlock) {
            if (isSending) {
                return;
            }
            isSending = true;
            tSendGift = new Timer();
            tSendGift.schedule(new TimerTask() {
                int count = 0;

                @Override
                public void run() {
                    if (count == 2) {
                        isSending = false;
                        tSendGift.cancel();
                    } else {
                        count++;
                    }

                }
            }, 0, 1000);
            String url = "http://" + host + ":" + port + "/ClientAPI/sendgift";
            RequestParams params = new RequestParams();
            params.put("serial", RoomManager.getInstance().getRoomProperties().optString("serial"));
            params.put("sendid", RoomManager.getInstance().getMySelf().peerId);
            params.put("sendname", RoomManager.getInstance().getMySelf().nickName);
            HashMap<String, String> js = new HashMap<String, String>();
            for (RoomUser u : userMap.values()) {
                //            try {
                js.put(u.peerId, u.nickName);
                //            } catch (JSONException e) {
                //                e.printStackTrace();
                //            }
            }
            params.put("receivearr", js);
            client.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                    try {
                        Log.e("xiao", response.toString());
                        int nRet = response.getInt("result");
                        if (nRet == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (RoomUser u : userMap.values()) {
                                        long giftnumber = 0;
                                        if (u.properties.containsKey("giftnumber")) {
                                            giftnumber = u.properties.get("giftnumber") instanceof Integer ? (int) u.properties.get("giftnumber") : (long) u.properties.get("giftnumber");
                                        }
                                        giftnumber++;
                                        RoomManager.getInstance().changeUserProperty(u.peerId, "__all", "giftnumber", giftnumber);

                                    }
                                    //                                    isSending = false;
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("emm", "error=" + throwable.toString());
                }
            });
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitDialog();
            return true;
        }
      /*  if (keyCode == KeyEvent.KEYCODE_HOME) {
            if (Tools.isDialogShow) {
                Log.i("111111111", "qqqqqqqqqq");
                return true;
            } else {
                Log.i("111111111", "zzzzzzzzzzzz");
                return false;
            }
        }*/
        return super.onKeyDown(keyCode, event);
    }

    private void sendClassBeginToPhp() {
        if (!(myRrole == 0)) {
            return;
        }
        String webFun_controlroom = "http://" + host + ":" + port + "/ClientAPI" + "/roomstart";
        RequestParams params = new RequestParams();
        try {
            params.put("serial", RoomManager.getInstance().getRoomProperties().get("serial"));
            params.put("companyid", RoomManager.getInstance().getRoomProperties().get("companyid"));
            client.post(webFun_controlroom, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                    try {
                        int nRet = response.getInt("result");
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("emm", "error=" + throwable.toString());
                    //                RoomClient.getInstance().joinRoomcallBack(-1);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean getfinalClassBeginMode() {
        boolean isauto = true;
        try {
            isauto = RoomManager.getInstance().getRoomProperties().getString("companyid").equals("10035") ? true : RoomControler.isAutoClassBegin();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return isauto;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videofragment != null && stream != null && stream.isVideo()) {
            videofragment.setVoice();
        }

        //RoomManager.getInstance().useLoudSpeaker(true);
        closeSpeaker();
        if (isClassBegin) {
            if (RoomManager.getInstance().getMySelf() != null && !TextUtils.isEmpty(RoomManager.getInstance().getMySelf().peerId)) {
                doPlayVideo(RoomManager.getInstance().getMySelf().peerId);
            }
        }
    }

    public void closeSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (audioManager.isWiredHeadsetOn()) {
                    RoomManager.getInstance().useLoudSpeaker(false);
                } else {
                    RoomManager.getInstance().useLoudSpeaker(true);
                    openSpeaker();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final static String B_PHONE_STATE =
            TelephonyManager.ACTION_PHONE_STATE_CHANGED;

    private BroadcastReceiverMgr mBroadcastReceiver;

    //按钮1-注册广播
    public void registerIt() {
        mBroadcastReceiver = new BroadcastReceiverMgr();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(B_PHONE_STATE);
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    //按钮2-撤销广播
    public void unregisterIt() {
        unregisterReceiver(mBroadcastReceiver);
    }

    //点击通知进入一个Activity，点击返回时进入指定页面。
    public void resultActivityBackApp() {
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setTicker("南充教育教师端");
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setContentTitle(getString(R.string.app_name));
        mBuilder.setContentText(getString(R.string.back_hint));

        //设置点击一次后消失（如果没有点击事件，则该方法无效。）
        mBuilder.setAutoCancel(true);

        //点击通知之后需要跳转的页面
        Intent resultIntent = new Intent(this, RoomActivity.class);

        //使用TaskStackBuilder为“通知页面”设置返回关系
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //为点击通知后打开的页面设定 返回 页面。（在manifest中指定）
        //        stackBuilder.addParentStack(RoomActivity.class);
        //        stackBuilder.addNextIntent(resultIntent);

        PendingIntent pIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pIntent);
        // mId allows you to update the notification later on.
        //        nm.notify(2, mBuilder.build());
    }
}
