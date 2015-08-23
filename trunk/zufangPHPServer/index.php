<?php
require("info_provider.php");

define ("CITY","city");
define ("KEYWORD","keyword");

define("LONG_DES", "longDes");
define("PRICE", "price");
define("SHORT_DES", "shortDes");
define("DETAIL_LINK", "detailLink");
define("PAST_TIME", "pastTime");

define("REQUEST_TYPE", "request_type");
define("REQUEST_TYPE_NEW", "new_request");
define("REQUEST_TYPE_UPDATE", "update_request");

define("OLD_MIN_PAST_TIME", "old_min_past_time");
define("SAVED_TIME", "saved_time");

if(array_key_exists(CITY,$_GET)){
	$city = $_GET[CITY];
}else{
	$warning = "请选择城市信息！";
	echo $warning;
	return;
}

if(array_key_exists(CITY,$_GET)){
	$keyword = $_GET[KEYWORD];
} else {
	$keyword  = NULL;
}

$info_list = array();
$info = array();

$provider = new InfoProvider();
$keyword_58 = iconv("gb2312","utf-8",$keyword);
$keyword_58 = urlencode($keyword_58);

$file = fopen($provider->get58Url($city, $keyword_58),"r");
if($file == FALSE){
	echo "服务器出现错误，请稍后再试！";
	return;
}
$count = 0;
while (!feof($file)) {
	$count ++;
	$line = fgets($file);

	$start = strpos($line, "<tr>");
	if($start !== FALSE){
		$info = array();
	}
	
	$start = strpos($line, "<tr><td class=\"t\"><a class=\"t\" target=\"_blank\" href=\"");
	if ($start !== FALSE) {
		$start = strpos($line, "<tr><td class=\"t\"><a class=\"t\" target=\"_blank\" href=\"") + strlen("<tr><td class=\"t\"><a class=\"t\" target=\"_blank\" href=\"");
		$end = strpos($line, "html\">");
		$info[DETAIL_LINK] = substr($line, $start, $end - $start)."html";
		$start2 = strpos($line, "html\">") + strlen("html\">");
		$end2 = strpos($line, "</a><span");
		$info[LONG_DES] = str_replace("\n","",str_replace("</b>", "", str_replace("<b>", "", substr($line, $start2, $end2 - $start2))));
	}

	$start = strpos($line, "<td class=\"tc\" ><b class='pri'>");
	
	if ($start !== FALSE) {
		$start = strpos($line, "<td class=\"tc\" ><b class='pri'>") + strlen("<td class=\"tc\" ><b class='pri'>");
		$end = strpos($line, "</b></td>");
		$info[PRICE] = substr($line, $start, $end - $start);
	}

	
	if(strpos($line,"卧") || strpos($line,"室")){
		$start = 0;
		$end = strpos($line, "</td>");
		$info[SHORT_DES] = substr($line, $start, $end - $start);
	}


	if(strpos($line,"小时") || strpos($line,"分钟") || strpos($line,"-")){
		$info[PAST_TIME] = $line;
	}
	
	if(strpos($line,"今天")){
		$info[PAST_TIME] = "今天";
	}
	
	$start = strpos($line, "</tr>");
	if ($start !== FALSE) {
		$info_list[] = $info;
	}
	
}

///////////////////////////////////////////Part II///////////////////////////////////////////////////
$keyword_ganji = iconv("gb2312","utf-8",$keyword);
$keyword_ganji = urlencode($keyword_ganji);

$file=fopen($provider->getGanjiUrl($city, $keyword_ganji), "r");

