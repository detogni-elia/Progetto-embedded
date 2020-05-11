package com.detons97gmail.progetto_embedded;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.util.Log;
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

    /**
     * Interface defines method to handle click of an item in the RecyclerView
     */

    public interface OnSpeciesSelectedListener {
        void onSpeciesListItemClick(int position);
    }

    SpeciesListAdapter(Context c, ArrayList<File> images, ArrayList<String> names, OnSpeciesSelectedListener listener){
        //If not all files are available show placeholder informations
        if(images == null || names == null || names.size() != images.size()) {
            for(int i = 0; i < 20; i++)
                fullData.add(new DataWrapper(null, "Nome placeholder " + i));

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
        Log.v("SpeciesListAdapter", "Called constructor");
    }
    @Override
    public SpeciesListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView item = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.species_list_item_layout, parent, false);
        return new SpeciesListItemViewHolder(item, clickListener);
    }

    @Override
    public void onBindViewHolder(SpeciesListItemViewHolder holder, int position) {
        holder.setImage(filteredData.get(position).getImage());
        holder.setName(filteredData.get(position).getName());
        Log.v("SpeciesListAdapter", "Called onBindViewHolder");
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    /*
    void testChange(int position){
        images.set(position, context.getResources().getDrawable(R.drawable.ic_placeholder_icon_vector));
        names.set(position, "MODIFIED");
        notifyDataSetChanged();
    }

     */

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
