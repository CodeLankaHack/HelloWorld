<?php
	define('HOST','***');
	define('USER','***');
	define('PASS','root@***');
	define('DB','***');
	
	$con = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect');
