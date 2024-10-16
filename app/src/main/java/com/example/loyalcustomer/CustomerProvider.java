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

public class CustomerProvider extends ContentProvider {
    private static final String AUTHORITY = "com.example.loyalcustomer.provider.customer";
    private static final String PATH_CUSTOMER_LIST = "customers";
    private static final String PATH_CUSTOMER_BY_ID = "customer";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_CUSTOMER_LIST);
    public static final Uri CONTENT_URI_BY_ID = Uri.parse("content://" + AUTHORITY + "/" + PATH_CUSTOMER_BY_ID);

    private SQLiteDatabase database;
    private static final int CUSTOMERS = 1;
    private static final int CUSTOMER = 2;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {
        sURIMatcher.addURI(AUTHORITY, PATH_CUSTOMER_LIST, CUSTOMERS);
        sURIMatcher.addURI(AUTHORITY, PATH_CUSTOMER_BY_ID, CUSTOMER);
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
        queryBuilder.setTables(DBHelper.C_TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case CUSTOMERS:
                break;
            case CUSTOMER:
                queryBuilder.appendWhere(DBHelper.C_COLUMN_ID + "=" + uri.getLastPathSegment());
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
        long id = database.insert(DBHelper.C_TABLE_NAME, null, values);
        if (id > 0) {
            Uri customerUri = ContentUris.withAppendedId(CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(customerUri, null);
            return customerUri;
        } else {
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted = 0;
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case CUSTOMERS:
                rowsDeleted = database.delete(DBHelper.C_TABLE_NAME, selection, selectionArgs);
                break;
            case CUSTOMER:
                String id = uri.getLastPathSegment();
                if (id != null) {
                    rowsDeleted = database.delete(DBHelper.C_TABLE_NAME, DBHelper.C_COLUMN_ID + " = ?", new String[]{id});
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
            case CUSTOMERS:
                rowsUpdated = database.update(DBHelper.C_TABLE_NAME, values, selection, selectionArgs);
                break;
            case CUSTOMER:
                String id = uri.getLastPathSegment();
                if (id != null) {
                    rowsUpdated = database.update(DBHelper.C_TABLE_NAME, values, DBHelper.C_COLUMN_ID + " = ?", new String[]{id});
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
