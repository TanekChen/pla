package com.parse.batch;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
/**
 * 作業代碼：ParseRegularLog<br>
 * 描述：可轉換有簡單規則的log<br>
 * 公司：ACOL.<br>
 *
 *【資料來源】：<br>
 *【輸出報表】：<br>
 *【異動紀錄】： <2014/06/21> 加入可設定空欄位功能<br> 
 *           <2014/06/22> 加入日期格式轉換功能<br>
 *           <2014/06/25> 加入 比對不到group的值仍會將key的欄位保留
 *
 * @author Jerry
 * @since 2014/06/22
 * @version 1.0.0 
 */
public class ParseRegularLog {

	//D:\rtc\workspace_pla\PLA\pdb\201404\20140429_pdb1\log-20140604-JOB_N5203A.txt
	private static final String LOG_PARTITION_DEFINE_PATH = "logPartitionDefine.txt";
	private static final String LOG_DIRECTORY = "pdb";
	private static final String CSV_DIRECTORY = "csv_out";
	private static String[] serverList = {"pdb1", "pdb2"};
	
	private static final SimpleDateFormat SDF_OUT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static String[] dateInFormat = {"yyyy/MM/dd HH:mm:ss", "MMdd.HHmmss.SSS"};
	
	private static final String TAG_BEGIN = "#tagBegin\\(logId\\):\"(.+)\"";
	private static final String HEAD_FORMAT = "#tag\\(head\\):\"(.+)\"";
	private static final String PATTERN_FORMAT = "#tag\\(pattern\\):\"(.+?)\" #tag\\(pLocation\\):\"(\\d+)\"(( #tag\\(pKey\\):\"(\\w+)\" #tag\\(pGroup\\):\"\\$(\\d+)\"( #tag\\(pDateCase\\):\"(\\d+)\")*)+)";
	private static final String PATTERN_HMSTR_FORMAT = " #tag\\(pKey\\):\"(\\w+)\" #tag\\(pGroup\\):\"\\$(\\d+)\"( #tag\\(pDateCase\\):\"(\\d+)\")*";
	private static final String PATTERN_PDATECASE_FORMAT = " #tag\\(pKey\\):\"(\\w+)\" #tag\\(pGroup\\):\"\\$(\\d+)\" #tag\\(pDateCase\\):\"(\\d+)\"";
	private static final String SPECIFIC_FORMAT = "#tag\\(specific\\):(( #tag\\(sKey\\):\"(\\w+)\" #tag\\(sValue\\):\"([\\w #]+)\")+)";
	private static final String SPECIFIC_HMSTR_FORMAT = " #tag\\(sKey\\):\"(\\w+)\" #tag\\(sValue\\):\"([\\w #]+)\"";

	//the head, pattern in logPartitionDefine
	private static String head;
	private static ArrayList<Body> patternList;

	static class Body{
		String pattern;
		int location;
		HashMap<String, String> hashMap;
		int dateCase;
		boolean active;

		Body(){
			pattern = "";
			location = 0;
			hashMap = new HashMap<String, String>();
			dateCase = 0;
			active = true;
		}
		
		void resetActive(){
			active = true;
		}
	}

