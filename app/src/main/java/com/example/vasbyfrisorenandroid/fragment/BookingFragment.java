package com.example.vasbyfrisorenandroid.fragment;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasbyfrisorenandroid.MainActivity;
import com.example.vasbyfrisorenandroid.R;
import com.example.vasbyfrisorenandroid.decoration.SpacesItemDecoration;
import com.example.vasbyfrisorenandroid.model.barber.Barber;
import com.example.vasbyfrisorenandroid.model.booking.BookedTime;
import com.example.vasbyfrisorenandroid.model.date.Days;
import com.example.vasbyfrisorenandroid.model.db.Database;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;


public class BookingFragment extends Fragment implements View.OnClickListener, OnTimeListener {

    private View rootView;
    private HorizontalCalendar calendar;
    private RecyclerView timeSlotRecyclerView;
    private List<TimeSlot> timeSlotList;
    private Service service;
    private TextView barberName;
    private AppCompatButton bookButton;
    private DatabaseReference dbBookingReference, dbTimeSlotReference, dbBarbersReference;
    private int id, selectedItem;
    private ImageView barberImg, backButton;
    private RelativeLayout barberBox;

    private static final int NOTIFICATION_ID = 1;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder notificationBuilder;


    private Database db;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.booking, container, false);
        db = new Database();
        selectedServiceTitle = rootView.findViewById(R.id.selected_service_title);
        selectedServiceImg = rootView.findViewById(R.id.selected_service_img);
        selectedServicePrice = rootView.findViewById(R.id.selected_service_price);
        TextView selectedServiceTitle = rootView.findViewById(R.id.selected_service_title);
        CircleImageView selectedServiceImg = rootView.findViewById(R.id.selected_service_img);
        TextView selectedServicePrice = rootView.findViewById(R.id.selected_service_price);
        bookButton = rootView.findViewById(R.id.book_button);
        barberName = rootView.findViewById(R.id.barber_name);
        barberBox = rootView.findViewById(R.id.barber_box);
        notificationManager = NotificationManagerCompat.from(rootView.getContext());
        barberImg = rootView.findViewById(R.id.barber_img);
        backButton = rootView.findViewById(R.id.backbutton);
        dbBarbersReference = FirebaseDatabase.getInstance().getReference().child("Barbers");
        timeSlotList = new ArrayList<>();
        //InitBarbers();
        initCalendar();
        dbTimeSlotReference = FirebaseDatabase.getInstance().getReference().child("Timeslot");

        if(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) == 1){
            resetTimeSlot();
            String[] barbers = rootView.getContext().getResources().getStringArray(R.array.barbers);
            db.addBarbersTimeslot(barbers);
        }

        //for getting current data
        db.getCurrentTimeslotsForSelectedBarber(barberName.getText().toString(),
                String.valueOf(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)),
                Objects.requireNonNull(Days.of(Calendar.getInstance().getTime().getDay())).name());

        initTimeSlot();
        timeSlotRecyclerView.addItemDecoration(new SpacesItemDecoration(40));

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            service = bundle.getParcelable("service");
            selectedServiceTitle.setText(service.getServiceTitle());
            selectedServiceImg.setImageResource(service.getImgResource());
            selectedServicePrice.setText("Totalt: " + service.getPrice() + "kr");
            dbBookingReference = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Users")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    .child("bookings");
            initId();
        }
        createNotificationChannel();
        createNotification();
        return rootView;
    }

    private void resetTimeSlot() {
        dbTimeSlotReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data: snapshot.getChildren()) {
                    data.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        //For selecting new date
        calendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                dbTimeSlotReference
                        .child(barberName.getText().toString())
                        .child(String.valueOf(date.get(Calendar.WEEK_OF_YEAR)))
                        .child(Objects.requireNonNull(Days.of(date.getTime().getDay())).name())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!timeSlotList.isEmpty())
                                    timeSlotList.clear();

                                for (DataSnapshot data : snapshot.getChildren()) {

                                    TimeSlot timeSlot = data.getValue(TimeSlot.class);

                                    if (timeSlot != null) {
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

        dbBarbersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot data : snapshot.getChildren()) {
                        Barber barber = data.getValue(Barber.class);
                        if (barber != null) {
                            if (barber.isAvailable()) {
                                barberName.setText(barber.getName());
                                updateTimeSlot();
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        calendar.refresh();
        bookButton.setOnClickListener(this);
        barberBox.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initCalendar() {

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


    private void InitBarbers() {
        String[] barbers = rootView.getContext().getResources().getStringArray(R.array.barbers);
        for (String barberName : barbers) {
            dbBarbersReference.child(barberName).setValue(new Barber(barberName, true));
        }
    }

    private void initTimeSlot() {
        timeSlotRecyclerView = rootView.findViewById(R.id.recyclerView);
        timeSlotRecyclerView.setHasFixedSize(true);
        RecyclerView.Adapter timeSlotAdapter = new TimeAdapter(timeSlotList, this);

        LinearLayoutManager timeSlotLayoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.HORIZONTAL, false);
        timeSlotRecyclerView.setLayoutManager(timeSlotLayoutManager);
        timeSlotRecyclerView.setAdapter(timeSlotAdapter);
        timeSlotAdapter.notifyDataSetChanged();
    }

    private void initId() {
        dbBookingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    id = (int) snapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "väsbyfrisören";
            String description = "notification";

            int important = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notification", name, important);
            channel.setDescription(description);

            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void createNotification() {
        Intent intent = new Intent(rootView.getContext(), MainActivity.class);

        intent.putExtra("notification", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(rootView.getContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        notificationBuilder = new NotificationCompat.Builder(requireActivity(), "notification")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Bokningstid")
                .setContentText("Din bokning gick igenom")
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.barber_box:
                showPopUp(v);
                break;

            case R.id.book_button:

                Map<String, Object> serviceInfo = new HashMap<>();
                serviceInfo.put("service", service);

                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String date = formatter.format(calendar.getSelectedDate().getTime());
                BookedTime bookedTime = new BookedTime
                        .BookedTimeBuilder(LocalDate.now().toString(), timeSlotList.get(selectedItem).getTime())
                        .week(calendar.getSelectedDate().get(Calendar.WEEK_OF_YEAR))
                        .bookedDate(date)
                        .bookedDay(Days.of(calendar.getSelectedDate().getTime().getDay()).name())
                        .year(calendar.getSelectedDate().getTime().getYear() + 1900)
                        .isChecked(false)
                        .build();

                serviceInfo.put("bookedTime", bookedTime);
                serviceInfo.put("barber", barberName.getText());

                dbBookingReference.child(String.valueOf(id)).setValue(serviceInfo, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        HomeFragment fragment = new HomeFragment();
                        getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                                .replace(R.id.fragment_container, fragment)
                                .commit();

                        updateTimeFromTimeSlot();

                        Toast.makeText(v.getContext(), "Bokningen lyckades!", Toast.LENGTH_SHORT).show();
                        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
                    }
                });
                break;
            case R.id.backbutton:
                Fragment fragment = new HomeFragment();
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                break;

        }
    }

    @Override
    public void onTimeClick(View view, int position) {
        selectedItem = position;
    }

    private void showPopUp(View v) {
        dbBarbersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Boolean> barbersStatus = new HashMap<>();

                addBarbersStatus(barbersStatus, snapshot);

                PopupMenu popupMenu = new PopupMenu(rootView.getContext(), v);
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.setGravity(Gravity.END);

                removeBarbersThatAreNotAvailable(barbersStatus, popupMenu);

                popupMenu.setOnMenuItemClickListener(menuItem -> {

                    switch (menuItem.getItemId()) {

                        case R.id.barber1:
                            barberName.setText(R.string.first_barber);
                            barberImg.setImageResource(R.drawable.profile_img_test);
                            updateTimeSlot();
                            return true;

                        case R.id.barber2:
                            barberName.setText(R.string.second_barber);
                            barberImg.setImageResource(R.drawable.app_icon);
                            updateTimeSlot();
                            return true;

                        case R.id.barber3:
                            barberName.setText(R.string.third_barber);
                            barberImg.setImageResource(R.drawable.logo);
                            updateTimeSlot();
                            return true;

                        default:
                            return false;
                    }
                });
                popupMenu.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addBarbersStatus(Map<String, Boolean> barbersStatus, DataSnapshot snapshot) {
        for (DataSnapshot barber : snapshot.getChildren()) {
            Barber b = barber.getValue(Barber.class);

            if (b != null) {
                barbersStatus.put(b.getName(), b.isAvailable());
            }
        }
    }

    private void removeBarbersThatAreNotAvailable(Map<String, Boolean> barbersStatus, PopupMenu popupMenu) {
        for (int i = 0; i < popupMenu.getMenu().size(); i++) {
            if (!barbersStatus.get(popupMenu.getMenu().getItem(i).getTitle())) {
                popupMenu.getMenu().getItem(i).setEnabled(false); //Gör att texten är gråfärgad
            }
        }
    }

    private void updateTimeSlot() {
        timeSlotList.clear();
        dbTimeSlotReference.child(barberName.getText().toString())
                .child(String.valueOf(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)))
                .child(Objects.requireNonNull(Days.of(Calendar.getInstance().getTime().getDay())).name())
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {

                        if (!timeSlotList.isEmpty())
                            timeSlotList.clear();

                        for (DataSnapshot data : snapshot1.getChildren()) {

                            TimeSlot timeSlot = data.getValue(TimeSlot.class);

                            if (timeSlot != null) {
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
    }

    private void updateTimeFromTimeSlot() {
        dbTimeSlotReference
                .child(barberName.getText().toString()) //barber
                .child(String.valueOf(calendar.getSelectedDate().get(Calendar.WEEK_OF_YEAR))) //week of year
                .child(Objects.requireNonNull(Days.of(calendar.getSelectedDate().getTime().getDay())).name()) //day
                .child(String.valueOf(selectedItem)) //date
                .child("available") //bool variable
                .setValue(false);
    }
}
