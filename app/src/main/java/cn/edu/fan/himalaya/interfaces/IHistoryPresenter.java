package cn.edu.fan.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import cn.edu.fan.himalaya.base.IBasePresenter;

//历史接口
public interface IHistoryPresenter extends IBasePresenter<IHistoryCallback> {

    //获取历史内容
    void listHistories();

    //添加历史
    void addHistory(Track track);

    //删除历史
    void delHistory(Track track);

    //清楚历史
    void cleanHistories();


}
