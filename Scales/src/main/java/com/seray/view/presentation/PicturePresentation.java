package com.seray.view.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.Display;

import com.seray.scales.R;
import com.seray.sjc.AppExecutors;
import com.seray.sjc.SjcConfig;
import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.db.dao.ScreenResourceDao;
import com.seray.sjc.entity.device.ScreenResource;
import com.seray.sjc.poster.SjcUpdatePoster;
import com.seray.sjc.util.BannerImageLoader;
import com.seray.util.LogUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：李程
 * CreateTime：2019/6/1 12:19
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public class PicturePresentation extends BasePresentation {

    private final int STOP_PLAYER = 0x2000;
    private final int START_PLAYER = 0x2001;
    private final int PAUSE_PLAYER = 0x2002;
    private final int SET_VIDEO_URL = 0x2003;

    private Banner mTopNewsView;
//    private VideoView mVideoView;

//    private ProgressBar mProgressBar;

//    private boolean playerPaused;
//    private int currentPosition;

    private List<ScreenResource> mData = new ArrayList<>();

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mTopNewsView.update(mData);
                    break;
                case STOP_PLAYER:
//                    stopPlayer();
                    break;
                case START_PLAYER:
//                    startPlayer();
                    break;
                case PAUSE_PLAYER:
//                    pausePlayer();
                    break;
                case SET_VIDEO_URL:
//                    setVideoUrl(msg.obj.toString());
//                    startPlayer();
                    break;
            }
        }
    };

    /**
     * 业务数据周期性自检更新回调通知
     */
    private BroadcastReceiver mBusinessDataUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(SjcConfig.ACTION_UPDATE_BUSINESS_DATA)) {
                String notifyType = intent.getStringExtra(SjcUpdatePoster.KEY_INTENT);
                if (SjcConfig.UPDATE_SCREEN_RESOURCE.equals(notifyType)) {
                    initData();
                }
            }
        }
    };

    public PicturePresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_ad);
        registerResourceChangeBroadcast();
//        initVideoView();
        mTopNewsView = (Banner) findViewById(R.id.presentation_viewPager);
        mTopNewsView.setImageLoader(new BannerImageLoader());
        mTopNewsView.isAutoPlay(true);
        mTopNewsView.setDelayTime(5000);
        mTopNewsView.setBannerStyle(BannerConfig.NUM_INDICATOR);
        mTopNewsView.setViewPagerIsScroll(false);

        mTopNewsView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                LogUtil.i("Banner", "current position = " + position);
//                position--;
//                if (position < mData.size()) {
//                    ScreenResource screenResource = mData.get(position);
//                    String resourceType = screenResource.getResourceType();
//
//                    if (resourceType.equals("2")) {
//
//                        mTopNewsView.setVisibility(View.GONE);
//                        mVideoView.setVisibility(View.VISIBLE);
//                        mProgressBar.setVisibility(View.VISIBLE);
//
//                        mTopNewsView.isAutoPlay(false);
//                        mTopNewsView.stopAutoPlay();
//
//                        Message message = Message.obtain();
//                        message.what = SET_VIDEO_URL;
//                        message.obj = screenResource.getResourceUrl();
//                        mHandler.sendMessage(message);
//
//                    }
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initData();
    }

//    private void initVideoView() {
//        mVideoView = (VideoView) findViewById(R.id.presentation_video);
//        mProgressBar = (ProgressBar) findViewById(R.id.loading_progress_bar);
//        mVideoView.setMediaController(new MediaController(getContext()));
//        mVideoView.requestFocus();
//        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                LogUtil.i("视频播放完成");
//                mVideoView.stopPlayback();
//                mTopNewsView.isAutoPlay(true);
//                mTopNewsView.startAutoPlay();
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mTopNewsView.setVisibility(View.VISIBLE);
//                        mVideoView.setVisibility(View.GONE);
//                    }
//                });
//            }
//        });
//
//        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                LogUtil.i("视频加载完成");
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mTopNewsView.setVisibility(View.GONE);
//                        mProgressBar.setVisibility(View.GONE);
//                        mVideoView.setVisibility(View.VISIBLE);
//                    }
//                });
//            }
//        });
//
//        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
//                LogUtil.i("视频播放出错了-what=" + what + ",extra=" + extra);
//                mVideoView.stopPlayback();
//                mProgressBar.setVisibility(View.GONE);
////                if (null != listener) {
////                    listener.onError(mp);
////                }
//                if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
//                    //媒体服务器挂掉了。此时，程序必须释放MediaPlayer 对象，并重新new 一个新的。
//                    LogUtil.e("媒体服务器挂掉了");
//                } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
//                    if (extra == MediaPlayer.MEDIA_ERROR_IO) {
//                        //文件不存在或错误，或网络不可访问错误
//                        LogUtil.e("文件不存在或错误，或网络不可访问错误");
//                    } else if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
//                        //超时
//                        LogUtil.e("超时");
//                    }
//                }
//                return true;
//            }
//        });
//    }

    @Override
    public void show() {
        super.show();
        mTopNewsView.startAutoPlay();
    }

    @Override
    public void dismiss() {
        mTopNewsView.stopAutoPlay();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mBusinessDataUpdateReceiver);
        super.dismiss();
    }

//    private void setVideoUrl(String resource) {
//        //播放本地视频
//        mVideoView.setVideoURI(Uri.parse(resource));
//    }
//
//    private void pausePlayer() {
//        if (null != mVideoView) {
//            playerPaused = true;
//            this.currentPosition = mVideoView.getCurrentPosition();
//            mVideoView.pause();
//        }
//    }
//
//    private void stopPlayer() {
//        if (null != mVideoView) {
//            mVideoView.stopPlayback();
//            mHandler.removeCallbacksAndMessages(null);
//        }
//    }
//
//    public void startPlayer() {
//        if (null != mVideoView) {
////            mVideoView.seekTo(currentPosition);
//            mVideoView.start();
//        }
//    }

    private void registerResourceChangeBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SjcConfig.ACTION_UPDATE_BUSINESS_DATA);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBusinessDataUpdateReceiver, filter);
    }

    private void initData() {
        AppExecutors.getInstance().queryIO().submit(new Runnable() {
            @Override
            public void run() {
                ScreenResourceDao screenResourceDao = AppDatabase.getInstance().getScreenResourceDao();
                final List<ScreenResource> screenResources = screenResourceDao.loadPictures();
//                List<ScreenResource> videos = screenResourceDao.loadVideos();
                mData.clear();
                mData.addAll(screenResources);

                mHandler.sendEmptyMessage(0);

            }
        });
    }
}
