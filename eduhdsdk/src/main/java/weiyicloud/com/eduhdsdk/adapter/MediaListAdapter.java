package weiyicloud.com.eduhdsdk.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.classroomsdk.ShareDoc;
import com.classroomsdk.WhiteBoradManager;
import com.talkcloud.roomsdk.RoomManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weiyicloud.com.eduhdsdk.R;
import weiyicloud.com.eduhdsdk.ui.RoomActivity;

/**
 * Created by Administrator on 2017/5/29.
 */

public class MediaListAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<ShareDoc> mediaList = new ArrayList<ShareDoc>();
    private String roomNum;
    private long localfileid = -1;
    public static boolean isPublish = false;
    public static boolean isPlay = false;


    PopupWindow pop = null;

    public void setPop(PopupWindow pop) {
        this.pop = pop;
    }

    public void setLocalfileid(long localfileid) {
        this.localfileid = localfileid;
    }

    public MediaListAdapter(Activity context, ArrayList<ShareDoc> mediaList, String roomNum) {
        this.activity = context;
        this.mediaList = mediaList;
        this.roomNum = roomNum;
    }

    @Override
    public int getCount() {
        return mediaList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.file_list_item, null);
            holder.img_media_type = (ImageView) convertView.findViewById(R.id.img_file_type);
            holder.txt_media_name = (TextView) convertView.findViewById(R.id.txt_file_name);
            holder.img_play = (ImageView) convertView.findViewById(R.id.img_eye);
            holder.img_delete = (ImageView) convertView.findViewById(R.id.img_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (mediaList.size() > 0) {
            final ShareDoc media = mediaList.get(position);
            if (media != null) {
                holder.img_media_type.setImageResource(getMediaIcon(media.getFilename()));
                holder.txt_media_name.setText(media.getFilename());
                holder.img_play.setImageResource(R.drawable.btn_play_normal_list);
                holder.img_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (media.getFileid() == localfileid || RoomManager.isMediaPublishing) {
                            return;
                        }
                        if (pop != null) {
                            pop.dismiss();
                        }
                        RoomManager.isMediaPublishing = true;
                        isPlay = true;
                        if (localfileid != -1) {
                            RoomManager.getInstance().unPublishMedia();
                            WhiteBoradManager.getInstance().setCurrentMediaDoc(media);
                        } else {
                            localfileid = media.getFileid();
                            WhiteBoradManager.getInstance().setCurrentMediaDoc(media);
                            String strSwfpath = media.getSwfpath();
                            if (strSwfpath != null && !strSwfpath.isEmpty()) {
                                int pos = strSwfpath.lastIndexOf('.');
                                if (pos != -1) {
                                    strSwfpath = String.format("%s-%d%s", strSwfpath.substring(0, pos), 1, strSwfpath.substring(pos));
                                    String url = "http://" + WhiteBoradManager.getInstance().getFileServierUrl() + ":" + WhiteBoradManager.getInstance().getFileServierPort() + strSwfpath;
                                    if (RoomActivity.isClassBegin) {
                                        RoomManager.getInstance().publishMedia(url, isMp4(media.getFilename()), media.getFilename(), media.getFileid(), "__all");
                                    } else {
                                        RoomManager.getInstance().publishMedia(url, isMp4(media.getFilename()), media.getFilename(), media.getFileid(), RoomManager.getInstance().getMySelf().peerId);
                                    }
                                }
                            }
                        }


                    }
                });

                if (RoomActivity.myRrole == 0) {
                    holder.img_delete.setVisibility(View.VISIBLE);
                } else if (RoomActivity.myRrole == 2) {
                    holder.img_delete.setVisibility(View.GONE);
                }
                holder.img_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (pop != null) {
                            pop.dismiss();
                        }
                        if (media.getFileid() == localfileid) {
                            RoomManager.getInstance().unPublishMedia();
                        }
                        WhiteBoradManager.getInstance().delRoomFile(roomNum, media.getFileid(), media.isMedia(), RoomActivity.isClassBegin);
                    }
                });
            }
        }
        return convertView;
    }

    private boolean isMp4(String filename) {
        int icon = -1;
        if (filename.toLowerCase().endsWith("mp4") || filename.toLowerCase().endsWith("webm")) {
            return true;
        } else {
            return false;
        }
    }

    private int getMediaIcon(String filename) {
        int icon = -1;
        if (filename.toLowerCase().endsWith("mp4") || filename.toLowerCase().endsWith("webm")) {
            icon = R.drawable.icon_mp4;
        } else if (filename.toLowerCase().endsWith("mp3") || filename.toLowerCase().endsWith("wav") || filename.toLowerCase().endsWith("ogg")) {
            icon = R.drawable.icon_mp3;
        }
        return icon;
    }


    class ViewHolder {
        ImageView img_media_type;
        ImageView img_play;
        ImageView img_delete;
        TextView txt_media_name;
    }

}
