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


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.ParseException;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MapsActivity";
    private Context mContext;

    //Parse details


    //Constants
    private static final int DEFAULT_STATE = 0;
    private static final int ADD_MARKERS_STATE = 1;
    private static final int DRAW_PATH_STATE = 2;

    private String currentConnectedFriend = null;

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

    //SocketIO mSocket;
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


        NAME = ParseUtility.getUserName();
        EMAIL = ParseUtility.getUserEmail();

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
            //LatLng otherlocation = new LatLng(location.getLatitude()+100, location.getLongitude()+100);
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
                if (CUR_STATE == ADD_MARKERS_STATE) {
                    LatLng lastPoistion = null;
                    if (pathMarkers != null && pathMarkers.size() != 0) {
                        lastPoistion = pathMarkers.get(pathMarkers.size() - 1);
                    }
                    pathMarkers.add(latLng);
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    if (lastPoistion != null) {
                        String url = getMapsApiDirectionsUrl(lastPoistion, latLng);
                        ReadTask downloadTask = new ReadTask(mContext);
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
                if(currentConnectedFriend != null){
                    receiveSelectFriendLocation(currentConnectedFriend);
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_share));
                }

                CUR_STATE = ADD_MARKERS_STATE;
                break;
           /* case ADD_MARKERS_STATE:
                CUR_STATE = DRAW_PATH_STATE;
                break;*/
            case DRAW_PATH_STATE:
                pathMarkers.clear();
                mMap.clear();
                fab.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
                currentConnectedFriend = null;
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
            friends = ParseUtility.getConnectedList();
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

    public void receiveSelectFriendLocation(String receiverName){
        if(mMap != null){

        }
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

    public void addPolyLineOptions(PolylineOptions options){
        if(mMap != null && options != null){
            mMap.addPolyline(options);
        }else{
            Log.e(TAG,"mMap is null");
        }
    }





    public void connectToServer(){
        final String local_tag = "SocketIO";
        /*try {
            mSocket = new SocketIO(Constants.SERVER_URL);
            mSocket.connect(new IOCallback() {
                @Override
                public void onDisconnect() {

                }

                @Override
                public void onConnect() {
                    Log.i(local_tag, "connection established");
                }

                @Override
                public void onMessage(String s, IOAcknowledge ioAcknowledge) {
                    Log.i(local_tag, "onMessage : " + s);
                }

                @Override
                public void onMessage(JSONObject jsonObject, IOAcknowledge ioAcknowledge) {
                    Log.i(local_tag, "onMessage jsonObject : " + jsonObject);
                }

                @Override
                public void on(String event, IOAcknowledge ioAcknowledge, Object... objects) {
                    Log.i(local_tag, "Server triggered event : " + event);
                }

                @Override
                public void onError(SocketIOException e) {
                    Log.i(local_tag, "onerror");
                }
            });


        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/

       /* try {
            mSocket = new SocketIO(Constants.SERVER_URL);
            mSocket.connect();
            mSocket.on(Constants.EVENT_RECEIVE_LOCATION, new Emitter.Listener() {
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

            mSocket.on(Constants.MESSAGE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    final Object[] random = args;
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Log.i(TAG, "received location " + random[0]);
                            Toast.makeText(mContext, "recieved message from server ; " + random[0], Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }*/

    }



    public void sendLocation(double latitude, double longitude){
       // double[] location = {latitude, longitude};
        String location = String.valueOf(latitude) + String.valueOf(longitude);
        //mSocket.emit(Constants.EVENT_SEND_LOCATION,location);
    }

    public void disconnectFromServer(){
       // mSocket.disconnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnectFromServer();
    }
}

