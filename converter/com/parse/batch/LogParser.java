package com.parse.batch;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.parse.base.ParserLauncher;

public class LogParser implements Runnable{
	static final String DECL_NO = "DECL_NO";
	static final String MAWB = "MAWB";
	static final String HAWB = "HAWB";
	static final String VSL_REG_NO = "VSL_REG_NO";
	static final String SO_NO = "SO_NO";
	String data;
	String orgdata;
	boolean containTime = false;
	boolean beakPointFormatSpecified = false;
	BufferedReader reader = null;
	BufferedWriter writer = null;
	HashMap<String, String> patternCollect = new HashMap<String, String>();
	HashMap<String, String> patternLib = new HashMap<String, String>();
	
	String fileName = "";
	String fileInPath = "";
	String fileOutPath = "";
	static String logFormat1 = "log\\-([0-9]{8})\\-(([0-9|A-Z]{1,}))\\."; //log-YYYYMMDD-LOG_ID.log/txt
	static String logFormat2 = "log\\-(([0-9|A-Z]{1,}))\\.";			//log-LOG_ID.log/txt
	boolean logWithDate = false;
	
	boolean breakPoint = false;
	
	String declFormat = "([A-Z]{2}[\\s|A-Z|0-9]{2}[A-Z|0-9]{10})";
	String vslFormat = "([0-9]{6}|[0-9]{1}[A-Z|0-9]{5})(.*)";//海關通關號碼：必為5碼
	String soNoFormat = "([0-9|A-Z]{4})(.*)"; //裝貨單號碼必為4碼
	String chkSoNoFormat = "([A-Z]{4})";//檢查抓到的裝貨單是否為全英文，若為全英文代表有抓錯
	String mawbFormat = "([0-9]{3}[\\-]{0,1}[0-9]{4,32})"; //主提單最多35碼
	String hawbFormat = "([0-9|A-Z|\\-]{8,35})"; ////分提單最多35碼
	String chkHawbFormat = "([A-Z]{8,})";//檢查抓到的分提單是否前8碼皆為英文，若是則代表有抓錯
	
	//for DDL
	String yearStr = null;
	String timeStr = null;
	String LogId = null;
	String dateString = null;
	HashMap<String,String>  declValue= new HashMap<String,String>();
	HashMap<String,String>  vslRegNoValue = new HashMap<String,String> ();
	HashMap<String,String>  soNoValue = new HashMap<String,String> ();
	HashMap<String,String>  mawbValue = new HashMap<String,String> ();
	HashMap<String,String>  hawbValue = new HashMap<String,String> ();
	
	
	public void run(){
		dataAnalizer();
	}
	
	//建構式
	public LogParser(String log_name, String fileInPath, String fileOutPath){
		this.fileName = log_name;
		this.fileInPath = fileInPath;
		this.fileOutPath = fileOutPath;
	}
	 
	//分析斷點
	public void dataAnalizer(){
		Pattern logPattern ;
		Matcher TimeMatchCheck  ;
		int stopAnaly = 0;
	
		Pattern ptn1 = Pattern.compile(logFormat1);
		String parseTime = fileName;
		Matcher mch1 = ptn1.matcher(parseTime) ;
		if(!mch1.find()){
			System.out.println("logFormat1 undefined, check logFormat2...");
			Pattern.compile(logFormat2);
			mch1 = ptn1.matcher(parseTime) ;
			if(!mch1.find()){
				System.out.println("logFormat2 still undefined");
				return;
			}else{
				yearStr = "00010101"; //有此類log再處理 pending
			}
		}else{
			logWithDate = true;
		}
	 
		if(logWithDate){
			yearStr = mch1.group(1);
			LogId  = mch1.group(2);
		}else{
			LogId  = mch1.group(1);
		}
		
		
		//檢查log內容開頭是否為日期+時間(0616.040304.540)
		logPattern = Pattern.compile("\\d{4}[.]\\d{6}[.]\\d{3}\\s*.*");
		String toParse ;
		
		//檢查是否有指定斷點格式
		if(!ParserLauncher.logLib.get(LogId).equals("?")){
			beakPointFormatSpecified = true;
		}
		
		try{
			reader = getFile();
			while((data = reader.readLine()) != null){
				stopAnaly ++;
				TimeMatchCheck = logPattern.matcher(data);
				//LOG開頭為日期+時間(0616.040304.540)
				if(TimeMatchCheck.matches()){
					toParse = data.substring(15).trim();
					containTime = true;
					if(toParse == null || toParse.isEmpty()){ 
						toParse = "";
					}
				}else{
					toParse = data;
					if(toParse == null){
						toParse = "";
					}
				}
				//沒有指定斷點
				if(!beakPointFormatSpecified){
					//System.out.println("toParse = "+toParse);
					if(patternCollect.get(toParse) == null){
						patternCollect.put(toParse, toParse);
					}else{
						//System.out.println("toParse="+toParse);
						try{
						if(	toParse.isEmpty()  //以空行為斷點
								|| toParse.substring(0, 7).equals("-------") 
								|| toParse.substring(0, 7).equals("=======")
								|| toParse.substring(0, 7).equals("*******")
							){
							patternLib.put( toParse, toParse);
						}	
						}catch(StringIndexOutOfBoundsException id){} //LOG內容未達7碼發生Exception就算了
						 
					}
				}else{
					if(toParse.substring(0,ParserLauncher.logLib.get(LogId).length()).equals(ParserLauncher.logLib.get(LogId))){
						breakPoint = true;
					}
				}

				//分析前1000行就好了
				if(stopAnaly > 1000){
					break;
				}
			}

			if(patternLib.size() == 0 && !beakPointFormatSpecified){
				System.out.println("log formate undefined");
			}else if(beakPointFormatSpecified && !breakPoint){ //保檢再檢查一下指定的斷點是否還適合
				System.out.println("designated log formate undefined:"+LogId);
			} 
			else
				parseLog();
			
		}catch(Exception e){
			System.out.println("dataAnalizer Exp:"+e);
		}finally{
			try{
				reader.close();
				if(writer!= null){
					writer.close();
				}
			}catch(Exception e){
				System.out.println("dataAnalizer:file closing Exp:"+e);
			}
		}

		
	}
	
