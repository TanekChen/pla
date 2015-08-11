<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.io.*,java.util.*,java.sql.*"%>
<%@ page import="javax.servlet.http.*,javax.servlet.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css"
	href="css/jquery-ui-1.11.0/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css" />
<script src="js/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="js/jquery.jqGrid.min.js" type="text/javascript"></script>
<script src="js/i18n/grid.locale-tw.js" type="text/javascript"></script>
<script src="css/jquery-ui-1.11.0/jquery-ui.min.js" type="text/javascript"></script>
<script src="js/datepick/jquery.datepick.min.js" type="text/javascript"></script>
<script src="js/datetimepicker/ui.datetimepicker.js" type="text/javascript"></script>
<script src="js/datetimepicker/ui.datetimepicker-zh-TW.js" type="text/javascript"></script>
<script>
	function query(){
		//alert($("[name='declNo']").val());
		var declNo=$("[name='declNo']").val();
		var vslRegNo=$("[name='vslRegNo']").val();
		var soNo=$("[name='soNo']").val();
		var mawb=$("[name='mawb']").val();
		var hawb=$("[name='hawb']").val();
		var startDate=$("[name='startDate']").val();
		var endDate=$("[name='endDate']").val();
		var qryLogName=$("[name='qryLogName']").val();
		qryLogName = qryLogName.replace(/\s/g,"");
		var request = {"declNo":declNo,"vslRegNo":vslRegNo,"soNo":soNo,"mawb":mawb,"hawb":hawb,"startDate":startDate,"endDate":endDate,"qryLogName":qryLogName,"qry":true};
		
		// 讓換頁ACTION可以執行
		if ("true" != $("[name='chgPage']").val())
			$("[name='chgPage']").val("true");
		$.post("LogAnalyticsAction", request , function (response, status) {
			if (status == "success") {
				//alert(response.list[0].declNo);
				//alert(response.list[1].declNo);
				//var grid = jQuery("#list1")[0];
				//var jsongrid = eval("("+response.responseText+")"); 
				$("#list1")[0].addJSONData(response);
				$("[name='procDesc']").val(response['procDesc']);
				//jsongrid = null; 
				//response = null;
				//$('#list1').jqGrid('navGrid',"#pager",{edit:false,add:false,del:false});
			}
			else{
				alert(response);
			}
			//jQuery("#list1")[0].addJSONData(response);
		},'json');
		
	}
	function opendialog(id,rowNum){
		//alert(id +","+rowNum);
		var procDescListStr=$("[name='procDesc']").val();
		procDescListStr = procDescListStr.trim();
		//procDescListStr = procDescListStr.replace(/nextLINE/g,"<br />");
		//procDescListStr = procDescListStr.replace(/\+/g,"&nbsp;");
		//alert("<br/>");
		//alert(procDescListStr);
		
		var procDescList = procDescListStr.split("nextROW");
		//var decode = decodeURIComponent(procDescList[rowNum-1]);
		var replaceStr = procDescList[rowNum-1];
		replaceStr = replaceStr.replace(/</g,"&lt");
		replaceStr = replaceStr.replace(/>/g,"&rt");
		replaceStr = replaceStr.replace(/nextLINE/g,"<br />");
		//decode.replace(/</g,"&lt");
		//decode.replace(/>/g,"&rt");
		$('#dialog').html( replaceStr );
		$( "#dialog" ).dialog("open");
	}
	jQuery(document).ready(function(){ 
		jQuery("#list1").jqGrid({
			url: 'LogAnalyticsAction',
		    datatype: 'json',
		    //datastr: 'list',
		    mtype: 'POST',
		    postData:{model:function(){ return $("#form01").serialize();}},
		    data:'rows',
		    colNames:['ID','logId','logTime','declno','vslRegNo','soNo','mawb','hawb'],
		    colModel :[ 
              {name:'id', index:'id', width:100},
		      {name:'logId', index:'logId', width:140},
		      {name:'logTime', index:'logTime', width:140},
		      {name:'declNo', index:'declNo', width:100},
		      {name:'vslRegNo', index:'vslRegNo', width:100},
		      {name:'soNo', index:'soNo', width:50},
		      {name:'mawb', index:'mawb', width:140},
		      {name:'hawb', index:'hawb', width:140}
		    ],
		    pager: '#pager',
		    rowNum:15,
		    //rowList:[10],
		    viewrecords: true,
		    caption: 'ResultGrid',
		    height:350,
		    toolbar:[false,"both"],
		    altRows:true,
		    ondblClickRow: function(id,rowNum){ opendialog(id,rowNum);}
		});
		$( "#dialog" ).dialog({   
	        autoOpen: false,   
	        height: 400,   
	        width: 1000,   
	        modal: true,   
	        buttons: {
	            Close: function() {
	                $( this ).dialog( "close" );
	            }
	        },
	        close: function() {
	        	$( this ).dialog( "close" );
	        }   
		});
		$("[name='startDate']").datetimepicker({
			hourText:'時',
			minuteText:'分',
			showSecond: true,
			timeFormat: 'hh:mm:ss',
			secondText:'秒',
			closeText:'儲存',
			timeText:'時間',
			currentText:'預設'
		});
		$("[name='endDate']").datetimepicker({
			hourText:'時',
			minuteText:'分',
			showSecond: true,
			timeFormat: 'hh:mm:ss',
			secondText:'秒',
			closeText:'儲存',
			timeText:'時間',
			currentText:'預設'
		});
		//$("#startDate,#endDate").datetimepicker().mask('9999/99/99 99:99:99');
		//$.datetimepicker.formatDate( "yyyy-mm-dd", new Date( 2007, 1 - 1, 26 ) );
	});
</script>
<title>LogAnalytics</title>
</head>
<body>
	<h1>LogAnalytics</h1>
	<input type="hidden" name="procDesc"/>
	
	<form id="form01">
	<input type="hidden" name="chgPage"/>
	<table >
	<tr>
	<td>報單號碼:</td><td><input type="text" name="declNo" value=""/></td>
	<td >查詢LOG名稱:</td><td colspan="6"><input type="text" name="qryLogName" size="110" value="EA90"/></td>
	<tr> 
	<td>海關通關號碼:</td><td><input type="text" name="vslRegNo" /></td>
	<td>裝貨單號碼:</td><td><input type="text" name="soNo" /></td>
	<td>主託運單號碼:</td><td><input type="text" name="mawb" /></td>
	<td>分託運單號碼:</td><td><input type="text" name="hawb" /></td>
	</tr>
	<tr >
	<td>起始日期:</td><td><input type="text" id="startDate" name="startDate"/></td>
	<td>結束日期:</td><td><input type="text" id="endDate" name="endDate"/></td>
	<td colspan="4"></td>
	</tr> 
	</table>
	</form>
	<button onclick="query();">查詢</button>
	<table id="list1"></table>
	<div id="pager"></div>
	<div id="dialog" title="procDesc" style="display:none;"></div>
	
	
</body>
</html>