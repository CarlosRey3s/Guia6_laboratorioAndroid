package com.example.loginproject.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginproject.R;

public class ActivityMain2Binding {
    public final RecyclerView mainRecyclerView;
    public final ImageView imgAgregar;
    private final View root;

    private ActivityMain2Binding(View root) {
        this.root = root;
        mainRecyclerView = root.findViewById(R.id.mainRecyclerView);
        imgAgregar = root.findViewById(R.id.imgAgregar);
    }

    @NonNull
    public static ActivityMain2Binding inflate(@NonNull LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.activity_main2, parent, attachToParent);
        return new ActivityMain2Binding(root);
    }

    @NonNull
    public static ActivityMain2Binding inflate(@NonNull LayoutInflater inflater) {
        View root = inflater.inflate(R.layout.activity_main2, null, false);
        return new ActivityMain2Binding(root);
    }

    @NonNull
    public View getRoot() {
        return root;
    }
}

