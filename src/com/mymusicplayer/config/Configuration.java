package com.mymusicplayer.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Configuration {
	private static final String SETTING_INFOS = "Configuration";

	public String lastEnter;
	
	
	public static Configuration instance;

	private Configuration() {
	}

	public static Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration();
		}
		return instance;
	}

	public void saveMe(Context context) {

		SharedPreferences preferences = context.getSharedPreferences(
				SETTING_INFOS, context.MODE_PRIVATE);
		Editor preferencesEditor = preferences.edit();

		//这里写需要记忆的东西
		/*****************/
		preferencesEditor.putString("lastEnter", lastEnter);
		
		
		
		/*****************/
		
		
		preferencesEditor.commit();
	}

	public void readMe(Context context) {

		SharedPreferences preferences = context.getSharedPreferences(
				SETTING_INFOS, context.MODE_PRIVATE);
		
		lastEnter = preferences.getString("lastEnter", "");
	}
}
