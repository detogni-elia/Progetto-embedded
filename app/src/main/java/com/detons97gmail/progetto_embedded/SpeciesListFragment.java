package com.detons97gmail.progetto_embedded;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Fragment will be attached to SpeciesListActivity and will show the species requested
 * Implements interface OnSpeciesSelectedListener of SpeciesListAdapter to enable onClickListener for every element
 */

public class SpeciesListFragment extends Fragment implements SpeciesListAdapter.OnSpeciesSelectedListener {
    private static final String TAG = "SpeciesListFragment";
    //Static variables for permission requests
    //Variables to store UI objects
    private RecyclerView recyclerView;
    //LayoutManager for the RecyclerView
    private RecyclerView.LayoutManager layoutManager;
    //Adapter for the RecyclerView
    private SpeciesListAdapter adapter;
    //Application context
    private Context context;

    //State variables
    /**
     * Required empty public constructor
     */
    public SpeciesListFragment() {
    }

    /**
     * We override onAttach in order to store the application Context
     * Method called each time fragment is attached to Activity. For example, when orientation changes the Activity is destroyed then created anew
     * but fragment will be retained and attached to new Activity.
     * @param c Application Context
     */
    @Override
    public void onAttach(Context c){
        super.onAttach(c);
        context = c;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //We want to override onCreateOptionsMenu to implement search filtering
        setHasOptionsMenu(true);
        //We want to retain fragment instance on configuration changes such as screen rotation
        //in order to maintain the adapter created as to not load all the data and images again.
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Initialize new adapter if necessary
        if(adapter ==  null) {
            Log.v(TAG, "Adapter was null in method onCreateView");
            initiliazeAdapter();
        }
        //Not null when instance was retained.
        else {
            Log.v(TAG, "Adapter was not null in method onCreateView");
        }
        //Inflate layout
        //Views must always be inflated even if instance was retained.
        return inflater.inflate(R.layout.species_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        recyclerView = view.findViewById(R.id.species_list_recycler_view);
        //Best practice suggested by Google when data will remain unchanged
        //Commented out because of search filtering implementation
        //recyclerView.setHasFixedSize(true);

        //Display items as list if orientation is portrait, else display them side by side
        int orientation = context.getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
        }
        else {
            layoutManager = new GridLayoutManager(context, 2);
            recyclerView.setLayoutManager(layoutManager);
        }

        recyclerView.setAdapter(adapter);
        //recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        //Clear all references that might cause memory leaks
        recyclerView.setAdapter(null);
        recyclerView = null;
        layoutManager = null;
        context = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        //Get search item and set Listener for queries
        MenuItem item = menu.getItem(0);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Initialize adapter loading data from memory (we don't need to ask for permissions if we load from application specific storage)
     */
    //private final ArrayList<SpeciesListAdapter.DataWrapper> wrap = new ArrayList<>();

    private void initiliazeAdapter(){
        //Root path to our application specific storage (the "Files" directory)
        File rootPath = context.getExternalFilesDir(null);
        File[] images = null;
        if(rootPath != null) {
            //Get path to required images
            //TODO: Here we will need to modify the path depending on the species requested and the region
            String imagesPath = rootPath.getPath() + "/images";
            File imagesFolder = new File(imagesPath);
            //Get list of all images in folder
            //TODO: Images paths should be stored in a database
            images = imagesFolder.listFiles();
        }
        //If no images were found load default list with placeholder data
        if(images == null || images.length == 0)
            adapter = new SpeciesListAdapter(null, null, this);

        else{
            ArrayList<String> speciesNames = new ArrayList<>();
            //Names will be derived from file names
            //TODO: Get names from a database
            for (File image : images) {
                speciesNames.add(image.getName());
            }
            adapter = new SpeciesListAdapter(new ArrayList<>(Arrays.asList(images)), speciesNames, this);
            /*
            ArrayList<File> imagesList = new ArrayList<>(Arrays.asList(images));
            adapter = new SpeciesListAdapter(wrap,this);
            wrap.add(new SpeciesListAdapter.DataWrapper(imagesList.get(0).getAbsolutePath(), speciesNames.get(0)));
            adapter.myNotifyDataSetChanged();
            wrap.add(new SpeciesListAdapter.DataWrapper(imagesList.get(1).getAbsolutePath(), speciesNames.get(1)));
            wrap.add(new SpeciesListAdapter.DataWrapper(imagesList.get(2).getAbsolutePath(), speciesNames.get(2)));
            adapter.myNotifyDataSetChanged();

             */
        }
    }

    //Implement adapter interface method
    @Override
    public void onSpeciesListItemClick(int position) {
        Utilities.showToast(context, "Click on element n. " + position, Toast.LENGTH_SHORT);
    }
}
