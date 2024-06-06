package com.example.eventhubtfg.ui.calendar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventhubtfg.Evento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CalendarViewModel extends ViewModel {
    private final MutableLiveData<List<Evento>> events = new MutableLiveData<>();
    private final DatabaseReference databaseReference;

    public CalendarViewModel() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("eventos");
        loadEvents();
    }

    public LiveData<List<Evento>> getEvents() {
        return events;
    }

    private void loadEvents() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Evento> eventList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Evento event = snapshot.getValue(Evento.class);
                    eventList.add(event);
                }
                events.setValue(eventList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar error
            }
        });
    }
}