$curr_city = new City();
while (!feof($file)) {
	$count ++;
	$line = fgets($file);
	$start = strpos($line, "<a class=\"list_title\" href=\"");
	if ($start !== FALSE) {
		$start = $start + strlen("<a class=\"list_title\" href=\"");
		$end = strpos($line, "\" target=\"_blank\">");

		$info = array();
		
		$info[DETAIL_LINK] = "http://" . $curr_city->CITY_GANJI[$city] . ".ganji.com" . substr($line, $start, $end - $start);

		$start2 = strpos($line, "\" target=\"_blank\">", $end) + strlen("\" target=\"_blank\">");
		$end2 = strpos($line, "</a>");
		
		$info[LONG_DES] =  str_replace("\n","",str_replace("<span class='f_c_red'>", "", str_replace("</span>", "", substr($line, $start2, $end2 - $start2))));
	}

	$start = strpos($line, "<dd class=\"room\">");
	if ($start !== FALSE) {
		$start = $start + strlen("<dd class=\"room\">");
		$end = strpos($line, "</dd>");
		
		$short_des = substr($line, $start, $end - $start);
		if ($short_des == "") {
			$short_des = "无描述";
		}
		$info[SHORT_DES] = $short_des;
	}

	$start = strpos($line, "<dd><span class=\"price\">");
	if ($start !== FALSE) {
		$start = $start + strlen("<dd><span class=\"price\">");
		$end = strpos($line, "<font class=\"f_c_gray\">");

		if (strpos($line, "<font class=") !== FALSE) {
			$end = strpos($line, "<font class=");
		} else {
			$end = strpos($line, "</span><span");
        }
		
		$info[PRICE] = str_replace("", "", substr($line, $start, $end - $start));

		$start2 = strpos($line, "\"time\">") + strlen("\"time\">");
		$end2 = strpos($line, "</span>", $start2);
		$past_time = str_replace("</b>", "", str_replace("<b>", "", substr($line, $start2, $end2 - $start2)));
		
		$info[PAST_TIME] = $past_time;
		$info_list[] = $info;
	}
	
}


function changeTimeToMillis($time) {
	if (strpos($time, "分钟") !== FALSE) {
		return substr($time, 0, strpos($time, "分钟")) * 60 * 1000;
	} else if (strpos($time, "小时") !== FALSE) {
		return substr($time, 0, strpos($time, "小时")) * 60 * 60 * 1000;
	} else if (strpos($time, "今天") !== FALSE) {
		return  PHP_INT_MAX -1;
	}
	
	return PHP_INT_MAX;
}


if(array_key_exists(OLD_MIN_PAST_TIME,$_GET) && array_key_exists(REQUEST_TYPE,$_GET) && array_key_exists(SAVED_TIME,$_GET) ){
	$OLD_MIN_PAST_TIME = $_GET[OLD_MIN_PAST_TIME];
	error_log("old_min_past_time: ".$OLD_MIN_PAST_TIME);
	$tempArray = array();
	if($_GET[REQUEST_TYPE] == REQUEST_TYPE_UPDATE) {
		
		date_default_timezone_set("Asia/Shanghai");
		foreach($info_list as $item){
			error_log("pasttime:".$item[PAST_TIME]);
			$cur_time = date('U')*1000;
			if( changeTimeToMillis($item[PAST_TIME]) < (changeTimeToMillis($OLD_MIN_PAST_TIME) +  $cur_time - $_GET[SAVED_TIME])){
			
				$tempArray[] = $item;
			}
		}
		$info_list = $tempArray;
	} 
}

function sortByTime($a,$b){
	$c = changeTimeToMillis($a[PAST_TIME] );
	$d = changeTimeToMillis($b[PAST_TIME] );
	if($c > $d){
		return 1;
	}
	
	if($c== $d){
		return 0;
	}
	
	if($c< $d){
		return -1;
	}	
}

usort($info_list,"sortByTime");

$temp = "";
$temp .= "[";
$sum = 0;
$arrayCount = count($info_list);
foreach($info_list as $item){ 
	$sum ++;
	$temp .= "{";
	$temp .= "\"longDes\":\"" . $item[LONG_DES] . "\", ";
	$temp .= "\"price\":\"" . $item[PRICE] . "\", ";
	$temp .= "\"shortDes\":\"" . $item[SHORT_DES] . "\", ";
	$temp .= "\"detailLink\":\"" . $item[DETAIL_LINK] . "\", ";
	$temp .= "\"pastTime\":\"" . $item[PAST_TIME] . "\"";
	if ($sum == $arrayCount) {
		$temp .= "}";
	} else {
		$temp .= "},";
	}
}

$temp .= "]";

echo $temp;

?>