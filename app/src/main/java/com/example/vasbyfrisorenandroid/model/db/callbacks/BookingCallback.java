package com.example.vasbyfrisorenandroid.model.db.callbacks;

import com.example.vasbyfrisorenandroid.model.mybooking.MyBooking;

import java.util.List;

public interface BookingCallback {
    void callback(int id);
    void callback(List<MyBooking> myBookings);
}
