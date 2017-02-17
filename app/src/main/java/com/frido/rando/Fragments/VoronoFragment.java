package com.frido.rando.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.frido.rando.Objects.RandoPicture;
import com.frido.rando.R;
import com.frido.rando.Utilities.VoronoAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by fjmar on 1/26/2017.
 */

public class VoronoFragment extends Fragment {
    private ArrayList<String> listUrls;
    @BindView(R.id.historyListView) ListView listView;
    private Unbinder unbinder;
    private VoronoAdapter voronoAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history,container,false);
        listUrls = getURLSFromFirebase();
        unbinder = ButterKnife.bind(this,view );
        voronoAdapter = new VoronoAdapter(listUrls,getActivity().getApplicationContext());
        

        return view;

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.vorono_history_menu,menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.listSwitch:
                switchFragments();
                break;
            case R.id.gridLayout:
                switchGridFragments();
                break;
            default: break;
        }
        return true;
    }

    private void switchGridFragments() {
        Fragment gridFragment   = new HistoryStaggeredGridFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragmentContainer, gridFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    private void switchFragments() {
        Fragment historyListFragment = new HistoryListFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, historyListFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    private ArrayList<String> getURLSFromFirebase() {
        final ArrayList<String> urls = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userID = user.getUid();
        DatabaseReference reference = database.getReference("users").child(userID);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("firebase",dataSnapshot.toString());
                Iterable<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = dataSnapshotIterator.iterator();
                while (iterator.hasNext()){
                    RandoPicture randoPicture =iterator.next().getValue(RandoPicture.class);
                    urls.add(randoPicture.getThumbnail_ID());
                }
                voronoAdapter.update(urls);
                listView.setAdapter(voronoAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return urls;
    }
}
