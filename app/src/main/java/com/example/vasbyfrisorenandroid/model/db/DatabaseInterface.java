package com.example.vasbyfrisorenandroid.model.db;

import com.example.vasbyfrisorenandroid.model.db.callbacks.BarberCallback;
import com.example.vasbyfrisorenandroid.model.db.callbacks.BookingCallback;
import com.example.vasbyfrisorenandroid.model.db.callbacks.TimeslotCallback;
import com.google.android.gms.tasks.Task;

import java.util.Map;

public interface DatabaseInterface {
    void addBarbersTimeslot(String[] barbers);
    void getCurrentTimeslotsForSelectedBarber(String barberName, String weekOfYear, String day, TimeslotCallback firebaseCallback);
    void resetTimeslot();
    void updateTimeslotStatus(String barberName, String weekOfYear, String day, int selectedItem);
    void getBookingId(BookingCallback firebaseCallback);
    void getBarberStatus(BarberCallback firebaseCallback);
    void addBooking(int id, Map<String, Object> serviceInfo);
    void getFirstAvailableBarber(BarberCallback firebaseCallback);
    void getMyBookings(BookingCallback firebaseCallback);
    Task<Void> updateBookingTimeStatus(int position, BookingCallback firebaseCallback);
}
