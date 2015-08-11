package com.parse.batch;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ParseER10 {
	private final static String ER11start = "===== ER11 START =====";
	private final static String declNo = "EDM_rec.DECL_NO=<<";
	private final static String doNx801 = "DO_NX801=<<";
	private final static String firistRun = "FIRIST_RUN=<<";
	private final static String item = "ITEM=";
	private final static String regList = "REG_LIST=<";
	private final static String permitNoList = "PERMIT_NO_LIST=<";
	private final static String ingoreAllPermit = "INGORE_ALL_PERMIT=<";
	private final static String ER11end = "===== ER11 END =====";
	public static void batch(String beginDate,String endDate,String [] others){
		try {
			BufferedReader in
			   = new BufferedReader(
					   new FileReader("D:/PG/1待辦事項/pla/log-20140520-ER10.txt"));
			String line = "";
			while ((line = in.readLine())!=null){
				if (line.indexOf(ER11start) > 0){
					System.out.println("declNo"+line.substring(line.indexOf(declNo)+1, line.indexOf(">>")));
					System.out.println("doNx801"+line.substring(line.indexOf(doNx801)+1, line.indexOf(">>")));
					System.out.println("firistRun"+line.substring(line.indexOf(firistRun)+1, line.indexOf(">>")));
				}
				if (line.indexOf(item) > 0){
					System.out.println("item"+line.substring(line.indexOf(item)+1, line.indexOf(item)+2));
					System.out.println("regList"+line.substring(line.indexOf(regList)+1, line.indexOf(">")));
					System.out.println("permitNoList"+line.substring(line.indexOf(permitNoList)+1, line.indexOf(">>")));
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
