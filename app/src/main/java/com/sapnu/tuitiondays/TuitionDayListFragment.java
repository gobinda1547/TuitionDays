package com.sapnu.tuitiondays;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class TuitionDayListFragment extends MyFragment {
    private static final String DEBUG_TAG = "[GPTuitionDayListFrag]";

    private MyFragmentListener myFragmentListener;

    public TuitionDayListFragment() {
        // Required empty public constructor
    }

    void setMyFragmentListener(MyFragmentListener myFragmentListener){
        this.myFragmentListener = myFragmentListener;
    }

    @Override
    boolean handleBackButtonPressed() {
        myFragmentListener.changeFragmentTo(CurrentShowingFragmentName.TUITION_LIST);
        return true;
    }

    @Override
    void handleMenuItemSelection(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.AddTuitionDayMenuItem:
                Toast.makeText(getActivity(), "Add Tuition Day", Toast.LENGTH_SHORT).show();
                myFragmentListener.changeFragmentTo(CurrentShowingFragmentName.TUITION_LIST);
                break;
            case R.id.DeleteAllTuitionDaysMenuItem:
                Toast.makeText(getActivity(), "Delete all tuition days", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tuition_day_list, container, false);
        textView = view.findViewById(R.id.example);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(DEBUG_TAG, "onPause called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(DEBUG_TAG, "onResume called");
        String name = DatabaseManager.getInstance().getTuitionNameSelectedValue();
        if( name == null){
            name = "null";
        }
        if(textView == null){
            Log.d(DEBUG_TAG, "text view is null");
        }
        textView.setText(name);
    }


}
