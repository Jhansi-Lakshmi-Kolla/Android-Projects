package com.osu.way2go.db;

import android.provider.BaseColumns;

/**
 * Created by jhansi_lak on 11/7/2015.
 */
public final class MySQLiteContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public MySQLiteContract() {}

    /* Inner class that defines the table contents */
    public static abstract class UserEntry implements BaseColumns {

        public static final String TABLE_USER = "user";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_FIRSTNAME = "firstname";
        public static final String COLUMN_LASTNAME = "lastname";
    }
}
