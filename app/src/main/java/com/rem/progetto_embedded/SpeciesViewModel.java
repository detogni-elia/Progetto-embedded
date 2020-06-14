package com.rem.progetto_embedded;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.LruCache;

import androidx.collection.ArrayMap;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.rem.progetto_embedded.Database.AppDatabase;
import com.rem.progetto_embedded.Database.Entity.Contacts;
import com.rem.progetto_embedded.Database.Entity.Creatures;
import com.rem.progetto_embedded.Database.Entity.Symptoms;

import java.util.ArrayList;
import java.util.List;

/**
 * SpeciesViewModel queries the database and is used inside SpeciesListActivity
 */

public class SpeciesViewModel extends AndroidViewModel {
    private LruCache<String, Bitmap> adapterCache;
    private LiveData<List<Creatures>> data;

    public SpeciesViewModel(Application application) {
        super(application);
        data = null;
        adapterCache = null;
    }

    public LiveData<List<Creatures>> getSpecies(String country, String category, List<String> symptoms, String contact) {
        if(data == null) {
            AppDatabase db = AppDatabase.getInstance(getApplication().getApplicationContext(), country);
            if (symptoms == null || symptoms.isEmpty()) {
                data = db.creaturesDao().getFromCategory(category);
                //data = (AppDatabase.getInstance(getApplication().getApplicationContext(), country).creaturesDao().getFromCategory(category).getValue());
            }
            else{
                data = db.effectsDao().getCreatures(contact, category, symptoms);
            }
        }
        return data;
            /*
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
            */
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
