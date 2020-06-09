package com.rem.progetto_embedded;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.LruCache;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.rem.progetto_embedded.Adapters.SpeciesListAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * SpeciesViewModel queries the database and is used inside SpeciesListActivity
 */

public class SpeciesViewModel extends AndroidViewModel {
    private LruCache<String, Bitmap> adapterCache;
    private MutableLiveData<ArrayList<SpeciesListAdapter.DataWrapper>> data;

    public SpeciesViewModel(Application application) {
        super(application);
        data = null;
        adapterCache = null;
    }

    /**
     * Query database for species of a certain country and category and provoking certain symptoms
     * @param country The country of the species
     * @param category The category of the species
     * @param symptoms The symptoms caused by the species
     * @return The species relative to country, category and that provoke each and every one of the selected symptoms, if symptoms is null then returns all species without filtering them
     */

    public LiveData<ArrayList<SpeciesListAdapter.DataWrapper>> getSpecies(String country, String category, String[] symptoms) {
        if(data == null) {
            data = new MutableLiveData<>();
            //Get path of downloaded resources (either in external sd or in emulated)
            File rootPath = Utilities.getResourcesFolder(getApplication().getApplicationContext());
            File[] images = null;
            if (rootPath != null) {
                //Get path to required images
                //Example path: files/India/Images/Animals/
                String imagesPath = rootPath.getPath() + "/" + country + "/Images/" + category;
                File imagesFolder = new File(imagesPath);
                //Get list of all images in folder
                //TODO: Images paths should be stored in a database
                images = imagesFolder.listFiles();
            }
            //Return empty dataset if for some reason there weren't any resources (eg: User deletes the resources folder without pausing the Activity)
            if (images == null || images.length == 0) {
                Utilities.showToast(getApplication().getApplicationContext(), getApplication().getApplicationContext().getString(R.string.images_load_error));
                return data;
                //adapter = new SpeciesListAdapter(null, this);
            }
            else {
                ArrayList<String> speciesNames = new ArrayList<>();
                //TODO: Get names from a database
                for (File image : images) {
                    speciesNames.add(image.getName());
                }

                //Wrap data inside DataWrapper class in order for SpeciesListAdapter to use it
                ArrayList<SpeciesListAdapter.DataWrapper> wrappedData = SpeciesListAdapter.DataWrapper.fromArrayList(new ArrayList<>(Arrays.asList(images)), speciesNames);
                data.setValue(wrappedData);
            }
        }
        return data;
    }

    /**
     * Method used to save SpeciesListAdapter's Bitmap cache during configuration changes
     * @param cache The adapter's cache to be restored after a configuration change
     */
    public void storeCache(LruCache<String, Bitmap> cache) {
        adapterCache = cache;
    }

    public LruCache<String, Bitmap> getStoredCache() {
        return adapterCache;
    }
}
