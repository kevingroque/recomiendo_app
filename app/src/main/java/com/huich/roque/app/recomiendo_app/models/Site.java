package com.huich.roque.app.recomiendo_app.models;

import java.util.ArrayList;

public class Site extends SiteId{

    private String nombre;
    private String descripcion;
    private String direccion;
    private String distrito;
    private String telefono;
    private Float latitud;
    private Float longitud;
    private Float rating;
    private ArrayList<String> url_imagen;

    public Site() {
    }

    public Site(String nombre, String descripcion, String direccion, String distrito, String telefono, Float latitud, Float longitud, Float rating, ArrayList<String> url_imagen) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.distrito = distrito;
        this.telefono = telefono;
        this.latitud = latitud;
        this.longitud = longitud;
        this.rating = rating;
        this.url_imagen = url_imagen;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Float getLatitud() {
        return latitud;
    }

    public void setLatitud(Float latitud) {
        this.latitud = latitud;
    }

    public Float getLongitud() {
        return longitud;
    }

    public void setLongitud(Float longitud) {
        this.longitud = longitud;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public ArrayList<String> getUrl_imagen() {
        return url_imagen;
    }

    public void setUrl_imagen(ArrayList<String> url_imagen) {
        this.url_imagen = url_imagen;
    }
}
