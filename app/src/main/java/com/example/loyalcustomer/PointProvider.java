package com.example.loyalcustomer;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PointProvider extends ContentProvider {
    private static final String AUTHORITY = "com.example.loyalcustomer.provider";
    private static final String PATH_POINT_LIST = "points";
    private static final String PATH_POINT_BY_ID = "point";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_POINT_LIST);
    public static final Uri CONTENT_URI_BY_ID = Uri.parse("content://" + AUTHORITY + "/" + PATH_POINT_BY_ID);

    private SQLiteDatabase database;
    private static final int POINTS = 1;
    private static final int POINT = 2;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {
        sURIMatcher.addURI(AUTHORITY, PATH_POINT_LIST, POINTS);
        sURIMatcher.addURI(AUTHORITY, PATH_POINT_BY_ID, POINT);
    }





    @Override
    public boolean onCreate() {
        DBHelper dbHelper = new DBHelper(getContext());
        database = dbHelper.getWritableDatabase();
        return (database != null);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DBHelper.P_TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case POINTS:
                break;
            case POINT:
                queryBuilder.appendWhere(DBHelper.P_COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = database.insert(DBHelper.P_TABLE_NAME, null, values);
        if (id > 0) {
            Uri pointUri = ContentUris.withAppendedId(CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(pointUri, null);
            return pointUri;
        } else {
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted = 0;
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case POINTS:
                rowsDeleted = database.delete(DBHelper.P_TABLE_NAME, selection, selectionArgs);
                break;
            case POINT:
                String id = uri.getLastPathSegment();
                if (id != null) {
                    rowsDeleted = database.delete(DBHelper.P_TABLE_NAME, DBHelper.P_COLUMN_ID + " = ?", new String[]{id});
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsUpdated = 0;
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case POINTS:
                rowsUpdated = database.update(DBHelper.P_TABLE_NAME, values, selection, selectionArgs);
                break;
            case POINT:
                String id = uri.getLastPathSegment();
                if (id != null) {
                    rowsUpdated = database.update(DBHelper.P_TABLE_NAME, values, DBHelper.P_COLUMN_ID + " = ?", new String[]{id});
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }


}
