package weiyicloud.com.eduhdsdk.entity;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.webrtc.SurfaceViewRenderer;

/**
 * Created by Administrator on 2017/5/22.
 */

public class VideoItem {
    public SurfaceViewRenderer sf_video;
    public ImageView img_cam;
    public ImageView img_mic;
    public ImageView img_pen;
    public ImageView img_hand;
    public TextView txt_name;
    public TextView txt_gift_num;
    public RelativeLayout rel_group;
    public ImageView img_video_back;
    public LinearLayout lin_gift;
    public LinearLayout parent;
    public LinearLayout lin_name_label;
    public String peerid = "";
    public RelativeLayout rel_video_label;
    public boolean canMove = false;
    public boolean isMoved = false;
    public boolean isSefMoved = false;
    public boolean isLayoutd = false;
    public boolean isZoomd = false;
    public float oldX = 0;
    public float oldY = 0;
    public float newX = 0;
    public float newY = 0;
//    public void scale(float scale){
////        ViewHelper.setScaleX(sf_video,scale);
////        ViewHelper.setScaleY(sf_video,scale);
////        ViewHelper.setScaleX(rel_group,scale);
////        ViewHelper.setScaleY(rel_group,scale);
////        ViewHelper.setScaleX(img_video_back,scale);
////        ViewHelper.setScaleY(img_video_back,scale);
//        ViewHelper.setScaleX(parent,scale);
//        ViewHelper.setScaleY(parent,scale);
////        ViewHelper.setScaleX(lin_name_label,scale);
////        ViewHelper.setScaleY(lin_name_label,scale);
////        ViewHelper.setScaleX(rel_video_label,scale);
////        ViewHelper.setScaleY(rel_video_label,scale);
//
//    }
}
