package com.sapnu.tuitiondays;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.sapnu.tuitiondays.database.DatabaseManager;
import com.sapnu.tuitiondays.entity.CurrentShowingFragmentName;
import com.sapnu.tuitiondays.entity.MyFragment;
import com.sapnu.tuitiondays.tuition_day_list.TuitionDayListFragment;
import com.sapnu.tuitiondays.tuition_list.TuitionListFragment;
import com.sapnu.tuitiondays.entity.MyFragmentListener;

public class MainActivity extends AppCompatActivity implements MyFragmentListener {
    private static final String DEBUG_TAG = "[GPMainActivity]";

    private static boolean MOBILE_ADS_INITIALIZED = false;
    private static boolean SHOWING_MOBILE_ADS = false;

    private AdView mAdView;

    private MyFragment currentShowingFragment;
    private CurrentShowingFragmentName currentShowingFragmentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "onCreate called");
        setContentView(R.layout.activity_main);

        mAdView = findViewById(R.id.adView);
        mAdView.setVisibility(View.GONE);

        DatabaseManager.initializeDatabaseManager(this);

        showFragment();

        new Handler().postDelayed(this::initializeMobileAds, 100);
    }

    void showFragment(){
        currentShowingFragmentName = DatabaseManager.getInstance().getCurrentShowingFragmentName();
        if(currentShowingFragmentName == null){
            currentShowingFragmentName = CurrentShowingFragmentName.TUITION_LIST;
            DatabaseManager.getInstance().storeCurrentShowingFragmentName(currentShowingFragmentName);
        }
        Log.d(DEBUG_TAG, "Showing Fragment :"+ String.valueOf(currentShowingFragmentName));

        switch (currentShowingFragmentName){
            case TUITION_LIST:
                currentShowingFragment = new TuitionListFragment();
                break;
            case TUITION_DAY_LIST:
                currentShowingFragment = new TuitionDayListFragment();
                break;
        }
        currentShowingFragment.setMyFragmentListener(this);

        //before changing fragment


        //when changing fragment
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, currentShowingFragment);
        fragmentTransaction.show(currentShowingFragment);
        fragmentTransaction.commit();

        Log.d(DEBUG_TAG, "i think problem is here");

        //after changing fragment

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(DEBUG_TAG, "onPause called");
        new Handler().postDelayed(this::closeAds, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(DEBUG_TAG, "onResume called");
        new Handler().postDelayed(this::loadAds, 100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(DEBUG_TAG, "onOptionsItemSelected called");
        currentShowingFragment.handleMenuItemSelection(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (currentShowingFragmentName){
            case TUITION_LIST:
                getMenuInflater().inflate(R.menu.tuition_list_menu, menu);
                break;
            case TUITION_DAY_LIST:
                getMenuInflater().inflate(R.menu.tuition_day_list_menu, menu);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void changeFragmentTo(CurrentShowingFragmentName fragmentName) {
        Log.d(DEBUG_TAG, "fragment changing request received");
        DatabaseManager.getInstance().storeCurrentShowingFragmentName(fragmentName);

        //before changin the fragment we should change the menu
        super.invalidateOptionsMenu();

        showFragment();
    }

    @Override
    public void onBackPressed(){
        if(!currentShowingFragment.handleBackButtonPressed()) {
            super.onBackPressed();
        }
    }

    void initializeMobileAds(){
        if(MOBILE_ADS_INITIALIZED){
            Log.d(DEBUG_TAG, "Mobile ads already Initialized!");
            return;
        }
        new Handler(Looper.getMainLooper()).post(() -> {
            if(!this.isInternetAvailable()) {
                Log.d(DEBUG_TAG, "Internet is not available so can't initialize Mobile ads.[return]");
                return;
            }
            Log.d(DEBUG_TAG, "Initializing Mobile Ads");
            MobileAds.initialize(this, getString(R.string.def_app_id));
            MOBILE_ADS_INITIALIZED = true;
            Log.d(DEBUG_TAG, "Mobile Ads Initialization Done");

            loadAds();
        });
    }

    public void loadAds(){
        new Handler(Looper.getMainLooper()).post(() -> {
            if(!MOBILE_ADS_INITIALIZED){
                Log.d(DEBUG_TAG, "Mobile ads is not Initialized!");
                return;
            }
            if(SHOWING_MOBILE_ADS){
                Log.d(DEBUG_TAG, "Already Showing Ads so [RETURN]");
                return;
            }
            if(!this.isInternetAvailable()){
                Log.d(DEBUG_TAG, "Internet is not available so no need to show ads.[return]");
                return;
            }

            Log.d(DEBUG_TAG, "loading banner ads");
            SHOWING_MOBILE_ADS = true;
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            mAdView.loadAd(adRequest);
            Log.d(DEBUG_TAG, "loaded banner ads");
        });
    }

    private void closeAds(){
        if(mAdView != null){
            mAdView.setVisibility(View.GONE);
            mAdView.destroy();
        }
        SHOWING_MOBILE_ADS = false;
    }

    boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager == null) return false;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
