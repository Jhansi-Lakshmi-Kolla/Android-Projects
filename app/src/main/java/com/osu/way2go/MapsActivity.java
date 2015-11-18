package com.osu.way2go;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.osu.way2go.com.osu.way2go.utilities.HttpConnection;
import com.osu.way2go.com.osu.way2go.utilities.PathJSONParser;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MapsActivity";
    private Context mContext;

    //Parse details
    ParseUser currentUser;


    //Constants
    private static final int DEFAULT_STATE = 0;
    private static final int ADD_MARKERS_STATE = 1;
    private static final int DRAW_PATH_STATE = 2;

    private String currentConnectedFriend;

    private int CUR_STATE = DEFAULT_STATE;

    //Navigation drawer contents
    String TITLES[] = {"Invite Friends","Invites","Connected","Blocked","Settings"};
    int ICONS[] = {android.R.drawable.btn_star,android.R.drawable.btn_star,android.R.drawable.btn_star,android.R.drawable.btn_star,android.R.drawable.btn_star};
    private String NAME;
    private String EMAIL;
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

    Socket mSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mContext = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentUser = ParseUser.getCurrentUser();
        NAME = getUserName();
        EMAIL = getUserEmail();

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
        mDrawerToggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.openDrawer,R.string.closeDrawer);
        drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();

        /*NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/

        pathMarkers = new ArrayList<>();
        connectToServer();

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
            sendLocation(curlocation.latitude, curlocation.longitude);
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
                showSelectfriendDialog();
                CUR_STATE = ADD_MARKERS_STATE;
                break;
            case ADD_MARKERS_STATE:
                CUR_STATE = DRAW_PATH_STATE;
                break;
            case DRAW_PATH_STATE:
                pathMarkers.clear();
                mMap.clear();
                CUR_STATE = DEFAULT_STATE;
                break;
        }
        Log.i(TAG, "Current state is " + CUR_STATE);
    }

    public void showSelectfriendDialog(){
        final Dialog selectFriendsDialog = new Dialog((mContext));
        selectFriendsDialog.setContentView(R.layout.add_friends_layout);
        selectFriendsDialog.setTitle("Select a Friend");
        ListView addFriendsList = (ListView) selectFriendsDialog.findViewById(R.id.addFriends);
        List<String> friends = null;
        try {
            friends = getConnectedList();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final FriendsListDialogAdapter selectFriendAdapter = new FriendsListDialogAdapter(friends, mContext, 1);
        addFriendsList.setAdapter(selectFriendAdapter);


        Button inviteAll = (Button) selectFriendsDialog.findViewById(R.id.inviteAll);
        inviteAll.setVisibility(View.INVISIBLE);

        Button invite = (Button) selectFriendsDialog.findViewById(R.id.invite);
        invite.setText("Select");

       invite.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               currentConnectedFriend = selectFriendAdapter.getSelectedFriend();
               selectFriendsDialog.dismiss();
           }
       });

        selectFriendsDialog.show();
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

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        return false;
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

    public String getUserName(){
        return currentUser.get("FName").toString()+ " " + currentUser.get("LName").toString();
    }

    public String getUserEmail(){
        return currentUser.getEmail();
    }

    public List<String> getInvites() throws ParseException {
        List<Object> invites = new ArrayList<>();
        List<String> invitesNames = new ArrayList<>();
        ParseQuery<ParseObject> q = ParseQuery.getQuery("Invite");
        q.whereEqualTo("Username", currentUser.getUsername());
        List<ParseObject> li = q.find();
        //invites.addAll(li.get(0).getString("Invites"));
        for(ParseObject o : li){
            invites.addAll(o.getList("Invites"));
        }


        for(Object oo : invites)
            invitesNames.add(oo.toString());
        return invitesNames;
    }

    public List<String> getallUsers() throws ParseException {
        final List<String> allusers = new ArrayList<>();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        List<ParseUser> r = query.find();
        //query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        for(ParseUser p : r)
        {
            if(!currentUser.getUsername().equals(p.getUsername()))
            {
                Log.i(TAG, "adding inside getAllusers " + p.getUsername());
                allusers.add(p.getUsername());
            }
        }
        return allusers;
    }

    public List<String> getConnectedList() throws ParseException {
        List<Object> connectedList = new ArrayList<>();
        List<String> connectedNames = new ArrayList<>();
        ParseQuery<ParseObject> q = ParseQuery.getQuery("Invite");
        q.whereEqualTo("Username", currentUser.getUsername());
        List<ParseObject> li = q.find();
        //invites.addAll(li.get(0).getString("Invites"));
        for(ParseObject o : li){
            connectedList.addAll(o.getList("Friends"));
        }


        for(Object oo : connectedList)
            connectedNames.add(oo.toString());
        return connectedNames;
    }

    public List<String> getBlockedList() throws ParseException {
        List<Object> blockedList = new ArrayList<>();
        List<String> blockedNames = new ArrayList<>();
        ParseQuery<ParseObject> q = ParseQuery.getQuery("Invite");
        q.whereEqualTo("Username", currentUser.getUsername());
        List<ParseObject> li = q.find();
        //invites.addAll(li.get(0).getString("Invites"));
        for(ParseObject o : li){
            blockedList.addAll(o.getList("Blocked"));
        }


        for(Object oo : blockedList)
            blockedNames.add(oo.toString());
        return blockedNames;
    }
    public List<String> getDirectedList(){
        return null;
    }

    public void putInvites(List<String> invites){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Invite");


        for(String user : invites){
            query.whereEqualTo("Username",user);
            try {
                List<ParseObject> results = query.find();
                for(ParseObject p : results){
                    Log.i(TAG, "putting invites in " + p.getString("Username"));
                    final ParseUser u = ParseUser.getCurrentUser();
                    p.addUnique("Invites", u.getUsername());
                    p.save();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }

    public void putInFriendsList(String inviter){
        ParseQuery<ParseObject> pq = ParseQuery.getQuery("Invite");
        pq.whereEqualTo("Username", ParseUser.getCurrentUser().getUsername());
        try {
            List<ParseObject> results = pq.find();
            for(ParseObject p : results){
                Log.i(TAG, "putting invites in " + p.getString("Username"));
                //final ParseUser u = ParseUser.getCurrentUser();
                p.addUnique("Friends", inviter);
                p.save();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseQuery<ParseObject> qq = ParseQuery.getQuery("Invite");
        //qq.whereEqualTo("Username", ParseUser.getCurrentUser().getUsername());
        qq.whereEqualTo("Username", inviter);
        try {
            List<ParseObject> results = qq.find();
            for(ParseObject p : results){
                Log.i(TAG, "putting invites in " + p.getString("Username"));
                //final ParseUser u = ParseUser.getCurrentUser();
                p.addUnique("Friends", ParseUser.getCurrentUser().getUsername());
                p.save();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public void removeinInvitesList(String inviter){

        ParseQuery<ParseObject> pq = ParseQuery.getQuery("Invite");
        pq.whereEqualTo("Username", ParseUser.getCurrentUser().getUsername());
        try {
            List<ParseObject> results = pq.find();
            for(ParseObject p : results){
                Log.i(TAG, "putting invites in " + p.getString("Username"));
                p.removeAll("Invites", Arrays.asList(inviter));
                p.save();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void connectToServer(){

        try {
            mSocket = IO.socket(Constants.SERVER_URL);
            mSocket.connect();
            mSocket.on(Constants.EVENT_RECEIVE_SEND, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    final Object[] random = args;
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Log.i(TAG, "received location " + random[0]);
                            Toast.makeText(mContext, "recieved another location ; " + random[0], Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }



    public void sendLocation(double latitude, double longitude){
       // double[] location = {latitude, longitude};
        String location = String.valueOf(latitude) + String.valueOf(longitude);
        mSocket.emit(Constants.EVENT_SEND_LOCATION,location);
    }

    public void disconnectFromServer(){
        mSocket.disconnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnectFromServer();
    }
}

