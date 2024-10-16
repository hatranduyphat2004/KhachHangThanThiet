package com.example.loyalcustomer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText inpUsername,inpOldPassword, inpNewPassword, inpConfirmPassword;
    private Button btnChangePassword, btnBack;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnBack = findViewById(R.id.btnBack);
        inpOldPassword = findViewById(R.id.inpOldPassword);
        inpNewPassword = findViewById(R.id.inpNewPassword);
        inpConfirmPassword = findViewById(R.id.inpConfirmPassword);
        inpUsername = findViewById(R.id.inpUsername);

        btnBack.setOnClickListener(v -> {
            finish();
        });
        btnChangePassword.setOnClickListener(v -> {
            if (doChangePassword()) {
                Toast.makeText(ChangePasswordActivity.this, "Change password success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            } else
                Toast.makeText(ChangePasswordActivity.this, "Change password failed", Toast.LENGTH_SHORT).show();

        });

    }

    private boolean doChangePassword() {
        try {
            String username = inpUsername.getText().toString();
            String oldPwd = inpOldPassword.getText().toString();
            String newPwd = inpNewPassword.getText().toString();
            String confirmPwd = inpConfirmPassword.getText().toString();

            // Kiểm tra mật khẩu mới và xác nhận có khớp không
            if (!newPwd.equals(confirmPwd)) {
                Toast.makeText(ChangePasswordActivity.this, "Mật khẩu xác nhận không khớp với mật khẩu mới", Toast.LENGTH_SHORT).show();
                return false;
            }

            ContentResolver contentResolver = getContentResolver();

            Uri uri = AccountProvider.CHANGE_PASSWORD_URI;


            ContentValues values = new ContentValues();

            values.put(DBHelper.A_COLUMN_USERNAME, username);
            values.put(DBHelper.A_COLUMN_PASSWORD, oldPwd);
            values.put("new_password", newPwd);

            int rowsUpdated = contentResolver.update(uri, values, null, null);

            return rowsUpdated == 1;

        } catch (Exception e) {
            Log.d(">>> ChangePassword <<<", "Lỗi khi đổi mật khẩu: " + e.toString());
        }


        return false;
    }




}


