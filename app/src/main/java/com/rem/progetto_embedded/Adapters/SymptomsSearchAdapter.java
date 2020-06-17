package com.rem.progetto_embedded.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rem.progetto_embedded.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for SymptomsSearchActivity
 */

public class SymptomsSearchAdapter extends RecyclerView.Adapter<SymptomsSearchAdapter.SymptomsViewHolder> implements Filterable {
    private List<DataWrapper> fullData;
    private List<DataWrapper> filteredData = new ArrayList<>();
    private OnSymptomCheckListener listener;

    public SymptomsSearchAdapter(List<DataWrapper> data, OnSymptomCheckListener listener){
        fullData = data;
        filteredData.addAll(data);
        this.listener = listener;
    }
    @Override
    public SymptomsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout item = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.symptoms_search_item_layout, parent, false);
        //Initialize SymptomsViewHolder passing the layout, an invalid position that will be updated in obBindViewHolder and the listener that will handle clicks on the Switches
        return new SymptomsViewHolder(item, -1, listener);
    }

    @Override
    public void onBindViewHolder(SymptomsViewHolder holder, int position) {
        int absolutePos = filteredData.get(position).absolutePos;
        //We set the tag for the switch to ignore the method onCheckedChanged
        holder.symptomSwitch.setTag(false);
        //We set the Switch to the correct position
        holder.symptomSwitch.setChecked(filteredData.get(position).selected);
        //Now onCheckedChanged will have effect if called
        holder.symptomSwitch.setTag(true);

        holder.symptom.setText(filteredData.get(position).symptom);

        //Save absolute position of the symptom inside fullData inside the ViewHolder to correctly handle clicks
        holder.symptomAbsolutePosition = absolutePos;
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    @Override
    public Filter getFilter(){
        return filter;
    }

    //Filter to search for symptoms
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<DataWrapper> filtered = new ArrayList<>();
            if(constraint.toString().isEmpty())
                filtered.addAll(fullData);
            else{
                for(DataWrapper wrapper: fullData){
                    String symptom = wrapper.getSymptom();
                    if(symptom.toLowerCase().contains(constraint.toString().toLowerCase()))
                        filtered.add(wrapper);
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filtered;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData.clear();
            filteredData.addAll((ArrayList<DataWrapper>)results.values);
            notifyDataSetChanged();
        }
    };

    /**
     * Class wraps data for the adapter, used inside SymptomsSearchActivity to wrap symptoms data before passing it to this adapter
     * Each instance represents a Symptom and saves the name, if the relative Switch is selected and the absolute position of the symptom inside fullData
     */
    public static class DataWrapper{
        private String symptom;
        private boolean selected;
        private int absolutePos;

        DataWrapper(String s, boolean sel, int absolutePos){
            symptom = s;
            selected = sel;
            this.absolutePos = absolutePos;
        }

        String getSymptom(){
            return symptom;
        }
        public boolean isSelected(){
            return selected;
        }

        /**
         * Wraps the data for the adapter
         * @param symptomsList String array containing the names of the symptoms
         * @param selections boolean array containing the value for the selection of each symptom
         * @return List of DataWrapper objects enclosing the data
         */
        public static List<DataWrapper> wrapData(String[] symptomsList, boolean[] selections){
            List<DataWrapper> toReturn = new ArrayList<>();
            int i = 0;
            for(String symptom: symptomsList){
                toReturn.add(new DataWrapper(symptom, selections[i++], toReturn.size()));
            }
            return toReturn;
        }
    }

    /**
     * ViewHolder used in this Adapter
     */
    class SymptomsViewHolder extends RecyclerView.ViewHolder {
        private TextView symptom;
        private Switch symptomSwitch;
        private int symptomAbsolutePosition;

        SymptomsViewHolder(RelativeLayout root, int position, final OnSymptomCheckListener listener){
            super(root);
            symptom = root.findViewById(R.id.textView);
            symptomSwitch = root.findViewById(R.id.symptomSwitch);
            //Tag specifies if we have to ignore the onCheckedChanged callback
            symptomSwitch.setTag(true);
            //We save the absolute position of the element in fullData so that we can communicate to the listener when a symptom is checked.
            //We assume the listener has the full list of symptoms (that has passed to the adapter wrapped inside DataWrapper objects)
            symptomAbsolutePosition = position;
            symptomSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //If tag is set to false then we ignore the callback (callback is invoked even when we set the switch programmatically, but we want to respond only to actual user clicks)
                    if(!(boolean)symptomSwitch.getTag())
                        return;
                    //Update both filtered and complete data, to store the changes
                    fullData.get(symptomAbsolutePosition).selected = isChecked;
                    filteredData.get(getAdapterPosition()).selected = isChecked;
                    listener.onSymptomChecked(symptomAbsolutePosition, isChecked);
                }
            });
        }
    }

    /**
     * Interface defines a callback to update a listener about the position of the checked/unchecked symptom
     */
    public interface OnSymptomCheckListener{
        void onSymptomChecked(int position, boolean checked);
    }
}
