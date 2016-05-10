package com.example.sami.nextbusturku;

/**
 * Created by Sami on 23.2.2016.
 */
public class TimeFormat implements Comparable {

    private int hour;
    private int minute;
    private int second;

    public TimeFormat(){
        hour = 0;
        minute = 0;
        second = 0;
    }
    // String muotoa "hh:mm:ss"
    public TimeFormat(String time){
        String[] splitted = time.split(":");
        this.hour = Integer.parseInt(splitted[0]);
        this.minute = Integer.parseInt(splitted[1]);
        this.second = Integer.parseInt(splitted[2]);
    }

    public TimeFormat(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }
    public void update(String time){
        String[] splitted = time.split(":");
        this.hour = Integer.parseInt(splitted[0],10);
        this.minute = Integer.parseInt(splitted[1],10);
        this.second = Integer.parseInt(splitted[2],10);

    }
    // returns string in format "hh:mm:ss"
    public String toString(){
        StringBuilder time = new StringBuilder();
        if (this.hour >= 0 && this.hour < 10){
            time.append("0"+this.hour+":");
        }
        else{ time.append(this.hour+":");}
        if (this.minute >= 0 && this.minute < 10){
            time.append("0"+this.minute+":");
        }
        else{ time.append(this.minute+":");}
        if (this.second >= 0 && this.second < 10){
            time.append("0"+this.second);
        }
        else{ time.append(this.second);}

        return time.toString();
    }
    // returns string in format "hh:mm"
    public String toStringNoSeconds(){
        StringBuilder time = new StringBuilder();
        if (this.hour >= 0 && this.hour < 10){
            time.append("0"+this.hour+":");
        }
        else{ time.append(this.hour+":");}
        if (this.minute >= 0 && this.minute < 10){
            time.append("0"+this.minute);
        }
        else{ time.append(this.minute);}

        return time.toString();
    }

    @Override
    public int compareTo(Object another) {
        if (this.hour==((TimeFormat)another).getHour()
            && this.minute==((TimeFormat)another).getMinute()
            && this.second==((TimeFormat)another).getSecond()){
            return 0;
        }
        if (this.hour==((TimeFormat)another).getHour()
                && this.minute==((TimeFormat)another).getMinute()
                && this.second<((TimeFormat)another).getSecond()){
            return -1;
        }
        if (this.hour==((TimeFormat)another).getHour()
                && this.minute<((TimeFormat)another).getMinute()){
            return -1;
        }
        if (this.hour<((TimeFormat)another).getHour()){
            return -1;
        }

        if (this.hour==((TimeFormat)another).getHour()
                && this.minute==((TimeFormat)another).getMinute()
                && this.second>((TimeFormat)another).getSecond()){
            return 1;
        }
        if (this.hour==((TimeFormat)another).getHour()
                && this.minute>((TimeFormat)another).getMinute()){
            return 1;
        }
        if (this.hour>((TimeFormat)another).getHour()){
            return 1;
        }
    return 0;

    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

}
