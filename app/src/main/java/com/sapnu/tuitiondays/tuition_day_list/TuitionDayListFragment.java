package com.sapnu.tuitiondays.tuition_day_list;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.sapnu.tuitiondays.entity.TuitionDateObject;

import java.util.ArrayList;
import java.util.Objects;

public class TuitionDayListFragment extends MyFragment implements DatePickerDialog.OnDateSetListener {
    private static final String DEBUG_TAG = "[GPTuitionDayListFrag]";

    private String selectedTuitionName = null;
    private RecyclerView recyclerView;

    private MyFragmentCallBacks myFragmentCallBacks;

    private AddTuitionDayDialog addTuitionDayDialog;
    private UpdateTuitionDayDialog updateTuitionDayDialog;

    public TuitionDayListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setting title for the activity
        selectedTuitionName = DatabaseManager.getInstance().getTuitionNameSelectedValue();
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle(selectedTuitionName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tuition_day_list, container, false);
        recyclerView = view.findViewById(R.id.TuitionDayListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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

        refreshRecycleView();
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
                deleteAllTuitionDaysForCurrentlySelectedTuitionName();
                break;
        }
    }

    public void deleteAllTuitionDaysForCurrentlySelectedTuitionName(){

        //if there is no days then return
        if(DatabaseManager.getInstance().getTuitionDateList(selectedTuitionName).size() <= 0){
            Toast.makeText(getActivity(), "No records to delete.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        //taking user permission to delete all the tuition dates
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Warning...");
        builder.setMessage("Are you sure to delete all the tuition dates for "+selectedTuitionName+"?");
        builder.setPositiveButton("YES", (dialog, which) -> {
            DatabaseManager.getInstance().deleteAllTuitionDay(selectedTuitionName);
            Toast.makeText(getActivity(), "Deleted all tuition days", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            refreshRecycleView();
        });
        builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Log.d(DEBUG_TAG, String.format("date input = %d/%d/%d", dayOfMonth, monthOfYear, year));
    }

    public void refreshRecycleView(){
        ArrayList<TuitionDateObject> tuitionDateList = DatabaseManager.getInstance().getTuitionDateList(selectedTuitionName);
        MyRecyclerViewAdapter recyclerViewAdapter = new MyRecyclerViewAdapter(tuitionDateList);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<TuitionDayListFragment.MyRecyclerViewAdapter.TuitionDayListViewHolder> {

        private ArrayList<TuitionDateObject> tuitionDatesArrayList;
        private int totalTuitionDays;

        private MyRecyclerViewAdapter(ArrayList<TuitionDateObject> tuitionDatesArrayList) {
            this.tuitionDatesArrayList = tuitionDatesArrayList;
            this.totalTuitionDays = tuitionDatesArrayList.size();
        }

        @Override
        public int getItemCount() {
            return tuitionDatesArrayList.size();
        }

        @NonNull
        @Override
        public TuitionDayListFragment.MyRecyclerViewAdapter.TuitionDayListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_tuition_day, viewGroup, false);
            return new TuitionDayListFragment.MyRecyclerViewAdapter.TuitionDayListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final TuitionDayListFragment.MyRecyclerViewAdapter.TuitionDayListViewHolder tuitionDayListViewHolder, @SuppressLint("RecyclerView") int i) {
            tuitionDayListViewHolder.display(tuitionDatesArrayList.get(i) , i);
        }

        class TuitionDayListViewHolder extends RecyclerView.ViewHolder {

            private int position;

            private TextView showTuitionDateTextView;
            private TextView showTuitionDateCommentTextView;
            private TextView showSerialNumberTextView;

            private TuitionDayListViewHolder(View itemView) {
                super(itemView);

                showTuitionDateTextView = itemView.findViewById(R.id.ShowTuitionDateTextView);
                showTuitionDateCommentTextView = itemView.findViewById(R.id.ShowParticularTuitionDateComment);
                showSerialNumberTextView = itemView.findViewById(R.id.SerialNumberTextView);

                itemView.setOnClickListener(view -> {
                    Log.d(DEBUG_TAG, "inside Normal Click Listener");

                    updateTuitionDayDialog = new UpdateTuitionDayDialog(getActivity(), new UpdateTuitionDayDialogCallBackHandler(), tuitionDatesArrayList.get(position));
                    updateTuitionDayDialog.setCancelable(false);
                    updateTuitionDayDialog.show();
                });

                itemView.setOnLongClickListener(view -> {
                    Log.d(DEBUG_TAG, "inside Long Click Listener");

                    //taking user permission for delete by showing dialog box
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                    builder2.setTitle(showTuitionDateTextView.getText().toString());
                    builder2.setMessage("Are you sure to delete this tuition date forever?");
                    builder2.setPositiveButton("YES", (dialog, which) -> {
                        String tuitionName = DatabaseManager.getInstance().getTuitionNameSelectedValue();
                        String deleteDate = showTuitionDateTextView.getText().toString();
                        DatabaseManager.getInstance().deleteTuitionDay(tuitionName, deleteDate);
                        Toast.makeText(getActivity(), "Deleted successfully!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        refreshRecycleView();
                    });
                    builder2.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());

                    AlertDialog alert2 = builder2.create();
                    alert2.show();
                    return true;
                });

            }

            private void display(TuitionDateObject currentTuitionDate, int position){
                this.position = position;

                showTuitionDateTextView.setText(currentTuitionDate.getDate());
                showTuitionDateCommentTextView.setText(currentTuitionDate.getComment());
                showSerialNumberTextView.setText(String.valueOf(totalTuitionDays - position));
            }
        }
    }


    public class UpdateTuitionDayDialogCallBackHandler implements UpdateTuitionDayDialogCallBacks {

        @Override
        public void backButtonPressed() {
            Log.d(DEBUG_TAG, "back button pressed, cancel update dialog call back received.");
            updateTuitionDayDialog.dismiss();
        }

        @Override
        public void dialogCancelPressed() {
            Log.d(DEBUG_TAG, "cancel update dialog call back received.");
            updateTuitionDayDialog.dismiss();
        }

        @Override
        public void dialogUpdatePressed(TuitionDateObject previous, TuitionDateObject current) {
            if(!previous.getDate().equals(current.getDate())){
                ArrayList<TuitionDateObject> dateObjects = DatabaseManager.getInstance().getTuitionDateList(selectedTuitionName);
                for(int i= 0;i<dateObjects.size();i++){
                    if(dateObjects.get(i).getDate().equals(current.getDate())){
                        Toast.makeText(getActivity(), "Date can't be Duplicate", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            DatabaseManager.getInstance().updateTuitionDay(selectedTuitionName, previous, current);
            Toast.makeText(getActivity(), "Successfully updated", Toast.LENGTH_SHORT).show();
            refreshRecycleView();
            updateTuitionDayDialog.dismiss();
        }
    }

    public class AddTuitionDayDialogCallBackHandler implements AddTuitionDayDialogCallBacks {

        @Override
        public void backButtonPressed() {
            Log.d(DEBUG_TAG, "back button pressed, dialog dismiss");
            addTuitionDayDialog.dismiss();
        }

        @Override
        public void dialogCancelPressed() {
            Log.d(DEBUG_TAG, "cancel dialog call back received.");
            addTuitionDayDialog.dismiss();
        }

        @Override
        public void dialogSavePressed(TuitionDateObject date) {
            ArrayList<TuitionDateObject> dateObjects = DatabaseManager.getInstance().getTuitionDateList(selectedTuitionName);
            for(int i= 0;i<dateObjects.size();i++){
                if(dateObjects.get(i).getDate().equals(date.getDate())){
                    Toast.makeText(getActivity(), "Date can't be Duplicate", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Log.d(DEBUG_TAG, "selected string " + date);
            DatabaseManager.getInstance().addTuitionDay(selectedTuitionName, date);
            refreshRecycleView();
            Toast.makeText(getActivity(), "Date saved!", Toast.LENGTH_SHORT).show();
            addTuitionDayDialog.dismiss();
        }
    }

}
