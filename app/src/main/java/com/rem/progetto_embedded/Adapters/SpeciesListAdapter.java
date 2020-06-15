package com.rem.progetto_embedded.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rem.progetto_embedded.Database.Entity.Creatures;
import com.rem.progetto_embedded.R;
import com.rem.progetto_embedded.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter used in SpeciesListActivity loads implements filterable in order to filter the data displayed
 */
public class SpeciesListAdapter extends RecyclerView.Adapter<SpeciesListItemViewHolder> implements Filterable {
    //Store all the data loaded
    private List<Creatures> fullData = new ArrayList<>();
    //Store the filtered data, this is the actual data shown in the RecyclerView
    private List<Creatures> filteredData = new ArrayList<>();
    //Listener fo clicks on the elements of the RecyclerView
    private OnSpeciesSelectedListener clickListener;
    //Cache to store images's Bitmaps

    private final String ROOT_PATH;

    //Tag for logging
    private final String TAG = getClass().getSimpleName();

    /**
     * Interface defines method to handle click of an item in the RecyclerView
     */
    public interface OnSpeciesSelectedListener {
        void onSpeciesListItemClick(int position);
    }

    public SpeciesListAdapter(Context context, OnSpeciesSelectedListener listener){
        clickListener = listener;
        ROOT_PATH = Utilities.getResourcesFolder(context).toString();
    }

    @Override
    public SpeciesListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView item = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.species_list_item_layout, parent, false);
        return new SpeciesListItemViewHolder(item, clickListener);
    }

    @Override
    public void onBindViewHolder(SpeciesListItemViewHolder holder, int position) {
        holder.setImage(ROOT_PATH + filteredData.get(position).getImage());
        holder.setName(filteredData.get(position).getCommonName());
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    //We define a filter for the data to implement searching for animals/plants's names
    private Filter filter = new Filter() {
        //This method will be automatically executed in a background thread so that the ui won't slow down
        //Checks the text filter passed and returns only the items containing the filter text in their names
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Creatures> filteredData = new ArrayList<>();
            if(constraint.toString().isEmpty())
                filteredData.addAll(fullData);
            else{
                for(Creatures wrapper: fullData){
                    String name = wrapper.getCommonName();
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
            filteredData.addAll((ArrayList<Creatures>) results.values);
            notifyDataSetChanged();
        }
    };

    public void setData(List<Creatures> data){
        fullData = data;
        filteredData.clear();
        filteredData.addAll(fullData);
    }

    public Creatures getElementAt(int position){
        return filteredData.get(position);
    }

    public void setClickListener(OnSpeciesSelectedListener listener){
        this.clickListener = listener;
    }
}
