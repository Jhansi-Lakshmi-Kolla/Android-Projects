package com.osu.way2go.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jhansi_lak on 11/7/2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {


    private static final String TEXT_TYPE = " TEXT";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +
                    MySQLiteContract.UserEntry.TABLE_USER + " (" +
                    MySQLiteContract.UserEntry._ID + " INTEGER PRIMARY KEY," +
                    MySQLiteContract.UserEntry.COLUMN_USERNAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    MySQLiteContract.UserEntry.COLUMN_PASSWORD + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    MySQLiteContract.UserEntry.COLUMN_FIRSTNAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    MySQLiteContract.UserEntry.COLUMN_LASTNAME + TEXT_TYPE + NOT_NULL + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MySQLiteContract.UserEntry.TABLE_USER;

    private static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 1;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
