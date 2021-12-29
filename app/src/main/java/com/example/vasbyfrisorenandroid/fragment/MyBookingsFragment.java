package com.example.vasbyfrisorenandroid.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasbyfrisorenandroid.R;
import com.example.vasbyfrisorenandroid.model.mybooking.MyBooking;
import com.example.vasbyfrisorenandroid.model.mybooking.MyBookingAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyBookingsFragment extends Fragment {


    private View rootView;
    private List<MyBooking> myBookingList;
    private RelativeLayout existing_mybooking_div, empty_mybooking_div;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager layoutManager;

    private TextView title;
    private LinearLayout underline;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.mybooking, container, false);
        title = rootView.findViewById(R.id.mybooking_title);
        underline = rootView.findViewById(R.id.mybooking_underline);
        title.setVisibility(View.GONE);
        underline.setVisibility(View.GONE);
        myBookingList = new ArrayList<>();

        Bundle b = getArguments();
        int notifications;

        if(b != null){
            notifications = b.getInt("bookingCount");

            if(notifications > 0){
                existing_mybooking_div = rootView.findViewById(R.id.existing_mybooking_div);
                empty_mybooking_div = rootView.findViewById(R.id.empty_mybooking_div);

                if(existing_mybooking_div.getVisibility() == View.GONE) {

                    if(empty_mybooking_div.getVisibility() == View.VISIBLE) {
                        empty_mybooking_div.setVisibility(View.GONE);
                    }

                    existing_mybooking_div.setVisibility(View.VISIBLE);
;                   title.setVisibility(View.VISIBLE);
                    underline.setVisibility(View.VISIBLE);
                    initMyBookingData();
                }
            }
        }
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(myBookingList != null) {
            myBookingList.clear();
        }
    }

    private void initMyBookingData(){
        DatabaseReference bookingDbReference =  FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("bookings");

        bookingDbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {

                        MyBooking myBooking = data.getValue(MyBooking.class);

                        if (myBooking != null) {
                            myBookingList.add(myBooking);
                        }
                    }
                    initRecyclerView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initRecyclerView(){
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(rootView.getContext());
        adapter = new MyBookingAdapter(myBookingList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
