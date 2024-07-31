package com.example.scannerdecodigodebarras;

public class Usuario {
    private String email;
    private String foto;
    private String contraseña;

    private boolean admin;

    public Usuario(String email, String contraseña, boolean admin) {
        this.email = email;
        this.contraseña = contraseña;
        this.admin = admin;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getFoto() {
        return foto;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}