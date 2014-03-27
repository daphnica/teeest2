package com.mymusicplayer.download;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.mymusicplayer.MusicPlayer;
import com.mymusicplayer.R;
import com.mymusicplayer.bean.DownloadBean;
import com.mymusicplayer.broadcast.MyBroadcastReceiver;
import com.mymusicplayer.callbacks.CallbackProcessEntity;
import com.mymusicplayer.util.Constants;
import com.mymusicplayer.util.MusicUtil;

public class DownloadTask implements CallbackProcessEntity.OnMethodCallback{
	
	private ConnectivityManager connectivityManager;
	private NetworkInfo mMobileInfo,mWifiInfo,mActiveInfo;
	//进度
	private int nProgress = 0;
	
	private MusicUtil mMusicUtil;
	private MyBroadcastReceiver mReceiver;
	
	private DownloadObserver mDownloadObserver;
	private DownloadManager mDownloadManager;
	private long mId;
	
	private PendingIntent mPendingIntent;
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	
	//判断是否开始下载
	private boolean toFlag = false;
	
	private List<Map<Long,Integer>> downloadFileList;
	private List<DownloadBean> downloadIdList;
	private List<DownloadBean> tempList;
	
//	private MyDownloadAsyncTask task;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
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
//					if(downloadIdList.size()==0){
//						mMusicUtil.refreshData(context, mReceiver);
//					}
					tempList.clear();
					break;
				default : 
					break;
			}
		};
	};
	
	private Thread thread;
	private Context context;
	
	public DownloadTask(Context context) {
		this.context = context;
	}
	
	public void initTask(){
		mReceiver = new MyBroadcastReceiver();
		mMusicUtil = new MusicUtil();
		downloadFileList = new ArrayList<Map<Long,Integer>>();
		downloadIdList = new ArrayList<DownloadBean>();
		tempList = new ArrayList<DownloadBean>();
		
		//初始化notification
		mPendingIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
		mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		mNotification = new Notification();
		//初始化网络监听
		connectivityManager = (ConnectivityManager)      
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		//gprs
		mMobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		//wifi
		mWifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		//active(gprs or wifi)
		mActiveInfo = connectivityManager.getActiveNetworkInfo();
		
		mDownloadObserver = new DownloadObserver(context,handler,
				mMusicUtil,mReceiver) ;
		mDownloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
		//注册ContentObserver
		context.getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"),
				true, mDownloadObserver);
		
		CallbackProcessEntity entity = CallbackProcessEntity.getInstance();
		entity.setMethodCallback(this);
	}
	
	public void runDownload(){
		MusicPlayer.bFlag = false;
		thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(mWifiInfo.isConnected()||mMobileInfo.isConnected()){
					Uri srcUri = Uri.parse("http://192.168.15.108:8080/C04.4/upload/test.mp3");
					DownloadManager.Request request = new Request(srcUri);
					request.setDestinationInExternalPublicDir("download456", "test.mp3");
					request.setDescription("下载");
					request.setTitle("titleTest");
					//AndroidManifest.xml里面要注册
					//<uses-permission android:name="android.permission.DOWNLOAD_WITHOUT_NOTIFICATION"/>
					request.setShowRunningNotification(false);
					mId = mDownloadManager.enqueue(request);
					DownloadBean dBean = new DownloadBean();
					dBean.setId(mId);
					downloadIdList.add(dBean);
					mDownloadObserver.setDownloader(mDownloadManager,downloadIdList);
					makeNotification();
				}else{
					Toast.makeText(context, "暂无可用网络", 1000).show();
				}
			}
		});
		handler.post(thread);
	}
	
	//初始化notification
	private void makeNotification(){
		mNotification.icon = R.drawable.ic_launcher;
		mNotification.tickerText = mMusicUtil.stringFormat(context, R.string.downloadStarted);
		toFlag = false;
	}
	
	public void doDestroy(){
		context.getContentResolver().unregisterContentObserver(mDownloadObserver);
	}

	@Override
	public void doCallBackProcessItem(int item,List<Map<Long,Integer>> list,long id) {
		this.downloadFileList = list;
	}
}
