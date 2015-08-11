package com.bean;

public class ResultRow{
	public ResultRow(){
		rowNum = -1;
		logTime = null;
	    logId = null;
	    declNo = null;
	    vslRegNo = null;
	    soNo = null;
	    mawb = null;
	    hawb = null;
	    procDesc = null;
	}
	//{START,FIELD}
	private int rowNum;
	private java.sql.Timestamp logTime;
	private String logId;
	private String declNo;
	private String vslRegNo;
	private String soNo;
	private String mawb;
	private String hawb;
	private String procDesc;
	//{END,FIELD}
	
	//{START,GEN_METHOD}
	public int getRowNum(){
		return rowNum;
	}
	public void setRowNum(int rowNum){
		this.rowNum = rowNum;
	}

	public java.sql.Timestamp getLogTime(){
		return logTime;
	}
	public void setLogTime(java.sql.Timestamp logTime){
		this.logTime = logTime;
	}

	public String getLogId(){
		return logId;
	}
	public void setLogId(String logId){
		this.logId = logId;
	}

	public String getDeclNo(){
		return declNo;
	}
	public void setDeclNo(String declNo){
		this.declNo = declNo;
	}

	public String getVslRegNo(){
		return vslRegNo;
	}
	public void setVslRegNo(String vslRegNo){
		this.vslRegNo = vslRegNo;
	}

	public String getSoNo(){
		return soNo;
	}
	public void setSoNo(String soNo){
		this.soNo = soNo;
	}

	public String getMawb(){
		return mawb;
	}
	public void setMawb(String mawb){
		this.mawb = mawb;
	}

	public String getHawb(){
		return hawb;
	}
	public void setHawb(String hawb){
		this.hawb = hawb;
	}

	public String getProcDesc(){
		return procDesc;
	}
	public void setProcDesc(String procDesc){
		this.procDesc = procDesc;
	}
	//{END,GEN_METHOD}
}
