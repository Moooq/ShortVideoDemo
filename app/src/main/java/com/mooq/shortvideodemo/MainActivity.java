package com.mooq.shortvideodemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends AppCompatActivity {

	RecyclerView rvVideo;

	private Context mContext;

	private List<String> urlList;

	private int currentPosition = 0;

	private ListVideoAdapter videoAdapter;
	private PagerSnapHelper snapHelper;
	private VideoViewLayoutManager layoutManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		rvVideo = (RecyclerView) findViewById(R.id.rv_video);
		initView();
	}

	private void initView() {
		urlList = new ArrayList<>();
		urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201811/26/09/5bfb4c55633c9.mp4");
		urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201805/100651/201805181532123423.mp4");
		urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803151735198462.mp4");
		urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803150923220770.mp4");
		urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803150922255785.mp4");
		urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803150920130302.mp4");
		urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803141625005241.mp4");
		urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803141624378522.mp4");
		urlList.add("http://chuangfen.oss-cn-hangzhou.aliyuncs.com/public/attachment/201803/100651/201803131546119319.mp4");

		//加载native库
		try {
			IjkMediaPlayer.loadLibrariesOnce(null);
			IjkMediaPlayer.native_profileBegin("libijkplayer.so");
		} catch (Exception e) {
			this.finish();
		}

		snapHelper = new PagerSnapHelper();
		snapHelper.attachToRecyclerView(rvVideo);


		videoAdapter = new ListVideoAdapter(urlList,mContext);
		layoutManager = new VideoViewLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
		rvVideo.setItemViewCacheSize(-1);
		rvVideo.setLayoutManager(layoutManager);
		rvVideo.setAdapter(videoAdapter);
		rvVideo.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
				switch(newState){
					case RecyclerView.SCROLL_STATE_IDLE:
						View view = snapHelper.findSnapView(layoutManager);
						ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
						if (viewHolder != null && viewHolder instanceof VideoViewHolder) {
							((MyVideoPlayer)((VideoViewHolder) viewHolder).getView(R.id.mvp_item)).start();
						}
						break;
					case RecyclerView.SCROLL_STATE_DRAGGING:
						break;
					case RecyclerView.SCROLL_STATE_SETTLING:
						break;
				}
			}
		});

	}

	@Override
	protected void onStop() {
		super.onStop();
		IjkMediaPlayer.native_profileEnd();
	}

	class ListVideoAdapter extends BaseRecAdapter<String, VideoViewHolder> {

		private int lastPosition = 0;

		private Context mContext;

		public ListVideoAdapter(List<String> list,Context mContext) {
			super(list);
			this.mContext = mContext;
		}

		@Override
		public void onHolder(VideoViewHolder holder, String bean, final int position) {
			ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
			layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

			((MyVideoPlayer)holder.getView(R.id.mvp_item)).setListener(new MyVideoPlayerListener() {
				@Override
				public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {

				}

				@Override
				public void onCompletion(IMediaPlayer iMediaPlayer) {

				}

				@Override
				public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
					return false;
				}

				@Override
				public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
					return false;
				}

				@Override
				public void onPrepared(IMediaPlayer iMediaPlayer) {
					if (position == 0)iMediaPlayer.start();
				}

				@Override
				public void onSeekComplete(IMediaPlayer iMediaPlayer) {

				}

				@Override
				public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {

				}
			});
			if (position-lastPosition==1||lastPosition - position >1){
				currentPosition++;
			}else if (lastPosition - position == 1||position-lastPosition >1){
				currentPosition--;
			}
			lastPosition = position;
			if (currentPosition>=urlList.size()){
				currentPosition = 0;
			}
			if (currentPosition<0){
				currentPosition = urlList.size()-1;
			}
			Log.d("testee", "onHolder: "+currentPosition+" "+position +" "+ lastPosition);
			((MyVideoPlayer)holder.getView(R.id.mvp_item)).setVideoPath(urlList.get(currentPosition));
			((TextView)holder.getView(R.id.tv_item)).setText("item"+position+",position"+currentPosition);
		}

		@Override
		public VideoViewHolder onCreateHolder() {
			return null;
		}

		@Override
		public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			VideoViewHolder viewHolder = VideoViewHolder.get(mContext, parent, R.layout.item_video);
			return viewHolder;
		}

		@Override
		public int getItemCount() {
			return 4;
		}
	}
	public static class VideoViewHolder extends ViewHolder {
		private SparseArray<View> mViews;
		private View mConvertView;

		public VideoViewHolder(Context context, View itemview, ViewGroup parent) {
			super(itemview);
			mConvertView = itemview;
			mViews = new SparseArray<View>();
		}

		public static VideoViewHolder get(Context context, ViewGroup parent, int layoutid) {
			Log.d("VideoViewHolder", "get: "+context);
			View itemview = LayoutInflater.from(context).inflate(layoutid, parent, false);
			VideoViewHolder holder = new VideoViewHolder(context, itemview, parent);
			return holder;
		}

		public <T extends View> T getView(int viewid) {
			View view = mViews.get(viewid);
			if (view == null) {
				view = mConvertView.findViewById(viewid);
				mViews.put(viewid, view);
			}
			return (T) view;
		}

	}

	public class VideoViewLayoutManager extends LinearLayoutManager{

		public VideoViewLayoutManager(Context context) {
			super(context);
		}

		public VideoViewLayoutManager(Context context, int orientation, boolean reverseLayout) {
			super(context, orientation, reverseLayout);
		}

		public VideoViewLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
			super(context, attrs, defStyleAttr, defStyleRes);
		}

		@Override
		public RecyclerView.LayoutParams generateDefaultLayoutParams() {
			return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		}

		@Override
		public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
			Log.d("VideoViewLayoutManager","onLayoutChildren ");
			if (getItemCount() == 0){
				detachAndScrapAttachedViews(recycler);
				return;
			}
			//state.isPreLayout()是支持动画的
			if (getItemCount() == 0 && state.isPreLayout()){
				return;
			}
			//将当前Recycler中的view全部移除并放到报废缓存里,之后优先重用缓存里的view
			detachAndScrapAttachedViews(recycler);

			int actualHeight = 0;
			for (int i = 0 ;i < getItemCount() ; i++){
				View scrap = recycler.getViewForPosition(i);
				addView(scrap);
				measureChildWithMargins(scrap,0,0);
				int width = getDecoratedMeasuredWidth(scrap);
				int height = getDecoratedMeasuredHeight(scrap);
				layoutDecorated(scrap,0,actualHeight,width,actualHeight+height);
				actualHeight+=height;
				//超出界面的就不画了,也不add了
				if (actualHeight > getHeight()){
					break;
				}
			}
		}

		@Override
		public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
			//填充
			fill(dy,recycler,state);

			//滚动
			offsetChildrenVertical(dy*-1);

			//回收已经离开界面的
			recycleOut(dy,recycler,state);

			return dy;
		}

		@Override
		public boolean canScrollVertically() {
			return true;
		}

		private void fill(int dy, RecyclerView.Recycler recycler, RecyclerView.State state){
			//界面向下滚动的时候,dy为正,向上滚动的时候dy为负
			//向下滚动
			if (dy > 0){
				//先在底部填充
				View  lastView = getChildAt(getChildCount() -1);
				int lastPos = getPosition(lastView);
				if (lastView.getBottom() -  dy < getHeight()){
					View scrap;
					if (lastPos == getItemCount() -1){
						scrap = recycler.getViewForPosition(0);
					}else {
						scrap = recycler.getViewForPosition(lastPos+1);
					}
					addView(scrap);
					measureChildWithMargins(scrap,0,0);
					int width = getDecoratedMeasuredWidth(scrap);
					int height = getDecoratedMeasuredHeight(scrap);
					layoutDecorated(scrap,0,lastView.getBottom(),width,lastView.getBottom()+height);
				}
			}else {
				//向上滚动
				//现在顶部填充
				View  firstView = getChildAt(0);
				int layoutPostion = getPosition(firstView);

				if (firstView.getTop() >= 0 ){
					View scrap ;
					if (layoutPostion == 0){
						scrap = recycler.getViewForPosition(getItemCount()-1);
					}else {
						scrap = recycler.getViewForPosition(layoutPostion -1);
					}
					addView(scrap,0);
					measureChildWithMargins(scrap,0,0);
					int width = getDecoratedMeasuredWidth(scrap);
					int height = getDecoratedMeasuredHeight(scrap);
					layoutDecorated(scrap,0,firstView.getTop() - height,width,firstView.getTop());
				}
			}
		}
		private void recycleOut(int dy, RecyclerView.Recycler recycler, RecyclerView.State state){
			for (int i = 0 ; i <getChildCount() ;i++){
				View view = getChildAt(i);
				if (dy >0){
					if (dy - view.getBottom() >getHeight()){
						Log.d("testee","recycleOut " + i);
						removeAndRecycleView(view,recycler);
					}
				}else {
					if (view.getTop()-dy > getHeight()){
						Log.d("testee","recycleOut " + i);
						removeAndRecycleView(view,recycler);
					}
				}
			}
		}
	}

}
