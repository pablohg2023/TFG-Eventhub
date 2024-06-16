package com.example.eventhubtfg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class CardEventAdapter extends RecyclerView.Adapter<EventViewHolder> {
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

        // Verificar si la URL de la imagen no está vacía
        if (evento.getImagenUrl() != null && !evento.getImagenUrl().isEmpty()) {
            // Usar Picasso para cargar la imagen desde la URL
            Picasso.get().load(evento.getImagenUrl()).into(holder.imagen);
        }

        holder.nombre.setText(evento.getNombre());
        holder.descripcion.setText(evento.getDescripcion());

        // Establecer el ícono del botón de favoritos como no favorito inicialmente
        holder.botonFavorito.setImageResource(R.drawable.ic_favorite_border);

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
                    boolean favorito = dataSnapshot.exists();

                    // Establecer el ícono del botón de favoritos según el estado del evento
                    holder.botonFavorito.setImageResource(favorito ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);

                    // Establecer el estado favorito del evento
                    evento.setFavorito(favorito);

                    // Establecer OnClickListener para el botón de favoritos
                    holder.botonFavorito.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Invertir el estado de favorito localmente
                            boolean cambioFavorito = !evento.getFavorito();
                            evento.setFavorito(cambioFavorito);

                            // Cambiar el ícono del botón
                            holder.botonFavorito.setImageResource(cambioFavorito ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);

                            // Actualizar la base de datos de favoritos del usuario
                            if (cambioFavorito) {
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
                    databaseError.getMessage();
                }
            });
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí puedes abrir un nuevo fragmento para mostrar los detalles del evento
                DetailEventFragment fragment = new DetailEventFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("evento", evento);
                fragment.setArguments(bundle);

                // Obtener el FragmentManager
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

                // Comenzar una nueva transacción de fragmento
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                // Configurar una animación personalizada para la transición
                transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down);

                // Reemplazar el fragmento actual con el fragmento de detalle del evento
                transaction.add(R.id.fragment_container, fragment); // Usar add() en lugar de replace()
                transaction.addToBackStack(null);  // Opcional, para permitir retroceder
                transaction.commit();
            }
        });
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

    public void updateData(ArrayList<Evento> nuevosEventos) {
        this.eventos = nuevosEventos;
        notifyDataSetChanged();
    }
}
