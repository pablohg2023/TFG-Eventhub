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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhubtfg.CardCalendarAdapter;
import com.example.eventhubtfg.CardEventAdapter;
import com.example.eventhubtfg.Evento;
import com.example.eventhubtfg.R;
import com.example.eventhubtfg.databinding.FragmentCalendarBinding;
import com.example.eventhubtfg.databinding.FragmentEventBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;
    private CalendarView calendarView;
    private CalendarViewModel calendarViewModel;
    private CardCalendarAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inicializar ViewModel
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerCalendar;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Inicializar CalendarView
        calendarView = root.findViewById(R.id.calendarView);

        adapter = new CardCalendarAdapter(getContext(), calendarViewModel.getFavoriteEvents().getValue());
        recyclerView.setAdapter(adapter);

        calendarViewModel.getFavoriteEvents().observe(getViewLifecycleOwner(), eventos -> {
            if (eventos != null) {
                adapter.updateData(eventos);
            }
        });

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}