package com.example.adm.lab5_7_baza_telefonow;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class DBProvider extends ContentProvider {

    private DBHelper mDBHelper;


    private static final String ID =
            "com.example.adm.lab5_6_baza_telefonow.DBProvider";

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + ID + "/" + DBHelper.TABLE_NAME);

    private static final int WHOLE_TABLE = 1;
    private static final int SELECTED_ROW = 2;

    private static final UriMatcher MATCHED_URI =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHED_URI.addURI(ID, DBHelper.TABLE_NAME,
                WHOLE_TABLE);
        MATCHED_URI.addURI(ID, DBHelper.TABLE_NAME +
                "/#", SELECTED_ROW);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = MATCHED_URI.match(uri);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        long addedId = 0;
        switch (uriType) {
            case WHOLE_TABLE:
                addedId = db.insert(DBHelper.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(DBHelper.TABLE_NAME + "/" + addedId);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        int uritype = MATCHED_URI.match(uri);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor cursor = null;
        switch (uritype) {
            case WHOLE_TABLE:
                cursor = db.query(false,
                        DBHelper.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder,
                        null,
                        null);
                break;
            case SELECTED_ROW:
                cursor = db.query(false, DBHelper.TABLE_NAME, projection,
                        addIdIntoSelection(selection, uri), selectionArgs, null,
                        null, sortOrder, null, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new DBHelper(getContext());
        return false;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = MATCHED_URI.match(uri);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int countOfDeleted = 0;
        switch (uriType) {
            case WHOLE_TABLE:
                countOfDeleted = db.delete(DBHelper.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case SELECTED_ROW:
                countOfDeleted = db.delete(DBHelper.TABLE_NAME,
                        addIdIntoSelection(selection, uri), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return countOfDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uritype = MATCHED_URI.match(uri);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int countOfUpdated = 0;
        switch (uritype) {
            case WHOLE_TABLE:
                countOfUpdated = db.update(DBHelper.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case SELECTED_ROW:
                countOfUpdated = db.update(DBHelper.TABLE_NAME,
                        values, addIdIntoSelection(selection, uri), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return countOfUpdated;
    }

    private String addIdIntoSelection(String selection, Uri uri)
    {
        if (selection!=null && !selection.equals(""))
            selection = selection + " and " + DBHelper.ID + "="
                    + uri.getLastPathSegment();
        else
            selection = DBHelper.ID + "=" +
                    uri.getLastPathSegment();
        return selection;
    }
}
