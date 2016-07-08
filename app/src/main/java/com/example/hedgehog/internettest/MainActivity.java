package com.example.hedgehog.internettest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvMain;
    Button bGetCity, bGetDistrict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMain = (TextView) findViewById(R.id.tvMain);
        bGetCity = (Button) findViewById(R.id.bGetCity);
        bGetDistrict = (Button) findViewById(R.id.bGetDistrict);
        bGetCity.setOnClickListener(this);
        bGetDistrict.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        AsyncTask<Integer,Integer,String> at = new MyAsyncTask();
        switch (v.getId()){
            case R.id.bGetCity:
                 at.execute(R.id.bGetCity);

                break;
            case R.id.bGetDistrict:
                    at.execute(R.id.bGetDistrict);
                break;
        }
    }

    private class MyAsyncTask extends AsyncTask<Integer, Integer, String>{

        @Override
        protected String doInBackground(Integer... params) {
            String responce = null;
            InputStream is = null;
            String myurl = " ";
            switch (params[0]){
                case R.id.bGetCity:
                    myurl = API.getPock();
                    break;
                case R.id.bGetDistrict:
                    myurl = API.getPock();
                    break;
            }

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                int responseCode = conn.getResponseCode();
                if (responseCode!=200){
                    Log.d("asdf", ""+ responseCode);
                }
                is = conn.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(isr);
                String read = null;
                try {
                    read = br.readLine();
                    while(read != null) {
                        sb.append(read);
                        read = br.readLine();
                    }
                } catch (IOException e) { }
                responce = sb.toString();
                try {
                    isr.close();
                    br.close();
                } catch (IOException e) { }

            } catch (Exception e) { }

            StringBuilder sb = new StringBuilder();

            JSONObject jsonResponce = null;
            try {
                jsonResponce = new JSONObject(responce);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray jsonTypes = jsonResponce.optJSONArray("types");
            String name = jsonResponce.optString("name");
            sb.append(name);
            sb.append("\n");
            for (int i = 0; i < jsonTypes.length(); i++){

                JSONObject type = jsonTypes.optJSONObject(i);
                String name2 = type.optString("name");
                sb.append(name2);
                sb.append("\n");
            }

            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            tvMain.setText(s);
        }
    }

    public static boolean isNetworkExist (Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean result = false;

        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!= null) {
            result = result || connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE).isConnected();
        } else {
            result = result || true;
        }
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!= null) {
            result = result || connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI).isConnected();
        } else {
            result = result || true;
        }
        return result;

    }
}
