package com.example.fmdriver.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.fmdriver.MainActivity;
import com.example.fmdriver.listeners.internaldatabase.OnAllDevicesLoadedListener;
import com.example.fmdriver.listeners.internaldatabase.OnDeviceAddedListener;
import com.example.fmdriver.listeners.internaldatabase.OnDeviceLoadedListener;
import com.example.fmdriver.listeners.internaldatabase.OnDeviceRemovedListener;
import com.example.fmdriver.listeners.internaldatabase.OnDeviceUpdatedListener;
import com.example.fmdriver.listeners.internaldatabase.OnDevicesCountCheckedListener;
import com.example.fmdriver.objects.Device;
import com.example.fmdriver.utils.AppConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class DataSource implements AppConstants {

    private SQLiteDatabase database;

    Context context;
    MainActivity activity;

    private DbHelper dbHelper;

    public DataSource(Context context) {
        dbHelper = new DbHelper(context);
        this.context = context;
        this.activity = (MainActivity) context;
    }

    public void open() throws SQLException {
        Log.i(TAG_DB_INTERNAL, "open()");
        if (database != null) {
            Log.i(TAG_DB_INTERNAL, "database != null -> return");
            return;
        }

        try {
            database = dbHelper.getWritableDatabase();
        } catch (NullPointerException e) {
            Log.i(TAG_DB_INTERNAL, "NullPointerException - " + e.getMessage());
            e.printStackTrace();
            dbHelper = new DbHelper(context);
            database = dbHelper.getWritableDatabase();
        } catch (IllegalStateException e) {
            Log.i(TAG_DB_INTERNAL, "IllegalStateException - " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void close() {
        if (dbHelper != null) dbHelper.close();
        dbHelper = null;
    }

    public void clearTable() {
        if (database == null) open();
        database.delete(DbHelper.TABLE_DEVICES, null, null);
    }

    public String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    //closeDb - při TRUE se jen zavře databáze a nic jiného se neprovede
    public void addDevice(final Device device, final OnDeviceAddedListener listener) {
        if (device == null) return;
        if (device.getAndroidId() == null) return;
        if (device.getAndroidId().equals("")) return;

        //Nejprve zjistim, jestli už zařízení se stejným androidId není v databázi.
        //Pokud ne, bude do databáze vloženo. V opačném případě se uložení neprovede.
        getDeviceByAndroidId(device.getAndroidId(), new OnDeviceLoadedListener() {
            @Override
            public void onDeviceLoaded(Device loadedDevice) {
                if (loadedDevice == null) {
                    open();
                    ContentValues values = new ContentValues();
                    values.put(DbHelper.COLUMN_REMOTE_DATABASE_ID, device.getId());
                    values.put(DbHelper.COLUMN_ANDROID_ID, device.getAndroidId());
                    values.put(DbHelper.COLUMN_DEVICE_ID, device.getDeviceId());
                    values.put(DbHelper.COLUMN_DEVICE_TOKEN, device.getToken());
                    values.put(DbHelper.COLUMN_SERVICE_STATUS, device.isServiceIsStarted() ? 1 : 0);
                    values.put(DbHelper.COLUMN_GPS_STATUS, device.isGpsIsStarted() ? 1 : 0);
                    values.put(DbHelper.COLUMN_LAST_UPDATE, formatDate(new Date()));

                    long insertId = database.insert(DbHelper.TABLE_DEVICES, null, values);

                    final Cursor cursor = database.query(
                            DbHelper.TABLE_DEVICES,
                            null,
                            DbHelper.COLUMN_ID + " = " + insertId,
                            null,
                            null,
                            null,
                            null);

                    cursor.moveToFirst();
                    final Device d = cursorToDevice(cursor);
                    if (listener != null) listener.onDeviceAdded(d);
                    cursor.close();
                } else {
                    updateDevice(device, new OnDeviceUpdatedListener() {
                        @Override
                        public void onDeviceUpdated(Device device) {
                            if (listener != null) listener.onDeviceAdded(device);
                        }
                    });
                }
            }
        });
    }

    public void getDeviceByRemoteId(String id, OnDeviceLoadedListener listener) {
        open();
        Device toReturn = null;
        String selectQuery = "SELECT  * FROM " + DbHelper.TABLE_DEVICES + " WHERE " + DbHelper.COLUMN_REMOTE_DATABASE_ID + " = " + id;
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) toReturn = cursorToDevice(cursor);
        cursor.close();
        if(listener != null) listener.onDeviceLoaded(toReturn);
    }

    public void getDeviceByAndroidId(String androidId, OnDeviceLoadedListener listener) {
        open();
        Device toReturn = null;
        String selectQuery = "SELECT  * FROM " + DbHelper.TABLE_DEVICES + " WHERE " + DbHelper.COLUMN_ANDROID_ID + " = '" + androidId + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) toReturn = cursorToDevice(cursor);
        cursor.close();
        if(listener != null) listener.onDeviceLoaded(toReturn);
    }

    public void updateDevice(Device device, OnDeviceUpdatedListener listener) {
        if (database == null) open();

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_REMOTE_DATABASE_ID, device.getId());
        values.put(DbHelper.COLUMN_ANDROID_ID, device.getAndroidId());
        values.put(DbHelper.COLUMN_DEVICE_ID, device.getDeviceId());
        values.put(DbHelper.COLUMN_DEVICE_TOKEN, device.getToken());
        values.put(DbHelper.COLUMN_SERVICE_STATUS, device.isServiceIsStarted());
        values.put(DbHelper.COLUMN_GPS_STATUS, device.isGpsIsStarted());
        values.put(DbHelper.COLUMN_LAST_UPDATE, formatDate(new Date()));

        int rowsUpdated = database.update(DbHelper.TABLE_DEVICES, values, DbHelper.COLUMN_REMOTE_DATABASE_ID + " = ?", new String[] {String.valueOf(device.getId())});
        final Cursor cursor = database.query(
                DbHelper.TABLE_DEVICES,
                null,
                DbHelper.COLUMN_REMOTE_DATABASE_ID + " = " + device.getId(),
                null,
                null,
                null,
                null);

        cursor.moveToFirst();
        final Device d = cursorToDevice(cursor);
        if (listener != null) listener.onDeviceUpdated(d);
    }

    public void updateDeviceStatus(Device device, OnDeviceUpdatedListener listener) {
        if (database == null) open();

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_SERVICE_STATUS, device.isServiceIsStarted());
        values.put(DbHelper.COLUMN_GPS_STATUS, device.isGpsIsStarted());
        values.put(DbHelper.COLUMN_LAST_UPDATE, formatDate(new Date()));

        int rowsUpdated = database.update(DbHelper.TABLE_DEVICES, values, DbHelper.COLUMN_REMOTE_DATABASE_ID + " = ?", new String[] {String.valueOf(device.getId())});
        final Cursor cursor = database.query(
                DbHelper.TABLE_DEVICES,
                null,
                DbHelper.COLUMN_ID + " = " + device.getId(),
                null,
                null,
                null,
                null);

        cursor.moveToFirst();
        final Device d = cursorToDevice(cursor);
        if (listener != null) listener.onDeviceUpdated(d);
    }

    public void removeDevice(int deviceRemoteId, OnDeviceRemovedListener listener) {
        open();
        int rowsRemoved = database.delete(DbHelper.TABLE_DEVICES, DbHelper.COLUMN_REMOTE_DATABASE_ID + " = ?", new String[] {String.valueOf(deviceRemoteId)});
        if (listener != null) listener.onDeviceRemoved();
    }

    public void getAllDevices(OnAllDevicesLoadedListener listener) {
        open();
        ArrayList<Device> toReturn = new ArrayList<>();
        Device device;
        Cursor cursor;

        try {
            cursor = database.query(
                    DbHelper.TABLE_DEVICES,    //TABLE NAME
                    null,
                    null,
                    null,
                    null,
                    null,
                    DbHelper.COLUMN_LAST_UPDATE + " ASC");

            cursor.moveToFirst();

            for(int i = 0, count = cursor.getCount(); i < count; i ++) {
                device = cursorToDevice(cursor);
                toReturn.add(device);
                if(cursor.isLast()) break;
                cursor.moveToNext();
            }

            cursor.close();
            if (listener != null) listener.onAllDevicesLoaded(toReturn);
        } catch (Exception e) {
            if (listener != null) listener.onAllDevicesLoaded(toReturn);
        }
    }

    private Device cursorToDevice(Cursor cursor) {
        Device device = new Device();

        //POZOR - s id z lokální databáze vůbec nepracuju.
        //Důležité je ID z externí databáze (ve sloupci "remote_id")
        device.setId(cursor.getInt(1));
        device.setAndroidId(cursor.getString(2));
        device.setDeviceId(cursor.getString(3));
        device.setToken(cursor.getString(4));
        device.setServiceIsStarted(cursor.getInt(5) == 1);
        device.setGpsIsStarted(cursor.getInt(6) == 1);
        device.setDateOfLastServiceUpdate(cursor.getString(7));

        return device;
    }

    public void getItemsCount(OnDevicesCountCheckedListener listener) {
        open();
        int toReturn = 0;
        String selectQuery = "SELECT COUNT(*) AS pocet FROM " + DbHelper.TABLE_DEVICES;
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) toReturn = cursor.getInt(0);
        cursor.close();
        if (listener != null) listener.onDevicesCountChecked(toReturn);
    }
}
