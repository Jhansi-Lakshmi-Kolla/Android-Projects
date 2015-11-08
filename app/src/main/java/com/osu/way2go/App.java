package com.osu.way2go;

/**
 * Created by hiiamgovind on 11/7/2015.
 */
import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.parse.Parse;

import java.io.IOException;

public class App extends Application {

    GoogleCloudMessaging gcm;
    String regid;
    String PROJECT_NUMBER = "631056255308";

    @Override public void onCreate() {
        super.onCreate();
        getRegId();
        Parse.initialize(this, "vf4GA2XhHHGXPFiUui7DtLehlReMLYb1TDhwJEHz", "do5SJemdcJcfdtAeOZKaP0W3jG2It2b9IsmFiEft"); // Your Application ID and Client Key are defined elsewhere
    }

    public void getRegId(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regid;
                    Log.i("GCM", msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
               // regIDTV.setText(msg + "\n");
            }
        }.execute(null, null, null);
    }
}
