package com.mymusicplayer.bean;

public class ResumeInfo {

	private String path;  
	private int thid;
	private Long finishedBytes;  
	  
	public ResumeInfo(String path, int thid, Long finishedBytes) {  
		this.path = path;  
		this.thid = thid;  
		this.finishedBytes = finishedBytes;  
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getThid() {
		return thid;
	}

	public void setThid(int thid) {
		this.thid = thid;
	}

	public Long getFinishedBytes() {
		return finishedBytes;
	}

	public void setFinishedBytes(Long finishedBytes) {
		this.finishedBytes = finishedBytes;
	}

	  
	  
}
