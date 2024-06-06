package com.example.eventhubtfg.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventhubtfg.Evento;
import com.example.eventhubtfg.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<Evento>> listaDatos;

    public HomeViewModel() {
        listaDatos = new MutableLiveData<>();
        cargarEventosDesdeFirebase();
    }

    public LiveData<ArrayList<Evento>> getListaDatos() {
        return listaDatos;
    }

    private void cargarEventosDesdeFirebase() {
        FirebaseDatabase.getInstance().getReference("eventos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Evento> eventos = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Evento evento = dataSnapshot.getValue(Evento.class);
                    if (evento != null) {
                        eventos.add(evento);
                    }
                }
                listaDatos.setValue(eventos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar el error aquí
            }
        });
    }
}
