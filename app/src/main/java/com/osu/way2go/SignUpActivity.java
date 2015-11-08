
package com.osu.way2go;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ParseException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.osu.way2go.db.MySQLiteDBUtility;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private Context mContext;
    MySQLiteDBUtility mDbUtility;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mContext = this;
        mDbUtility = new MySQLiteDBUtility(mContext);

        final EditText firstnameBox = (EditText) findViewById(R.id.firstnamebox);
        final EditText lastnameBox = (EditText) findViewById(R.id.lastnamebox);
        final EditText emailBox = (EditText) findViewById(R.id.emailBox);
        final EditText passwordBox = (EditText) findViewById(R.id.passwordBox);
        final EditText confirmPasswordBox = (EditText) findViewById(R.id.confirmPasswordBox);

        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstname = firstnameBox.getText().toString();
                String lastname = lastnameBox.getText().toString();
                String emailID = emailBox.getText().toString();
                String password = passwordBox.getText().toString();
                String confirmPassword = confirmPasswordBox.getText().toString();
                if(isPasswordGood(password, confirmPassword)){
                    registerUser(firstname, lastname, emailID, password);
                    //add in parse
                    ParseUser user = new ParseUser();
                    user.setUsername(emailID);
                    user.setPassword(password);
                    user.setEmail(emailID);

                    // other fields can be set just like with ParseObject
                    user.put("FName", firstname);
                    user.put("LName", lastname);
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(com.parse.ParseException e) {
                            if(e==null)
                            {Log.i(TAG, "Put to parse");}
                            else
                            {Log.i(TAG, "Not put in parse");}
                        }

//                        public void done(ParseException e) {
//                            if(e==null)
//                            {Log.i(TAG, "Sexy");}
//                            else
//                            {Log.i(TAG, "Not Sexy");}
//
//                        }

                    });

                }
            }
        });
    }

    public void registerUser(String firstName, String lastName, String emailID, String password){
        mDbUtility.createUser(emailID, firstName, lastName, password);
    }

    public boolean isPasswordGood(String pswd, String confirmpswd){
        if(pswd.equals(confirmpswd)){
            return true;
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Passwords do not match");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return false;
        }
    }

}