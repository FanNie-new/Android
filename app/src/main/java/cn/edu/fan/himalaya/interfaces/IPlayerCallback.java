package cn.edu.fan.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

//播放的回调接口
public interface IPlayerCallback {

    //开始播放
    void onPlayStart();

    //播放暂停
    void onPlayPause();

    //播放停止
    void onPlayStop();

    //播放错误
    void onPlayError();

    //下一首播放
    void nextPlay(Track track);

    //上一首播放
    void PrePlay(Track track);

    //播放列表数据返回 list 播放列表数据
    void onListLoaded(List<Track> list);

    //切换播放模式
    void onPlayModeChange(XmPlayListControl.PlayMode playMode);

    //进度条的改变
    void onProgressChange(int currentProgress, int total);

    //广告正在加载
    void onAdLoading();

    //广告结束
    void onAdFinished();

    //更新当前节目
    void onTrackUpdate(Track track, int playIndex);

    //通知UI更新播放列表的顺序
    void updateListOrder(boolean isReverse);
}
