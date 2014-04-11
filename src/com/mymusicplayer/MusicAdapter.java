package com.mymusicplayer;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.mymusicplayer.bean.MusicBean;
import com.mymusicplayer.callbacks.CallbackDeleteEntity;
import com.mymusicplayer.util.MusicUtil;

public class MusicAdapter extends BaseAdapter {

	private static MusicAdapter instance;
	
	private LayoutInflater mInflater;
	private List<MusicBean> mAllMusic;
	private String mIsDelete;
	
	private IntentFilter mFilter;
	private Intent mIntent;
	private CallbackDeleteEntity entity;
	
	public MusicAdapter() {
		/* another git test1*/
	}
	
	public static MusicAdapter getInstance(){
		if(instance==null){
			instance = new MusicAdapter();
		}
		return instance;
	}
	
	public void initAdapter(Context context,String mIsDelete) {
		this.mInflater=LayoutInflater.from(context);
		mAllMusic=new MusicUtil().getAllMusic(context);
		Log.e("size", String.valueOf(mAllMusic.size()));
		this.mIsDelete = mIsDelete;
		mFilter = new IntentFilter();
		entity = CallbackDeleteEntity.getInstance();
	}
	
	@Override
	public int getCount() {
		return mAllMusic.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View view, ViewGroup viewgroup) {

		if(view==null){
			view=mInflater.inflate(R.layout.music_adapter, null);
		}
		final Getter getter;
		if(view!=null){
			getter = new Getter();
			getter.music_name=(TextView) view.findViewById(R.id.music_name);
			getter.singer=(TextView) view.findViewById(R.id.singer);
			getter.duration = (TextView) view.findViewById(R.id.duration);
			getter.mIsDelete = (CheckBox) view.findViewById(R.id.is_delete);
			view.setTag(getter);
		}else {
			getter=(Getter) view.getTag();	
		}
		
		if(mIsDelete!=null){
			getter.music_name.setText(mAllMusic.get(position).getMusic_name());
			getter.singer.setText(mAllMusic.get(position).getSinger());
			getter.duration.setVisibility(View.GONE);
			getter.mIsDelete.setVisibility(View.VISIBLE);
			
			getter.mIsDelete.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					
					String item = "";
					if(isChecked){
						item = "add,"+mAllMusic.get(position).getPath();
					}else{
						item = "del,"+mAllMusic.get(position).getPath();
					}
					entity.invokeMethod(item);
				}
			});
			
		}else{
			getter.music_name.setText(mAllMusic.get(position).getMusic_name());
			getter.singer.setText(mAllMusic.get(position).getSinger());
			getter.duration.setText(mAllMusic.get(position).getDuration());
			getter.duration.setVisibility(View.VISIBLE);
			getter.mIsDelete.setVisibility(View.GONE);
		}

		return view;
	}
	
	public class Getter {
		private TextView music_name;
		private TextView singer;
		private TextView duration;
		private CheckBox mIsDelete;
	}
	
}
