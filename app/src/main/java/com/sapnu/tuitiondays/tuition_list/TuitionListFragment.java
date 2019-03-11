package com.sapnu.tuitiondays.tuition_list;

import android.annotation.SuppressLint;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sapnu.tuitiondays.entity.MyFragmentNames;
import com.sapnu.tuitiondays.entity.MyFragment;
import com.sapnu.tuitiondays.entity.MyFragmentCallBacks;
import com.sapnu.tuitiondays.R;
import com.sapnu.tuitiondays.database.DatabaseManager;
import com.sapnu.tuitiondays.entity.TuitionObject;

import java.util.ArrayList;

public class TuitionListFragment extends MyFragment {
    private static final String DEBUG_TAG = "[GPTuitionListFragment]";

    private MyFragmentCallBacks myFragmentCallBacks;

    private RecyclerView recyclerView;

    private AddTuitionNameDialog addTuitionNameDialog;

    public TuitionListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tuition_list, container, false);
        recyclerView = view.findViewById(R.id.TuitionListRecyclerView);
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

    public void refreshRecycleView(){
        ArrayList<TuitionObject> tuitionList = DatabaseManager.getInstance().getTuitionList();
        MyRecyclerViewAdapter recyclerViewAdapter = new MyRecyclerViewAdapter(tuitionList);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void setMyFragmentCallBacks(MyFragmentCallBacks myFragmentCallBacks) {
        this.myFragmentCallBacks = myFragmentCallBacks;
    }

    @Override
    public boolean handleBackButtonPressed() {
        //in this fragment there is no task
        return false;
    }

    @Override
    public void handleMenuItemSelection(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.AddNewTuitionMenuItem:
                addTuitionNameDialog = new AddTuitionNameDialog(getActivity(), new AddNewTuitionDialogCallBackHandler());
                addTuitionNameDialog.setCancelable(false);
                addTuitionNameDialog.show();
                break;
        }
    }

    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.TuitionListViewHolder> {

        private ArrayList<TuitionObject> tuitionObjectArrayList;

        private MyRecyclerViewAdapter(ArrayList<TuitionObject> tuitionObjectArrayList) {
            this.tuitionObjectArrayList = tuitionObjectArrayList;
        }

        @Override
        public int getItemCount() {
            return tuitionObjectArrayList.size();
        }

        @NonNull
        @Override
        public TuitionListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_tuition, viewGroup, false);
            return new TuitionListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final TuitionListViewHolder tuitionListViewHolder, @SuppressLint("RecyclerView") int i) {
            TuitionObject currentTuitionObject = tuitionObjectArrayList.get(i);
            tuitionListViewHolder.showTuitionNameTextView.setText(currentTuitionObject.getTuitionName());
            @SuppressLint("DefaultLocale") String totalDaysString = String.format("Total days : %d", currentTuitionObject.getTuitionDates().size());
            tuitionListViewHolder.showTuitionTotalDayTextView.setText(totalDaysString);
        }

        class TuitionListViewHolder extends RecyclerView.ViewHolder{

            private TextView showTuitionNameTextView;
            private TextView showTuitionTotalDayTextView;
            private ImageButton deleteTuitionNameImageButton;

            private TuitionListViewHolder(View itemView) {
                super(itemView);

                //setting click listener above overall cardview
                itemView.findViewById(R.id.EachTuitionDetailsCardView).setOnClickListener(view ->  {
                    Log.d(DEBUG_TAG, "card view pressed");
                    DatabaseManager.getInstance().storeTuitionNameSelected(showTuitionNameTextView.getText().toString());
                    Log.d(DEBUG_TAG, "requesting for showing tuition day list");
                    myFragmentCallBacks.showFragment(MyFragmentNames.TUITION_DAY_LIST);
                });

                showTuitionNameTextView = itemView.findViewById(R.id.ShowTuitionNameTextView);
                showTuitionTotalDayTextView = itemView.findViewById(R.id.ShowTuitionTotalDayTextView);
                deleteTuitionNameImageButton = itemView.findViewById(R.id.DeleteTuitionNameImageButton);

                deleteTuitionNameImageButton.setOnClickListener(v -> {
                    Log.d(DEBUG_TAG, "delete button pressed");
                    //taking user permission for delete by showing dialog box
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                    builder2.setTitle("Delete this tuition info.");
                    builder2.setMessage("Are you sure to delete this tuition info forever?");
                    builder2.setPositiveButton("YES", (dialog, which) -> {
                        DatabaseManager.getInstance().deleteTuition(showTuitionNameTextView.getText().toString());
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

    public class AddNewTuitionDialogCallBackHandler implements AddNewTuitionDialogCallBacks {

        @Override
        public void dialogCancelPressed() {
            addTuitionNameDialog.dismiss();
        }

        @Override
        public void dialogSavePressed(String tuitionName) {
            if(tuitionName.length() == 0){
                Toast.makeText(getActivity(), "Name is Empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if(DatabaseManager.getInstance().checkIfTuitionNameAlreadyExist(tuitionName)){
                Toast.makeText(getActivity(), "Name can't be duplicate", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(DEBUG_TAG, "storing tuition name  = [" + tuitionName +"]");
            Toast.makeText(getActivity(), "Tuition name added successfully", Toast.LENGTH_SHORT).show();
            DatabaseManager.getInstance().addTuition(tuitionName);
            addTuitionNameDialog.dismiss();

            refreshRecycleView();
        }
    }


}
