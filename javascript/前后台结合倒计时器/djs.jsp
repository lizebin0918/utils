<%@ page language="java" import="java.util.*" pageEncoding="GBK"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.util.Calendar"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%
	SimpleDateFormat dfs = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
	java.util.Date begin=new Date();
	java.util.Date end = null;
	if(session.getAttribute("endTime") == null){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(begin);
		calendar.add(Calendar.MINUTE, 6);//当前时间加上分钟，这个变量应设置在application中
		end = calendar.getTime();
		session.setAttribute("endTime", end);
	}else{
		end = (java.util.Date)session.getAttribute("end");
	}
 	long l=end.getTime()-begin.getTime();
	if(l < 0) {
		l = 0;
	}
	long day=l/(24*60*60*1000);
	long hour=(l/(60*60*1000));
	long hour2=(l/(60*60*1000)-day*24);
	long min=((l/(60*1000))-day*24*60-hour2*60);
	long s=(l/1000-day*24*60*60-hour2*60*60-min*60);
	String interval=(hour<10?"0" + hour : "" + hour) + ":" + (min < 10 ? "0"+min: "" + min) + ":" + (s < 10 ? "0" + s:"" + s);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>My JSP 'djs.jsp' starting page</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
</head>
<body>
	剩余时间:<span id="clock"><%=interval%></span>
</body>
<script type="text/javascript">
  	var clock = document.getElementById("clock");
	var counter, startTime, normalelapse = 1000, nextelapse = normalelapse, start = clock.innerHTML, timer = null;
	var alert_time = "00:05:00", finish = "00:00:00";
	function run() {
		counter = 0;
		startTime = new Date().valueOf(); // 初始化开始时间
		// nextelapse 是定时时间,初始时为1000 毫秒
		// 注意setInterval 函数: 时间逝去nextelapse(毫秒)后,onTimer 才开始执行
		timer = window.setInterval("onTimer()", nextelapse);
	}
	function stop() {
		window.clearTimeout(timer);
	} // 停止运行
	window.onload = function() {
	};
	function onTimer() { // 倒计时函数
		if (start == alert_time) {
			alert("还有5 分钟结束,请做好结束准备！");
		} //剩余5 分钟时,提示警告信息
		if (start == finish) { //倒计时结束
			window.clearInterval(timer);
			alert("倒计时结束!");
			return;
		};
		var hms = new String(start).split(":");
		var s = new Number(hms[2]);
		var m = new Number(hms[1]);
		var h = new Number(hms[0]);
		s -= 1;
		if (s < 0) {
			s = 59;
			m -= 1;
		}
		if (m < 0) {
			m = 59;
			h -= 1;
		}
		var ss = s < 10 ? ("0" + s) : s;
		var sm = m < 10 ? ("0" + m) : m;
		var sh = h < 10 ? ("0" + h) : h;
		start = sh + ":" + sm + ":" + ss; //显示倒计时
		clock.innerHTML = start;
		window.clearInterval(timer); // 清除上一次的定时器
		// 自校验系统时间得到时间差,并由此得到下次所启动的新定时器的间隔时间nextelapse
		counter++;
		var counterSecs = counter * 1000;
		var elapseSecs = new Date().valueOf() - startTime; //已经经过的秒数
		var diffSecs = counterSecs - elapseSecs;
		//如果误差在1 秒之内,则可以接受
		//否则认为客户端用户调整了系统时间,不再进行定时器误差调整,仍使用默认值1000ms
		if (Math.abs(diffSecs) < 1000) {
			nextelapse = normalelapse + diffSecs;
		} else {
			nextelapse = normalelapse;
		}
		// 启动新的定时器
		timer = window.setInterval("onTimer()", nextelapse);
	}
	run();
</script>
</html>
