package com.mymusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.mymusicplayer.download.DownloadResumeTask;
import com.mymusicplayer.download.DownloadTask;

public class DownloadService extends Service{

	private DownloadTask downloadTask;
	private DownloadResumeTask downloadResumeTask;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("", "onCreate--service");
//		downloadTask = new DownloadTask(this);
//		downloadTask.initTask();
		
		downloadResumeTask = new DownloadResumeTask(this);
	}
	
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.e("", "onStart--service");
//		downloadTask.runDownload();
		
		try {
			downloadResumeTask.startDownload(2,"http://192.168.15.108:8080/C04.4/upload/test.mp3");
//			downloadResumeTask.startDownload(3,"http://192.168.15.108:8080/C04.4/upload/boa.mp3");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		downloadTask.doDestroy();
	}
	
}
