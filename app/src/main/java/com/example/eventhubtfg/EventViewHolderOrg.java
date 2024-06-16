package com.example.eventhubtfg;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EventViewHolderOrg extends RecyclerView.ViewHolder {
    ImageView eventImage;
    TextView eventName;
    TextView eventDescription;
    ImageButton deleteButton;

    public EventViewHolderOrg(@NonNull View itemView) {
        super(itemView);
        eventImage = itemView.findViewById(R.id.event_image);
        eventName = itemView.findViewById(R.id.event_name);
        eventDescription = itemView.findViewById(R.id.event_description);
        deleteButton = itemView.findViewById(R.id.delete_button);
    }
}
