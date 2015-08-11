package com.bean;
import java.util.*;

public class QueryResult {
	//{START,FIELD}
	int totalRowNum;
	int totalPageNum;
	ArrayList<ResultRow> resultRows;
	//{END,FIELD}
		
	public QueryResult(){
		totalRowNum = -1;
		totalPageNum = -1;
		resultRows = new ArrayList<ResultRow>(0);
	}
	//{START,GEN_METHOD}
	public int getTotalRowNum(){
		return totalRowNum;
	}
	public void setTotalRowNum(int totalRowNum){
		this.totalRowNum = totalRowNum;
	}

	public int getTotalPageNum(){
		return totalPageNum;
	}
	public void setTotalPageNum(int totalPageNum){
		this.totalPageNum = totalPageNum;
	}

	public ArrayList<ResultRow> getResultRows(){
		return resultRows;
	}
	public void setResultRows(ArrayList<ResultRow> resultRows){
		this.resultRows = resultRows;
	}
	//{END,GEN_METHOD}
}
