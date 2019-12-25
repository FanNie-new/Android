package cn.edu.fan.himalaya.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHistoryDaoCallback {

    //添加历史的结果
    void onHistoryAdd(boolean isSuccess);

    //删除历史的结果
    void onHistoryDel(boolean isSuccess);

    //加载历史的结果
    void onHistoriesLoaded(List<Track> tracks);

    //清楚l历史结果
    void onHistoriesClean(boolean isSuccess);
}
