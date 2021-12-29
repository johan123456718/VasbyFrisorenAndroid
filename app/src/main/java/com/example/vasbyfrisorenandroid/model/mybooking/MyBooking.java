package com.example.vasbyfrisorenandroid.model.mybooking;

import com.example.vasbyfrisorenandroid.model.booking.BookedTime;
import com.example.vasbyfrisorenandroid.model.service.Service;

public class MyBooking {

    private String barber;
    private Service service;
    private BookedTime bookedTime;


    public MyBooking(){

    }

    public MyBooking(String barber, Service service, BookedTime bookedTime){
        this.barber = barber;
        this.service = service;
        this.bookedTime = bookedTime;
    }

    public String getBarber() {
        return barber;
    }

    public Service getService() {
        return service;
    }

    public BookedTime getBookedTime(){return bookedTime;}
}
