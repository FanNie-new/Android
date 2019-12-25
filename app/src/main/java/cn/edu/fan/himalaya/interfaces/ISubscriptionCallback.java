package cn.edu.fan.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

//订阅的回调方法
public interface ISubscriptionCallback {

    //调用添加的时候，去通知UI结果
    void onAddResult(boolean isSuccess);

    //删除订阅的回调方法
    void onDeleteResult(boolean isSuccess);

    //订阅专辑加载的结果回调方法
    void onSubscriptionsLoaded(List<Album> albumList);

    //订阅数量
    void onSubFull();
}
