package com.example.loyalcustomer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = ">>>LoginActivity<<<";

    EditText inpUsername, inpPassword;

    Button btnLogin, btnChangePassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        btnLogin = findViewById(R.id.btnLogin);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        inpUsername = findViewById(R.id.inpUsername);
        inpPassword = findViewById(R.id.inpPassword);


        //
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (doLogin()) {
                    Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                    Intent intentList = new Intent(LoginActivity.this, ListActivity.class);
                    startActivity(intentList);
                } else
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();

            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ChangePasswordActivity.class));
            }
        });
    }

    private boolean doLogin() {
        try {
            // Lấy username và password từ input
            String username = inpUsername.getText().toString().trim();
            String password = inpPassword.getText().toString().trim();

            // Validate username và password (bổ sung)
            // Todo

            //Dùng ContentResolver để thao tác với dữ liệu
            ContentResolver contentResolver = getContentResolver();



            // Tạo URI cho truy vấn đăng nhập
            Uri loginUri = AccountProvider.LOGIN_URI;

            // Thực hiện truy vấn với ContentValues
            Cursor cursor = contentResolver.query(loginUri, null, null, new String[]{username, password}, null);



            boolean isValid = cursor != null && cursor.getCount() > 0;
            if (cursor != null) {
                cursor.close(); // Đảm bảo đóng cursor
            }
            return isValid; // Trả về true nếu đăng nhập thành công


        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Lỗi khi login: " + e.toString());
        }

        return false;
    }


}