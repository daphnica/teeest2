package com.mymusicplayer.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.mymusicplayer.MusicPlayer;
import com.mymusicplayer.bean.MusicBean;
import com.mymusicplayer.broadcast.MyBroadcastReceiver;

public class MusicUtil {
	
	public MusicUtil() {
	}
	

	public void refreshData(Context context,MyBroadcastReceiver receiver){
		if(!MusicPlayer.bFlag){
			IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
			filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			filter.addDataScheme("file");
			context.registerReceiver(receiver, filter);
			MusicPlayer.bFlag = true;
		}
			context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
					Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath())));
	}
	
	public void refreshFile(Context context,MyBroadcastReceiver receiver,String uri){
		if(!MusicPlayer.bFlag){
			IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			filter.addDataScheme("file");
			context.registerReceiver(receiver, filter);
			MusicPlayer.bFlag = true;
		}
			context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
					Uri.parse(uri)));
	}
	
	
	public List<MusicBean> getAllMusic(Context context) {
		List<MusicBean> allMusic = new ArrayList<MusicBean>();
		MusicBean musicBean = null;
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] {MediaStore.Audio.Media.ARTIST, 
						MediaStore.Audio.Media.TITLE, 
						MediaStore.Audio.Media.DATA, 
						MediaStore.Audio.Media.DURATION, 
				},
				null,
				null,
				null);
		if (null != cursor) {
			while (cursor.moveToNext()) {
				String artistName = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.ARTIST));
				String musicName = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.TITLE));
				String musicPath = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.DATA));
				String musicDuration = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.DURATION));
				musicBean = new MusicBean();
				musicBean.setMusic_name(musicName);
				musicBean.setPath(musicPath);
				musicBean.setSinger(artistName);
				musicBean.setDuration(calcDuration(musicDuration));
				allMusic.add(musicBean);
			}
		}
		return allMusic;
	}
	
	public String calcDuration(String duration){
		int d = Integer.parseInt(duration);
		return String.valueOf(d/1000/60)+":"+String.valueOf(d/1000%60);
	}
	
	public String stringFormat(Context context,int id){
		return context.getResources().getString(id);
	}

}
