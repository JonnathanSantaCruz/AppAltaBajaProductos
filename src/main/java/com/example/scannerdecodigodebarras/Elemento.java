package com.example.scannerdecodigodebarras;

public class Elemento {

    private String nombre, codigo, filial;
    private boolean seleccionado;

    public Elemento(){}

    public Elemento(String nombre, String codigo, String filial) {
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

    public boolean estaSeleccionado() {
        return seleccionado;
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

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }
}
