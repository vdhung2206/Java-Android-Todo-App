package com.example.todoapp.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.AppDatabase;
import com.example.todoapp.DAO.ChiTietNhanDAO;
import com.example.todoapp.DAO.GhiChuDAO;
import com.example.todoapp.Entity.ChiTietNhan;
import com.example.todoapp.Entity.GhiChu;
import com.example.todoapp.Entity.Nhan;
import com.example.todoapp.R;
import com.example.todoapp.Services.ChiTietNhanService;
import com.example.todoapp.Services.GhiChuService;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class GhichuAdapter extends RecyclerView.Adapter<GhichuAdapter.myViewHolder> {
    private final ArrayList<GhiChu> arrayList;
    private final OnItemClickListener listener; // Bộ lắng nghe sự kiện click
    private final Context context;
    GhiChuService ghiChuService;
    ChiTietNhanService chiTietNhanService;
    private ItemTouchHelper itemTouchHelper;

    public GhichuAdapter(ArrayList<GhiChu> arrayList, Context context, OnItemClickListener listener) {
        this.arrayList = arrayList;
        this.context = context;
        this.listener = listener;
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        GhiChuDAO ghiChuDAO = appDatabase.ghiChuDAO();
        ChiTietNhanDAO chiTietNhanDAO = appDatabase.chiTietNhanDAO();
        this.ghiChuService = new GhiChuService(ghiChuDAO);
        this.chiTietNhanService = new ChiTietNhanService(chiTietNhanDAO);
    }

    public interface OnItemClickListener {
        void onItemClick(GhiChu ghiChu, int position);

        void onItemLongClick(GhiChu ghiChu, int position);

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ghichu, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        GhiChu ghiChu = arrayList.get(position);
        holder.itemView.setSelected(ghiChu.isSelected()); //! vcl
//        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        if (ghiChu.getIsPin() == 2) {
            // Đặt layout background khác cho các phần tử có isPin = 2
            holder.noiDung.setText(ghiChu.getNoiDung());
            holder.tieuDe.setVisibility(View.GONE);
            holder.noiDung.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundResource(R.drawable.background_separator);
            holder.noiDung.setTextColor(0xFF000000);
            holder.itemView.setClickable(false);
            holder.itemView.setLongClickable(false);
            holder.nhan.setVisibility(View.GONE);
        } else {
            holder.itemView.setClickable(false);
            holder.itemView.setLongClickable(false);
            TextView noiDungTextView = holder.itemView.findViewById(R.id.noiDung);
            holder.itemView.setBackgroundResource(R.drawable.itemborder);
            if (ghiChu.getNoiDung() == null) {
                noiDungTextView.setText("");
            } else {
                noiDungTextView.setText(ghiChu.getNoiDung());
                noiDungTextView.setVisibility(View.VISIBLE);
            }
            if (ghiChu.getNoiDung() == null || ghiChu.getNoiDung().isEmpty()) {
                noiDungTextView.setVisibility(View.GONE);
            } else {
                noiDungTextView.setText(ghiChu.getNoiDung());
                noiDungTextView.setVisibility(View.VISIBLE);
            }
            holder.tieuDe.setText(ghiChu.getTieuDe());
            TextView tieuDeTextView = holder.itemView.findViewById(R.id.tieuDe);
            if (ghiChu.getTieuDe() == null || ghiChu.getTieuDe().isEmpty()) {
                // Nếu tiêu đề rỗng, ẩn TextView maGhiChu
                tieuDeTextView.setVisibility(View.GONE);
            } else {
                tieuDeTextView.setText(ghiChu.getTieuDe());
                tieuDeTextView.setVisibility(View.VISIBLE);
                noiDungTextView.setText(ghiChu.getNoiDung());
                noiDungTextView.setMaxLines(15);
                noiDungTextView.setEllipsize(TextUtils.TruncateAt.END);
            }
            holder.nhan.setVisibility(View.VISIBLE);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemLongClick(arrayList.get(position), position);
                        return true;
                    }
                    return false;
                }
            });
            holder.listNhan();
            holder.addDateTimeLabel(ghiChu);
        }
    }

    @Override
    public int getItemCount() {
        if (arrayList != null && arrayList.size() > 0)
            return arrayList.size();
        else
            return 0;
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView noiDung, tieuDe;
        FlexboxLayout nhan;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            noiDung = itemView.findViewById(R.id.noiDung);
            tieuDe = itemView.findViewById(R.id.tieuDe);
            nhan = itemView.findViewById(R.id.nhan);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Kích hoạt sự kiện kéo và thả khi ấn giữ
                    if (itemTouchHelper != null) {
                        itemTouchHelper.startDrag(myViewHolder.this);
                    }
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(arrayList.get(position), position);
                    }
                }
            });
        }
        public void listNhan() {
            this.nhan.removeAllViews();
            ArrayList<Nhan> listNhan = (ArrayList<Nhan>) chiTietNhanService.getListNhanOfGhiChu(arrayList.get(getAdapterPosition()).getMaGhiChu());

            // Số nhãn tối đa để hiển thị
            int maxLabels = 2;

            // Lặp qua danh sách các nhãn và thêm chúng vào FlexboxLayout, nhưng chỉ tối đa 2 nhãn
            for (int i = 0; i < Math.min(listNhan.size(), maxLabels); i++) {
                Nhan nhan = listNhan.get(i);
                TextView textView = createLabelTextView(nhan.getTenNhan(),0);
                this.nhan.addView(textView);
            }

            int remainingCount = listNhan.size() - maxLabels;
            if (remainingCount > 0) {
                TextView remainingTextView = createLabelTextView("+" + remainingCount,0);
                this.nhan.addView(remainingTextView);
            }
        }
        public void addDateTimeLabel(GhiChu ghiChu) {
            String ngayNhac = ghiChu.getNgayNhac();
            String gioNhac = ghiChu.getGioNhac();

            if (ngayNhac != null && !ngayNhac.isEmpty() && gioNhac != null && !gioNhac.isEmpty()) {
                String dateTime = formatDateTime(ngayNhac, gioNhac);
                TextView dateTimeTextView = createLabelTextView(dateTime, ghiChu.getDaGui());
                this.nhan.addView(dateTimeTextView, 0);
                Drawable clockIcon;
                if(ghiChu.getNhacLapLai()!=0){
                    if(ghiChu.getDaGui() == 1){
                        clockIcon = context.getDrawable(R.drawable.baseline_repeat_24_sent);
                    } else {
                        clockIcon = context.getDrawable(R.drawable.baseline_repeat_18);
                    }
                } else{
                    if(ghiChu.getDaGui() == 1){
                        clockIcon = context.getDrawable(R.drawable.baseline_access_alarm_24);
                    } else {
                        clockIcon = context.getDrawable(R.drawable.baseline_access_alarms_18);
                    }
                }
                if(ghiChu.getDaXong()==1){
                    dateTimeTextView.setPaintFlags(dateTimeTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                dateTimeTextView.setCompoundDrawablesWithIntrinsicBounds(clockIcon, null, null, null);
                dateTimeTextView.setCompoundDrawablePadding(20);
            }
        }
        private TextView createLabelTextView(String label, int daGui) {
            TextView textView = new TextView(context);
            textView.setText(label);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textView.setClickable(true);
            layoutParams.rightMargin = context.getResources().getDimensionPixelSize(R.dimen.margin_8dp);
            layoutParams.topMargin = context.getResources().getDimensionPixelSize(R.dimen.margin_8dp) ;
            textView.setLayoutParams(layoutParams);
            Drawable drawable;
            if(daGui == 1){
                drawable = context.getResources().getDrawable(R.drawable.background_label2);
                textView.setTextColor(0xFFA8A7A7);
            } else {
                drawable = context.getResources().getDrawable(R.drawable.background_label1);
                textView.setTextColor(0xFF313131);
            }

            textView.setBackground(drawable);
            return textView;
        }
        private String formatDateTime(String ngayNhac, String gioNhac) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date convertedDate = dateFormat.parse(ngayNhac);

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                Date convertedTime = timeFormat.parse(gioNhac);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(convertedDate);

                Calendar homNay = Calendar.getInstance();
                Calendar homQua = Calendar.getInstance();
                homQua.add(Calendar.DAY_OF_YEAR, -1);
                Calendar ngayMai = Calendar.getInstance();
                ngayMai.add(Calendar.DAY_OF_YEAR, 1);

                String ngay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                String thang = String.valueOf(calendar.get(Calendar.MONTH) + 1);

                String nam = calendar.get(Calendar.YEAR) == homNay.get(Calendar.YEAR)
                        ? ""
                        : ", " + String.valueOf(calendar.get(Calendar.YEAR));

                String result = ngay + " thg " + thang + nam + ", " + timeFormat.format(convertedTime);

                if (isSameDay(calendar, homNay)) {
                    return "Hôm nay" + nam + ", " + timeFormat.format(convertedTime);
                } else if (isSameDay(calendar, homQua)) {
                    return "Hôm qua" + nam + ", " + timeFormat.format(convertedTime);
                } else if (isSameDay(calendar, ngayMai)) {
                    return "Ngày mai" + nam + ", " + timeFormat.format(convertedTime);
                } else {
                    return result;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return "";
        }

        private boolean isSameDay(Calendar cal1, Calendar cal2) {
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        }

    }
}
