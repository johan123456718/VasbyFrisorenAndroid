package com.example.vasbyfrisorenandroid.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasbyfrisorenandroid.R;
import com.example.vasbyfrisorenandroid.model.notification.Notification;
import com.example.vasbyfrisorenandroid.model.notification.NotificationAdapter;
import com.example.vasbyfrisorenandroid.model.setting.Setting;
import com.example.vasbyfrisorenandroid.model.setting.SettingAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {


    private View rootView;
    private List<Notification> notificationList;
    private RelativeLayout existing_notification_div, empty_notification_div;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager layoutManager;

    private TextView title;
    private LinearLayout underline;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.notification, container, false);
        title = rootView.findViewById(R.id.notification_title);
        underline = rootView.findViewById(R.id.notification_underline);
        title.setVisibility(View.GONE);
        underline.setVisibility(View.GONE);

        Bundle b = getArguments();
        int notifications = 0;

        if(b != null){
            notifications = b.getInt("lol");

            if(notifications > 0){
                existing_notification_div = rootView.findViewById(R.id.existing_notification_div);
                empty_notification_div = rootView.findViewById(R.id.empty_notification_div);

                if(existing_notification_div.getVisibility() == View.GONE) {

                    if(empty_notification_div.getVisibility() == View.VISIBLE) {
                        empty_notification_div.setVisibility(View.GONE);
                    }

                    existing_notification_div.setVisibility(View.VISIBLE);
;                   title.setVisibility(View.VISIBLE);
                    underline.setVisibility(View.VISIBLE);
                    initRecyclerView();
                }
            }
        }
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(notificationList != null) {
            notificationList.clear();
        }
    }

    private void initRecyclerView(){
        notificationList = new ArrayList<>();
        notificationList.add(new Notification(R.drawable.ic_launcher_background, "Du har bokat din tid för klippning", "15 min sen"));
        notificationList.add(new Notification(R.drawable.ic_launcher_background, "gkmfdkgmfdlkgmdflkmgfdklmgl", "15 min sen"));
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(rootView.getContext());
        adapter = new NotificationAdapter(notificationList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
