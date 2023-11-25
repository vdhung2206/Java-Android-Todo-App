package com.example.todoapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.AppDatabase;
import com.example.todoapp.DAO.GhiChuDAO;
import com.example.todoapp.DAO.NhanDAO;
import com.example.todoapp.Entity.GhiChu;
import com.example.todoapp.Entity.Nhan;
import com.example.todoapp.GanNhanActivity;
import com.example.todoapp.MainActivity;
import com.example.todoapp.R;
import com.example.todoapp.Services.GhiChuService;
import com.example.todoapp.Services.NhanService;

import java.util.ArrayList;
import java.util.List;

public class GanNhanAdapter extends RecyclerView.Adapter<GanNhanAdapter.myViewHolder> {
    private final ArrayList<Nhan> arrayList;
    private final ArrayList<Nhan> selectedArrayList;
    private GanNhanActivity ganNhanActivity;
    private final OnItemClickListener listener; // Bộ lắng nghe sự kiện click
    private final Context context;
    NhanService nhanService;


    public GanNhanAdapter(GanNhanActivity ganNhanActivity, ArrayList<Nhan> arrayList,ArrayList<Nhan> selectedArrayList, Context context, OnItemClickListener listener) {
        this.arrayList = arrayList;
        this.ganNhanActivity = ganNhanActivity;
        this.selectedArrayList = selectedArrayList;
        this.context = context;
        this.listener = listener;
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        NhanDAO nhanDao = appDatabase.nhanDAO();
        this.nhanService = new NhanService(nhanDao);
    }

    public interface OnItemClickListener {
        void onItemClick(GhiChu ghiChu, int position);

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_gannhan_item, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        Nhan nhan = arrayList.get(position);
        for(int i=0; i<selectedArrayList.size(); i++){
            if(nhan.getMaNhan() == selectedArrayList.get(i).getMaNhan()){
                holder.checkBox.setChecked(true);
            }
        }
        holder.tenNhan.setText(nhan.getTenNhan());
    }


    @Override
    public int getItemCount() {
        if (arrayList != null && arrayList.size() > 0)
            return arrayList.size();
        else
            return 0;
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView tenNhan;
        CheckBox checkBox;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tenNhan = itemView.findViewById(R.id.tenNhan);
            checkBox = itemView.findViewById(R.id.checked);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkBox.setChecked(!checkBox.isChecked());
                    if(checkBox.isChecked()){
                        ganNhanActivity.ganNhan(getAdapterPosition());
                    } else {
                        ganNhanActivity.goNhan(getAdapterPosition());
                    }
                }
            });
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!buttonView.isPressed()) {
                        // Bỏ qua sự kiện nếu nó không phải là do người dùng tương tác
                        return;
                    }

                    if (isChecked) {
                        ganNhanActivity.ganNhan(getAdapterPosition());
                    } else {
                        ganNhanActivity.goNhan(getAdapterPosition());
                    }
                }
            });
        }
    }
}
