package com.example.todoapp.Objects;

public class ChiTietDanhSach {
    int id;
    String noiDung;
    boolean checked;

    public ChiTietDanhSach(int id, String noiDung, boolean checked) {
        this.id = id;
        this.noiDung = noiDung;
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
