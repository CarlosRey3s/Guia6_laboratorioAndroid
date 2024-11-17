package com.example.loginproject.UI.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginproject.Dtos.Section;
import com.example.loginproject.Models.Presupuesto;
import com.example.loginproject.R;
import com.example.loginproject.UI.viewHolders.MainViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MainRecycleAdapter extends RecyclerView.Adapter<MainViewHolder> {
    private List<Section> sectionList;
    private List<Presupuesto> sectionItems;
    private final ChildRecycleAdapter.OnItemChildClickListener onChildItemClickListener;

    public MainRecycleAdapter(List<Presupuesto> sectionItems, ChildRecycleAdapter.OnItemChildClickListener itemClickListener) {
        this.sectionItems = sectionItems != null ? sectionItems : new ArrayList<>();
        this.onChildItemClickListener = itemClickListener;
        this.sectionList = new ArrayList<>();
        sectionList.add(new Section("ACTIVAS", onlyActiveBudget()));
        sectionList.add(new Section("COMPLETADAS", onlyNonActiveBudget()));
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.section_row, parent, false);
        return new MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        Section section = sectionList.get(position);
        String sectionName = section.getSectionName();
        List<Presupuesto> items;

        switch (sectionName) {
            case "ACTIVAS":
                items = onlyActiveBudget();
                break;
            case "COMPLETADAS":
                items = onlyNonActiveBudget();
                break;
            default:
                items = new ArrayList<>();
        }

        holder.sectionName.setText(sectionName);
        ChildRecycleAdapter childRecyclerAdapter = new ChildRecycleAdapter(items, onChildItemClickListener);
        holder.childRcv.setAdapter(childRecyclerAdapter);
    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    public void setDataList(List<Presupuesto> dataList) {
        this.sectionItems = dataList;
        this.sectionList.clear();
        sectionList.add(new Section("ACTIVAS", onlyActiveBudget()));
        sectionList.add(new Section("COMPLETADAS", onlyNonActiveBudget()));
        notifyDataSetChanged();
    }

    private List<Presupuesto> onlyActiveBudget() {
        List<Presupuesto> activeBudgets = new ArrayList<>();
        for (Presupuesto presupuesto : sectionItems) {
            if (presupuesto.isActivo()) {
                activeBudgets.add(presupuesto);
            }
        }
        return activeBudgets;
    }

    private List<Presupuesto> onlyNonActiveBudget() {
        List<Presupuesto> nonActiveBudgets = new ArrayList<>();
        for (Presupuesto presupuesto : sectionItems) {
            if (!presupuesto.isActivo()) {
                nonActiveBudgets.add(presupuesto);
            }
        }
        return nonActiveBudgets;
    }
}
