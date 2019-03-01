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

class DatabaseManager {
    private static final String DEBUG = "[DatabaseManager]";

    private static DatabaseManager databaseManager;
    static DatabaseManager getInstance(){
        if(databaseManager == null){
            databaseManager = new DatabaseManager();
        }
        return databaseManager;
    }

    private static final String SHARED_PREF_TAG_FOR_CURRENT_FRAGMENT_NAME = "CURRENT_FRAGMENT_NAME_TAG";
    private static final String SHARED_PREF_KEY_FOR_CURRENT_FRAGMENT_NAME = "CURRENT_FRAGMENT_NAME_KEY";

    void storeCurrentShowingFragmentName(Context context, CurrentShowingFragmentName currentShowingFragmentName) {
        try {
            //Initialize serialized data
            ByteArrayOutputStream serializedData = new ByteArrayOutputStream();
            ObjectOutputStream serializer = new ObjectOutputStream(serializedData);
            serializer.writeObject(currentShowingFragmentName);

            //Insert serialized object into shared preferences
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_TAG_FOR_CURRENT_FRAGMENT_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();

            edit.putString(SHARED_PREF_KEY_FOR_CURRENT_FRAGMENT_NAME, Base64.encodeToString(serializedData.toByteArray(), Base64.DEFAULT));
            edit.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    CurrentShowingFragmentName getCurrentShowingFragmentName(Context context) {
        CurrentShowingFragmentName currentShowingFragmentName = null;

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_TAG_FOR_CURRENT_FRAGMENT_NAME, Context.MODE_PRIVATE);
        String serializedData = sharedPreferences.getString(SHARED_PREF_KEY_FOR_CURRENT_FRAGMENT_NAME, null);
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
}