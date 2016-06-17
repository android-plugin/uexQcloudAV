package org.zywx.wbpalmstar.plugin.uexqcloudav;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Message;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.tencent.qcload.playersdk.ui.UiChangeInterface;
import com.tencent.qcload.playersdk.ui.VideoRootFrame;
import com.tencent.qcload.playersdk.util.PlayerListener;
import com.tencent.qcload.playersdk.util.VideoInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.DataHelper;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.plugin.uexqcloudav.vo.OpenVO;
import org.zywx.wbpalmstar.plugin.uexqcloudav.vo.VideoInfoVO;

import java.util.ArrayList;
import java.util.List;

public class EUExQcloudAV extends EUExBase {

    OpenVO mOpenVO;
    VideoRootFrame mPlayer;

    public EUExQcloudAV(Context context, EBrowserView eBrowserView) {
        super(context, eBrowserView);
    }

    @Override
    protected boolean clean() {
        if (mPlayer!=null){
            mPlayer.release();
            mPlayer=null;
        }
        return false;
    }
    

    @Override
    public void onHandleMessage(Message message) {
        if(message == null){
            return;
        }
        Bundle bundle=message.getData();
        switch (message.what) {

        default:
                super.onHandleMessage(message);
        }
    }

    public void open(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        mOpenVO = DataHelper.gson.fromJson(params[0],OpenVO.class);
        addPlayerView(mOpenVO);
        JSONObject jsonResult = new JSONObject();
        try {
            jsonResult.put("", "");
        } catch (JSONException e) {
        }
        callBackPluginJs(JsConst.CALLBACK_OPEN, jsonResult.toString());
    }

    private void addPlayerView(OpenVO openVO){
        if (openVO==null){
            return;
        }
        if (mPlayer ==null) {
            mPlayer = new VideoRootFrame(mContext);
        }
        RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(openVO.getWidth(),
                openVO.getHeight());
        lp.leftMargin= openVO.getX();
        lp.topMargin= openVO.getY();
        addViewToCurrentWindow(mPlayer,lp);
        List<VideoInfo> videos=new ArrayList<VideoInfo>();

        for (VideoInfoVO videoInfoVO:openVO.getData()){
            videos.add(transVideoInfo(videoInfoVO));
        }

        mPlayer.play(videos);
        mPlayer.seekTo(openVO.getStartSeconds());
        mPlayer.setToggleFullScreenHandler(new UiChangeInterface() {
            @Override
            public void OnChange() {
                if (mPlayer.isFullScreen()) {
                    //播放器全屏时，将页面设置为竖屏状态
                    ((ViewGroup)mPlayer.getParent()).removeView(mPlayer);
                    addPlayerView(mOpenVO);
                    ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    exitFullScreen();
                } else {
                    //播放器非全屏时，将页面设置为横屏状态，此时播放器控件宽度自适应到屏幕宽度实现全屏
                    ((ViewGroup)mPlayer.getParent()).removeView(mPlayer);

                    RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    lp.leftMargin=0;
                    lp.topMargin=0;
                    addViewToCurrentWindow(mPlayer,lp);
                    ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    enterFullScreen();
                }
            }
        });
        mPlayer.setListener(new PlayerListener() {
            @Override
            public void onError(Exception e) {
                callBackPluginInt(JsConst.ON_STATE_CHANGED,-1);
            }

            @Override
            public void onStateChanged(int i) {
                int state=getCallbackState(i);
                callBackPluginInt(JsConst.ON_STATE_CHANGED,state);
            }
        });


    }

    /**
     * 把sdk返回的state转成插件定义的state
     * @param state 返回码
     * 1 STATE_IDLE 播放器空闲，既不在准备也不在播放
     * 2 STATE_PREPARING 播放器正在准备
     * 3 STATE_BUFFERING 播放器已经准备完毕，但无法立即播放。此状态的原因有很多，但常见的是播放器需要缓冲更多数据才能开始播放
     * 4 STATE_PAUSE 播放器准备好并可以立即播放当前位置
     * 5 STATE_PLAY 播放器正在播放中
     * 6 STATE_ENDED 播放已完毕
     * @return
     */
    private int getCallbackState(int state){
        switch (state){
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 4;
            case 5:
                return 3;
            case 6:
                return 5;
            default:
                return 0;
        }
    }

    //全屏
    public void enterFullScreen() {
        ((Activity) mContext).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    //退出全屏
    public void exitFullScreen() {
        ((Activity) mContext).getWindow().setFlags(~WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private VideoInfo transVideoInfo(VideoInfoVO videoInfoVO){
        VideoInfo videoInfo=new VideoInfo();
        videoInfo.url=videoInfoVO.getUrl();
        videoInfo.description=videoInfoVO.getDesc();
        videoInfo.type=getRealType(videoInfoVO.getVideoType());
        return videoInfo;
    }

    private VideoInfo.VideoType getRealType(int type){
        switch (type){
            case 0:
                return VideoInfo.VideoType.HLS;
            case 1:
                return VideoInfo.VideoType.MP4;
            case 2:
                return VideoInfo.VideoType.MP3;
            case 3:
                return VideoInfo.VideoType.AAC;
            case 4:
                return VideoInfo.VideoType.FMP4;
            case 5:
                return VideoInfo.VideoType.WEBM;
            case 6:
                return VideoInfo.VideoType.MKV;
            case 7:
                return VideoInfo.VideoType.TS;
            default:return VideoInfo.VideoType.MP4;
        }
    }

    public void play(String[] params) {
        if (mPlayer!=null){
            mPlayer.play();
        }
    }

    public void pause(String[] params) {
        if (mPlayer!=null){
            mPlayer.pause();
        }
    }

    public void stop(String[] params){
        if (mPlayer!=null){
            mPlayer.pause();
            mPlayer.seekTo(0);
        }
    }

    public void close(String[] params) {
       if (mPlayer!=null){
           removeViewFromCurrentWindow(mPlayer);
           mPlayer.release();
           mPlayer=null;
       }
    }

    public void clear(String[] params) {
      if (mPlayer!=null){

      }
    }

    public int getCurrentTime(String[] params) {
        if (mPlayer==null){
            return 0;
        }
        int currentTime=mPlayer.getCurrentTime();
        callBackPluginInt(JsConst.CALLBACK_GET_CURRENT_TIME, currentTime);
        return currentTime;
    }

    public void seekTo(String[] params) {
        try {
            int  time = Integer.valueOf(params[0]);
            if (mPlayer!=null){
                mPlayer.seekTo(time);
            }
        }catch (Exception e){

        }

    }

    private void callBackPluginJs(String methodName, String jsonData){
        String js = SCRIPT_HEADER + "if(" + methodName + "){"
                + methodName + "('" + jsonData + "');}";
        onCallback(js);
    }

    private void callBackPluginInt(String methodName, int jsonData){
        String js = SCRIPT_HEADER + "if(" + methodName + "){"
                + methodName + "(" + jsonData + ");}";
        onCallback(js);
    }


}
