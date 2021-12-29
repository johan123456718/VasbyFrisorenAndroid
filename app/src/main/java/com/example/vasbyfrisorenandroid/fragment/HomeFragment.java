package com.example.vasbyfrisorenandroid.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasbyfrisorenandroid.R;
import com.example.vasbyfrisorenandroid.decoration.SpacesItemDecoration;
import com.example.vasbyfrisorenandroid.model.product.Product;
import com.example.vasbyfrisorenandroid.model.product.ProductAdapter;
import com.example.vasbyfrisorenandroid.model.service.OnServiceListener;
import com.example.vasbyfrisorenandroid.model.service.Service;
import com.example.vasbyfrisorenandroid.model.service.ServiceAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements View.OnClickListener, OnServiceListener {

    private View rootView;

    //Service
    private List<Service> serviceList;
    private RecyclerView serviceRecyclerView;
    private RecyclerView.Adapter serviceAdapter;
    private LinearLayoutManager serviceLayoutManager;

    //Product
    private List<Product> productList;
    private RecyclerView productRecyclerView;
    private RecyclerView.Adapter productAdapter;
    private GridLayoutManager productLayoutManager;

    //Other
    private FloatingActionButton profileImg;
    private TextView loginText, notificationBadge;
    private ImageView rViewScrollButton;
    private ImageButton contactUs, notificationBell;

    //Notification

    private int myBookingCount;

    //Firebase
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
        notificationBadge = rootView.findViewById(R.id.notification_badge);

        auth = FirebaseAuth.getInstance();

        initServiceRecyclerView();
        initProductRecyclerView();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        user = auth.getCurrentUser();
        
        if(user != null){
            loginText.setText(user.getDisplayName());
            initNotificationCount();
        }else{
            FirebaseAuth.getInstance().signOut();
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

    private void initServiceRecyclerView(){
        serviceList = new ArrayList<>();
        serviceList.add(new Service(R.drawable.klippning, "Klippning", 250));
        serviceList.add(new Service(R.drawable.tvatt,"Line2", 250));
        serviceList.add(new Service(R.drawable.skagg, "Line3", 250));
        serviceList.add(new Service(R.drawable.nopp, "Line4", 250));
        serviceList.add(new Service(R.drawable.logo, "Line5", 0));
        serviceList.add(new Service(R.drawable.logo, "Line6", 0));
        serviceList.add(new Service(R.drawable.logo, "Line7", 0));
        serviceList.add(new Service(R.drawable.logo, "Line8", 0));
        serviceList.add(new Service(R.drawable.logo, "Line9", 0));
        serviceList.add(new Service(R.drawable.logo, "Line10", 0));
        serviceList.add(new Service(R.drawable.logo, "Line11", 0));
        serviceList.add(new Service(R.drawable.logo, "Line12", 0));
        serviceList.add(new Service(R.drawable.logo, "Line13", 0));
        serviceList.add(new Service(R.drawable.logo, "Line14", 0));

        serviceRecyclerView = rootView.findViewById(R.id.recyclerView);
        serviceRecyclerView.setNestedScrollingEnabled(false);
        serviceLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        serviceRecyclerView.setLayoutManager(serviceLayoutManager);
        serviceRecyclerView.setHasFixedSize(true);

        serviceAdapter = new ServiceAdapter(serviceList, this);
        serviceRecyclerView.setAdapter(serviceAdapter);
    }

    private void initProductRecyclerView(){

        productList = new ArrayList<>();
        productList.add(new Product(R.drawable.logo, "Schampo", 230));
        productList.add(new Product(R.drawable.logo, "Schampo", 230));
        productList.add(new Product(R.drawable.logo, "Schampo", 230));
        productList.add(new Product(R.drawable.logo, "Schampo", 230));
        productList.add(new Product(R.drawable.logo, "Schampo", 230));
        productList.add(new Product(R.drawable.logo, "Schampo", 230));

        productRecyclerView = rootView.findViewById(R.id.recyclerView2);
        productRecyclerView.setHasFixedSize(true);
        productLayoutManager = new GridLayoutManager(getContext(), 3);
        productAdapter = new ProductAdapter(productList);

        productRecyclerView.setLayoutManager(productLayoutManager);
        productRecyclerView.setAdapter(productAdapter);
        productRecyclerView.addItemDecoration(new SpacesItemDecoration(30));
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

                if(user != null) {
                    fragment = new ProfileFragment();

                    getFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                            .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                }else{
                    fragment = new EmailLoginFragment();

                    getFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                            .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                }
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

                fragment = new MyBookingsFragment();
                Bundle data = new Bundle();
                data.putInt("bookingCount", myBookingCount);
                fragment.setArguments(data);

                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                break;

            case R.id.recyclerViewScroll_button:

                if (serviceRecyclerView.canScrollVertically(1)) { //-1 up, 1 down
                    serviceRecyclerView.smoothScrollToPosition(serviceLayoutManager.findFirstVisibleItemPosition() + 4);
                }
                //notification_nr++;
                break;
        }
    }

    private void initNotificationCount(){

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(auth.getCurrentUser().getUid())
                .child("bookings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                myBookingCount = (int) snapshot.getChildrenCount();
                if(myBookingCount > 0)
                    notificationBadge.setText(String.valueOf(myBookingCount));
                else
                    notificationBadge.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onServiceClick(View v, int position) {

        AppCompatActivity activity = (AppCompatActivity) v.getContext();

        Fragment fragment = new BookingFragment();
        Service service = serviceList.get(position);

        Bundle bundle = new Bundle();
        bundle.putParcelable("service", service);
        fragment.setArguments(bundle);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }
}
