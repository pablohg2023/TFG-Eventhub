package com.example.eventhubtfg;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarViewHolder extends RecyclerView.ViewHolder {
    ImageView imagen;
    TextView nombre;
    ImageButton botonFavorito;

    public CalendarViewHolder(@NonNull View itemView) {
        super(itemView);
        imagen = itemView.findViewById(R.id.event_image);
        nombre = itemView.findViewById(R.id.event_name);
        botonFavorito = itemView.findViewById(R.id.favorite_button);
    }
}

