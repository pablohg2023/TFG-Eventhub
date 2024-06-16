package com.example.eventhubtfg.ui.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhubtfg.CardEventAdapter;
import com.example.eventhubtfg.databinding.FragmentEventBinding;

public class EventFragment extends Fragment {

    private FragmentEventBinding binding;
    private EventViewModel eventViewModel;
    private CardEventAdapter adapter;
    private TextView txtFavoritos;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        binding = FragmentEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recylcerId;
        txtFavoritos = binding.txtFavoritos;

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CardEventAdapter(getContext(), eventViewModel.getEventosFavoritos().getValue());
        recyclerView.setAdapter(adapter);

        eventViewModel.getEventosFavoritos().observe(getViewLifecycleOwner(), eventos -> {
            if (eventos != null && !eventos.isEmpty()) {
                adapter.updateData(eventos);
                recyclerView.setVisibility(View.VISIBLE);
                txtFavoritos.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                txtFavoritos.setVisibility(View.VISIBLE);
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