package com.example.loyalcustomer;

import android.app.Activity;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomerArrayAdapter extends ArrayAdapter<CustomerModel> {
    Activity context;
    int idItemLayout;
    ArrayList<CustomerModel> customers;
    final int maxLines = 3;

    public CustomerArrayAdapter(Activity context, int idItemLayout, ArrayList<CustomerModel> customers) {
        super(context, idItemLayout, customers);
        this.context = context;
        this.idItemLayout = idItemLayout;
        this.customers = customers;
    }

    // getView hàm sắp xếp dliệu


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Tạo đế chưa layout
        LayoutInflater myInflater = context.getLayoutInflater();

        // Đặt layout lên đế tạo thành view
        convertView = myInflater.inflate(idItemLayout, null);

        // Lấy 1 ptu trong mảng
        CustomerModel cus = customers.get(position);

        // Khai báo và tham chiếu id => hiển thị item(phone num, point, createdAt, updatedAt, Note, DeleleButton)
        TextView itemPhone = convertView.findViewById(R.id.itemPhone);
        TextView itemPoint = convertView.findViewById(R.id.itemPoint);

        TextView itemNote = convertView.findViewById(R.id.itemNote);
        itemNote.setMovementMethod(new ScrollingMovementMethod());
        itemNote.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Kiểm tra nếu TextView có nhiều hơn 2 dòng (có thể cuộn)
                if (itemNote.getLineCount() > maxLines) {
                    // Cho phép TextView nhận sự kiện cuộn
                    v.getParent().requestDisallowInterceptTouchEvent(true);

                    // Xử lý sự kiện cuộn bên trong TextView
                    if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                return false;
            }
        });


        TextView itemCreatedAt = convertView.findViewById(R.id.itemCreatedAt);
        TextView itemUpdatedAt = convertView.findViewById(R.id.itemUpdatedAt);
        ImageButton btnDelete = convertView.findViewById(R.id.btnDelete);

        itemPhone.setText(cus.getPhone());
        itemPoint.setText(cus.getPoint()+"");

        itemNote.setText(cus.getNote());
        itemNote.setMaxLines(maxLines);
        itemCreatedAt.setText(Utils.LocalDateToString(cus.getCreatedAt()));
        itemUpdatedAt.setText(Utils.LocalDateToString(cus.getUpdatedAt()));
        btnDelete.setImageResource(R.drawable.bin);
        return convertView;


    }

    @Override
    public int getCount() {
        return customers.size();
    }
}
