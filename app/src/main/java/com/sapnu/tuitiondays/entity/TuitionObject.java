package com.sapnu.tuitiondays.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class TuitionObject implements Serializable {
    private String tuitionName;
    private ArrayList<TuitionDateObject> tuitionDateObjects;

    public TuitionObject(String tuitionName, ArrayList<TuitionDateObject> tuitionDateObjects){
        this.tuitionName = tuitionName;
        this.tuitionDateObjects = tuitionDateObjects;
    }

    public void addDate(TuitionDateObject dateObject){
        tuitionDateObjects.add(0, dateObject);
    }

    public void removeDay(String day){
        for(int i=0;i<tuitionDateObjects.size();i++){
            if(tuitionDateObjects.get(i).getDate().equals(day)){
                tuitionDateObjects.remove(i);
                return;
            }
        }
    }

    public void removeAllDays(){
        tuitionDateObjects = new ArrayList<>();
    }

    public String getTuitionName(){
        return tuitionName;
    }

    public ArrayList<TuitionDateObject> getTuitionDates(){
        return tuitionDateObjects;
    }
}
