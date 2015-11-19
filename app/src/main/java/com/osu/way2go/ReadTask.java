package com.osu.way2go;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.osu.way2go.com.osu.way2go.utilities.HttpConnection;

/**
 * Created by jhansi_lak on 11/18/2015.
 */
public class ReadTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "ReadTask";
    private MapsActivity callerMapsActivity;

    public ReadTask(Context context){
        callerMapsActivity = (MapsActivity) context;
    }
    @Override
    protected String doInBackground(String... url) {
        String data = "";
        try {
            HttpConnection http = new HttpConnection();
            data = http.readUrl(url[0]);
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result == null || result.length() == 0){
            Log.e(TAG,"result from HttpConnection is null");
        }else{
            Log.i(TAG, "result from HttpConnection " + result);
            new ParserTask(callerMapsActivity).execute(result);
        }
    }
}
