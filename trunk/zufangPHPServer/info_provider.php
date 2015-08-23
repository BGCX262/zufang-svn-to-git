<?php

define("BASE_URL_58", ".58.com/hezu/0/?final=1&searchtype=3&sourcetype=5");
define("BASE_URL_GANJI", ".ganji.com/fang3/");

require("city.php");

class InfoProvider {

	public function get58Url($city, $key) {
		$curr_city = new City();
		if (strlen($key) == 0) {
			return "http://" . $curr_city->CITY_58[$city] . BASE_URL_58;
		} else {
			return "http://" . $curr_city->CITY_58[$city] . BASE_URL_58 . "&key=" . $key;
		}
	}

	public function getGanjiUrl($city, $key) {
		$curr_city = new City();
		
		if (strlen($key) == 0) {
			return "http://" . $curr_city->CITY_GANJI[$city] . BASE_URL_GANJI;
		} else {
			return "http://" . $curr_city->CITY_GANJI[$city] . BASE_URL_GANJI . "_" . $key . "/";
		}
	}
}

?>