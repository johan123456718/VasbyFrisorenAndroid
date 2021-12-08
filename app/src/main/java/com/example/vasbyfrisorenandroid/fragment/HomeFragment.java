package com.example.vasbyfrisorenandroid.fragment;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasbyfrisorenandroid.MainActivity;
import com.example.vasbyfrisorenandroid.R;
import com.example.vasbyfrisorenandroid.model.service.Service;
import com.example.vasbyfrisorenandroid.model.service.ServiceAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private List<Service> serviceList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private GridLayoutManager layoutManager;
    private ImageButton contactUs, notificationBell;
    private FloatingActionButton profileImg;
    private TextView loginText, notificationBadge;
    private ImageView rViewScrollButton;


    private static final int NOTIFICATION_ID = 1;
    private NotificationManagerCompat  notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private int notification_nr;

    private FirebaseAuth auth;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home, container, false);
        contactUs = rootView.findViewById(R.id.contact_us);
        profileImg = rootView.findViewById(R.id.profile_img_btn);
        loginText = rootView.findViewById(R.id.login_text);
        notificationBell = rootView.findViewById(R.id.notification_bell);
        rViewScrollButton = rootView.findViewById(R.id.recyclerViewScroll_button);
        notificationManager = NotificationManagerCompat.from(rootView.getContext());
        notificationBadge = rootView.findViewById(R.id.notification_badge);

        auth = FirebaseAuth.getInstance();

        createNotificationChannel();
        createNotification();
        initRecyclerView();
        notification_nr = 0;
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        user = auth.getCurrentUser();
        
        if(user != null){
            loginText.setText(user.getDisplayName());
        }
        contactUs.setOnClickListener(this);
        profileImg.setOnClickListener(this);
        loginText.setOnClickListener(this);
        notificationBell.setOnClickListener(this);
        rViewScrollButton.setOnClickListener(this);
    }

    public void onDestroy() {
        super.onDestroy();
        serviceList.clear();
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        switch(v.getId()) {

            case R.id.contact_us:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:0859031881"));
                startActivity(intent);
                break;

            case R.id.profile_img_btn:
                fragment = new ProfileFragment();

                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                break;

            case R.id.login_text:
                FirebaseUser user = auth.getCurrentUser();

                if(user == null) {
                    fragment = new EmailLoginFragment();
                    getFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                            .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                }else{
                    fragment = new ProfileFragment();
                    getFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                            .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                }
            break;

            case R.id.notification_bell:

                fragment = new NotificationFragment();
                Bundle data = new Bundle();
                data.putInt("lol", notification_nr);
                fragment.setArguments(data);

                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                break;

            case R.id.recyclerViewScroll_button:

                if (recyclerView.canScrollVertically(1)) { //-1 up, 1 down
                    recyclerView.smoothScrollToPosition(layoutManager.findFirstVisibleItemPosition() + 4);
                }
                notification_nr++;
                notificationBadge.setText(String.valueOf(notification_nr));
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
            break;
        }
    }

    private void createNotification() {
        Intent intent = new Intent(rootView.getContext(), MainActivity.class);

        intent.putExtra("notification", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(rootView.getContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        notificationBuilder = new NotificationCompat.Builder(getActivity(), "notification")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Bokningstid")
            .setContentText("Du har 15 min kvar tills din bokning startar.")
             .setStyle(new NotificationCompat.BigTextStyle()
                     .bigText(getString(R.string.bigText_notification))
                     .setBigContentTitle("Bokningstid")
                     .setSummaryText("Bokning")
             )
             .setContentIntent(pendingIntent)
             .setAutoCancel(true)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setPriority(Notification.PRIORITY_DEFAULT);
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "väsbyfrisören";
            String description = "notification";

            int important = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notification", name, important);
            channel.setDescription(description);

            NotificationManager manager = (NotificationManager) getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void initRecyclerView(){
        serviceList = new ArrayList<>();
        serviceList.add(new Service(R.drawable.logo, "Line"));
        serviceList.add(new Service(R.drawable.logo, "Line2"));
        serviceList.add(new Service(R.drawable.logo, "Line3"));
        serviceList.add(new Service(R.drawable.logo, "Line4"));
        serviceList.add(new Service(R.drawable.logo, "Line5"));
        serviceList.add(new Service(R.drawable.logo, "Line6"));
        serviceList.add(new Service(R.drawable.logo, "Line7"));
        serviceList.add(new Service(R.drawable.logo, "Line8"));
        serviceList.add(new Service(R.drawable.logo, "Line9"));
        serviceList.add(new Service(R.drawable.logo, "Line10"));
        serviceList.add(new Service(R.drawable.logo, "Line11"));
        serviceList.add(new Service(R.drawable.logo, "Line12"));
        serviceList.add(new Service(R.drawable.logo, "Line13"));
        serviceList.add(new Service(R.drawable.logo, "Line14"));

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getContext(), 2);
        adapter = new ServiceAdapter(serviceList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
