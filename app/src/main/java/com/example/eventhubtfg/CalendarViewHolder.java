package com.example.eventhubtfg;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarViewHolder extends RecyclerView.ViewHolder {
    ImageView eventImage;
    TextView eventName;
    ImageButton favoriteButton;

    public CalendarViewHolder(@NonNull View itemView) {
        super(itemView);
        eventImage = itemView.findViewById(R.id.event_image);
        eventName = itemView.findViewById(R.id.event_name);
        favoriteButton = itemView.findViewById(R.id.favorite_button);
    }
}

