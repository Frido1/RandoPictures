package com.frido.rando.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.frido.rando.ImageViewFullscreen;
import com.frido.rando.Objects.RandoPicture;
import com.frido.rando.R;
import com.frido.rando.Utilities.OnItemClickListener;
import com.frido.rando.Utilities.RecycleViewerAdapter;
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
 * Created by fjmar on 1/31/2017.
 */

public class HistoryStaggeredGridFragment extends Fragment {
    private ArrayList<String> listUrls;
    @BindView(R.id.staggeredRecycleView)
    RecyclerView recyclerView;
    RecycleViewerAdapter recycleViewerAdapter;
    private StaggeredGridLayoutManager gridLayoutManager;
    private Unbinder unbinder;
    private Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listUrls = getURLSFromFirebase();
        this.context = getActivity();
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_staggeredgrid,container,false);
        unbinder = ButterKnife.bind(this,view );
        OnItemClickListener itemClickListener = getListener();
        recycleViewerAdapter = new RecycleViewerAdapter(listUrls,context,itemClickListener);
        recyclerView.setAdapter(recycleViewerAdapter);
        gridLayoutManager = new StaggeredGridLayoutManager(3,1);
        recyclerView.setLayoutManager(gridLayoutManager);
        return  view;
    }

    private OnItemClickListener getListener() {
        OnItemClickListener itemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(String url) {
                String thumbnailFilename = (String) url;
                String picUrl = RandoPicture.setFatPitaURL_FromThumbnailName(thumbnailFilename);
                Intent intent = new Intent(getActivity().getApplicationContext(),ImageViewFullscreen.class);
                intent.putExtra("imageToView", picUrl);
                intent.putExtra("fileName",thumbnailFilename);
                startActivity(intent);
            }
        };
        return itemClickListener;
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
                    recycleViewerAdapter.notifyDataSetChanged();
                    gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return urls;

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.grid_list_menu,menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.voronoiSwitch:
                switchVoronoFragment();
                break;
            case R.id.list_menu_action:
                switchGridoFragments();
                break;
            default: break;
        }
        return true;
    }

    private void switchGridoFragments() {
        Fragment listFragment   = new HistoryListFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragmentContainer, listFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    private void switchVoronoFragment() {
        Fragment VoronoFragment = new VoronoFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragmentContainer, VoronoFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
