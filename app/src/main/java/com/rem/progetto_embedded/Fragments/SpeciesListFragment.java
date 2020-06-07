package com.rem.progetto_embedded.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rem.progetto_embedded.Activities.AnimalDetailsActivity;
import com.rem.progetto_embedded.Values;
import com.rem.progetto_embedded.R;
import com.rem.progetto_embedded.Adapters.SpeciesListAdapter;
import com.rem.progetto_embedded.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Fragment will be attached to SpeciesListActivity and will show the species requested
 * Implements interface OnSpeciesSelectedListener of SpeciesListAdapter to enable onClickListener for every element
 */

public class SpeciesListFragment extends Fragment implements SpeciesListAdapter.OnSpeciesSelectedListener {
    private static final String TAG = "SpeciesListFragment";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SpeciesListAdapter adapter;
    private Context context;

    private ArrayList<SpeciesListAdapter.DataWrapper> data;

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
        //Initialize new adapter if necessary
        if(adapter ==  null && getArguments() != null) {
            Log.v(TAG, "Adapter was null in method onCreateView");
            initiliazeAdapter(getArguments().getString(Values.EXTRA_COUNTRY), getArguments().getString(Values.EXTRA_CATEGORY), getArguments().getStringArray(Values.EXTRA_SYMPTOMS));
        }
        //Not null when instance was retained.
        else {
            Log.v(TAG, "Adapter was not null in method onCreateView");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Inflate layout
        //Views must always be inflated even if instance was retained.
        return inflater.inflate(R.layout.species_list_fragment_layout, container, false);
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
     * Initialize adapter loading data from database (we don't need to ask for permissions if we load from application specific storage)
     * @param country The string of the country in the database
     * @param category The string of the species category in the database
     * @param symptoms The array containing the list of symptoms to filter the query result
     */
    private void initiliazeAdapter(String country, String category, String[] symptoms){
        if(symptoms == null)
            //Adapter will have to query all elements
            ;
        //Get root path of app's files (either in external sd or in emulated)
        File rootPath = Utilities.getResourcesFolder(requireContext());
        //File rootPath = context.getExternalFilesDir(null);
        //File rootPath = context.getFilesDir();
        File[] images = null;
        if(rootPath != null) {
            //Get path to required images
            //Example path: files/India/Images/Animals/
            String imagesPath = rootPath.getPath() + "/" + country + "/Images/" + category;
            Log.v(TAG, imagesPath);
            File imagesFolder = new File(imagesPath);
            //Get list of all images in folder
            //TODO: Images paths should be stored in a database
            images = imagesFolder.listFiles();
        }
        //If no images were found initialize dummy adapter as to not show error message every time the method onCreateView is called
        //TODO: Show AlertDialog instead of Toast
        if(images == null || images.length == 0) {
            adapter = new SpeciesListAdapter(null, this);
            Utilities.showToast(context, context.getString(R.string.images_load_error), Toast.LENGTH_SHORT);
        }

        else{
            ArrayList<String> speciesNames = new ArrayList<>();
            //Names will be derived from file names
            //TODO: Get names from a database
            for (File image : images) {
                speciesNames.add(image.getName());
            }

            data = SpeciesListAdapter.DataWrapper.fromArrayList(new ArrayList<>(Arrays.asList(images)), speciesNames);
            adapter = new SpeciesListAdapter(data, this);
        }
    }

    //Implement adapter interface method
    @Override
    public void onSpeciesListItemClick(int position) {
        Intent startIntent = new Intent(getContext(), AnimalDetailsActivity.class);
        startIntent.putExtra(Values.EXTRA_IMAGE_PATH, data.get(position).getImage());
        startIntent.putExtra(Values.EXTRA_NAME, data.get(position).getName());
        //TODO: WE SHOULD GET THIS EXTRA INFO ONLY WHEN REQUESTED, QUERYING THE DATABASE.
        startIntent.putExtra(Values.EXTRA_SPECIES, "Nome della specie");
        startIntent.putExtra(Values.EXTRA_DIET, "Tipo di dieta");
        //TODO: ADD VARIOUS SYMPTOMS AND LOCALIZATION
        //TODO: ADD SYSTEM TO LOCALIZE ON MAP (FOR EXAMPLE, CENTER AND RADIUS TO DESCRIBE AN AREA WHERE THE ANIMAL, PLANT, INSECT CAN BE FOUND)
        startIntent.putExtra(Values.EXTRA_SYMPTOMS, getString(R.string.details_symptom_bite) + ": Gonfiore, bruciore, dolore, sanguinamento, intorpidimento");
        startActivity(startIntent);
    }

    //To call resizeImageCache in Adapter class
    public void resizeImageCache()
    {
        adapter.resizeImageCache();
    }

    //To call deleteImageCache in Adapter class
    public void deleteImageCache()
    {
        adapter.deleteImageCache();
    }
}