	//讀檔
	public BufferedReader getFile(){
		File fin = new File(fileInPath + fileName);  
		
		FileReader rin = null;
		BufferedReader bin = null;
		try{
			rin = new FileReader(fin);
			//bin = new BufferedReader(rin);
			bin= new BufferedReader(new InputStreamReader(new FileInputStream(fin), "UTF-8"));
		}catch(Exception e){
			System.out.println("reading Exp:"+e);
		}
		return bin;
	}
	
	//寫出檔案
	public BufferedWriter getWriter(){
	 
		File f = new File(fileOutPath +fileName+".out");
		FileWriter fw = null;
		BufferedWriter bw = null;
		try{
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);
		}catch(Exception out){
			System.out.println("writting Exp:"+out);
			return bw;
		}
		return bw;
	}
	
		public void parseLog(){
			reader = getFile();
			writer = getWriter();
			String parseLog = null;
			  
			try{
					while((data = reader.readLine()) != null){
						if(containTime){
							orgdata = data;
							data = data.substring(15).trim();
						}
						//System.out.println("data = "+data+"; containTime = "+containTime);
						String key = data; //
						if(key == null || key.isEmpty()){
							key = "";
						}
						//System.out.println("data = "+data);
						breakPoint = false;
						//System.out.println("242");
						if(beakPointFormatSpecified){ //有指定斷點之格式
							if(key.equals("")){ //以空行作為斷點
								if( ParserLauncher.logLib.get(LogId).equals("")){
									breakPoint = true;
								}
							} 
							else if(key.substring(0,ParserLauncher.logLib.get(LogId).length()).equals(ParserLauncher.logLib.get(LogId))){
								breakPoint = true;
							}
						}else if(patternLib.get(key) != null){
							breakPoint = true;
						}
						//System.out.println("data = "+data);
						//System.out.println("breakPoint = "+breakPoint);
						//System.out.println("259");
						if( !breakPoint){
							//System.out.println("data = "+data);
							
							if(data != null && !data.isEmpty() ){
								if( parseLog == null){
									parseLog = data;
									if(containTime){
										//0616.040304.540
										timeStr = orgdata.substring(0,15).trim();
									}
								}else{
									parseLog = parseLog +"\n"+ data;
								};
							}

						}else{ 
							//System.out.println("else = "+parseLog);
							if(parseLog == null){//第一行為斷點時，parseLog沒有值
								continue;
							}
						//	System.out.println("parseLog1 = "+parseLog);
							
							//直接由字串中取得處理時間
							if(ParserLauncher.timeFormatLib.containsKey(LogId)){
								Pattern ptn = Pattern.compile((String)ParserLauncher.timeFormatLib.get(LogId).get(0));
								Matcher mch = ptn.matcher(parseLog) ;
								if(mch.find()){
									 //System.out.println(mch.group(new Integer((String)ParserLauncher.timeFormatLib.get(LogId).get(1))));
									dateString = mch.group(new Integer((String)ParserLauncher.timeFormatLib.get(LogId).get(1)));
								}
							}
							keyParser(parseLog,DECL_NO,25,14);
							keyParser(parseLog,VSL_REG_NO,20,6);
							keyParser(parseLog,SO_NO,15,4);
							keyParser(parseLog,MAWB,50,12);
							keyParser(parseLog,HAWB,35,8);
							
							//System.out.println("declValue before insert = "+declValue.keySet());
							doInsert(parseLog);
							parseLog = null;
							declValue = new HashMap<String,String>();
							vslRegNoValue = new HashMap<String,String>();
							soNoValue = new HashMap<String,String>();
							mawbValue = new HashMap<String,String>();
							hawbValue = new HashMap<String,String>();
							
						}//else end

					} //while((data = reader.readLine()) != null) end
					//處理最後一筆(若最後一行沒有斷點者)
					if(parseLog != null){
						//parseDecl(parseLog); 
						keyParser(parseLog,DECL_NO,25,14);
						keyParser(parseLog,VSL_REG_NO,20,6);
						keyParser(parseLog,SO_NO,15,4);
						keyParser(parseLog,MAWB,50,12);
						keyParser(parseLog,HAWB,35,8);
					}
					
			}catch(Exception e){
				System.out.println(LogId+":parseLog:reading Exp:"+e);
			}finally{
				try{
					reader.close();
					writer.close();
				}catch(Exception e){
					System.out.println(LogId+":parseLog:file clossing Exp:"+e);
				}
			}
		}
		
		
		/*
			estimateLen:符合的KEY的欄位+KEY值的大略長度。例：DECL_NO = AA  01234567890，約抓25碼進行分析
		 	formatLen:  Key值的長度。例：若要抓報單號碼，則指定長度為14碼。
		 */
		public void keyParser(String parseLog, String parseType, int estimateLen,int formatLen){
		
			HashMap<String,String> keyValue = new HashMap<String,String>();
			String format = null;
			String phaseParse = null;//
			String parseFrame [] = null;
			
			if(parseType.equals(DECL_NO)){
				parseFrame = new String[]{"DECL_NO","decl_no","報單號碼"};
				format = declFormat;
			} else if(parseType.equals(MAWB)){
				parseFrame = new String[]{"MAWB","mawb","主提單"};
				format = mawbFormat;
			}else if(parseType.equals(HAWB)){
				parseFrame = new String[]{"HAWB","hawb","Hawb","分提單"};
				format = hawbFormat;
			}else if(parseType.equals(VSL_REG_NO)){
				parseFrame = new String[]{"VSL_REG_NO","vslRegNo","海關通關號碼"};
				format = vslFormat;
			}else if(parseType.equals(SO_NO)){
				parseFrame = new String[]{"SO_NO","so_no","SoNo","裝貨單"};
				format = soNoFormat;
			}
			else
			{
				System.out.println("keyParser:parseType undefined!"+parseType);
				return;
			}
			
			int indexToParse = -5;
			int keyInd = -1; //符合KEY值欄位的第一個字元
			int keyIndEnd = 0;  
			int fromInd = 0;
			boolean matched = false;
			boolean lastPrase = false;
			try{	
				for(String str:parseFrame){
					//System.out.println("parse Format = "+str);
					lastPrase = false;
					keyInd = parseLog.indexOf(str);
					if(keyInd == -1){ //找不到符合KEY值的欄位
						continue;
					}
        	
					fromInd = 0;
					//System.out.println("str = "+str);
					while(true){
						//System.out.println("str = "+str);
						//System.out.println("fromInd1 = "+fromInd);
						keyInd = parseLog.indexOf(str,fromInd); //找到符合KEY值的欄位；fromInd作為尋找下一個KEY欄位的起始點
						/*
						if(keyInd < 0 ){
							fromInd = keyIndEnd; //
							keyIndEnd = fromInd + estimateLen;
						}else{
							keyIndEnd = keyInd + estimateLen; //
						}
						if(lastPrase){
							break;
						}
						//避免indexOutOfBoundary
						if(parseLog.length()-1 < keyIndEnd ){
							keyIndEnd = parseLog.length()-1;
							lastPrase = true;
							//System.out.println("final keyIndEnd = "+keyIndEnd);
						}
						//System.out.println("lastPrase2 = "+lastPrase);
					
						if(keyInd < 0){
							continue;
						}						
						*/
						
						if(keyInd < 0 ){break;}
						keyIndEnd = keyInd + estimateLen; 
						//避免indexOutOfBoundary
						if(parseLog.length()-1 < keyIndEnd ){
							keyIndEnd = parseLog.length()-1;
							lastPrase = true;
							//System.out.println("final keyIndEnd = "+keyIndEnd);
						}
						
						phaseParse = parseLog.substring(keyInd,keyIndEnd); //mawb = xxx-xxxxxxxx
						
						//System.out.println("phaseParse = "+phaseParse); 
					
						Pattern ptn = Pattern.compile(format);
						Matcher mch = ptn.matcher(phaseParse) ;
						if(!mch.find()){
							fromInd = keyInd + str.length(); 
							continue;
						}
						
						try{
							for(int i = 0; i < mch.groupCount();i++){
								
								//if(mch.group(i).length() < formatLen|| mch.group(i).contains("\n")){	
								if(( parseType.equals(VSL_REG_NO) ||  parseType.equals(SO_NO) ) && ( mch.group(i).length() != formatLen || mch.group(i).contains("\n") )){				
									if(!matched){
										fromInd = keyInd + str.length();
									}
									continue;
								}else{
									//System.out.println("	value = "+mch.group(i));
									if( parseType.equals(SO_NO)){
										Pattern chkPtn = Pattern.compile(chkSoNoFormat);
										Matcher chkMch = chkPtn.matcher(mch.group(i)) ;
										if(chkMch.find()){	
											fromInd = keyInd + str.length();
											continue;
										}
									}
									
									matched = true;
									indexToParse = parseLog.indexOf(phaseParse,fromInd);//取得該片斷字串的比對起始點
									//System.out.println("  indexToParse1 = "+indexToParse);
									indexToParse = parseLog.indexOf(mch.group(i),indexToParse);//取得KEY值的起始點
									//System.out.println("  indexToParse2 = "+indexToParse +", mch.group(i) = "+mch.group(i)+", len = "+mch.group(i).length());
									fromInd = indexToParse + mch.group(i).length();//將key值的起始點+長度，作為下一LOOP的比對起始點
									keyValue.put(mch.group(i), parseLog);
									//System.out.println("value to put = "+mch.group(i));
									//System.out.println("keyValue = "+keyValue.keySet());
									
								}	
							
							}//for(int i = 0; i < mch.groupCount();i++) end
							
						}catch(IllegalStateException il){
							//System.out.println("EXP = "+il);
							break;
						}
						
					} //while(true) end
				}
			}catch(Exception e){
				System.out.println(LogId+": keyParser exp:"+e);
			}
			
			if(parseType.equals(DECL_NO)){
				declValue.putAll(keyValue);
			} else if(parseType.equals(MAWB)){
				mawbValue.putAll(keyValue);
			}else if(parseType.equals(HAWB)){
				hawbValue.putAll(keyValue);
			}else if(parseType.equals(VSL_REG_NO)){
				vslRegNoValue.putAll(keyValue);
			}else if(parseType.equals(SO_NO)){
				soNoValue.putAll(keyValue);
			}
			
			
		}
		
		public void doInsert(String parseLog_in){
			int declRow = declValue.size();
			int vslRegNoRow = vslRegNoValue.size();
			int soNoRow = soNoValue.size();
			int mawbRow = mawbValue.size();
			int hawbRow = hawbValue.size();
			int keyCnt = 0;  
			boolean multiKey = false;  
			String declNo = "";
			String vslRegNo = "";
			String soNo = "";
			String mawb = "";
			String hawb = "";
			//檢查是否有多個同性質的KEY值。例：一段字串中，包含1個以上的報單號碼
			int orderAry [] = {declRow,vslRegNoRow,soNoRow,mawbRow,hawbRow};
			for(int i = 0; i< orderAry.length; i++){
				keyCnt = keyCnt + orderAry[i];
				if(orderAry[i] > 1){
					multiKey = true;
					if(i == 0){
						System.out.println("declNo cnt < "+orderAry[i]+" >");
						System.out.println(declValue.keySet());
					}else if( i == 1){
						System.out.println("vslRegNo cnt < "+orderAry[i]+" >");
						System.out.println(vslRegNoValue.keySet());
					}else if( i == 2){
						System.out.println("soNo cnt < "+orderAry[i]+" >");
						System.out.println(soNoValue.keySet());
					}else if( i == 3){
						System.out.println("Mawb cnt < "+orderAry[i]+" >");
						System.out.println(mawbValue.keySet());
					}else if( i == 4){
						System.out.println("Hawb cnt < "+orderAry[i]+" >");
						System.out.println(hawbValue.keySet());
					}
				}
			} //for(int i = 0; i< orderAry.length; i++) end
			
			if(multiKey){
				System.out.println("multiKey within single phrase, coding review is needed, logId < "+LogId+" >,"+fileInPath);
				return;
			}
			
			if(keyCnt == 0){
				return;
			}
			//System.out.println("declValue == >"+declValue.keySet());
			for(Object key:declValue.keySet()){
				declNo = (String)key;
			}
			for(Object key:vslRegNoValue.keySet()){
				vslRegNo = (String)key;
			}
			for(Object key:soNoValue.keySet()){
				soNo = (String)key;
			}
			for(Object key:mawbValue.keySet()){
				mawb = (String)key;
			}
			for(Object key:hawbValue.keySet()){
				hawb = (String)key;
			}
			parseLog_in = parseLog_in.replaceAll("\"", "\"\"");
			try{
				//writer.write("=============================");
				//dateFormat: 2014-06-16 07:20:58.510
				if(ParserLauncher.timeFormatLib.containsKey(LogId)){
					dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new SimpleDateFormat((String) ParserLauncher.timeFormatLib.get(LogId).get(2)).parse(dateString));
				}else{
					dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format( new SimpleDateFormat("yyyyMMdd MMdd.HHmmss.SSS").parse(yearStr+" "+timeStr));
				}
				//Date d = new SimpleDateFormat("yyyyMMdd MMdd.HHmmss.SSS").parse(yearStr+" "+timeStr);
				//Date d = new SimpleDateFormat("yyyyMMdd MMdd.HHmmss.SSS").parse(yearStr+" "+timeStr);
				//System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(d));
				writer.write("\""+dateString+"\"");
				writer.write("`\""+LogId+"\"");
				writer.write("`\""+declNo+"\""); //DECL_NO
				writer.write("`\""+vslRegNo+"\""); //VSL_REG_NO
				writer.write("`\""+soNo+"\""); //SO_NO
				writer.write("`\""+mawb+"\""); //MAWB
				writer.write("`\""+hawb+"\""); //HAWB
				writer.write("`\""+parseLog_in+"\""); //CONTENT
				writer.newLine();
				
			}catch(Exception e){
				System.out.println(LogId+":doInsert writting Exp:"+e);
			}
			
		}

		/*
		//Run Testing
		public static void main(String[] args){
			
			Pattern pptLog = Pattern.compile(logToParse);
			Matcher mchLog = null;
			ArrayList logList = new ArrayList();
			
		//	new LogParser().dataAnalizer();
			
			
			//取得資料夾內的檔名
			File source = new File("D:/workPla/pla/pdb/201406/20140616_pdb1");
			File[] file = source.listFiles();		
			for (int i = 0; i < file.length; i++) {
				//System.out.println(file[i].getParent() + file[i].getName());
				//System.out.println(file[i].getName());
				mchLog = pptLog.matcher(file[i].getName());
				if(mchLog.find()){
					logList.add(file[i].getName());
				}
			}
			
//			for(int i =0; i < logList){
//				
//			}
			
//			Pattern ptn1 = Pattern.compile(logFormat1);
//			String t = "log-20140101-IH52.txt";
//			Matcher mch1 = ptn1.matcher(t) ;
//			if(mch1.find()){
//				System.out.println("T-s:"+mch1.start());
//				System.out.println("T-end:"+mch1.end());
//				
//			}else{
//				System.out.println("F");
//			}
//			System.out.println("t len = "+t.length());
//			System.out.println("mch.groupCount() = "+mch1.groupCount());
//			String mawb = "dddd";
//			
//			for(int i = 0; i < mch1.groupCount();i++){
//				System.out.println(mch1.group(i)); 
//			}
//			
			
//			String ary [] = t.split("-");
//			
//			for(String str:ary){
//				System.out.println(str);
//			}
			
			
			
//			DateFormat dfIn = new SimpleDateFormat("yyyyMMddhhmmssSS");
//			DateFormat dfOut = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
//			String test = new String("2009020910554007");
//			try{
//				Date date = dfIn.parse(test);
//				Timestamp p = new Timestamp(date.getTime());
//				p.setNanos(073);
//				System.out.println(dfOut.format(date));
//				System.out.println("p ="+p);
//				
//			}catch(Exception i){}
//			
		}
		*/
	
		/* 
		 * select to_timestamp('20130612.150340.703','YYYYMMDD.HH24MISS.US')::timestamp without time zone;
		 *http://www.postgresql.org/docs/8.1/interactive/functions-formatting.html
		 *insert into exlgdmm values(to_timestamp('20140620.150101.073','YYYYMMDD.HH24MISS.US'),'ED90',null,null,null,null,null,'TEST');
		 * */
		 

}
