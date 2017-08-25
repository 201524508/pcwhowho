package com.example.yuuuuu.sample;

/**
 * Created by s0woo on 2017-08-14.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBbookmark extends SQLiteOpenHelper {
    public DBbookmark (Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists Bookmark("
                + "name text, "
                + "sport text, "
                + "nation text);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists Bookmark";
        db.execSQL(sql);
        onCreate(db);
    }

    public void insert(String name, String sport, String nation) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put("id", i);
        values.put("name", name);
        values.put("sport", sport);
        values.put("nation", nation);
        db.insert("Bookmark", null, values);
        db.close();
    }

    public void delete(String name_, String sport_, String nation_) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete("Bookmark", "name=? and sport=? and nation=?", new String[] {name_, sport_, nation_});

        db.close();
    }

    public String getResult() {
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM Bookmark", null);

        while (cursor.moveToNext()) {
            result += "이름 : "
                    + cursor.getString(0)
                    + " | 종목 : "
                    + cursor.getString(1)
                    + " | 국가 : "
                    + cursor.getString(2)
                    + "\n";
        }

        return result;
    }

    public int getCount() {
        SQLiteDatabase db = getReadableDatabase();
        int i=0;

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM Bookmark", null);

        while (cursor.moveToNext()) {
            i++;
        }

        return i;
    }
}
