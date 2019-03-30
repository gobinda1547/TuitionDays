package com.sapnu.tuitiondays.tuition_day_list;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;

import com.sapnu.tuitiondays.R;
import com.sapnu.tuitiondays.entity.TuitionDateObject;

public class UpdateTuitionDayDialog extends Dialog {
    private static final String DEBUG = "[AddTuitionNameDialog]";

    private DatePicker datePicker;
    private EditText commentField;

    private UpdateTuitionDayDialogCallBacks callBacks;

    private TuitionDateObject previous;

    UpdateTuitionDayDialog(@NonNull Context context, UpdateTuitionDayDialogCallBacks callBacks) {
        super(context);
        this.callBacks = callBacks;
    }

    void updateView(TuitionDateObject previous){
        this.previous = previous;

        String dateString = previous.getDate();
        dateString = dateString.replaceAll(" ", "");
        String[] res = dateString.split("/");

        int year = Integer.parseInt(res[0]);
        int month = Integer.parseInt(res[1]);
        int date = Integer.parseInt(res[2]);

        datePicker.updateDate(year, month, date);
        commentField.setText(previous.getComment());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_add_tuition_day_dialog);

        datePicker = findViewById(R.id.UpdateTuitionDayDatePicker);
        commentField = findViewById(R.id.UpdateCommentsEditText);

        findViewById(R.id.CancelUpdateTuitionDayDialog).setOnClickListener(view -> {
            //when cancel button pressed we just simply sent one call back to parent class
            callBacks.dialogCancelPressed();
        });

        findViewById(R.id.UpdateTuitionDayDialog).setOnClickListener(view -> {
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();

            //since it's 0 index based we have to make it 1 based
            month = month + 1;

            @SuppressLint("DefaultLocale") String selection = String.format("%d / %d / %d", year, month, day);
            Log.d(DEBUG, selection);

            String comment = commentField.getText().toString();
            if(comment.length() == 0){
                comment = "No comment!";
            }

            TuitionDateObject current = new TuitionDateObject(selection, comment);
            callBacks.dialogUpdatePressed(previous,current);
        });

    }

}
