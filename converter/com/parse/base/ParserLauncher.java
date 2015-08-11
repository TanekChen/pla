package com.parse.base;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.parse.batch.LogParser;

/*傳入參數
 * 20140616 20140616 ED90.log
 * 20140616 20140616
 * */

public class ParserLauncher {
	public static final String LOG_IN_DIRECTORY_PATH_1 = "D:/workPla/pla/pdb/";
	public static final String LOG_OUT_DIRECTORY_PATH_1 = "D:/workPla/pla/csv_out/";
	public static final String LOG_IN_DIRECTORY_PATH_2 = "D:/workPla/pla/pdb/";
	public static final String LOG_OUT_DIRECTORY_PATH_2 = "D:/workPla/pla/csv_out/";
	
	public static String LOG_IN_PATH_1 = "";
	public static String LOG_OUT_PATH_1 = "";
	public static String LOG_IN_PATH_2 = "";
	public static String LOG_OUT_PATH_2 = "";
	
	public static String DATE_BEG = null; //起始日期資料夾
	public static String DATE_END = null; //終止日期資料夾
	public static String LOG_ID = null;
	public static HashMap<String,String> logLib = new HashMap<String,String>();
	public static HashMap<String,ArrayList> timeFormatLib = new HashMap<String,ArrayList>();
	ArrayList<String> timeConfig = new ArrayList<String>();
	
	static final String logFormat = "log\\-([0-9]{8})\\-(([0-9|A-Z]{1,}))\\."; //log-YYYYMMDD-LOG_ID.log/txt
	
