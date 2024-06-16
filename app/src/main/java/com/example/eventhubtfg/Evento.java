package com.example.eventhubtfg;

import java.io.Serializable;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Evento implements Serializable {

    private int id;
    private String imagenUrl;
    private String nombre;
    private String descripcion;
    private String lugar;
    private String fecha;
    private String hora;
    private double precio;
    private Boolean favorito;
    private Date fechaDate; // Fecha como tipo Date

    public Evento() {
    }

    public Evento(Integer id, String imagenUrl, String nombre, String descripcion, String lugar, String fecha, String hora, double precio, Boolean favorito) {
        this.id = id;
        this.imagenUrl = imagenUrl;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.fecha = fecha;
        this.hora = hora;
        this.favorito = favorito;
        this.precio = precio;
        this.fechaDate = convertStringToDate(fecha);
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Boolean getFavorito() {
        return favorito;
    }

    public void setFavorito(Boolean favorito) {
        this.favorito = favorito;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
        this.fechaDate = convertStringToDate(fecha); // Convertir la fecha String a tipo Date
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Date getFechaDate() {
        return fechaDate;
    }

    // Método para convertir la fecha String a tipo Date
    public Date convertStringToDate(String fechaString) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.parse(fechaString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
