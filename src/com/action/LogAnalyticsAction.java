package com.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model.LogAnalyticsModel;

import com.bean.QueryParam;


public class LogAnalyticsAction extends HttpServlet{
	private static final long serialVersionUID = 1L;
	public LogAnalyticsAction() {
        super();
        model = new LogAnalyticsModel();
    }
	private LogAnalyticsModel model; 
	//查詢
	private QueryParam qry(HttpServletRequest request) throws ParseException{
		QueryParam qp = new QueryParam();
		qp.setDeclNo(request.getParameterValues("declNo")[0].equals("")?null:request.getParameterValues("declNo")[0]);
		qp.setVslRegNo(request.getParameter("vslRegNo").equals("")?null:request.getParameter("vslRegNo"));
		qp.setSoNo(request.getParameter("soNo").equals("")?null:request.getParameter("soNo"));
		qp.setMawb(request.getParameter("mawb").equals("")?null:request.getParameter("mawb"));
		qp.setHawb(request.getParameter("hawb").equals("")?null:request.getParameter("hawb"));
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		qp.setBeginLogTime(request.getParameter("startDate").equals("")?null:new java.sql.Date(dFormat.parse(request.getParameter("startDate")).getTime()));
		qp.setEndLogTime(request.getParameter("endDate").equals("")?null:new java.sql.Date(dFormat.parse(request.getParameter("endDate")).getTime()));
		qp.setPage(1);
		String [] logId = request.getParameter("qryLogName").split(",");
		qp.setLogIds(logId);
		return qp;
	}
	//換頁
	private QueryParam turnPage(HttpServletRequest request) throws ParseException{
		QueryParam qp = new QueryParam();
		
		String [] colName = request.getParameterValues("model")[0].split("&");
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		for (String col : colName){
			if(col.startsWith("declNo="))
				qp.setDeclNo(col.substring(col.indexOf("=")+1).equals("")?null:col.substring(col.indexOf("=")+1));
			else if(col.startsWith("qryLogName=")){
				col = col.replaceAll("\\+", "");
				String [] logId = col.substring(col.indexOf("=")+1).split("%2C");
				qp.setLogIds(logId);
			}
			else if(col.startsWith("vslRegNo="))
				qp.setVslRegNo(col.substring(col.indexOf("=")+1).equals("")?null:col.substring(col.indexOf("=")+1));
			else if(col.startsWith("soNo="))
				qp.setSoNo(col.substring(col.indexOf("=")+1).equals("")?null:col.substring(col.indexOf("=")+1));
			else if(col.startsWith("mawb="))
				qp.setMawb(col.substring(col.indexOf("=")+1).equals("")?null:col.substring(col.indexOf("=")+1));
			else if(col.startsWith("hawb="))
				qp.setHawb(col.substring(col.indexOf("=")+1).equals("")?null:col.substring(col.indexOf("=")+1));
			else if(col.startsWith("startDate="))
				qp.setBeginLogTime(col.substring(col.indexOf("=")+1).equals("")?null:(java.sql.Date)dFormat.parse(col.substring(col.indexOf("=")+1)));
			else if(col.startsWith("endDate="))
				qp.setEndLogTime(col.substring(col.indexOf("=")+1).equals("")?null:(java.sql.Date)dFormat.parse(col.substring(col.indexOf("=")+1)));
		}
		
		int	page = Integer.valueOf(request.getParameter("page"));
		qp.setPage(page);
		
		return qp;
	}
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		request.setCharacterEncoding("UTF-8");
	    response.setContentType("text/html; charset=UTF-8");
//		String countryCode = request.getParameter("declNo");
//		System.out.println("countryCode <"+countryCode+">");
//		String body = request.getParameter("body");
//		System.out.println("body <"+body+">");
		//System.out.println("model "+request.getParameterValues("model")[0]);
		System.out.println("declNo "+request.getParameterValues("declNo"));
		System.out.println(request.getParameter("page"));
		System.out.println(request.getParameter("sidx"));
		System.out.println(request.getParameter("sord"));
		System.out.println(request.getParameter("rows"));
		System.out.println(request.getParameter("totalrows"));
		System.out.println(request.getParameter("records"));
        PrintWriter out = response.getWriter();
//        response.setContentType("text/html");
        response.setHeader("Cache-control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "-1");
        response.setHeader("Content-Type", "application/json; charset=UTF-8");
        
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");
//        response.setCharacterEncoding("UTF-8");
        
        //設定RESPONSE 回傳成功
        String responseStr = "{\"success\":true}" ;
//        測試語句
//        if (request.getParameter("page") == null)
//        	responseStr = "{\"success\":true,\"page\":\"1\",\"total\":\"2\",\"records\":\"17\",\"rows\":[" +
//        			"{\"declNo\":\"bdklsajabdklsajf\"},{\"declNo\":\"def\"}" +
//            		",{\"declNo\":\"d''ef\"},{\"declNo\":\"def\"},{\"declNo\":\"def\"},{\"declNo\":\"def\"}" +
//            		",{\"declNo\":\"def\"},{\"declNo\":\"def\"},{\"declNo\":\"def\"},{\"declNo\":\"def\"}" +
//            		",{\"declNo\":\"def\"},{\"declNo\":\"def\"},{\"declNo\":\"def\"},{\"declNo\":\"def\"}" +
//            		",{\"declNo\":\"def\"}]" +
//            		",\"procDesc\":"+
//            		"\"0521.011420.240   ===== ER11 START =====  EDM_rec.DECL_NO=<<CH  0322365NXP>>,DO_NX801=<<Y>>,FIRIST_RUN=<<F>>nextLINE0521.011420.240 >>> ITEM=1, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.250 >>> ITEM=2, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.250 >>> ITEM=3, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.250 >>> ITEM=4, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.250 >>> ITEM=5, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.250 >>> ITEM=6, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.250 >>> ITEM=7, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.250 >>> ITEM=8, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.260 >>> ITEM=9, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.260 >>> ITEM=10, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.260 >>> ITEM=11, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.260 >>> ITEM=12, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.260 >>> ITEM=13, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.260 >>> ITEM=14, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.260 >>> ITEM=15, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.260 >>> ITEM=16, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.270 >>> ITEM=17, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.270 >>> ITEM=18, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.270 >>> ITEM=19, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.270 >>> ITEM=20, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.270 >>> ITEM=21, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.270 >>> ITEM=22, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.270 >>> ITEM=23, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.280 >>> ITEM=24, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.280 >>> ITEM=25, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.280 >>> ITEM=26, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.280 >>> ITEM=27, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.280 >>> ITEM=28, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.280 >>> ITEM=29, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.280 >>> ITEM=30, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.290 >>> ITEM=31, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.290 >>> ITEM=32, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.290 >>> ITEM=33, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.290 >>> ITEM=34, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.290 >>> ITEM=35, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.290 >>> ITEM=36, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.290 >>> ITEM=37, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.300 >>> ITEM=38, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.300 >>> ITEM=39, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.300 >>> ITEM=40, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.300 >>> ITEM=41, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.300 >>> ITEM=42, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.300 >>> ITEM=43, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.300 >>> ITEM=44, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.300 >>> ITEM=45, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.310 >>> ITEM=46, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.310 >>> ITEM=47, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.310 >>> ITEM=48, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.310 >>> ITEM=49, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.310 >>> ITEM=50, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.310 >>> ITEM=51, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.310 >>> ITEM=52, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.310 >>> ITEM=53, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.320 >>> ITEM=54, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.320 >>> ITEM=55, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.320 >>> ITEM=56, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.320 >>> ITEM=57, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.320 >>> ITEM=58, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.320 >>> ITEM=59, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.320 >>> ITEM=60, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.330 >>> ITEM=61, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.330 >>> ITEM=62, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.330 >>> ITEM=63, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.330 >>> ITEM=64, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.330 >>> ITEM=65, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.330 >>> ITEM=66, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.330 >>> ITEM=67, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.340 >>> ITEM=68, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.340 >>> ITEM=69, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.340 >>> ITEM=70, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.340 >>> ITEM=71, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.340 >>> ITEM=72, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.340 >>> ITEM=73, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.340 >>> ITEM=74, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.340 >>> ITEM=75, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.350 >>> ITEM=76, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.350 >>> ITEM=77, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.350 >>> ITEM=78, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.350 >>> ITEM=79, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.350 >>> ITEM=80, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.350 >>> ITEM=81, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.350 >>> ITEM=82, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.350 >>> ITEM=83, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.360 >>> ITEM=84, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.360 >>> ITEM=85, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.360 >>> ITEM=86, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.360 >>> ITEM=87, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.360 >>> ITEM=88, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.360 >>> ITEM=89, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.360 >>> ITEM=90, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.360 >>> ITEM=91, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.370 >>> ITEM=92, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.370 >>> ITEM=93, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.370 >>> ITEM=94, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.370 >>> ITEM=95, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.370 >>> ITEM=96, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.370 >>> ITEM=97, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.370 >>> ITEM=98, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.370 >>> ITEM=99, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.380 >>> ITEM=100, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.380 >>> ITEM=101, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.380 >>> ITEM=102, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.380 >>> ITEM=103, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.380 >>> ITEM=104, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.380 >>> ITEM=105, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.390 >>> ITEM=106, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.390 >>> ITEM=107, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.390 >>> ITEM=108, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.390 >>> ITEM=109, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.390 >>> ITEM=110, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.390 >>> ITEM=111, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.390 >>> ITEM=112, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.400 >>> ITEM=113, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.400 >>> ITEM=114, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.400 >>> ITEM=115, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.400 >>> ITEM=116, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.400 >>> ITEM=117, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.400 >>> ITEM=118, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.400 >>> ITEM=119, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.400 >>> ITEM=120, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.410 >>> ITEM=121, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.410 >>> ITEM=122, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.410 >>> ITEM=123, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.410 >>> ITEM=124, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.410 >>> ITEM=125, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.410 >>> ITEM=126, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.410 >>> ITEM=127, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.420 >>> ITEM=128, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.420 >>> ITEM=129, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.420 >>> ITEM=130, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.420 >>> ITEM=131, REG_LIST=<,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.420 >>> ITEM=132, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.420 >>> ITEM=133, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.420 >>> ITEM=134, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.430 >>> ITEM=135, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.430 >>> ITEM=136, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.430 >>> ITEM=137, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.430 >>> ITEM=138, REG_LIST=<123,,,,,,,,,,>, PERMIT_NO_LIST=<>, INGORE_ALL_PERMIT=<>nextLINE0521.011420.430   ===== ER11 END =====\""
//            		+"}";
//        else
//        	responseStr = "{\"success\":true,\"page\":\"2\",\"total\":\"2\",\"records\":\"17\",\"rows\":[" +
//        			"{\"id\":\"16\",\"declNo\":\"sec1\"},{\"id\":\"17\",\"declNo\":\"sec2\"}]}";
//        System.out.println("{\"success\":true,\"rows\":[{\"declNo\":\"abdklsajf\"},{\"declNo\":\"def\"}]}");
        
        try {
        	if (request.getParameter("qry")!=null)
        		responseStr=model.qryEXLGDMM_JSON(qry(request));
        	else {
        		System.out.println(request.getParameterValues("model")[0]);
        		if(request.getParameterValues("model")[0].contains("chgPage=true"))
        			responseStr=model.qryEXLGDMM_JSON(turnPage(request));
        	}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(responseStr);
        String netEncode = java.net.URLEncoder.encode(responseStr,"UTF-8");
        System.out.println(netEncode);
        out.println(responseStr);
        
//        if(countryInfo.getName() == null){
//            myObj.addProperty("success", false);
//        }
//        else {
//            myObj.addProperty("success", true);
//        }
//        myObj.add("countryInfo", countryObj);
//        out.println(myObj.toString());
        out.close();
	}
}
