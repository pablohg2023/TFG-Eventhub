package com.example.eventhubtfg.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhubtfg.CardCalendarAdapter;
import com.example.eventhubtfg.R;
import com.example.eventhubtfg.databinding.FragmentCalendarBinding;


public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;
    private CalendarView calendarView;
    private CalendarViewModel calendarViewModel;
    private CardCalendarAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerCalendar;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        calendarView = root.findViewById(R.id.calendarView);

        adapter = new CardCalendarAdapter(getContext(), calendarViewModel.getEventosFavoritos().getValue(), calendarView);
        recyclerView.setAdapter(adapter);

        calendarViewModel.getEventosFavoritos().observe(getViewLifecycleOwner(), eventos -> {
            if (eventos != null) {
                adapter.actualizarDatabase(eventos);
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