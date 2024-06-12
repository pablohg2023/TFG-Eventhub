package com.example.eventhubtfg.ui.home;

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
import com.example.eventhubtfg.CardEventAdapterOrg;
import com.example.eventhubtfg.Evento;
import com.example.eventhubtfg.databinding.FragmentHomeBinding;
import com.example.eventhubtfg.databinding.FragmentHomeOrgBinding;

import java.util.ArrayList;

public class HomeOrgFragment extends Fragment {

    private FragmentHomeOrgBinding binding;
    private HomeOrgViewModel homeOrgViewModel;
    private CardEventAdapterOrg adapter;

    private ArrayList<Evento> listaEventos = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeOrgViewModel = new ViewModelProvider(this).get(HomeOrgViewModel.class);

        binding = FragmentHomeOrgBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recylcerId;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CardEventAdapterOrg(getContext(), homeOrgViewModel.getListaDatos().getValue());
        recyclerView.setAdapter(adapter);

        homeOrgViewModel.getListaDatos().observe(getViewLifecycleOwner(), eventos -> {
            if (eventos != null) {
                adapter.updateData(eventos);
                listaEventos.clear();
                listaEventos.addAll(eventos);
                adapter.updateData(listaEventos);
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