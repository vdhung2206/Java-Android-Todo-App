package com.example.todoapp.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.AppDatabase;
import com.example.todoapp.ChinhSuaNhanActivity;
import com.example.todoapp.DAO.NhanDAO;
import com.example.todoapp.Entity.Nhan;
import com.example.todoapp.FirstFragment;
import com.example.todoapp.R;
import com.example.todoapp.Services.NhanService;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class NhanAdapter extends  RecyclerView.Adapter<NhanAdapter.myViewHolder>{
    private final ArrayList<Nhan> arrayList;
    private final Context context;
    private NhanService nhanService;
    boolean isNewItemAdded = false;
    private final NhanAdapter.OnItemClickListener listener;
    public NhanAdapter(ArrayList<Nhan> arrayList, Context context, OnItemClickListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        NhanDAO nhanDAO = appDatabase.nhanDAO();
        this.nhanService = new NhanService(nhanDAO);
    }



    public interface OnItemClickListener {
        boolean onItemClick(int position);

    }
    @NonNull
    @Override
    public NhanAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nhan, parent, false);
        return new NhanAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NhanAdapter.myViewHolder holder, int position) {
        Nhan nhan = arrayList.get(holder.getAdapterPosition());
        if(position==0){
            holder.toolbar.setNavigationIcon(R.drawable.baseline_add_24);
            holder.toolbar.setNavigationOnClickListener(v -> {
                listener.onItemClick(holder.getAdapterPosition());
            });
            holder.toolbar.getMenu().clear();
            holder.tenNhan.setHint("Tạo nhãn mới");
        } else {
            holder.toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.edit) {
                    listener.onItemClick(holder.getAdapterPosition());
                }
                return false;
            });
        }
        holder.tenNhan.setText(nhan.getTenNhan());
        if(isNewItemAdded){
            holder.changeMenuBack(holder.getAdapterPosition());
            holder.changeNavigationBack(holder.getAdapterPosition());
            holder.itemView.setSelected(false);
            isNewItemAdded=false;
        }
        holder.tenNhan.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Kiểm tra xem EditText có chứa dữ liệu hay không
                    String inputText = holder.tenNhan.getText().toString().trim();
                    if (!inputText.isEmpty()) {
                        // Gọi sự kiện của item "Done" hoặc "Xong" trong Toolbar
                        holder.toolbar.getMenu().performIdentifierAction(R.id.done, 0);
                    }
                    return true; // Đánh dấu rằng sự kiện đã được xử lý
                }
                return false;
            }
        });
    }
    @Override
    public int getItemCount() {
        return arrayList.size(); // Trả về số lượng phần tử trong danh sách
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    public class myViewHolder extends RecyclerView.ViewHolder {
        public TextView tenNhan;
        public MaterialToolbar toolbar;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tenNhan = itemView.findViewById(R.id.tenNhan);
            toolbar = itemView.findViewById(R.id.toolbar);
            tenNhan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        listener.onItemClick(getAdapterPosition());
                        showKeyboard(tenNhan);
                    } else{
                        hideKeyboard(tenNhan);
                    }
                }
            });
//            toolbar.setOnMenuItemClickListener(item -> {
//                int position = getAdapterPosition();
//                if(item.getItemId() == R.id.edit){
//                    tenNhan.requestFocus();
//                    listener.onItemClick(position);
//                }
////                return false;
//            });
        }
        public void changeMenu(int position){
            SharedPreferences preferences = context.getSharedPreferences("ghiNhoDangNhap", MODE_PRIVATE);
            toolbar.getMenu().clear();
            if(getAdapterPosition() == 0){
                toolbar.inflateMenu(R.menu.themnhanitem);
                toolbar.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.done) {
                        if(!tenNhan.getText().toString().equals("")){
                            if(nhanService.kiemTraTenNhanTonTai(tenNhan.getText().toString(),Integer.valueOf(preferences.getString("UID", "")))==0){
                                Nhan nhan = new Nhan(tenNhan.getText().toString(),Integer.valueOf(preferences.getString("UID", "")));
                                nhan.setUID(Integer.valueOf(preferences.getString("UID", "")));
                                int maNhan = (int) nhanService.taoNhan(new Nhan(tenNhan.getText().toString(),Integer.valueOf(preferences.getString("UID", ""))));
                                nhan.setMaNhan(maNhan);
                                arrayList.add(nhan);
                                addNewItem();
                                tenNhan.setText("");
                                itemView.setSelected(false);
                                changeMenuBack(getAdapterPosition());
                                changeNavigationBack(getAdapterPosition());
                                notifyItemInserted(arrayList.size()-1);
                            } else {
                                Toast.makeText(context, "Nhãn đã tồn tại!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(context, "Chưa nhập tên nhãn!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                });
            } else {
                toolbar.inflateMenu(R.menu.listnhanselecteditem);
                toolbar.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.done) {
                        Nhan nhan = arrayList.get(getAdapterPosition());
                        nhan.setTenNhan(tenNhan.getText().toString());
                        nhanService.suaNhan(nhan);
                        tenNhan.clearFocus();
                        arrayList.set(getAdapterPosition(), nhan);
                        itemView.setSelected(false);
                        changeMenuBack(getAdapterPosition());
                        changeNavigationBack(getAdapterPosition());
                    }
                    return true;
                });
            }
        }
        public void changeMenuBack(int position){
            tenNhan.clearFocus();
            toolbar.getMenu().clear();
            if(getAdapterPosition() == 0){
                toolbar.getMenu().clear();
            } else {
                toolbar.inflateMenu(R.menu.listnhanitem);
                toolbar.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.edit) {
                        listener.onItemClick(getAdapterPosition());
                    }
                    return false;
                });
            }
        }
        public void changeNavigation(int position){
            if(getAdapterPosition() == 0){
                toolbar.setNavigationIcon(R.drawable.baseline_close_24);
                toolbar.setNavigationOnClickListener(v -> {
                    changeMenuBack(getAdapterPosition());
                    changeNavigationBack(getAdapterPosition());
                    tenNhan.setText("");
                    itemView.setSelected(false);
                    tenNhan.clearFocus();
                });
            } else {
                toolbar.setNavigationIcon(R.drawable.outline_delete_24);
                toolbar.setNavigationOnClickListener(v -> {
                    nhanService.xoaNhan(arrayList.get(getAdapterPosition()));
                    arrayList.remove(getAdapterPosition());
                    tenNhan.clearFocus();
                    notifyItemRemoved(getAdapterPosition());
                });
            }
        }
        public void changeNavigationBack(int position){

            tenNhan.clearFocus();
            if(getAdapterPosition() == 0){
                toolbar.setNavigationIcon(R.drawable.baseline_add_24);
                toolbar.setNavigationOnClickListener(v -> {
                    listener.onItemClick(getAdapterPosition());
                });
            } else {
                toolbar.setNavigationIcon(R.drawable.outline_label_24);
                toolbar.setNavigationOnClickListener(null);
            }
        }
        public void setSelected(boolean selected){
            itemView.setSelected(selected);
        }
        public void addNewItem() {
            // ...
            isNewItemAdded = true; // Đặt biến này thành true sau khi thêm một phần tử mới
        }
    }
    private void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
