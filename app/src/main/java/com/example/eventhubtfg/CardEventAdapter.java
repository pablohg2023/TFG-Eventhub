package com.example.eventhubtfg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CardEventAdapter extends RecyclerView.Adapter<CardEventAdapter.EventViewHolder> {
    private ArrayList<Evento> eventos;
    private Context context;

    public CardEventAdapter(Context context, ArrayList<Evento> eventos) {
        this.context = context;
        this.eventos = (eventos != null) ? eventos : new ArrayList<>();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card_event_adapter, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Evento evento = eventos.get(position);
        Glide.with(context).load(evento.getImagenUrl()).into(holder.eventImage);
        holder.eventName.setText(evento.getNombre());
        holder.eventDescription.setText(evento.getDescripcion());
        // Aquí puedes agregar más lógica, como manejar el botón de favorito
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
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

    public void updateData(ArrayList<Evento> nuevosEventos) {
        this.eventos = nuevosEventos;
        notifyDataSetChanged();
    }
}

