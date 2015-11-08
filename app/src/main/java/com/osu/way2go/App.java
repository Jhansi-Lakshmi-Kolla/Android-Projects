package com.osu.way2go;

/**
 * Created by hiiamgovind on 11/7/2015.
 */
import android.app.Application;
import com.parse.Parse;

public class App extends Application {

    @Override public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "XXXXX", "XXXXX"); // Your Application ID and Client Key are defined elsewhere
    }
}
