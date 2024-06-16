package com.example.eventhubtfg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class CardEventAdapterOrg extends RecyclerView.Adapter<EventViewHolderOrg> {
    private ArrayList<Evento> eventos;
    private Context context;
    private OnEventDeleteListener deleteListener;

    public CardEventAdapterOrg(Context context, ArrayList<Evento> eventos, OnEventDeleteListener onEventDeleteListener) {
        this.context = context;
        this.eventos = (eventos != null) ? eventos : new ArrayList<>();
        this.deleteListener = onEventDeleteListener;
    }

    @NonNull
    @Override
    public EventViewHolderOrg onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card_event_adapter_org, parent, false);
        return new EventViewHolderOrg(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolderOrg holder, int position) {
        Evento evento = eventos.get(position);

        if (evento.getImagenUrl() != null && !evento.getImagenUrl().isEmpty()) {
            Picasso.get().load(evento.getImagenUrl()).into(holder.imagen);
        }

        holder.nombre.setText(evento.getNombre());
        holder.descripcion.setText(evento.getDescripcion());

        holder.botonEliminar.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onEventDelete(evento.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    public void updateData(ArrayList<Evento> nuevosEventos) {
        this.eventos = nuevosEventos;
        notifyDataSetChanged();
    }

    public interface OnEventDeleteListener {
        void onEventDelete(int eventId);
    }
}