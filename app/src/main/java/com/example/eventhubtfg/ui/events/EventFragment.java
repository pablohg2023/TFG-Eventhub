package com.example.eventhubtfg.ui.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        binding = FragmentEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recylcerId;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CardEventAdapter(getContext(), eventViewModel.getFavoriteEvents().getValue());
        recyclerView.setAdapter(adapter);

        eventViewModel.getFavoriteEvents().observe(getViewLifecycleOwner(), eventos -> {
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