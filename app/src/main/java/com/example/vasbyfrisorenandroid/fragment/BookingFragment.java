package com.example.vasbyfrisorenandroid.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasbyfrisorenandroid.R;
import com.example.vasbyfrisorenandroid.decoration.SpacesItemDecoration;
import com.example.vasbyfrisorenandroid.model.service.Service;
import com.example.vasbyfrisorenandroid.model.timeslot.OnTimeListener;
import com.example.vasbyfrisorenandroid.model.timeslot.TimeAdapter;
import com.example.vasbyfrisorenandroid.model.timeslot.TimeSlot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;


public class BookingFragment extends Fragment implements View.OnClickListener, OnTimeListener {

    private View rootView;
    private HorizontalCalendar calendar;
    private RecyclerView timeSlotRecyclerView;
    private RecyclerView.Adapter timeSlotAdapter;
    private LinearLayoutManager timeSlotLayoutManager;
    private List<TimeSlot> timeSlotList;
    private Service service;
    private TextView selectedServiceTitle, selectedServicePrice, barberName;
    private CircleImageView selectedServiceImg;
    private AppCompatButton bookButton;
    private DatabaseReference dbReference;
    private int id, selectedItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.booking, container, false);

        selectedServiceTitle = rootView.findViewById(R.id.selected_service_title);
        selectedServiceImg = rootView.findViewById(R.id.selected_service_img);
        selectedServicePrice = rootView.findViewById(R.id.selected_service_price);
        bookButton = rootView.findViewById(R.id.book_button);
        barberName = rootView.findViewById(R.id.barber_name);

        initCalendar();

        timeSlotList = getTimeSet(Calendar.getInstance().getTime());
        initTimeSlot();

        Bundle bundle = this.getArguments();

        if(bundle != null){
            service = bundle.getParcelable("service");
            selectedServiceTitle.setText(service.getServiceTitle());
            selectedServiceImg.setImageResource(service.getImgResource());
            selectedServicePrice.setText("Totalt: " + String.valueOf(service.getPrice()) + "kr");
            dbReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("bookings");
            initId();
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        calendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                //do something
                timeSlotList = getTimeSet(date.getTime());
                initTimeSlot();

            }
        });
        timeSlotRecyclerView.addItemDecoration(new SpacesItemDecoration(30));
        bookButton.setOnClickListener(this);
    }


    private void initCalendar(){

        Calendar startDate = Calendar.getInstance();

        /* ends after 3 weeks from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.WEEK_OF_YEAR, 3);

        calendar = new HorizontalCalendar.Builder(rootView, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(7)
                .configure()
                    .showTopText(false)
                    .textColor(Color.LTGRAY, rootView.getResources().getColor(R.color.white2))
                    .selectorColor(rootView.getResources().getColor(R.color.white2))
                    .sizeMiddleText(14)
                    .sizeBottomText(12)
                .end()
                .build();
    }

    private void initTimeSlot(){
        timeSlotRecyclerView = rootView.findViewById(R.id.recyclerView);
        timeSlotRecyclerView.setHasFixedSize(true);
        timeSlotAdapter = new TimeAdapter(timeSlotList, this);

        timeSlotLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        timeSlotRecyclerView.setLayoutManager(timeSlotLayoutManager);
        timeSlotRecyclerView.setAdapter(timeSlotAdapter);
    }

    private List<TimeSlot> getTimeSet(Date selectedDate){

        if(timeSlotList != null){
            timeSlotList.clear();
        }

        List<TimeSlot> result = new ArrayList<>();

        SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm");

        Calendar calendar = Calendar.getInstance();

        Date date;

        String day;

        if(selectedDate.getDay() > 0 && selectedDate.getDay() < 6) { //between monday to friday
            calendar.set(Calendar.HOUR_OF_DAY, 10);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            date = calendar.getTime();
            day = date24Format.format(date);
            result.add(0, new TimeSlot(day, true));

            for (int i = 1; i < 19; i++) {

                // add 15 minutes to the current time; the hour adjusts automatically!
                calendar.add(Calendar.MINUTE, 30);
                day = date24Format.format(calendar.getTime());

                result.add(new TimeSlot(day, true));
            }
        }else{
            calendar.set(Calendar.HOUR_OF_DAY, 11);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            date = calendar.getTime();
            day = date24Format.format(date);
            result.add(0, new TimeSlot(day, true));

            for (int i = 1; i < 15; i++) {

                // add 15 minutes to the current time; the hour adjusts automatically!
                calendar.add(Calendar.MINUTE, 30);
                day = date24Format.format(calendar.getTime());

                result.add(new TimeSlot(day, true));
            }
        }

        return result;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {


            case R.id.book_button:
                Map<String, Object> serviceInfo = new HashMap<>();
                Map<String, Object> bookedTimeInfo = new HashMap<>();
                serviceInfo.put("service", service);

                bookedTimeInfo.put("booked_date", calendar.getSelectedDate().getTime().getDate());
                bookedTimeInfo.put("booked_day", DayOfWeek.of(calendar.getSelectedDate().getTime().getDay()).toString());
                bookedTimeInfo.put("year", calendar.getSelectedDate().getTime().getYear() + 1900); //adding 1900 in order to get the year
                bookedTimeInfo.put("book_created", LocalDate.now().toString());
                bookedTimeInfo.put("time", timeSlotList.get(selectedItem).getTime());

                serviceInfo.put("booked_time", bookedTimeInfo);
                serviceInfo.put("barber", barberName.getText());
                //add barber too
                dbReference.child(String.valueOf(id + 1)).setValue(serviceInfo);
            break;

        }
    }

    private void initId(){
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    id = (int)snapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onTimeClick(View view, int position) {
        selectedItem = position;
    }
}
