package com.sapnu.tuitiondays.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.sapnu.tuitiondays.entity.MyFragmentNames;
import com.sapnu.tuitiondays.entity.TuitionObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class DatabaseManager {
    private static final String DEBUG_TAG = "[GPDatabaseManager]";

    private static final String SHARED_PREF_TAG_FOR_CURRENT_FRAGMENT_NAME = "CURRENT_FRAGMENT_NAME_TAG";
    private static final String SHARED_PREF_TAG_FOR_TUITION_LIST = "TUITION_LIST_TAG";
    private static final String SHARED_PREF_TAG_FOR_TUITION_NAME_SELECTED = "TUITION_NAME_SELECTED_TAG";

    private static final String SHARED_PREF_KEY_FOR_CURRENT_FRAGMENT_NAME = "CURRENT_FRAGMENT_NAME_KEY";
    private static final String SHARED_PREF_KEY_FOR_TUITION_LIST = "TUITION_LIST_KEY";
    private static final String SHARED_PREF_KEY_FOR_TUITION_NAME_SELECTED = "TUITION_NAME_SELECTED_KEY";

    private static DatabaseManager databaseManager;

    private SharedPreferences sharedPreferencesForSavingFragmentName;
    private SharedPreferences sharedPreferencesForTuitionList;
    private SharedPreferences sharedPreferencesForTuitionNameSelected;

    public static DatabaseManager getInstance(){
        return databaseManager;
    }

    public static void initializeDatabaseManager(Context context){
        databaseManager = new DatabaseManager();
        databaseManager.setSharedPreferencesForSavingFragmentName(context.getSharedPreferences(SHARED_PREF_TAG_FOR_CURRENT_FRAGMENT_NAME, Context.MODE_PRIVATE));
        databaseManager.setSharedPreferencesForTuitionList(context.getSharedPreferences(SHARED_PREF_TAG_FOR_TUITION_LIST, Context.MODE_PRIVATE));
        databaseManager.setSharedPreferencesForTuitionNameSelected(context.getSharedPreferences(SHARED_PREF_TAG_FOR_TUITION_NAME_SELECTED, Context.MODE_PRIVATE));
        databaseManager.initializeTuitionList();
    }

    private void setSharedPreferencesForSavingFragmentName(SharedPreferences sharedPreferencesForSavingFragmentName) {
        this.sharedPreferencesForSavingFragmentName = sharedPreferencesForSavingFragmentName;
    }
    private void setSharedPreferencesForTuitionList(SharedPreferences sharedPreferencesForTuitionList) {
        this.sharedPreferencesForTuitionList = sharedPreferencesForTuitionList;
    }
    private void setSharedPreferencesForTuitionNameSelected(SharedPreferences sharedPreferencesForTuitionNameSelected) {
        this.sharedPreferencesForTuitionNameSelected = sharedPreferencesForTuitionNameSelected;
    }

    public void storeCurrentFragmentNameAsLastlyShowed(MyFragmentNames lastFragment) {
        try {
            //Initialize serialized data
            ByteArrayOutputStream serializedData = new ByteArrayOutputStream();
            ObjectOutputStream serializer = new ObjectOutputStream(serializedData);
            serializer.writeObject(lastFragment);

            //Insert serialized object into shared preferences
            SharedPreferences.Editor edit = sharedPreferencesForSavingFragmentName.edit();

            edit.putString(SHARED_PREF_KEY_FOR_CURRENT_FRAGMENT_NAME, Base64.encodeToString(serializedData.toByteArray(), Base64.DEFAULT));
            boolean result = edit.commit();
            Log.d(DEBUG_TAG, "storing current showing fragment name value. result is " + String.valueOf(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public MyFragmentNames getLastSavedFragmentName() {
        MyFragmentNames lastSavedFragmentName = null;

        String serializedData = sharedPreferencesForSavingFragmentName.getString(SHARED_PREF_KEY_FOR_CURRENT_FRAGMENT_NAME, null);
        if(serializedData == null){
            Log.d(DEBUG_TAG, "serialize data is nil.");
            return null;
        }

        try {
            ByteArrayInputStream input = new ByteArrayInputStream(Base64.decode(serializedData, Base64.DEFAULT));
            ObjectInputStream inputStream = new ObjectInputStream(input);
            lastSavedFragmentName = (MyFragmentNames) inputStream.readObject();
        } catch (IOException|ClassNotFoundException|java.lang.IllegalArgumentException e) {
            e.printStackTrace();
        }
        return lastSavedFragmentName;
    }


    private ArrayList<TuitionObject> tuitionList = new ArrayList<>();

    private void storeTuitionList() {
        try {
            //Initialize serialized data
            ByteArrayOutputStream serializedData = new ByteArrayOutputStream();
            ObjectOutputStream serializer = new ObjectOutputStream(serializedData);
            serializer.writeObject(tuitionList);

            //Insert serialized object into shared preferences
            SharedPreferences.Editor edit = sharedPreferencesForTuitionList.edit();

            edit.putString(SHARED_PREF_KEY_FOR_TUITION_LIST, Base64.encodeToString(serializedData.toByteArray(), Base64.DEFAULT));
            boolean result = edit.commit();
            Log.d(DEBUG_TAG, "successfully stored tution list result = " + String.valueOf(result));
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(DEBUG_TAG, "problem occurs while saving tution list");
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeTuitionList() {
        //default initialization
        tuitionList = new ArrayList<>();

        String serializedData = sharedPreferencesForTuitionList.getString(SHARED_PREF_KEY_FOR_TUITION_LIST, null);
        if(serializedData == null){
            Log.d(DEBUG_TAG, "serialize data for tuition list is nil.");
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

    public void addTuition(String tuitionName){
        TuitionObject currentTuitionObject = new TuitionObject(tuitionName, new ArrayList<>());
        this.tuitionList.add(0, currentTuitionObject); //adding in the first position

        Log.d(DEBUG_TAG, "before storing size = "+ String.valueOf(tuitionList.size()));

        //If tuitionList changed then update it
        this.storeTuitionList();
    }

    public void addTuitionDay(String tuitionName, String day){
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

    public void deleteTuition(String tuitionName){
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

    public void deleteTuitionDay(String tuitionName, String tuitionDay){
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

    public ArrayList<TuitionObject> getTuitionList(){
        return tuitionList;
    }

    public boolean checkIfTuitionNameAlreadyExist(String tuitionName){
        int totalTuition = tuitionList.size();
        for(int i=0;i<totalTuition;i++){
            if(tuitionName.equals(tuitionList.get(i).getTuitionName())){
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> getTuitionDayList(String tuitionName){
        int totalTuition = tuitionList.size();
        for(int i=0;i<totalTuition;i++){
            if(tuitionList.get(i).getTuitionName().equals(tuitionName)){
                return tuitionList.get(i).getTuitionDays();
            }
        }
        return new ArrayList<>();
    }

    public void storeTuitionNameSelected(String tuitionName) {
        try {
            ByteArrayOutputStream serializedData = new ByteArrayOutputStream();
            ObjectOutputStream serializer = new ObjectOutputStream(serializedData);
            serializer.writeObject(tuitionName);

            //Insert serialized object into shared preferences
            SharedPreferences.Editor edit = sharedPreferencesForTuitionNameSelected.edit();

            edit.putString(SHARED_PREF_KEY_FOR_TUITION_NAME_SELECTED, Base64.encodeToString(serializedData.toByteArray(), Base64.DEFAULT));
            boolean result = edit.commit();
            Log.d(DEBUG_TAG, "storing tuition name selected value. result is " + String.valueOf(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public String getTuitionNameSelectedValue() {
        String serializedData = sharedPreferencesForTuitionNameSelected.getString(SHARED_PREF_KEY_FOR_TUITION_NAME_SELECTED, null);
        if(serializedData == null){
            Log.d(DEBUG_TAG, "serialize data is nil.");
            return null;
        }

        try {
            ByteArrayInputStream input = new ByteArrayInputStream(Base64.decode(serializedData, Base64.DEFAULT));
            ObjectInputStream inputStream = new ObjectInputStream(input);
            return (String) inputStream.readObject();
        } catch (IOException|ClassNotFoundException|java.lang.IllegalArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }


}