package com.example.todoapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class NonFocusableRecyclerView extends RecyclerView {

    public NonFocusableRecyclerView(Context context) {
        super(context);
    }

    public NonFocusableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NonFocusableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        // Không làm gì cả để ngăn việc tập trung tự động
    }
}
