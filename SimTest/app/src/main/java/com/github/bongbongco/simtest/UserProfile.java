package com.github.bongbongco.simtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        Intent intent = getIntent();
        String id = intent.getStringExtra(MainActivity.ID);
        String result = intent.getStringExtra(MainActivity.RESULT);
        TextView requesterTextView = (TextView) findViewById(R.id.requesterTextView);
        TextView idResponseTextView = (TextView) findViewById(R.id.idResponseTextView);
        TextView pwResponseTextView = (TextView) findViewById(R.id.pwResponseTextView);
        TextView imeiResponseTextView = (TextView) findViewById(R.id.imeiResponseTextView);
        TextView ssnResponseTextView = (TextView) findViewById(R.id.ssnResponseTextView);

        result = result.substring(4); // server response message error

        Toast.makeText(getApplicationContext(),"Before try",Toast.LENGTH_SHORT);
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = new JSONArray(jsonObject.getString("USER"));

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject insideObject = jsonArray.getJSONObject(i);
                requesterTextView.setText("Welcome " + id +  " " + insideObject.getString("RESULT"));
                idResponseTextView.setText("ID : "+ insideObject.getString("ID"));
                pwResponseTextView.setText("PW : " + insideObject.getString("PASSWORD"));
                imeiResponseTextView.setText("IMEI : " + insideObject.getString("IMEI"));
                ssnResponseTextView.setText("SSN : " + insideObject.getString("SSN"));
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

}
