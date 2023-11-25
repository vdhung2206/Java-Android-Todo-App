package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import com.example.todoapp.Adapters.GhichuAdapter;
import com.example.todoapp.Adapters.NhanAdapter;
import com.example.todoapp.DAO.NhanDAO;
import com.example.todoapp.Entity.Nhan;
import com.example.todoapp.Services.NhanService;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class ChinhSuaNhanActivity extends AppCompatActivity {
    MaterialToolbar topbar;
    RecyclerView recyclerView;
    ArrayList<Nhan> arrayList;
    int selectedPosition = -1;
    NhanService nhanService;
    NhanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_nhan);

        AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());
        NhanDAO nhanDAO = appDatabase.nhanDAO();
        this.nhanService = new NhanService(nhanDAO);

        topbar = findViewById(R.id.topbar1);
        topbar.setNavigationOnClickListener(v -> {
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
            onBackPressed();
        });
        SharedPreferences preferences = getSharedPreferences("ghiNhoDangNhap", MODE_PRIVATE);
        arrayList = (ArrayList<Nhan>) nhanService.getList(Integer.valueOf(preferences.getString("UID", "")));
        Toast.makeText(this, preferences.getString("UID", ""), Toast.LENGTH_SHORT).show();
        arrayList.add(0, new Nhan("",-1));
        recyclerView = findViewById(R.id.rclListNhan);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NhanAdapter(arrayList, this, new NhanAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(int position) {
                NhanAdapter.myViewHolder newHolder = (NhanAdapter.myViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                newHolder.tenNhan.requestFocus();
                newHolder.changeMenu(position);
                newHolder.changeNavigation(position);
                newHolder.setSelected(true);
                //Khi da co phan tu khac duoc selected
                if (selectedPosition != -1) {
                    NhanAdapter.myViewHolder oldHolder = (NhanAdapter.myViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedPosition);
                    if (selectedPosition != position && oldHolder != null) {
                        oldHolder.setSelected(false);
                        oldHolder.changeMenuBack(selectedPosition);
                        oldHolder.changeNavigationBack(selectedPosition);
                    }
                }
                selectedPosition = position;
                return true;
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void onEditTextClick(View view) {
        View parentItem = (View) view.getParent(); // Lấy parent view của EditText
        parentItem.performClick(); // Gọi sự kiện onClick của parent view
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}