package com.osu.way2go;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ParseException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.osu.way2go.db.MySQLiteDBUtility;
import com.parse.LogInCallback;
import com.parse.ParseUser;
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
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    // do stuff with the user
                    ParseUser.logOut();
                }
                String userNameEntered = usernameBox.getText().toString();
                String passwordEntered = passwordBox.getText().toString();
                ParseUser.logInInBackground(userNameEntered, passwordEntered, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, com.parse.ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.
                            Log.i(TAG, "Logged in");
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