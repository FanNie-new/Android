package cn.edu.fan.himalaya.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

public interface IHistoryDao {

    //设置回调接口
    void setCallback(IHistoryDaoCallback callback);

    //添加历史
    void addHistory(Track track);

    //删除历史
    void delHistory(Track track);

    //清楚历史内容
    void clearHistory();

    //获取历史内容
    void listHistories();
}
