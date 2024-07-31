package com.example.scannerdecodigodebarras;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class General extends AppCompatActivity {

    //ESTA VA A SER LA CLASE PADRE DE TODAS LAS CLASES CON PANTALLA
    //Y LO QUE VA A SER PRINCIPALMENTE ES VER SI LA APLICACION ESTA EN SEGUNDO PLANO
    //Y CUANDO LO ESTE PONER QUE LA CUENTA NO ESTA EN USO


    //ORDEN DE LOS ARRAYS
    //EscanearCodigo - IngresarElemento - IngresarTecnico - MostrarElementos - OrdenarCaja - MostrarCaja
    protected static boolean[] pantallas = new boolean[Global.cantPantallas];

    //ME DICE LA CANTIDAD DE PANTALLAS CREADAS
    protected static int cantPantallasCreadas;

    protected void actualizarEstadoCuenta() {
        if(Global.usuario.getAdmin()){
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            Map<String, Object> dato = new HashMap<>();

            int i = 0, j=0;
            do {
                if(pantallas[i]){
                    j++;
                }
                i++;
            }while (Global.cantPantallas > i);
            if(cantPantallasCreadas == j){
                dato.put("inicio sesion", "false");
                Global.cuentaUsada=false;
            }else{
                dato.put("inicio sesion", "true");
                Global.cuentaUsada=true;
            }
            firebaseFirestore.collection(Global.usuario.getEmail()).document("usando cuenta").update(dato);
        }else{
            if (!Global.terminoRetiro) {
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                Map<String, Object> dato = new HashMap<>();

                int i = 0, j=0;
                do {
                    if(pantallas[i]){
                        j++;
                    }
                    i++;
                }while (Global.cantPantallas > i);
                if(cantPantallasCreadas == j){
                    dato.put("inicio sesion", "false");
                    Global.cuentaUsada=false;
                }else{
                    dato.put("inicio sesion", "true");
                    Global.cuentaUsada=true;
                }
                firebaseFirestore.collection(Global.usuario.getEmail()).document("usando cuenta").update(dato);
            }
        }
    }
}