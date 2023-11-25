package com.example.todoapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.todoapp.DAO.ChiTietNhanDAO;
import com.example.todoapp.DAO.GhiChuDAO;
import com.example.todoapp.DAO.NhanDAO;
import com.example.todoapp.DAO.TaiKhoanDAO;
import com.example.todoapp.Entity.ChiTietNhan;
import com.example.todoapp.Entity.GhiChu;
import com.example.todoapp.Entity.Nhan;
import com.example.todoapp.Entity.TaiKhoan;

import java.lang.annotation.Annotation;

@Database(entities = {TaiKhoan.class, GhiChu.class, Nhan.class, ChiTietNhan.class},
        version = 12, exportSchema = false)
//@TypeConverters(Annotation.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "ToDo.APP6";

    public abstract TaiKhoanDAO taiKhoanDAO();
    public abstract GhiChuDAO ghiChuDAO();
    public abstract NhanDAO nhanDAO();
    public abstract ChiTietNhanDAO chiTietNhanDAO();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, DATABASE_NAME)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}