package com.example.eventhubtfg;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EventViewHolderOrg extends RecyclerView.ViewHolder {
    ImageView imagen;
    TextView nombre;
    TextView descripcion;
    ImageButton botonEliminar;

    public EventViewHolderOrg(@NonNull View itemView) {
        super(itemView);
        imagen = itemView.findViewById(R.id.event_image);
        nombre = itemView.findViewById(R.id.event_name);
        descripcion = itemView.findViewById(R.id.event_description);
        botonEliminar = itemView.findViewById(R.id.delete_button);
    }
}
