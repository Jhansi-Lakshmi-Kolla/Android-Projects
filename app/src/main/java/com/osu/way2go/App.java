package com.osu.way2go;

/**
 * Created by hiiamgovind on 11/7/2015.
 */
import android.app.Application;
import com.parse.Parse;

public class App extends Application {

    @Override public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "vf4GA2XhHHGXPFiUui7DtLehlReMLYb1TDhwJEHz", "do5SJemdcJcfdtAeOZKaP0W3jG2It2b9IsmFiEft"); // Your Application ID and Client Key are defined elsewhere
    }
}
