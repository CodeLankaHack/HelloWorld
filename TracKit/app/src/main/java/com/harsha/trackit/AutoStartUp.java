package com.harsha.trackit;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class AutoStartUp extends Service {
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
    Config con = new Config();
    String UsrKy = "";
    double previousLat = 0.00;
    double previousLong = 0.00;
    double previousSpeed = 0.00;
    Location location;
    long time, synctime, timetosync, timetocleartemplocarray, cleartemploc;
    SQLiteDatabase db;
    protected LocationManager locationManager;
    ArrayList<String> PreviousLatArr = new ArrayList<String>();
    ArrayList<String> PreviousLongArr = new ArrayList<String>();
    ArrayList<String> FromTrackingTimeArr = new ArrayList<String>();
    ArrayList<String> ToTrackingTimeArr = new ArrayList<String>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        LogErrors("TracKit Started");
        StartUp();
    }

    public void StartUp() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATES,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                new MyLocationListener()
        );
        showCurrentLocation();


    }

    protected void showCurrentLocation() {
        location = null;
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    //
//    private double getAltitude(double longitude, double latitude) {
//        double result = Double.NaN;
//        HttpClient httpClient = new DefaultHttpClient();
//        HttpContext localContext = new BasicHttpContext();
//        String url = "http://maps.googleapis.com/maps/api/elevation/"
//                + "xml?locations=" + String.valueOf(latitude)
//                + "," + String.valueOf(longitude)
//                + "&sensor=true";
//        HttpGet httpGet = new HttpGet(url);
//        try {
//            HttpResponse response = httpClient.execute(httpGet, localContext);
//            HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                InputStream instream = entity.getContent();
//                int r = -1;
//                StringBuffer respStr = new StringBuffer();
//                while ((r = instream.read()) != -1)
//                    respStr.append((char) r);
//                String tagOpen = "<elevation>";
//                String tagClose = "</elevation>";
//                if (respStr.indexOf(tagOpen) != -1) {
//                    int start = respStr.indexOf(tagOpen) + tagOpen.length();
//                    int end = respStr.indexOf(tagClose);
//                    String value = respStr.substring(start, end);
//                    result = (double)(Double.parseDouble(value)); //*3.2808399// convert from meters to feet
//                }
//                instream.close();
//            }
//        } catch (ClientProtocolException e) {}
//        catch (IOException e) {}
//
//        return result;
//    }
    private Connection ConnectionHelper(String user, String password,
                                        String database, String server) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + server + ";"
                    + "databaseName=" + database + ";user=" + user
                    + ";password=" + password + ";";
            connection = DriverManager.getConnection(ConnectionURL);
        } catch (SQLException se) {
            LogErrors(se.getMessage());
        } catch (ClassNotFoundException e) {
            LogErrors(e.getMessage());
        } catch (Exception e) {
            LogErrors(e.getMessage());
        }
        if (connection == null) {
            LogErrors("Cannot connect to the server");
            return connection;
        } else {
            return connection;
        }
    }

