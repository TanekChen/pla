package com.parse.base;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.Calendar;
import java.util.Properties;
import java.util.regex.Pattern;

public class converBase {
	protected static final String batchEXPath = "gov.customs.aci.ex.batch";
	protected static final String batchCSPath = "gov.customs.aci.cs.batch";
	protected static final String batchRMPath = "gov.customs.aci.rm.batch";
	protected static Class prog;
	public static void batch(String progID,String methodName,String arg0,String[] Others) throws Exception{
		String path;
//		if (sysID.equals("EX")){
//			path = batchEXPath+"."+progID;
//		}
//		else if (sysID.equals("CS")){
//			path = batchCSPath+"."+progID;
//		}
//		else if (sysID.equals("RM")){
//			path = batchRMPath+"."+progID;
//		}
//		else{
//			path="";
//		}
		
//		path = "com.parse.batch."+progID;
		path = progID;
		try {
			System.out.println(path);
			boolean batB = true;
			prog=Class.forName(path);
			java.lang.reflect.Method mths[] = prog.getMethods();
			for (int i = 0; i < mths.length; i++) {
				java.lang.reflect.Method mth = mths[i];
				if (mth.getName().equals(methodName)) {
//					有batch method
					batB=false;
					Constructor ctr = prog.getConstructor();
					Object obj = ctr.newInstance();
//					mth.invoke(obj, beginDate , endDate ,Others);
					mth.invoke(obj);
				}
			}
			if (batB){
				System.out.println("被呼叫的程式，無batch method");
			}
//			取得參數型態數量
//			mth.getArgumentTypes();
//			取得method 參數型態
//			prog.getMethod("", parameterTypes)
		} catch (ClassNotFoundException e1) {
			System.out.println("無此程式");
			e1.printStackTrace();
		}
		
	}
	private static boolean checkDate(String date){
		if (!Pattern.matches("\\d{8}",date)){
			return false;
		}
		else{
			try{
			int year = Integer.parseInt(date.substring(0, 4));
			int month = Integer.parseInt(date.substring(4, 6));
			int day = Integer.parseInt(date.substring(6, 8));
			boolean leapYear=false;

			if ((year % 4 == 0) && ((year % 400 ==0) || (year % 100 !=0 )) ){
				leapYear = true;
			}
			else{
				leapYear = false;
			}
			if (year < 1900){
				return false;
			}
			if (month > 12 || month < 1){
				return false;
			}
			if (month == 1){
				if (day > 31 || day < 1){
					return false;
				}
				else{
					return true;
				}
			}
			else if (month == 2){
				if (leapYear){
					if (day > 29 || day < 1){
						return false;
					}
					else{
						return true;
					}
				}
				else{
					if (day > 28 || day < 1){
						return false;
					}
					else{
						return true;
					}
				}
			}
			else if (month == 3){
				if (day > 31 || day < 1){
					return false;
				}
				else{
					return true;
				}
			}
			else if (month == 4){
				if (day > 30 || day < 1){
					return false;
				}
				else{
					return true;
				}
			}
			else if (month == 5){
				if (day > 31 || day < 1){
					return false;
				}
				else{
					return true;
				}
			}
			else if (month == 6){
				if (day > 30 || day < 1){
					return false;
				}
				else{
					return true;
				}
			}
			else if (month == 7){
				if (day > 31 || day < 1){
					return false;
				}
				else{
					return true;
				}
			}
			else if (month == 8){
				if (day > 31 || day < 1){
					return false;
				}
				else{
					return true;
				}
			}
			else if (month == 9){
				if (day > 30 || day < 1){
					return false;
				}
				else{
					return true;
				}
			}
			else if (month == 10){
				if (day > 31 || day < 1){
					return false;
				}
				else{
					return true;
				}
			}
			else if (month == 11){
				if (day > 30 || day < 1){
					return false;
				}
				else{
					return true;
				}
			}
			else if (month == 12){
				if (day > 31 || day < 1){
					return false;
				}
				else{
					return true;
				}
			}
			}catch(Exception e) {
				System.out.println("Exception: " + e);
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}
	private static String toStringDate(String date){
		int year = Integer.parseInt(date.substring(0, 4)) - 1911;
		return year+"/"+date.substring(4, 6)+"/"+date.substring(6, 8);
	}
	public static void main(String[] args) {
		
		Properties p = new Properties(System.getProperties());

//		System.out.println(p.getProperty("os.name"));
//		System.out.println(p.getProperty("os.arch"));
//		System.out.println(p.getProperty("user.dir"));
		System.out.println("參數長度: "+args.length);
		try {
			BufferedReader br
			   = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("請輸入程式名稱 參數:");
			String str = br.readLine();
			String[] strToken=str.split(" ");
//			RocDate beginDate= new RocDate();
//			RocDate endDate= new RocDate();
			String [] others = null;
			if(strToken.length == 0 ){
				System.out.println("參數錯誤");
			}
			else if(strToken.length == 1){
				others =new String[0];
				converBase.batch(strToken[0],"", "", others);
			}
			else if (strToken.length == 2)
			{
				others = new String[0];
				converBase.batch(strToken[0],strToken[1], "", others);
			}
			else if (strToken.length == 3){
				others =new String[0];
				converBase.batch(strToken[0],strToken[1], strToken[2], others);
			}
			else{
				others = new String[strToken.length-3];
				int j = 0;
				for (int i=4;i<strToken.length;i++){
					others[j]=strToken[i];
					j ++;
				}
				converBase.batch(strToken[0],strToken[1], strToken[2], others);
			}
			
		} catch (Exception e) {
			System.out.println("Exception: " + e);
			e.printStackTrace();
		}
		
	}
}
