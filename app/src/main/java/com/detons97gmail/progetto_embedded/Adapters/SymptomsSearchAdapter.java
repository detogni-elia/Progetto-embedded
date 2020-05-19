package com.detons97gmail.progetto_embedded.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.detons97gmail.progetto_embedded.R;

import java.util.ArrayList;

public class SymptomsSearchAdapter extends RecyclerView.Adapter<SymptomsSearchAdapter.SymptomsViewHolder> implements Filterable {
    private ArrayList<DataWrapper> fullData;
    private ArrayList<DataWrapper> filteredData = new ArrayList<>();
    private ToDefineListener listener;

    public SymptomsSearchAdapter(ArrayList<DataWrapper> data, ToDefineListener listener){
        fullData = data;
        filteredData.addAll(data);
        this.listener = listener;
    }

    @Override
    public SymptomsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout item = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.symptoms_search_item, parent, false);
        return new SymptomsViewHolder(item, -1, listener);
    }

    @Override
    public void onBindViewHolder(SymptomsViewHolder holder, int position) {
        int absolutePos = filteredData.get(position).absolutePos;
        boolean sel = filteredData.get(position).selected;
        holder.symptomSwitch.setTag(false);
        holder.symptomSwitch.setChecked(filteredData.get(position).selected);
        holder.symptomSwitch.setTag(true);
        holder.symptom.setText(filteredData.get(position).symptom);
        holder.symptomAbsolutePosition = absolutePos;
        Log.v("ADAPTER", "Pos: " + position + " Abs: " + absolutePos + " Sel: " +sel);
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

        public DataWrapper(String s, boolean sel, int absolutePos){
            symptom = s;
            selected = sel;
            this.absolutePos = absolutePos;
        }
        public void setSymptom(String s){
            symptom = s;
        }
        public void setSelected(boolean sel){
            selected = sel;
        }
        public void setAbsolutePos(int pos){
            absolutePos = pos;
        }
        public String getSymptom(){
            return symptom;
        }
        public boolean getSelected(){
            return selected;
        }
        public int getAbsolutePos(){
            return absolutePos;
        }
        public static ArrayList<DataWrapper> getWrappedData(String[] symptomsList){
            ArrayList<DataWrapper> toReturn = new ArrayList<>();
            for(String symptom: symptomsList){
                toReturn.add(new DataWrapper(symptom, false, toReturn.size()));
            }
            return toReturn;
        }
    }

    class SymptomsViewHolder extends RecyclerView.ViewHolder {
        private TextView symptom;
        private Switch symptomSwitch;
        private int symptomAbsolutePosition = -1;
        private ToDefineListener listener;

        SymptomsViewHolder(RelativeLayout root, int position, ToDefineListener toDefineListener){
            super(root);
            symptom = root.findViewById(R.id.textView);
            symptomSwitch = root.findViewById(R.id.symptomSwitch);
            symptomSwitch.setTag(true);
            symptomAbsolutePosition = position;
            listener = toDefineListener;
            symptomSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!(boolean)symptomSwitch.getTag())
                        return;
                    fullData.get(symptomAbsolutePosition).selected = isChecked;
                    filteredData.get(getAdapterPosition()).selected = isChecked;
                    listener.onSwitchClicked(symptomAbsolutePosition, isChecked);
                    Log.v("ViewHolder", "SWITCH ATTIVO: " + isChecked + " ABS: " + symptomAbsolutePosition + " POS: " + getAdapterPosition());
                }
            });
        }
    }

    public interface ToDefineListener{
        void onSwitchClicked(int symptomPosition, boolean selected);
    }
}