	public static void main(String[] args){
		
		//指定要處理之LOG_ID及breakPoint
		//若breakPoint沒有特別指定，則以「?」
		logLib.put("EA90","=====");
//		logLib.put("EA95", "======");
//		logLib.put("EP01", "?");
//		logLib.put("EE01", "------");
//		logLib.put("EE08", "[*******");
//		logLib.put("EH50", "EH50-- START");
		
		//指定LOG：抓取處理時間的方式
		ArrayList<String> timeConfig = new ArrayList<String>();
		timeConfig.add("(.*RunTime:((\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2})).*)");  //RunTime:2014/06/16 00:01:58
		timeConfig.add("3");	//group位置
		timeConfig.add("yyyy/MM/dd HH:mm:ss"); //date Format
		timeFormatLib.put("EA90",timeConfig );
		
		
		 //取得傳入的參數
		 for(int i = 0; i < args.length; i++){
			 if(i == 0){
				 DATE_BEG = args[i];
			 }else if(i == 1){
				 DATE_END = args[i];
			 }else if (i == 2){
				 LOG_ID = args[i];
			 }else
			 {
				 System.out.println("wrong number of Parameters");
			 }
		 }
		 
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd"); 
		Calendar calBeg = Calendar.getInstance();
		Calendar calEnd = Calendar.getInstance();
		try{
			calBeg.setTime(df.parse(DATE_BEG));
			calEnd.setTime(df.parse(DATE_END));
		}catch(Exception e){
			System.out.println("Date parser Exception:"+e);
			return;
		}

		while( calBeg.getTime().getTime() <=  calEnd.getTime().getTime()){
			 //設定PATH
			 LOG_IN_PATH_1 = LOG_IN_DIRECTORY_PATH_1 + new SimpleDateFormat("yyyyMMdd").format(calBeg.getTime()).substring(0,6)+"/" + new SimpleDateFormat("yyyyMMdd").format(calBeg.getTime()) + "_pdb1/";
			 LOG_IN_PATH_2 = LOG_IN_DIRECTORY_PATH_2 + new SimpleDateFormat("yyyyMMdd").format(calBeg.getTime()).substring(0,6)+"/" + new SimpleDateFormat("yyyyMMdd").format(calBeg.getTime()) + "_pdb2/";
			 LOG_OUT_PATH_1 = LOG_OUT_DIRECTORY_PATH_1 + new SimpleDateFormat("yyyyMMdd").format(calBeg.getTime()).substring(0,6)+"/" + new SimpleDateFormat("yyyyMMdd").format(calBeg.getTime()) + "_pdb1/";
			 LOG_OUT_PATH_2 = LOG_OUT_DIRECTORY_PATH_2 + new SimpleDateFormat("yyyyMMdd").format(calBeg.getTime()).substring(0,6)+"/" + new SimpleDateFormat("yyyyMMdd").format(calBeg.getTime()) + "_pdb2/";
			 
			File tf = new File(LOG_OUT_PATH_1.substring(0,LOG_OUT_PATH_1.length()-1));
			//System.out.println("path:"+ LOG_OUT_PATH_2.substring(0,LOG_OUT_PATH_2.length()-1));
			if(!(tf.exists() && tf.isDirectory())){
				tf.mkdirs();
				//System.out.println("create new DIRECTORYs:"+ LOG_OUT_PATH_2.substring(0,LOG_OUT_PATH_2.length()-1));
			}
			 
			 File source = new File(LOG_IN_PATH_1);		 
			 File[] file = source.listFiles();
			 Pattern ptnLog = null;
			 ptnLog = Pattern.compile(logFormat);
			 if(LOG_ID != null){
				 ptnLog = Pattern.compile(LOG_ID);
			 }
			 Matcher mchLog =  null ;
			 
			for (int i = 0; i < file.length; i++) {
					//System.out.println(file[i].getParent() + file[i].getName());
				mchLog = ptnLog.matcher(file[i].getName());
				if(mchLog.find()){
					if(LOG_ID != null){
						System.out.println("db1 = "+file[i].getName());
						//new LogParser(file[i].getName(),LOG_IN_PATH_1,LOG_OUT_DIRECTORY_PATH_1);
						new Thread(new LogParser(file[i].getName(),LOG_IN_PATH_1,LOG_OUT_PATH_1)).start();
					}else if (logLib.containsKey(mchLog.group(2))){
						System.out.println("db1 = "+file[i].getName());
						//new LogParser(file[i].getName(),LOG_IN_PATH_1,LOG_OUT_PATH_1);
						new Thread(new LogParser(file[i].getName(),LOG_IN_PATH_1,LOG_OUT_PATH_1)).start();
					}
				}
			}
			
			 source = new File(LOG_IN_PATH_2);		 
			 file = source.listFiles();
			 ptnLog = Pattern.compile(logFormat);
			 tf = new File(LOG_OUT_PATH_2.substring(0,LOG_OUT_PATH_2.length()-1));
				//System.out.println("path:"+ LOG_OUT_PATH_2.substring(0,LOG_OUT_PATH_2.length()-1));
			if(!(tf.exists() && tf.isDirectory())){
				tf.mkdirs();
				//System.out.println("create new DIRECTORYs:"+ LOG_OUT_PATH_2.substring(0,LOG_OUT_PATH_2.length()-1));
			}
			 if(LOG_ID != null){
				 ptnLog = Pattern.compile(LOG_ID);
			 }
			 
			for (int i = 0; i < file.length; i++) {
					//System.out.println(file[i].getParent() + file[i].getName());
				mchLog = ptnLog.matcher(file[i].getName());
				if(mchLog.find()){
					if(LOG_ID != null){
						System.out.println("db2 = "+file[i].getName());
						//new LogParser(file[i].getName(),LOG_IN_PATH_2,LOG_OUT_PATH_2);
						new Thread(new LogParser(file[i].getName(),LOG_IN_PATH_2,LOG_OUT_PATH_2)).start();
					}else if (logLib.containsKey(mchLog.group(2))){
						System.out.println("db2 = "+file[i].getName());
						//new LogParser(file[i].getName(),LOG_IN_PATH_2,LOG_OUT_PATH_2);
						new Thread(new LogParser(file[i].getName(),LOG_IN_PATH_2,LOG_OUT_PATH_2)).start();
					}
				}
			}
			 
			//System.out.println(new SimpleDateFormat("yyyyMMdd").format(calBeg.getTime()));
			calBeg.add(Calendar.DATE, 1);
		 }
		
		

        
 }     
	
	
}
