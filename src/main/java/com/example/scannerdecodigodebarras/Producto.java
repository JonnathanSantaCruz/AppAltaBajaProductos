package com.example.scannerdecodigodebarras;

public class Producto {

    private String nombre, codigo, filial;

    public Producto(){

    }

    public Producto(String nombre, String codigo, String filial) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.filial = filial;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getFilial() {
        return filial;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setFilial(String filial) {
        this.filial = filial;
    }
}
