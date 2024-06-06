package com.example.eventhubtfg.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventhubtfg.Evento;
import com.example.eventhubtfg.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private CalendarViewModel calendarViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Inicializar ViewModel
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

        // Inicializar CalendarView
        calendarView = view.findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                Toast.makeText(getContext(), "Selected date: " + selectedDate, Toast.LENGTH_SHORT).show();
                // Aquí podrías filtrar los eventos para mostrar solo los de la fecha seleccionada
                calendarViewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
                    // Filtra los eventos por la fecha seleccionada y actualiza la UI
                    // Aquí podrías actualizar un RecyclerView con los eventos filtrados
                });
            }
        });

        // Observar los eventos y resaltar las fechas en el CalendarView
        calendarViewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
            for (Evento evento : events) {
                highlightEventDate(evento.getFecha());
            }
        });

        return view;
    }

    private void highlightEventDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(dateStr);
            if (date != null) {
                calendarView.setDate(date.getTime(), true, true);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}