package com.harsha.trackitmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Harsha on 05/12/16.
 */
public class AddMachineActivity extends Activity implements View.OnClickListener {
    EditText machinetype,trackerid;
    TextView machineid;
    Button insert;
    private static final String REGISTER_URL = "http://neeeecoholidays.com/Scripts/insertmachine.php";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_machine);
        machinetype= (EditText) findViewById(R.id.txtmachinetype);
        trackerid= (EditText) findViewById(R.id.txttrackerid);
        insert=(Button)findViewById(R.id.btnaddmachine);
        insert.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if(v == insert){
            registerMachine();
        }
    }
    private void registerMachine() {
        String machinetypetext = machinetype.getText().toString().trim().toLowerCase();
        String trackeridtext = trackerid.getText().toString().trim().toLowerCase();

        register(machinetypetext,trackeridtext);
    }
    private void register(String machineType, String TrackID) {
        String urlSuffix = "?MachType="+machineType+"&TrackID="+TrackID;
        class RegisterUser extends AsyncTask<String, Void, String> {

            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AddMachineActivity.this, "Please Wait",null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {
                String s = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(REGISTER_URL+s);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String result;

                    result = bufferedReader.readLine();

                    return result;
                }catch(Exception e){
                    return null;
                }
            }
        }

        RegisterUser ru = new RegisterUser();
        ru.execute(urlSuffix);
    }
}
