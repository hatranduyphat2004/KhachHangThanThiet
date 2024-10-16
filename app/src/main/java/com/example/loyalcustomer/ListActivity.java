package com.example.loyalcustomer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private ListView lvCustomer;
    private CustomerArrayAdapter myAdapter;
    private ArrayList<MainModel> customers;
    private TextView emptyList;

    private Button btnInputPoint, btnUsePoint, btnList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initData();
        initView();
        initHandleClickOnBtn();
    }


    private void initData() {
        customers = new ArrayList<MainModel>();
        customers = getCustomers();

    }
    private ArrayList<MainModel> getCustomers() {
        ArrayList<MainModel> ls = new ArrayList<MainModel>();

        try {
            //Dùng ContentResolver để thao tác với dữ liệu
            ContentResolver contentResolver = getContentResolver();

            Uri uri = PointProvider.POINTS_WITH_CUSTOMER_URI;

            //Kiểu sắp xếp (nên để theo thời gian giảm dần)
            String sortOrder = DBHelper.P_COLUMN_CREATED_AT + " DESC";

            //Cho cursor chạy để tìm hàng dữ liệu thỏa với điều kiện trong database
            Cursor cursor = contentResolver.query(uri, null, null, null, sortOrder);


            if (cursor != null && cursor.moveToFirst()) {
                do {

                    String phone = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.C_COLUMN_PHONE));
                    int point = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.P_COLUMN_CURRENT_POINT));
                    int usedPoint = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.P_COLUMN_USED_POINT));
                    String note = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.P_COLUMN_NOTE));
                    LocalDateTime timeCreated = Utils.StringToLocalDate(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.P_COLUMN_CREATED_AT)));
                    LocalDateTime timeUpdated = Utils.StringToLocalDate(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.P_COLUMN_UPDATED_AT)));
                    ls.add(new MainModel(phone, point, usedPoint, note, timeCreated, timeUpdated));
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch(Exception e) {
            Log.d(">>> ListActivity <<<", "Lỗi khi lấy danh sách point: " + e.toString());
        }
    return ls;
    }

    private void initView() {
        btnInputPoint = findViewById(R.id.l_btnInputPoint);
        btnUsePoint = findViewById(R.id.l_btnUsePoint);
        btnList = findViewById(R.id.l_btnList);
        emptyList = findViewById(R.id.emptyList);
        lvCustomer = findViewById(R.id.lvCustomer);
        myAdapter = new CustomerArrayAdapter(ListActivity.this, R.layout.item, customers);
        lvCustomer.setAdapter(myAdapter);
        if (customers.size() == 0) {
            emptyList.setVisibility(View.VISIBLE);
            lvCustomer.setVisibility(View.INVISIBLE);
        } else {
            emptyList.setVisibility(View.INVISIBLE);
            lvCustomer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        initView();
        myAdapter.notifyDataSetChanged();
    }
    private void initHandleClickOnBtn() {
        btnInputPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(ListActivity.this, InputPointActivity.class);
            }
        });
        btnUsePoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(ListActivity.this, UsePointActivity.class);
            }
        });
    }


    // hàm mở activity
    private void openActivity(Activity src, Class des){
        Intent intent1 = new Intent(src, des);
        startActivity(intent1);

    }}