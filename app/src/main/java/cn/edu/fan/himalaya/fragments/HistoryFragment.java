package cn.edu.fan.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

import cn.edu.fan.himalaya.R;
import cn.edu.fan.himalaya.activity.PlayerActivity;
import cn.edu.fan.himalaya.adapters.TrackListAdapter;
import cn.edu.fan.himalaya.base.BaseApplication;
import cn.edu.fan.himalaya.base.BaseFragment;
import cn.edu.fan.himalaya.interfaces.IHistoryCallback;
import cn.edu.fan.himalaya.presenters.HistoryPresenter;
import cn.edu.fan.himalaya.presenters.PlayerPresenter;
import cn.edu.fan.himalaya.views.ConfirmCheckBoxDialog;
import cn.edu.fan.himalaya.views.UILoader;

//历史
public class HistoryFragment extends BaseFragment implements IHistoryCallback, TrackListAdapter.ItemClickListener, TrackListAdapter.ItemLongClickListener, ConfirmCheckBoxDialog.OnDialogActionClickListener {

    private UILoader mUiLoader;
    private RecyclerView mHistoryList;
    private TrackListAdapter mTracklListAdapter;
    private HistoryPresenter mHistoryPresenter;
    private Track mCurrentHistoryItem = null;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout rootView = (FrameLayout) layoutInflater.inflate(R.layout.fragment_history,container,false);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(BaseApplication.getAppContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }

                @Override
                protected View getEmptyView() {
                    View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
                    TextView tips = emptyView.findViewById(R.id.empty_view_tips_tv);
                    tips.setText("没有历史记录~~~");
                    return emptyView;
                }
            };
        }else{
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
        }

        //HistoryPresenter
        mHistoryPresenter = HistoryPresenter.getHistoryPresenter();
        mHistoryPresenter.registerViewCallback(this);
        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        mHistoryPresenter.listHistories();
        rootView.addView(mUiLoader);
        return rootView;
    }

    private View createSuccessView(ViewGroup container) {
        View successView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_history, container, false);
        TwinklingRefreshLayout refreshLayout = successView.findViewById(R.id.history_over_scroll_view);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadmore(false);
        //recycleView
        mHistoryList = successView.findViewById(R.id.history_list);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
        mHistoryList.setLayoutManager(linearLayoutManager);
        //设置item的上下间距
        mHistoryList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                //px ---> dp
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 2);
                outRect.right = UIUtil.dip2px(view.getContext(), 2);
            }
        });
        //设置适配器
        mTracklListAdapter = new TrackListAdapter();
        mTracklListAdapter.setItemClickListener(this);
        mTracklListAdapter.setItemLongClickListener(this);
        mHistoryList.setAdapter(mTracklListAdapter);
        return successView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHistoryPresenter != null) {
            mHistoryPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onHistoriesLoaded(List<Track> tracks) {
        if (tracks == null || tracks.size() == 0) {
            mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
        }else{
            //更新数据
            mTracklListAdapter.setData(tracks);
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
    }

    @Override
    public void onItemClick(List<Track> detailDta, int position) {
        //设置播放器的数据
        PlayerPresenter playerPresenter =  PlayerPresenter.getPlayerPresenter();
        playerPresenter.setPlayList(detailDta,position);
        //跳转到播放器界面
        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Track track) {
        this.mCurrentHistoryItem = track;
        //去删除历史
//        Toast.makeText(getActivity(),"历史记录" + track.getTrackTitle(),Toast.LENGTH_SHORT).show();
        ConfirmCheckBoxDialog dialog = new ConfirmCheckBoxDialog(getActivity());
        dialog.setOnDialogActionClickListener(this);
        dialog.show();
    }

    @Override
    public void onCancelClick() {
        //不用做
    }

    @Override
    public void onConfirmDel(boolean isCheck) {
        //去删除历史
        if (mHistoryPresenter != null && mCurrentHistoryItem != null) {
            if (!isCheck) {
                mHistoryPresenter.delHistory(mCurrentHistoryItem);
            }else{
                mHistoryPresenter.cleanHistories();
            }
        }
    }
}
