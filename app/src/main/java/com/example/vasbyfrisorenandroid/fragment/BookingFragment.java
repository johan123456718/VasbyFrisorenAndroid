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
import com.example.vasbyfrisorenandroid.model.db.callbacks.BarberCallback;
import com.example.vasbyfrisorenandroid.model.db.callbacks.BookingCallback;
import com.example.vasbyfrisorenandroid.model.db.callbacks.TimeslotCallback;
import com.example.vasbyfrisorenandroid.model.service.Service;
import com.example.vasbyfrisorenandroid.model.timeslot.OnTimeListener;
import com.example.vasbyfrisorenandroid.model.timeslot.TimeAdapter;
import com.example.vasbyfrisorenandroid.model.timeslot.TimeSlot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
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
    private int selectedItem;
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
        TextView selectedServiceTitle = rootView.findViewById(R.id.selected_service_title);
        CircleImageView selectedServiceImg = rootView.findViewById(R.id.selected_service_img);
        TextView selectedServicePrice = rootView.findViewById(R.id.selected_service_price);
        bookButton = rootView.findViewById(R.id.book_button);
        barberName = rootView.findViewById(R.id.barber_name);
        barberBox = rootView.findViewById(R.id.barber_box);
        notificationManager = NotificationManagerCompat.from(rootView.getContext());
        barberImg = rootView.findViewById(R.id.barber_img);
        backButton = rootView.findViewById(R.id.backbutton);
        timeSlotList = new ArrayList<>();
        //InitBarbers();
        initCalendar();

        if(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) == 1){
            db.resetTimeslot();
            String[] barbers = rootView.getContext().getResources().getStringArray(R.array.barbers);
            db.addBarbersTimeslot(barbers);
        }

        //for getting current data
        db.getCurrentTimeslotsForSelectedBarber(barberName.getText().toString(),
                String.valueOf(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)),
                Objects.requireNonNull(Days.of(Calendar.getInstance().getTime().getDay())).name(),
                new TimeslotCallback() {
                    @Override
                    public void callback(List<TimeSlot> timeslots) {
                        timeSlotList = timeslots;
                        initTimeSlot(timeSlotList);
                        timeSlotRecyclerView.addItemDecoration(new SpacesItemDecoration(40));
                    }
                });

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            service = bundle.getParcelable("service");
            selectedServiceTitle.setText(service.getServiceTitle());
            selectedServiceImg.setImageResource(service.getImgResource());
            selectedServicePrice.setText("Totalt: " + service.getPrice() + "kr");
        }
        createNotificationChannel();
        createNotification();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        //For selecting new date
        calendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                db.getCurrentTimeslotsForSelectedBarber(barberName.getText().toString(),
                        String.valueOf(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)),
                        Objects.requireNonNull(Days.of(Calendar.getInstance().getTime().getDay())).name(),
                        new TimeslotCallback() {
                            @Override
                            public void callback(List<TimeSlot> timeslots) {
                                timeSlotList = timeslots;
                                initTimeSlot(timeSlotList);
                            }
                        });
            }
        });

        db.getFirstAvailableBarber(new BarberCallback() {
            @Override
            public void callback(Map<String, Boolean> barberStatus) {

            }

            @Override
            public void callback(Barber barber) {
                barberName.setText(barber.getName());
                db.getCurrentTimeslotsForSelectedBarber(barberName.getText().toString(),
                        String.valueOf(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)),
                        Objects.requireNonNull(Days.of(Calendar.getInstance().getTime().getDay())).name(),
                        new TimeslotCallback() {
                            @Override
                            public void callback(List<TimeSlot> timeslots) {
                                timeSlotList = timeslots;
                                initTimeSlot(timeSlotList);
                            }
                        });
                calendar.goToday(true);
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
           // dbBarbersReference.child(barberName).setValue(new Barber(barberName, true));
        }
    }

    private void initTimeSlot(List<TimeSlot> timeSlotList) {
        timeSlotRecyclerView = rootView.findViewById(R.id.recyclerView);
        timeSlotRecyclerView.setHasFixedSize(true);
        RecyclerView.Adapter timeSlotAdapter = new TimeAdapter(timeSlotList, this);

        LinearLayoutManager timeSlotLayoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.HORIZONTAL, false);
        timeSlotRecyclerView.setLayoutManager(timeSlotLayoutManager);
        timeSlotRecyclerView.setAdapter(timeSlotAdapter);
        timeSlotAdapter.notifyDataSetChanged();
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
                db.getBookingId(new BookingCallback() {
                    @Override
                    public void callback(int id) {
                        Map<String, Object> serviceInfo = createServiceInfo();
                        db.addBooking(id, serviceInfo);
                        HomeFragment fragment = new HomeFragment();
                        getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                                .replace(R.id.fragment_container, fragment)
                                .commit();

                        db.updateTimeslotStatus(barberName.getText().toString(),
                                String.valueOf(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)),
                                Objects.requireNonNull(Days.of(Calendar.getInstance().getTime().getDay())).name(),
                                selectedItem);

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
        db.getBarberStatus(new BarberCallback() {
            @Override
            public void callback(Map<String, Boolean> barbersStatus) {
                PopupMenu popupMenu = new PopupMenu(rootView.getContext(), v);
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.setGravity(Gravity.END);
                removeBarbersFromPopup(barbersStatus, popupMenu);
                popupMenu.setOnMenuItemClickListener(menuItem -> {

                    switch (menuItem.getItemId()) {

                        case R.id.barber1:
                            updateBarberBookingUI(getResources().getString(R.string.first_barber), R.drawable.profile_img_test);
                            return true;

                        case R.id.barber2:
                            updateBarberBookingUI(getResources().getString(R.string.second_barber), R.drawable.app_icon);
                            return true;

                        case R.id.barber3:
                            updateBarberBookingUI(getResources().getString(R.string.third_barber), R.drawable.app_icon);
                            return true;

                        default:
                            return false;
                    }
                });
                popupMenu.show();
            }

            @Override
            public void callback(Barber barbers) {

            }
        });
    }

    private void updateBarberBookingUI(String barberName, int imgResource){
        this.barberName.setText(barberName);
        this.barberImg.setImageResource(imgResource);
        db.getCurrentTimeslotsForSelectedBarber(barberName,
                String.valueOf(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)),
                Objects.requireNonNull(Days.of(Calendar.getInstance().getTime().getDay())).name(),
                new TimeslotCallback() {
                    @Override
                    public void callback(List<TimeSlot> timeslots) {
                        timeSlotList = timeslots;
                        initTimeSlot(timeSlotList);
                        calendar.goToday(true);
                    }
                });
    }

    private void removeBarbersFromPopup(Map<String, Boolean> barbersStatus, PopupMenu popupMenu) {
        for (int i = 0; i < popupMenu.getMenu().size(); i++) {
            if (!barbersStatus.get(popupMenu.getMenu().getItem(i).getTitle())) {
                popupMenu.getMenu().getItem(i).setEnabled(false); //Gör att texten är gråfärgad
            }
        }
    }

    private Map<String, Object> createServiceInfo(){
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
        return serviceInfo;
    }
}
