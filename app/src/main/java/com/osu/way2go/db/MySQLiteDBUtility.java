package com.osu.way2go.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by jhansi_lak on 11/7/2015.
 */
public class MySQLiteDBUtility {

    private static final String TAG = "MySQLiteDBUtility";

   // private SQLiteDatabase db;
    private Context mContext;
    MySQLiteHelper mDbHelper;

    public MySQLiteDBUtility(Context context){
        this.mContext = context;
        mDbHelper = new MySQLiteHelper(mContext);
    }

    public void close() {
        mDbHelper.close();
    }

    public boolean createUser(String userName, String firstName, String lastname, String password){
        String tableName = MySQLiteContract.UserEntry.TABLE_USER;
        if(hasUser(userName,tableName)){
            return false;
        }else{
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MySQLiteContract.UserEntry.COLUMN_USERNAME, userName);
            values.put(MySQLiteContract.UserEntry.COLUMN_PASSWORD, password);
            values.put(MySQLiteContract.UserEntry.COLUMN_FIRSTNAME, firstName);
            values.put(MySQLiteContract.UserEntry.COLUMN_LASTNAME, lastname);

            long newRowID = db.insert(tableName, null, values);
            if(newRowID != -1)
                return true;
            else
                return false;
        }
    }

    public boolean hasUser(String username, String tableName){
        if(mDbHelper == null){
            Log.i(TAG,"mDbHelper is null");
            return false;
        }
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        if(db == null){
            Log.i(TAG,"GetReadable database returned null");
        }
        String[] projection = {
                MySQLiteContract.UserEntry._ID,
                MySQLiteContract.UserEntry.COLUMN_USERNAME,
                MySQLiteContract.UserEntry.COLUMN_PASSWORD,
                MySQLiteContract.UserEntry.COLUMN_FIRSTNAME,
                MySQLiteContract.UserEntry.COLUMN_LASTNAME
        };

        String selection = MySQLiteContract.UserEntry.COLUMN_USERNAME + " =?";
        String[] selectionArgs = {username};

        Cursor c = db.query(tableName, projection, selection, selectionArgs, null, null, null);
        return c.moveToFirst();
    }

    public boolean areCorrectCredentails(String userName, String password){
        String tableName = MySQLiteContract.UserEntry.TABLE_USER;
        if(!hasUser(userName, tableName)){
            return false;
        }else{
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            String[] projection = {
                    MySQLiteContract.UserEntry.COLUMN_PASSWORD,
            };

            String selection = MySQLiteContract.UserEntry.COLUMN_USERNAME + " =?";
            String[] selectionArgs = {userName};

            Cursor c = db.query(tableName, projection, selection, selectionArgs, null, null, null);
            if(c.moveToFirst()){
                String actualPassword = c.getString(c.getColumnIndex(MySQLiteContract.UserEntry.COLUMN_PASSWORD));
                if(actualPassword.equals(password))
                    return true;
                else
                    return false;
            }
        }
        return false;
    }
}
