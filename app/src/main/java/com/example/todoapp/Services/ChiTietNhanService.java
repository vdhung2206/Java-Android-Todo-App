package com.example.todoapp.Services;

import android.database.sqlite.SQLiteOpenHelper;

import com.example.todoapp.DAO.ChiTietNhanDAO;
import com.example.todoapp.DAO.GhiChuDAO;
import com.example.todoapp.Entity.ChiTietNhan;
import com.example.todoapp.Entity.GhiChu;
import com.example.todoapp.Entity.Nhan;

import java.util.List;

public class ChiTietNhanService {
    ChiTietNhanDAO chiTietNhanDAO;

    public ChiTietNhanService(ChiTietNhanDAO chiTietNhanDAO) {
        this.chiTietNhanDAO = chiTietNhanDAO;
    }
    public List<Nhan> getListNhanOfGhiChu(int maGhiChu){
        return chiTietNhanDAO.getListNhanOfGhiChu(maGhiChu);
    }
    public long themChiTietNhan(ChiTietNhan chiTietNhan){
        return chiTietNhanDAO.themChiTietNhan(chiTietNhan);
    }
    public int xoaChiTietNhan(int maNhan, int maGhiChu){
        return chiTietNhanDAO.xoaChiTietNhan(maNhan, maGhiChu);
    }
    public List<GhiChu> getListGhiChuOfNhan(int maNhan){
        return chiTietNhanDAO.getListGhiChuOfNhan(maNhan);
    }
}
