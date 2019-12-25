package cn.edu.fan.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import cn.edu.fan.himalaya.base.BaseApplication;
import cn.edu.fan.himalaya.base.BaseFragment;
import cn.edu.fan.himalaya.interfaces.ISubscriptionCallback;
import cn.edu.fan.himalaya.interfaces.ISubscriptionPresenter;
import cn.edu.fan.himalaya.presenters.AlbumDetailPresenter;
import cn.edu.fan.himalaya.presenters.SubscriptionPresenter;
import cn.edu.fan.himalaya.utils.Constants;
import cn.edu.fan.himalaya.views.ConfirmDialog;
import cn.edu.fan.himalaya.views.UILoader;

//订阅
public class SubscriptionFragment extends BaseFragment implements ISubscriptionCallback, AlbumListAdapter.OnAlbumItemClickListener, AlbumListAdapter.OnAlbumItemLongClickListener, ConfirmDialog.OnDialogActionClickListener {

    private ISubscriptionPresenter mSubscriptionPresenter;
    private RecyclerView mSubListView;
    private AlbumListAdapter mAlbumListAdapter;
    private Album mCurrentClickAlbum = null;
    private UILoader mUiLoader;
    private TwinklingRefreshLayout mRefreshLayout;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout rootView = (FrameLayout) layoutInflater.inflate(R.layout.fragment_suoscription,container,false);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(container.getContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }

                @Override
                protected View getEmptyView() {
                    View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view,this,false);
                    TextView tipsView = emptyView.findViewById(R.id.empty_view_tips_tv);
                    tipsView.setText(R.string.no_sub_content_tips_text);
                    return emptyView;
                }
            };
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
            rootView.addView(mUiLoader);
        }
        return rootView;
    }

    private View createSuccessView() {
        View itemView = LayoutInflater.from(BaseApplication.getAppContext())
                .inflate(R.layout.item_subscription,null);
        //刷新
        mRefreshLayout = itemView.findViewById(R.id.sub_over_scroll_view);
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableLoadmore(false);
        //RecycleView相关
        mSubListView = itemView.findViewById(R.id.sub_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mSubListView.setLayoutManager(linearLayoutManager);
        mSubListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                //px ---> dp
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        mAlbumListAdapter = new AlbumListAdapter();
        mAlbumListAdapter.setAlbumItemClickListener(this);
        mAlbumListAdapter.setOnAlbumItemLongClickListener(this);
        mSubListView.setAdapter(mAlbumListAdapter);
        //
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.registerViewCallback(this);
        mSubscriptionPresenter.getSubscriptionList();
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        return itemView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消接口的注册，避免内存泄漏
        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unRegisterViewCallback(this);
        }

        mAlbumListAdapter.setAlbumItemClickListener(null);
    }

    @Override
    public void onAddResult(boolean isSuccess) {

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        //给出取消成功的提示
        Toast.makeText(BaseApplication.getAppContext(),
                isSuccess ? R.string.cancel_sub_success : R.string.cancle_sub_failed,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubscriptionsLoaded(List<Album> albumList) {
        if (albumList.size() == 0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }else{
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }
        //更新UI
        if (mAlbumListAdapter != null) {
            mAlbumListAdapter.setData(albumList);
        }
    }

    @Override
    public void onSubFull() {
        Toast.makeText(getActivity(),"最大订阅数为" + Constants.MAX_SUB_COUNT,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int clickPosition, Album album) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        //item被点击了，跳转到详情界面
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Album album) {
        this.mCurrentClickAlbum = album;
        //订阅的item被长按了
        ConfirmDialog confirmDialog = new ConfirmDialog(getActivity());
        confirmDialog.setOnDialogActionClickListener(this);
        confirmDialog.show();
    }

    @Override
    public void onCancelClick() {
        //取消订阅
        if (mCurrentClickAlbum != null && mSubscriptionPresenter != null) {
            mSubscriptionPresenter.deleteSubscription(mCurrentClickAlbum);
        }
    }

    @Override
    public void OnGiveUpClick() {
        //放弃取消订阅

    }
}
