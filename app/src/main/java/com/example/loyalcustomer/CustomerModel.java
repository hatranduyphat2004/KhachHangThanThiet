package com.example.loyalcustomer;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomerModel {
    private String phone;
    private int point;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;

    // Constructors, getters, and setters

    public CustomerModel(String phone, int point, String note, LocalDateTime createdAt, LocalDateTime updatedAt, String status) {
        this.phone = phone;
        this.point = point;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
