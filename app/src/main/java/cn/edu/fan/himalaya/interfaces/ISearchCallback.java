package cn.edu.fan.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

public interface ISearchCallback {

    //搜索结果的回调方法
    void onSearchResultLoaded(List<Album> result);

    //获取推荐热词的结果回调方法
    void onHotWordLoaded(List<HotWord> hotWordList);

    //加载更多结果返回
    // result 结果 isOkay true 表示加载更多，false 表示没有更多
    void onLoadMoreResult(List<Album> result, boolean isOkay);

    //联想关键字的结果回调方法
    void onRecommendWordLoaded(List<QueryResult> keyWordList);

    //错误通知回调
    void onError(int errorCode,String errorMsg);
}
