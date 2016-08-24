package com.github.bongbongco.simtest;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText idEditText;
    private EditText pwEditText;

    public static final String ID = "USERNAME";
    public static final String RESULT = "RESULT";

    String id, pw, imei, ssn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TelephonyManager telephoneInfo = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        imei = telephoneInfo.getDeviceId();
        ssn = telephoneInfo.getSimSerialNumber();

        setTextView(R.id.imeiTextView, telephoneInfo.getDeviceId());
        setTextView(R.id.gsmTextView, imei);
        setTextView(R.id.SsnTextView, ssn);

        idEditText = (EditText) findViewById(R.id.idEditText);
        pwEditText = (EditText) findViewById(R.id.pwEditText);

        RootingUser rootingJudge = new RootingUser();

        if(rootingJudge.Access()){
            finish();
        }
        WifiManagerThread wifiManagerThread = new WifiManagerThread();
        wifiManagerThread.setDaemon(true);
        wifiManagerThread.start();
    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if(disconnectWifi()){
                Toast.makeText(MainActivity.this, "WIFI를 사용하실 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    class WifiManagerThread extends Thread{
        @Override
        public void run() {
            while (true) {
                try {
                    handler.sendMessage(handler.obtainMessage());
                    Thread.sleep(1000);
                } catch (Throwable t) {
                }
            }
        }
    }

    public boolean disconnectWifi() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            wifiManager.disconnect();
            wifiManager.setWifiEnabled(false);
            return true;
        }
        return false;
    }

    public void invokeLogin(View view) {

        id = idEditText.getText().toString();
        pw = pwEditText.getText().toString();

        login(id, pw, imei, ssn);
    }

    private void setTextView(int resourceId, String text) {
        TextView view = (TextView) findViewById(resourceId);
        view.setText(text);
    }

    private void login(final String id, String pw, String ssn, String imei) {

        class LoginAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(MainActivity.this, "잠시만 기다려주세요.", "진행 중...");
            }

            @Override
            protected String doInBackground(String... params) {

                String id = params[0];
                String pw = params[1];
                String ssn = params[2];
                String imei = params[3];

                InputStream is = null;
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("id", params[0])
                        .appendQueryParameter("pw", params[1])
                        .appendQueryParameter("ssn", params[2])
                        .appendQueryParameter("imei", params[3]);
                String query = builder.build().getEncodedQuery();
                String response = null;

                try {
                    URL url = new URL("http://springs-thursday.iptime.org/st-apps/usim/login.php");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setConnectTimeout(15000);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);

                    OutputStream outputStream = urlConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

                    bufferedWriter.write(query);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    urlConnection.connect();

                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        while ((line = bufferedReader.readLine()) != null) {
                            response += line;
                        }
                    }
                    else {
                        response ="";
                    }
                }
                catch(MalformedURLException e){
                    e.printStackTrace();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String result){
                String response = result.trim();
                loadingDialog.dismiss();
                Intent intent = new Intent(MainActivity.this, UserProfile.class);
                intent.putExtra(ID, id);
                intent.putExtra(RESULT, result);
                finish();
                startActivity(intent);
            }
        }
        LoginAsync loginAsync = new LoginAsync();
        loginAsync.execute(id, pw, imei, ssn);
    }

}