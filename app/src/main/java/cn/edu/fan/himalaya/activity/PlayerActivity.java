package cn.edu.fan.himalaya.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.fan.himalaya.R;
import cn.edu.fan.himalaya.adapters.PlayerTrackPageAdapter;
import cn.edu.fan.himalaya.base.BaseActivity;
import cn.edu.fan.himalaya.interfaces.IPlayerCallback;
import cn.edu.fan.himalaya.presenters.PlayerPresenter;
import cn.edu.fan.himalaya.views.SobPopWindow;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerActivity extends BaseActivity implements IPlayerCallback, ViewPager.OnPageChangeListener{

    private static final String TAG = "PlayerActivity";
    private ImageView mControlBtn;
    private PlayerPresenter mPlayerPresenter;
    private SimpleDateFormat mMinFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat = new SimpleDateFormat("hh:mm:ss");
    private TextView mTotalDuration;
    private TextView mCurrentPosition;
    private SeekBar mDurationBar;
    private int mCurrentProgress = 0;
    private boolean mIsUserTouchProgress = false;
    private ImageView mPlayerPreBtn;
    private ImageView mPlayerNextBtn;
    private TextView mTrackTitleTv;
    private String mTrackTitleText;
    private ViewPager mTrackPageView;
    private PlayerTrackPageAdapter mTrackPageAdapter;
    private boolean mIsUserSlidePager = false;
    private ImageView mPlayModeSwitchBtn;

    private XmPlayListControl.PlayMode mCurrentPlayMode = PLAY_MODEL_LIST;
    //
    private static Map<XmPlayListControl.PlayMode,XmPlayListControl.PlayMode> sPlayModeRule = new HashMap<>();

    /*
     * PLAY_MODEL_SINGLE单曲播放
     * PLAY_MODEL_SINGLE_LOOP 单曲循环播放
     * PLAY_MODEL_LIST列表播放
     * PLAY_MODEL_LIST_LOOP列表循环
     * PLAY_MODEL_RANDOM 随机播放
     * */
    static{
        sPlayModeRule.put(PLAY_MODEL_LIST,PLAY_MODEL_LIST_LOOP) ;
        sPlayModeRule.put(PLAY_MODEL_LIST_LOOP,PLAY_MODEL_RANDOM) ;
        sPlayModeRule.put(PLAY_MODEL_RANDOM,PLAY_MODEL_SINGLE_LOOP) ;
        sPlayModeRule.put(PLAY_MODEL_SINGLE_LOOP,PLAY_MODEL_LIST) ;
    }

    private ImageView mPlayList;
    private SobPopWindow mSobPopWindow;
    private ValueAnimator mEnterBgAnimator;
    private ValueAnimator mOutBgAnimator;

    public final int BG_ANIMATION_DURATION = 500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        //注册
        mPlayerPresenter.registerViewCallback(this);
        initEvent();
        initBgAnimation();
    }

    private void initBgAnimation() {
        mEnterBgAnimator = ValueAnimator.ofFloat(1.0f,0.7f);
        mEnterBgAnimator.setDuration(BG_ANIMATION_DURATION);
        mEnterBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                //处理一下背景，有点透明度
                updateBgAlpha(value);
            }
        });
        //退出的
        mOutBgAnimator = ValueAnimator.ofFloat(0.7f,1.0f);
        mOutBgAnimator.setDuration(BG_ANIMATION_DURATION);
        mOutBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                //处理一下背景，有点透明度
                updateBgAlpha(value);
            }
        });
    }


    //给控件设置相关的事件
    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果现在的状态是正在播放,那么就暂停
                if (mPlayerPresenter.isPlaying()) {
                    mPlayerPresenter.pause();
                }else{
                    //反之,播放
                    mPlayerPresenter.play();
                }
            }
        });
        //进度条事件
        mDurationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mCurrentProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgress = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgress = false;
                //手离开拖动进度条的时候更新进度
                mPlayerPresenter.seekTo(mCurrentProgress);
            }
        });

        mPlayerPreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放前一首
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playPre();
                }
            }
        });

        mPlayerNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放下一首
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playNext();
                }
            }
        });
        mTrackPageView.addOnPageChangeListener(this);

        mTrackPageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action =  event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mIsUserSlidePager = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        mIsUserSlidePager = false;
                        break;
                }
                return false;
            }
        });

        mPlayModeSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //处理播放模式的切换
                switchPlayMode();
            }
        });

        mPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //展示播放列表
                mSobPopWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
                //修改背景的透明有一个渐变的过程
                mEnterBgAnimator.start();
            }
        });
        //点击消失
        mSobPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //pop窗体消失以后，回复透明度
                mOutBgAnimator.start();
            }
        });

        mSobPopWindow.setOnPlayListItemClickListener(new SobPopWindow.OnPlayListItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //播放列表里的item被点击了
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playByIndex(position);
                }
            }
        });

        mSobPopWindow.setPlayListActionListener(new SobPopWindow.PlayListActionListener() {
            @Override
            public void onPlayModeClick() {
                //切换播放模式
                switchPlayMode();
            }

            @Override
            public void onOrderClick() {
                //点击了切换顺序和逆序
                Toast.makeText(PlayerActivity.this,"切换列表顺序",Toast.LENGTH_SHORT).show();
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.reversePlayList();
                }
            }
        });
    }

    private void switchPlayMode() {
        //根据当前的mode获取到下一个mode
        XmPlayListControl.PlayMode playMode = sPlayModeRule.get(mCurrentPlayMode);
        //修改播放模式
        if (mPlayerPresenter != null) {
            mPlayerPresenter.switchPlayMode(playMode);
        }
    }

    public void updateBgAlpha(float alpha){
        //获取window
        Window window = getWindow();
        //拿到属性
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha = alpha;
        window.setAttributes(attributes);
    }

    //根据当前的状态，更新播放模式图标
    private void updatePlayModeBtnImg() {
        int resId = R.drawable.selector_play_mode_list_order;
        switch (mCurrentPlayMode) {
            case PLAY_MODEL_LIST:
                resId = R.drawable.selector_play_mode_list_order;
                break;
            case PLAY_MODEL_RANDOM:;
                resId = R.drawable.selector_play_mode_list_random;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_play_mode_list_looper;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_play_mode_single_loop;
                break;
        }
        mPlayModeSwitchBtn.setImageResource(resId);
    }

    //找到各个控件
    private void initView() {
        mControlBtn = this.findViewById(R.id.play_or_pause_btn);
        mTotalDuration = this.findViewById(R.id.track_duration);
        mCurrentPosition = this.findViewById(R.id.current_position);
        mDurationBar = this.findViewById(R.id.track_seek_bar);
        mPlayerPreBtn = this.findViewById(R.id.player_pre);
        mPlayerNextBtn = this.findViewById(R.id.player_next);
        mTrackTitleTv = this.findViewById(R.id.track_title);
        //判空
        if (!TextUtils.isEmpty(mTrackTitleText)) {
            mTrackTitleTv.setText(mTrackTitleText);
        }

        mTrackPageView = this.findViewById(R.id.track_pager_view);
        //创建适配器
        mTrackPageAdapter = new PlayerTrackPageAdapter();
        //设置适配器
        mTrackPageView.setAdapter(mTrackPageAdapter);
        //切换播放模式的按钮
        mPlayModeSwitchBtn = this.findViewById(R.id.player_switch_btn);
        //播放列表
        mPlayList = this.findViewById(R.id.player_list);
        mSobPopWindow = new SobPopWindow();
    }

    // 取消注册
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
            mPlayerPresenter = null;
        }
    }

    @Override
    public void onPlayStart() {
        //开始播放,修改UI成暂停的按钮
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_pause);
        }

    }

    @Override
    public void onPlayPause() {
        //暂停后,修改UI成播放的按钮
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.play_press);
        }

    }

    @Override
    public void onPlayStop() {
        //停止后,修改UI成播放的按钮
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.stop_press);
        }

    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void nextPlay(Track track) {

    }

    @Override
    public void PrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {
//        Log.d(TAG,"list ==>" + list);
        //把数据设置到设配器里去
        if (mTrackPageAdapter != null) {
            mTrackPageAdapter.setData(list);
        }
        //数据回来以后，也要给节目列表一份
        if (mSobPopWindow != null) {
            mSobPopWindow.setListData(list);
        }

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {
        //更新播放模式并且修改UI
        mCurrentPlayMode = playMode;
        //更新pop里的播放模式
        mSobPopWindow.updatePlayMode(mCurrentPlayMode);
        updatePlayModeBtnImg();
    }

    @Override
    public void onProgressChange(int currentProgress, int total) {
        mDurationBar.setMax(total);
        //更新播放进度条，更新进度条
        String totalDuration;
        String currentPosition;
        if (total > 1000 * 60 * 60) {
            totalDuration = mHourFormat.format(total);
            currentPosition = mHourFormat.format(currentProgress);
        }else{
            totalDuration = mMinFormat.format(total);
            currentPosition = mMinFormat.format(currentProgress);
        }
        //更新事件
        if (mTotalDuration != null) {
            mTotalDuration.setText(totalDuration);
        }
        //更新当前的时间
        if (mCurrentPosition != null) {
            mCurrentPosition.setText(currentPosition);
        }
        //更新进度
        //计算当前进度
        if (!mIsUserTouchProgress) {
//        Log.d(TAG,"currentProgress --->" + currentProgress);
            mDurationBar.setProgress(currentProgress);
        }
    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (track == null) {
            Log.d(TAG,"onTrackUpdate ---> track null");
            return;
        }
        this.mTrackTitleText = track.getTrackTitle();
        if (mTrackTitleTv != null) {
            //设置当前的标题
            mTrackTitleTv.setText(mTrackTitleText);
        }
        //当前节目改变的时候，就获取到当前播放器中播放位置
        //当前的节目改变以后，要修改页面的图片
        if (mTrackPageView != null) {
            mTrackPageView.setCurrentItem(playIndex,false);
        }

        //修改播放列表里的播放位置
        if (mSobPopWindow != null) {
            mSobPopWindow.setCurrentPlayPosition(playIndex);
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {
        mSobPopWindow.updateOrderIcon(isReverse);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d(TAG,"position --->" + position);
        //当页面选中的时候，就去切换播放的内容
        if (mPlayerPresenter != null && mIsUserSlidePager) {
            mPlayerPresenter.playByIndex(position);
        }
        mIsUserSlidePager = false;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
