package com.example.eventhubtfg;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.eventhubtfg.Evento;
import com.example.eventhubtfg.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventDetailFragment extends Fragment {
    private static final String ARG_EVENT_ID = "eventId";

    private ImageView eventImage;
    private TextView eventName;
    private TextView eventDescription;
    private TextView eventLocation;
    private TextView eventDate;
    private TextView eventTime;

    private DatabaseReference databaseReference;

    public static EventDetailFragment newInstance(int eventId) {
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);

        eventImage = view.findViewById(R.id.event_image_detail);
        eventName = view.findViewById(R.id.event_name_detail);
        eventDescription = view.findViewById(R.id.event_description_detail);
        eventLocation = view.findViewById(R.id.event_location_detail);
        eventDate = view.findViewById(R.id.event_date_detail);
        eventTime = view.findViewById(R.id.event_time_detail);

        if (getArguments() != null) {
            int eventId = getArguments().getInt(ARG_EVENT_ID);
            loadEventDetails(eventId);
        }

        return view;
    }

    private void loadEventDetails(int eventId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("eventos").child(String.valueOf(eventId));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Evento event = dataSnapshot.getValue(Evento.class);
                if (event != null) {
                    Glide.with(getContext()).load(event.getImagenUrl()).into(eventImage);
                    eventName.setText(event.getNombre());
                    eventDescription.setText(event.getDescripcion());
                    eventLocation.setText(event.getLugar());
                    eventDate.setText(event.getFecha());
                    eventTime.setText(event.getHora());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar error
            }
        });
    }
}