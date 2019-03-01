package com.sapnu.tuitiondays;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

class DatabaseManager {
    private static final String DEBUG = "[DatabaseManager]";

    private SharedPreferences sharedPreferencesForSavingFragmentName;
    private SharedPreferences sharedPreferencesForTuitionList;

    private static DatabaseManager databaseManager;
    static DatabaseManager getInstance(){
        return databaseManager;
    }
    static void initializeDatabaseManager(Context context){
        databaseManager = new DatabaseManager();
        databaseManager.sharedPreferencesForSavingFragmentName = context.getSharedPreferences(SHARED_PREF_TAG_FOR_CURRENT_FRAGMENT_NAME, Context.MODE_PRIVATE);
        databaseManager.sharedPreferencesForTuitionList = context.getSharedPreferences(SHARED_PREF_TAG_FOR_TUITION_LIST, Context.MODE_PRIVATE);
        databaseManager.initializeTuitionList();
    }

    private static final String SHARED_PREF_TAG_FOR_CURRENT_FRAGMENT_NAME = "CURRENT_FRAGMENT_NAME_TAG";
    private static final String SHARED_PREF_KEY_FOR_CURRENT_FRAGMENT_NAME = "CURRENT_FRAGMENT_NAME_KEY";

    void storeCurrentShowingFragmentName(CurrentShowingFragmentName currentShowingFragmentName) {
        try {
            //Initialize serialized data
            ByteArrayOutputStream serializedData = new ByteArrayOutputStream();
            ObjectOutputStream serializer = new ObjectOutputStream(serializedData);
            serializer.writeObject(currentShowingFragmentName);

            //Insert serialized object into shared preferences
            SharedPreferences.Editor edit = sharedPreferencesForSavingFragmentName.edit();

            edit.putString(SHARED_PREF_KEY_FOR_CURRENT_FRAGMENT_NAME, Base64.encodeToString(serializedData.toByteArray(), Base64.DEFAULT));
            edit.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    CurrentShowingFragmentName getCurrentShowingFragmentName() {
        CurrentShowingFragmentName currentShowingFragmentName = null;

        String serializedData = sharedPreferencesForSavingFragmentName.getString(SHARED_PREF_KEY_FOR_CURRENT_FRAGMENT_NAME, null);
        if(serializedData == null){
            Log.d(DEBUG, "serialize data is nil.");
            return null;
        }

        try {
            ByteArrayInputStream input = new ByteArrayInputStream(Base64.decode(serializedData, Base64.DEFAULT));
            ObjectInputStream inputStream = new ObjectInputStream(input);
            currentShowingFragmentName = (CurrentShowingFragmentName) inputStream.readObject();
        } catch (IOException|ClassNotFoundException|java.lang.IllegalArgumentException e) {
            e.printStackTrace();
        }
        return currentShowingFragmentName;
    }


    private ArrayList<TuitionObject> tuitionList = new ArrayList<>();
    private static final String SHARED_PREF_TAG_FOR_TUITION_LIST = "TUITION_LIST_TAG";
    private static final String SHARED_PREF_KEY_FOR_TUITION_LIST = "TUITION_LIST_KEY";

    private void storeTuitionList() {
        try {
            //Initialize serialized data
            ByteArrayOutputStream serializedData = new ByteArrayOutputStream();
            ObjectOutputStream serializer = new ObjectOutputStream(serializedData);
            serializer.writeObject(tuitionList);

            //Insert serialized object into shared preferences
            SharedPreferences.Editor edit = sharedPreferencesForTuitionList.edit();

            edit.putString(SHARED_PREF_KEY_FOR_TUITION_LIST, Base64.encodeToString(serializedData.toByteArray(), Base64.DEFAULT));
            edit.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeTuitionList() {
        //default initialization
        tuitionList = new ArrayList<>();

        String serializedData = sharedPreferencesForTuitionList.getString(SHARED_PREF_KEY_FOR_TUITION_LIST, null);
        if(serializedData == null){
            Log.d(DEBUG, "serialize data for tuition list is nil.");
            return;
        }

        try {
            ByteArrayInputStream input = new ByteArrayInputStream(Base64.decode(serializedData, Base64.DEFAULT));
            ObjectInputStream inputStream = new ObjectInputStream(input);
            tuitionList = (ArrayList<TuitionObject>) inputStream.readObject();
        } catch (IOException|ClassNotFoundException|java.lang.IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    void addTuition(String tuitionName){
        TuitionObject currentTuitionObject = new TuitionObject(tuitionName, new ArrayList<String>());
        this.tuitionList.add(currentTuitionObject);

        //If tuitionList changed then update it
        this.storeTuitionList();
    }

    void addTuitionDay(String tuitionName, String day){
        int totalTuition = tuitionList.size();
        for(int i=0;i<totalTuition;i++){
            if(tuitionList.get(i).getTuitionName().equals(tuitionName)){
                tuitionList.get(i).addDay(day);
                break;
            }
        }

        //If tuitionList changed then update it
        this.storeTuitionList();
    }

    void deleteTuition(String tuitionName){
        int totalTuition = tuitionList.size();
        for(int i=0;i<totalTuition;i++){
            if(tuitionList.get(i).getTuitionName().equals(tuitionName)){
                tuitionList.remove(i);
                break;
            }
        }

        //If tuitionList changed then update it
        this.storeTuitionList();
    }

    void deleteTuitionDay(String tuitionName, String tuitionDay){
        int totalTuition = tuitionList.size();
        for(int i=0;i<totalTuition;i++){
            if(tuitionList.get(i).getTuitionName().equals(tuitionName)){
                tuitionList.get(i).removeDay(tuitionDay);
                break;
            }
        }

        //If tuitionList changed then update it
        this.storeTuitionList();
    }

    ArrayList<TuitionObject> getTuitionList(){
        return tuitionList;
    }

    ArrayList<String> getTuitionNameList(){
        ArrayList<String> tuitionNames = new ArrayList<>();
        int totalTuition = tuitionList.size();
        for(int i=0;i<totalTuition;i++){
            tuitionNames.add(tuitionList.get(i).getTuitionName());
        }
        return tuitionNames;
    }

    ArrayList<String> getTuitionDayList(String tuitionName){
        int totalTuition = tuitionList.size();
        for(int i=0;i<totalTuition;i++){
            if(tuitionList.get(i).getTuitionName().equals(tuitionName)){
                return tuitionList.get(i).getTuitionDays();
            }
        }
        return new ArrayList<String>();
    }
}