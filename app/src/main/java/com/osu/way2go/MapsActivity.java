package com.osu.way2go;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.osu.way2go.com.osu.way2go.utilities.HttpConnection;
import com.osu.way2go.com.osu.way2go.utilities.PathJSONParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";


    //Constants
    private static final int DEFAULT_STATE = 0;
    private static final int ADD_MARKERS_STATE = 1;
    private static final int DRAW_PATH_STATE = 2;

    private int CUR_STATE = DEFAULT_STATE;

    //Navigation drawer contents
    String TITLES[] = {"Invite Friends","Friends","Connected","Blocked","Settings"};
    int ICONS[] = {android.R.drawable.btn_star,android.R.drawable.btn_star,android.R.drawable.btn_star,android.R.drawable.btn_star,android.R.drawable.btn_star};
    private String NAME = "Jhansi Lakshmi";
    private String EMAIL = "jhansilakshmikolla@gmail.com";
    int PROFILE = R.drawable.jhansi;

    //Path direct contents
    List<LatLng> pathMarkers;

    private GoogleMap mMap;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DrawerLayout drawer;
    ActionBarDrawerToggle mDrawerToggle;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFloatingBar();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MyAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE, this);

        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.openDrawer,R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }



        };
        drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();

        pathMarkers = new ArrayList<>();


    }

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            Log.i(TAG, "onLocationChanged");
            LatLng curlocation = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng otherlocation = new LatLng(location.getLatitude()+100, location.getLongitude()+100);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(curlocation).title("Marker in current location"));
            //addLines(new LatLng(40.722543, -73.998585), new LatLng(40.7577, -73.9857));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curlocation, 13));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapready");
        mMap = googleMap;
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(CUR_STATE == ADD_MARKERS_STATE){
                    LatLng lastPoistion = null;
                    if(pathMarkers != null && pathMarkers.size() != 0){
                        lastPoistion = pathMarkers.get(pathMarkers.size()-1);
                    }
                    pathMarkers.add(latLng);
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    if(lastPoistion != null){
                        String url = getMapsApiDirectionsUrl(lastPoistion, latLng);
                        ReadTask downloadTask = new ReadTask();
                        downloadTask.execute(url);
                    }

                }

            }
        });

        /*LatLng sydney = new LatLng(40.002401, -83.015073);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    public void toggleFloatingBar(){
        int state = CUR_STATE;
        switch (state){
            case DEFAULT_STATE:
                CUR_STATE = ADD_MARKERS_STATE;
                break;
            case ADD_MARKERS_STATE:
                CUR_STATE = DRAW_PATH_STATE;
               /* String url = getMapsApiDirectionsUrl(pathMarkers);
                ReadTask downloadTask = new ReadTask();
                downloadTask.execute(url);*/
                break;
            case DRAW_PATH_STATE:
                pathMarkers.clear();
                mMap.clear();
                CUR_STATE = DEFAULT_STATE;
                break;
        }
        Log.i(TAG, "Current state is " + CUR_STATE);
    }

   /* private void addLines(List<LatLng> markers) {

        Log.i(TAG, "adding polylines");
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.add(TIMES_SQUARE, BROOKLYN_BRIDGE, LOWER_MANHATTAN,
                TIMES_SQUARE).width(5).color(Color.BLUE).geodesic(true);
        mMap.addPolyline(polylineOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LOWER_MANHATTAN,13));

    }*/

    private String getMapsApiDirectionsUrl(LatLng position1, LatLng position2) {
       // String waypoints = "waypoints=optimize:true";
                /*+ LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude
                + "|" + "|" + BROOKLYN_BRIDGE.latitude + ","
                + BROOKLYN_BRIDGE.longitude + "|" + WALL_STREET.latitude + ","
                + WALL_STREET.longitude;*/
        String waypoints = "origin="
                + position1.latitude + ","
                + position1.longitude
                +"&destination="
                + position2.latitude + ","
                + position2.longitude;
        /*for(LatLng location : markers){
            waypoints = waypoints + "|" + location.latitude + "," + location.longitude;
        }*/

        String sensor = "sensor=false&mode=driving&alternatives=true&key=AIzaSyD4TRKgooV4yC6arDAbCi0HHQaSw8_EXKU";
        String params = waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
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
                new ParserTask().execute(result);
            }
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                Log.i(TAG,jObject.toString());
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
            if(mMap != null){
                mMap.addPolyline(polyLineOptions);
            }else{
                Log.e(TAG,"mMap is null");
            }

        }
    }


}
