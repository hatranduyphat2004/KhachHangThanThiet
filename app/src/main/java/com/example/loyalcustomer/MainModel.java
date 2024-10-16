package com.example.loyalcustomer;

import java.time.LocalDateTime;

public class MainModel {
    private String phone;
    private int point;
    private int usedPoint;


    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;

    // Constructors, getters, and setters

    public MainModel(String phone, int point, int usedPoint, String note, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.phone = phone;
        this.point = point;
        this.usedPoint = usedPoint;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = "active";
    }

    public int getUsedPoint() {
        return usedPoint;
    }

    public void setUsedPoint(int usedPoint) {
        this.usedPoint = usedPoint;
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
