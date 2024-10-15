package com.example.loyalcustomer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "loyalcustomer.db";
    public static final int DATABASE_VERSION = 1;

    public static final String C_TABLE_NAME = "customer";
    public static final String C_COLUMN_ID = "id";
    public static final String C_COLUMN_PHONE = "phone";

    public static final String P_TABLE_NAME = "point";
    public static final String P_COLUMN_ID = "id";
    public static final String P_COLUMN_CURRENT_POINT = "current_point";
    public static final String P_COLUMN_USED_POINT = "used_point";
    public static final String P_COLUMN_NOTE = "note";
    public static final String P_COLUMN_CREATED_AT = "createdAt";
    public static final String P_COLUMN_UPDATED_AT = "updatedAt";
    public static final String P_COLUMN_CUSTOMER_ID = "customer_id";


    public static final String A_TABLE_NAME = "account";
    public static final String A_COLUMN_ID = "id";
    public static final String A_COLUMN_USERNAME = "username";
    public static final String A_COLUMN_PASSWORD = "password";
    public static final String A_COLUMN_STATUS = "status";
//    CREATE TABLE customer (
//            id INTEGER PRIMARY KEY AUTOINCREMENT,
//            phone TEXT NOT NULL,
//            username TEXT NOT NULL,
//            password TEXT NOT NULL
//    );
//
//
//    CREATE TABLE point (
//            id INTEGER PRIMARY KEY AUTOINCREMENT,
//            customer_id INTEGER,
//            current_point INTEGER DEFAULT 0,
//            used_point INTEGER DEFAULT 0,
//            note TEXT,
//            createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
//            updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
//            FOREIGN KEY (customer_id) REFERENCES customer(id)
//            );

    //Câu SQL để tạo bảng customer
    private static final String C_TABLE_CREATE = "CREATE TABLE " + C_TABLE_NAME + " (" +
            C_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            C_COLUMN_PHONE + " TEXT NOT NULL);";

    //Câu SQL để tạo bảng point
    private static final String P_TABLE_CREATE = "CREATE TABLE " + P_TABLE_NAME + " (" +
            P_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            P_COLUMN_CUSTOMER_ID + " INTEGER, " +
            P_COLUMN_CURRENT_POINT + " INTEGER DEFAULT 0, " +
            P_COLUMN_USED_POINT + " INTEGER DEFAULT 0, " +
            P_COLUMN_NOTE + " TEXT, " +
            P_COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            P_COLUMN_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            " FOREIGN KEY (" + P_COLUMN_CUSTOMER_ID + ") REFERENCES " + C_TABLE_NAME + "(" + C_COLUMN_ID + "));";

    //Câu SQL để tạo bảng account
    private static final String A_TABLE_CREATE = "CREATE TABLE " + A_TABLE_NAME + " (" +
            A_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            A_COLUMN_USERNAME + " TEXT NOT NULL, " +
            A_COLUMN_PASSWORD + " TEXT NOT NULL, " +
            A_COLUMN_STATUS + " INTEGER DEFAULT 1);";




    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(C_TABLE_CREATE);
        db.execSQL(P_TABLE_CREATE);
        db.execSQL(A_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + C_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + P_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + A_TABLE_NAME);
        onCreate(db);
    }
}
