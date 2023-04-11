package com.example.vasbyfrisorenandroid.model.db;

import androidx.annotation.NonNull;

import com.example.vasbyfrisorenandroid.model.barber.Barber;
import com.example.vasbyfrisorenandroid.model.date.Days;
import com.example.vasbyfrisorenandroid.model.db.callbacks.BarberCallback;
import com.example.vasbyfrisorenandroid.model.db.callbacks.BookingCallback;
import com.example.vasbyfrisorenandroid.model.db.callbacks.TimeslotCallback;
import com.example.vasbyfrisorenandroid.model.timeslot.TimeSlot;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Database implements DatabaseInterface {
    private FirebaseDatabase db;
    private DatabaseReference dbBookingReference, dbTimeSlotReference, dbBarbersReference;
    private List<TimeSlot> currentTimeslot;
    private Map<String, Boolean> barbersStatus;
    private Barber barber;
    public Database() {
        db = FirebaseDatabase.getInstance();
        currentTimeslot = new ArrayList<>();
        barbersStatus = new HashMap<>();
        dbBarbersReference = db.getReference().child("Barbers");
        dbTimeSlotReference = db.getReference().child("Timeslot");
        dbBookingReference = db
                .getReference()
                .child("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("bookings");
    }

    @Override
    public void addBarbersTimeslot(String[] barbers) {
        dbTimeSlotReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    for (String barber : barbers) {
                        initTimeSlotFirebase(barber);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void getCurrentTimeslotsForSelectedBarber(String barberName, String weekOfYear, String day, TimeslotCallback firebaseCallback) {
        dbTimeSlotReference.child(barberName)
                .child(weekOfYear)
                .child(Objects.requireNonNull(day))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currentTimeslot.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            TimeSlot timeSlot = data.getValue(TimeSlot.class);
                            if (timeSlot != null) {
                                currentTimeslot.add(timeSlot);
                            }
                        }
                        firebaseCallback.callback(currentTimeslot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void resetTimeslot() {
        dbTimeSlotReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    data.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateTimeslotStatus(String barberName, String weekOfYear, String day, int selectedItem){
        dbTimeSlotReference
                .child(barberName) //barber
                .child(weekOfYear) //week of year
                .child(day) //day
                .child(String.valueOf(selectedItem)) //date
                .child("available") //bool variable
                .setValue(false);
    }

    @Override
    public void getBookingId(BookingCallback firebaseCallback) {
        dbBookingReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int id = 0;
                if (snapshot.exists()) {
                    id = (int) snapshot.getChildrenCount();
                }
                firebaseCallback.callback(id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void getBarberStatus(BarberCallback firebaseCallback) {
        dbBarbersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot barber : snapshot.getChildren()) {
                    Barber b = barber.getValue(Barber.class);
                    if (b != null) {
                        barbersStatus.put(b.getName(), b.isAvailable());
                    }
                }
                firebaseCallback.callback(barbersStatus);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void addBooking(int id, Map<String, Object> serviceInfo) {
        dbBookingReference.child(String.valueOf(id)).setValue(serviceInfo);
    }

    @Override
    public void getFirstAvailableBarber(BarberCallback firebaseCallback) {
        dbBarbersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Barber _barber = data.getValue(Barber.class);
                        if (_barber != null) {
                            if (_barber.isAvailable()) {
                                barber = _barber;
                                break;
                            }
                        }
                    }
                    firebaseCallback.callback(barber);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initTimeSlotFirebase(String barber) {
        List<TimeSlot> result;
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int weeks = 52;

        for (int i = currentWeek; i < weeks; i++) {
            int daySelect = calendar.get(Calendar.DAY_OF_WEEK);
            int lastCallDate = Days.MONDAY.getDay();

            if (daySelect == lastCallDate) {
                for (int j = 1; j < 6; j++) { //from monday to friday
                    result = getWeekDaysTimeSet();
                    dbTimeSlotReference.child(barber)
                            .child(String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)))
                            .child(Objects.requireNonNull(Days.of(j)).name())
                            .setValue(result);
                }
            } else {
                for (int j = daySelect - 1; j < 6; j++) { //from the daySelect we are now to friday
                    result = getWeekDaysTimeSet();
                    dbTimeSlotReference.child(barber)
                            .child(String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)))
                            .child(Objects.requireNonNull(Days.of(j)).name())
                            .setValue(result);
                }
            }

            dbTimeSlotReference.child(barber)
                    .child(String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)))
                    .child(Objects.requireNonNull(Days.of(0)).name())
                    .setValue(getWeekEndTimeSet(DayOfWeek.SUNDAY)); //Sunday
            dbTimeSlotReference.child(barber)
                    .child(String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)))
                    .child(Objects.requireNonNull(Days.of(6)).name())
                    .setValue(getWeekEndTimeSet(DayOfWeek.SATURDAY)); //Saturday
            calendar.add(Calendar.WEEK_OF_YEAR, 1); //add up one week toward
            calendar.set(Calendar.DAY_OF_WEEK, 1); //starts from monday
        }
    }

    private List<TimeSlot> getWeekDaysTimeSet() {
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

        for (int i = 1; i < 19; i++) {

            // add 15 minutes to the current time; the hour adjusts automatically!
            calendar.add(Calendar.MINUTE, 30);
            day = date24Format.format(calendar.getTime());

            result.add(new TimeSlot(day, true));
        }

        return result;
    }

    private List<TimeSlot> getWeekEndTimeSet(DayOfWeek dayOfWeek) {
        List<TimeSlot> result = new ArrayList<>();
        SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        Date date;
        String day;

        if (dayOfWeek == DayOfWeek.SUNDAY) {
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
        } else if (dayOfWeek == DayOfWeek.SATURDAY) {
            calendar.set(Calendar.HOUR_OF_DAY, 10);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            date = calendar.getTime();
            day = date24Format.format(date);
            result.add(0, new TimeSlot(day, true));

            for (int i = 1; i < 17; i++) {

                // add 15 minutes to the current time; the hour adjusts automatically!
                calendar.add(Calendar.MINUTE, 30);
                day = date24Format.format(calendar.getTime());

                result.add(new TimeSlot(day, true));
            }
        }
        return result;
    }
}
