package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.todoapp.Adapters.GanNhanAdapter;
import com.example.todoapp.Adapters.NhanAdapter;
import com.example.todoapp.DAO.ChiTietNhanDAO;
import com.example.todoapp.DAO.NhanDAO;
import com.example.todoapp.Entity.ChiTietNhan;
import com.example.todoapp.Entity.GhiChu;
import com.example.todoapp.Entity.Nhan;
import com.example.todoapp.Services.ChiTietNhanService;
import com.example.todoapp.Services.NhanService;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class GanNhanActivity extends AppCompatActivity {
    MaterialToolbar topbar;
    RecyclerView recyclerView;
    ArrayList<Nhan> arrayList;
    ArrayList<Nhan> selectedArrayList;
    NhanService nhanService;
    ChiTietNhanService chiTietNhanService;
    GanNhanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gan_nhan);
        AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());
        NhanDAO nhanDAO = appDatabase.nhanDAO();
        ChiTietNhanDAO chiTietNhanDAO = appDatabase.chiTietNhanDAO();
        this.nhanService = new NhanService(nhanDAO);
        this.chiTietNhanService = new ChiTietNhanService(chiTietNhanDAO);
        SharedPreferences preferences = getSharedPreferences("ghiNhoDangNhap", MODE_PRIVATE);
        topbar = findViewById(R.id.topbar1);
        arrayList = (ArrayList<Nhan>) nhanService.getList(Integer.valueOf(preferences.getString("UID", "")));
        int maGhiChu = getIntent().getIntExtra("maGhiChu", -1);
        selectedArrayList = (ArrayList<Nhan>) chiTietNhanService.getListNhanOfGhiChu(maGhiChu);
        recyclerView = findViewById(R.id.rclListGanNhan);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GanNhanAdapter(this,arrayList, selectedArrayList, this, new GanNhanAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(GhiChu ghiChu, int position) {

            }
        });
        recyclerView.setAdapter(adapter);
        topbar.setNavigationOnClickListener(v -> {
//            for(int i=0; i<arrayList.size();i++){
//                GanNhanAdapter.myViewHolder holder = (GanNhanAdapter.myViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
//                if(holder.isSelected()){
//                    int maGhiChu = getIntent().getIntExtra("maGhiChu",-1);
//                    ChiTietNhan chiTietNhan = new ChiTietNhan();
//                    chiTietNhan.setMaNhan(arrayList.get(i).getMaNhan());
//                    chiTietNhan.setMaGhiChu(maGhiChu);
//                    chiTietNhanService.themChiTietNhan(chiTietNhan);
//                }
//            }
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
            onBackPressed();
        });
    }

    public void ganNhan(int position) {
        int maGhiChu = getIntent().getIntExtra("maGhiChu", -1);
        ChiTietNhan chiTietNhan = new ChiTietNhan();
        chiTietNhan.setMaNhan(arrayList.get(position).getMaNhan());
        chiTietNhan.setMaGhiChu(maGhiChu);
        chiTietNhanService.themChiTietNhan(chiTietNhan);
    }
    public void goNhan(int position) {
        int maGhiChu = getIntent().getIntExtra("maGhiChu", -1);
        chiTietNhanService.xoaChiTietNhan(arrayList.get(position).getMaNhan(),maGhiChu);
    }
}