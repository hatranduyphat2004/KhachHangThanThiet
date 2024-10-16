package com.example.loyalcustomer;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Utils {
//    public static String LocalDateToString(LocalDate l) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        return sdf.format(l);
//    }
//
//    public static LocalDate stringToLocalDate(String s) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        LocalDate date = LocalDate.parse(s);
//        return date;
//    }

    public static String LocalDateToString(LocalDateTime dt){
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dt.format(format);
    }

    // Hàm chuyển đổi chuỗi thành LocalDateTime
    public static LocalDateTime StringToLocalDate(String dateString) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            return LocalDateTime.parse(dateString, format);
        } catch (DateTimeParseException e) {
            // Xử lý ngoại lệ nếu chuỗi không đúng định dạng
            System.err.println("Invalid date format: " + e.getMessage());
            return null; // Hoặc ném một ngoại lệ tùy thuộc vào cách bạn muốn xử lý
        }
    }

    

}
