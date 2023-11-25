package com.example.todoapp.Services;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todoapp.DAO.TaiKhoanDAO;
import com.example.todoapp.DatabaseHelper;
import com.example.todoapp.Entity.TaiKhoan;

public class TaiKhoanService {
    TaiKhoanDAO taiKhoanDAO;
    private SQLiteOpenHelper dbHelper;

    public TaiKhoanService(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public boolean themTaiKhoan(String taiKhoan, String matKhau) {
        SQLiteDatabase myDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("taiKhoan", taiKhoan);
        contentValues.put("matKhau", matKhau);
        long result = myDatabase.insert("TaiKhoan", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkTaiKhoan(String taiKhoan) {
        SQLiteDatabase myDatabase = dbHelper.getWritableDatabase();
        Cursor cursor = myDatabase.rawQuery("select * from TaiKhoan where taiKhoan = ?", new String[]{taiKhoan});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }
    @SuppressLint("Range")
    public TaiKhoan checkDangNhap(String taiKhoan, String matKhau) {
        SQLiteDatabase myDatabase = dbHelper.getWritableDatabase();
        Cursor cursor = myDatabase.rawQuery("select * from TaiKhoan where taiKhoan = ? and matKhau = ?", new String[]{taiKhoan, matKhau});
        if(cursor.moveToNext()){
            int UID = cursor.getInt(cursor.getColumnIndex("UID"));
            String username = cursor.getString(cursor.getColumnIndex("taiKhoan"));
            TaiKhoan tk = new TaiKhoan(UID,username,"");
            if (cursor.getCount() > 0) {
                return tk;
            } else {
                return null;
            }
        }
        return null;
    }
}

