package com.detons97gmail.progetto_embedded;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class SpeciesListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private CardView card;
    private SpeciesListAdapter.onSpeciesSelectedListener clickListener;
    SpeciesListItemViewHolder(CardView itemView, SpeciesListAdapter.onSpeciesSelectedListener listener) {
        super(itemView);
        card = itemView;
        clickListener = listener;
        card.setOnClickListener(this);
    }
    void setImage(Drawable image){
        ImageView im = card.findViewById(R.id.image_view);
        im.setImageDrawable(image);z
    }
    void setName(String name){
        TextView tx = card.findViewById(R.id.text_view);
        tx.setText(name);
    }
    public CardView getItem(){
        return card;
    }

    @Override
    public void onClick(View v) {
        clickListener.onSpeciesListItemClick(getAdapterPosition());
    }
}
