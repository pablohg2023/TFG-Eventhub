package com.example.eventhubtfg;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EventViewHolder extends RecyclerView.ViewHolder {
    ImageView eventImage;
    TextView eventName;
    TextView eventDescription;
    ImageButton favoriteButton;

    public EventViewHolder(@NonNull View itemView) {
        super(itemView);
        eventImage = itemView.findViewById(R.id.event_image);
        eventName = itemView.findViewById(R.id.event_name);
        eventDescription = itemView.findViewById(R.id.event_description);
        favoriteButton = itemView.findViewById(R.id.favorite_button);
    }
}
