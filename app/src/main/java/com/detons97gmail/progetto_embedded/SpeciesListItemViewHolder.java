package com.detons97gmail.progetto_embedded;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ViewHolder for SpeciesListAdapter
 */

public class SpeciesListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private CardView card;
    private SpeciesListAdapter.OnSpeciesSelectedListener clickListener;
    SpeciesListItemViewHolder(CardView itemView, SpeciesListAdapter.OnSpeciesSelectedListener listener) {
        super(itemView);
        card = itemView;
        //Gets the SpeciesListAdapter reference (in which is declared the interface OnSpeciesSelectedListener)
        clickListener = listener;
        //Sets click listener for the card, which will be called when a click is detected
        card.setOnClickListener(this);
    }
    void setImage(Drawable image){
        ImageView im = card.findViewById(R.id.image_view);
        im.setImageDrawable(image);
    }
    void setName(String name){
        TextView tx = card.findViewById(R.id.text_view);
        tx.setText(name);
    }
    @Override
    public void onClick(View v) {
        clickListener.onSpeciesListItemClick(getAdapterPosition());
    }
}
