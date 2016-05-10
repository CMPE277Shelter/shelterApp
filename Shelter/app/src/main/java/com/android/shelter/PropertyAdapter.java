package com.android.shelter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Prasanna on 5/5/16.
 */
public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>{
    List<Property> properties;



    // Provide a suitable constructor (depends on the kind of dataset)
    public PropertyAdapter(List<Property> properties) {
        this.properties = properties;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return properties.size();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PropertyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.property_item_layout, viewGroup, false);
        PropertyViewHolder pvh = new PropertyViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PropertyViewHolder propertyViewHolder, int i) {
        propertyViewHolder.propertyName.setText(properties.get(i).getName());
        propertyViewHolder.propertyType.setText(properties.get(i).getType());
        propertyViewHolder.propertyPhoto.setImageResource(properties.get(i).getPhotoId());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class PropertyViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView propertyName;
        TextView propertyType;
        ImageView propertyPhoto;

        PropertyViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.property_card_view);
            propertyName = (TextView)itemView.findViewById(R.id.property_name);
            propertyType = (TextView)itemView.findViewById(R.id.property_type);
            propertyPhoto = (ImageView)itemView.findViewById(R.id.property_photo);
        }
    }
}
