<form method="post" enctype="multipart/form-data">
    Select File to upload:
    <input type="file" name="fileToUpload" id="fileToUpload">
    <input type="submit" value="Upload File" name="submit">
</form>
<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
  $arrM=array();
// check for required fields
if (isset($_POST['submit'])) {
	
	$Lat;
	$Lon;
	$VehiID;
	$DateTime;
	$VirbSts;
	$time;
	 // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
 
 $target_dir = "uploads/";
$target_file = $target_dir . "sample.txt";
$uploadOk = 1;


    $check = move_uploaded_file($_FILES["fileToUpload"]["tmp_name"], $target_file);
    if($check !== false) {
        echo "File Upload Success";
        $uploadOk = 1;
    } else {
        echo "Faile";
        $uploadOk = 0;
    }

 
 
 
 
 
    // connecting to db
    $db = new DB_CONNECT();
 $f = fopen($target_file , "r");

	// Read line by line until end of file
	while (!feof($f)) { 

	// Make an array using comma as delimiter
	   $arrM = explode(" ",fgets($f)); 

	// Write links (get the data in the array)
	  $Lat=$arrM[0];
	  $Lon=$arrM[2];
	  $DateTime=$arrM[4];
	  $DateTime=date("Y-m-d", strtotime($DateTime) );
	  $time=$arrM[5];
	  $VirbSts=$arrM[7];
	  

	
    
 
   
 
    // mysql inserting a new row
if(sizeof($arrM)==8){
    $result = mysql_query("INSERT INTO Data(Lat,Lon,VehiID,DateTime,VirbSts,Time) VALUES('$Lat', '$Lon',12,'$DateTime','$VirbSts','$time')");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = $DateTime;
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
 
        // echoing JSON response
        echo json_encode($response);
    }
}
	}

	fclose($f);
}
?>