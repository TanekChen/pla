package com.bean;

public class QueryParam {
	public QueryParam(){
		beginLogTime = null;
		endLogTime = null;
	    logIds = null;
	    declNo = null;
	    vslRegNo = null;
	    soNo = null;
	    mawb = null;
	    hawb = null;
	    page = -1;
    }
    //{START,FIELD}
    private java.sql.Date beginLogTime;
    private java.sql.Date endLogTime;
    private String[] logIds;
    private String declNo;
    private String vslRegNo;
    private String soNo;
    private String mawb;
    private String hawb;
    private int page;
    //{END,FIELD}

    //{START,GEN_METHOD}
	public java.sql.Date getBeginLogTime(){
		return beginLogTime;
	}
	public void setBeginLogTime(java.sql.Date beginLogTime){
		this.beginLogTime = beginLogTime;
	}

	public java.sql.Date getEndLogTime(){
		return endLogTime;
	}
	public void setEndLogTime(java.sql.Date endLogTime){
		this.endLogTime = endLogTime;
	}

	public String[] getLogIds(){
		return logIds;
	}
	public void setLogIds(String[] logIds){
		this.logIds = logIds;
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

	public int getPage(){
		return page;
	}
	public void setPage(int page){
		this.page = page;
	}
	//{END,GEN_METHOD}
}
