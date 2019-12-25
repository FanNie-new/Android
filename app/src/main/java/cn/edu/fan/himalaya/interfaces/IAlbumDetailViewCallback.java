package cn.edu.fan.himalaya.interfaces;

import android.os.Trace;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDetailViewCallback {

    /*
    * 专辑详情内容加载出来了
    * */
    void onDetailListLoaded(List<Track> tracks);

    //网络错误
    void onNetWorkError(int errorCode, String errorMsg);

    /*
    * 把Album 传给UI
    * */
    void onAlbumLoaded(Album album);

    //加载更多的接口 size > 0 表示加载成功 size < 0  表示加载失败
    void onLoaderMoreFinished(int size);

    //下拉刷新 size > 0 表示刷新成功 size < 0 表示刷新失败
    void onRefreshFinished(int size);
}
