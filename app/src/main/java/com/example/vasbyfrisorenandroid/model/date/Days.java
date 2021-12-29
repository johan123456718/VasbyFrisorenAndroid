package com.example.vasbyfrisorenandroid.model.date;

public enum Days {
    SUNDAY(0), MONDAY(1), TUESDAY(2), WEDNESDAY(3), THURSDAY(4), FRIDAY(5), SATURDAY(6);

    private int day;

    Days(int day){
        this.day = day;
    }

    public int getDay() {
        return day;
    }

    public static Days of(int day){

        switch(day){
            case 0:
                return Days.SUNDAY;
            case 1:
                return Days.MONDAY;

            case 2:
                return Days.TUESDAY;

            case 3:
                return Days.WEDNESDAY;

            case 4:
                return Days.THURSDAY;

            case 5:
                return Days.FRIDAY;

            case 6:
                return Days.SATURDAY;

            default:
                return null;
        }
    }

}
