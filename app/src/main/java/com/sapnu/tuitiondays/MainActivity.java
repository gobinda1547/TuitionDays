package com.sapnu.tuitiondays;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "[MainActivity]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    void showFragment(){
        CurrentShowingFragmentName currentShowingFragmentName = DatabaseManager.getInstance().getCurrentShowingFragmentName(this);
        if(currentShowingFragmentName == null){
            currentShowingFragmentName = CurrentShowingFragmentName.TUITION_LIST;
            DatabaseManager.getInstance().storeCurrentShowingFragmentName(this, currentShowingFragmentName);
        }
        Log.d(DEBUG_TAG, "Showing Fragment :"+ String.valueOf(currentShowingFragmentName));

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        MyFragment currentFragment = null;

        switch (currentShowingFragmentName){
            case TUITION_LIST:
                currentFragment = new TuitionListFragment();
                break;
            case TUITION_DAY_LIST:
                currentFragment = new TuitionDayListFragment();
                break;
            default:
                Log.d(DEBUG_TAG, "User want to view Invalid Fragment!");
                break;
        }

        currentFragment.refreshContentViewForCurrentFragment();
        currentFragment.refreshMenuOptionForCurrentFragment();
        fragmentTransaction.replace(R.id.fragment_container, currentFragment);
        fragmentTransaction.show(currentFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showFragment();
    }
}
