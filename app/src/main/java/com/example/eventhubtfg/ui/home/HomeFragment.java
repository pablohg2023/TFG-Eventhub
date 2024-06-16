package com.example.eventhubtfg.ui.home;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhubtfg.CardEventAdapter;
import com.example.eventhubtfg.Evento;
import com.example.eventhubtfg.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private CardEventAdapter adapter;
    private ArrayList<Evento> listaEventos = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recylcerId;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CardEventAdapter(getContext(), homeViewModel.getListaDatos().getValue());
        recyclerView.setAdapter(adapter);

        homeViewModel.getListaDatos().observe(getViewLifecycleOwner(), eventos -> {
            if (eventos != null) {
                adapter.updateData(eventos);
                listaEventos.clear();
                listaEventos.addAll(eventos);
                adapter.updateData(listaEventos);

            }
        });

        SearchView searchView = binding.searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtrarEventos(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarEventos(newText);
                return true;
            }
        });

        return root;
    }

    private void filtrarEventos(String texto) {
        ArrayList<Evento> eventosFiltrados = new ArrayList<>();
        if (listaEventos != null && !TextUtils.isEmpty(texto)) {
            for (Evento evento : listaEventos) {
                if (evento.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                    eventosFiltrados.add(evento);
                }
            }
        } else if (listaEventos != null) {
            eventosFiltrados.addAll(listaEventos);
        }
        adapter.updateData(eventosFiltrados);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}