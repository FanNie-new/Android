package cn.edu.fan.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

//通知UI
public interface IRecommendViewCallback {

    //获取推荐内容的结果
    void onRecommendListLoaded(List<Album> result);

    //网络错误
    void onNetWorkError();

    //数据为空
    void onEmpty();

    //正在加载
    void onLoading();

}
