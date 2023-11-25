package com.example.todoapp.Objects;

import java.sql.Time;
import java.util.Date;

public class LoiNhac {
    int maLoinhac;
    Date ngaynhac;
    Time gioNhac;
    boolean lapLai;

    public LoiNhac(int maLoinhac, Date ngaynhac, Time gioNhac, boolean lapLai) {
        this.maLoinhac = maLoinhac;
        this.ngaynhac = ngaynhac;
        this.gioNhac = gioNhac;
        this.lapLai = lapLai;
    }

    public int getMaLoinhac() {
        return maLoinhac;
    }

    public void setMaLoinhac(int maLoinhac) {
        this.maLoinhac = maLoinhac;
    }

    public Date getNgaynhac() {
        return ngaynhac;
    }

    public void setNgaynhac(Date ngaynhac) {
        this.ngaynhac = ngaynhac;
    }

    public Time getGioNhac() {
        return gioNhac;
    }

    public void setGioNhac(Time gioNhac) {
        this.gioNhac = gioNhac;
    }

    public boolean isLapLai() {
        return lapLai;
    }

    public void setLapLai(boolean lapLai) {
        this.lapLai = lapLai;
    }
}
