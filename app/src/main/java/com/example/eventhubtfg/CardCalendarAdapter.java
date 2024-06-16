package com.example.eventhubtfg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.annotation.NonNull;
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

public class CardCalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
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
            Picasso.get().load(evento.getImagenUrl()).into(holder.imagen);
        }

        holder.nombre.setText(evento.getNombre());
        holder.botonFavorito.setImageResource(R.drawable.ic_favorite_border);

        int eventoId = evento.getId();
        String userId = obtenerIdUsuario();

        if (userId != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference favoritosRef = database.getReference("favoritos").child(userId).child(String.valueOf(eventoId));

            favoritosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean favorito = dataSnapshot.exists();
                    holder.botonFavorito.setImageResource(favorito ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
                    evento.setFavorito(favorito);

                    holder.botonFavorito.setOnClickListener(v -> {
                        boolean cambioFavorito = !evento.getFavorito();
                        evento.setFavorito(cambioFavorito);
                        holder.botonFavorito.setImageResource(cambioFavorito ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
                        if (cambioFavorito) {
                            favoritosRef.setValue(evento);
                        } else {
                            favoritosRef.removeValue();
                        }
                        notifyItemChanged(position);
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    databaseError.getMessage();
                }
            });
        }

        holder.itemView.setOnClickListener(v -> {
            if (evento.getFechaDate() != null) {
                long tiempo = evento.getFechaDate().getTime();
                calendarView.setDate(tiempo, true, true);

                String hora = evento.getHora();
                Toast.makeText(context, "Hora del evento: " + hora, Toast.LENGTH_SHORT).show();
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

    public void actualizarDatabase(ArrayList<Evento> nuevosEventos) {
        this.eventos = nuevosEventos;
        notifyDataSetChanged();
    }
}
