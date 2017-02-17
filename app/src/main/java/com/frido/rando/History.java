package com.frido.rando;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.frido.rando.Fragments.HistoryListFragment;


public class History extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment historyListFragment = new HistoryListFragment();
        transaction.add(R.id.fragmentContainer,historyListFragment);
        transaction.commit();
    }


}
