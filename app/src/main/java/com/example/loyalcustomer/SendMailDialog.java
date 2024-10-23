package com.example.loyalcustomer;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SendMailDialog extends DialogFragment {
    private EmailDialogListener listener;

    // Giao diện để truyền dữ liệu email trở lại Activity
    public interface EmailDialogListener {
        void onSendEmail(String email);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (EmailDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement EmailDialogListener");
        }    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Inflate giao diện từ XML
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_email, null);

        // Lấy tham chiếu tới các view trong dialog
        EditText emailEditText = view.findViewById(R.id.inpEmail);
        Button sendButton = view.findViewById(R.id.btnSendMail);
        Button cancelButton = view.findViewById(R.id.btnCancelSendMail);

        // Xử lý sự kiện khi nhấn nút Gửi
        sendButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            if (!email.isEmpty()) {
                listener.onSendEmail(email);  // Gửi email trở lại Activity
                dismiss();  // Đóng dialog sau khi gửi
            } else {
                emailEditText.setError("Please enter a valid email");
            }
        });

        // Xử lý sự kiện khi nhấn nút Hủy
        cancelButton.setOnClickListener(v -> dismiss());

        // Tạo và trả về một AlertDialog với giao diện đã inflate
        return new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }


}
