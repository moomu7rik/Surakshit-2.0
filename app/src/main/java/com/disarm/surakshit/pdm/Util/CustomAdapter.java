package com.disarm.surakshit.pdm.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.disarm.surakshit.pdm.R;

class CustomAdapter extends ArrayAdapter<String>{
    public CustomAdapter(Context context, String[] files) {
        super(context, R.layout.custom_row ,files);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // default -  return super.getView(position, convertView, parent);
        // add the layout
        LayoutInflater myCustomInflater = LayoutInflater.from(getContext());
        View customView = myCustomInflater.inflate(R.layout.custom_row, parent, false);
        // get references.
        String singleFoodItem = getItem(position);
        TextView itemText = (TextView) customView.findViewById(R.id.item_text);
        ImageView buckysImage = (ImageView) customView.findViewById(R.id.my_profile_image);
        String[] parts = singleFoodItem.split("_");

        // dynamically update the text from the array
        itemText.setText(parts[1]);
        // using the same image every time
     //   buckysImage.setImageResource(R.drawable.ic_image_accent);
        // Now we can finally return our custom View or custom item
        if (singleFoodItem.contains("jpeg")) {
            buckysImage.setImageResource(R.drawable.image);
        } else if (singleFoodItem.contains("mp4")) {
            buckysImage.setImageResource(R.drawable.video);
        } else if (singleFoodItem.contains("3gp")) {
            buckysImage.setImageResource(R.drawable.audio);
        }
        return customView;
    }
}