//    public void showMessage(String title, String message) {
//        Builder builder = new Builder(this);
//        builder.setCancelable(true);
//        builder.setTitle(title);
//        builder.setMessage(message);
//        builder.show();
//    }

    private void SyncWithServer(double latitude, double longitude, double speed, double altitude, String UsrKy) {
        db = openOrCreateDatabase("GeoLocations", Context.MODE_PRIVATE, null);
        CreateTables();
        double distance = calcDistance(previousLat, latitude, previousLong, longitude);
        if (distance > 5) {
            if (previousLat != latitude || previousLong != longitude) {
                if (previousSpeed <= 0.6 && speed <= 0.6) {
                } else {
                    if (PreviousLatArr.contains(String.valueOf(latitude)) || PreviousLongArr.contains(String.valueOf(longitude))) {
                    } else {
                        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                        long currenttimeinsec = System.currentTimeMillis() / 1000;
                        long secdiff = currenttimeinsec - time;
                        timetocleartemplocarray = currenttimeinsec - cleartemploc;
                        timetosync = currenttimeinsec - synctime;
                        if (secdiff >= 10) {

                            if (PreviousLongArr.contains(String.valueOf(longitude)) || PreviousLatArr.contains(String.valueOf(latitude))) {
                            } else {
                                PreviousLatArr.add(String.valueOf(latitude));
                                PreviousLongArr.add(String.valueOf(longitude));
                                db.execSQL("INSERT INTO GeoLocationsToBeSynced (DateTime,Latitude,Longitude,Speed,Altitude,Area,UsrKy)VALUES('" + currentDateTimeString + "','" + latitude + "','" + longitude + "','" + speed + "','" + altitude + "','" + "Automated" + "','" + UsrKy + "')");
                                if (PreviousLongArr.size() >= 5 || PreviousLatArr.size() >= 5 || timetocleartemplocarray >= 7200) {
                                    PreviousLongArr.clear();
                                    PreviousLatArr.clear();
                                }
                                previousLat = latitude;
                                previousLong = longitude;
                                previousSpeed = speed;
                                time = System.currentTimeMillis() / 1000;
                                location = null;
                            }
                        }
                    }
                }
            }
        }
        if (timetosync >= 900) {
            String username = con.getuName();
            String pswd = con.getpwd();
            String database = con.getdb();
            String ipaddress = con.getip();
            Connection connect;
            long numRows = DatabaseUtils.queryNumEntries(db, "GeoLocationsToBeSynced");
            for (int i = 0; i < numRows; i++) {
                try {
                    Cursor inc = db.rawQuery("SELECT * FROM GeoLocationsToBeSynced", null);
                    if (inc.moveToFirst()) {
                        if (isNetworkAvailable()) {
                            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                            connect = ConnectionHelper(username, pswd, database,
                                    ipaddress);
                            CallableStatement proc =
                                    connect.prepareCall("{ call GeoLocations_InsertMob(?,?,?,?,?,?,?) }");
                            proc.setString("LogDt", inc.getString(0));
                            proc.setString("Latitude", inc.getString(1));
                            proc.setString("Longitude", inc.getString(2));
                            proc.setString("Speed", inc.getString(3));
                            if (altitude == Double.NaN) {
                                proc.setString("Altitude", "NaN");
                            } else {
                                proc.setString("Altitude", inc.getString(4));
                            }
                            proc.setString("Area", inc.getString(5));
                            proc.setString("UsrKy", inc.getString(6));
                            proc.execute();
                            db.execSQL("DELETE FROM GeoLocationsToBeSynced WHERE DateTime='" + inc.getString(0) + "'");
                        } else {
                            LogErrors("Unable to Connect");
                        }
                    }
                } catch (Exception e) {
                    LogErrors(String.valueOf(e.getMessage()));
                }
            }
            synctime = System.currentTimeMillis() / 1000;
            db.execSQL("DELETE FROM GeoLocationsToBeSynced");
            cleartemploc = System.currentTimeMillis() / 1000;
        }
    }

    private void SyncErrors() {
        if (isNetworkAvailable()) {
            String username = con.getuName();
            String pswd = con.getpwd();
            String database = con.getdb();
            String ipaddress = con.getip();
            Connection connect;
            long numRows = DatabaseUtils.queryNumEntries(db, "GeoLocAppErrors");
            for (int i = 0; i < numRows; i++) {
                try {
                    Cursor inc = db.rawQuery("SELECT * FROM GeoLocAppErrors", null);
                    if (inc.moveToFirst()) {
                        Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                        connect = ConnectionHelper(username, pswd, database,
                                ipaddress);
                        CallableStatement proc =
                                connect.prepareCall("{ call GeoLocationErrors_InsertMob(?,?,?) }");
                        proc.setString("Err", inc.getString(2));
                        proc.setString("DateTime", inc.getString(1));
                        proc.setString("UsrKy", inc.getString(3));
                        proc.execute();
                        db.execSQL("DELETE FROM GeoLocAppErrors WHERE DateTime='" + inc.getString(1) + "'");
                    }
                } catch (Exception e) {
                    LogErrors(String.valueOf(e.getMessage()));
                }
            }
        } else {
            LogErrors("Unable to Sync Errors");
        }
    }

    private double calcDistance(double previousLat, double latitude, double previousLong, double longitude) {
        int Radius = 6371;// radius of earth in Km
        double dLat = Math.toRadians(latitude - previousLat);
        double dLon = Math.toRadians(longitude - previousLong);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(previousLat))
                * Math.cos(Math.toRadians(latitude)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double meter = valueResult % 1000;
        return meter * 100;
    }

    private String UsrKy() {
        db = openOrCreateDatabase("GeoLocations", Context.MODE_PRIVATE, null);
        CreateTables();
        Cursor inc = db.rawQuery("SELECT UsrKy FROM UsrTable WHERE id=1", null);
        if (inc.moveToFirst()) {
            UsrKy = inc.getString(0);
            //Toast.makeText(AutoStartUp.this, "UsrKy From DB", Toast.LENGTH_LONG).show();
        } else {
            UsrKy = getUserKy();
            if(UsrKy!=null) {
                // Toast.makeText(AutoStartUp.this, "UsrKy From Server", Toast.LENGTH_LONG).show();
                db = openOrCreateDatabase("GeoLocations", Context.MODE_PRIVATE, null);
                CreateTables();
                db.execSQL("INSERT INTO UsrTable VALUES('" + 1 + "','" + "1" + "')");
                db.execSQL("UPDATE UsrTable SET UsrKy = ('" + UsrKy + "') WHERE id=1");
            }
        }
        return UsrKy;
    }

    public class MyLocationListener implements LocationListener {
        Date date=new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String curdate = "";
        ArrayList<String> FromDateTime = new ArrayList<String>();
        ArrayList<String> ToDateTime = new ArrayList<String>();

        public void onLocationChanged(final Location location) {
            db = openOrCreateDatabase("GeoLocations", Context.MODE_PRIVATE, null);
            CreateTables();
            if (location == null) {
                LogErrors("Unable to get location");
            } else {
                final String UsrKy = UsrKy();
                if (UsrKy != null) {
                    final int threadDelay = con.getdelay();
                    final Handler handler = new Handler();
                    final Runnable r = new Runnable() {
                        public void run() {
                            Date prevTime=null;
                            Date curTime=null;
                            Date date1=null;
                            long numRows = DatabaseUtils.queryNumEntries(db, "GeoLocTimeUpdate");
                            if (numRows <= 0) {
                                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                                Date date = new Date();
                                String curT=dateFormat.format(date);
                                db.execSQL("INSERT INTO GeoLocTimeUpdate VALUES ('" + "1" + "','" + curT + "')");
                                CheckTimeDuration();
                            }

                            Cursor incr = db.rawQuery("SELECT LastInsTrackTime FROM GeoLocTimeUpdate WHERE id=1", null);
                            if (incr.moveToFirst()) {
                                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                                Date date = new Date();
                                String curT=dateFormat.format(date);
                                try {
                                     curTime=format.parse(curT);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                try {
                                     date1 = format.parse(incr.getString(0));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                long diff=curTime.getTime()-date1.getTime();
                                if(diff>7.2e+6)
                                {
                                    db.execSQL("DELETE FROM GeoLocTimeUpdate");
                                }
                            }
                            long NumrowsToTrack = DatabaseUtils.queryNumEntries(db, "GeoLocTrackingTime");
                            if (NumrowsToTrack == 0) {
                                showCurrentLocation();
                                SyncWithServer(location.getLatitude(), location.getLongitude(), location.getSpeed(), 0.00, UsrKy);
                            } else {
                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                                try {
                                    Cursor inc = db.rawQuery("SELECT FromTrackingTime FROM GeoLocTrackingTime", null);
                                    FromDateTime.clear();
                                    for (inc.moveToFirst(); !inc.isAfterLast(); inc.moveToNext()) {
                                        FromDateTime.add(inc.getString(0));
                                    }
                                    Cursor incc = db.rawQuery("SELECT ToTrackingTime FROM GeoLocTrackingTime", null);
                                    ToDateTime.clear();
                                    for (incc.moveToFirst(); !incc.isAfterLast(); incc.moveToNext()) {
                                        ToDateTime.add(incc.getString(0));
                                    }
                                    Date dNow = new Date();
                                    SimpleDateFormat ft =
                                            new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                                    curdate = ft.format(dNow);
                                } catch (Exception e) {
                                    LogErrors(e.getMessage());
                                }

                                try {
                                    DateFormat sdf = new SimpleDateFormat(
                                            "yyyy-MM-dd kk:mm:ss");
                                    for (int d = 0; d < FromDateTime.size() && d < ToDateTime.size(); d++) {
                                        String firstStr = curdate;
                                        String secondStr = ToDateTime.get(d);
                                        Date first = sdf.parse(firstStr);
                                        Date second = sdf.parse(secondStr);
                                        boolean after = (first.before(second));
                                        String firstStr1 = FromDateTime.get(d);
                                        String secondStr1 = curdate;
                                        Date first1 = sdf.parse(firstStr1);
                                        Date second1 = sdf.parse(secondStr1);
                                        boolean before = (first1.before(second1));

                                        if (before && after == true) {
                                            showCurrentLocation();
                                            SyncWithServer(location.getLatitude(), location.getLongitude(), location.getSpeed(), 0.00, UsrKy);
                                        }
                                    }
                                } catch (Exception e) {
                                    LogErrors(e.getMessage());
                                }
                            }
                            long numofRows = DatabaseUtils.queryNumEntries(db, "GeoLocAppErrors");
                            if (numofRows > 0) {
                                SyncErrors();
                            }
                            handler.postDelayed(this, threadDelay);
                        }
                    };
                    handler.postDelayed(r, threadDelay);
                } else {
                    Toast.makeText(AutoStartUp.this, "ERROR/Invalid User", Toast.LENGTH_LONG).show();
                }

            }

        }

        public void CheckTimeDuration() {
            db = openOrCreateDatabase("GeoLocations", Context.MODE_PRIVATE, null);
            CreateTables();
            long currentDateTimeString = System.currentTimeMillis();
            if (isNetworkAvailable()) {
                try {
                    String username = con.getuName();
                    String pswd = con.getpwd();
                    String database = con.getdb();
                    String ipaddress = con.getip();
                    Statement st;
                    Connection connect;
                    connect = ConnectionHelper(username, pswd, database,
                            ipaddress);
                    st = connect.createStatement();
                    final String UsrKy = UsrKy();
                    ResultSet rs = st.executeQuery("SELECT * FROM GetTrackingTime_SelectMob('" + UsrKy + "')");
                    boolean hasRows = false;
                    while (rs != null && rs.next()) {
                        FromTrackingTimeArr.add(rs.getString(1));
                        ToTrackingTimeArr.add(rs.getString(2));
                        hasRows = true;
                    }
                    if (hasRows == false) {
                        FromTrackingTimeArr.add("SYNC");
                        ToTrackingTimeArr.add("SYNC");
                    }
                    if (FromTrackingTimeArr.size() == ToTrackingTimeArr.size()) {
                        if (!FromTrackingTimeArr.contains(String.valueOf("SYNC"))) {
                            db = openOrCreateDatabase("GeoLocations", Context.MODE_PRIVATE, null);
                            db.execSQL("DELETE FROM GeoLocTrackingTime");
                            for (int i = 0; i < FromTrackingTimeArr.size(); i++) {
                                db.execSQL("INSERT INTO GeoLocTrackingTime (FromTrackingTime,ToTrackingTime,StrtTime,UsrKy)VALUES('" + FromTrackingTimeArr.get(i) + "','" + ToTrackingTimeArr.get(i) + "','" + currentDateTimeString + "','" + UsrKy + "')");
                            }
                        } else {
                            //db.execSQL("INSERT INTO GeoLocTrackingTime (FromTrackingTime,ToTrackingTime,StrtTime,UsrKy)VALUES('" + FromTrackingTimeArr.get(0) + "','" + ToTrackingTimeArr.get(0) + "','" + currentDateTimeString + "','" + UsrKy + "')");
                            //showCurrentLocation();
                            //SyncWithServer(location.getLatitude(), location.getLongitude(), location.getSpeed(), 0.00, UsrKy);
                        }
                    } else {
                        FromTrackingTimeArr.clear();
                        ToTrackingTimeArr.clear();
                        db.execSQL("DELETE FROM GeoLocTrackingTime");
                    }
                } catch (Exception e) {
                    LogErrors(e.getMessage());
                }
            } else {
                LogErrors("Unable to get Tracking Updates");
            }
            FromTrackingTimeArr.clear();
            ToTrackingTimeArr.clear();
        }

        public void onStatusChanged(String s, int i, Bundle b) {
        }

        public void onProviderDisabled(String s) {
            LogErrors("GPS turned OFF");
            Toast.makeText(AutoStartUp.this,
                    "GPS turned OFF ",
                    Toast.LENGTH_LONG).show();
        }

        public void onProviderEnabled(String s) {
            LogErrors("GPS turned ON");
            Toast.makeText(AutoStartUp.this,
                    "GPS turned ON",
                    Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onDestroy() {
        LogErrors("TracKit Stopped");
    }

    private String getUserKy() {
        String name = null;
        if (isNetworkAvailable()) {
            try {
                String username = con.getuName();
                String pswd = con.getpwd();
                String database = con.getdb();
                String ipaddress = con.getip();
                Statement st;
                Connection connect;
                connect = ConnectionHelper(username, pswd, database,
                        ipaddress);
                st = connect.createStatement();
                String imei = getIMEI();
                ResultSet rs = st.executeQuery("Select dbo.GetUsrKy_SelectMob('" + imei + "')");
                if (rs != null && rs.next()) {
                    name = rs.getString(1);
                    if (name != null) {
                        return name;
                    } else {
                        Toast.makeText(AutoStartUp.this, "ERROR/Invalid User", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(AutoStartUp.this, "ERROR/Invalid User", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(AutoStartUp.this, "ERROR/Invalid User", Toast.LENGTH_LONG).show();
        }
        return name;

    }
public void CreateTables()
{
    db = openOrCreateDatabase("GeoLocations", Context.MODE_PRIVATE, null);
    db.execSQL("CREATE TABLE IF NOT EXISTS UsrTable(id INTEGER,UsrKy VARCHAR);");
    db.execSQL("CREATE TABLE IF NOT EXISTS GeoLocationsToBeSynced(DateTime VARCHAR,Latitude VARCHAR,Longitude VARCHAR,Speed VARCHAR,Altitude VARCHAR,Area VARCHAR,UsrKy VARCHAR);");
    db.execSQL("CREATE TABLE IF NOT EXISTS GeoLocAppErrors(ErrorID INTEGER PRIMARY KEY ,DateTime VARCHAR,Error VARCHAR,UsrKy VARCHAR)");
    db.execSQL("CREATE TABLE IF NOT EXISTS GeoLocTrackingTime(TrkTimID INTEGER PRIMARY KEY ,FromTrackingTime VARCHAR,ToTrackingTime VARCHAR,StrtTime VARCHAR,UsrKy VARCHAR)");
    db.execSQL("CREATE TABLE IF NOT EXISTS GeoLocTimeUpdate(id INTEGER,LastInsTrackTime VARCHAR)");
}
    public String getIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void LogErrors(final String exception) {
        db = openOrCreateDatabase("GeoLocations", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS UsrTable(id INTEGER,UsrKy VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS GeoLocationsToBeSynced(DateTime VARCHAR,Latitude VARCHAR,Longitude VARCHAR,Speed VARCHAR,Altitude VARCHAR,Area VARCHAR,UsrKy VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS GeoLocAppErrors(ErrorID INTEGER PRIMARY KEY ,DateTime VARCHAR,Error VARCHAR,UsrKy VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS GeoLocTrackingTime(TrkTimID INTEGER PRIMARY KEY ,FromTrackingTime VARCHAR,ToTrackingTime VARCHAR,StrtTime VARCHAR,UsrKy VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS GeoLocTimeUpdate(id INTEGER,LastInsTrackTime VARCHAR)");
        long numRows = DatabaseUtils.queryNumEntries(db, "GeoLocAppErrors");
        if (numRows > 5) {
            db.execSQL("DELETE FROM GeoLocAppErrors");
        }
        db = openOrCreateDatabase("GeoLocations", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS GeoLocAppErrors(ErrorID INTEGER PRIMARY KEY,DateTime VARCHAR,Error VARCHAR,UsrKy VARCHAR)");
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        String UserKy=UsrKy();
        if(UserKy!=null) {
            db.execSQL("INSERT INTO GeoLocAppErrors (DateTime,Error,UsrKy)VALUES('" + currentDateTimeString + "','" + exception + "','" + UsrKy() + "')");
        }
        if (exception.contains("ON") || exception.contains("OFF")) {
        } else {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                LogErrors(e.getMessage());
            }
            if(!exception.contains("TracKit Started"))
            {
                Toast.makeText(AutoStartUp.this, "Error", Toast.LENGTH_LONG).show();
            }

        }
    }
}