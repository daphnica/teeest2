package com.mymusicplayer.callbacks;

import java.util.List;
import java.util.Map;


public class CallbackProcessEntity {

	private static CallbackProcessEntity instance;
	private OnMethodCallback methodCallback;
	
	public CallbackProcessEntity() {
	}
	
	public static CallbackProcessEntity getInstance(){
		if(instance==null){
			instance = new CallbackProcessEntity();
		}
		return instance;
	}
	
	public void invokeMethod(int item,List<Map<Long,Integer>> list,long id){
		if(methodCallback!=null){
			methodCallback.doCallBackProcessItem(item,list,id);
		}
	}
	
	public void setMethodCallback(OnMethodCallback methodCallback) {
		this.methodCallback = methodCallback;
	}

	public interface OnMethodCallback {
        public void doCallBackProcessItem(int item,List<Map<Long,Integer>> list,long id);
    }
}