	private static String getRow(HashMap<String, String> outHmCSV, String logSegment){
		ArrayList<String> keyArray = new ArrayList<String>(Arrays.asList(outHmCSV.keySet().toArray(new String[0])));
		Collections.sort(keyArray);
		int i = 0;
		String row = "";
		while(i < keyArray.size()){
			//System.out.println(outHmCSV.get(keyArray.get(i)));
			String value = outHmCSV.get(keyArray.get(i));
			if("#empty".equals(value)){
				row += "`";
			}else{
				value = value.replaceAll("\"", "\"\"");
				row += "\""+ value +"\"`";
			}
			i++;
		}
		if(logSegment.length() > 1000000)
			logSegment = logSegment.substring(0, 1000000) +"... ...";
		logSegment = logSegment.replaceAll("\"", "\"\"");
		row += "\""+ logSegment +"\"";
		return row;
	}
	private static void parseLog(String logFileName) throws IOException, ParseException{
		String csvFileName = logFileName.replaceFirst(LOG_DIRECTORY, CSV_DIRECTORY);
		String csvFilePath = csvFileName.substring(0, csvFileName.lastIndexOf('/'));
		//System.out.println("csvFilePath:"+csvFilePath);
		File file = new File(csvFilePath);
		if(!(file.exists() && file.isDirectory())){
			file.mkdirs();
			//System.out.println("create new DIRECTORYs:"+ csvFilePath);
		}
		
		BufferedReader logBfr = new BufferedReader(new FileReader(new File(logFileName)));
		BufferedWriter csvBfw = new BufferedWriter(new FileWriter(new File(csvFileName +".out")));
		HashMap<String, String> outHmCSV = new HashMap<String, String>();
		Body bodyTemp;
		String line = "";
		String logSegment = "";
		Calendar calendar = Calendar.getInstance();
		boolean parsing = false;
		boolean firstHead = true;
		int lineNum = 0;
		while((line = logBfr.readLine()) != null){
			//line = line.trim();
			if(line.matches(head)){
				parsing = true;
				lineNum = 1;//define "#tag(head)" is line number one. 
				if(!firstHead){
					//write out
					for(int i=0; i<patternList.size(); i++){
						bodyTemp = patternList.get(i);
						if(bodyTemp.active == true){
							String key = bodyTemp.hashMap.keySet().toArray(new String[0])[0];
							outHmCSV.put(key, "");
						}
						bodyTemp.resetActive();
					}
					csvBfw.write(getRow(outHmCSV, logSegment));
					outHmCSV.clear();
					for(int i=0; i<patternList.size(); i++){
						bodyTemp = patternList.get(i);
						bodyTemp.resetActive();
					}
					logSegment = "";
					csvBfw.newLine();
				}
				firstHead = false;
			}
			if(parsing){
				logSegment += line +"\n";

				int i = 0;
				//Body bodyTemp;
				while(i < patternList.size()){
					bodyTemp = patternList.get(i);
					if(bodyTemp.active == true && (bodyTemp.location == 0 || lineNum == bodyTemp.location) && line.matches(bodyTemp.pattern)){
						String key = bodyTemp.hashMap.keySet().toArray(new String[0])[0];
						String value = line.replaceFirst(bodyTemp.pattern, "$"+ bodyTemp.hashMap.get(key));
						if(bodyTemp.dateCase != 0){
							//value = convertDate(bodyTemp.dateCase-1, value);
							if(bodyTemp.dateCase == 1){
								calendar.setTime(new SimpleDateFormat(dateInFormat[bodyTemp.dateCase-1]).parse(value));
							}else if(bodyTemp.dateCase == 2){
								calendar.setTime(new SimpleDateFormat("yyyy"+ dateInFormat[bodyTemp.dateCase-1]).parse(csvFileName.replaceFirst(".*-(\\d{4})\\d{4}-.*", "$1") + value));
							}
							value = SDF_OUT.format(calendar.getTime());
						}
						outHmCSV.put(key, value);
						bodyTemp.active = false;
						//System.out.println("value:"+line.replaceFirst(bodyTemp.pattern, "$"+ bodyTemp.hashMap.get(key)));
					}else if(bodyTemp.active == true && bodyTemp.location == -1){//#tag(specific)
						String key = bodyTemp.hashMap.keySet().toArray(new String[0])[0];
						outHmCSV.put(key, bodyTemp.hashMap.get(key));
						bodyTemp.active = false;
					}
					i ++;
				}
			}
			lineNum ++;
		}//while
		logBfr.close();
		//write out
		for(int i=0; i<patternList.size(); i++){
			bodyTemp = patternList.get(i);
			if(bodyTemp.active == true){
				String key = bodyTemp.hashMap.keySet().toArray(new String[0])[0];
				outHmCSV.put(key, "");
			}
			bodyTemp.resetActive();
		}
		csvBfw.write(getRow(outHmCSV, logSegment));
		outHmCSV.clear();
		logSegment = "";
		csvBfw.newLine();
		
		csvBfw.flush();
		csvBfw.close();
	}//parseLog(String logFileName)
	private static boolean getLpdStructure(String logId) throws IOException{
		boolean isExist = false;
		File file = new File(LOG_PARTITION_DEFINE_PATH);
		BufferedReader lpd = new BufferedReader(new FileReader(file));
		String line = "";
		boolean parsing = false;
		patternList = new ArrayList<Body>(0);
		while((line = lpd.readLine()) != null){
			line = line.trim();
			if(line.startsWith("#tag")){
				if(line.equals("#tagBegin(logId):\""+ logId +"\"")){
					parsing = true;
					//System.out.println("parsing = true;");
				}
				//set pld to structure
				if(parsing && line.matches(HEAD_FORMAT)){
					head = line.replaceFirst(HEAD_FORMAT, "$1");
					//System.out.println("head:"+ head);
				}
				if(parsing && line.matches(PATTERN_FORMAT)){
					//split up patternValue, pKeyValue, pGroupValue
					String patternValue = line.replaceFirst(PATTERN_FORMAT, "$1");
					//System.out.println("patternValue:"+ patternValue);
					int pLocationValue = Integer.valueOf(line.replaceFirst(PATTERN_FORMAT, "$2"));
					String hmLine = line.replaceFirst(PATTERN_FORMAT, "$3");
					//System.out.println("hmLine:"+ hmLine);
					Pattern p = Pattern.compile(PATTERN_HMSTR_FORMAT);
					Matcher m = p.matcher(hmLine);
					while(m.find()){
						Body body = new Body();
						body.pattern = patternValue;
						body.location = pLocationValue;
						String temp = m.group();//temp
						String pKeyValue = temp.replaceFirst(PATTERN_HMSTR_FORMAT, "$1");
						String pGroupValue = temp.replaceFirst(PATTERN_HMSTR_FORMAT, "$2");
						body.hashMap.put(pKeyValue, pGroupValue);
						int dateCaseValue = 0;
						if(temp.matches(PATTERN_PDATECASE_FORMAT)){
							dateCaseValue = Integer.valueOf(temp.replaceFirst(PATTERN_PDATECASE_FORMAT, "$3"));
						}
						body.dateCase = dateCaseValue;
						patternList.add(body);
						//System.out.println(m.group());
					}
				}
				if(parsing && line.matches(SPECIFIC_FORMAT)){
					//split up sKeyValue, sValueValue
					String hmLine = line.replaceFirst(SPECIFIC_FORMAT, "$1");
					//System.out.println("hmLine:"+ hmLine);
					Pattern p = Pattern.compile(SPECIFIC_HMSTR_FORMAT);
					Matcher m = p.matcher(hmLine);
					while(m.find()){
						Body body = new Body();
						body.pattern = "";
						body.location = -1;
						String sKeyValue = m.group();//temp
						String sValueValue = sKeyValue.replaceFirst(SPECIFIC_HMSTR_FORMAT, "$2");
						sKeyValue = sKeyValue.replaceFirst(SPECIFIC_HMSTR_FORMAT, "$1");
						body.hashMap.put(sKeyValue, sValueValue);
						patternList.add(body);
						//System.out.println(m.group());
					}
				}
				if(parsing && line.equals("#tagEnd(logId):\""+ logId +"\"")){
					parsing = false;
					isExist = true;
					//System.out.println("parsing = false");
					break;
				}
			}//line.startsWith("#tag")
		}
		lpd.close();

		/*
		int i = 0;
		while(i < patternList.size()){
			System.out.println(patternList.get(i).pattern);
			String[] keys = patternList.get(i).hashMap.keySet().toArray(new String[0]);
			for(String key:keys){
				System.out.println(key);
				System.out.println(patternList.get(i).hashMap.get(key));
			}
			i++;
		}
		*/
		return isExist;
	}//getLpdStructure(String logId)
	
