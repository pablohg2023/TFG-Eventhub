package com.example.eventhubtfg.ui.events;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventhubtfg.Evento;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Evento>> eventosFavoritos;

    public EventViewModel() {
        eventosFavoritos = new MutableLiveData<>();
        cargarEventosFavoritos();
    }

    public LiveData<ArrayList<Evento>> getEventosFavoritos() {
        return eventosFavoritos;
    }

    private void cargarEventosFavoritos() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            FirebaseDatabase.getInstance().getReference("favoritos").child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<Evento> eventos = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Evento evento = dataSnapshot.getValue(Evento.class);
                        if (evento != null) {
                            eventos.add(evento);
                        }
                    }
                    eventosFavoritos.setValue(eventos);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    error.getMessage();
                }
            });
        }
    }
}
