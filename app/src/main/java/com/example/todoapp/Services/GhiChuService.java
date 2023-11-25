package com.example.todoapp.Services;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todoapp.DAO.GhiChuDAO;
import com.example.todoapp.DatabaseHelper;
import com.example.todoapp.Entity.GhiChu;

import java.util.ArrayList;
import java.util.List;

public class GhiChuService {
    GhiChuDAO ghiChuDAO;
    private SQLiteOpenHelper dbHelper;

    public GhiChuService(GhiChuDAO ghiChuDAO) {
        this.ghiChuDAO = ghiChuDAO;
    }

    public int suaGhiChu(GhiChu ghiChu, int maGhiChu) {
        return ghiChuDAO.suaGhiChu(ghiChu);
    }

    public List<GhiChu> getList(String taiKhoanGhiNho){
        return ghiChuDAO.getList(taiKhoanGhiNho);
    }
    public  void tangOrder(int newOrder, String taiKhoanGhiNho){
        ghiChuDAO.tangOrder(newOrder, taiKhoanGhiNho);
    }
    public  void tangPinedOrder(int newOrder, String taiKhoanGhiNho){
        ghiChuDAO.tangPinedOrder(newOrder, taiKhoanGhiNho);
    }
    public  void giamOrder(int newOrder, String taiKhoanGhiNho){
        ghiChuDAO.giamOrder(newOrder, taiKhoanGhiNho);
    }
    public void capNhatOrder(int maGhiChu, int newOrder){
        ghiChuDAO.capNhatOrder(maGhiChu, newOrder );
    }
    public int xoaGhiChu(GhiChu ghiChu){
        return ghiChuDAO.xoaGhiChu(ghiChu);
    }
}
