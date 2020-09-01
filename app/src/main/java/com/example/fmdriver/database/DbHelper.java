package com.example.fmdriver.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;


public class DbHelper extends SQLiteOpenHelper {

    public static final String TABLE_DEVICES = "devices";

    //TABULKA S PŘERUŠENÍMI
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_REMOTE_DATABASE_ID = "remote_id";
    public static final String COLUMN_ANDROID_ID = "android_id";
    public static final String COLUMN_DEVICE_ID = "device_id";
    public static final String COLUMN_DEVICE_TOKEN = "device_token";
    public static final String COLUMN_SERVICE_STATUS = "service_status";
    public static final String COLUMN_GPS_STATUS = "gps_status";
    public static final String COLUMN_LAST_UPDATE = "last_update";

    private static final String DATABASE_NAME = "fm.db";
    private static final int DATABASE_VERSION = 4;

    private static final String TABLE_DEVICES_CREATE = "CREATE TABLE "
            + TABLE_DEVICES
            + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_REMOTE_DATABASE_ID + " INTEGER (300), "
            + COLUMN_ANDROID_ID + " VARCHAR (300), "
            + COLUMN_DEVICE_ID + " VARCHAR (300), "
            + COLUMN_DEVICE_TOKEN + " VARCHAR (500), "
            + COLUMN_SERVICE_STATUS + " TINYINT, "
            + COLUMN_GPS_STATUS + " TINYINT, "
            + COLUMN_LAST_UPDATE + " DATETIME "
            + ");";

    public Context context;


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_DEVICES_CREATE);
    }

    public static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICES);
        onCreate(db);
    }
}
