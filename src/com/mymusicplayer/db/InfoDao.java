package com.mymusicplayer.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mymusicplayer.bean.ResumeInfo;

public class InfoDao {

	private static DBOpenHelper helper;  
    public InfoDao(Context context) {  
        if(helper == null)  
        helper = new DBOpenHelper(context);  
    }  
      
    public void insert(ResumeInfo info){  
        SQLiteDatabase db = helper.getWritableDatabase();  
        db.execSQL("INSERT INTO filedownlog(path,threadid,done) VALUES(?,?,?)",new Object[]{info.getPath(),info.getThid(),info.getFinishedBytes()});  
          
    }  
    
    public void delete(String path,int thid){  
        SQLiteDatabase db = helper.getWritableDatabase();  
        db.execSQL("DELETE FROM filedownlog WHERE path=? AND threadid=?", new Object[]{path,thid});  
          
    }  
    
    public void update(ResumeInfo info) {    
        SQLiteDatabase db = helper.getWritableDatabase();    
        db.execSQL("UPDATE filedownlog SET done=? WHERE path=? AND threadid=?", new Object[] { info.getFinishedBytes(), info.getPath(), info.getThid() });    
          
    }   
    
    public ResumeInfo query(String path, int thid) {    
        SQLiteDatabase db = helper.getWritableDatabase();    
        Cursor c = db.rawQuery("SELECT path, threadid, done FROM filedownlog WHERE path=? AND threadid=?", new String[] { path, String.valueOf(thid) });    
        ResumeInfo info = null;    
        if (c.moveToNext())    
            info = new ResumeInfo(c.getString(0), c.getInt(1), c.getLong(2));    
        c.close();    
         
        return info;    
    }    
    
    public void deleteAll(String path, Long fileLen) {    
        SQLiteDatabase db = helper.getWritableDatabase();    
        db.execSQL("DELETE FROM filedownlog WHERE path=? ", new Object[] { path });    
    }  
    
    public List<String> queryUndone() {    
        SQLiteDatabase db = helper.getWritableDatabase();    
        Cursor c = db.rawQuery("SELECT DISTINCT path FROM filedownlog", null);    
        List<String> pathList = new ArrayList<String>();    
        while (c.moveToNext())    
            pathList.add(c.getString(0));    
        c.close();    
         
        return pathList;    
    }    
    
	public void deleteUndone(String path) {    
        SQLiteDatabase db = helper.getWritableDatabase();    
        db.execSQL("DELETE FROM filedownlog WHERE path=? ", new Object[] { path });    
    }
    
}
