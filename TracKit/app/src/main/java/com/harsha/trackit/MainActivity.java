package com.harsha.trackit;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    private TextView errors;
    SQLiteDatabase db;
    Button clearData,btnrestartservice,btnstopservice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(MainActivity.this, AutoStartUp.class));
        setContentView(R.layout.activity_main);
        errors=(TextView)findViewById(R.id.txterrors);
        clearData  = (Button) findViewById(R.id.btncleerrors);
        btnrestartservice  = (Button) findViewById(R.id.btnrestartservice);
        btnstopservice  = (Button) findViewById(R.id.btnstopservice);
        db=openOrCreateDatabase("GeoLocations", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS UsrTable(id INTEGER,UsrKy VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS GeoLocationsToBeSynced(DateTime VARCHAR,Latitude VARCHAR,Longitude VARCHAR,Speed VARCHAR,Altitude VARCHAR,Area VARCHAR,UsrKy VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS GeoLocAppErrors(ErrorID INTEGER PRIMARY KEY,DateTime VARCHAR,Error VARCHAR,UsrKy VARCHAR)");
        //Check_Errors();

//        clearData.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                db.execSQL("DELETE FROM ErrorLog"); //delete all rows in a table
//                Toast.makeText(MainActivity.this, "Records Deleted Successfully - HTNMaps",
//                        Toast.LENGTH_LONG).show();
//            }
//        });
//        btnrestartservice.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                stopService(new Intent(MainActivity.this, AutoStartUp.class));
//                startService(new Intent(MainActivity.this, AutoStartUp.class));
//                Toast.makeText(MainActivity.this, "Service Restarted - HTNMaps",
//                        Toast.LENGTH_LONG).show();
//            }
//        });
//        btnstopservice.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                stopService(new Intent(MainActivity.this, AutoStartUp.class));
//                Toast.makeText(MainActivity.this, "Service Stopped - HTNMaps",
//                        Toast.LENGTH_LONG).show();
//            }
//        });
       System.exit(0);
    }

//    private void Check_Errors()
//    {
//        Cursor inc=db.rawQuery("SELECT Error FROM GeoTable WHERE id=1", null);
//        if(inc.moveToFirst())
//        {
//            String previousval=errors.getText().toString();
//            if(previousval=="No Errors")
//            {
//                previousval="";
//            }
//            errors.setText(previousval+"  "+inc.getString(0));
//        }
//
//    }

}
