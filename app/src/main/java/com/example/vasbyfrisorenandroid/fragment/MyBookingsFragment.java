package com.example.vasbyfrisorenandroid.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasbyfrisorenandroid.R;
import com.example.vasbyfrisorenandroid.model.booking.BookedTime;
import com.example.vasbyfrisorenandroid.model.db.Database;
import com.example.vasbyfrisorenandroid.model.db.callbacks.BookingCallback;
import com.example.vasbyfrisorenandroid.model.mybooking.MyBooking;
import com.example.vasbyfrisorenandroid.model.mybooking.MyBookingAdapter;
import com.example.vasbyfrisorenandroid.model.mybooking.OnMyBookingListener;
import com.example.vasbyfrisorenandroid.model.service.Service;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyBookingsFragment extends Fragment implements OnMyBookingListener, View.OnClickListener {
    private View rootView;
    private List<MyBooking> myBookingList;
    private RelativeLayout existing_mybooking_div, empty_mybooking_div;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager layoutManager;
    private TextView title;
    private LinearLayout underline;
    private ImageView backButton;
    private Database db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.empty_booking, container, false); // Will show empty_mybooking_div from the beginning
        title = rootView.findViewById(R.id.mybooking_title);
        underline = rootView.findViewById(R.id.pickedmybooking_underline);
        backButton = rootView.findViewById(R.id.backbutton);
        title.setVisibility(View.GONE);
        underline.setVisibility(View.GONE);
        myBookingList = new ArrayList<>();
        db = new Database();

        Bundle b = getArguments();
        int notifications;

        if (b != null) {
            notifications = b.getInt("bookingCount");
            changeViewForMyBookings(notifications);
        }
        return rootView;
    }

    private void changeViewForMyBookings(int notifications) {
        if (notifications > 0) { // Should only show existing_mybooking_div if there are bookings and start switching
            existing_mybooking_div = rootView.findViewById(R.id.existing_mybooking_div);
            empty_mybooking_div = rootView.findViewById(R.id.empty_mybooking_div);

            if (existing_mybooking_div.getVisibility() == View.GONE) {

                if (empty_mybooking_div.getVisibility() == View.VISIBLE) {
                    empty_mybooking_div.setVisibility(View.GONE);
                }

                existing_mybooking_div.setVisibility(View.VISIBLE);

                title.setVisibility(View.VISIBLE);
                underline.setVisibility(View.VISIBLE);
                initMyBookingData();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        backButton.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myBookingList != null) {
            myBookingList.clear();
        }
    }

    private void initMyBookingData() {
        db.getMyBookings(new BookingCallback() {
            @Override
            public void callback(int id) {

            }

            @Override
            public void callback(List<MyBooking> myBookings) {
                myBookingList = myBookings;
                initRecyclerView();
            }
        });
    }

    private void initRecyclerView() {
        Log.d("[MyBookingsFragment].initRecyclerView", "Starting init RecyclerView");
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(rootView.getContext());
        adapter = new MyBookingAdapter(myBookingList, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onServiceClick(View view, int position) {
        db.updateBookingTimeStatus(position, new BookingCallback() {
            @Override
            public void callback(int id) {

            }

            @Override
            public void callback(List<MyBooking> myBookings) {

            }
        }).addOnCompleteListener(task -> {
            Fragment fragment = new PickedMyBookingFragment();
            Service service = myBookingList.get(position).getService();
            BookedTime bookedTime = myBookingList.get(position).getBookedTime();
            String barber = myBookingList.get(position).getBarber();
            Bundle bundle = new Bundle();
            bundle.putParcelable("service", service);
            bundle.putParcelable("bookedTime", bookedTime);
            bundle.putString("barber", barber);
            bundle.putInt("selectedItemId", (position));
            if (getArguments() != null) {
                bundle.putInt("bookingCount", getArguments().getInt("bookingCount"));
            }
            fragment.setArguments(bundle);
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backbutton:
                Fragment fragment = new HomeFragment();
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                break;
        }
    }
}
