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
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.vasbyfrisorenandroid.R;
import com.example.vasbyfrisorenandroid.decoration.SpacesItemDecoration;
import com.example.vasbyfrisorenandroid.model.product.Product;
import com.example.vasbyfrisorenandroid.model.product.ProductAdapter;
import com.example.vasbyfrisorenandroid.model.service.OnServiceListener;
import com.example.vasbyfrisorenandroid.model.service.Service;
import com.example.vasbyfrisorenandroid.model.service.ServiceAdapter;
import com.example.vasbyfrisorenandroid.viewpager.SlidePagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements View.OnClickListener {

    private View rootView;

    //Other
    private FloatingActionButton profileImg;
    private TextView loginText, notificationBadge;
    private ImageButton contactUs, notificationBell;

    //Notification
    private int myBookingCount;

    //Firebase
    private FirebaseAuth auth;
    private FirebaseUser user;

    //ViewPager
    private ViewPager pager;
    private PagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home, container, false);
        contactUs = rootView.findViewById(R.id.contact_us);
        profileImg = rootView.findViewById(R.id.profile_img_btn);
        loginText = rootView.findViewById(R.id.login_text);
        notificationBell = rootView.findViewById(R.id.notification_bell);
        CreatePageView();
        notificationBadge = rootView.findViewById(R.id.notification_badge);
        auth = FirebaseAuth.getInstance();
        return rootView;
    }

    private void CreatePageView() {
        pager = rootView.findViewById(R.id.pager);
        List<Fragment> list = new ArrayList<>();
        list.add(new ServicePageFragment());
        list.add(new ProductPageFragment());
        pagerAdapter = new SlidePagerAdapter(getActivity().getSupportFragmentManager(), list);
        pager.setAdapter(pagerAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        user = auth.getCurrentUser();

        if (user != null) {
            loginText.setText(user.getDisplayName());
            initNotificationCount();
            CreatePageView();
        } else {
            FirebaseAuth.getInstance().signOut();
        }

        contactUs.setOnClickListener(this);
        profileImg.setOnClickListener(this);
        loginText.setOnClickListener(this);
        notificationBell.setOnClickListener(this);
    }

    public void onDestroy() {
        super.onDestroy();
        pager.clearOnPageChangeListeners();
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;
        switch (v.getId()) {

            case R.id.contact_us:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:0859031881"));
                startActivity(intent);
                break;

            case R.id.profile_img_btn:

                if (user != null) {
                    fragment = new ProfileFragment();

                    getFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                            .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                } else {
                    fragment = new EmailLoginFragment();

                    getFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                            .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                }
                break;

            case R.id.login_text:
                FirebaseUser user = auth.getCurrentUser();

                if (user == null) {
                    fragment = new EmailLoginFragment();
                    getFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                            .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                } else {
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
        }
    }

    private void initNotificationCount() {

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(auth.getCurrentUser().getUid())
                .child("bookings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                myBookingCount = (int) snapshot.getChildrenCount();
                if (myBookingCount > 0)
                    notificationBadge.setText(String.valueOf(myBookingCount));
                else
                    notificationBadge.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
