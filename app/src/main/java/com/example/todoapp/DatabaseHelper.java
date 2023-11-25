package com.example.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String databaseName = "todoap.db";

    public DatabaseHelper(@Nullable Context context) {
        super(context, databaseName, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase myDatabase) {
        myDatabase.execSQL("create table TaiKhoan(UID integer primary key autoincrement, taiKhoan text unique, matKhau text)");
        myDatabase.execSQL("create table GhiChu(maGhiChu integer primary key autoincrement not null, tieuDe text, maNoiDung integer, maLoiNhac integer,maDanhSach integer, ngaySuaDoi String, gioSuaDoi String, daXoa integer, ngayXoa String, gioXoa String, UID integer)");
        myDatabase.execSQL("create table NoiDung(maNoiDung integer primary key autoincrement, maGhiChu integer, noiDung text)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase myDatabase, int i, int i1) {
//        myDatabase.execSQL("drop table if exists GhiChuDAO");
//        myDatabase.execSQL("create table GhiChuDAO(maGhiChu integer primary key autoincrement, tieuDe text, maNoiDung integer, maLoiNhac integer,maDanhSach integer, ngaySuaDoi String, gioSuaDoi String, daXoa integer, ngayXoa String, gioXoa String, UID integer)");
        myDatabase.execSQL("create table NoiDung(maNoiDung integer primary key autoincrement, maGhiChu integer, noiDung text)");
    }
}
