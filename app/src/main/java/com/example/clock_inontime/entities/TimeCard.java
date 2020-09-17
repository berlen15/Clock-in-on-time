package com.example.clock_inontime.entities;

public class TimeCard {
    String date;
    String entryTime, outTime;
    String userId;
    String entryLocation, outLocation;

    Double hour;

    public Double getHour() {
        return hour;
    }

    public void setHour(Double hour) {
        this.hour = hour;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    Double min;
    public String getJustify() {
        return justify;
    }

    public void setJustify(String justify) {
        this.justify = justify;
    }

    String justify;
    public String getEntryLocation() {
        return entryLocation;
    }

    public void setEntryLocation(String entryLocation) {
        this.entryLocation = entryLocation;
    }

    public String getOutLocation() {
        return outLocation;
    }

    public void setOutLocation(String outLocation) {
        this.outLocation = outLocation;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTimeCardId() {
        return timeCardId;
    }

    public void setTimeCardId(String timeCardId) {
        this.timeCardId = timeCardId;
    }

    String timeCardId;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public TimeCard() {
    }

    public TimeCard(String day, String entryTime, String outTime, String userId, String entryLocation, String outLocation, String justify,
                    Double hour, Double min) {
        this.date = day;
        this.entryLocation = entryLocation;
        this.outLocation = outLocation;
        this.entryTime = entryTime;
        this.outTime = outTime;
        this.userId = userId;
        this.justify = justify;
        this.hour=hour;
        this.min=min;
    }


}
