package com.mooq.shortvideodemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by moq.
 * on 2019/5/9
 */
public class MyVideoPlayer extends FrameLayout {

	private IMediaPlayer mMediaPlayer = null;

	/**
	 * 视频文件地址
	 */
	private String mPath = "";

	private SurfaceView surfaceView;

	private MyVideoPlayerListener listener;
	private Context mContext;

	public MyVideoPlayer(@NonNull Context context) {
		super(context);
		initVideoView(context);
	}

	public MyVideoPlayer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initVideoView(context);
	}
	public MyVideoPlayer(Context context, AttributeSet attrs) {
		super(context, attrs);
		initVideoView(context);
	}


	private void initVideoView(Context context) {
		mContext = context;

		//获取焦点，不知道有没有必要~。~
		setFocusable(true);
	}

	/**
	 * 设置视频地址。
	 * 根据是否第一次播放视频，做不同的操作。
	 *
	 * @param path the path of the video.
	 */
	public void setVideoPath(String path) {
		if (TextUtils.equals("", mPath)) {
			//如果是第一次播放视频，那就创建一个新的surfaceView
			mPath = path;
			createSurfaceView();
		} else {
			//否则就直接load
			mPath = path;
			load();
		}
	}

	private void createSurfaceView() {
		//生成一个新的surface view
		surfaceView = new SurfaceView(mContext);
		surfaceView.getHolder().addCallback(new mySurfaceCallback());
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT
				, LayoutParams.MATCH_PARENT, Gravity.CENTER);
		surfaceView.setLayoutParams(layoutParams);
		this.addView(surfaceView);
	}

	private class mySurfaceCallback implements SurfaceHolder.Callback {
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			//surfaceview创建成功后，加载视频
			load();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		}
	}

	private void createPlayer() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.setDisplay(null);
			mMediaPlayer.release();
		}
		IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
		ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);

//开启硬解码        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);

		mMediaPlayer = ijkMediaPlayer;

		if (listener != null) {
			mMediaPlayer.setOnPreparedListener(listener);
			mMediaPlayer.setOnInfoListener(listener);
			mMediaPlayer.setOnSeekCompleteListener(listener);
			mMediaPlayer.setOnBufferingUpdateListener(listener);
			mMediaPlayer.setOnErrorListener(listener);
		}
	}

	public void setListener(MyVideoPlayerListener listener) {
		this.listener = listener;
		if (mMediaPlayer != null) {
			mMediaPlayer.setOnPreparedListener(listener);
		}
	}

	private void load() {
		//每次都要重新创建IMediaPlayer
		createPlayer();
		try {
			mMediaPlayer.setDataSource(mPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//给mediaPlayer设置视图
		mMediaPlayer.setDisplay(surfaceView.getHolder());

		mMediaPlayer.prepareAsync();
	}

	public void start() {
		if (mMediaPlayer != null) {
			mMediaPlayer.start();
		}
	}

	public void release() {
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	public void pause() {
		if (mMediaPlayer != null) {
			mMediaPlayer.pause();
		}
	}

	public void stop() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
		}
	}


	public void reset() {
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
		}
	}


	public long getDuration() {
		if (mMediaPlayer != null) {
			return mMediaPlayer.getDuration();
		} else {
			return 0;
		}
	}


	public long getCurrentPosition() {
		if (mMediaPlayer != null) {
			return mMediaPlayer.getCurrentPosition();
		} else {
			return 0;
		}
	}


	public void seekTo(long l) {
		if (mMediaPlayer != null) {
			mMediaPlayer.seekTo(l);
		}
	}
}
