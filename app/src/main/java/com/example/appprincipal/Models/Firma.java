package com.example.appprincipal.Models;

public class Firma {

    private int id;
    private String imagen;
    private String description;

    //Constructor Vacio
    public Firma(){

    }

    public Firma(int id, String imagen, String description) {
        this.id = id;
        this.imagen = imagen;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
