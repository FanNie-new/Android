package cn.edu.fan.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

//历史接口回调
public interface IHistoryCallback{

    //历史内容加载
    void onHistoriesLoaded(List<Track> tracks);
}
