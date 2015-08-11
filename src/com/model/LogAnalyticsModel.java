package com.model;

import java.io.*;

import javax.sql.*;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.sun.rowset.*;
import com.bean.*;
public class LogAnalyticsModel {
//	public static enum ColumnName {LOG_TIME, LOG_ID, DECL_NO, VSL_REG_NO, SO_NO, MAWB, HAWB, PROC_DESC};
	private static final int PAGE_ROWS = 15;
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final String CONFIG_FILE = "D:/rtc/workspace/pla/DB.conf";
	private Properties conf ;
	public LogAnalyticsModel(){
		try {
			
			conf = new Properties();
			conf.load((InputStream) new FileInputStream(CONFIG_FILE));
			Class.forName("org.postgresql.Driver");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("File Exception : "+e.getStackTrace());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException Exception : "+e.getStackTrace());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@SuppressWarnings("resource")
	public QueryResult qryEXLGDMM(QueryParam queryParam) {
		System.out.println("host:"+conf.getProperty("POSTGRESQL"));
		System.out.println("url:"+conf.getProperty("MDW.URL"));
		System.out.println("usr:"+conf.getProperty("MDW.USER"));
		System.out.println("pass:"+conf.getProperty("MDW.PASS"));
	    
	    QueryResult queryResult = new QueryResult();
		try {
			RowSet jrs = new JdbcRowSetImpl();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT *                       \n");
			sb.append("  FROM EXLGDMM2                 \n");
			sb.append(" WHERE 1 = 1                   \n");
			
			if(queryParam.getBeginLogTime() != null){
				sb.append("AND LOG_TIME >= TO_TIMESTAMP('"+SDF.format(queryParam.getBeginLogTime())+"','YYYY-MM-DD HH24:MI') \n");
			}
			if(queryParam.getEndLogTime() != null){
				sb.append("   AND LOG_TIME <  TO_TIMESTAMP('"+SDF.format(queryParam.getEndLogTime())+"','YYYY-MM-DD HH24:MI') \n");
			}
			String logIdCon = "";
			String[] logIds = queryParam.getLogIds();
			if(logIds != null){
				for(int i=0; i<logIds.length; i++){
					logIdCon += "'"+ logIds[i] +"',";
				}
				logIdCon = logIdCon.substring(0, logIdCon.length()-1);
				if(!"".equals(logIdCon))
					sb.append("   AND LOG_ID in ("+ logIdCon +")         \n");
			}
			if(queryParam.getDeclNo() != null && !"".equals(queryParam.getDeclNo())){
				sb.append("   AND DECL_NO = '"+ queryParam.getDeclNo()  +"'\n");
			}
			if(queryParam.getVslRegNo() != null && !"".equals(queryParam.getVslRegNo())){
				sb.append("   AND VSL_REG_NO = '"+ queryParam.getVslRegNo()  +"'\n");
			}
			if(queryParam.getSoNo() != null && !"".equals(queryParam.getSoNo())){
				sb.append("   AND SO_NO = '"+ queryParam.getSoNo()  +"'\n");
			}
			if(queryParam.getMawb() != null && !"".equals(queryParam.getMawb())){
				sb.append("   AND MAWB = '"+ queryParam.getMawb()  +"'\n");
			}
			if(queryParam.getHawb() != null && !"".equals(queryParam.getHawb())){
				sb.append("   AND HAWB = '"+ queryParam.getHawb()  +"'\n");
			}
			sb.append("ORDER BY LOG_TIME, LOG_ID, DECL_NO");
			System.out.println(sb.toString() + " ORDER BY　LOG_TIME,LOG_ID,DECL_NO");
			
			jrs.setUrl(conf.getProperty("MDW.URL"));
			jrs.setUsername(conf.getProperty("MDW.USER"));
			jrs.setPassword(conf.getProperty("MDW.PASS"));
			
			jrs.setCommand(sb.toString());
			jrs.execute();
			
			ArrayList<ResultRow> resultRows = new ArrayList<ResultRow>(0);
			int pos = PAGE_ROWS * (queryParam.getPage()-1);//移動到此page第一筆的前一筆
			jrs.absolute(pos);
			
			int totalRowNum = pos;
			while(jrs.next()){
				totalRowNum ++;
				//{LOG_TIME, LOG_ID, DECL_NO, VSL_REG_NO, SO_NO, MAWB, HAWB, PROC_DESC};
				if(jrs.getRow() <= pos+PAGE_ROWS){
					ResultRow resultRow = new ResultRow();
					resultRow.setRowNum(jrs.getRow());
					resultRow.setLogTime(jrs.getTimestamp("LOG_TIME"));
					resultRow.setLogId(jrs.getString("LOG_ID"));
					resultRow.setDeclNo(jrs.getString("DECL_NO"));
					resultRow.setVslRegNo(jrs.getString("VSL_REG_NO"));
					resultRow.setSoNo(jrs.getString("SO_NO"));
					resultRow.setMawb(jrs.getString("MAWB"));
					resultRow.setHawb(jrs.getString("HAWB"));
					resultRow.setProcDesc(jrs.getString("PROC_DESC"));
					resultRows.add(resultRow);
					System.out.println(jrs.getRow()+" logTime:"+SDF.format(resultRow.getLogTime())+" Decl_NO:"+jrs.getString("DECL_NO"));// + " proc_desc:"+ jrs.getString("PROC_DESC")
				}
			}
			queryResult.setResultRows(resultRows);
			
			if(totalRowNum == pos)
				totalRowNum = 0;
			queryResult.setTotalRowNum(totalRowNum);
			queryResult.setTotalPageNum(totalRowNum%PAGE_ROWS==0 ? totalRowNum/PAGE_ROWS : totalRowNum/PAGE_ROWS+1);
			System.out.println("totolRowNum: "+ queryResult.getTotalRowNum());
			System.out.println("totolPageNum: "+ queryResult.getTotalPageNum());
			
			System.out.println("done");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return queryResult;
	}
	public String qryEXLGDMM_JSON(QueryParam queryParam) throws UnsupportedEncodingException{
		String nextRowMark = "nextROW";
		String nextLine = "nextLINE";
		QueryResult queryResult = qryEXLGDMM(queryParam);
		String jsonStr = "";
		jsonStr = "{";
		jsonStr += "\"success\":true,";
		jsonStr += "\"page\":\""+queryParam.getPage()+"\",";
		jsonStr += "\"total\":\""+queryResult.getTotalPageNum()+"\",";
		jsonStr += "\"records\":\""+queryResult.getTotalRowNum()+"\",";
		jsonStr += "\"procDesc\":\"";
		//jsonStr += "\"rows\":";
		
		ArrayList<ResultRow> resultRowsList = queryResult.getResultRows();
		ResultRow resultRow;
		String jsonAryStr = "[";
		String allProcDesc = "";
		String temp = "";
		for(int i=0; i<resultRowsList.size(); i++){
			resultRow = resultRowsList.get(i);
			jsonAryStr += "{";
			
			jsonAryStr += "\"id\":";
			jsonAryStr += "\""+resultRow.getRowNum()+"\",";
			
			jsonAryStr += "\"logTime\":";
			temp = SDF.format(resultRow.getLogTime());
			jsonAryStr += "\""+temp+"\",";
			
			jsonAryStr += "\"logId\":";
			jsonAryStr += "\""+resultRow.getLogId()+"\",";
			
			jsonAryStr += "\"declNo\":";
			jsonAryStr += "\""+resultRow.getDeclNo()+"\",";
			
			jsonAryStr += "\"vslRegNo\":";
			jsonAryStr += "\""+resultRow.getVslRegNo()+"\",";
			
			jsonAryStr += "\"soNo\":";
			jsonAryStr += "\""+resultRow.getSoNo()+"\",";
			
			jsonAryStr += "\"mawb\":";
			jsonAryStr += "\""+resultRow.getMawb()+"\",";
			
			jsonAryStr += "\"hawb\":";
			jsonAryStr += "\""+resultRow.getHawb()+"\"";
			
			jsonAryStr += "},";
//			allProcDesc += java.net.URLEncoder.encode(resultRow.getProcDesc(),"UTF-8") + nextRowMark;
			allProcDesc += resultRow.getProcDesc() + nextRowMark;
		}
		jsonAryStr = jsonAryStr.replaceAll("null", "");
		if(jsonAryStr.length() > 10){
			jsonAryStr = jsonAryStr.substring(0, jsonAryStr.length()-1);
		}
		
		jsonAryStr += "]";
		if(allProcDesc.length() > 0)
			allProcDesc = allProcDesc.substring(0, allProcDesc.length() - nextRowMark.length());
		allProcDesc = allProcDesc.replaceAll("\n", nextLine);
		allProcDesc = allProcDesc.replaceAll("\"", "'");
		jsonStr += allProcDesc+"\",";
		jsonStr += "\"rows\":";
		jsonStr += jsonAryStr;
		jsonStr += "}";
		return jsonStr;
	}
	
	public static void main(String[] args) throws Exception{
		LogAnalyticsModel model = new LogAnalyticsModel();
		QueryParam queryParam = new QueryParam();
		String[] logIds = {"ED90"};
//		queryParam.setLogIds(logIds);
		//queryParam.setDeclNo("CR  0336609735");
		queryParam.setPage(2);
		
		Calendar begCal = Calendar.getInstance();
		begCal.setTime(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2014/05/21 03:03:03"));
        java.sql.Date beginDate = new java.sql.Date(begCal.getTimeInMillis());
//        queryParam.setBeginLogTime(beginDate);
        
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2014/06/04 03:03:03"));
        java.sql.Date endDate = new java.sql.Date(endCal.getTimeInMillis());
//        queryParam.setEndLogTime(endDate);
        
		//QueryResult queryResult = model.qryEXLGDMM(queryParam);
		System.out.println(model.qryEXLGDMM_JSON(queryParam));
	}
	
	
}
