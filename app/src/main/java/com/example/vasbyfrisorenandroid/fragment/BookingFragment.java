package com.example.vasbyfrisorenandroid.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasbyfrisorenandroid.R;
import com.example.vasbyfrisorenandroid.decoration.SpacesItemDecoration;
import com.example.vasbyfrisorenandroid.model.Date.Days;
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
    private DatabaseReference dbBookingReference, dbTimeSlotReference, dbBarbersReference;
    private int id, selectedItem;
    private ImageView barberSpinner, barberImg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.booking, container, false);

        selectedServiceTitle = rootView.findViewById(R.id.selected_service_title);
        selectedServiceImg = rootView.findViewById(R.id.selected_service_img);
        selectedServicePrice = rootView.findViewById(R.id.selected_service_price);
        bookButton = rootView.findViewById(R.id.book_button);
        barberName = rootView.findViewById(R.id.barber_name);
        barberSpinner = rootView.findViewById(R.id.spinner_drop_button);
        barberImg = rootView.findViewById(R.id.barber_img);

        initCalendar();
        dbTimeSlotReference = FirebaseDatabase.getInstance().getReference().child("Timeslot");

        /*
        //for testing
        String[] barbers = rootView.getContext().getResources().getStringArray(R.array.barbers);
        for (String barber : barbers) {
            initTimeSlotFirebase(barber);
        }*/

        //check if bugs turn up
        dbTimeSlotReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    Calendar c = Calendar.getInstance();
                    int day = c.get(Calendar.WEEK_OF_YEAR);
                    c.add(Calendar.WEEK_OF_YEAR, 2);
                    int lastCallDate = c.get(Calendar.WEEK_OF_YEAR);

                    if(day == lastCallDate) {

                        String[] barbers = rootView.getContext().getResources().getStringArray(R.array.barbers);
                        for (String barber : barbers) {
                            initTimeSlotFirebase(barber);
                        }
                    }

                }else{
                    String[] barbers = rootView.getContext().getResources().getStringArray(R.array.barbers);
                    for (String barber : barbers) {
                        initTimeSlotFirebase(barber);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //

        timeSlotList = new ArrayList<>();

        dbTimeSlotReference.child(barberName.getText().toString())
                .child(String.valueOf(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)))
                .child(Days.of(Calendar.getInstance().getTime().getDay()).name())
                .addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot data : snapshot.getChildren()) {

                    TimeSlot timeSlot = data.getValue(TimeSlot.class);

                    if (timeSlot != null) {

                        timeSlotList.add(timeSlot);
                    }
                }
                initTimeSlot();
                timeSlotRecyclerView.addItemDecoration(new SpacesItemDecoration(30));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Bundle bundle = this.getArguments();

        if(bundle != null){
            service = bundle.getParcelable("service");
            selectedServiceTitle.setText(service.getServiceTitle());
            selectedServiceImg.setImageResource(service.getImgResource());
            selectedServicePrice.setText("Totalt: " + String.valueOf(service.getPrice()) + "kr");
            dbBookingReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("bookings");
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

                dbTimeSlotReference
                        .child(barberName.getText().toString())
                        .child(String.valueOf(date.get(Calendar.WEEK_OF_YEAR)))
                        .child(Days.of(date.getTime().getDay()).name())
                        .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!timeSlotList.isEmpty())
                            timeSlotList.clear();

                        for(DataSnapshot data: snapshot.getChildren()){

                            TimeSlot timeSlot = data.getValue(TimeSlot.class);

                            if(timeSlot != null) {
                                timeSlotList.add(timeSlot);
                            }
                        }
                        initTimeSlot();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        calendar.refresh();
        bookButton.setOnClickListener(this);
        barberSpinner.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initCalendar(){

        Calendar startDate = Calendar.getInstance();

        /* ends after 2 weeks from now */
        Calendar endDate = Calendar.getInstance();

        endDate.add(Calendar.WEEK_OF_YEAR, 2);

        calendar = new HorizontalCalendar.Builder(rootView, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .configure()
                    .showTopText(false)
                    .textColor(Color.LTGRAY, rootView.getResources().getColor(R.color.white2))
                    .selectorColor(rootView.getResources().getColor(R.color.white2))
                    .sizeMiddleText(14)
                    .sizeBottomText(12)
                .end()
                .build();
    }

    private void initTimeSlotFirebase(String barber) {
        List<TimeSlot> result;
        Calendar calendar = Calendar.getInstance();
        int nrOfWeeksForward = 3;

        for(int i = 0; i < nrOfWeeksForward; i++) {

            int day = calendar.get(Calendar.DAY_OF_WEEK);
            int lastCallDate = Days.MONDAY.getDay();

            if(day == lastCallDate) {
                for (int j = 1; j < 6; j++) { //from monday to friday
                    result = getWeekDaysTimeSet();
                    dbTimeSlotReference.child(barber).child(String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR))).child(Days.of(j).name()).setValue(result);
                }
            }else{
                for (int j = day-1; j < 6; j++) { //from the day we are now to friday
                    result = getWeekDaysTimeSet();
                    dbTimeSlotReference.child(barber).child(String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR))).child(Days.of(j).name()).setValue(result);
                }
            }

            result = getWeekEndTimeSet();
            dbTimeSlotReference.child(barber).child(String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR))).child(Days.of(0).name()).setValue(result); //Sunday
            dbTimeSlotReference.child(barber).child(String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR))).child(Days.of(6).name()).setValue(result); //Saturday
            calendar.add(Calendar.WEEK_OF_YEAR, 1); //add up one week toward
            calendar.set(Calendar.DAY_OF_WEEK, 1); //starts from monday
        }
    }

    private void initTimeSlot(){
        timeSlotRecyclerView = rootView.findViewById(R.id.recyclerView);
        timeSlotRecyclerView.setHasFixedSize(true);
        timeSlotAdapter = new TimeAdapter(timeSlotList, this);

        timeSlotLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        timeSlotRecyclerView.setLayoutManager(timeSlotLayoutManager);
        timeSlotRecyclerView.setAdapter(timeSlotAdapter);
        timeSlotAdapter.notifyDataSetChanged();
    }

    private List<TimeSlot> getWeekDaysTimeSet(){
        List<TimeSlot> result = new ArrayList<>();

        SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm");

        Calendar calendar = Calendar.getInstance();

        Date date;

        String day;

        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        date = calendar.getTime();
        day = date24Format.format(date);
        result.add(0, new TimeSlot(day, true));

        for (int i = 1; i < 18; i++) {

            // add 15 minutes to the current time; the hour adjusts automatically!
            calendar.add(Calendar.MINUTE, 30);
            day = date24Format.format(calendar.getTime());

            result.add(new TimeSlot(day, true));
        }

        return result;
    }

    private List<TimeSlot> getWeekEndTimeSet(){
        List<TimeSlot> result = new ArrayList<>();

        SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm");

        Calendar calendar = Calendar.getInstance();

        Date date;

        String day;

        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        date = calendar.getTime();
        day = date24Format.format(date);
        result.add(0, new TimeSlot(day, true));

        for (int i = 1; i < 14; i++) {

            // add 15 minutes to the current time; the hour adjusts automatically!
            calendar.add(Calendar.MINUTE, 30);
            day = date24Format.format(calendar.getTime());

            result.add(new TimeSlot(day, true));
        }

        return result;
    }

    private void initId(){
        dbBookingReference.addValueEventListener(new ValueEventListener() {
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
    public void onClick(View v) {
        switch(v.getId()) {

            case R.id.spinner_drop_button:
                showPopUp(v);
            break;

            case R.id.book_button:

                Map<String, Object> serviceInfo = new HashMap<>();
                Map<String, Object> bookedTimeInfo = new HashMap<>();
                serviceInfo.put("service", service);

                bookedTimeInfo.put("booked_date", calendar.getSelectedDate().getTime().getDate());
                bookedTimeInfo.put("booked_day", Days.of(calendar.getSelectedDate().getTime().getDay()).name());
                bookedTimeInfo.put("year", calendar.getSelectedDate().getTime().getYear() + 1900); //adding 1900 in order to get the year
                bookedTimeInfo.put("book_created", LocalDate.now().toString());
                bookedTimeInfo.put("time_taken", timeSlotList.get(selectedItem).getTime());

                serviceInfo.put("booked_time", bookedTimeInfo);
                serviceInfo.put("barber", barberName.getText());

                dbBookingReference.child(String.valueOf(id + 1)).setValue(serviceInfo, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        HomeFragment fragment= new HomeFragment();
                        getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                                .replace(R.id.fragment_container, fragment)
                                .commit();

                        dbTimeSlotReference
                                .child(barberName.getText().toString()) //barber
                                .child(String.valueOf(calendar.getSelectedDate().get(Calendar.WEEK_OF_YEAR))) //week of year
                                .child(Days.of(calendar.getSelectedDate().getTime().getDay()).name()) //day
                                .child(String.valueOf(selectedItem)) //date
                                .child("available") //bool variable
                                .setValue(false);

                        Toast.makeText(v.getContext(), "Bokningen lyckades!", Toast.LENGTH_SHORT).show();
                    }
                });

            break;
        }
    }

    @Override
    public void onTimeClick(View view, int position) {
        selectedItem = position;
    }

    private void showPopUp(View v){
        PopupMenu popupMenu =  new PopupMenu(rootView.getContext(), v);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId()){

                    case R.id.barber1:
                        barberName.setText(R.string.first_barber);
                        barberImg.setImageResource(R.drawable.profile_img_test);
                        timeSlotList.clear();
                        dbTimeSlotReference.child(barberName.getText().toString())
                                .child(String.valueOf(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)))
                                .child(Days.of(Calendar.getInstance().getTime().getDay()).name())
                                .addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if(!timeSlotList.isEmpty())
                                            timeSlotList.clear();

                                        for(DataSnapshot data: snapshot.getChildren()){

                                            TimeSlot timeSlot = data.getValue(TimeSlot.class);

                                            if(timeSlot != null) {
                                                timeSlotList.add(timeSlot);
                                            }
                                        }
                                        initTimeSlot();
                                        calendar.goToday(true);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                        return true;

                    case R.id.barber2:
                        barberName.setText(R.string.second_barber);
                        barberImg.setImageResource(R.drawable.app_icon);
                        timeSlotList.clear();
                        dbTimeSlotReference.child(barberName.getText().toString())
                                .child(String.valueOf(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)))
                                .child(Days.of(Calendar.getInstance().getTime().getDay()).name())
                                .addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if(!timeSlotList.isEmpty())
                                            timeSlotList.clear();

                                        for(DataSnapshot data: snapshot.getChildren()){

                                            TimeSlot timeSlot = data.getValue(TimeSlot.class);

                                            if(timeSlot != null) {
                                                timeSlotList.add(timeSlot);
                                            }
                                        }
                                        initTimeSlot();
                                        calendar.goToday(true);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                        return true;

                    case R.id.barber3:
                        barberName.setText(R.string.third_barber);
                        barberImg.setImageResource(R.drawable.logo);
                        timeSlotList.clear();
                        dbTimeSlotReference.child(barberName.getText().toString())
                                .child(String.valueOf(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)))
                                .child(Days.of(Calendar.getInstance().getTime().getDay()).name())
                                .addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if(!timeSlotList.isEmpty())
                                            timeSlotList.clear();

                                        for(DataSnapshot data: snapshot.getChildren()){

                                            TimeSlot timeSlot = data.getValue(TimeSlot.class);

                                            if(timeSlot != null) {
                                                timeSlotList.add(timeSlot);
                                            }
                                        }
                                        initTimeSlot();
                                        calendar.goToday(true);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                        return true;

                    default:
                        return false;
                }
            }
        });
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

}
