package cn.edu.fan.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

import cn.edu.fan.himalaya.R;
import cn.edu.fan.himalaya.activity.DetailActivity;
import cn.edu.fan.himalaya.adapters.AlbumListAdapter;
import cn.edu.fan.himalaya.base.BaseFragment;
import cn.edu.fan.himalaya.interfaces.IRecommendViewCallback;
import cn.edu.fan.himalaya.presenters.AlbumDetailPresenter;
import cn.edu.fan.himalaya.presenters.RecommendPresenter;
import cn.edu.fan.himalaya.views.UILoader;

//推荐
public class AlbumFragment extends BaseFragment implements IRecommendViewCallback, UILoader.OnRetryClickListener, AlbumListAdapter.OnAlbumItemClickListener {

    private final static String TAG = "AlbumFragment";
    private View mRootView;
    private RecyclerView mRecommendView;
    private AlbumListAdapter mAlbumListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UILoader mUiLoader;

    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, final ViewGroup container) {

        mUiLoader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater, container);
            }
        };


        //去拿数据回来 -- 获取到数据
        //获取到逻辑层的对象
        mRecommendPresenter = RecommendPresenter.getInstance();
        //设置通知接口的注册
        mRecommendPresenter.registerViewCallback(this);
        //获取推荐列表
        mRecommendPresenter.getRecommendList();

        if (mUiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
        }

        mUiLoader.setOnRetryClickListener(this);
        //返回view，给界面显示
        return mUiLoader;
    }

    //成功显示的View
    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        //View加载完成
        mRootView = layoutInflater.inflate(R.layout.fragment_recommend, container, false);
        //RecycleView
        //1、找到控件
        mRecommendView = mRootView.findViewById(R.id.recommend_list);
        TwinklingRefreshLayout twinklingRefreshLayout = mRootView.findViewById(R.id.sub_over_scroll_view);
        twinklingRefreshLayout.setPureScrollModeOn();
        //2、设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);  //设置方向
        mRecommendView.setLayoutManager(linearLayoutManager);
        mRecommendView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                //px ---> dp
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        //3、设置适配器
        mAlbumListAdapter = new AlbumListAdapter();
        mRecommendView.setAdapter(mAlbumListAdapter);
        mAlbumListAdapter.setAlbumItemClickListener(this);
        return mRootView;
    }

    @Override
    public void onRecommendListLoaded(List<Album> result) {
        Log.d(TAG,"onRecommendListLoaded: ");
        //当我们获取到推荐内容的时候，这个方法就会被调用(成功)
        //数据回来以后，更新UI
        mAlbumListAdapter.setData(result);
        mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onNetWorkError() {
        Log.d(TAG,"onNetWorkError: ");
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onEmpty() {
        Log.d(TAG,"onEmpty: ");
        mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
    }

    @Override
    public void onLoading() {
        Log.d(TAG,"onLoading: ");
        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消接口的注册，避免内存泄漏
        if (mRecommendPresenter != null) {
            mRecommendPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onRetryClick() {
        //表示网络不佳的时候，点击了重试
        //重新获取数据即可
        if (mRecommendPresenter != null) {
            mRecommendPresenter.getRecommendList();
        }

    }

    @Override
    public void onItemClick(int position,Album album) {
        //根据位置拿到数据
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        //item被点击了,跳转到详情界面
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);

    }
}
