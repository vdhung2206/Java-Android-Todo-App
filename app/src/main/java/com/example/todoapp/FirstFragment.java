package com.example.todoapp;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.Adapters.GhichuAdapter;
import com.example.todoapp.DAO.GhiChuDAO;
import com.example.todoapp.Entity.GhiChu;
import com.example.todoapp.Services.GhiChuService;
import com.example.todoapp.databinding.FragmentFirstBinding;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirstFragment extends Fragment {
    private boolean multiselect = false;
    String taiKhoanGhiNho;
    GhiChuService ghiChuService;
    List<GhiChu> arrayList;
    GhichuAdapter adapter;
    private FragmentFirstBinding binding;
    private RecyclerView recyclerView;
    private boolean isItemBeingDragged = false;
    private boolean itemMoved = false;
    private boolean firstMoved = true;
    boolean mDraggable = true;
    boolean hasSeparator = false;
    boolean hasKhacSeparator = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase appDatabase = AppDatabase.getDatabase(getContext());
        GhiChuDAO ghiChuDAO = appDatabase.ghiChuDAO();
        this.ghiChuService = new GhiChuService(ghiChuDAO);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ActivityResultLauncher<Intent> editNoteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            if (result.getData() != null) {
                                GhiChu newNote = result.getData().getParcelableExtra("new_note");
                                if (newNote != null) {
                                    FirstFragment firstFragment = (FirstFragment) getParentFragmentManager().findFragmentById(R.id.mainFrame);
                                    if (firstFragment != null) {
                                        firstFragment.editNote(newNote, (result.getData().getIntExtra("position", -1)));
                                    }
                                }
                            }
                        }
                    }
                }
        );
        SharedPreferences preferences = getContext().getSharedPreferences("ghiNhoDangNhap", MODE_PRIVATE);
        taiKhoanGhiNho = preferences.getString("UID", "");
        AppDatabase appDatabase = AppDatabase.getDatabase(getContext().getApplicationContext());
        GhiChuDAO ghiChuDAO = appDatabase.ghiChuDAO();
        arrayList = ghiChuDAO.getList(taiKhoanGhiNho);
        recyclerView = view.findViewById(R.id.rclListGhiChu);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        MaterialToolbar collapsingToolbar = requireActivity().findViewById(R.id.collapsingToolbar);
        AppBarLayout topBarLayout = requireActivity().findViewById(R.id.topbarlayout);
        AppBarLayout hiddenTopBarLayout = requireActivity().findViewById(R.id.hiddentopbarlayout);
        MaterialToolbar topbar = requireActivity().findViewById(R.id.topbar);
        ItemTouchHelper.Callback simpleCallback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = mDraggable ? ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END : 0; // Cho phép kéo lên và kéo xuống

                if (arrayList.get(viewHolder.getAdapterPosition()).getIsPin() == 2) {
                    dragFlags = 0;
                }
                return makeMovementFlags(dragFlags, 0);

            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                int separatorKhacIndex = 0;
                for(int i = 0; i <arrayList.size(); i++){
                    if(arrayList.get(i).getIsPin()==2){
                        separatorKhacIndex = i;
                    }
                }
                if (arrayList.get(fromPosition).getIsPin() == 1) {
                    if(toPosition >= separatorKhacIndex || arrayList.get(toPosition).getIsPin()==2){
                        return false;
                    }
                } else if (arrayList.get(fromPosition).getIsPin() == 0){
                    if(toPosition <= separatorKhacIndex && separatorKhacIndex !=0){
                        return false;
                    }
                }
                Collections.swap(arrayList, fromPosition, toPosition);
                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                if (firstMoved) {
                    ((MainActivity) getActivity()).hideTopBar();
                    Window window = getActivity().getWindow();
                    window.setStatusBarColor(getResources().getColor(R.color.white));
                    firstMoved = false;
                }
                itemMoved = true;
                for (int i = 0; i < arrayList.size(); i++) {
                    arrayList.get(i).setOrder(i + 1);
                    ghiChuService.suaGhiChu(arrayList.get(i), arrayList.get(i).getMaGhiChu());
                }
                return true;
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    isItemBeingDragged = true;
                    if (viewHolder != null && isItemBeingDragged) {
                        viewHolder.itemView.setBackgroundResource(R.drawable.background_drag);
                    }
                } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && itemMoved) {
                    isItemBeingDragged = false;
                    itemMoved = false;
                    firstMoved = true;
                    changeMode();
                }
            }

            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackgroundResource(R.drawable.itemborder); // Đặt nền về trạng thái bình thường
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        adapter = new GhichuAdapter((ArrayList<GhiChu>) arrayList, requireContext(), new GhichuAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(GhiChu ghiChu, int position) {
                GhichuAdapter.myViewHolder holder = (GhichuAdapter.myViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                if (!multiselect) {
                    Intent intent = new Intent(getContext(), SuaGhiChuActivity.class);
                    intent.putExtra("ghiChuDaTao", ghiChu);
                    intent.putExtra("loai", "sua");
                    intent.putExtra("position", position);
                    editNoteLauncher.launch(intent);
                } else {
                    ghiChu.setSelected(!ghiChu.isSelected());
                    arrayList.set(position, ghiChu);
                    if (arrayList.get(position).isSelected()) {
                        holder.itemView.setSelected(true);
                    } else {
                        holder.itemView.setSelected(false);
                        if (countSelectedItems() == 0) {
                            ((MainActivity) getActivity()).hideTopBar();
                            multiselect = false;
                        }
                    }
                    showCountSelectedItems();
                }
            }

            public void onItemLongClick(GhiChu ghiChu, int position) {
                GhichuAdapter.myViewHolder holder = (GhichuAdapter.myViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                holder.itemView.setSelected(true);
                if (!multiselect) {
                    multiselect = true;
                    ghiChu.setSelected(true);
                    arrayList.set(position, ghiChu);
                    Animation slideInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in);
                    collapsingToolbar.startAnimation(slideInAnimation);
                    topbar.setVisibility(View.INVISIBLE);
                    topBarLayout.setVisibility(View.INVISIBLE);
                    collapsingToolbar.setVisibility(View.VISIBLE);
                    hiddenTopBarLayout.setVisibility(View.VISIBLE);
                    Window window = getActivity().getWindow();
                    window.setStatusBarColor(getResources().getColor(R.color.md_theme_light_primary));
                } else {
                    ghiChu.setSelected(!ghiChu.isSelected());
                    arrayList.set(position, ghiChu);
                    if (arrayList.get(position).isSelected()) {
                        holder.itemView.setSelected(true);
                    } else {
                        holder.itemView.setSelected(false);
                        //ghiChu.setSelected(false);
                        if (countSelectedItems() == 0) {
                            ((MainActivity) getActivity()).hideTopBar();
                            Window window = getActivity().getWindow();
                            window.setStatusBarColor(getResources().getColor(R.color.white));
                            changeMode();
                        }
                    }
                }
                showCountSelectedItems();
            }
        });
        recyclerView.setAdapter(adapter);
        addSeparatorIfNeeded();
        removeSeparatorKhacIfNeeded();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void addNewNote(GhiChu newNote) {
        if (newNote != null) {
            if (!hasSeparator) {
                arrayList.add(0, newNote);
                adapter.notifyItemInserted(0);
            } else {
                int lastPinIndex = -1;
                for (int i = arrayList.size() - 1; i >= 0; i--) {
                    GhiChu ghiChu = arrayList.get(i);
                    if (ghiChu.getIsPin() == 1) {
                        lastPinIndex = i;
                        break;
                    }
                }
                if(hasKhacSeparator){
                    arrayList.add(lastPinIndex + 2, newNote);
                    adapter.notifyItemInserted(lastPinIndex + 2);
                } else {
                    addKhacSeparatorIfNeeded();
                    arrayList.add(lastPinIndex + 2, newNote);
                    adapter.notifyItemInserted(lastPinIndex + 2);
                }
            }
        }
        capNhatOrder();
    }
    public void editNote(GhiChu newNote, int position) {
        if (newNote != null) {
            arrayList.set(position, newNote);
            adapter.notifyItemChanged(position);
        }
    }
    public void changeMode() {
        multiselect = false;
        Window window = getActivity().getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.white));
        for (int i = 0; i < arrayList.size(); i++) {
            int position = i;
            GhiChu ghiChu = arrayList.get(position);
            ghiChu.setSelected(false);
            arrayList.set(position, ghiChu);
            ghiChuService.suaGhiChu(arrayList.get(i), arrayList.get(i).getMaGhiChu());
            GhichuAdapter.myViewHolder holder = (GhichuAdapter.myViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
            if (holder != null) {
                holder.itemView.setSelected(false);
            }
        }
        mDraggable = true;
        adapter.notifyDataSetChanged();
    }
    public int countSelectedItems() {
        int count = 0;
        for (GhiChu ghiChu : arrayList) {
            if (ghiChu.isSelected()) {
                count++;
            }
        }
        mDraggable = count < 1;
        return count;
    }
    public void showCountSelectedItems() {
        TextView selectedCount = requireActivity().findViewById(R.id.selectedCount);
        selectedCount.setText(String.valueOf(countSelectedItems()));
    }
    public void markSelectedItemsAsDeleted() {
        for (int i = arrayList.size() - 1; i >= 0; i--) {
            GhiChu ghiChu = arrayList.get(i);
            if (ghiChu.isSelected()) {
                ghiChu.setDaXoa(1);
                ghiChu.setSelected(false);
                ghiChuService.giamOrder(ghiChu.getOrder(), taiKhoanGhiNho);
                arrayList.remove(i);
                ghiChuService.suaGhiChu(ghiChu, ghiChu.getMaGhiChu());
                GhichuAdapter.myViewHolder holder = (GhichuAdapter.myViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    holder.itemView.setSelected(false);
                }
                adapter.notifyItemRemoved(i);
            }
        }
        ((MainActivity) getActivity()).hideTopBar();
        Window window = getActivity().getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.white));
        multiselect = false;
        removeSeparatorsIfNeeded();
        removeSeparatorKhacIfNeeded();
    }
    public void setNotifyForSelectedNotes(String date, String time, int repeatInterval) {
        for (GhiChu ghiChu : arrayList) {
            if (ghiChu.isSelected()) {
                ghiChu.setCoLoiNhac(1);
                ghiChu.setDaXong(0);
                ghiChu.setDaGui(0);
                ghiChu.setNgayNhac(date);
                ghiChu.setGioNhac(time);
                ghiChu.setNhacLapLai(repeatInterval);
                ghiChuService.suaGhiChu(ghiChu, ghiChu.getMaGhiChu());
            }
        }
        adapter.notifyDataSetChanged();
    }
    public void deleteNotifyForSelectedNotes() {
        for (GhiChu ghiChu : arrayList) {
            if (ghiChu.isSelected()) {
                ghiChu.setCoLoiNhac(0);
                ghiChu.setNgayNhac("");
                ghiChu.setGioNhac("");
                ghiChu.setNhacLapLai(0);
                ghiChuService.suaGhiChu(ghiChu, ghiChu.getMaGhiChu());
            }
        }
        adapter.notifyDataSetChanged();
    }
    public ArrayList<GhiChu> getSelectedNotes() {
        ArrayList<GhiChu> selectedNotes = new ArrayList<>();
        for (GhiChu ghiChu : arrayList) {
            if (ghiChu.isSelected()) {
                selectedNotes.add(ghiChu);
            }
        }
        return selectedNotes;
    }
    public void PinChoices(){
        boolean hasUnpinnedItem = false;
        for (GhiChu ghiChu : arrayList) {
            if (ghiChu.isSelected() && ghiChu.getIsPin() == 0) {
                hasUnpinnedItem = true;
                break;
            }
        }
        if (hasUnpinnedItem) {
            markSelectedItemsAsPined();
        } else {
            unpinSelectedItems();
        }
    }
    public void unpinSelectedItems() {
        List<GhiChu> selectedItems = new ArrayList<>();
        int selectedCount = 0;
        addKhacSeparatorIfNeeded();
        for (int i = 0; i < arrayList.size(); i++) {
            GhiChu ghiChu = arrayList.get(i);
            if (ghiChu.isSelected()) {
                selectedItems.add(ghiChu);
                selectedCount++;
            }
        }

        if (selectedCount > 0) {
            // Xác định vị trí của phần tử đã chọn trong danh sách
            int[] selectedIndexes = new int[selectedCount];
            for (int i = 0; i < selectedCount; i++) {
                int selectedIndex = arrayList.indexOf(selectedItems.get(i));
                selectedIndexes[i] = selectedIndex;
            }
            int separatorKhacIndex = 0;
            for(int i = 0; i <arrayList.size(); i++){

                if(arrayList.get(i).getIsPin()==2){
                    separatorKhacIndex = i;
                }
            }
            for (int i = 0; i < selectedCount; i++) {
                GhiChu selectedGhiChu = selectedItems.get(i);
                int fromPosition = arrayList.indexOf(selectedGhiChu);
                int toPosition = separatorKhacIndex;
                arrayList.remove(fromPosition);
                arrayList.add(toPosition, selectedGhiChu);
                // Thông báo cho Adapter biết phần tử đã được di chuyển
                adapter.notifyItemMoved(fromPosition, toPosition);
                // Cập nhật trạng thái của phần tử đã di chuyển
                selectedGhiChu.setSelected(false);
                selectedGhiChu.setIsPin(0);
                ghiChuService.suaGhiChu(selectedGhiChu, selectedGhiChu.getMaGhiChu());

                GhichuAdapter.myViewHolder holder = (GhichuAdapter.myViewHolder) recyclerView.findViewHolderForAdapterPosition(toPosition);
                if (holder != null) {
                    // Đặt selected của itemView thành false
                    holder.itemView.setSelected(false);
                }
            }

            ((MainActivity) getActivity()).hideTopBar();
            Window window = getActivity().getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.white));
            multiselect = false;
        }
        capNhatOrder();
        removeSeparatorsIfNeeded();
    }
    public void markSelectedItemsAsPined() {
        List<GhiChu> selectedItems = new ArrayList<>();
        int selectedCount = 0;

        for (int i = 0; i < arrayList.size(); i++) {
            GhiChu ghiChu = arrayList.get(i);
            if (ghiChu.isSelected()) {
                selectedItems.add(ghiChu);
                selectedCount++;
            }
        }
        if (selectedCount > 0) {
            int[] selectedIndexes = new int[selectedCount];
            for (int i = 0; i < selectedCount; i++) {
                int selectedIndex = arrayList.indexOf(selectedItems.get(i));
                selectedIndexes[i] = selectedIndex;
            }
            for (int i = 0; i < selectedCount; i++) {
                int fromPosition = selectedIndexes[i];
                int toPosition = i;
                if (hasSeparator) {
                    toPosition++;
                }
                GhiChu selectedGhiChu = arrayList.remove(fromPosition);
                arrayList.add(toPosition, selectedGhiChu);
                adapter.notifyItemMoved(fromPosition, toPosition);
                selectedGhiChu.setSelected(false);
                selectedGhiChu.setIsPin(1);
                ghiChuService.suaGhiChu(selectedGhiChu, selectedGhiChu.getMaGhiChu());

                GhichuAdapter.myViewHolder holder = (GhichuAdapter.myViewHolder) recyclerView.findViewHolderForAdapterPosition(toPosition);
                if (holder != null) {
                    holder.itemView.setSelected(false);
                }
            }
            ((MainActivity) getActivity()).hideTopBar();
            Window window = getActivity().getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.white));
            multiselect = false;
        }
        capNhatOrder();
        addSeparatorIfNeeded();
        removeSeparatorKhacIfNeeded();
    }
    public void capNhatOrder() {
        for (int i = 0; i < arrayList.size(); i++) {
            GhiChu ghiChu = arrayList.get(i);
            ghiChu.setOrder(i + 1);
            ghiChuService.suaGhiChu(ghiChu, ghiChu.getMaGhiChu());
        }
    }
    public void removeSeparatorsIfNeeded() {
        boolean hasPinnedItems = false;
        for (GhiChu ghiChu : arrayList) {
            if (ghiChu.getIsPin() == 1) {
                hasPinnedItems = true;
                break;
            }
        }
        if (!hasPinnedItems) {
            for (int i = arrayList.size() - 1; i >= 0; i--) {
                if (arrayList.get(i).getIsPin() == 2) {
                    arrayList.remove(i);
                    adapter.notifyItemRemoved(i);
                }
            }
            hasSeparator = false;
        }
    }
    public void removeSeparatorKhacIfNeeded() {
        boolean allItemsPinned = true;
        for (GhiChu ghiChu : arrayList) {
            if (ghiChu.getIsPin() ==0) {
                allItemsPinned = false;
                break;
            }
        }
        if (allItemsPinned && hasKhacSeparator) {
            for (int i = arrayList.size() - 1; i >= 0; i--) {
                if (arrayList.get(i).getIsPin() == 2 && arrayList.get(i).getNoiDung().equals("Khác")) {
                    arrayList.remove(i);
                    adapter.notifyItemRemoved(i);
                    hasKhacSeparator = false;
                    break;
                }
            }
        }
    }
    public void addKhacSeparatorIfNeeded() {
        boolean hasUnpinnedItems = false;
        for (GhiChu ghiChu : arrayList) {
            if (ghiChu.getIsPin() != 1) {
                hasUnpinnedItems = true;
                break;
            }
        }
        if (hasUnpinnedItems && !hasKhacSeparator) {
            GhiChu separator1 = new GhiChu();
            separator1.setNoiDung("Khác");
            separator1.setIsPin(2);
            int lastPinIndex = 0;
            for (int i = arrayList.size() - 1; i >= 0; i--) {
                if (arrayList.get(i).getIsPin() == 1) {
                    lastPinIndex = i;
                    break;
                }
            }
            arrayList.add(lastPinIndex + 1, separator1);
            adapter.notifyItemInserted(lastPinIndex + 2);
            hasKhacSeparator = true;
        }
    }
    public void addSeparatorIfNeeded() {
        if (!hasSeparator) {
            boolean pinFound = false;
            int lastPinIndex = -1;
            for (GhiChu ghiChu : arrayList) {
                if (ghiChu.getIsPin() == 1) {
                    pinFound = true;
                    break;
                }
            }
            for (int i = arrayList.size() - 1; i >= 0; i--) {
                GhiChu ghiChu = arrayList.get(i);
                if (ghiChu.getIsPin() == 1) {
                    pinFound = true;
                    lastPinIndex = i;
                    break;
                }
            }
            if (pinFound) {
                GhiChu separator = new GhiChu();
                separator.setNoiDung("Được ghim");
                separator.setIsPin(2);
                arrayList.add(0, separator);
                GhiChu separator1 = new GhiChu();
                separator1.setNoiDung("Khác");
                hasKhacSeparator = true;
                separator1.setIsPin(2);
                arrayList.add(lastPinIndex + 2, separator1);
                adapter.notifyItemInserted(0);
                adapter.notifyItemInserted(lastPinIndex + 2);
                hasSeparator = true;
            }
        }
    }
    public void reLoad() {
        adapter.notifyDataSetChanged();
    }
}