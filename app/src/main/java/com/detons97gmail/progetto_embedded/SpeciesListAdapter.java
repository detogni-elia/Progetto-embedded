package com.detons97gmail.progetto_embedded;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

/**
 * Adapter used in SpeciesListFragment loads implements filterable in order to filter the data displayed
 */

public class SpeciesListAdapter extends RecyclerView.Adapter<SpeciesListItemViewHolder> implements Filterable {
    /**
     * Private class wraps the data in order to bind species name with relative image for the purpose of filtering the data
     */
    private static class DataWrapper{
        private String image;
        private String name;

        DataWrapper(String img, String n){
            image = img;
            name = n;
        }
        private String getImage(){
            return image;
        }
        private String getName(){
            return name;
        }
    }

    //Store all the data loaded
    private ArrayList<DataWrapper> fullData = new ArrayList<>();
    //Store the filtered data, this will be the data passed to the RecyclerView
    private ArrayList<DataWrapper> filteredData = new ArrayList<>();
    //Listener fo clicks on the elements
    private OnSpeciesSelectedListener clickListener;
    //Cache for images
    private LruCache<String, Bitmap> imageCache;
    //Tag for logging
    private static final String TAG = "SpeciesListAdapter";

    /**
     * Interface defines method to handle click of an item in the RecyclerView
     */

    public interface OnSpeciesSelectedListener {
        void onSpeciesListItemClick(int position);
    }

    SpeciesListAdapter(ArrayList<File> images, ArrayList<String> names, OnSpeciesSelectedListener listener){
        //Load default list of placeholder items if something went wrong
        if(images == null || names == null || names.size() != images.size()) {
            for(int i = 0; i < 20; i++)
                fullData.add(new DataWrapper("", "Placeholder item n. " + i));

            filteredData.addAll(fullData);
        }
        else {
            for(int i = 0; i < images.size(); i++){
                fullData.add(new DataWrapper(images.get(i).getAbsolutePath(), names.get(i)));
            }
            filteredData.addAll(fullData);
        }
        //Listener passed will be the SpeciesListFragment that implements the interface
        clickListener = listener;
        //50 Mb of cache
        int cacheSize = 1024 * 1024 * 50;
        //LruCache takes cacheSize in Kb
        imageCache = new LruCache<String, Bitmap>(cacheSize / 1024){
            @Override
            protected int sizeOf(String key, Bitmap bitmap){
                return bitmap.getByteCount() / 1024;
            }
        };

    }
    @Override
    public SpeciesListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView item = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.species_list_item_layout, parent, false);
        return new SpeciesListItemViewHolder(item, clickListener);
    }

    @Override
    public void onBindViewHolder(SpeciesListItemViewHolder holder, int position) {
        Bitmap cachedImage = imageCache.get(filteredData.get(position).getImage());
        //Load image from storage if not in cache
        if(cachedImage == null){
            //TODO: Load image in background thread instead of UI thread, AsyncTask deprecated in api level R
            //https://android-developers.googleblog.com/2009/05/painless-threading.html  contains threading solutions
            Bitmap loadedImage = BitmapFactory.decodeFile(filteredData.get(position).getImage());
            //If image not available set placeholder image
            if(loadedImage == null)
                holder.setPlaceholderImage();

            else {
                holder.setImage(loadedImage);
                //Save image to cache
                imageCache.put(filteredData.get(position).getImage(), loadedImage);
                Log.v(TAG, "Added image to cache: " + filteredData.get(position).getImage());
            }
        }
        else {
            holder.setImage(cachedImage);
            Log.v(TAG, "Loaded image from cache: " + filteredData.get(position).getImage());
        }

        //holder.setImage(filteredData.get(position).getImage());
        holder.setName(filteredData.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    //We define a filter for the data
    private Filter filter = new Filter() {
        //This method will be automatically executed in background thread so that the ui won't slow down
        //Checks the text filter passed and returns only the items containing the filter text in their names
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<DataWrapper> filteredData = new ArrayList<>();
            if(constraint.toString().isEmpty())
                filteredData.addAll(fullData);
            else{
                for(DataWrapper wrapper: fullData){
                    String name = wrapper.getName();
                    if(name.toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredData.add(wrapper);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredData;
            return filterResults;
        }
        //This method will be executed on UI thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData.clear();
            filteredData.addAll((ArrayList<DataWrapper>) results.values);
            notifyDataSetChanged();
        }
    };
}
