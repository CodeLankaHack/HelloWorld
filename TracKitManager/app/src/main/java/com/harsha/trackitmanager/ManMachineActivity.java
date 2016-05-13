package com.harsha.trackitmanager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Harsha on 05/12/16.
 */
public class ManMachineActivity extends Activity implements View.OnClickListener {
    Button btnAddnewmach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.man_machine);
        btnAddnewmach = (Button) findViewById(R.id.btnaddmachine);
        btnAddnewmach.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==btnAddnewmach)
        {
            Intent i = new Intent(ManMachineActivity.this,
                    AddMachineActivity.class);
            startActivity(i);
        }
    }
}