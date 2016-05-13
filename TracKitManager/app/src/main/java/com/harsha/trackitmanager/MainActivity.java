package com.harsha.trackitmanager;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity implements View.OnClickListener{
Button btnmanage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnmanage=(Button)findViewById(R.id.btnmanage);
        btnmanage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==btnmanage)
        {
            Intent i = new Intent(MainActivity.this,
                    ManMachineActivity.class);
            startActivity(i);
        }
    }
}
