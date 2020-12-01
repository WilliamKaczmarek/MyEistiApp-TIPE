package com.example.myeisti.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentManager;

import com.example.myeisti.classe.Marks;
import com.example.myeisti.fragments.sem1;
import com.example.myeisti.fragments.sem2;

public class PageAdapter extends FragmentPagerAdapter {

    private int numoftabs;
    private Marks[] tab;

    public PageAdapter(FragmentManager fm, int numOfTabs,Marks[] tab) {
        super(fm);
        this.numoftabs = numOfTabs;
        this.tab = tab;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                return (sem1.newInstance(this.tab));
            case 1 :
                return  (sem2.newInstance(this.tab));
            default :
                return null;
        }
    }

    @Override
    public int getCount() {
        return (2);
    }


    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
