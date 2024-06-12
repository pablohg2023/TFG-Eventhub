package com.example.eventhubtfg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CardCalendarAdapter extends RecyclerView.Adapter<CardCalendarAdapter.CalendarViewHolder> {
    private ArrayList<Evento> eventos;
    private Context context;
    private CalendarView calendarView;

    public CardCalendarAdapter(Context context, ArrayList<Evento> eventos, CalendarView calendarView) {
        this.context = context;
        this.eventos = (eventos != null) ? eventos : new ArrayList<>();
        this.calendarView = calendarView;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card_calendar_adapter, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Evento evento = eventos.get(position);

        if (evento.getImagenUrl() != null && !evento.getImagenUrl().isEmpty()) {
            Picasso.get().load(evento.getImagenUrl()).into(holder.eventImage);
        }

        holder.eventName.setText(evento.getNombre());

        holder.favoriteButton.setImageResource(R.drawable.ic_favorite_border);

        int eventoId = evento.getId();
        String userId = obtenerIdUsuario();

        if (userId != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference favoritosRef = database.getReference("favoritos").child(userId).child(String.valueOf(eventoId));

            favoritosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean esFavorito = dataSnapshot.exists();
                    holder.favoriteButton.setImageResource(esFavorito ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
                    evento.setFavorito(esFavorito);

                    holder.favoriteButton.setOnClickListener(v -> {
                        boolean nuevoEstadoFavorito = !evento.getFavorito();
                        evento.setFavorito(nuevoEstadoFavorito);
                        holder.favoriteButton.setImageResource(nuevoEstadoFavorito ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
                        if (nuevoEstadoFavorito) {
                            favoritosRef.setValue(evento);
                        } else {
                            favoritosRef.removeValue();
                        }
                        notifyItemChanged(position);
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar el error si es necesario
                }
            });
        } else {
            holder.favoriteButton.setOnClickListener(v -> {
                // Manejar la situación donde no hay usuario autenticado, si es necesario
            });
        }

        holder.itemView.setOnClickListener(v -> {
            if (evento.getFechaDate() != null) {
                long dateInMillis = evento.getFechaDate().getTime();
                calendarView.setDate(dateInMillis, true, true);

                String horaEvento = evento.getHora();
                Toast.makeText(context, "Hora del evento: " + horaEvento, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String obtenerIdUsuario() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null) {
            return usuario.getUid();
        } else {
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
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

    public void updateData(ArrayList<Evento> nuevosEventos) {
        this.eventos = nuevosEventos;
        notifyDataSetChanged();
    }
}
