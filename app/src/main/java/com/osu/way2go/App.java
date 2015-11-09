package com.osu.way2go;

/**
 * Created by hiiamgovind on 11/7/2015.
 */
import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.parse.Parse;

import java.io.IOException;

public class App extends Application {

    private static final String TAG = "APP";

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

    /*public void sendRegID(){
        try {
            Bundle data = new Bundle();
            // the account is used for keeping
            // track of user notifications
            data.putString("account", account);
            // the action is used to distinguish
            // different message types on the server
            data.putString("action", "register");
            String msgId = Integer.toString(getNextMsgId());
            gcm.send( + "@gcm.googleapis.com", msgId,
                    SyncStateContract.Constants.GCM_DEFAULT_TTL, data);
        } catch (IOException e) {
            Log.e(TAG, "IOException while sending registration id", e);
        }
    }

    private void sendMessage(GoogleCloudMessaging gcm, Intent intent) {
        try {
            String msg = intent.getStringExtra(SyncStateContract.Constants.KEY_MESSAGE_TXT);
            Bundle data = new Bundle();
            data.putString(SyncStateContract.Constants.ACTION, SyncStateContract.Constants.ACTION_ECHO);
            data.putString("message", msg);
            String id = Integer.toString(getNextMsgId());
            gcm.send(mSenderId + "@gcm.googleapis.com", id, data);
            Log.v("grokkingandroid", "sent message: " + msg);
        } catch (IOException e) {
            Log.e("grokkingandroid", "Error while sending a message", e);
        }
    }*/



}
