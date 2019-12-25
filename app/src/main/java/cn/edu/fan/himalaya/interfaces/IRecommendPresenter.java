package cn.edu.fan.himalaya.interfaces;

import cn.edu.fan.himalaya.base.IBasePresenter;

//推荐逻辑
public interface IRecommendPresenter extends IBasePresenter<IRecommendViewCallback> {
    /*
    * 获取推荐内容
    * */
    void getRecommendList();

    /*
    * 下拉刷新更多内容
    * */
    void pullRefreshMore();

    /*
    * 上拉加载更多
    * */
    void loadMore();

}
