package com.osu.way2go;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.osu.way2go.db.MySQLiteDBUtility;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    Context mContext;
    EditText usernameBox;
    EditText passwordBox;
    Button loginButton;
    TextView signUp;
    TextView forgotPassword;
    MySQLiteDBUtility mDBUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.title_activity_login);
        mContext = this;
        mDBUtility = new MySQLiteDBUtility(mContext);
        usernameBox = (EditText) findViewById(R.id.username);
        passwordBox = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithParse();
            }
        });
        signUp = (TextView) findViewById(R.id.signup);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(mContext, SignUpActivity.class);
                startActivity(signupIntent);
            }
        });
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

       /* Button connect = (Button) findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    Socket mSocket = IO.socket("http://192.168.2.7:8080");
                    mSocket.connect();
                    mSocket.emit("connect", "jhansi");
                    mSocket.on("message", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            if (args != null)
                                Log.i(TAG, "server sent " + args[0]);
                        }
                    });
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });*/
    }

    public void loginWithParse() {
        if (ParseUser.getCurrentUser() != null) {
            ParseUser.logOut();
        }
        String userNameEntered = usernameBox.getText().toString();
        String passwordEntered = passwordBox.getText().toString();
        ParseUser.logInInBackground(userNameEntered, passwordEntered, new LogInCallback() {
            @Override
            public void done(ParseUser user, com.parse.ParseException e) {
                if (user != null) {
                    Log.i(TAG, "Logged in");
                    final ParseUser currentUser = ParseUser.getCurrentUser();
                    if (currentUser != null) {
                        // do stuff with the user
                        Log.i(TAG, "Friends");
                        List<String> f = currentUser.getList("Friends");
                        for (String model : f) {
                            Log.i(TAG, model);
                        }
                        Log.i(TAG, "Users");
                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        //query.whereEqualTo("gender", "female");
                        query.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> objects, com.parse.ParseException e) {
                                if (e == null) {
                                    for (ParseUser p : objects) {
                                        //Log.i(TAG, currentUser.getUsername());
                                        if (!currentUser.getUsername().equals(p.getUsername())) {
                                            Log.i(TAG, p.getUsername());
                                        }
                                    }
                                } else {
                                    // Something went wrong.
                                }
                            }
                        });

                    } else {
                        Log.i(TAG, "failed");
                    }



                    Intent mapIntent = new Intent(mContext, MapsActivity.class);
                    startActivity(mapIntent);
                } else {
                    Log.i(TAG, "Log in failed");
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Enter Correct credentials");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });
//                if(mDBUtility.areCorrectCredentails(userNameEntered, passwordEntered)){
//
//                    Intent mapIntent = new Intent(mContext, MapsActivity.class);
//                    startActivity(mapIntent);
//                }else{
//                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
//                    alertDialog.setTitle("Alert");
//                    alertDialog.setMessage("Enter Correct credentials");
//                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                    alertDialog.show();
//                }
    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }
}