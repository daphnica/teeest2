package com.mymusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.mymusicplayer.download.DownloadTask;

public class DownloadService extends Service{

	private DownloadTask downloadTask;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("", "onCreate--service");
		
		downloadTask = new DownloadTask(this);
		downloadTask.initTask();
	}
	
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.e("", "onStart--service");
		downloadTask.runDownload();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		downloadTask.doDestroy();
	}
	
}
