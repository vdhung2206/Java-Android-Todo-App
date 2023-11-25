package com.example.todoapp.Objects;

import java.util.ArrayList;

public class DanhSach {
    int maDanhSach;
    int maGhiChu;
    ArrayList<ChiTietDanhSach> arrayList;

    public DanhSach(int maDanhSach, int maGhiChu, ArrayList<ChiTietDanhSach> arrayList) {
        this.maDanhSach = maDanhSach;
        this.maGhiChu = maGhiChu;
        this.arrayList = arrayList;
    }

    public int getMaDanhSach() {
        return maDanhSach;
    }

    public void setMaDanhSach(int maDanhSach) {
        this.maDanhSach = maDanhSach;
    }

    public int getMaGhiChu() {
        return maGhiChu;
    }

    public void setMaGhiChu(int maGhiChu) {
        this.maGhiChu = maGhiChu;
    }

    public ArrayList<ChiTietDanhSach> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<ChiTietDanhSach> arrayList) {
        this.arrayList = arrayList;
    }
}
