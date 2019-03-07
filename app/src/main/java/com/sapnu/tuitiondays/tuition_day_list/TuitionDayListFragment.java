package com.sapnu.tuitiondays.tuition_day_list;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

import java.util.ArrayList;

public class TuitionDayListFragment extends MyFragment implements DatePickerDialog.OnDateSetListener {
    private static final String DEBUG_TAG = "[GPTuitionDayListFrag]";


    private String selectedTuitionName = null;
    private RecyclerView recyclerView;

    private MyFragmentCallBacks myFragmentCallBacks;

    private AddTuitionDayDialog addTuitionDayDialog;

    public TuitionDayListFragment() {
        // Required empty public constructor
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
        selectedTuitionName = DatabaseManager.getInstance().getTuitionNameSelectedValue();
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
                Toast.makeText(getActivity(), "Delete all tuition days", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Log.d(DEBUG_TAG, String.format("date input = %d/%d/%d", dayOfMonth, monthOfYear, year));
    }


    public void refreshRecycleView(){
        ArrayList<String> tuitionDayList = DatabaseManager.getInstance().getTuitionDayList(selectedTuitionName);
        MyRecyclerViewAdapter recyclerViewAdapter = new MyRecyclerViewAdapter(tuitionDayList);
        recyclerView.setAdapter(recyclerViewAdapter);
    }


    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<TuitionDayListFragment.MyRecyclerViewAdapter.TuitionDayListViewHolder> {

        private ArrayList<String> tuitionDaysArrayList;

        private MyRecyclerViewAdapter(ArrayList<String> tuitionDaysArrayList) {
            this.tuitionDaysArrayList = tuitionDaysArrayList;
        }

        @Override
        public int getItemCount() {
            return tuitionDaysArrayList.size();
        }

        @NonNull
        @Override
        public TuitionDayListFragment.MyRecyclerViewAdapter.TuitionDayListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_tuition_day, viewGroup, false);
            return new TuitionDayListFragment.MyRecyclerViewAdapter.TuitionDayListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final TuitionDayListFragment.MyRecyclerViewAdapter.TuitionDayListViewHolder tuitionDayListViewHolder, @SuppressLint("RecyclerView") int i) {
            String currentTuitionDate = tuitionDaysArrayList.get(i);
            tuitionDayListViewHolder.showTuitionDateTextView.setText(currentTuitionDate);
        }

        class TuitionDayListViewHolder extends RecyclerView.ViewHolder {

            private TextView showTuitionDateTextView;

            private TuitionDayListViewHolder(View itemView) {
                super(itemView);

                showTuitionDateTextView = itemView.findViewById(R.id.ShowTuitionDateTextView);

                itemView.findViewById(R.id.DeleteTuitionDateImageButton).setOnClickListener(v -> {
                    Log.d(DEBUG_TAG, "delete button pressed");

                    //taking user permission for delete by showing dialog box
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                    builder2.setTitle("Delete " + showTuitionDateTextView.getText().toString());
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
                });
            }
        }
    }

    public class AddTuitionDayDialogCallBackHandler implements AddTuitionDayDialogCallBacks {

        @Override
        public void dialogCancelPressed() {
            Log.d(DEBUG_TAG, "cancel dialog call back received.");
            addTuitionDayDialog.dismiss();
        }

        @Override
        public void dialogSavePressed(String date) {
            Log.d(DEBUG_TAG, "selected string " + date);
            DatabaseManager.getInstance().addTuitionDay(selectedTuitionName, date);
            refreshRecycleView();
            Toast.makeText(getActivity(), "Date saved!", Toast.LENGTH_SHORT).show();
            addTuitionDayDialog.dismiss();
        }
    }

}
