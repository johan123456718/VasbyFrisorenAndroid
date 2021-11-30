package com.example.vasbyfrisorenandroid.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasbyfrisorenandroid.R;
import com.example.vasbyfrisorenandroid.model.service.Service;
import com.example.vasbyfrisorenandroid.model.service.ServiceAdapter;
import com.example.vasbyfrisorenandroid.model.setting.Setting;
import com.example.vasbyfrisorenandroid.model.setting.SettingAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private View rootView;
    private List<Setting> settingList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager layoutManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.profile, container, false);
        initRecyclerView();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        settingList.clear();
    }

    private void initRecyclerView(){
        settingList = new ArrayList<>();
        settingList.add(new Setting(R.drawable.email_icon, "Email", "Loco"));
        settingList.add(new Setting(R.drawable.contact2, "Telefon", "Loco"));
        settingList.add(new Setting(R.drawable.email_icon, "Email", "Loco"));
        settingList.add(new Setting(R.drawable.email_icon, "Email", "Loco"));
        settingList.add(new Setting(R.drawable.email_icon, "Email", "Loco"));
        settingList.add(new Setting(R.drawable.email_icon, "Email", "Loco"));
        settingList.add(new Setting(R.drawable.email_icon, "Email", "Loco"));
        settingList.add(new Setting(R.drawable.email_icon, "Email", "Loco"));
        settingList.add(new Setting(R.drawable.person_stroke, "Radera konto", "Loco"));

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(rootView.getContext());
        adapter = new SettingAdapter(settingList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
