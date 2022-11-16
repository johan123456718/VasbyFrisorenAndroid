package com.example.vasbyfrisorenandroid.model.db;

public interface DatabaseInterface {
    void addBarbersTimeslot(String[] barbers);
    void getCurrentTimeslotsForSelectedBarber(String barberName, String weekOfYear, String day);
}
