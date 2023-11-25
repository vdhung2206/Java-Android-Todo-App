package com.example.todoapp.Objects;

public class Nhan {
    int maNhan;
    String tenNhan;

    public Nhan(int maNhan, String tenNhan) {
        this.maNhan = maNhan;
        this.tenNhan = tenNhan;
    }

    public int getMaNhan() {
        return maNhan;
    }

    public void setMaNhan(int maNhan) {
        this.maNhan = maNhan;
    }

    public String getTenNhan() {
        return tenNhan;
    }

    public void setTenNhan(String tenNhan) {
        this.tenNhan = tenNhan;
    }
}
