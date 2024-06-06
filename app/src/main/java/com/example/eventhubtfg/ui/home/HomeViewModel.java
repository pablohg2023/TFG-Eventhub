package com.example.eventhubtfg.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventhubtfg.Evento;
import com.example.eventhubtfg.R;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<Evento>> listaDatos;

    public HomeViewModel() {
        listaDatos = new MutableLiveData<>();
        llenarEventos();
    }

    public LiveData<ArrayList<Evento>> getListaDatos() {
        return listaDatos;
    }

    private void llenarEventos() {
        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(new Evento(R.drawable.concierto, "Concierto en Vivo: Artista XYZ", "Concierto en vivo del reconocido artista XYZ. Un espectáculo lleno de energía y emoción", "Teatro Municipal", "20-07-2024", "20:00"));
        eventos.add(new Evento(R.drawable.arte, "Exposición de Arte Moderno", "Obras de artistas contemporáneos en esta emocionante exposición de arte moderno.", "Galería de Arte Contemporáneo", "25-03-2024", "10:00"));
        eventos.add(new Evento(R.drawable.gastronomia, "Festival de Comida Internacional", "Amplia variedad de delicias culinarias de todo el mundo en este festival gastronómico", "Plaza Principal", "02-04-2024", "18:00"));
        eventos.add(new Evento(R.drawable.teatro, "Presentación de Teatro Clásico: 'Romeo y Julieta'", "Revive la historia de amor más famosa de todos los tiempos con esta emocionante presentación", "Teatro Nacional", "10-04-2024", "19:30"));
        eventos.add(new Evento(R.drawable.libros, "Feria de Libros y Lectura", "Amplia selección de libros de diferentes géneros y participa en sesiones de lectura y charlas con autores destacados.", "Parque Retiro", "15-04-2024", "11:00"));
        eventos.add(new Evento(R.drawable.magia, "Espectáculo de Magia: Ilusionista Max", "Déjate sorprender por los trucos y la magia del ilusionista Max en este espectáculo lleno de misterio y diversión.", "Auditorio Municipal", "15-06-2024", "20:00"));
        listaDatos.setValue(eventos);
    }
}
