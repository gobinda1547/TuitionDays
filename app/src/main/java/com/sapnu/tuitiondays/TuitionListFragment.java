package com.sapnu.tuitiondays;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class TuitionListFragment extends MyFragment {
    private static final String DEBUG_TAG = "[GPTuitionListFragment]";

    private MyFragmentListener myFragmentListener;

    private RecyclerView recyclerView;

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
    void setMyFragmentListener(MyFragmentListener myFragmentListener) {
        this.myFragmentListener = myFragmentListener;
    }

    @Override
    boolean handleBackButtonPressed() {
        //in this fragment there is no task
        return false;
    }

    @Override
    void handleMenuItemSelection(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.AddNewTuitionMenuItem:
                DatabaseManager.getInstance().addTuition(getCurrentTimeAsString());
                Toast.makeText(getActivity(), "Add New Tuition", Toast.LENGTH_SHORT).show();
                refreshRecycleView();
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
            tuitionListViewHolder.showTuitionTotalDayTextView.setText("Total day : "+String.valueOf(currentTuitionObject.getTuitionDays().size()));
        }

        class TuitionListViewHolder extends RecyclerView.ViewHolder{

            private TextView showTuitionNameTextView;
            private TextView showTuitionTotalDayTextView;
            private ImageButton deleteTuitionNameImageButton;

            private TuitionListViewHolder(View itemView) {
                super(itemView);

                showTuitionNameTextView = itemView.findViewById(R.id.ShowTuitionNameTextView);
                showTuitionTotalDayTextView = itemView.findViewById(R.id.ShowTuitionTotalDayTextView);
                deleteTuitionNameImageButton = itemView.findViewById(R.id.DeleteTuitionNameImageButton);

                showTuitionNameTextView.setOnClickListener(v -> {
                    DatabaseManager.getInstance().storeTuitionNameSelected(showTuitionNameTextView.getText().toString());
                    Log.d(DEBUG_TAG, "requesting for showing tuition day list");
                    myFragmentListener.changeFragmentTo(CurrentShowingFragmentName.TUITION_DAY_LIST);
                });

                deleteTuitionNameImageButton.setOnClickListener(v -> {
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

    private String getCurrentTimeAsString(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(new Date());
    }


}
