package cn.edu.fan.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import cn.edu.fan.himalaya.base.IBasePresenter;

//订阅接口
//订阅一般是有上限的 比如不能超过100个
public interface ISubscriptionPresenter extends IBasePresenter<ISubscriptionCallback> {

    //添加订阅
    void addSubscription(Album album);

    //删除订阅
    void deleteSubscription(Album album);

    //获取订阅列表
    void getSubscriptionList();

    //判断当前专辑是否已经收藏/订阅
    boolean isSub(Album album);

}
