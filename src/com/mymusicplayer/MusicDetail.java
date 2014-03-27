package com.mymusicplayer;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.mymusicplayer.bean.MusicBean;
import com.mymusicplayer.util.MusicUtil;
import com.mymusicplayer.util.Player;

public class MusicDetail extends Activity implements android.view.View.OnClickListener {

	private TextView mDetailName;
	private TextView mDetailSinger;
	private ImageButton mDetailStart;
	private ImageButton mDetailPause;
	private ImageButton mDetailStop;
	private ImageButton mDetailLast;
	private ImageButton mDetailNext;
	private SeekBar mSeekBar;
	
	private List<MusicBean> mAllMusic;
	private Player mPlayer;
	
	private int mMusicNum = 0;
	private String mArtistName;
	private String mMusicName;
	private String mMusicPath;
	
	private Intent mIntent;
	private final Handler handler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_detail);
		init();
		
	}
	
	
	@Override
	public void onClick(View v) {

		switch (v.getId()){
		case R.id.detail_start :
			Player.mediaPlayer.start();
			handler.post(updateMusic);
			break;
		case R.id.detail_pause :
			Player.mediaPlayer.pause();
			break;
		case R.id.detail_stop :
//			player.stop();
			Player.mediaPlayer.stop();
			Player.mediaPlayer.seekTo(0);
			mSeekBar.setProgress(0);
			try {
				Player.mediaPlayer.reset();
				Player.mediaPlayer.setDataSource(mAllMusic.get(0).getPath());
				Player.mediaPlayer.prepare();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			handler.removeCallbacks(updateMusic);
			break;
		case R.id.detail_last :
			mMusicNum = mPlayer.getNowMusic();
			mMusicNum--;
			if (mMusicNum < 0) {
				mMusicNum = mAllMusic.size() - 1;
			}
			mArtistName = mAllMusic.get(mMusicNum).getSinger();
			mMusicName = mAllMusic.get(mMusicNum).getMusic_name();
			mMusicPath = mAllMusic.get(mMusicNum).getPath();
			mPlayer.setMusicName(mDetailSinger, mDetailName);
			mPlayer.setMusic(mAllMusic);
			mPlayer.previousMusic(mMusicNum);
			break;
		case R.id.detail_next :
			mMusicNum = mPlayer.getNowMusic();
			mMusicNum++;
			if (mMusicNum >= mAllMusic.size()) {
				mMusicNum = 0;
			}
			mArtistName = mAllMusic.get(mMusicNum).getSinger();
			mMusicName = mAllMusic.get(mMusicNum).getMusic_name();
			mMusicPath = mAllMusic.get(mMusicNum).getPath();
			mPlayer.setMusicName(mDetailSinger, mDetailName);
			mPlayer.setMusic(mAllMusic);
			mPlayer.nextMusic(mMusicNum);
			break;
		default:
			break;
		}
	}
	
	private void init(){
		mDetailName=(TextView) findViewById(R.id.detail_name);
		mDetailSinger=(TextView) findViewById(R.id.detail_singer);
		mDetailStart=(ImageButton) findViewById(R.id.detail_start);
		mDetailPause=(ImageButton) findViewById(R.id.detail_pause);
		mDetailStop=(ImageButton) findViewById(R.id.detail_stop);
		mDetailLast=(ImageButton) findViewById(R.id.detail_last);
		mDetailNext=(ImageButton) findViewById(R.id.detail_next);
		mSeekBar=(SeekBar) findViewById(R.id.seekbar);
		
		mDetailStart.setOnClickListener(this);
		mDetailPause.setOnClickListener(this);
		mDetailStop.setOnClickListener(this);
		mDetailLast.setOnClickListener(this);
		mDetailNext.setOnClickListener(this);
		
		mAllMusic = new MusicUtil().getAllMusic(getApplicationContext());
//		mediaplayer = MediaPlayer.create(getApplicationContext(), R.raw.boa);
		
		mIntent = getIntent();
		mDetailName.setText(mIntent.getStringExtra("MusicName"));
		mDetailSinger.setText(mIntent.getStringExtra("MusicSinger"));
		
		try {
			mPlayer = Player.getInstance();
			if(Player.mediaPlayer.isPlaying()){
				Player.mediaPlayer.stop();
				Player.mediaPlayer.reset();
			}
			Player.mediaPlayer.setDataSource(mIntent.getStringExtra("MusicPath"));
			Player.mediaPlayer.prepare();
			Player.mediaPlayer.start();
			handler.post(updateMusic);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		mSeekBar.setMax(Player.mediaPlayer.getDuration());
		Log.e("progress", String.valueOf(Player.mediaPlayer.getDuration()));
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			int progress;
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Log.e("onStopTrackingTouch", String.valueOf(progress));
				Player.mediaPlayer.seekTo(progress);
				seekBar.setProgress(progress);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
//				if(fromUser){
//					mediaplayer.seekTo(progress);
//				}
//				this.progress=progress*Player.mediaPlayer.getDuration()/seekBar.getMax();
				this.progress = progress * mPlayer.mediaPlayer.getDuration()
						/ seekBar.getMax();
//				Log.e("onProgressChanged", String.valueOf(progress));
			}
		});
		
	}
	
	private Thread updateMusic = new  Thread(new Runnable() {
		
		@Override
		public void run() {
			mSeekBar.setProgress(Player.mediaPlayer.getCurrentPosition());
			handler.postDelayed(updateMusic, 100);
		}
	});
}
