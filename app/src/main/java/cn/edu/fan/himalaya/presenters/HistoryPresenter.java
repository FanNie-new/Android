package cn.edu.fan.himalaya.presenters;

import android.util.Log;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import cn.edu.fan.himalaya.base.BaseApplication;
import cn.edu.fan.himalaya.data.HistoryDao;
import cn.edu.fan.himalaya.data.IHistoryDao;
import cn.edu.fan.himalaya.data.IHistoryDaoCallback;
import cn.edu.fan.himalaya.interfaces.IHistoryCallback;
import cn.edu.fan.himalaya.interfaces.IHistoryPresenter;
import cn.edu.fan.himalaya.utils.Constants;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

//历史记录最多为100条，如果超过100条记录的话，那么就删除最前面添加的，再把当前的历史添加进去
public class HistoryPresenter implements IHistoryPresenter, IHistoryDaoCallback {

    private static final String TAG = "HistoryPresenter";
    private List<IHistoryCallback> mCallbacks = new ArrayList<>();
    private final IHistoryDao mHistoryDao;
    private List<Track> mCurrentHistories = null;
    private Track mCurrentAddTrack = null;

    private HistoryPresenter(){
        mHistoryDao = new HistoryDao();
        mHistoryDao.setCallback(this);
        listHistories();
    }

    private static HistoryPresenter sHistoryPresenter = null;

    public static HistoryPresenter getHistoryPresenter(){
        if (sHistoryPresenter == null) {
            synchronized (HistoryPresenter.class){
                if (sHistoryPresenter == null) {
                    sHistoryPresenter = new HistoryPresenter();
                }
            }
        }
        return sHistoryPresenter;
    }

    @Override
    public void listHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if(mHistoryDao != null) {
                    mHistoryDao.listHistories();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private boolean isDoDelAsOutOfSize = false;

    @Override
    public void addHistory(final Track track) {
        //需要去判断是否已经大于等于100条记录
        if (mCurrentHistories != null && mCurrentHistories.size() >= Constants.MAX_HISTORY_COUNT) {
            isDoDelAsOutOfSize =true;
            this.mCurrentAddTrack = track;
            //先不能添加，先删除最前面的一条记录，再添加
            delHistory(mCurrentHistories.get(mCurrentHistories.size() - 1));
        }else{
            doAddHistory(track);
        }
    }

    private void doAddHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> observableEmitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.addHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> observableEmitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.delHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void cleanHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> observableEmitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.clearHistory();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void registerViewCallback(IHistoryCallback iHistoryCallback) {
        //UI 注册过来
        if (!mCallbacks.contains(iHistoryCallback)) {
            mCallbacks.add(iHistoryCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(IHistoryCallback iHistoryCallback) {
        //删除UI的回调接口
        mCallbacks.remove(iHistoryCallback);
    }

    @Override
    public void onHistoryAdd(boolean isSuccess) {
        //nothing to do
        listHistories();
    }

    @Override
    public void onHistoryDel(boolean isSuccess) {
        //nothing to do
        if (isDoDelAsOutOfSize && mCurrentAddTrack != null) {
            isDoDelAsOutOfSize = false;
            //添加当前的数据进到数据库里
            addHistory(mCurrentAddTrack);
        }else{
            listHistories();
        }
    }

    @Override
    public void onHistoriesLoaded(final List<Track> tracks) {
        this.mCurrentHistories = tracks;
        Log.d(TAG,"history szie --->" + tracks.size());
        //通知UI更新数据
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (IHistoryCallback callback : mCallbacks) {
                    callback.onHistoriesLoaded(tracks);
                }
            }
        });
    }

    @Override
    public void onHistoriesClean(boolean isSuccess) {
        //nothing to do
        listHistories();
    }
}
