package com.parse.batch;

public class contentParser {
	
	String logName = null;
	int ind = -1;
	
	public contentParser(String log){
		logName = log;
	}
	
	public String getResult(String logSegment){
		String result = null;
		String[] msgFormat = null;
		if("EA90".equals(logName)){
			msgFormat = new String[]{"MSG={Accept!","MSG={Reject"};
		}else if("ED90".equals(logName)){
			msgFormat = new String[]{"MSG={ED90 Accept","MSG={ED90 Reject"};
		}
		
		for(String msg:msgFormat){
			ind = logSegment.indexOf(msg);
			if(ind >= 0){
				result = logSegment.substring(ind);
			}
		}
		return result;
		
	}
	
}
