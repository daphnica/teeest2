package com.mymusicplayer.asynctask;

import android.R.integer;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadAsyncTask extends AsyncTask<String, integer, String>{

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		Log.e("onPreExecute", Thread.currentThread().getName());
		super.onPreExecute();
	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		Log.e("doInBackground", Thread.currentThread().getName());
		publishProgress();
		return null;
	}
	
	@Override
	protected void onProgressUpdate(integer... values) {
		// TODO Auto-generated method stub
		Log.e("onProgressUpdate", Thread.currentThread().getName());
		super.onProgressUpdate(values);
	}
	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		Log.e("onPostExecute", Thread.currentThread().getName());
		super.onPostExecute(result);
	}

}
