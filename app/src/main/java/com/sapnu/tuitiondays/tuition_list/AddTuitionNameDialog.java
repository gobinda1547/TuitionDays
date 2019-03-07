package com.sapnu.tuitiondays.tuition_list;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;

import com.sapnu.tuitiondays.R;

public class AddTuitionNameDialog extends Dialog{
    private static final String DEBUG = "[AddTuitionNameDialog]";

    private AddNewTuitionDialogCallBacks callBacks;
    private EditText editText;

    AddTuitionNameDialog(@NonNull Activity activity, AddNewTuitionDialogCallBacks callBacks) {
        super(activity);
        this.callBacks = callBacks;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_add_tuition_name_dialog);

        editText = findViewById(R.id.EnterTuitionNameEditText);
        //editText.requestFocus();

        findViewById(R.id.CancleTuitionNameDialog).setOnClickListener(view -> {
            //since user pressed cancel so we just dismiss the dialog
            callBacks.dialogCancelPressed();
        });

        findViewById(R.id.SaveTuitionNameDialog).setOnClickListener(view -> {
            String tuitionName = editText.getText().toString().trim();
            Log.d(DEBUG, "name is = [" + tuitionName +"]");
            callBacks.dialogSavePressed(tuitionName);
        });
    }
}
