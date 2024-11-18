package com.example.baiktra;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "KhoanChi.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "khoanchi";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TYPE = "type";
    public static final  String COLUMN_AMOUNT = "amount";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_AMOUNT + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData(String id,String date, String type, String amount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, id);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_AMOUNT, amount);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    //Doc du lieu
    public Cursor getAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        return  db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public boolean isIDExist(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID}, COLUMN_ID + " = ?", new String[]{id}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }



}
