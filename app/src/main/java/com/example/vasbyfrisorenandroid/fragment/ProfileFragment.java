package com.example.vasbyfrisorenandroid.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private View rootView;

    private List<Setting> settingList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager layoutManager;

    private FirebaseAuth auth;

    private TextView profileName, profileEmail;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.profile, container, false);
        auth = FirebaseAuth.getInstance();
        profileName = rootView.findViewById(R.id.profile_name);
        profileEmail = rootView.findViewById(R.id.profile_email);

        initRecyclerView();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            profileName.setText(user.getDisplayName());
            profileEmail.setText(user.getEmail());
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        settingList.clear();
    }

    private void initRecyclerView(){
        creatingSettingList();

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(rootView.getContext());
        adapter = new SettingAdapter(settingList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void creatingSettingList(){
        settingList = new ArrayList<>();
        settingList.add(new Setting(R.drawable.email_icon, "Email", "Här kan du ändra ditt email"));
        settingList.add(new Setting(R.drawable.contact2, "Telefon", "Här kan du ändra ditt telefonNr"));
        settingList.add(new Setting(R.drawable.email_icon, "Email", "Loco"));
        settingList.add(new Setting(R.drawable.email_icon, "Email", "Loco"));
        settingList.add(new Setting(R.drawable.email_icon, "Email", "Loco"));
        settingList.add(new Setting(R.drawable.email_icon, "Email", "Loco"));
        settingList.add(new Setting(R.drawable.email_icon, "Email", "Loco"));
        settingList.add(new Setting(R.drawable.email_icon, "Email", "Loco"));
        settingList.add(new Setting(R.drawable.person_stroke, "Radera konto", "Loco"));
        settingList.add(new Setting(0, "Logga ut", null));
    }
}
