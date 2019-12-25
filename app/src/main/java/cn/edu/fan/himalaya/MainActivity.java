package cn.edu.fan.himalaya;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;

import cn.edu.fan.himalaya.activity.PlayerActivity;
import cn.edu.fan.himalaya.activity.SearchActivity;
import cn.edu.fan.himalaya.adapters.IndicatorAdapter;
import cn.edu.fan.himalaya.adapters.MainContentAdapter;
import cn.edu.fan.himalaya.interfaces.IPlayerCallback;
import cn.edu.fan.himalaya.presenters.PlayerPresenter;
import cn.edu.fan.himalaya.presenters.RecommendPresenter;
import cn.edu.fan.himalaya.views.RoundRectImageView;

public class MainActivity extends FragmentActivity implements IPlayerCallback {

    private static final String TAG = "MainActivity";
    private MagicIndicator magicIndicator;
    private ViewPager mContentPager;
    private IndicatorAdapter mIndicatorAdapter;
    private RoundRectImageView mRoundRectImageView;
    private TextView mHeaderTitle;
    private TextView mSubTitle;
    private ImageView mPlayControl;
    private PlayerPresenter mPlayerPresenter;
    private View mPlayControlItem;
    private View mSearchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        //
        initPresenter();
    }

    private void initPresenter() {
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
    }

    //设置事件
    private void initEvent() {
        //监听滑动
        mContentPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mIndicatorAdapter.setOnIndicatorTapClickListener(new IndicatorAdapter.OnIndicatorTapClickListener() {
            @Override
            public void onTabClick(int index) {
//                Log.d(TAG,"index --->" + index);
                if (mContentPager != null) {
                    mContentPager.setCurrentItem(index); // 将位置传出来
                }
            }
        });
        //播放
        mPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    boolean hasPlayList =  mPlayerPresenter.hasPlayList();
                    if (!hasPlayList) {
                        //没有设置过播放列表，就播放默认的第一个推荐专辑
                        //第一个推荐专辑，每天都会变
                        playFirstRecommend();
                    }else{
                        if (mPlayerPresenter.isPlaying()) {
                            mPlayerPresenter.pause();
                        }else{
                            mPlayerPresenter.play();
                        }
                    }
                }
            }
        });
        //播放
        mPlayControlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPlayList =  mPlayerPresenter.hasPlayList();
                if (!hasPlayList) {
                    playFirstRecommend();
                }
                //跳转到播放器界面
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                startActivity(intent);
            }
        });

        //搜索
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到搜索界面
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    //播放第一个推荐的内容
    private void playFirstRecommend() {
        List<Album> currentRecommend =  RecommendPresenter.getInstance().getCurrentRecommend();
        if (currentRecommend != null && currentRecommend.size() > 0) {
            Album album = currentRecommend.get(0);
            long albumId= album.getId();
            mPlayerPresenter.playByAlbumId(albumId);
        }
    }

    //找到控件
    private void initView() {
        magicIndicator = this.findViewById(R.id.main_indicator);
        magicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));
        //创建indicator的适配器
        mIndicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        //平分位置
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(mIndicatorAdapter);
        //设置要显示的内容

        //ViewPager
        mContentPager = this.findViewById(R.id.content_pager);

        //创建内容适配器
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MainContentAdapter mainContentAdapter = new MainContentAdapter(supportFragmentManager);
        mContentPager.setAdapter(mainContentAdapter);

        //把ViewPager和indicator绑定在一起
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mContentPager);

        //播放控制相关的
        mRoundRectImageView = this.findViewById(R.id.main_track_cover);
        mHeaderTitle = this.findViewById(R.id.main_head_title);
        mHeaderTitle.setSelected(true);
        mSubTitle = this.findViewById(R.id.main_sub_title);
        mPlayControl = this.findViewById(R.id.main_play_control);
        //播放条目
        mPlayControlItem = this.findViewById(R.id.main_play_control_item);
        //搜索相关
        mSearchBtn = this.findViewById(R.id.search_btn);
    }

    //取消注册
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }
    }

    private void updatePlayControl(boolean isPlaying){
        if (mPlayControl != null) {
            mPlayControl.setImageResource(isPlaying ?
                    R.drawable.selector_player_pause : R.drawable.selector_player_play);
        }
    }
    @Override
    public void onPlayStart() {
        updatePlayControl(true);
    }

    @Override
    public void onPlayPause() {
        updatePlayControl(false);
    }

    @Override
    public void onPlayStop() {
        updatePlayControl(false);
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

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (track != null) {
            String trackTitle = track.getTrackTitle();
            String nickName = track.getAnnouncer().getNickname();
            String coverUrlMiddle = track.getCoverUrlMiddle();
//            Log.d(TAG,"trackTitle --->" + trackTitle);
//            Log.d(TAG,"nickName --->" + nickName);
//            Log.d(TAG,"coverUrlMiddle --->" + coverUrlMiddle);
            if (mHeaderTitle != null) {
                mHeaderTitle.setText(trackTitle);
            }
            if (mSubTitle != null) {
                mSubTitle.setText(nickName);
            }
            Picasso.with(this).load(coverUrlMiddle).into(mRoundRectImageView);
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}
