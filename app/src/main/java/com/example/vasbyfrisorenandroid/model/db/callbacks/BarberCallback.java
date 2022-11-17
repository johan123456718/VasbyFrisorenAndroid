package com.example.vasbyfrisorenandroid.model.db.callbacks;

import com.example.vasbyfrisorenandroid.model.barber.Barber;

import java.util.Map;

public interface BarberCallback {
    void callback(Map<String, Boolean> barberStatus);
    void callback(Barber barbers);
}
