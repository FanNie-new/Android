package cn.edu.fan.himalaya.presenters;

import android.util.Log;

import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.List;

import cn.edu.fan.himalaya.data.XimalayaApi;
import cn.edu.fan.himalaya.interfaces.IRecommendPresenter;
import cn.edu.fan.himalaya.interfaces.IRecommendViewCallback;

//实现类
public class RecommendPresenter implements IRecommendPresenter {

    private final static String TAG = "RecommendPresenter";

    private List<IRecommendViewCallback> mCallbacks = new ArrayList<>();

    private List<Album> mCurrentRecommend = null;
    private List<Album> mRecommendList;
    private List<Album> mRecommendsList;

    private RecommendPresenter() { }

    private static RecommendPresenter sInstance = null;

    /*
     * 获取单例对象
     * */
    public static RecommendPresenter getInstance() {
        if (sInstance == null) {
            synchronized (RecommendPresenter.class) {
                if (sInstance == null) {
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    //获取当前的推荐专辑列表 使用之前要判空
    public List<Album> getCurrentRecommend(){
        return mCurrentRecommend;
    }

    /*
     * 获取推荐内容
     * 喜马拉雅SDK接入文档 3.10.6 获取猜你喜欢专辑
     * */
    @Override
    public void getRecommendList() {
        //获取推荐内容
        //如果内容不空的话，那么直接使用当前的内容
        if(mRecommendList != null && mRecommendList.size() > 0) {
            Log.d(TAG,"getRecommendList -- > from list.");
            handlerRecommendResult(mRecommendList);
            return;
        }
        //封装参数
        updateLoading();
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getRecommendList(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                Log.d(TAG, "thread name --->" + Thread.currentThread().getName());
                //数据获取成功
                if (gussLikeAlbumList != null) {
                    mRecommendsList = gussLikeAlbumList.getAlbumList();
                    handlerRecommendResult(mRecommendsList);

                }
            }

            @Override
            public void onError(int i, String s) {
                //数据获取出错
                Log.d(TAG, "error --->" + i);
                Log.d(TAG, "errorMsg --->" + s);
                handlerError();
            }

        });
    }

    private void handlerError() {
        //通知UI更新
        if (mCallbacks != null) {
            for (IRecommendViewCallback callBack : mCallbacks) {
                callBack.onNetWorkError();
            }
        }
    }

    private void handlerRecommendResult(List<Album> albumList) {
        //通知UI更新
        if(albumList != null) {
            //测试，清空一下，让界面显示空
//            albumList.clear();
            if(albumList.size() == 0) {
                for(IRecommendViewCallback callback : mCallbacks) {
                    callback.onEmpty();
                }
            } else {
                for(IRecommendViewCallback callback : mCallbacks) {
                    callback.onRecommendListLoaded(albumList);
                }
                this.mCurrentRecommend = albumList;
            }
        }
    }

    private void updateLoading(){
        for (IRecommendViewCallback callBack : mCallbacks) {
            callBack.onLoading();
        }
    }

    @Override
    public void pullRefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null && !mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }
}
