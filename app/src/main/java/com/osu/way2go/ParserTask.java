package com.osu.way2go;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.osu.way2go.com.osu.way2go.utilities.PathJSONParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jhansi_lak on 11/18/2015.
 */
public class ParserTask extends
        AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    private static final String TAG = "ParserTask";
    private MapsActivity callerMapsActivity;

    public ParserTask(MapsActivity mMapsActivity){
        this.callerMapsActivity = mMapsActivity;
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(
            String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            PathJSONParser parser = new PathJSONParser();
            Log.i(TAG, jObject.toString());
            routes = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
        ArrayList<LatLng> points = null;
        PolylineOptions polyLineOptions = null;
        if(routes == null || routes.size() == 0){
            Log.e(TAG, "routes returned from background ParserTask has no content");
        }

        // traversing through routes
        for (int i = 0; i < routes.size(); i++) {
            Log.i(TAG,"inside routes");
            points = new ArrayList<LatLng>();
            polyLineOptions = new PolylineOptions();
            List<HashMap<String, String>> path = routes.get(i);

            for (int j = 0; j < path.size(); j++) {
                Log.i(TAG,"inside path");
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            polyLineOptions.addAll(points);
            polyLineOptions.width(4);
            polyLineOptions.color(Color.BLUE);
        }
        callerMapsActivity.addPolyLineOptions(polyLineOptions);
    }
}