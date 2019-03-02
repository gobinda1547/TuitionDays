package com.sapnu.tuitiondays;

import java.io.Serializable;
import java.util.ArrayList;

class TuitionObject implements Serializable {
    private String tuitionName;
    private ArrayList<String> tuitionDays;

    TuitionObject(String tuitionName, ArrayList<String> tuitionDays){
        this.tuitionName = tuitionName;
        this.tuitionDays = tuitionDays;
    }

    void addDay(String day){
        tuitionDays.add(day);
    }

    void removeDay(String day){
        tuitionDays.remove(day);
    }

    String getTuitionName(){
        return tuitionName;
    }

    ArrayList<String> getTuitionDays(){
        return tuitionDays;
    }
}