	/*1. no INPUT ,execute date is today and all logIds defined in the LPD.
	 *2. INPUT startDate and endDate,execute all logIds defined in the LPD.
	 *3. INPUT startDate, endDate and logIds.
	*/
	public static void main(String args[]) {
		String logStartDateStr = "";
		String logEndDateStr = "";
		Calendar logStartDate = Calendar.getInstance();
		Calendar logEndDate = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		//get date
		if(args.length == 0){
			logEndDate.add(Calendar.DATE, 1);
			System.out.println("0_logStartDate:"+ sdf.format(logStartDate.getTime()));
			System.out.println("0_logEndDate:"+ sdf.format(logEndDate.getTime()));
		}else if(args.length == 1){
			System.out.println("wrong args number.");
			System.exit(-1);
		}else if(args.length >= 2){
			logStartDateStr = args[0].trim();
			logEndDateStr = args[1].trim();
			if(!(logStartDateStr.matches("\\d{8}") && logEndDateStr.matches("\\d{8}"))){
				System.out.println("input Date is not matches d{8} format.");
				System.exit(-1);
			}else{
				try{
					logStartDate.setTime(sdf.parse(logStartDateStr));
					logEndDate.setTime(sdf.parse(logEndDateStr));
					System.out.println("2_logStartDate:"+ sdf.format(logStartDate.getTime()));
					System.out.println("2_logEndDate:"+ sdf.format(logEndDate.getTime()));
				}catch(ParseException pe){
					pe.printStackTrace();
					System.exit(-1);
				}
			}
		}
		
		//get logId (ED90.log)
		ArrayList<String> logIdList = new ArrayList<String>(0);
		if(args.length >= 3){//get specific logId
			for(int i = 2; i < args.length; i++){
				logIdList.add(args[i].trim());
			}
		}else if(args.length <= 2){//no specific logId, get all of the logPartitionDefine.
			try{
				File file = new File(LOG_PARTITION_DEFINE_PATH);
				BufferedReader lpd = new BufferedReader(new FileReader(file));
				String line = "";
				while((line = lpd.readLine()) != null){
					line = line.trim();
					if(line.matches(TAG_BEGIN)){
						logIdList.add(line.replaceFirst(TAG_BEGIN, "$1"));
					}
				}
				lpd.close();
			}catch(Exception e){
				e.printStackTrace();
				System.exit(-1);
			}
		}
		
		//execute
		while(logStartDate.compareTo(logEndDate) < 0){
			String doDateStr = sdf.format(logStartDate.getTime());
			System.out.println("loop_doDateStr:"+ doDateStr);
			for(int i = 0; i<logIdList.size(); i++){
				//D:\rtc\workspace_pla\PLA\pdb\201404\20140429_pdb1\log-20140604-JOB_N5203A.txt
				boolean getLpdStructureOk = false;
				String logId = logIdList.get(i);
				System.out.println(logId);
				try{
					getLpdStructureOk = getLpdStructure(logId);
				}catch(FileNotFoundException fileNotFundException){
					fileNotFundException.printStackTrace();
				}catch(IOException ioException){
					ioException.printStackTrace();
					System.exit(-1);
				}
				if(getLpdStructureOk){
					for(int j = 0; j<serverList.length; j++){
						String logFileName = LOG_DIRECTORY +"/"+ doDateStr.substring(0, 6) +"/"+ doDateStr +"_"+ serverList[j] +"/log-"+ doDateStr +"-"+logId;
						System.out.println(logFileName);
						try{
							parseLog(logFileName);
						}catch(FileNotFoundException fileNotFundException){
							fileNotFundException.printStackTrace();
						}catch(IOException ioException){
							ioException.printStackTrace();
							System.exit(-1);
						}catch(ParseException parseException){
							parseException.printStackTrace();
							System.exit(-1);
						}
					}//for(int j = 0; j<serverList.length; j++)
				}else{
					System.out.println("getLpdStructure("+ logId + ") return false.");
				}
			}//for(int i = 0; i<logIdList.size(); i++)
			logStartDate.add(Calendar.DATE, 1);
		}//while(logStartDate.compareTo(logEndDate) < 0)
		System.out.println("ParseRegularLog Done");
	}//main
}
