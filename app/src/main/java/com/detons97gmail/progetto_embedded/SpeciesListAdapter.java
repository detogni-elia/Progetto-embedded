package com.detons97gmail.progetto_embedded;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

/**
 * Adapter used in SpeciesListFragment loads
 */

public class SpeciesListAdapter extends RecyclerView.Adapter<SpeciesListItemViewHolder> implements Filterable {
    private static class DataWrapper{
        private Drawable image;
        private String name;

        DataWrapper(Drawable img, String n){
            image = img;
            name = n;
        }
        private void setImage(Drawable newImg){
            image = newImg;
        }
        private void setName(String newName){
            name = newName;
        }
        private Drawable getImage(){
            return image;
        }
        private String getName(){
            return name;
        }
    }
    private ArrayList<DataWrapper> fullData = new ArrayList<>();
    private ArrayList<DataWrapper> filteredData = new ArrayList<>();
    private Context context;
    private onSpeciesSelectedListener clickListener;

    SpeciesListAdapter(Context c, ArrayList<File> images, ArrayList<String> names, onSpeciesSelectedListener listener){
        //context = c;
        if(images == null || names == null || names.size() != images.size()) {
            //this.images = new ArrayList<>();
            //this.names = new ArrayList<>();
            for(int i = 0; i < 20; i++)
                fullData.add(new DataWrapper(c.getResources().getDrawable(R.drawable.ic_placeholder_icon_vector), "Nome placeholder " + i));

            filteredData.addAll(fullData);

        }
        else {
            for(int i = 0; i < images.size(); i++){
                fullData.add(new DataWrapper(Drawable.createFromPath(images.get(i).getAbsolutePath()), names.get(i)));
            }
            filteredData.addAll(fullData);
        }
        clickListener = listener;

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

    private Filter filter = new Filter() {
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

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData.clear();
            filteredData.addAll((ArrayList<DataWrapper>) results.values);
            notifyDataSetChanged();
        }
    };

    public interface onSpeciesSelectedListener {
        void onSpeciesListItemClick(int position);
    }
}
