package com.example.loyalcustomer;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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

import java.time.LocalDateTime;

public class UsePointActivity extends AppCompatActivity {
    Button u_btnSave, u_btnSaveNext, u_btnInputPoint, u_btnList;
    EditText u_inpPhone, u_inpCurrentPoint, u_inpUsePoint, u_inpNote;
    private int u_idPoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_use_point);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        u_btnSaveNext = findViewById(R.id.u_btnSaveNext);
        u_btnSave = findViewById(R.id.u_btnSave);
        u_btnInputPoint = findViewById(R.id.u_btnInputPoint);
        u_btnList = findViewById(R.id.u_btnList);

        u_inpPhone = findViewById(R.id.u_inpPhone);
        u_inpCurrentPoint = findViewById(R.id.u_inpCurrentPoint);
        u_inpUsePoint = findViewById(R.id.u_inpUsePoint);
        u_inpNote = findViewById(R.id.u_inpNote);


        // Thiết lập OnFocusChangeListener
        u_inpPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // Khi EditText mất focus
                    String text = u_inpPhone.getText().toString();
                    // Thực hiện hành động cần thiết
                    handleFocusLost(text);
                }
            }
        });
        u_btnSave.setOnClickListener(v-> {
            if (handleSave())
                finish();
        });

        u_btnSaveNext.setOnClickListener(v -> {
            if (handleSave())
                reset();
        });


        u_btnList.setOnClickListener(v -> {
            if (!u_inpPhone.getText().toString().isEmpty()) {
                goActivity(ListActivity.class);
                return;
            }
            startActivity(new Intent(UsePointActivity.this, ListActivity.class));

        });
        u_btnInputPoint.setOnClickListener(v -> {
            if (!u_inpPhone.getText().toString().isEmpty()) {
                goActivity(UsePointActivity.class);
                return;
            }
            startActivity(new Intent(UsePointActivity.this, InputPointActivity.class));

        });

    }
    private void goActivity(Class c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UsePointActivity.this);
        builder.setMessage("Are you sure ?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Xử lý khi người dùng chọn "Yes"
                reset();
                startActivity(new Intent(UsePointActivity.this, c));
            }
        });

        // Thêm nút No
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void handleFocusLost(String text) {
        // Xử lý sự kiện khi EditText mất focus
        try {
            //Dùng ContentResolver để thao tác với dữ liệu
            ContentResolver contentResolver = getContentResolver();

            Uri uri = PointProvider.POINT_BY_PHONE_URI;

            String selection = DBHelper.C_COLUMN_PHONE + " = ?";
            String[] selectionArgs = new String[]{text};

            //Cho cursor chạy để tìm hàng dữ liệu thỏa với điều kiện trong database
            Cursor cursor = contentResolver.query(uri, null, selection, selectionArgs, null);

            int point = -1;
            int idP = -1;
            String noteT = "";
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    point = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.P_COLUMN_CURRENT_POINT));
                    idP = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.P_COLUMN_ID));
                    noteT = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.P_COLUMN_NOTE));
                } while (cursor.moveToNext());
                cursor.close();
            }
            if (point != -1) {
                u_idPoint = idP;
                u_inpCurrentPoint.setText(point+"");
                u_inpNote.setText(noteT);
                u_inpCurrentPoint.setTextColor(Color.GRAY);

            }
            else {
                u_inpCurrentPoint.setText("Phone doesn't exist");
                u_inpCurrentPoint.setTextColor(Color.RED);
                u_inpNote.setText("");
            }
        } catch(Exception e) {
            Log.d(">>> UsePOintActivity <<<", "Lỗi khi lấy point by phone: " + e.toString());
        }
    }

    private boolean handleSave() {
        int currentPoint = Integer.parseInt(u_inpCurrentPoint.getText().toString());
        int usePoint = Integer.parseInt(u_inpUsePoint.getText().toString());
        String note = u_inpNote.getText().toString();
        LocalDateTime now = LocalDateTime.now();

        if (usePoint > currentPoint) {
            Toast.makeText(getApplicationContext(), "Not enough points!", Toast.LENGTH_SHORT).show();
            return false;
        }


        try {
            ContentResolver contentResolver = getContentResolver();

            Uri uri = PointProvider.CONTENT_URI_BY_ID;
            String selection = "id = ?";
            String[] selectionArgs = new String[]{u_idPoint + ""};

            ContentValues values = new ContentValues();
            values.put(DBHelper.P_COLUMN_ID, u_idPoint+"");
            values.put(DBHelper.P_COLUMN_UPDATED_AT, Utils.LocalDateToString(now));
            values.put(DBHelper.P_COLUMN_CURRENT_POINT, (currentPoint - usePoint));
            values.put(DBHelper.P_COLUMN_NOTE, note);

            int rowsUpdated = contentResolver.update(uri, values, selection, selectionArgs);
        } catch (Exception e) {
            Log.d(">>> UsePointActivity <<<", e.toString());
        }

        Toast.makeText(getApplicationContext(), "Point used!", Toast.LENGTH_SHORT).show();
        return true;
    }

    private void reset() {

        u_inpPhone.setText("");
        u_inpNote.setText("");
        u_inpCurrentPoint.setText("");
        u_inpUsePoint.setText("");
        u_idPoint = -1;
        u_inpPhone.requestFocus();
    }

}