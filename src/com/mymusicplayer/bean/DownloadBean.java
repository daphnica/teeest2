package com.mymusicplayer.bean;

public class DownloadBean {

	private int status = -100;
	private long id;
	private boolean completeFlag = false;
	private int process = 0;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public boolean isCompleteFlag() {
		return completeFlag;
	}
	public void setCompleteFlag(boolean completeFlag) {
		this.completeFlag = completeFlag;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getProcess() {
		return process;
	}
	public void setProcess(int process) {
		this.process = process;
	}
	
}
