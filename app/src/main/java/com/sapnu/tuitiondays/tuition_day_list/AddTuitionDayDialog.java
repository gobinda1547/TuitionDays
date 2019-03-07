package com.sapnu.tuitiondays.tuition_day_list;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Window;
import android.widget.DatePicker;

import com.sapnu.tuitiondays.R;

public class AddTuitionDayDialog extends Dialog {
    private static final String DEBUG = "[AddTuitionNameDialog]";

    private AddTuitionDayDialogCallBacks callBacks;

    AddTuitionDayDialog(@NonNull Context context, AddTuitionDayDialogCallBacks callBacks) {
        super(context);
        this.callBacks = callBacks;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_add_tuition_day_dialog);

        DatePicker datePicker = findViewById(R.id.AddTuitionDayDatePicker);

        findViewById(R.id.CancleTuitionDayDialog).setOnClickListener(view -> {
            //when cancel button pressed we just simply sent one call back to parent class
            callBacks.dialogCancelPressed();
        });

        findViewById(R.id.SaveTuitionDayDialog).setOnClickListener(view -> {
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();

            //since it's 0 index based we have to make it 1 based
            month = month + 1;

            @SuppressLint("DefaultLocale") String selection = String.format("selected = %d/%d/%d", day, month, year);
            Log.d(DEBUG, selection);
            callBacks.dialogSavePressed(selection);
        });

    }

}
