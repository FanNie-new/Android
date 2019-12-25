package cn.edu.fan.himalaya.interfaces;

import cn.edu.fan.himalaya.base.IBasePresenter;

public interface ISearchPresenter extends IBasePresenter<ISearchCallback> {

    //进行搜索
    void doSearch(String keyWord);

    //重新搜索
    void reSearch();

    //加载更多的搜索结果
    void loadMore();

    //获取热词
    void getHotWord();

    //推荐列表
    void getRecommendWord(String keyWord);
}
