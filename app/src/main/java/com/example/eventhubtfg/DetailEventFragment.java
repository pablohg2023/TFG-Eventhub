package com.example.eventhubtfg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

public class DetailEventFragment extends Fragment {

    private ImageButton btnCerrar;
    private ImageView eventImageView;
    private TextView eventNameTextView;
    private TextView eventDescriptionTextView;
    private TextView eventPlaceTextView;
    private TextView eventDateTextView;
    private TextView eventTimeTextView;

    private TextView eventPriceTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_event, container, false);

        // Enlazar los TextViews y la ImageView del layout
        eventImageView = view.findViewById(R.id.event_image);
        eventNameTextView = view.findViewById(R.id.event_name);
        eventDescriptionTextView = view.findViewById(R.id.event_description);
        eventPlaceTextView = view.findViewById(R.id.event_place);
        eventDateTextView = view.findViewById(R.id.event_date);
        eventTimeTextView = view.findViewById(R.id.event_time);
        eventPriceTextView = view.findViewById(R.id.event_price);
        btnCerrar = view.findViewById(R.id.btnCerrar);

        // Obtener los datos del evento de los argumentos del fragmento
        Bundle bundle = getArguments();
        if (bundle != null) {
            Evento evento = (Evento) bundle.getSerializable("evento");
            if (evento != null) {
                eventNameTextView.setText(evento.getNombre());
                eventDescriptionTextView.setText(evento.getDescripcion());
                eventPlaceTextView.setText("Lugar: " + evento.getLugar());
                eventDateTextView.setText("Fecha: " + evento.getFecha());
                eventTimeTextView.setText("Hora: " + evento.getHora());
                eventPriceTextView.setText("Precio: " + Double.toString(evento.getPrecio()) + " €");

                // Cargar la imagen del evento si hay una URL válida
                if (evento.getImagenUrl() != null && !evento.getImagenUrl().isEmpty()) {
                    Picasso.get().load(evento.getImagenUrl()).into(eventImageView);
                }
            }
        }

        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().remove(DetailEventFragment.this).commit();
            }
        });

        return view;
    }

}

