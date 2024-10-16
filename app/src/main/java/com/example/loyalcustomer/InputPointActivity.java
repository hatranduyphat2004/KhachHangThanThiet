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

public class InputPointActivity extends AppCompatActivity {

    EditText inpPhone, inpCurrentPoint, inpAddPoint, inpNote;
    Button btnSave, btnSaveNext, i_btnList, i_btnUsePoint;
    private int idPoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_input_point);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        inpPhone = findViewById(R.id.inpPhone);
        inpCurrentPoint = findViewById(R.id.inpCurrentPoint);
        inpAddPoint = findViewById(R.id.inpAddPoint);
        inpNote = findViewById(R.id.inpNote);
        btnSave = findViewById(R.id.btnSave);
        btnSaveNext = findViewById(R.id.btnSaveNext);
        i_btnList = findViewById(R.id.i_btnList);
        i_btnUsePoint = findViewById(R.id.i_btnUsePoint);

        // Thiết lập OnFocusChangeListener
        inpPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // Khi EditText mất focus
                    String text = inpPhone.getText().toString();
                    // Thực hiện hành động cần thiết
                    handleFocusLost(text);
                }
            }
        });

        btnSave.setOnClickListener(v-> {
            handleSave();
            finish();
        });

        btnSaveNext.setOnClickListener(v -> {
            handleSave();
            reset();
        });

        i_btnList.setOnClickListener(v -> {
            if (!inpPhone.getText().toString().isEmpty()) {
                goActivity(ListActivity.class);
                return;
            }
            startActivity(new Intent(InputPointActivity.this, ListActivity.class));

        });
        i_btnUsePoint.setOnClickListener(v -> {
            if (!inpPhone.getText().toString().isEmpty()) {
                goActivity(UsePointActivity.class);
                return;
            }
            startActivity(new Intent(InputPointActivity.this, UsePointActivity.class));

        });
    }

    private void goActivity(Class c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(InputPointActivity.this);
        builder.setMessage("Are you sure ?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Xử lý khi người dùng chọn "Yes"
                reset();
                startActivity(new Intent(InputPointActivity.this, c));
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
                idPoint = idP;
                inpCurrentPoint.setText(point+"");
                inpNote.setText(noteT);
                inpCurrentPoint.setTextColor(Color.GRAY);

            }
            else {
                inpCurrentPoint.setText("Phone doesn't exist");
                inpCurrentPoint.setTextColor(Color.RED);
                inpNote.setText("");
            }
        } catch(Exception e) {
            Log.d(">>> InputPOintActivity <<<", "Lỗi khi lấy point by phone: " + e.toString());
        }
    }

    private void handleSave() {

        int currentPoint = Integer.parseInt(inpCurrentPoint.getText().toString());
        int addPoint = Integer.parseInt(inpAddPoint.getText().toString());
        String note = inpNote.getText().toString();
        LocalDateTime now = LocalDateTime.now();

        Log.d(">>> InputPointActivity <<<", "Now is: " + now);


        try {
            ContentResolver contentResolver = getContentResolver();

            Uri uri = PointProvider.CONTENT_URI_BY_ID;
            String selection = "id = ?";
            String[] selectionArgs = new String[]{idPoint + ""};

            ContentValues values = new ContentValues();
            values.put(DBHelper.P_COLUMN_ID, idPoint+"");
            values.put(DBHelper.P_COLUMN_UPDATED_AT, Utils.LocalDateToString(now));
            values.put(DBHelper.P_COLUMN_CURRENT_POINT, (currentPoint + addPoint));
            values.put(DBHelper.P_COLUMN_NOTE, note);

            int rowsUpdated = contentResolver.update(uri, values, selection, selectionArgs);
        } catch (Exception e) {
            Log.d("INputPointActivity", e.toString());
        }

        Toast.makeText(getApplicationContext(), "Point saved!", Toast.LENGTH_SHORT).show();
    }

    private void reset() {

        inpPhone.setText("");
        inpNote.setText("");
        inpCurrentPoint.setText("");
        inpAddPoint.setText("");
        idPoint = -1;
        inpPhone.requestFocus();
    }
}