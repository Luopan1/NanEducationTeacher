package weiyicloud.com.eduhdsdk.ui;

import android.app.Service;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.classroomsdk.ShareDoc;
import com.classroomsdk.WhiteBoradManager;
import com.talkcloud.roomsdk.RoomManager;
import com.talkcloud.roomsdk.Stream;

import org.webrtc.EglBase;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import weiyicloud.com.eduhdsdk.R;
import weiyicloud.com.eduhdsdk.adapter.MediaListAdapter;

import weiyicloud.com.eduhdsdk.tools.Tools;


public class VideoFragment extends Fragment {

    private static String sync = "";
    static private VideoFragment mInstance = null;
    private View fragmentView;
    //MP4视频
    LinearLayout lin_video_play;
    SurfaceViewRenderer suf_mp4;
    ImageView img_close_mp4;
    LinearLayout lin_video_control;
    ImageView img_play_mp4;
    TextView txt_mp4_name;
    TextView txt_mp4_time;
    SeekBar sek_mp4;
    ImageView img_voice_mp4;
    SeekBar sek_voice_mp4;
    ShareDoc currentDoc;
    AudioManager audioManager;
    long localFileId;
    boolean isStoped = false;
    boolean iscreate = true;
    boolean isPlay = true;
    private Stream stream;
    private double vol = 0.5;
    private boolean isMute = false;

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    static public VideoFragment getInstance() {
        synchronized (sync) {
            if (mInstance == null) {
                mInstance = new VideoFragment();
            }
            return mInstance;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (fragmentView == null) {
            fragmentView = inflater.inflate(R.layout.fragment_video, null);
            fragmentView.bringToFront();
            //mp4
            lin_video_play = (LinearLayout) fragmentView.findViewById(R.id.lin_video_play);
            suf_mp4 = (SurfaceViewRenderer) fragmentView.findViewById(R.id.suf_mp4);
            suf_mp4.init(EglBase.create().getEglBaseContext(), null);
//            suf_mp4.setZOrderOnTop(true);
            suf_mp4.setZOrderMediaOverlay(true);

            img_close_mp4 = (ImageView) fragmentView.findViewById(R.id.img_close_mp4);
            lin_video_control = (LinearLayout) fragmentView.findViewById(R.id.lin_video_control);
            img_play_mp4 = (ImageView) fragmentView.findViewById(R.id.img_play_mp4);
            txt_mp4_name = (TextView) fragmentView.findViewById(R.id.txt_mp4_name);
            txt_mp4_time = (TextView) fragmentView.findViewById(R.id.txt_mp4_time);
            sek_mp4 = (SeekBar) fragmentView.findViewById(R.id.sek_mp4);
            img_voice_mp4 = (ImageView) fragmentView.findViewById(R.id.img_voice_mp4);
            sek_voice_mp4 = (SeekBar) fragmentView.findViewById(R.id.sek_voice_mp4);

        } else {
            ViewGroup parent = (ViewGroup) fragmentView.getParent();
            if (parent != null) {
                parent.removeView(fragmentView);
            }
        }
        audioManager = (AudioManager) getActivity().getSystemService(Service.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 75, 0);
//        mediaPlay();
        return fragmentView;
    }

    public void setVoice() {
        if (img_voice_mp4 != null && sek_voice_mp4 != null) {
            if (isMute) {
                RoomManager.getInstance().setRemoteStreamAudioVolume(0);
                img_voice_mp4.setImageResource(R.drawable.btn_mute_pressed);
                sek_voice_mp4.setProgress(0);
            } else {
                RoomManager.getInstance().setRemoteStreamAudioVolume(vol);
                img_voice_mp4.setImageResource(R.drawable.btn_volume_pressed);
                sek_voice_mp4.setProgress((int) (vol * 100));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (stream != null) {
            suf_mp4.setEnableHardwareScaler(true);
            suf_mp4.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            RoomManager.getInstance().playVideo(stream.getExtensionId(), suf_mp4);
            suf_mp4.requestLayout();
            if (txt_mp4_name != null) {
                txt_mp4_name.setText((String) stream.attrMap.get("filename"));
            }
        }
        lin_video_play.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        if (RoomActivity.myRrole == 0) {
            lin_video_control.setVisibility(View.VISIBLE);
            img_close_mp4.setVisibility(View.VISIBLE);
        } else {
            lin_video_control.setVisibility(View.INVISIBLE);
            img_close_mp4.setVisibility(View.INVISIBLE);
        }
        img_close_mp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoomManager.getInstance().unPublishMedia();
//                ((RoomActivity)getActivity()).removeVideoFragment();
            }
        });
        img_play_mp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MediaListAdapter.isPublish) {
                    RoomManager.getInstance().playMedia((Boolean) stream.attrMap.get("pause") == null ? false : (Boolean) stream.attrMap.get("pause"));
                } else {
                    ShareDoc media = WhiteBoradManager.getInstance().getCurrentMediaDoc();
                    WhiteBoradManager.getInstance().setCurrentMediaDoc(media);
                    String strSwfpath = media.getSwfpath();
                    int pos = strSwfpath.lastIndexOf('.');
                    strSwfpath = String.format("%s-%d%s", strSwfpath.substring(0, pos), 1, strSwfpath.substring(pos));
                    String url = "http://" + WhiteBoradManager.getInstance().getFileServierUrl() + ":" + WhiteBoradManager.getInstance().getFileServierPort() + strSwfpath;
                    if (RoomActivity.isClassBegin) {
                        RoomManager.getInstance().publishMedia(url, com.classroomsdk.Tools.isMp4(media.getFilename()), media.getFilename(), media.getFileid(), "__all");
                    } else {
                        RoomManager.getInstance().publishMedia(url, com.classroomsdk.Tools.isMp4(media.getFilename()), media.getFilename(), media.getFileid(), RoomManager.getInstance().getMySelf().peerId);
                    }
                }
            }
        });
        sek_mp4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        RoomManager.getInstance().setRemoteStreamAudioVolume(vol);
        img_voice_mp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMute) {
                    RoomManager.getInstance().setRemoteStreamAudioVolume(vol);
                    img_voice_mp4.setImageResource(R.drawable.btn_volume_pressed);
                    sek_voice_mp4.setProgress((int) (vol * 100));
                } else {
                    RoomManager.getInstance().setRemoteStreamAudioVolume(0);
                    img_voice_mp4.setImageResource(R.drawable.btn_mute_pressed);
                    sek_voice_mp4.setProgress(0);
                }
                isMute = !isMute;
            }
        });
        sek_voice_mp4.setProgress((int) (vol * 100));
        sek_voice_mp4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float vol = (float) progress / (float) seekBar.getMax();
                if (vol > 0) {
                    img_voice_mp4.setImageResource(R.drawable.btn_volume_pressed);
                } else {
                    img_voice_mp4.setImageResource(R.drawable.btn_mute_pressed);
                }
                RoomManager.getInstance().setRemoteStreamAudioVolume(vol);
                if (fromUser) {
                    VideoFragment.this.vol = vol;
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

    public void controlMedia(Stream stream, long pos, boolean isPlay) {
        if (sek_mp4 != null) {
            int curtime = (int) ((double) pos / (int) stream.attrMap.get("duration") * 100);
            sek_mp4.setProgress(curtime);

        }
        if (img_play_mp4 != null) {
            if (!isPlay) {
                img_play_mp4.setImageResource(R.drawable.btn_pause_normal);
            } else {
                img_play_mp4.setImageResource(R.drawable.btn_play_normal);
            }
        }
        if (txt_mp4_time != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("mm:ss ");
            Date curDate = new Date(pos);//获取当前时间
            Date daDate = new Date((int) stream.attrMap.get("duration"));
            String strcur = formatter.format(curDate);
            String strda = formatter.format(daDate);
            txt_mp4_time.setText(strcur + "/" + strda);
        }
        if (txt_mp4_name != null) {
            txt_mp4_name.setText((String) stream.attrMap.get("filename"));
        }
    }

    @Override
    public void onStop() {

        isStoped = true;
        super.onStop();
    }

    @Override
    public void onDestroyView() {
//        RoomManager.getInstance().unPlayVideo(stream.getExtensionId());
        suf_mp4.release();
        isMute = false;
        suf_mp4 = null;
        super.onDestroyView();
        mInstance = null;
    }
}
