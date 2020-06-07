package com.rem.progetto_embedded.Adapters;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rem.progetto_embedded.R;

/**
 * ViewHolder for SpeciesListAdapter
 */

public class SpeciesListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView textView;
    private ImageView imageView;
    private SpeciesListAdapter.OnSpeciesSelectedListener clickListener;
    SpeciesListItemViewHolder(CardView itemView, SpeciesListAdapter.OnSpeciesSelectedListener listener) {
        super(itemView);
        textView = itemView.findViewById(R.id.text_view);
        imageView = itemView.findViewById(R.id.image_view);
        //Gets the SpeciesListAdapter reference (in which is declared the interface OnSpeciesSelectedListener)
        clickListener = listener;
        //Sets click listener for the card, which will be called when a click is detected
        itemView.setOnClickListener(this);
    }
    void setImage(Bitmap image){
        imageView.setImageBitmap(image);
    }
    void setPlaceholderImage(){
        imageView.setImageResource(R.drawable.ic_placeholder_icon_vector);
    }
    void setName(String name){
        textView.setText(name);
    }
    //Calls method onSpeciesListItemClick implemented in SpeciesListFragment
    @Override
    public void onClick(View v) {
        clickListener.onSpeciesListItemClick(getAdapterPosition());
    }
}
