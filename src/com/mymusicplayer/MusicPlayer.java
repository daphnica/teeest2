package com.mymusicplayer;

import java.io.File;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.mymusicplayer.bean.MusicBean;
import com.mymusicplayer.broadcast.MyBroadcastReceiver;
import com.mymusicplayer.callbacks.CallbackDeleteEntity;
import com.mymusicplayer.config.Configuration;
import com.mymusicplayer.service.DownloadService;
import com.mymusicplayer.util.MusicUtil;

public class MusicPlayer extends Activity implements OnItemClickListener,
		CallbackDeleteEntity.OnMethodCallback {

	// 竖屏0，横屏2
	public static int nLanorpor = 0;
	// 竖屏0，删除1，横屏2
	public static int nMenuId = 0;
	// 按钮（删除1，返回2，确认删除3）
	public static int nBtnNo = 0;
	// 判断是否已经注册过广播
	public static boolean bFlag = false;
	public static MusicAdapter mMusicAdapter;

	private Configuration config;
	private Date updateTime;

	private ListView mlist;
	private List<MusicBean> mAllMusic;
	private MusicUtil mMusicUtil;

	private MenuInflater mInflater;
	private MyBroadcastReceiver mReceiver;

	// 删除item的物理地址
	private String sSumDeletItem = "";

	private String message = "";
	// 回调
	private CallbackDeleteEntity entity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.e("", "onCreate");
		if (savedInstanceState != null) {
			if (savedInstanceState.getBoolean("isBreak")) {
				Toast.makeText(getApplicationContext(),
						savedInstanceState.getString("BreakTime"),
						Toast.LENGTH_LONG).show();
			}
		}
		/** git test,can you find me?yes,you can!no10 **/

		entity = CallbackDeleteEntity.getInstance();
		entity.setMethodCallback(this);
		config = Configuration.getInstance();
		config.readMe(getApplicationContext());
		Toast.makeText(getApplicationContext(), config.lastEnter,
				Toast.LENGTH_LONG).show();
		updateTime = new Date();

		// 注册onclick
		mlist = (ListView) findViewById(R.id.music_list);
		mlist.setOnItemClickListener(this);

		mReceiver = new MyBroadcastReceiver();
		mMusicUtil = new MusicUtil();
		mMusicUtil.refreshData(this, mReceiver);
		mMusicAdapter = MusicAdapter.getInstance();
		mMusicAdapter.initAdapter(getApplicationContext(), null);
		mlist.setAdapter(mMusicAdapter);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.e("", "onStart");
		MusicPlayer.bFlag = false;
		// mMusicUtil.refreshData(this, mReceiver);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.e("", "onRestart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e("", "onResume");
		if (!message.isEmpty()) {
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
					.show();
			message = "";
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.e("", "onStop");
		// 注销广播
		if (MusicPlayer.bFlag) {
			unregisterReceiver(mReceiver);
		}
	}

	@Override
	protected void onPause() {
		Log.e("", "onPause");
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.e("", "onDestroy");
		getUpdateTime();
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int pos, long arg3) {

		mAllMusic = mMusicUtil.getAllMusic(getApplicationContext());
		Intent intent = new Intent(this, MusicDetail.class);
		intent.putExtra("MusicName", mAllMusic.get(pos).getMusic_name());
		intent.putExtra("MusicSinger", mAllMusic.get(pos).getSinger());
		intent.putExtra("MusicPath", mAllMusic.get(pos).getPath());
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mInflater = getMenuInflater();
		mInflater.inflate(R.menu.pormain, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		Log.e("", "onPrepareOptionsMenu");
		menu.clear();
		switch (nMenuId) {
		case 0:
			// 竖屏
			mInflater.inflate(R.menu.pormain, menu);
			break;
		case 1:
			// 删除
			mInflater.inflate(R.menu.deletemenu, menu);
			break;
		case 2:
			// 横屏
			mInflater.inflate(R.menu.lanmain, menu);
			break;
		default:
			break;
		}
		return super.onPrepareOptionsMenu(menu);

	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		Log.e("", "onMenuOpened");
		return true;
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
		Log.e("", "onOptionsMenuClosed");
		mlist.setOnItemClickListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals(
				mMusicUtil.stringFormat(this, R.string.download))) {
			startService(new Intent(getApplicationContext(),
					DownloadService.class));
		} else if (item.getTitle().equals(
				mMusicUtil.stringFormat(this, R.string.delete))) {
			nBtnNo = 1;
			mMusicUtil.refreshData(getApplicationContext(), mReceiver);
		} else if (item.getTitle().equals(
				mMusicUtil.stringFormat(this, R.string.back))) {
			nBtnNo = 2;
			mMusicUtil.refreshData(getApplicationContext(), mReceiver);
		} else if (item.getTitle().equals(
				mMusicUtil.stringFormat(this, R.string.deleteComfirm))) {
			nBtnNo = 3;
			String[] delItem = sSumDeletItem.substring(0,
					sSumDeletItem.length() - 1).split(",");
			// 文件物理删除
			for (String d : delItem) {
				File f = new File(d);
				f.delete();
			}
			mMusicUtil.refreshData(getApplicationContext(), mReceiver);
		} else if (item.getTitle().equals(
				mMusicUtil.stringFormat(this, R.string.lan))) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			nMenuId = 2;
			nLanorpor = 2;
		} else if (item.getTitle().equals(
				mMusicUtil.stringFormat(this, R.string.por))) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			nMenuId = 0;
			nLanorpor = 0;
		} else {
			// TODO
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		String year = String.valueOf(1900 + updateTime.getYear());
		String month = String.valueOf(updateTime.getMonth() + 1);
		String day = String.valueOf(updateTime.getDate());
		String hour = String.valueOf(updateTime.getHours());
		String min = String.valueOf(updateTime.getMinutes());
		String sec = String.valueOf(updateTime.getSeconds());
		outState.putString("BreakTime", "崩溃时间为:" + year + "-" + month + "-"
				+ day + " " + hour + ":" + min + ":" + sec);
		outState.putBoolean("isBreak", true);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		message = savedInstanceState.getString("BreakTime");
	}

	private void getUpdateTime() {
		String year = String.valueOf(1900 + updateTime.getYear());
		String month = String.valueOf(updateTime.getMonth() + 1);
		String day = String.valueOf(updateTime.getDate());
		String hour = String.valueOf(updateTime.getHours());
		String min = String.valueOf(updateTime.getMinutes());
		String sec = String.valueOf(updateTime.getSeconds());
		config.lastEnter = "上次登录时间为:" + year + "-" + month + "-" + day + " "
				+ hour + ":" + min + ":" + sec;
		config.saveMe(getApplicationContext());
	}

	@Override
	public void doCallBackDeleteItem(String item) {
		if (item.split(",")[0].equals("add")) {
			sSumDeletItem = sSumDeletItem + item.split(",")[1] + ",";
		} else {
			sSumDeletItem = sSumDeletItem.replace(item.split(",")[1] + ",", "");
		}
	}
}
