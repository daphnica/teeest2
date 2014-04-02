package com.mymusicplayer.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.mymusicplayer.bean.DownloadBean;
import com.mymusicplayer.bean.ResumeInfo;
import com.mymusicplayer.db.InfoDao;
import com.mymusicplayer.util.Constants;
import com.mymusicplayer.util.MusicUtil;

public class DownloadResumeTask {

	private Long mFinishedBytes = 0l;
	private Long mFileLen;
	private Context context;
	private InfoDao dao;
	private boolean isPause = false;
	private List<String> pathList;

	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private MusicUtil mMusicUtil;
	private PendingIntent mPendingIntent;
	private List<Map<Long, Integer>> downloadFileList;
	private List<DownloadBean> downloadIdList;
	private List<DownloadBean> tempList;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constants.QUERY_DOWNLOAD_PROGRESS:
//				for (DownloadBean bean : downloadIdList) {
//					for (Map<Long, Integer> map : downloadFileList) {
						if(msg.getData().getLong("finishedBytes")==mFileLen){
							Log.e("download done", "download done");
							return;
						}
//						mNotification.contentView = new RemoteViews(
//								context.getPackageName(),
//								R.layout.custom_dialog);
//						mNotification.contentView.setProgressBar(R.id.notiBar,
//								100, msg.getData().getByte("finishedBytes"), false);
//						mNotification.contentView.setTextViewText(
//								R.id.notiText,
//								mMusicUtil.stringFormat(context,
//										R.string.process)
//										+ msg.getData().getByte("finishedBytes") + "%");
//						mNotification.contentIntent = mPendingIntent;
//						mNotificationManager.notify(
//								Integer.parseInt(String.valueOf(bean.getId())),
//								mNotification);
//					}
//				}
				break;
			case Constants.QUERY_DOWNLOAD_COMPLETE:
				for (DownloadBean bean : downloadIdList) {
					if (bean.isCompleteFlag()) {
						tempList.add(bean);
						Toast.makeText(context, "下载完成", Toast.LENGTH_LONG)
								.show();
						mNotificationManager.cancel(Integer.parseInt(String
								.valueOf(bean.getId())));
					}
				}
				for (DownloadBean bean : tempList) {
					downloadIdList.remove(bean);
				}
				tempList.clear();
				break;
			default:
				break;
			}
		};
	};

	public DownloadResumeTask(Context context) {
		this.context = context;
		dao = new InfoDao(context);

		downloadFileList = new ArrayList<Map<Long, Integer>>();
		downloadIdList = new ArrayList<DownloadBean>();
		tempList = new ArrayList<DownloadBean>();
		mMusicUtil = new MusicUtil();
		mPendingIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
		mNotificationManager = (NotificationManager) context
				.getSystemService(context.NOTIFICATION_SERVICE);
		mNotification = new Notification();
	}
	
	public void startDownload(int thCount,String newPath) throws Exception {
		pathList = dao.queryUndone();// 判断上次没有下载完的任务
//		pathList = new ArrayList<String>();
		pathList.add(newPath);
		for (String path : pathList) {
			URL url = new URL(path);
			HttpClient httpClient = new DefaultHttpClient();// 下面几段代码是为了连接服务器
			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),
					20 * 1000);
			HttpPost post = new HttpPost(path);
			HttpResponse response = httpClient.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {// 如果返回200表明连接成功
				mFileLen = response.getEntity().getContentLength();
				String name = path.substring(path.lastIndexOf("/") + 1);
				File file = new File(Environment.getExternalStorageDirectory()
						+ "/download456", name);
				if (!file.exists()) {
					File f = new File(Environment.getExternalStorageDirectory()
							+ "/download456");
					f.mkdir();
					file.createNewFile();
				}
				Long partLen = (mFileLen + thCount - 1) / thCount;
				for (int i = 0; i < thCount; i++) {
					Long start = i*partLen;
					Long end = start + partLen;
					new DownloadThread(url, file,start, end, i).start();
				}
			} else {
				throw new IllegalArgumentException("404 path: " + path);
			}
		}
	}

	private final class DownloadThread extends Thread {
		private URL url;
		private File file;
		private Long start;
		private Long end;
		private int id;

		public DownloadThread(URL url, File file,Long start, Long end, int id) {
			this.url = url;
			this.file = file;
			this.start = start;
			this.end = end;
			this.id = id;
		}

		@Override
		public void run() {
			ResumeInfo info = dao.query(url.toString(), id);// 查询记录当中没有下完的任务
			if (info != null) {
				mFinishedBytes = info.getFinishedBytes();
				start = mFinishedBytes+start;
			} else {
				info = new ResumeInfo(url.toString(), id, 0l);
				dao.insert(info);
			}
//			Long start = info.getFinishedBytes();// 开始位置 = 已下载量
//			Long end = partLen - 1;
			
			
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(
						httpClient.getParams(), 20 * 1000);
				HttpPost post = new HttpPost(url.toString());
				post.setHeader("Range", "bytes=" + start + "-" + end); // 为上次没有下载完成的任务请求下载的起始位置
				HttpResponse response = httpClient.execute(post);

				RandomAccessFile raf = new RandomAccessFile(file, "rws");
				raf.seek(start);
				InputStream in = response.getEntity().getContent();// 获取输入流，写入文件
				byte[] buf = new byte[1024 * 10];
				int len;
				while ((len = in.read(buf)) != -1) {
					raf.write(buf, 0, len);
					mFinishedBytes += len;
//					Log.e("writebyte", Thread.currentThread().getName()+"--"+String.valueOf(mFinishedBytes));
					info.setFinishedBytes(mFinishedBytes);
					dao.update(info);
					Message msg = new Message();
					msg.what = Constants.QUERY_DOWNLOAD_PROGRESS;
					msg.getData().putLong("finishedBytes", mFinishedBytes);
					handler.sendMessage(msg); // 每次读取一定长度的文件内容后，更新进度条的进度
//					if (isPause) {
//						synchronized (dao) {
//							try {
//								// 暂停时该线程进入等待状态，并释放dao的锁
//								dao.wait();
//								// 重新连接服务器，在wait时可能丢失了连接，如果不加这一段代码会出现connection。。。。。peer的错误
//								httpClient = new DefaultHttpClient();
//								HttpConnectionParams.setConnectionTimeout(
//										httpClient.getParams(), 20 * 1000);
//								post = new HttpPost(url.toString());
//								post.setHeader("Range", "bytes="
//										+ mFinishedBytes + "-" + end);
//								response = httpClient.execute(post);
//								raf.seek(mFinishedBytes);
//								in = response.getEntity().getContent();
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
//					}
				}
				in.close();
				raf.close();
				// 删除下载记录
				dao.deleteAll(info.getPath(), mFileLen);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 暂停下载
	public void pause() {
		isPause = true;
	}

	// 继续下载
	public void resumeDownload() {
		isPause = false;
		// 恢复所有线程
		synchronized (dao) {
			dao.notifyAll();
		}
	}

	// 删除下载
	public void delete(String path) {
		isPause = true;
		synchronized (dao) {
			dao.deleteUndone(path);
		}
	}

}
