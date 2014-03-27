package com.mymusicplayer.util;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mymusicplayer.bean.MusicBean;

public class Player implements OnBufferingUpdateListener, OnCompletionListener,
		MediaPlayer.OnPreparedListener, SurfaceHolder.Callback {
	public static Player instance;
	private int videoWidth;
	private int videoHeight;
	public static MediaPlayer mediaPlayer;
	private SurfaceHolder surfaceHolder = null;
	private SeekBar skbProgress;
	private final String LOG = "Player";
	private Timer mTimer;
	private TextView mEnd;
	private TextView start;
	private TextView artist;
	private TextView music_name;
	private int nowMusic = 0;
	
	private Context context;
	private List<MusicBean> allMusic;
	public MusicUtil musicUtil;

	public Player(){
		
	}
	
	/**
	 * 单态
	 */
	public static Player getInstance() {
		if (instance == null) {
			instance = new Player();
			mediaPlayer = new MediaPlayer();
		}
		return instance;
	}
	
	public void setView(SeekBar music_skbProgress) {
		this.skbProgress = music_skbProgress;
		mTimer = new Timer();
		TimerTask mTimerTask = new TimerTask() {
			@Override
			public void run() {
				if (mediaPlayer == null)
					return;
				try {
					if (mediaPlayer.isPlaying() && skbProgress.isPressed() == false) {
						handleProgress.sendEmptyMessage(0);
					}
				} catch (IllegalStateException e) {
					e.printStackTrace();
					Log.e(LOG, e.toString());
//					mediaPlayer.stop();
				}
			}
		};
		mTimer.schedule(mTimerTask, 100, 1000);
	}

	public void setView(SurfaceView surfaceView, SeekBar skbProgres, Context context) {
		this.skbProgress = skbProgres;
		this.context = context;
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		// surfaceHolder.setFixedSize(100, 100);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		TimerTask mTimerTask = new TimerTask() {
			@Override
			public void run() {
				if (mediaPlayer == null)
					return;
				try {
					if (mediaPlayer.isPlaying() && skbProgress.isPressed() == false) {
						handleProgress.sendEmptyMessage(0);
					}
				} catch (IllegalStateException e) {
					e.printStackTrace();
					Log.e(LOG, e.toString());
					mediaPlayer.stop();
				}
			}
		};
		mTimer.schedule(mTimerTask, 100, 1000);
	}
	
	public void setTime(TextView start , TextView end){
		this.start = start;
		this.mEnd = end;
	}

	
	Handler handleProgress = new Handler() {
		public void handleMessage(Message msg) {
			if (mediaPlayer != null) {
				int position = mediaPlayer.getCurrentPosition();
				int duration = mediaPlayer.getDuration();

				if (duration > 0) {
					if(null != start && null != mEnd){
						int a = (duration / 1000) / 60;
						int x = (duration / 1000) % 60;
						int a1 = (position / 1000) / 60;
						int x1 = (position / 1000) % 60;
						start.setText(a1 + ":" + "" + x1);
						mEnd.setText(a + ":" + "" + x);
					}
					if (duration == position) {
						skbProgress.setProgress(skbProgress.getMax());
					} else {
						long pos = skbProgress.getMax() * position / duration;
						skbProgress.setProgress((int) pos);
					}
				}
			}
		};
	};

	public void play() {
		if (null != mediaPlayer) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
			} else {
				mediaPlayer.start();
			}
		}
	}

	public void playUrl(String videoUrl) {
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(videoUrl);
			mediaPlayer.prepare();// prepare之后自动播放
			mediaPlayer.start();
			if(null != artist){
				artist.setText(allMusic.get(nowMusic).getSinger());
				music_name.setText(allMusic.get(nowMusic).getMusic_name());
			}
			Log.e(LOG, "playUrl start ");
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			Log.e(LOG, "stop mediaPlayer is not null");
//			mediaPlayer = null;

		}
	}
	
	public void setMusic(List<MusicBean> allMusic){
		this.allMusic = allMusic;
	}
	
	public void nextMusic(int next){
		if(null != allMusic && allMusic.size() > 0){
			if(next > allMusic.size() - 1){
				next = 0;
			}
			nowMusic = next; 
			playUrl(allMusic.get(next).getPath());
			Log.e(LOG, "nextMusic start ");
		}
	}
	
	public void previousMusic(int previous){
		if(null != allMusic && allMusic.size() > 0){
			if(nowMusic <= 0){
				previous = allMusic.size() - 1;
			}
			nowMusic = previous;
			playUrl(allMusic.get(previous).getPath());
		}
	}
	
	public int getNowMusic(){
		return nowMusic;
	}
	
	public void setNowMusic(int nowMusic){
		this.nowMusic = nowMusic;
	}
	
	public void setMusicName(TextView artist , TextView music_name){
		this.artist = artist;
		this.music_name = music_name;
	}

	public void isplay() {
		try {
			if (null != surfaceHolder) {
				mediaPlayer.setDisplay(surfaceHolder);
			}
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnCompletionListener(this);
		} catch (Exception e) {
			Log.e("mediaPlayer", "error", e);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		Log.e("mediaPlayer", "surface changed");
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		isplay();
		Log.e("mediaPlayer", "surface created");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		Log.e("mediaPlayer", "surface destroyed");
	}

	@Override
	/**  
	 * 通过onPrepared播放  
	 */
	public void onPrepared(MediaPlayer arg0) {
		videoWidth = mediaPlayer.getVideoWidth();
		videoHeight = mediaPlayer.getVideoHeight();
		if (videoHeight != 0 && videoWidth != 0) {
			arg0.start();
		}
		Log.e("mediaPlayer", "onPrepared");
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		if(null!=skbProgress){
			skbProgress.setProgress(0);
		}
		nowMusic++;
		nextMusic(nowMusic);
		Log.e(LOG, "nowMusic : "+nowMusic);
	}

	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
		skbProgress.setSecondaryProgress(bufferingProgress);
		int currentProgress = skbProgress.getMax()
				* mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
		Log.e(currentProgress + "% play", bufferingProgress + "% buffer");

	}
	
	//循环播放
	public void LoopMusic(){  
		if(Player.mediaPlayer.isPlaying()){  
			Player.mediaPlayer.setLooping(true);  
			Player.mediaPlayer.start();  
		}  
    } 
}