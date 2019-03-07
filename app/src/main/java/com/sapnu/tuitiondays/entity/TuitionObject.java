package com.sapnu.tuitiondays.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class TuitionObject implements Serializable {
    private String tuitionName;
    private ArrayList<String> tuitionDays;

    public TuitionObject(String tuitionName, ArrayList<String> tuitionDays){
        this.tuitionName = tuitionName;
        this.tuitionDays = tuitionDays;
    }

    public void addDay(String day){
        tuitionDays.add(day);
    }

    public void removeDay(String day){
        tuitionDays.remove(day);
    }

    public String getTuitionName(){
        return tuitionName;
    }

    public ArrayList<String> getTuitionDays(){
        return tuitionDays;
    }
}
