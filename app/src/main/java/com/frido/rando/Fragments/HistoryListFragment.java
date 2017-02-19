package com.frido.rando.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.frido.rando.ImageViewFullscreen;
import com.frido.rando.Objects.RandoPicture;
import com.frido.rando.R;
import com.frido.rando.Utilities.CustomListAdapter;
import com.frido.rando.Utilities.SyncThumbnail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by fjmar on 1/26/2017.
 */

public class HistoryListFragment extends Fragment  {
    private ArrayList<String> listUrls;
    @BindView(R.id.historyListView)
    ListView listView;
    private Unbinder unbinder;
    private CustomListAdapter customListAdapter;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.context = getActivity().getApplicationContext();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_history,container,false);
        listUrls = getURLSFromFirebase();
        unbinder = ButterKnife.bind(this,view );
        customListAdapter = new CustomListAdapter(context,listUrls);

        listView.setAdapter(customListAdapter);
        AdapterView.OnItemClickListener onItemClickListener = getListener(customListAdapter);
        listView.setOnItemClickListener(onItemClickListener);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        AbsListView.MultiChoiceModeListener multiChoiceModeListener = getMultiListener();
        listView.setMultiChoiceModeListener(multiChoiceModeListener);
        return view;

    }

    private AbsListView.MultiChoiceModeListener getMultiListener() {
        AbsListView.MultiChoiceModeListener multiChoiceModeListener = new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            mode.setTitle(listView.getCheckedItemCount() +" selected");
                CardView cardView = (CardView) listView.getChildAt(position);
                if (cardView !=null) {
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green));
                }

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.delete_contextual_menu,menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        Log.d("menu", "delete action");
                        delete();
                        mode.finish();
                        return true;
                }
                return false;
            }

            private void delete() {
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                for (int i = 0; i < checked.size(); i++) {
                    if (checked.valueAt(i) == true){
                        //this url is just thumb_xxxx.jpg
                        final String url = (String) listView.getItemAtPosition(checked.keyAt(i));
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference reference = database.getReference();
                        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Query findData = reference.child("users").child(userID);
                        findData.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren();
                                Iterator<DataSnapshot> iterator = dataSnapshotIterator.iterator();
                                while (iterator.hasNext()) {
                                    DataSnapshot dataSnapshot1= (DataSnapshot) iterator.next();
                                    RandoPicture randoPicture = (RandoPicture) dataSnapshot1.getValue(RandoPicture.class);
                                    if (randoPicture.getThumbnail_ID().equals(url)){
                                        String key = dataSnapshot1.getKey();
                                        reference.child("users").child(userID).child(key).removeValue();
                                        customListAdapter.notifyDataSetChanged();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("database", databaseError.getDetails());

                            }
                        });

                        Log.d("menu", "deleting: "+url);
                    }
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        };
                return  multiChoiceModeListener;
    }

    private void checkThumbnailPaths(ArrayList<String> listUrls, Context applicationContext) {

        File file = context.getFilesDir();
        ArrayList<String> filesToBeDeleted = new ArrayList<>();
         String [] listOfFilesOnDevice=file.list();
        for (String stringFile :
                listOfFilesOnDevice) {
            int end = stringFile.length()-3;
            String temp = stringFile.substring(end);
            if (temp.equals("jpg")){
                //String temp2 = "thumb_"+stringFile;
                filesToBeDeleted.add(stringFile);
            }
        }



            for (String url :
                    listUrls) {
                File filePath = applicationContext.getFileStreamPath(url);
                //check to see if anything was added on another device
                if (!filePath.exists()){
                    String urlFinal = RandoPicture.setFatPitaURL_FromThumbnailName(url);
                    SyncThumbnail syncThumbnail = new SyncThumbnail(urlFinal,filePath,applicationContext);
                    syncThumbnail.getAndSaveThumbnail();
                }
                //check to see if it was deleted on another device
                if (filesToBeDeleted.contains(url)){
                    int location = filesToBeDeleted.indexOf(url);
                    filesToBeDeleted.remove(location);
                }
            }
        //check to see if any filse are to be deleted
        if (!filesToBeDeleted.isEmpty()){
            for (String fileName :
                    filesToBeDeleted) {
                File filePath = context.getFileStreamPath(fileName);
                filePath.delete();
            }
        }

    }


    private AdapterView.OnItemClickListener getListener(final CustomListAdapter customListAdapter) {
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String thumbnailFilename = (String) customListAdapter.getItem(position);
                String picUrl = RandoPicture.setFatPitaURL_FromThumbnailName(thumbnailFilename);
                Intent intent = new Intent(getActivity().getApplicationContext(),ImageViewFullscreen.class);
                intent.putExtra("imageToView", picUrl);
                intent.putExtra("fileName",thumbnailFilename);
                startActivity(intent);
            }
        };
                return onItemClickListener;
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
                checkThumbnailPaths(listUrls,getActivity().getApplicationContext());
                customListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("database", databaseError.toString());

            }
        });

        return urls;
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
            case R.id.voronoiSwitch:
                switchVoronoFragment();
                break;
            case R.id.gridLayout:
                switchGridoFragments();
                break;
            default: break;
        }
        return true;
    }

    private void switchGridoFragments() {
        Fragment gridFragment   = new HistoryStaggeredGridFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragmentContainer, gridFragment);
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

}
