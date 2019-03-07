package com.sapnu.tuitiondays.tuition_day_list;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.sapnu.tuitiondays.entity.MyFragmentNames;
import com.sapnu.tuitiondays.entity.MyFragment;
import com.sapnu.tuitiondays.entity.MyFragmentCallBacks;
import com.sapnu.tuitiondays.R;
import com.sapnu.tuitiondays.database.DatabaseManager;

public class TuitionDayListFragment extends MyFragment implements DatePickerDialog.OnDateSetListener {
    private static final String DEBUG_TAG = "[GPTuitionDayListFrag]";

    private MyFragmentCallBacks myFragmentCallBacks;

    private AddTuitionDayDialog addTuitionDayDialog;

    public TuitionDayListFragment() {
        // Required empty public constructor
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
        if (name == null) {
            name = "null";
        }
        if (textView == null) {
            Log.d(DEBUG_TAG, "text view is null");
        }
        textView.setText(name);
    }

    @Override
    public void setMyFragmentCallBacks(MyFragmentCallBacks myFragmentCallBacks) {
        this.myFragmentCallBacks = myFragmentCallBacks;
    }

    @Override
    public boolean handleBackButtonPressed() {
        myFragmentCallBacks.showFragment(MyFragmentNames.TUITION_LIST);
        return true;
    }

    @Override
    public void handleMenuItemSelection(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.AddTuitionDayMenuItem:
                addTuitionDayDialog = new AddTuitionDayDialog(getActivity(), new AddTuitionDayDialogCallBackHandler());
                addTuitionDayDialog.setCancelable(false);
                addTuitionDayDialog.show();
                break;
            case R.id.DeleteAllTuitionDaysMenuItem:
                Toast.makeText(getActivity(), "Delete all tuition days", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Log.d(DEBUG_TAG, String.format("date input = %d/%d/%d", dayOfMonth, monthOfYear, year));
    }

    public class AddTuitionDayDialogCallBackHandler implements AddTuitionDayDialogCallBacks{

        @Override
        public void dialogCancelPressed() {
            Log.d(DEBUG_TAG, "cancel dialog call back received.");
            addTuitionDayDialog.dismiss();
        }

        @Override
        public void dialogSavePressed(String date) {
            Log.d(DEBUG_TAG, "selected string " + date);
            addTuitionDayDialog.dismiss();
        }
    }

}
