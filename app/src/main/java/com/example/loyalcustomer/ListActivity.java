package com.example.loyalcustomer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private ListView lvCustomer;
    private CustomerArrayAdapter myAdapter;
    private ArrayList<CustomerModel> customers;


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
        customers = new ArrayList<CustomerModel>();
        int count = 30;
        // Tạo đối tượng LocalDate
        LocalDate localDate = LocalDate.now();

        // Chuyển LocalDate sang LocalDateTime bằng cách kết hợp với LocalTime
        LocalDateTime dtNoew = localDate.atTime(LocalTime.now());
        for (int i = 0; i < count; i++) {
            customers.add(new CustomerModel(
                    "0799664334", 10,
                    "tui la phat \ntui la phat \ntui la phat \n",
                    dtNoew, dtNoew, "active"
                    )
            );

        }

    }
    private void initView() {
        btnInputPoint = findViewById(R.id.l_btnInputPoint);
        btnUsePoint = findViewById(R.id.l_btnUsePoint);
        btnList = findViewById(R.id.l_btnList);

        lvCustomer = findViewById(R.id.lvCustomer);
        myAdapter = new CustomerArrayAdapter(ListActivity.this, R.layout.item, customers);
        lvCustomer.setAdapter(myAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
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