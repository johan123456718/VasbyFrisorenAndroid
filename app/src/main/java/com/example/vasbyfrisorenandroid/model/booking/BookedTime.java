package com.example.vasbyfrisorenandroid.model.booking;

import android.os.Parcel;
import android.os.Parcelable;

public class BookedTime implements Parcelable {

    private String bookedDate;
    private String bookedDay;
    private String bookCreated;
    private String timeTaken;
    private int year;
    private boolean checked;
    private int week;

    public BookedTime(){

    }

    public BookedTime(BookedTimeBuilder builder){
        this.bookedDate = builder.bookedDate;
        this.bookedDay = builder.bookedDay;
        this.bookCreated = builder.bookCreated;
        this.timeTaken = builder.timeTaken;
        this.year = builder.year;
        this.checked = builder.checked;
        this.week = builder.week;
    }

    protected BookedTime(Parcel in) {
        bookedDate = in.readString();
        bookedDay = in.readString();
        bookCreated = in.readString();
        timeTaken = in.readString();
        year = in.readInt();
        checked = in.readByte() != 0;
    }

    public int getYear() {
        return year;
    }

    public String getBookedDate() {
        return bookedDate;
    }

    public String getBookCreated() {
        return bookCreated;
    }

    public String getBookedDay() {
        return bookedDay;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public boolean isChecked() {
        return checked;
    }

    public int getWeek() {
        return week;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(bookedDate);
        parcel.writeString(bookedDay);
        parcel.writeString(bookCreated);
        parcel.writeString(timeTaken);
        parcel.writeInt(year);
    }

    public static final Creator<BookedTime> CREATOR = new Creator<BookedTime>() {
        @Override
        public BookedTime createFromParcel(Parcel in) {
            return new BookedTime(in);
        }

        @Override
        public BookedTime[] newArray(int size) {
            return new BookedTime[size];
        }
    };

    public static class BookedTimeBuilder{

        private final String bookCreated;
        private final String timeTaken;
        private int year, week;
        private String bookedDate;
        private String bookedDay;
        private boolean checked;


        public BookedTimeBuilder(String bookCreated, String timeTaken){
            this.bookCreated = bookCreated;
            this.timeTaken = timeTaken;
        }

        public BookedTimeBuilder year(int year){
            this.year = year;
            return this;
        }

        public BookedTimeBuilder bookedDate(String bookedDate){
            this.bookedDate = bookedDate;
            return this;
        }

        public BookedTimeBuilder week(int week){
            this.week = week;
            return this;
        }

        public BookedTimeBuilder bookedDay(String bookedDay){
            this.bookedDay = bookedDay;
            return this;
        }

        public BookedTimeBuilder isChecked(boolean isChecked){
            this.checked = isChecked;
            return this;
        }

        public BookedTime build(){
            BookedTime bookedTime = new BookedTime(this);
            return bookedTime;
        }
    }
}
