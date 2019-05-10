package com.mooq.shortvideodemo;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by moq.
 * on 2019/5/9
 */
public abstract class MyVideoPlayerListener implements IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnSeekCompleteListener {
}
