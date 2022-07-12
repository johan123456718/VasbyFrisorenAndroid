package com.example.vasbyfrisorenandroid.fragment;


import android.os.Bundle;
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
import com.example.vasbyfrisorenandroid.model.mybooking.MyBooking;
import com.example.vasbyfrisorenandroid.model.mybooking.MyBookingAdapter;
import com.example.vasbyfrisorenandroid.model.mybooking.OnMyBookingListener;
import com.example.vasbyfrisorenandroid.model.service.Service;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.empty_booking, container, false);
        title = rootView.findViewById(R.id.mybooking_title);
        underline = rootView.findViewById(R.id.pickedmybooking_underline);
        backButton = rootView.findViewById(R.id.backbutton);
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
    public void onStart() {
        super.onStart();
        backButton.setOnClickListener(this);
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

                    if(myBookingList != null){
                        myBookingList.clear();
                    }
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
        adapter = new MyBookingAdapter(myBookingList, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onServiceClick(View view, int position) {

        DatabaseReference bookingDbReference = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("bookings")
                .child(String.valueOf(position + 1))
                .child("bookedTime")
                .child("checked");

        Service service = myBookingList.get(position).getService();
        BookedTime bookedTime = myBookingList.get(position).getBookedTime();
        String barber = myBookingList.get(position).getBarber();

        bookingDbReference.setValue(true)
                .addOnCompleteListener(task -> {
                    Fragment fragment = new PickedMyBookingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("service", service);
                    bundle.putParcelable("bookedTime", bookedTime);
                    bundle.putString("barber", barber);
                    bundle.putInt("id", (position + 1));
                    bundle.putInt("timeSlotIndex", position);
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
