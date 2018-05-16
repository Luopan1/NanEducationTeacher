package weiyicloud.com.eduhdsdk.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.talkcloud.roomsdk.RoomManager;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import weiyicloud.com.eduhdsdk.R;
import weiyicloud.com.eduhdsdk.entity.ChatData;
import weiyicloud.com.eduhdsdk.tools.Translate;


/**
 * Created by Administrator on 2017/4/28.
 */

public class ChatListAdapter extends BaseAdapter {
    private ArrayList<ChatData> chatlist;
    private Context context;

    public ChatListAdapter(ArrayList<ChatData> chatlist, Context context) {
        this.chatlist = chatlist;
        this.context = context;
    }

    @Override
    public int getCount() {
        return chatlist.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_list_item, null);
            holder.lin_system = (LinearLayout) convertView.findViewById(R.id.lin_system);
            holder.lin_left = (LinearLayout) convertView.findViewById(R.id.lin_left);
            holder.lin_right = (LinearLayout) convertView.findViewById(R.id.lin_right);
            holder.txt_mem_name = (TextView) convertView.findViewById(R.id.txt_mem_name);
            holder.txt_state = (TextView) convertView.findViewById(R.id.txt_state);
           /* holder.txt_degree = (TextView) convertView.findViewById(R.id.txt_degree);*/

            holder.txt_name_left = (TextView) convertView.findViewById(R.id.txt_name_left);
            holder.txt_identity_left = (TextView) convertView.findViewById(R.id.txt_identity_left);
            holder.txt_ts_left = (TextView) convertView.findViewById(R.id.txt_ts_left);
            holder.txt_msg_left = (TextView) convertView.findViewById(R.id.txt_msg_left);
            holder.img_translation_left = (ImageView) convertView.findViewById(R.id.img_translation_left);
            holder.txt_trans_left = (TextView) convertView.findViewById(R.id.txt_trans_left);

            holder.txt_name_right = (TextView) convertView.findViewById(R.id.txt_name_right);
            holder.txt_identity_right = (TextView) convertView.findViewById(R.id.txt_identity_right);
            holder.txt_ts_right = (TextView) convertView.findViewById(R.id.txt_ts_right);
            holder.txt_msg_right = (TextView) convertView.findViewById(R.id.txt_msg_right);
            holder.img_translation_right = (ImageView) convertView.findViewById(R.id.img_translation_right);
            holder.txt_trans_right = (TextView) convertView.findViewById(R.id.txt_trans_right);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (chatlist.size() > 0) {
            final ChatData ch = chatlist.get(position);
            if (ch != null && ch.getUser() != null) {
                holder.lin_system.setVisibility(View.GONE);
                holder.lin_left.setVisibility(View.GONE);
                holder.lin_right.setVisibility(View.GONE);
               /* if (ch.getUser().role == 0) {
                    holder.txt_degree.setText("(" + context.getResources().getString(R.string.teacher) + ")");
                } else if (ch.getUser().role == 2) {
                    holder.txt_degree.setText("(" + context.getResources().getString(R.string.student) + ")");
                } else if (ch.getUser().role == 4) {
                    holder.txt_degree.setText("(" + context.getResources().getString(R.string.lass_patrol) + ")");
                } else if (ch.getUser().role == 1) {
                    holder.txt_degree.setText("(" + context.getResources().getString(R.string.assistant) + ")");
                }*/
                if (ch.isStystemMsg()) {
                    holder.lin_system.setVisibility(View.VISIBLE);
                    holder.txt_mem_name.setText(ch.getUser().nickName);
                    if (ch.getState() == 1) {
                        holder.txt_state.setText(ch.isInOut() ? R.string.join : R.string.leave);
                    } else {
                        String strIsHold = ch.isHold() ? context.getString(R.string.back_msg) : context.getString(R.string.re_back_msg);
                        String temp = (String) ch.getUser().properties.get("devicetype");
                        strIsHold = temp + strIsHold;
                        holder.txt_state.setText(strIsHold);
                    }
                } else {
                    if (ch.getUser().peerId.equals(RoomManager.getInstance().getMySelf().peerId)) {
                        holder.lin_right.setVisibility(View.VISIBLE);
                        ch.getUser().nickName = StringEscapeUtils.unescapeHtml4(ch.getUser().nickName);
                        holder.txt_name_right.setText(ch.getUser().nickName);
                        holder.txt_identity_right.setText("（" + context.getResources().getString(R.string.me) + "）");
                        holder.txt_ts_right.setText(ch.getTime());
                        holder.txt_msg_right.setText(ch.getMessage());

                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Translate.getInstance().translate(position, ch.getMessage());
                            }
                        });
                        holder.txt_trans_right.setText(ch.getTrans());
                        if (ch.isTrans()) {
                            holder.img_translation_right.setClickable(false);
                            holder.txt_trans_right.setVisibility(View.VISIBLE);
                        } else {
                            holder.img_translation_right.setClickable(true);
                            holder.txt_trans_right.setVisibility(View.GONE);
                        }
                    } else {
                        holder.lin_left.setVisibility(View.VISIBLE);
                        ch.getUser().nickName = StringEscapeUtils.unescapeHtml4(ch.getUser().nickName);
                        holder.txt_name_left.setText(ch.getUser().nickName);
                        if (ch.getUser().role == 0) {
                            holder.txt_identity_left.setVisibility(View.VISIBLE);
                            holder.txt_identity_left.setText("（" + context.getResources().getString(R.string.teacher) + "）");
                        } else {
                            holder.txt_identity_left.setVisibility(View.GONE);
                        }
                        holder.txt_ts_left.setText(ch.getTime());
                        holder.txt_msg_left.setText(ch.getMessage());

                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Translate.getInstance().translate(position, ch.getMessage());
                            }
                        });
                        holder.txt_trans_left.setText(ch.getTrans());
                        if (ch.isTrans()) {
                            holder.img_translation_left.setClickable(false);
                            holder.txt_trans_left.setVisibility(View.VISIBLE);
                        } else {
                            holder.img_translation_left.setClickable(true);
                            holder.txt_trans_left.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }

        return convertView;
    }

    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    class ViewHolder {
        LinearLayout lin_system;
        LinearLayout lin_left;
        LinearLayout lin_right;
        TextView txt_mem_name;
        TextView txt_state;
        /*TextView txt_degree;*/

        TextView txt_name_left;
        TextView txt_identity_left;
        TextView txt_ts_left;
        TextView txt_msg_left;
        ImageView img_translation_left;
        TextView txt_trans_left;

        TextView txt_name_right;
        TextView txt_identity_right;
        TextView txt_ts_right;
        TextView txt_msg_right;
        ImageView img_translation_right;
        TextView txt_trans_right;

    }
}
