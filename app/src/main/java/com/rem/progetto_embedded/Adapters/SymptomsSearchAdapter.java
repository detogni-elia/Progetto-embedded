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

public class SymptomsSearchAdapter extends RecyclerView.Adapter<SymptomsSearchAdapter.SymptomsViewHolder> implements Filterable {
    private ArrayList<DataWrapper> fullData;
    private ArrayList<DataWrapper> filteredData = new ArrayList<>();
    private OnSymptomCheckListener listener;

    public SymptomsSearchAdapter(ArrayList<DataWrapper> data, OnSymptomCheckListener listener){
        fullData = data;
        filteredData.addAll(data);
        this.listener = listener;
    }

    @Override
    public SymptomsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout item = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.symptoms_search_item_layout, parent, false);
        return new SymptomsViewHolder(item, -1, listener);
    }

    @Override
    public void onBindViewHolder(SymptomsViewHolder holder, int position) {
        int absolutePos = filteredData.get(position).absolutePos;
        holder.symptomSwitch.setTag(false);
        holder.symptomSwitch.setChecked(filteredData.get(position).selected);
        holder.symptomSwitch.setTag(true);
        holder.symptom.setText(filteredData.get(position).symptom);
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

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<DataWrapper> filtered = new ArrayList<>();
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
        public static ArrayList<DataWrapper> wrapData(String[] symptomsList, boolean[] selections){
            ArrayList<DataWrapper> toReturn = new ArrayList<>();
            int i = 0;
            for(String symptom: symptomsList){
                toReturn.add(new DataWrapper(symptom, selections[i++], toReturn.size()));
            }
            return toReturn;
        }
    }

    //TODO: MOVE THIS CLASS OUTSIDE OR MOVE SpeciesListItemViewHolder OUTSIDE OF RELATIVE ADAPTER, FOR CONSISTENCY
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
            //We save the absolute position of the element in fullData so that we can communicate the checked symptoms to the listener
            symptomAbsolutePosition = position;
            symptomSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //If tag is set to false then we ignore the callback (callback is invoked even when we set the switch programmatically)
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

    public interface OnSymptomCheckListener{
        void onSymptomChecked(int position, boolean checked);
    }
}
