<?php
	$con = mysqli_connect("smbiz", "neeeecoh_User", "root@123","neeeecoh_TrackerDB");
	
	$TrkID = $_POST["TrkID"];
	$Lat = $_POST["Lat"];
	$Lon = $_POST["Lon"];
	$VehiID = $_POST["VehiID"];
	$DateTime = $_POST["DateTime"];
	$VirbSts = $_POST["VirbSts"];
	$Time = $_POST["Time"];
	
	$statement = mysqli_prepare($con, "SELECT * FROM Data WHERE VehiID = 12");
	
	mysqli_stmt_execute($statement);
	
	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $TrkID, $Lat, $Lon, $VehiID, $DateTime, $VirbSts, $Time);
	
	$data = array();
	while(mysqli_stmt_fetch($statement)){
		$data[TrkID] = $TrkID;
		$data[Lat] = $Lat;
		$data[Lon] = $Lon;
		$data[VehiID] = $VehiID;
		$data[DateTime] = $DateTime;
		$data[VirbSts] = $VirbSts;
		$data[Time] = $Time;
	}
	echo json_encode($data);
	mysqli_stmt_close($statement);
	mysqli_close($con);
	?>