package cn.edu.fan.himalaya.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.edu.fan.himalaya.data.XimalayaApi;
import cn.edu.fan.himalaya.base.BaseApplication;
import cn.edu.fan.himalaya.interfaces.IPlayerCallback;
import cn.edu.fan.himalaya.interfaces.IPlayerPresenter;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;


//播放器的逻辑实现
public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private static final String TAG = "PlayerPresenter";
    private List<IPlayerCallback> mIPlayerCallbacks = new ArrayList<>();
    private final XmPlayerManager mXmPlayerManager;
    private Track mCurrentTrack;
    public static final int DEFAULT_PLAY_INDEX = 0;
    private int mCurrentIndex = DEFAULT_PLAY_INDEX;
    private final SharedPreferences mPlayModeSp;
    private XmPlayListControl.PlayMode mCurrentPlayMode = PLAY_MODEL_LIST;
    private boolean mIsReverse = false;

    public static final int PLAY_MODEL_LIST_INT = 0;
    public static final int PLAY_MODEL_LIST_LOOP_INT = 1;
    public static final int PLAY_MODEL_RANDOM_INT = 2;
    public static final int PLAY_MODEL_SINGLE_LOOP_INT = 3;

    //sp's key and name
    public static final String PLAY_MODE_SP_NAME = "PlayMode";
    public static final String PLAY_MODE_SP_KEY = "currentPlayMode";
    private int mCurrentProgressPosition = 0;
    private int mProgressDuration = 0;

    private PlayerPresenter(){
        //得到喜马拉雅实例
        mXmPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //广告相关的接口
        mXmPlayerManager.addAdsStatusListener(this);
        //注册播放器相关的接口
        mXmPlayerManager.addPlayerStatusListener(this);
        //需要记录当前的播放模式
        mPlayModeSp = BaseApplication.getAppContext().getSharedPreferences(PLAY_MODE_SP_NAME, Context.MODE_PRIVATE);
    }

    private static PlayerPresenter sPlayerPresenter;

    public static PlayerPresenter getPlayerPresenter() {
        if(sPlayerPresenter == null) {
            synchronized(PlayerPresenter.class) {
                if(sPlayerPresenter == null) {
                    sPlayerPresenter = new PlayerPresenter();
                }
            }
        }
        return sPlayerPresenter;
    }


    private boolean isPlayListSet = false;

    public void setPlayList(List<Track> list,int playIndex) {
        if(mXmPlayerManager != null) {
            mXmPlayerManager.setPlayList(list,playIndex);
            isPlayListSet = true;
            mCurrentTrack = list.get(playIndex);
            mCurrentIndex = playIndex;
        } else {
            Log.d(TAG,"mPlayerManager is null");
        }
    }

    @Override
    public void play() {
        if(isPlayListSet) {
            mXmPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if (mXmPlayerManager != null) {
            mXmPlayerManager.pause();
        }

    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {
        //播放前一首
        if (mXmPlayerManager != null) {
            mXmPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        //播放下一首
        if (mXmPlayerManager != null) {
            mXmPlayerManager.playNext();
        }
    }

    //判断是否有播放的节目列表
    public boolean hasPlayList(){
        return isPlayListSet;
    }
    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        if (mXmPlayerManager != null) {
            mCurrentPlayMode = mode;
            mXmPlayerManager.setPlayMode(mode);
            //通知UI更新播放模式
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onPlayModeChange(mode);
            }
            //保存到sp里面去
            SharedPreferences.Editor edit =  mPlayModeSp.edit();
            edit.putInt(PLAY_MODE_SP_KEY,getIntByPlayMode(mode));
            edit.commit();
        }
    }

    private int getIntByPlayMode(XmPlayListControl.PlayMode mode) {
        switch(mode) {
            case PLAY_MODEL_SINGLE_LOOP:
                return PLAY_MODEL_SINGLE_LOOP_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODEL_LIST_LOOP_INT;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODEL_RANDOM_INT;
            case PLAY_MODEL_LIST:
                return PLAY_MODEL_LIST_INT;
        }

        return PLAY_MODEL_LIST_INT;
    }

    private XmPlayListControl.PlayMode getModeByInt(int index) {
        switch(index) {
            case PLAY_MODEL_SINGLE_LOOP_INT:
                return PLAY_MODEL_SINGLE_LOOP;
            case PLAY_MODEL_LIST_LOOP_INT:
                return PLAY_MODEL_LIST_LOOP;
            case PLAY_MODEL_RANDOM_INT:
                return PLAY_MODEL_RANDOM;
            case PLAY_MODEL_LIST_INT:
                return PLAY_MODEL_LIST;
        }
        return PLAY_MODEL_LIST;
    }
    @Override
    public void getPlayList() {
        if (mXmPlayerManager != null) {
            List<Track> playList = mXmPlayerManager.getPlayList();
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onListLoaded(playList);
            }
        }
//        mXmPlayerManager.getCommonTrackList();
    }

    @Override
    public void playByIndex(int index) {
        //切换播放器到第index的位置进行播放
        if (mXmPlayerManager != null) {
            mXmPlayerManager.play(index);
        }
    }

    @Override
    public void seekTo(int progress) {
        //更新播放器的进度
        mXmPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlaying() {
        //返回当前是否正在播放
        return mXmPlayerManager.isPlaying();
    }

    @Override
    public void reversePlayList() {
        //把播放列表反转
        List<Track> playList = mXmPlayerManager.getPlayList();
        Collections.reverse(playList);
        mIsReverse = !mIsReverse;

        //第一参数是播放列表，第二个参数是播放的下标
        //新的下标 = 总的内容个数 - 1 - 当前的下标
        mCurrentIndex = playList.size() - 1 - mCurrentIndex;
        mXmPlayerManager.setPlayList(playList,mCurrentIndex);
        //更新UI
        mCurrentTrack = (Track) mXmPlayerManager.getCurrSound();
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onListLoaded(playList);
            iPlayerCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
            iPlayerCallback.updateListOrder(mIsReverse);
        }
    }

    @Override
    public void playByAlbumId(long id) {
        //1、要获取的转接的列表内容
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                //2、把专辑内容设置给播放器
                List<Track> tracks = trackList.getTracks();
                if (trackList != null && tracks.size() > 0) {
                    mXmPlayerManager.setPlayList(trackList,DEFAULT_PLAY_INDEX);
                    isPlayListSet = true;
                    mCurrentTrack = tracks.get(DEFAULT_PLAY_INDEX);
                    mCurrentIndex = DEFAULT_PLAY_INDEX;
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                Log.d(TAG,"errorCode --->" + errorCode);
                Log.d(TAG,"errorMsg --->" + errorMsg);
                Toast.makeText(BaseApplication.getAppContext(),"请求失败！",Toast.LENGTH_SHORT).show();
            }
        },(int)id,1);
        //3、播放了

    }

    //注册
    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {

        //添加
        if (!mIPlayerCallbacks.contains(iPlayerCallback)) {
            mIPlayerCallbacks.add(iPlayerCallback);
        }
        //更新之前，让UI的Pager有数据
        getPlayList();
        //通知当前的节目
        iPlayerCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
        //更新进度条
        iPlayerCallback.onProgressChange(mCurrentProgressPosition,mProgressDuration);
        //更新状态
        handlePlayState(iPlayerCallback);
        //从sp里获取
        int modeIndex = mPlayModeSp.getInt(PLAY_MODE_SP_KEY,PLAY_MODEL_LIST_INT);
        mCurrentPlayMode = getModeByInt(modeIndex);
        iPlayerCallback.onPlayModeChange(mCurrentPlayMode);

    }

    private void handlePlayState(IPlayerCallback iPlayerCallback) {
        int playerStatus = mXmPlayerManager.getPlayerStatus();
        //根据状态调用接口的方法
        if (PlayerConstants.STATE_STARTED == playerStatus) {
            iPlayerCallback.onPlayStart();
        }else{
            iPlayerCallback.onPlayStop();
        }
    }

    //取消注册
    @Override
    public void unRegisterViewCallback(IPlayerCallback iPlayerCallback) {
        //remove
        mIPlayerCallbacks.remove(iPlayerCallback);
    }

    //4.7 播放器回调
    // ---  广告相关的回调方法 start  ---
    @Override
    public void onStartGetAdsInfo() {
        Log.d(TAG,"onStartGetAdsInfo ---开始获取广告物料");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        Log.d(TAG,"onGetAdsInfo ---获取广告物料成功");
    }

    @Override
    public void onAdsStartBuffering() {
        Log.d(TAG,"onAdsStartBuffering ---广告开始缓冲");
    }

    @Override
    public void onAdsStopBuffering() {
        Log.d(TAG,"onAdsStopBuffering ---广告结束缓冲");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        Log.d(TAG,"onStartPlayAds ---开始播放广告");
    }

    @Override
    public void onCompletePlayAds() {
        Log.d(TAG,"onCompletePlayAds ---onCompletePlayAds()");
    }

    @Override
    public void onError(int what, int extra) {
        Log.d(TAG,"onError ---播放广告错误 what =>" + what + "extra =>" + extra);
    }
    // ---  广告相关的回调方法 end  ---

    // --- 播放器相关的回调方法 start ---
    @Override
    public void onPlayStart() {
        Log.d(TAG,"onPlayStart 开始播放");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        Log.d(TAG,"onPlayPause 暂停播放");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        Log.d(TAG,"onPlayStop 停止播放");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayStop();
        }
    }

    @Override
    public void onSoundPlayComplete() {
        Log.d(TAG,"onSoundPlayComplete 播放完成");
    }

    @Override
    public void onSoundPrepared() {
        Log.d(TAG,"onSoundPrepared 播放器准备完毕");
        mXmPlayerManager.setPlayMode(mCurrentPlayMode);
        if (mXmPlayerManager.getPlayerStatus() == PlayerConstants.STATE_PREPARED) {
            //播哦发情期准备完成，可以播放了
            mXmPlayerManager.play();
        }
    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel1) {
        Log.d(TAG,"onSoundSwitch 切歌 ");
        if (lastModel != null) {
            Log.d(TAG,"lastModel --->" + lastModel.getKind());
        }
        if (curModel1 != null) {
            Log.d(TAG,"curModel1 --->" + curModel1.getKind());
        }
        //curModel1代表的是当前播放的内容
        //通过getKind()方法来获取它是什么类型的
        // rack、radio和schedule；
        // 1、
//        if ("rack".equals(curModel1.getKind())) {
//            Track currentTrack = (Track) curModel1;
//            Log.d(TAG,"title --->" + currentTrack.getTrackTitle());
//        }
        //2、
        mCurrentIndex = mXmPlayerManager.getCurrentIndex();
        if (curModel1 instanceof Track) {
            Track currentTrack = (Track) curModel1;
            mCurrentTrack = currentTrack;
            //保存播放记录
            HistoryPresenter historyPresenter = HistoryPresenter.getHistoryPresenter();
            historyPresenter.addHistory(currentTrack);
            Log.d(TAG,"title ==>" + mCurrentTrack);
            //更新UI
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
            }
        }
    }

    @Override
    public void onBufferingStart() {
        Log.d(TAG,"onBufferingStart 开始缓冲");
    }

    @Override
    public void onBufferingStop() {
        Log.d(TAG,"onBufferingStop 结束缓冲");
    }

    @Override
    public void onBufferProgress(int percent) {
        Log.d(TAG,"onBufferProgress 缓冲进度 percent --->" + percent);
    }

    @Override
    public void onPlayProgress(int currPos, int duration) {
        this.mCurrentProgressPosition = currPos;
        this.mProgressDuration = duration;
        //单位是毫秒
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onProgressChange(currPos,duration);
        }
        Log.d(TAG,"onPlayProgress 播放进度回调");
    }

    @Override
    public boolean onError(XmPlayerException e) {
        Log.d(TAG,"onError 播放器错误 e --->" + e);
        return false;
    }
    // --- 播放器相关的回调方法 end ---
}
