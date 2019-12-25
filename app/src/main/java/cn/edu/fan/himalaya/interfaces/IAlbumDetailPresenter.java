package cn.edu.fan.himalaya.interfaces;

import cn.edu.fan.himalaya.base.IBasePresenter;

public interface IAlbumDetailPresenter extends IBasePresenter<IAlbumDetailViewCallback> {

    /*
     * 下拉刷新更多内容
     * */
    void pullRefreshMore();

    /*
     * 上拉加载更多
     * */
    void loadMore();

    /*
    * 获取专辑详情
    * */
    void getAlbumDetail(int albumId, int page);

}
