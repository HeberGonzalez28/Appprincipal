package com.example.appprincipal.models;

public class Fotografia {

    private String imagen;
    private String description;

    //Constructor Vacio
    public Fotografia(){

    }

    public Fotografia(String imagen, String description) {
        this.imagen = imagen;
        this.description = description;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
