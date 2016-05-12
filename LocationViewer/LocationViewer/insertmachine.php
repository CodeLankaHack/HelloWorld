<?php

		$machinetype = $_GET['MachType'];
		$trackerid = $_GET['TrackID'];

		if($machinetype == '' || $trackerid == ''){
			echo 'please fill all values';
		}else{
			require_once('db_connect_mob.php');
			$sql = "SELECT * FROM Machines WHERE TrackID='$trackerid'";
			
			$check = mysqli_fetch_array(mysqli_query($con,$sql));
			
			if(isset($check)){
				echo 'Machine ID already exist';
			}else{				
				$sql = "INSERT INTO Machines (MachType,TrackID) VALUES('$machinetype','$trackerid')";
				if(mysqli_query($con,$sql)){
					echo 'Machine Registered Successfully';
				}else{
					echo 'oops! Please try again!';
				}
			}
			mysqli_close($con);
		}
	