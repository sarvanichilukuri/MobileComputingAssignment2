package com.example.locationandsymptommonitoringapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "Login.db";

    public static final String PASS_PHRASE = "Demo@123";

    public DBHelper(Context context){
        super(context, "Login.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("CREATE TABLE users(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` TEXT, `username` TEXT NOT NULL, `password` TEXT NOT NULL )");
        db.execSQL("CREATE TABLE symptomAndLocationTracking(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` TEXT NOT NULL, `longitude` TEXT NOT NULL, `latitude` TEXT NOT NULL,  `heartRate` REAL NOT NULL, `breathingRate` REAL NOT NULL, `fever` REAL NOT NULL, `cough` REAL NOT NULL, `tiredness` REAL NOT NULL, `shortnessOfBreath` REAL NOT NULL, `muscleAches` REAL NOT NULL, `nausea` REAL NOT NULL, `soreThroat` REAL NOT NULL, `diarrhea` REAL NOT NULL, `headache` REAL NOT NULL, `lossOfSmellOrTaste` REAL NOT NULL )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE if exists users");
        db.execSQL("DROP TABLE if exists symptomAndLocationTracking");
    }

    public boolean insertData(String username, String password){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase(PASS_PHRASE);
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        long result = sqLiteDatabase.insert("users", null, contentValues);
        if(result == -1) return false;
        else {
           // PASS_PHRASE = password;
            return true;
        }

    }

    public boolean insertLocationData(String longitude, String latitude, String date){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase(PASS_PHRASE);
        ContentValues contentValues = new ContentValues();
        contentValues.put("longitude", longitude);
        contentValues.put("latitude", latitude);
        contentValues.put("timestamp", date);
        //new Date(System.currentTimeMillis())
        long result = sqLiteDatabase.insert("symptomAndLocationTracking", null, contentValues);
        if(result == -1) return false;
        else
            return true;

    }

    @SuppressLint("Range")
public UserInfo getLatestRowFromLocationNdSymptomsData(){
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase(PASS_PHRASE);
    Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM symptomAndLocationTracking where timestamp=(SELECT MAX(timestamp) FROM symptomAndLocationTracking)", null);
    UserInfo userInfo = new UserInfo();

    if(cursor.getCount() > 0){
        userInfo.id = (cursor.getInt(cursor.getColumnIndex("id")));
        userInfo.breathingRate = (cursor.getFloat(cursor.getColumnIndex("breathingRate")));
        userInfo.cough = (cursor.getFloat(cursor.getColumnIndex("cough")));
        userInfo.diarrhea = (cursor.getFloat(cursor.getColumnIndex("diarrhea")));
        userInfo.fever = (cursor.getFloat(cursor.getColumnIndex("fever")));
        userInfo.headache = (cursor.getFloat(cursor.getColumnIndex("headache")));
        userInfo.heartRate = (cursor.getFloat(cursor.getColumnIndex("heartRate")));
        userInfo.lossOfSmellOrTaste = (cursor.getFloat(cursor.getColumnIndex("lossOfSmellOrTaste")));
    }

    return userInfo;
}
    public boolean insertLocationNdSymptomsData(UserInfo userInfo){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase(PASS_PHRASE);
        ContentValues contentValues = new ContentValues();
        contentValues.put("longitude", userInfo.longitude);
        contentValues.put("latitude", userInfo.latitude);
        contentValues.put("timestamp", String.valueOf(userInfo.timestamp));
        contentValues.put("heartRate", userInfo.heartRate);
        contentValues.put("breathingRate", userInfo.breathingRate);
        contentValues.put("fever", userInfo.fever);
        contentValues.put("cough", userInfo.cough);
        contentValues.put("tiredness", userInfo.tiredness);
        contentValues.put("shortnessOfBreath", userInfo.shortnessOfBreath);
        contentValues.put("muscleAches", userInfo.muscleAches);
        contentValues.put("nausea", userInfo.nausea);
        contentValues.put("soreThroat", userInfo.soreThroat);
        contentValues.put("diarrhea", userInfo.diarrhea);
        contentValues.put("headache", userInfo.headache);
        contentValues.put("lossOfSmellOrTaste", userInfo.lossOfSmellOrTaste);
        if(userInfo.id != -1){
            contentValues.put("id", userInfo.id);
        }
        //new Date(System.currentTimeMillis())
        long result = sqLiteDatabase.insert("symptomAndLocationTracking", null, contentValues);
        if(result == -1) return false;
        else
            return true;

    }

    public boolean checkusername(String username){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase(PASS_PHRASE);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * from users where username = ?", new String[]{username});
        if(cursor.getCount() > 0){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean checkusernamepassword(String username, String password){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase(PASS_PHRASE);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * from users where username = ? and password = ? ", new String[]{username, password});
        if(cursor.getCount() > 0){
            //PASS_PHRASE = password;
            return true;
        }
        else{
            return false;
        }
    }

}
