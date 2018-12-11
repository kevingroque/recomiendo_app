package com.huich.roque.app.recomiendo_app.models;

public class Category extends CategoryId{

    private String nombre;
    private String descripcion;
    private String url_imagen;
    private boolean selected;

    public Category() {
    }

    public Category(String nombre, String descripcion, String url_imagen, boolean selected) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.url_imagen = url_imagen;
        this.selected = selected;
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

    public String getUrl_imagen() {
        return url_imagen;
    }

    public void setUrl_imagen(String url_imagen) {
        this.url_imagen = url_imagen;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
