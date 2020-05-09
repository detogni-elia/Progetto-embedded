package com.detons97gmail.progetto_embedded;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class SpeciesListAdapter extends RecyclerView.Adapter<SpeciesListItemViewHolder> {
    private Drawable[] images;
    private String[] names;
    private Context context;
    private onSpeciesSelectedListener clickListener;

    SpeciesListAdapter(Context c, File[] files, String[] names, onSpeciesSelectedListener listener){
        context = c;
        if(files == null || names == null){
            this.images = new Drawable[20];
            this.names = new String[20];
            for(int i = 0; i < images.length; i++){
                images[i] = c.getResources().getDrawable(R.drawable.ic_placeholder_icon_vector);
                this.names[i] = "Nome placeholder " + i;
            }
        }
        else {
            images = new Drawable[files.length];
            for (int i = 0; i < files.length; i++) {
                images[i] = Drawable.createFromPath(files[i].getAbsolutePath());
            }
            this.names = names;
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
        holder.setImage(images[position]);
        holder.setName(names[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    void testChange(int position){
        images[position] = context.getResources().getDrawable(R.drawable.ic_placeholder_icon_vector);
        names[position] = "MODIFIED";
        notifyDataSetChanged();
    }

    public interface onSpeciesSelectedListener {
        void onSpeciesListItemClick(int position);
    }
}
