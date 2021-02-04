package com.example.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final String COLUMN_ID = "COLUMN_ID";

    public static final String TEL_TABLE = "TEL_TABLE";

    public static final String COLUMN_TELNUM = "uuid";






    public DBHelper(@Nullable Context context) {
        super(context, "tel.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTablestatement = "CREATE TABLE " + TEL_TABLE+"" +
                " ("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_TELNUM+
                " TEXT)";

        db.execSQL(createTablestatement);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void addOne(String telNum){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();

        content.put(COLUMN_TELNUM, telNum);

        db.insert(TEL_TABLE, null, content);

    }

    public void deleteTable ()
    {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("delete from "+ TEL_TABLE);

    }

    public List<String> getAll(){

        List<String> allList = new ArrayList<>();

        String query = "SELECT * FROM " + TEL_TABLE;

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            do {
                //int id = cursor.getInt(0);
                String telNum = cursor.getString(1);








                allList.add(telNum);


            }
            while (cursor.moveToNext());

        }
        else{

        }

        cursor.close();
        db.close();
        return allList;
    }
}
