package com.example.vasbyfrisorenandroid.model.db.callbacks;

import com.example.vasbyfrisorenandroid.model.timeslot.TimeSlot;

import java.util.List;

public interface TimeslotCallback {
    void callback(List<TimeSlot> timeSlotList);
}
