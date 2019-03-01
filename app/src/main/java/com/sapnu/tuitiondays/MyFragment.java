package com.sapnu.tuitiondays;

import android.app.Fragment;

public abstract class MyFragment extends Fragment {

    abstract void refreshContentViewForCurrentFragment();

    abstract void refreshMenuOptionForCurrentFragment();

    abstract void onCurrentFragmentClose();
}
