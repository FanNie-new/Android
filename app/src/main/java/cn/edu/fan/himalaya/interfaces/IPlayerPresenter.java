package cn.edu.fan.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import cn.edu.fan.himalaya.base.IBasePresenter;

//实现播放的接口
public interface IPlayerPresenter extends IBasePresenter<IPlayerCallback> {

    //播放
    void play();

    //暂停
    void pause();

    //停止播放
    void stop();

    //上一首
    void playPre();

    //下一首
    void playNext();

    //切换播放模式
    void switchPlayMode(XmPlayListControl.PlayMode mode);

    //获取播放列表
    void getPlayList();

    //根据节目的位置进行播放
    void playByIndex(int index);

    //切换播放进度
    void seekTo(int progress);

    // 判断播放器是否正在播放
    boolean isPlaying();

    //把播放列表内容反转
    void reversePlayList();

    //播放专辑的第一个节目
    void playByAlbumId(long id);
}
