package cn.edu.fan.himalaya.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

import cn.edu.fan.himalaya.R;
import cn.edu.fan.himalaya.adapters.PlayListAdapter;
import cn.edu.fan.himalaya.base.BaseApplication;

public class SobPopWindow extends PopupWindow {

    private final View mPopView;
    private View mCloseBtn;
    private RecyclerView mTrackList;
    private PlayListAdapter mPlayListAdapter;
    private TextView mPlayModeTv;
    private ImageView mPlayModeIv;
    private View mPlayModeContainer;
    private PlayListActionListener mPlayModeClickListener = null;
    private View mOrderBtnContainer;
    private ImageView mOrderIcon;
    private TextView mOrderText;

    public SobPopWindow(){
        //设置它的宽高
        super( ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //注意设置setOutsideTouchable() 之前，先要设置setBackgroundDrawable()
        //否则无法关闭pop
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //设置可点击的
        setOutsideTouchable(true);
        //载进来View
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list,null);
        //设置内容
        setContentView(mPopView);
        //设置窗口进入和退出的动画
        setAnimationStyle(R.style.pop_animation);
        initView();
        initEvent();
    }

    //找到各个控件
    private void initView() {
        mCloseBtn = mPopView.findViewById(R.id.play_list_close_btn);
        //先找到控件
        mTrackList = mPopView.findViewById(R.id.play_list_rv);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BaseApplication.getAppContext());
        mTrackList.setLayoutManager(linearLayoutManager);
        //设置适配器
        mPlayListAdapter = new PlayListAdapter();
        mTrackList.setAdapter(mPlayListAdapter);
        //播放模式相关
        mPlayModeTv = mPopView.findViewById(R.id.play_list_play_mode_tv);
        mPlayModeIv = mPopView.findViewById(R.id.player_list_play_mode_iv);
        mPlayModeContainer = mPopView.findViewById(R.id.play_list_play_mode_container);
        //
        mOrderBtnContainer = mPopView.findViewById(R.id.play_list_order_container);
        mOrderIcon = mPopView.findViewById(R.id.play_list_order_iv);
        mOrderText = mPopView.findViewById(R.id.play_list_order_tv);
    }

    //设置事件
    private void initEvent() {
        //点击关闭以后，窗口消失
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SobPopWindow.this.dismiss();
            }
        });

        mPlayModeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放模式
                if (mPlayModeClickListener != null) {
                    mPlayModeClickListener.onPlayModeClick();
                }
            }
        });

        mOrderBtnContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放列表为顺序或者逆序
                mPlayModeClickListener.onOrderClick();
            }
        });
    }

    //给适配器设置数据
    public void setListData(List<Track> data){
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setData(data);
        }
    }

    public void setCurrentPlayPosition(int position){
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setCurrentPlayPosition(position);
            mTrackList.scrollToPosition(position);
        }
    }

    public void setOnPlayListItemClickListener(OnPlayListItemClickListener listener){
        mPlayListAdapter.setOnItemClickListener(listener);
    }

    //根据当前的状态，更新播放模式图标
    private void updatePlayModeBtnImg(XmPlayListControl.PlayMode playMode) {
        int resId = R.drawable.selector_play_mode_list_order;
        int textId = R.string.play_mode_order_text;
        switch (playMode) {
            case PLAY_MODEL_LIST:
                resId = R.drawable.selector_play_mode_list_order;
                textId = R.string.play_mode_order_text;
                break;
            case PLAY_MODEL_RANDOM:;
                resId = R.drawable.selector_play_mode_list_random;
                textId = R.string.play_mode_random_text;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_play_mode_list_looper;
                textId = R.string.play_mode_list_play_text;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_play_mode_single_loop;
                textId = R.string.play_mode_single_play_text;
                break;
        }
        mPlayModeIv.setImageResource(resId);
        mPlayModeTv.setText(textId);
    }

    //更新播放列表的播放模式
    public void updatePlayMode(XmPlayListControl.PlayMode currentPlayMode) {
        updatePlayModeBtnImg(currentPlayMode);
    }

    //更新切换列表顺和逆序的按钮和文字
    public void updateOrderIcon(boolean isReverse){
        mOrderIcon.setImageResource(isReverse ? R.drawable.selector_play_mode_list_order :
                R.drawable.selector_play_mode_list_reverse);
        mOrderText.setText(BaseApplication.getAppContext().getResources().getString(
                isReverse ? R.string.order_text : R.string.reverse_text));
    }

    public interface OnPlayListItemClickListener{
        void onItemClick(int position);
    }

    public void setPlayListActionListener(PlayListActionListener listener){
        this.mPlayModeClickListener = listener;
    }

    public interface PlayListActionListener{
        //播放模式被点击了
        void onPlayModeClick();
        //逆序播放或者顺序播放被点击了
        void onOrderClick();
    }
}
