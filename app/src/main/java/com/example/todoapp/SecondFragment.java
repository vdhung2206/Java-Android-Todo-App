//package com.example.todoapp;
//
//import static android.app.Activity.RESULT_OK;
//import static android.content.Context.MODE_PRIVATE;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResult;
//import androidx.activity.result.ActivityResultCallback;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.ItemTouchHelper;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.todoapp.Adapters.GhichuAdapter;
//import com.example.todoapp.DAO.GhiChuDAO;
//import com.example.todoapp.Entity.GhiChu;
//import com.example.todoapp.Services.GhiChuService;
//import com.example.todoapp.databinding.FragmentSecondBinding;
//import com.google.android.material.appbar.AppBarLayout;
//import com.google.android.material.appbar.MaterialToolbar;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//public class SecondFragment extends Fragment {
//    private boolean multiselect = false;
//    String taiKhoanGhiNho;
//    GhiChuService ghiChuService;
//    List<GhiChu> arrayList;
//    GhichuAdapter adapter;
//    private FragmentSecondBinding binding;
//    private RecyclerView recyclerView;
//    private boolean isItemBeingDragged = false;
//    private boolean itemMoved = false;
//    private boolean firstMoved = true;
//    boolean mDraggable = true;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        AppDatabase appDatabase = AppDatabase.getDatabase(getContext());
//        GhiChuDAO ghiChuDAO = appDatabase.ghiChuDAO();
//        this.ghiChuService = new GhiChuService(ghiChuDAO);
//    }
//
//    @Override
//    public View onCreateView(
//            LayoutInflater inflater, ViewGroup container,
//            Bundle savedInstanceState
//    ) {
//
//        binding = FragmentSecondBinding.inflate(inflater, container, false);
//        return binding.getRoot();
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        final ActivityResultLauncher<Intent> editNoteLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if (result.getResultCode() == RESULT_OK) {
//                            // Xử lý kết quả trả về từ Activity thêm ghi chú
//                            if (result.getData() != null) {
//                                // Kiểm tra và lấy dữ liệu ghi chú mới (nếu có)
//                                GhiChu newNote = result.getData().getParcelableExtra("new_note");
//                                if (newNote != null) {
//                                    // Truy cập FirstFragment và gọi phương thức addNewNote
//                                    FirstFragment firstFragment = (FirstFragment) getParentFragmentManager().findFragmentById(R.id.mainFrame1);
//                                    if (firstFragment != null) {
//                                        ArrayList a = (ArrayList) arrayList;
//                                        firstFragment.editNote(newNote,(result.getData().getIntExtra("position",-1)));
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//        );
//        SharedPreferences preferences = getContext().getSharedPreferences("ghiNhoDangNhap",MODE_PRIVATE);
//        taiKhoanGhiNho = preferences.getString("UID","");
//        AppDatabase appDatabase = AppDatabase.getDatabase(getContext().getApplicationContext());
//        GhiChuDAO ghiChuDAO = appDatabase.ghiChuDAO();
//        arrayList = ghiChuDAO.getPinedList(taiKhoanGhiNho);
//        recyclerView = view.findViewById(R.id.rclListGhiChu);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        layoutManager.setOrientation(layoutManager.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);
//        MaterialToolbar collapsingToolbar = requireActivity().findViewById(R.id.collapsingToolbar);
////        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
//        AppBarLayout topBarLayout = requireActivity().findViewById(R.id.topbarlayout);
//        AppBarLayout hiddenTopBarLayout = requireActivity().findViewById(R.id.hiddentopbarlayout);
//        MaterialToolbar topbar = requireActivity().findViewById(R.id.topbar);
//
//
//        ItemTouchHelper.Callback simpleCallback = new ItemTouchHelper.Callback() {
//            @Override
//            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//                int dragFlags = mDraggable ? ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END : 0; // Cho phép kéo lên và kéo xuống
//                return makeMovementFlags(dragFlags, 0);
//
//            }
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                int fromPosition = viewHolder.getAdapterPosition();
//                int toPosition = target.getAdapterPosition();
//                Collections.swap(arrayList, fromPosition, toPosition);
//                recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
//                if(firstMoved){
//                    ((MainActivity)getActivity()).hideTopBar();
//                    Window window = getActivity().getWindow();
//                    window.setStatusBarColor(getResources().getColor(R.color.white));
//                    firstMoved = false;
//                }
//                itemMoved = true;
//                for (int i = 0; i < arrayList.size(); i++) {
//                    arrayList.get(i).setOrder(i+1);
//                    ghiChuService.suaGhiChu(arrayList.get(i),arrayList.get(i).getMaGhiChu());
//                }
//                return true;
//            }
//            @Override
//            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
//                super.onSelectedChanged(viewHolder, actionState);
//                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
//                    isItemBeingDragged = true;
//                    if (viewHolder != null && isItemBeingDragged) {
//                        viewHolder.itemView.setBackgroundResource(R.drawable.background_drag);
//                    }
//                } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && itemMoved) {
//                    isItemBeingDragged = false;
//                    itemMoved = false;
//                    firstMoved = true;
//                    changeMode();
//                }
//            }
//            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//                super.clearView(recyclerView, viewHolder);
//                viewHolder.itemView.setBackgroundResource(R.drawable.itemborder); // Đặt nền về trạng thái bình thường
//            }
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//            }
//        };
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);
//        adapter = new GhichuAdapter((ArrayList<GhiChu>) arrayList,requireContext(),new GhichuAdapter.OnItemClickListener(){
//
//            @Override
//            public void onItemClick(GhiChu ghiChu, int position) {
//                GhichuAdapter.myViewHolder holder = (GhichuAdapter.myViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
//                if(!multiselect){
//                    Intent intent = new Intent(getContext(), SuaGhiChuActivity.class);
//                    intent.putExtra("ghiChuDaTao", ghiChu);
//                    intent.putExtra("loai","sua");
//                    intent.putExtra("position",position);
//                    editNoteLauncher.launch(intent);
//                } else{
////                    arrayList.get(position).setSelected(!arrayList.get(position).isSelected());
//                    ghiChu.setSelected(!ghiChu.isSelected());
//                    arrayList.set(position,ghiChu);
//                    if (arrayList.get(position).isSelected()) {
////                        ghiChu.setSelected(true);
//                        // Thay đổi màu nền khi mục được chọn
//                        holder.itemView.setSelected(true);
//                    } else {
//                        // Thay đổi màu nền khi mục không được chọn
//                        holder.itemView.setSelected(false);
//                        //ghiChu.setSelected(false);
//                        if(countSelectedItems()==0){
//                            ((MainActivity)getActivity()).hideTopBar();
//                            multiselect = false;
//                        }
//                    }
////                    adapter.notifyItemChanged(position);
//                    showCountSelectedItems();
//                }
//            }
//            public void onItemLongClick(GhiChu ghiChu, int position) {
//                GhichuAdapter.myViewHolder holder = (GhichuAdapter.myViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
//                holder.itemView.setSelected(true);
//                if(!multiselect){
//                    multiselect = true;
//                    ghiChu.setSelected(true);
//                    arrayList.set(position,ghiChu);
//                    holder.itemView.setSelected(true);
//                    Animation slideInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in);
//                    collapsingToolbar.startAnimation(slideInAnimation);
//                    topbar.setVisibility(View.INVISIBLE);
//                    topBarLayout.setVisibility(View.INVISIBLE);
//                    collapsingToolbar.setVisibility(View.VISIBLE);
//                    hiddenTopBarLayout.setVisibility(View.VISIBLE);
//                    Window window = getActivity().getWindow();
//                    window.setStatusBarColor(getResources().getColor(R.color.md_theme_light_primary));
//                } else {
//                    //ghiChu.setSelected(true);
//                    ghiChu.setSelected(!ghiChu.isSelected());
//                    arrayList.set(position,ghiChu);
//                    //holder.itemView.setSelected(true);
//                    if (arrayList.get(position).isSelected()) {
////                        ghiChu.setSelected(true);
//                        // Thay đổi màu nền khi mục được chọn
//                        holder.itemView.setSelected(true);
//                    } else {
//                        // Thay đổi màu nền khi mục không được chọn
//                        holder.itemView.setSelected(false);
//                        //ghiChu.setSelected(false);
//                        if(countSelectedItems()==0){
//                            ((MainActivity)getActivity()).hideTopBar();
//                            Window window = getActivity().getWindow();
//                            window.setStatusBarColor(getResources().getColor(R.color.white));
//                            changeMode();
//                        }
//                    }
//                }
////                adapter.notifyItemChanged(position);
//                showCountSelectedItems();
//            }
//        });
//        recyclerView.setAdapter(adapter);
//    }
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//    public void addNewNote(GhiChu newNote) {
//        if (newNote != null) {
//            // Thêm ghi chú mới vào danh sách và cập nhật RecyclerView
//            arrayList.add(0,newNote);
//            adapter.notifyItemInserted(0);
//        }
//    }
//    public void editNote(GhiChu newNote, int position) {
//        if (newNote != null) {
//            // Thêm ghi chú mới vào danh sách và cập nhật RecyclerView
//            arrayList.set(position,newNote);
//            ArrayList  a = (ArrayList) arrayList;
//            adapter.notifyItemChanged(position);
//        }
//    }
//    public void changeMode(){
//        multiselect = false;
//        Window window = getActivity().getWindow();
//        window.setStatusBarColor(getResources().getColor(R.color.white));
//        for (int i = 0; i < arrayList.size(); i++) {
//            int position = i;
//
//            // Đánh dấu phần tử trong danh sách không được chọn
//            GhiChu ghiChu = arrayList.get(position);
//            ghiChu.setSelected(false);
//            arrayList.set(position,ghiChu);
//            ghiChuService.suaGhiChu(arrayList.get(i),arrayList.get(i).getMaGhiChu());
//            // Tìm viewHolder của phần tử tại vị trí hiện tại
//            GhichuAdapter.myViewHolder holder = (GhichuAdapter.myViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
//            if (holder != null) {
//                // Đặt selected của itemView thành false
//                holder.itemView.setSelected(false);
//            }
//        }
//        // Cập nhật adapter
//        mDraggable = true;
//        adapter.notifyDataSetChanged();
//    }
//    public int countSelectedItems() {
//        int count = 0;
//        for (GhiChu ghiChu : arrayList) {
//            if (ghiChu.isSelected()) {
//                count++;
//            }
//        }
//        if (count >=1){
//            mDraggable = false;
//        } else {
//            mDraggable = true;
//        }
//        return count;
//    }
//    public void showCountSelectedItems(){
//        TextView selectedCount = requireActivity().findViewById(R.id.selectedCount);
//        selectedCount.setText(String.valueOf(countSelectedItems()));
//    }
//    public void markSelectedItemsAsDeleted() {
//        for (int i = arrayList.size() - 1; i >= 0; i--) {
//            GhiChu ghiChu = arrayList.get(i);
//            if (ghiChu.isSelected()) {
//                ghiChu.setDaXoa(1);
//                ghiChuService.giamOrder(ghiChu.getOrder(),taiKhoanGhiNho);
//                arrayList.remove(i);
//                ghiChuService.suaGhiChu(ghiChu, ghiChu.getMaGhiChu());
//                GhichuAdapter.myViewHolder holder = (GhichuAdapter.myViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
//                if (holder != null) {
//                    // Đặt selected của itemView thành false
//                    holder.itemView.setSelected(false);
//                }
//                adapter.notifyItemRemoved(i);
//            }
//        }
//        ((MainActivity) getActivity()).hideTopBar();
//        Window window = getActivity().getWindow();
//        window.setStatusBarColor(getResources().getColor(R.color.white));
//        multiselect = false;
//    }
//    public void reLoad(){
//        adapter.notifyDataSetChanged();
//    }
//}