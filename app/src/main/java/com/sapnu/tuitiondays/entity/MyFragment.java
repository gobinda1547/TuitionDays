package com.sapnu.tuitiondays.entity;

import android.app.Fragment;
import android.view.MenuItem;

public abstract class MyFragment extends Fragment {

    //every fragment have to keep an way to initialize fragment listener
    public abstract void setMyFragmentCallBacks(MyFragmentCallBacks callBacks);

    //currently showing fragment must have to keep an way to control back pressed
    public abstract boolean handleBackButtonPressed();

    //currently showing fragment must have to keep an way to control menu item selection
    public abstract void handleMenuItemSelection(MenuItem menuItem);

}
