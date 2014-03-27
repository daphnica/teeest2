package com.mymusicplayer.callbacks;

public class CallbackDeleteEntity {

	private static CallbackDeleteEntity instance;
	private OnMethodCallback methodCallback;
	
	public CallbackDeleteEntity() {
	}
	
	public static CallbackDeleteEntity getInstance(){
		if(instance==null){
			instance = new CallbackDeleteEntity();
		}
		return instance;
	}
	
	public void invokeMethod(String item){
		if(methodCallback!=null){
			methodCallback.doCallBackDeleteItem(item);
		}
	}
	
	public void setMethodCallback(OnMethodCallback methodCallback) {
		this.methodCallback = methodCallback;
	}

	public interface OnMethodCallback {
        public void doCallBackDeleteItem(String item);
    }
}
