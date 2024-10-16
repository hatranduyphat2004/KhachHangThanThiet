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
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AccountProvider extends ContentProvider {
    private static final String AUTHORITY = "com.example.loyalcustomer.provider.account";
    private static final String PATH_ACCOUNT_LIST = "accounts";
    private static final String PATH_ACCOUNT_BY_ID = "account";
    private static final String PATH_LOGIN = "login";
    private static final String PATH_CHANGE_PASSWORD = "change_password";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_ACCOUNT_LIST);
    public static final Uri CONTENT_URI_BY_ID = Uri.parse("content://" + AUTHORITY + "/" + PATH_ACCOUNT_BY_ID);
    public static final Uri LOGIN_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_LOGIN);
    public static final Uri CHANGE_PASSWORD_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_CHANGE_PASSWORD);


    private SQLiteDatabase database;
    private static final int ACCOUNTS = 1;
    private static final int ACCOUNT = 2;
    private static final int LOGIN = 3;
    private static final int CHANGE_PASSWORD = 4;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {
        sURIMatcher.addURI(AUTHORITY, PATH_ACCOUNT_LIST, ACCOUNTS);
        sURIMatcher.addURI(AUTHORITY, PATH_ACCOUNT_BY_ID, ACCOUNT);
        sURIMatcher.addURI(AUTHORITY, PATH_LOGIN, LOGIN);
        sURIMatcher.addURI(AUTHORITY, PATH_CHANGE_PASSWORD, CHANGE_PASSWORD);
    }





    @Override
    public boolean onCreate() {
        DBHelper dbHelper = new DBHelper(getContext());

        database = dbHelper.getWritableDatabase();
        return (database != null);
    }

    @Nullable
    @Override
    public Cursor query( Uri uri,  String[] projection,  String selection,  String[] selectionArgs,  String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DBHelper.A_TABLE_NAME);

        int uriType = sURIMatcher.match(uri);


        switch (uriType) {
            case LOGIN:
                // Lấy username và password từ selectionArgs
                if (selectionArgs != null && selectionArgs.length == 2) {
                    String username = selectionArgs[0];
                    String password = selectionArgs[1];

                    // Thêm điều kiện vào câu truy vấn
                    queryBuilder.appendWhere("username = ? AND password = ?");
                    selectionArgs = new String[]{username, password}; // Cập nhật selectionArgs
                } else {
                    throw new IllegalArgumentException("Thông tin đăng nhập không hợp lệ");
                }
                break;
            case CHANGE_PASSWORD:
                break;
            case ACCOUNTS:
                break;
            case ACCOUNT:
                queryBuilder.appendWhere(DBHelper.A_COLUMN_ID + "=" + uri.getLastPathSegment());
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
        long id = database.insert(DBHelper.A_TABLE_NAME, null, values);
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
            case ACCOUNTS:
                rowsDeleted = database.delete(DBHelper.A_TABLE_NAME, selection, selectionArgs);
                break;
            case ACCOUNT:
                String id = uri.getLastPathSegment();
                if (id != null) {
                    rowsDeleted = database.delete(DBHelper.A_TABLE_NAME, DBHelper.A_COLUMN_ID + " = ?", new String[]{id});
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
            case CHANGE_PASSWORD:
                // Lấy thông tin từ ContentValues
                String username = values.getAsString("username");
                String oldPassword = values.getAsString("password");
                String newPassword = values.getAsString("new_password");



                // Kiểm tra xem mật khẩu cũ có đúng không
                String selectionQuery = "username = ? AND password = ?";
                String[] selectionArgsQuery = { username, oldPassword };


                Cursor cursor = database.query(
                        DBHelper.A_TABLE_NAME,
                        null,
                        selectionQuery,
                        selectionArgsQuery,
                        null,
                        null,
                        null
                );

                int rows = cursor.getCount();
                cursor.close();

                if (rows > 0) {
                    // Nếu mật khẩu cũ đúng, cập nhật mật khẩu mới
                    ContentValues newValues = new ContentValues();
                    newValues.put("password", newPassword);

                    // Cập nhật mật khẩu trong cơ sở dữ liệu
                    rowsUpdated = database.update(
                            DBHelper.A_TABLE_NAME,
                            newValues,
                            DBHelper.A_COLUMN_USERNAME + " = ?",
                            new String[]{username}
                    );

                } else throw new IllegalArgumentException("Tài khoản hoặc mật khẩu không hợp lệ");
                break;

            case ACCOUNTS:
                rowsUpdated = database.update(DBHelper.A_TABLE_NAME, values, selection, selectionArgs);
                break;
            case ACCOUNT:
                String id = uri.getLastPathSegment();
                if (id != null) {
                    rowsUpdated = database.update(DBHelper.A_TABLE_NAME, values, DBHelper.A_COLUMN_ID + " = ?", new String[]{id});
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }


    // Phương thức đổi mật khẩu
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        // Kiểm tra xem mật khẩu cũ có đúng không
        String selection = "username = ? AND password = ?";
        String[] selectionArgs = { username, oldPassword };

        Cursor cursor = database.query(
                DBHelper.A_TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.getCount() > 0) {
            // Nếu thông tin đúng, cập nhật mật khẩu mới
            ContentValues values = new ContentValues();
            values.put("password", newPassword);

            int rowsUpdated = database.update(
                    DBHelper.A_TABLE_NAME,
                    values,
                    "username = ?",
                    new String[]{username}
            );

            cursor.close();
            return rowsUpdated > 0;
        }

        cursor.close();
        return false;
    }

}
