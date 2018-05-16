package com.classroomsdk;

/**
 * Created by Administrator on 2017/5/11.
 */

public interface WBCallback {

    void sendBoardData(String js);

    void deleteBoardData(String js);

    void onPageFinished();

    void fullScreenToLc(boolean isFull);

    void onJsPlay(String url,boolean isvideo,long fileid);
}
