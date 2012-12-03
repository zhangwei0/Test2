package com.wl.magz.utils;

import com.wl.magz.view.BookshelfItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
    
    public static final String DATABASE_NAME = "Mags";
    private static final int VERSION = 1;
   
    private static DBHelper mInstance;
    private static SQLiteDatabase mDb;

    private static final String TABLE_MY_MAGS = "my_mags";

    public DBHelper(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mDb = db;
        createTables();
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        
    }
    
    //This should be called when the application start
    public static void init(Context context) {
        if (mInstance == null) {
            mInstance = new DBHelper(context, DATABASE_NAME, null,VERSION);
            mDb = mInstance.getWritableDatabase();
        }
    }
    
    private void createTables() {
        if (VERSION == 1) {
            createTable1();
        }
    }
    
    private void createTable1() {
        String CREATE_MY_MAGS = "CREATE TABLE "
                + TABLE_MY_MAGS + "(_id INTEGER PRIMARY KEY, private_key varchar(20) NOT NULL," +
                		" name varchar(20) NOT NULL,  path varchar(30)," +
                		" download_complete INTEGER NOT NULL DEFAULT 1," +
                		" progress INTEGER, read_time INTEGER NOT NULL)";
        
        mDb.execSQL(CREATE_MY_MAGS);
    }
    
    public static Cursor getMyMagzs() {
        String sql = "SELECT * FROM " + TABLE_MY_MAGS;
        return mDb.rawQuery(sql, null);
    }
    
    public static Cursor getRecentReads() {
        String sql = "SELECT * FROM " + TABLE_MY_MAGS + " ORDER BY read_time";
        return mDb.rawQuery(sql, null);
    }
    
    public static long addNewMag(BookshelfItem magz) {
        ContentValues cv = magz.toValues();
        return mDb.insert(TABLE_MY_MAGS, null, cv);
    }
    
    public static int updateMyMagz(BookshelfItem magz) {
        ContentValues cv = magz.toValues();
        return mDb.update(TABLE_MY_MAGS, cv, "_id=?", new String[] {String.valueOf(magz.mId)});
    }

    public static int deleteMyMagz(long id) {
        return mDb.delete(TABLE_MY_MAGS, "_id=?", new String[] {String.valueOf(id)});
    }

}
