package com.example.vasbyfrisorenandroid.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasbyfrisorenandroid.R;
import com.example.vasbyfrisorenandroid.decoration.SpacesItemDecoration;
import com.example.vasbyfrisorenandroid.model.timeslot.TimeAdapter;
import com.example.vasbyfrisorenandroid.model.timeslot.TimeSlot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;


public class BookingFragment extends Fragment {

    private View rootView;
    private HorizontalCalendar calendar;
    private RecyclerView timeSlotRecyclerView;
    private RecyclerView.Adapter timeSlotAdapter;
    private LinearLayoutManager timeSlotLayoutManager;
    private List<TimeSlot> timeSlotList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.booking, container, false);
        initCalendar();

        timeSlotList = getTimeSet(Calendar.getInstance().getTime());
        initTimeSlot();

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
        timeSlotAdapter = new TimeAdapter(timeSlotList);

        timeSlotLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        timeSlotRecyclerView.setLayoutManager(timeSlotLayoutManager);
        timeSlotRecyclerView.setAdapter(timeSlotAdapter);
        timeSlotRecyclerView.addItemDecoration(new SpacesItemDecoration(30));
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
}
