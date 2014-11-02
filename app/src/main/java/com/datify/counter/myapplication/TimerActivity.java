package com.datify.counter.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class TimerActivity extends Activity {

    private Button startButton;
    private Button pauseButton;
    private Button saveButton;

    private TextView timerValue;

    private long startTime = 0L;

    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    private Calendar startDate;
    private Calendar endDate;
    private String index = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Bundle extras = getIntent().getExtras();

        if (extras != null) { //intent was sent properly with index info
            index = extras.getString("Index"); //get index info from the intent
        }
        Log.d("Console",index);
        timerValue = (TextView) findViewById(R.id.timerValue);

        startButton = (Button) findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
                startDate = Calendar.getInstance();
            }
        });

        pauseButton = (Button) findViewById(R.id.pauseButton);

        pauseButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimerThread);
            }
        });

        saveButton = (Button) findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            endDate = Calendar.getInstance();
            new MyHttpPost().execute();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(TimerActivity.this, MainActivity.class));
        finish();

    }



    private class MyHttpPost extends AsyncTask<String, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(String... arg0) {
            Log.d("Console", "Post entered");

            HttpParams params = new BasicHttpParams();
            Log.d("Console", "HttpParams OK");
            params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                    HttpVersion.HTTP_1_1);
            Log.d("Console", "Param setup OK");

            HttpClient httpclient = new DefaultHttpClient(params);
            Log.d("Console", "DefaultHttpClient OK");
            HttpPost httppost = new HttpPost("http://172.26.9.205:9393/time");
            Log.d("Console", "HttpPost OK");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                //Log.d("Console", "Start Date: " + startDate.getTime().toString());
                //Log.d("Console", "End Date: " + endDate.getTime().toString());
                nameValuePairs.add(new BasicNameValuePair("start_date", startDate.getTime().toString()));
                nameValuePairs.add(new BasicNameValuePair("end_date", endDate.getTime().toString()));
                nameValuePairs.add(new BasicNameValuePair("collection_id", index));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
                //Log.d("Console", "UTF Encoding OK");
                //Log.d("Console", "Start Date: " + startDate.getTime().toString());
                //Log.d("Console", "End Date: " + endDate.getTime().toString());
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                Log.d("Console", "ClientProtocolException");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.d("Console", "IOException");
            }


            Log.d("Console", "Post exited");
            return true;
        }
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            timerValue.setText("" + mins + ":"
                            + String.format("%02d", secs) + ":"
                            + String.format("%03d", milliseconds));
            customHandler.postDelayed(this, 0);
        }
    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == android.R.id.home)
        {
            startActivity(new Intent(TimerActivity.this, MainActivity.class));
            finish();
            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
