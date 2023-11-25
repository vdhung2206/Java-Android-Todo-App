package com.example.todoapp.Services;

import com.example.todoapp.DAO.ChiTietNhanDAO;
import com.example.todoapp.DAO.NhanDAO;
import com.example.todoapp.Entity.Nhan;

import java.util.List;

public class NhanService {
    NhanDAO nhanDAO;
    public NhanService(NhanDAO nhanDAO) {this.nhanDAO = nhanDAO;}
    public List<Nhan> getList(int UID){
        return nhanDAO.getList(UID);
    }
    public long taoNhan(Nhan nhan){
        return nhanDAO.themNhan(nhan);
    }
    public int xoaNhan(Nhan nhan){
        return nhanDAO.xoaNhan(nhan);
    }
    public int suaNhan(Nhan nhan){
        Nhan n = nhan;
        return nhanDAO.suaNhan(nhan);
    }
    public int kiemTraTenNhanTonTai(String tenNhan, int UID){
        return  nhanDAO.kiemTraTenNhanTonTai(tenNhan, UID);
    }
}
