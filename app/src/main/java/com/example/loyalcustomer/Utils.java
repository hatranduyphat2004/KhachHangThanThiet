package com.example.loyalcustomer;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public static LocalDateTime FormatDateSQL(String dt){

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime localDate = LocalDateTime.parse(dt, inputFormatter);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String outputDate = localDate.format(outputFormatter);
        return LocalDateTime.parse(outputDate);

    }

    

}
