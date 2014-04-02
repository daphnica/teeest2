package com.mymusicplayer.download;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.DownloadManager;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mymusicplayer.bean.DownloadBean;
import com.mymusicplayer.broadcast.MyBroadcastReceiver;
import com.mymusicplayer.callbacks.CallbackProcessEntity;
import com.mymusicplayer.util.Constants;
import com.mymusicplayer.util.MusicUtil;

public class DownloadObserver extends ContentObserver{

	private DownloadManager cDownloadManager;
	
	//下载完成状态
	private int nStatus = -100;
	
	private DownloadManager.Query mBaseQuery;
//	private boolean completeFlag = false;
	
	private Context context;
	private MusicUtil mMusicUtil;
	private MyBroadcastReceiver mReceiver;
	private CallbackProcessEntity entity;
	private Handler handler;
	
	private List<Map<Long,Integer>> downloadFileList;
	private List<DownloadBean> downloadIdList;
	private Map<Long,Integer> map;
	
	public DownloadObserver(Context context,Handler handler,
			MusicUtil musicUtil,MyBroadcastReceiver receiver) {
		super(handler);
		this.context = context;
		this.handler = handler;
		this.mMusicUtil = musicUtil;
		this.mReceiver = receiver;
		entity = CallbackProcessEntity.getInstance();
		map = new HashMap<Long, Integer>();
		downloadFileList = new ArrayList<Map<Long,Integer>>();
	}
	
	public void setDownloader(DownloadManager downloadManager,List<DownloadBean> downloadIdList) {
		this.cDownloadManager = downloadManager;
		this.downloadIdList = downloadIdList;
		nStatus = -100;
//		completeFlag = false;
	}
	
	@Override
	public void onChange(boolean selfChange) {
		if(nStatus!=Constants.COLUMN_STATUS_SUCCESS){
			updateDownloadChanged();
		}
	}
	
	private void updateDownloadChanged(){
		for(DownloadBean bean : downloadIdList){
			mBaseQuery = new DownloadManager.Query().setFilterById(bean.getId());
			if(cDownloadManager!=null){
				Cursor cursor = cDownloadManager.query(mBaseQuery);
				
				int mIdColumnId = cursor
						.getColumnIndexOrThrow(DownloadManager.COLUMN_ID);
				
				int nStatusColumnId = cursor
						.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS);
				int mTotalBytesColumnId = cursor
						.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
				int mCurrentBytesColumnId = cursor
						.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
				int mCurrentFileUriColumnId = cursor
						.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI);
				int mCurrentFileNameColumnId = cursor
						.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_FILENAME);
				
				long downloadId = -1;
				String fName = "";
				String uri = "";
				while (cursor.moveToNext()) {
					
					downloadId = cursor.getLong(mIdColumnId);
					long totalBytes = cursor.getLong(mTotalBytesColumnId);
					long currentBytes = cursor.getLong(mCurrentBytesColumnId);
					int status = cursor.getInt(nStatusColumnId);
					uri = cursor.getString(mCurrentFileUriColumnId);
//					fName = cursor.getString(mCurrentFileNameColumnId);
					
					//记录下载百分比
					int progress = getProgressValue(totalBytes, currentBytes);
					Log.e("cursor", downloadId+"---"+progress+"%");
					if(map.containsKey(downloadId)){
						downloadFileList.remove(map);
					}
					map.put(downloadId, progress);
					downloadFileList.add(map);
					entity.invokeMethod(progress,downloadFileList,downloadId);
					//记录下载状态
					nStatus = status;
					bean.setStatus(status);
				}
				
				Message msg = new Message();
				//下载状态为完成时的操作
				if(bean.getStatus()==DownloadManager.STATUS_SUCCESSFUL){
					msg.what=Constants.QUERY_DOWNLOAD_COMPLETE;
					if(!bean.isCompleteFlag()){
						mMusicUtil.refreshFile(context, mReceiver,uri);
//						mMusicUtil.refreshData(context, mReceiver);
						bean.setCompleteFlag(true);
						Log.e("refresh", bean.getId()+"--refresh");
					}
				}else{
					msg.what=Constants.QUERY_DOWNLOAD_PROGRESS;
					Log.e("running", "running");
				}
				cursor.close();
				handler.sendMessage(msg);
				
			}
		}
	}
	
	public int getProgressValue(long totalBytes, long currentBytes) {
		if (totalBytes == -1) {
			return 0;
		}
		return (int) (currentBytes * 100 / totalBytes);
	}
	
}