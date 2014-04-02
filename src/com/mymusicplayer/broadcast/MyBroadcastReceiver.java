package com.mymusicplayer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.mymusicplayer.MusicPlayer;

public class MyBroadcastReceiver extends BroadcastReceiver {

	private String action;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		action=intent.getAction();
		if(action.equals(Intent.ACTION_MEDIA_SCANNER_STARTED)){
			Log.e("", "scanner starts");
		}else if(action.equals(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)){
			Log.e("", "scanner file");
			try {
				Thread.sleep(500);
				if(MusicPlayer.mMusicAdapter==null){
					return;
				}
				switch(MusicPlayer.nBtnNo){
				case 1 : 
					MusicPlayer.mMusicAdapter.initAdapter(context,"isDelete");
					MusicPlayer.mMusicAdapter.notifyDataSetChanged();
					MusicPlayer.nMenuId=1;
					break;
				default :
					MusicPlayer.mMusicAdapter.initAdapter(context,null);
					MusicPlayer.mMusicAdapter.notifyDataSetChanged();
					MusicPlayer.nMenuId=MusicPlayer.nLanorpor;
					Log.e("BroadcastReceiver", "BroadcastReceiver refresh");
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else if(action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)){
			Log.e("", "scanner finish");
			if(MusicPlayer.mMusicAdapter==null){
				return;
			}
			switch(MusicPlayer.nBtnNo){
			case 1 : 
				MusicPlayer.mMusicAdapter.initAdapter(context,"isDelete");
				MusicPlayer.mMusicAdapter.notifyDataSetChanged();
				MusicPlayer.nMenuId=1;
				break;
			default :
				MusicPlayer.mMusicAdapter.initAdapter(context,null);
				MusicPlayer.mMusicAdapter.notifyDataSetChanged();
				MusicPlayer.nMenuId=MusicPlayer.nLanorpor;
				Log.e("BroadcastReceiver", "BroadcastReceiver refresh");
				break;
			}
		}else if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
//			if(mWifiInfo.isConnected()||mMobileInfo.isConnected()){
//				Log.i("current connection", mActiveInfo.getTypeName());
//			}else{
//				Toast.makeText(context, "暂无可用网络", 1000).show();
//			}
		}else if(action.equals("deleteConfirm")){
			return;
		}else{
			//TODO
		}
	}
}