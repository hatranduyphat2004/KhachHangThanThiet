package com.example.loyalcustomer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;
    private ListView lvCustomer;
    private CustomerArrayAdapter myAdapter;
    private ArrayList<MainModel> customers;
    private TextView emptyList;

    private Button btnInputPoint, btnUsePoint, btnList, btnLogout, btnExport, btnImport;
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
        btnLogout = findViewById(R.id.btnLogout);
        btnList = findViewById(R.id.l_btnList);
        btnExport = findViewById(R.id.btnExport);
        btnImport = findViewById(R.id.btnImport);
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
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logout();
            }
        });
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveXmlToExternalStorage(ListActivity.this);
            }
        });
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*"); // Đặt kiểu file là tất cả các loại. Bạn có thể thay đổi thành loại cụ thể như "application/xml"
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(Intent.createChooser(intent, "Chọn file"), 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Nhận URI của file đã chọn
            Uri uri = data.getData();

            if (uri != null) {
                String filePath = uri.getPath();
                // Xử lý file theo URI đã nhận
                Toast.makeText(this, "File đã chọn: " + filePath, Toast.LENGTH_SHORT).show();

                // Nếu bạn muốn import file .xml, gọi hàm xử lý tại đây
                if(importXmlFromUri(ListActivity.this,filePath))
                    Toast.makeText(this, "Imported successfully", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Import failed", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private ArrayList<MainModel> readFileXML(Context context, String inpFilePath) {
        ArrayList<MainModel> rs = new ArrayList<>();
        String filePath = inpFilePath.replace("/document/raw:", "");
        File file = new File(filePath);


        if (!file.exists()) {
            Log.d(">>>> Check read file <<<<<", "File not found: " + filePath);
            return rs;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(fis, null);
            int eventType = parser.getEventType();
            MainModel data = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equals("customer")) {
                            data = new MainModel();
                        } else if (data != null) {
                            switch (tagName) {
                                case "phone":
                                    data.setPhone(parser.nextText());
                                    break;
                                case "points":
                                    data.setPoint(Integer.parseInt(parser.nextText()));
                                    break;
                                case "usedPoint":
                                    data.setUsedPoint(Integer.parseInt(parser.nextText()));
                                    break;
                                case "note":
                                    data.setNote(parser.nextText());
                                    break;
                                case "createdAt":
                                    data.setCreatedAt(Utils.StringToLocalDate(parser.nextText()));
                                    break;
                                case "updatedAt":
                                    data.setUpdatedAt(Utils.StringToLocalDate(parser.nextText()));
                                    break;
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagName.equals("customer") && data != null) {
                            rs.add(data);
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            Log.d(">>>>>Check read file xml<<<", e.toString());
            e.printStackTrace();
        }

        return rs;
    }

    public boolean importXmlFromUri(Context context, String filePath) {
        boolean rs = true;
        ArrayList<MainModel> ls = readFileXML(context, filePath);


        for (MainModel m : ls) {
            try {
                Uri uri = CustomerProvider.CONTENT_URI;
                Uri uri2 = PointProvider.CONTENT_URI;

                //Thêm các giá trị cần lưu vào ContentValues
                ContentValues values = new ContentValues();
                values.put("phone", m.getPhone());

                //Dùng ContentResolver gọi hàm insert để thêm Customer
                Uri rsUri = getContentResolver().insert(uri, values);

                ContentValues values2 = new ContentValues();
                // asy id Customer vừa thêm
                int cId = Integer.parseInt(rsUri.toString().replace("content://com.example.loyalcustomer.provider.customer/customers/", ""));


                values2.put("customer_id", cId);
                values2.put("current_point", m.getPoint());
                values2.put("used_point", m.getUsedPoint());
                values2.put("note", m.getNote());
                values2.put("createdAt", Utils.LocalDateToString(m.getCreatedAt()));
                values2.put("updatedAt", Utils.LocalDateToString(m.getUpdatedAt()));


                getContentResolver().insert(uri2, values2);

            }

            catch(Exception e) {
                Log.d(">>>>Check insert<<<", e.toString());

                rs = false;
            }

        }


        return rs;
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
        builder.setMessage("Are you sure ?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Xử lý khi người dùng chọn "Yes"
                Toast.makeText(ListActivity.this, "Logout success", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(ListActivity.this, LoginActivity.class));
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
    // hàm mở activity
    private void openActivity(Activity src, Class des){
        Intent intent1 = new Intent(src, des);
        startActivity(intent1);

    }



    public void exportToXML(ArrayList<MainModel> customers) {
        try {
            LocalDateTime now = LocalDateTime.now();
            // Chuyển đổi LocalDateTime sang số giây từ Epoch Time
            long seconds = now.toEpochSecond(ZoneOffset.UTC);

            // Tạo file lưu XML trong bộ nhớ trong của thiết bị
            FileOutputStream fos = openFileOutput("customer_points_list" + seconds + ".xml", MODE_PRIVATE);

            // Tạo XmlSerializer
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();

            // Bắt đầu tiến trình ghi XML
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);

            // Ghi root tag <customers>
            xmlSerializer.startTag(null, "customers");

            // Ghi từng khách hàng
            for (MainModel customer : customers) {
                xmlSerializer.startTag(null, "customer");

                // Ghi thẻ <phone>
                xmlSerializer.startTag(null, "phone");
                xmlSerializer.text(customer.getPhone());
                xmlSerializer.endTag(null, "phone");

                // Ghi thẻ <points>
                xmlSerializer.startTag(null, "points");
                xmlSerializer.text(customer.getPoint()+"");
                xmlSerializer.endTag(null, "points");

                // Ghi thẻ <usedPoints>
                xmlSerializer.startTag(null, "usedPoint");
                xmlSerializer.text(String.valueOf(customer.getUsedPoint()));
                xmlSerializer.endTag(null, "usedPoint");


                // Ghi thẻ <note>
                xmlSerializer.startTag(null, "note");
                xmlSerializer.text(String.valueOf(customer.getNote()));
                xmlSerializer.endTag(null, "note");


                // Ghi thẻ <createdAt>
                xmlSerializer.startTag(null, "createdAt");
                xmlSerializer.text(String.valueOf(Utils.LocalDateToString(customer.getCreatedAt())));
                xmlSerializer.endTag(null, "createdAt");

                // Ghi thẻ <updatedAt>
                xmlSerializer.startTag(null, "updatedAt");
                xmlSerializer.text(String.valueOf(Utils.LocalDateToString(customer.getUpdatedAt())));
                xmlSerializer.endTag(null, "updatedAt");


                // Đóng thẻ </customer>
                xmlSerializer.endTag(null, "customer");
            }

            // Đóng thẻ </customers>
            xmlSerializer.endTag(null, "customers");

            // Kết thúc tài liệu
            xmlSerializer.endDocument();

            // Ghi nội dung vào file
            fos.write(writer.toString().getBytes());
            fos.close();

            // Thông báo thành công
            Toast.makeText(this, "Xuất XML thành công!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Xuất XML thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
    public void saveXmlToExternalStorage(Context context) {
        // Kiểm tra xem bộ nhớ ngoài có sẵn để ghi hay không
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // Lấy thư mục Documents trong bộ nhớ ngoài
//            File downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            File downloadsDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());

            // Kiểm tra xem thư mục có tồn tại không, nếu không thì tạo mới
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }
            if (downloadsDir != null) {
                // Tạo file mới với tên file được chỉ định
                LocalDateTime now = LocalDateTime.now();
                // Chuyển đổi LocalDateTime sang số giây từ Epoch Time
                long seconds = now.toEpochSecond(ZoneOffset.UTC);
                String fileName = "customer_points_list" + seconds + ".xml";
                File file = new File(downloadsDir, fileName);
                try {
                    // Ghi nội dung XML vào file
                    FileOutputStream fos = new FileOutputStream(file);



                    // Tạo XmlSerializer
                    XmlSerializer xmlSerializer = Xml.newSerializer();
                    StringWriter writer = new StringWriter();

                    // Bắt đầu tiến trình ghi XML
                    xmlSerializer.setOutput(writer);
                    xmlSerializer.startDocument("UTF-8", true);

                    // Ghi root tag <customers>
                    xmlSerializer.startTag(null, "customers");

                    // Ghi từng khách hàng
                    for (MainModel customer : customers) {
                        xmlSerializer.startTag(null, "customer");

                        // Ghi thẻ <phone>
                        xmlSerializer.startTag(null, "phone");
                        xmlSerializer.text(customer.getPhone());
                        xmlSerializer.endTag(null, "phone");

                        // Ghi thẻ <points>
                        xmlSerializer.startTag(null, "points");
                        xmlSerializer.text(customer.getPoint()+"");
                        xmlSerializer.endTag(null, "points");

                        // Ghi thẻ <usedPoints>
                        xmlSerializer.startTag(null, "usedPoint");
                        xmlSerializer.text(String.valueOf(customer.getUsedPoint()));
                        xmlSerializer.endTag(null, "usedPoint");


                        // Ghi thẻ <note>
                        xmlSerializer.startTag(null, "note");
                        xmlSerializer.text(String.valueOf(customer.getNote()));
                        xmlSerializer.endTag(null, "note");


                        // Ghi thẻ <createdAt>
                        xmlSerializer.startTag(null, "createdAt");
                        xmlSerializer.text(String.valueOf(Utils.LocalDateToString(customer.getCreatedAt())));
                        xmlSerializer.endTag(null, "createdAt");

                        // Ghi thẻ <updatedAt>
                        xmlSerializer.startTag(null, "updatedAt");
                        xmlSerializer.text(String.valueOf(Utils.LocalDateToString(customer.getUpdatedAt())));
                        xmlSerializer.endTag(null, "updatedAt");


                        // Đóng thẻ </customer>
                        xmlSerializer.endTag(null, "customer");
                    }

                    // Đóng thẻ </customers>
                    xmlSerializer.endTag(null, "customers");

                    // Kết thúc tài liệu
                    xmlSerializer.endDocument();

                    // Ghi nội dung vào file
                    fos.write(writer.toString().getBytes());


                    // Sau khi ghi xong nội dung vào file, hiện dialog nhập email
                    showEmailDialog(context, file);

                    fos.close();

                    // Thông báo thành công
                    Toast.makeText(context, "File saved to: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    Log.d(">>> ExportFile <<<", e.toString());
                    Toast.makeText(context, "Error saving file", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "External storage not available", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "External storage is not mounted", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEmailDialog(Context context, File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Send Email");

        // Thiết lập layout cho dialog
        final EditText emailInput = new EditText(context);
        emailInput.setHint("Enter email address");
        builder.setView(emailInput);

        // Thiết lập nút Gửi
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = emailInput.getText().toString();
                sendEmail(context, email, file);
            }
        });

        // Thiết lập nút Hủy
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void sendEmail(Context context, String email, File file) {
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("application/xml");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "LOYAL CUSTOMER");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "File loyal customers");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
            Toast.makeText(context, "Email sent successfully", Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }



}