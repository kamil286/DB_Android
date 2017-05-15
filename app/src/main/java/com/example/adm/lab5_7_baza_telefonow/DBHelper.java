package com.example.adm.lab5_7_baza_telefonow;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private Context mContext;
    public final static int DB_VERSION = 1;
    public final static String DB_NAME = "baza_telefonow";
    public final static String TABLE_NAME = "telefony";
    public final static String ID = "_id";
    public final static String MANUFACTURER = "producent";
    public final static String MODEL = "model";
    public final static String ANDROID_VERSION = "android";
    public final static String WWW = "www";
    public final static String LABEL = "DBHelper";
    public final static String DB_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + ID
            + " integer primary key autoincrement, " + MANUFACTURER + " text not null," + MODEL +
            " text not null," + ANDROID_VERSION + " text not null," + WWW + " text);";
    private static final String DB_REMOVE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DBHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DB_CREATE);
        onCreate(db);
    }


}
