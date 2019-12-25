package cn.edu.fan.himalaya.base;

import cn.edu.fan.himalaya.interfaces.IAlbumDetailViewCallback;

//
public interface IBasePresenter<T> {


    /*
     * 注册通知UI的接口
     *
     **/

    void registerViewCallback(T t);

    /*
     * 取消注册UI通知接口
     * */
    void unRegisterViewCallback(T t);
}
