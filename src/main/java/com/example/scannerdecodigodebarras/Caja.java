package com.example.scannerdecodigodebarras;

import com.firebase.ui.firestore.ObservableSnapshotArray;

import java.util.ArrayList;

public class Caja {

    private String id;
    private ArrayList<Elemento> elementos;

    public Caja (String id, ArrayList<Elemento> elementos){
        this.id = id;
        this.elementos = elementos;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Elemento> getElementos() {
        return elementos;
    }

}
