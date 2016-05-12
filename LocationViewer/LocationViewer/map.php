<?php
require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();

?>

    <html>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
        <title>Google Maps</title>

        <link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
  <script src="//code.jquery.com/jquery-1.10.2.js"></script>
  <script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
  <link rel="stylesheet" href="/resources/demos/style.css">
  <script>
  $(function() {
    $( ".datepicker" ).datepicker();
  });


  </script>
     
        <style type="text/css">
            body { font: normal 10pt Helvetica, Arial; }
            #map { width: 100%; height: 100%; border: 0px; padding: 0px; margin-left:10px; margin-right:50px;}
        </style>
        <script src="http://maps.google.com/maps/api/js?key=AIzaSyBx0uUcq3ErI1xwAwPeQi4Cl14m9PYAzUg&sensor=false" type="text/javascript"></script>
        <script type="text/javascript">

            var icon = new google.maps.MarkerImage("http://maps.google.com/mapfiles/ms/micons/blue.png",
                       new google.maps.Size(32, 32), new google.maps.Point(0, 0),
                       new google.maps.Point(16, 32));
            var center = null;
            var map = null;

            var currentPopup;
            var bounds = new google.maps.LatLngBounds();
            function addMarker(lat, lng, info) {
                var pt = new google.maps.LatLng(lat, lng);
                bounds.extend(pt);
                var marker = new google.maps.Marker({
                    position: pt,
                    icon: icon,
                    map: map
                });
                var popup = new google.maps.InfoWindow({
                    content: info,
                    maxWidth: 300
                });
                google.maps.event.addListener(marker, "click", function() {
                    if (currentPopup != null) {
                        currentPopup.close();
                        currentPopup = null;
                    }
                    popup.open(map, marker);
                    currentPopup = popup;
                });
                google.maps.event.addListener(popup, "closeclick", function() {
                    map.panTo(center);
                    currentPopup = null;
                });
            }           
            function initMap() {
                map = new google.maps.Map(document.getElementById("map"), {
                    center: new google.maps.LatLng(0,0),
                    zoom: 14,
                    mapTypeId: google.maps.MapTypeId.SATELLITE,
                    mapTypeControl: true,
                    mapTypeControlOptions: {
                        style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR
                    },
                    navigationControl: true,
                    navigationControlOptions: {
                        style: google.maps.NavigationControlStyle.ZOOM_PAN
                    }
                });

<?php

if(isset($_POST['fromdatepicker'])&&isset($_POST['todatepicker']))
{
$fdate=date("Y-m-d", strtotime($_POST['fromdatepicker']) );
$tdate=date("Y-m-d", strtotime($_POST['todatepicker']) );
$vid=$_POST['vid'];
$query = mysql_query("SELECT * FROM Data WHERE DATETIME BETWEEN '$fdate' AND '$tdate' And VehiID='$vid'")or die(mysql_error());
while($row = mysql_fetch_array($query))
{
  $name = $row['VehiID'];
  $lat = $row['Lat'];
  $lon = $row['Lon'];
  $desc = $row['DateTime'];



  echo("addMarker($lat, $lon, '<b>$name</b><br />$desc');\n");

}
}
?>
 center = bounds.getCenter();
     map.fitBounds(bounds);

     }
     </script>
     </head>
     <body onload="initMap()" style="margin:0px; border:0px; padding:0px;">
	 <form method="POST">
        <select name="vid">
		<?php                 
		$query1 = mysql_query("SELECT distinct VehiID FROM Data")or die(mysql_error());
		while($row = mysql_fetch_array($query1))
		{ ?>
		<option><?php echo $row['VehiID']; ?></option>
		<?php } ?>
	</select>
	<lable>From : </lable><input type="text" id="fromdatepicker" name="fromdatepicker" class="datepicker" width="100px"/>
	<lable>To : </lable><input type="text" id="todatepicker" name="todatepicker" class="datepicker" width="100px"/>
        <input type="submit"/>
        <center><div id="map"></div></center>
     
	</form>


     </body>
     </html>