package com.sapnu.tuitiondays;

import android.app.Fragment;
import android.view.MenuItem;

public abstract class MyFragment extends Fragment {

    //every fragment have to keep an way to initialize fragment listener
    abstract void setMyFragmentListener(MyFragmentListener myFragmentListener);

    //currently showing fragment must have to keep an way to control back pressed
    abstract boolean handleBackButtonPressed();

    //currently showing fragment must have to keep an way to control menu item selection
    abstract void handleMenuItemSelection(MenuItem menuItem);





    //abstract void refreshContentViewForCurrentFragment();

   // abstract void refreshMenuOptionForCurrentFragment();

    //abstract void onCurrentFragmentClose();
}
