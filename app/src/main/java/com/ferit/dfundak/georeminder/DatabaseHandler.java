package com.ferit.dfundak.georeminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Dora on 17/06/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static DatabaseHandler mDatabaseHandler = null;


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "geoReminder";
    private static final String TABLE_REMINDERS = "reminders";

    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LONG = "longitude";
    private static final String KEY_RADIUS = "radius";

    private DatabaseHandler(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHandler getInstance(Context context){
        if(mDatabaseHandler == null){
            mDatabaseHandler = new DatabaseHandler(context);
        }
        return mDatabaseHandler;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        this.onCreate(db);
    }

    //SQL statements
    static final String CREATE_TABLE = "CREATE TABLE " + TABLE_REMINDERS + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            KEY_TITLE + " TEXT," +
            KEY_DESCRIPTION + " TEXT,"+
            KEY_DATE + " TEXT," +
            KEY_TIME + " TEXT," +
            KEY_LAT + " REAL," +
            KEY_LONG + " REAL,"+
            KEY_RADIUS + " REAL" + ");";

    static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_REMINDERS;
    static final String SELECT_ALL = "SELECT " + KEY_ID + "," + KEY_TITLE + "," + KEY_DESCRIPTION + ","+ KEY_DATE + "," + KEY_TIME + "," + KEY_LAT + "," + KEY_LONG + ","+ KEY_RADIUS + " FROM " + TABLE_REMINDERS;

    public void insertReminder(reminderItem reminder){
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TITLE, reminder.getTitle());
        contentValues.put(KEY_DESCRIPTION, reminder.getDescription());
        contentValues.put(KEY_DATE, reminder.getDate());
        contentValues.put(KEY_TIME, reminder.getTime());
        contentValues.put(KEY_LAT, reminder.getLat());
        contentValues.put(KEY_LONG, reminder.getLong());
        contentValues.put(KEY_RADIUS, reminder.getRadius());

        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        long rowInserted = writableDatabase.insert(TABLE_REMINDERS, KEY_ID, contentValues);
        if(rowInserted != -1)
            Log.i("dora", "New row added, row id: " + rowInserted);
        else
            Log.i("dora", "SMTH WENT WRONG" + rowInserted);
        writableDatabase.close();
    }

    public void deleteFromTable (int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_REMINDERS + " WHERE " + KEY_ID + "='"+ id +"'");
        db.close();
    }

    public ArrayList<reminderItem> getAllReminders(){
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        Cursor reminderCursor = writableDatabase.rawQuery(SELECT_ALL, null);
        ArrayList<reminderItem> reminders = new ArrayList<>();
        if(reminderCursor.moveToFirst()){
            do{
                int id = reminderCursor.getInt(0);
                String title = reminderCursor.getString(1);
                String description = reminderCursor.getString(2);
                String date = reminderCursor.getString(3);
                String time = reminderCursor.getString(4);
                double lat = reminderCursor.getDouble(5);
                double lon = reminderCursor.getDouble(6);
                double radius = reminderCursor.getDouble(7);
                LatLng pinnedLocation = new LatLng(lat, lon);
                //LatLng pinnedLocation, float radius, String title, String description, String date, String time)
                reminders.add(new reminderItem(id, pinnedLocation, radius, title, description, date, time));

            }while(reminderCursor.moveToNext());
        }
        reminderCursor.close();
        writableDatabase.close();
        return reminders;
    }

}
