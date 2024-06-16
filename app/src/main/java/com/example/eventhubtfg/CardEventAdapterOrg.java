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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CardEventAdapterOrg extends RecyclerView.Adapter<CardEventAdapterOrg.EventViewHolder> {
    private ArrayList<Evento> eventos;
    private Context context;
    private OnEventDeleteListener onEventDeleteListener;

    public CardEventAdapterOrg(Context context, ArrayList<Evento> eventos, OnEventDeleteListener onEventDeleteListener) {
        this.context = context;
        this.eventos = (eventos != null) ? eventos : new ArrayList<>();
        this.onEventDeleteListener = onEventDeleteListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card_event_adapter_org, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Evento evento = eventos.get(position);

        if (evento.getImagenUrl() != null && !evento.getImagenUrl().isEmpty()) {
            Picasso.get().load(evento.getImagenUrl()).into(holder.eventImage);
        }

        holder.eventName.setText(evento.getNombre());
        holder.eventDescription.setText(evento.getDescripcion());

        holder.deleteButton.setOnClickListener(v -> {
            if (onEventDeleteListener != null) {
                onEventDeleteListener.onEventDelete(evento.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView eventName;
        TextView eventDescription;
        ImageButton deleteButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.event_image);
            eventName = itemView.findViewById(R.id.event_name);
            eventDescription = itemView.findViewById(R.id.event_description);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    public void updateData(ArrayList<Evento> nuevosEventos) {
        this.eventos = nuevosEventos;
        notifyDataSetChanged();
    }

    public interface OnEventDeleteListener {
        void onEventDelete(int eventId);
    }
}
