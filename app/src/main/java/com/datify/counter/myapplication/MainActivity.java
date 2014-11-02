package com.datify.counter.myapplication;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;


public class MainActivity extends ListActivity {
    private Calendar currentDate;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        String[] values = new String[] { "Bus Times", "Study Times", "General",
                "Commute", "Cycling", "Sprinting", "Swimming", "TV",
                "Performance", "Other" };
        // use your custom layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.rowlayout, R.id.label, values);
        setListAdapter(adapter);



    }

    //DELETE THIS -- TEMPORARY JSON TEST
    private class MyHttpPost extends AsyncTask<String, Void, Boolean>
    {
        //Log.d("Console", currentDate.toString());
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
                nameValuePairs.add(new BasicNameValuePair("start_date", currentDate.getTime().toString()));
                nameValuePairs.add(new BasicNameValuePair("end_date", currentDate.getTime().toString()));
                nameValuePairs.add(new BasicNameValuePair("collection_id", "1"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
        currentDate = Calendar.getInstance();
        //Log.d("Console", "Before currentDate string");
        //Log.d("Console", currentDate.getTime().toString());
        //new MyHttpPost().execute();

        String index = Long.toString(id);
        Intent moveToTimer = new Intent(this, TimerActivity.class);
        moveToTimer.putExtra("Index", index);
        startActivity(moveToTimer);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}