package com.example.yuuuuu.sample;

/**
 * Created by s0woo on 2017-08-25.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBalarm extends SQLiteOpenHelper {
    public DBalarm (Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists Alarm("
                + "day text, "
                + "sport text);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists Alarm";
        db.execSQL(sql);
        onCreate(db);
    }

    public void insert(String day, String sport) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("day", day);
        values.put("sport", sport);
        db.insert("Alarm", null, values);
        db.close();
    }

    public void delete(String day_, String sport_) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete("Alarm", "day=? and sport=?", new String[] {day_, sport_});

        db.close();
    }

    public String getResult() {
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM Alarm", null);

        while (cursor.moveToNext()) {
            result += "날짜: "
                    + cursor.getString(0)
                    + " | 종목 : "
                    + cursor.getString(1)
                    + "\n";
        }

        return result;
    }

}