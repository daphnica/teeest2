package com.mymusicplayer.download;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mymusicplayer.R;
import com.mymusicplayer.bean.DownloadBean;
import com.mymusicplayer.util.Constants;
import com.mymusicplayer.util.MusicUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

public class DownloadHandler extends Handler{

	private Context context;
	
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private MusicUtil mMusicUtil;
	private PendingIntent mPendingIntent;
	private List<Map<Long,Integer>> downloadFileList;
	private List<DownloadBean> downloadIdList;
	private List<DownloadBean> tempList;
	
	public DownloadHandler(Context context) {
		this.context = context;
	}
	
	public void initHandler(){
		downloadFileList = new ArrayList<Map<Long,Integer>>();
		downloadIdList = new ArrayList<DownloadBean>();
		tempList = new ArrayList<DownloadBean>();
		mMusicUtil = new MusicUtil();
		mPendingIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
		mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		mNotification = new Notification();
	}
	
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case Constants.QUERY_DOWNLOAD_PROGRESS:
			for(DownloadBean bean:downloadIdList){
				for(Map<Long,Integer> map:downloadFileList){
					mNotification.contentView = new RemoteViews(context
							.getPackageName(), R.layout.custom_dialog);
					mNotification.contentView.setProgressBar(R.id.notiBar, 100, map.get(bean.getId()), false);
					mNotification.contentView.setTextViewText(R.id.notiText,
							mMusicUtil.stringFormat(context,R.string.process) + map.get(bean.getId())+ "%");
					mNotification.contentIntent = mPendingIntent;
					mNotificationManager.notify(Integer.parseInt(String.valueOf(bean.getId())), mNotification);
				}
			}
			break;
		case Constants.QUERY_DOWNLOAD_COMPLETE :
			for(DownloadBean bean : downloadIdList){
				if(bean.isCompleteFlag()){
					tempList.add(bean);
					Toast.makeText(context, "下载完成", Toast.LENGTH_LONG).show();
					mNotificationManager.cancel(Integer.parseInt(String.valueOf(bean.getId())));
				}
			}
			for(DownloadBean bean : tempList){
				downloadIdList.remove(bean);
			}
			tempList.clear();
			break;
		default : 
			break;
	}
	}
}
