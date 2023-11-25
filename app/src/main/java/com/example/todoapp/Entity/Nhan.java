package com.example.todoapp.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "nhan_table")
public class Nhan {
    @PrimaryKey(autoGenerate = true)
    private int maNhan;
    private String tenNhan;
    private int UID;
    private boolean isSelected = false;
    public Nhan(String tenNhan, int UID)
    {
        this.tenNhan = tenNhan;
        this.UID = UID;
    }

    public int getMaNhan() {
        return maNhan;
    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public void setMaNhan(int maNhan) {
        this.maNhan = maNhan;
    }

    public String getTenNhan() {
        return tenNhan;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setTenNhan(String tenNhan) {
        this.tenNhan = tenNhan;
    }
}
