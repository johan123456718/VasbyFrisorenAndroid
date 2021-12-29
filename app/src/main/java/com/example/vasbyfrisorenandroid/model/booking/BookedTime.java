package com.example.vasbyfrisorenandroid.model.booking;

public class BookedTime {

    private String bookedDate;
    private String bookedDay;
    private String bookCreated;
    private String timeTaken;
    private int year;
    private boolean isChecked;

    public BookedTime(){

    }

    public BookedTime(BookedTimeBuilder builder){
        this.bookedDate = builder.bookedDate;
        this.bookedDay = builder.bookedDay;
        this.bookCreated = builder.bookCreated;
        this.timeTaken = builder.timeTaken;
        this.year = builder.year;
        this.isChecked = builder.checked;
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
        return isChecked;
    }

    public static class BookedTimeBuilder{

        private final String bookCreated;
        private final String timeTaken;
        private int year;
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
