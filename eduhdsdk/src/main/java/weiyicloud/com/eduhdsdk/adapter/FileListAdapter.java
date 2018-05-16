package weiyicloud.com.eduhdsdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.classroomsdk.LocalControl;
import com.classroomsdk.Packager;
import com.classroomsdk.ShareDoc;
import com.classroomsdk.Tools;
import com.classroomsdk.WhiteBoradManager;
import com.talkcloud.roomsdk.RoomManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import weiyicloud.com.eduhdsdk.R;
import weiyicloud.com.eduhdsdk.ui.RoomActivity;

/**
 * Created by Administrator on 2017/5/28.
 */

public class FileListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ShareDoc> fileList = new ArrayList<ShareDoc>();
    private String roomNum;
    PopupWindow pop = null;

    public void setPop(PopupWindow pop) {
        this.pop = pop;
    }

    public FileListAdapter(Context context, ArrayList<ShareDoc> fileList, String Roomnum) {
        this.context = context;
        this.fileList = fileList;
        this.roomNum = Roomnum;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (fileList.size() > 0) {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.file_list_item, null);
                holder.img_file_type = (ImageView) convertView.findViewById(R.id.img_file_type);
                holder.txt_file_name = (TextView) convertView.findViewById(R.id.txt_file_name);
                holder.img_eye = (ImageView) convertView.findViewById(R.id.img_eye);
                holder.img_delete = (ImageView) convertView.findViewById(R.id.img_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final ShareDoc fileDoc = fileList.get(position);
            if (fileDoc != null) {
                if (fileDoc.getFileid() == 0) {
                    fileDoc.setFilename(context.getString(R.string.share_pad));
                }
                int icon = getFileIcon(fileDoc.getFilename());
                holder.img_file_type.setImageResource(icon);
                holder.txt_file_name.setText(fileDoc.getFilename());

                if (fileDoc.getFileid() == WhiteBoradManager.getInstance().getCurrentFileDoc().getFileid()) {
                    holder.img_eye.setImageResource(R.drawable.btn_eyes_02_normal);
                } else {
                    holder.img_eye.setImageResource(R.drawable.btn_eyes_01_normal);
                }
                if (RoomActivity.myRrole == 0) {
                    if (fileDoc.getFileid() == 0) {
                        holder.img_delete.setVisibility(View.GONE);
                    } else {
                        holder.img_delete.setVisibility(View.VISIBLE);
                    }
                } else if (RoomActivity.myRrole == 2) {
                    holder.img_delete.setVisibility(View.GONE);
                }
                holder.img_eye.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (fileDoc.getFileid() == WhiteBoradManager.getInstance().getCurrentFileDoc().getFileid()) {
                            return;
                        }
                        WhiteBoradManager.getInstance().localChangeDoc(fileDoc);
                        notifyDataSetChanged();
                        if (RoomActivity.isClassBegin) {
                            JSONObject data = new JSONObject();
                            data = Packager.pageSendData(fileDoc);
                            RoomManager.getInstance().pubMsg("ShowPage", "DocumentFilePage_ShowPage", "__allExceptSender", data.toString(), true,null,null);

                        }
                        if (pop != null) {
                            pop.dismiss();
                        }
                    }
                });
                holder.img_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (pop != null) {
                            pop.dismiss();
                        }
                        WhiteBoradManager.getInstance().delRoomFile(roomNum, fileDoc.getFileid(), fileDoc.isMedia(), RoomActivity.isClassBegin);
                    }
                });
            }
        }
        return convertView;
    }

    private int getFileIcon(String filename) {
        int icon = -1;
        if (filename == null && filename.isEmpty()) {
            icon = R.drawable.icon_empty;
        }
        if (filename.toLowerCase().endsWith(".pptx") || filename.toLowerCase().endsWith(".ppt") || filename.toLowerCase().endsWith(".pps")) {
            icon = R.drawable.icon_ppt;
        } else if (filename.toLowerCase().endsWith(".docx") || filename.toLowerCase().endsWith(".doc")) {
            icon = R.drawable.icon_word;
        } else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")
                || filename.toLowerCase().endsWith(".png") || filename.toLowerCase().endsWith(".gif")
                || filename.toLowerCase().endsWith(".bmp")) {
            icon = R.drawable.icon_images;
        } else if (filename.toLowerCase().endsWith(".xls") || filename.toLowerCase().endsWith(".xlsx")
                || filename.toLowerCase().endsWith(".xlt") || filename.toLowerCase().endsWith("xlsm")) {
            icon = R.drawable.icon_excel;
        } else if (filename.toLowerCase().endsWith(".pdf")) {
            icon = R.drawable.icon_pdf;
        } else if (filename.equals(context.getResources().getString(R.string.share_pad))) {
            icon = R.drawable.icon_empty;
        } else if (filename.toLowerCase().endsWith(".txt")) {
            icon = R.drawable.icon_text_pad;
        } else if (filename.toLowerCase().endsWith(".zip")) {
            icon = R.drawable.icon_h5;
        }
        return icon;
    }

    class ViewHolder {
        ImageView img_file_type;
        TextView txt_file_name;
        ImageView img_eye;
        ImageView img_delete;
    }


}
