package com.example.eventhubtfg;

import android.annotation.SuppressLint;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    public void onBindViewHolder(@NonNull EventViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Evento evento = eventos.get(position);
        Glide.with(context).load(evento.getImagenUrl()).into(holder.eventImage);
        holder.eventName.setText(evento.getNombre());
        holder.eventDescription.setText(evento.getDescripcion());

        // Establecer el ícono del botón de favoritos como no favorito inicialmente
        holder.favoriteButton.setImageResource(R.drawable.ic_favorite_border);

        // Obtener el ID del evento
        int eventoId = evento.getId();

        // Obtener el ID del usuario actual
        String userId = obtenerIdUsuario();

        if (userId != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference favoritosRef = database.getReference("favoritos").child(userId).child(String.valueOf(eventoId));

            // Comprobar si el evento está en la tabla de favoritos
            favoritosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean esFavorito = dataSnapshot.exists();

                    // Establecer el ícono del botón de favoritos según el estado del evento
                    holder.favoriteButton.setImageResource(esFavorito ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);

                    // Establecer el estado favorito del evento
                    evento.setFavorito(esFavorito);

                    // Establecer OnClickListener para el botón de favoritos
                    holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Invertir el estado de favorito localmente
                            boolean nuevoEstadoFavorito = !evento.getFavorito();
                            evento.setFavorito(nuevoEstadoFavorito);

                            // Cambiar el ícono del botón
                            holder.favoriteButton.setImageResource(nuevoEstadoFavorito ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);

                            // Actualizar la base de datos de favoritos del usuario
                            if (nuevoEstadoFavorito) {
                                // Agregar a favoritos
                                favoritosRef.setValue(evento);
                            } else {
                                // Eliminar de favoritos
                                favoritosRef.removeValue();
                            }

                            // Notificar que el ítem ha cambiado
                            notifyItemChanged(position);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar el error si es necesario
                }
            });
        } else {
            // Establecer OnClickListener para el botón de favoritos cuando no hay usuario autenticado
            holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Manejar la situación donde no hay usuario autenticado, si es necesario
                }
            });
        }
    }

    // Método para obtener el ID del usuario actual
    private String obtenerIdUsuario() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null) {
            return usuario.getUid();
        } else {
            // Si el usuario es nulo, significa que no hay ningún usuario autenticado
            // Aquí puedes manejar esta situación según las necesidades de tu aplicación
            return null;
        }
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
