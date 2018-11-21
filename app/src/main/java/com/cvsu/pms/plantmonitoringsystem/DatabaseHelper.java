package com.cvsu.pms.plantmonitoringsystem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "config.db";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void queryData(String sql) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void insertData(String name, String orderTime, String startTime, String endTime, String duration) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO config_table VALUES(null, ?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1,name);
        statement.bindString(1,orderTime);
        statement.bindString(1,startTime);
        statement.bindString(1,endTime);
        statement.bindString(1,duration);

        statement.executeInsert();
    }

    public void updateData(String name, int orderTime, String startTime, String endTime, String duration) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE config_table SET orderTime = ?, startTime = ?, endTime = ?, duration = ? WHERE name = ?";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindDouble(1,(double) orderTime);
        statement.bindString(1,startTime);
        statement.bindString(1,endTime);
        statement.bindString(1,duration);
        statement.bindString(1,name);

        statement.execute();
        database.close();
    }

    public void deleteData(int orderTime) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM config_table WHERE orderTime = ?";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindDouble(1,(double) orderTime);

        statement.execute();
        database.close();
    }

    public void deleteConfig(String name) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM config_table WHERE name = ?";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, name);

        statement.execute();
        database.close();
    }

    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
