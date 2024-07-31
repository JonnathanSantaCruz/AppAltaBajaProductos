package com.example.scannerdecodigodebarras;

public class Tecnico {

    private String nombre, apellido, numero, dni, empresa, cajas;


    public Tecnico() {

    }

    public Tecnico(String nombre, String apellido, String dni, String numero, String empresa, String cajas) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.numero = numero;
        this.empresa = empresa;
        this.cajas = cajas;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDni() {
        return dni;
    }

    public String getNumero() {
        return numero;
    }

    public String getCajas() {
        return cajas;
    }

    public void setCajas(String cajas) {
        this.cajas = cajas;
    }

    public String getEmpresa() {
        return empresa;
    }
}