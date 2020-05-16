package com.detons97gmail.progetto_embedded.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;


import com.detons97gmail.progetto_embedded.Utilities.AnimalDetails;
import com.detons97gmail.progetto_embedded.R;


public class AnimalDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private AnimalDetails animalInfo;
    private final LayoutInflater inflater;
    private static int TYPE_IMAGE=1;
    private static int TYPE_TEXT=2;

    public AnimalDetailsAdapter(Context context, AnimalDetails ai)
    {
        this.animalInfo=ai;
        inflater=LayoutInflater.from(context);
    }

    public class DetailsImageViewHolder extends RecyclerView.ViewHolder
    {
        final AnimalDetailsAdapter adapter;
        final ImageView detailsImage;

        DetailsImageViewHolder(View itemView, AnimalDetailsAdapter ad)
        {
            super(itemView);
            //Get the layout
            detailsImage=itemView.findViewById(R.id.detailsImage);
            //Associate with adapter
            this.adapter=ad;
        }
    }

    public class DetailsContentViewHolder extends RecyclerView.ViewHolder
    {
        final AnimalDetailsAdapter adapter;
        final TextView attributeName;
        final TextView attributeContent;

        DetailsContentViewHolder(View itemView, AnimalDetailsAdapter ad)
        {
            super(itemView);
            //Get the layout
            attributeName=itemView.findViewById(R.id.attributeName);
            attributeContent=itemView.findViewById(R.id.attributeContent);
            //Associate with adapter
            this.adapter=ad;
        }

    }

    @Override
    public int getItemViewType(int position)
    {
        //The first element is always a photo
        if(position==0)
            return TYPE_IMAGE;
        return TYPE_TEXT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //Create view from Layout
        View view;
        if(viewType==TYPE_IMAGE)
        {
            //Create a imageLayout
            view=inflater.inflate(R.layout.image_details_animal_layout, parent, false);
            return new DetailsImageViewHolder(view, this);
        }
        //Create a text Layout
        view=inflater.inflate(R.layout.details_animal_layout, parent, false);
        return new DetailsContentViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if(getItemViewType(position)==TYPE_IMAGE)
        {
            //Set image
            ((DetailsImageViewHolder)holder).detailsImage.setImageResource(animalInfo.getImageRef());
        }
        else
        {
            //Set text
            ((DetailsContentViewHolder)holder).attributeName.setText(animalInfo.getAttributeName(position));
            ((DetailsContentViewHolder)holder).attributeContent.setText(animalInfo.getAttributeContent(position));
        }
    }

    @Override
    public int getItemCount()
    {
        return animalInfo.size();
    }
}
