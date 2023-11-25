package com.example.todoapp.DAO;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.todoapp.Entity.TaiKhoan;

@Dao
public interface TaiKhoanDAO {

    @Insert
    long themTaiKhoan(TaiKhoan taiKhoan);


    @Query("SELECT COUNT(*) FROM TaiKhoan WHERE taiKhoan = :taiKhoan")
    int checkTaiKhoan(String taiKhoan);

    @Query("SELECT * FROM TaiKhoan WHERE taiKhoan = :taiKhoan AND matKhau = :matKhau LIMIT 1")
    TaiKhoan checkDangNhap(String taiKhoan, String matKhau);
}
