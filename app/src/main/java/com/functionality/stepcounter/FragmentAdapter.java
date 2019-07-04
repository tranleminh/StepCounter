package com.functionality.stepcounter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter {
    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        graphFragment graphFragment = new graphFragment();
        Bundle bundle = new Bundle();
        position = position+1;
        bundle.putString("message", "Hello from page : "+position);
        graphFragment.setArguments(bundle);
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
