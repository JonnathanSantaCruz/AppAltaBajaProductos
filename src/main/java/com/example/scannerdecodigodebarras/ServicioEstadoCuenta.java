package com.example.scannerdecodigodebarras;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ServicioEstadoCuenta extends Service {

    private FirebaseFirestore firebaseFirestore;
    private static String gmail;
    private static String horaInicioRetiro, fechaRetiro;
    private static boolean terminoRetiro, retiroIniciado;

    @Override
    public void onCreate(){
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int idProceso){
        return START_STICKY;
    }

    @Override
    public boolean stopService(Intent name) {
        //ME ACTUALIZA EL ESTADO DE LA CUENTA
        actualizarCuenta();
        // ME BORRA LOS ELEMENTOS, LAS CAJAS Y EL TECNICO SI SE CIERRA LA APP
        // SIN HABER TERMINADO EL RETIRO
        borrarCarga();
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //ME ACTUALIZA EL ESTADO DE LA CUENTA
        actualizarCuenta();
        // ME BORRA LOS ELEMENTOS, LAS CAJAS Y EL TECNICO SI SE CIERRA LA APP
        // SIN HABER TERMINADO EL RETIRO
        borrarCarga();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void borrarCarga(){
        if(!terminoRetiro && retiroIniciado){
            //ACA BORRO TODOS LOS ELEMENTOS CARGADOS EN LA HORA EN LA QUE SE INICIO EL RETIRO
            firebaseFirestore.collection(gmail).document(fechaRetiro)
                    .collection("cajas").document(horaInicioRetiro).collection("elementos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                if(documentSnapshot.exists()){
                                    firebaseFirestore.collection(gmail).document(fechaRetiro)
                                            .collection("cajas").document(horaInicioRetiro).collection("elementos")
                                            .document(documentSnapshot.getId()).delete();
                                }
                            }
                        }
                    });


            //ACA BORRO LOS DATOS CARGADOS DEL TECNICO EN LA HORA EN LA QUE SE INICIO EL RETIRO
            firebaseFirestore.collection(gmail).document(fechaRetiro)
                    .collection("cajas").document(horaInicioRetiro).collection("tecnico").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                if(documentSnapshot.exists()){
                                    firebaseFirestore.collection(gmail).document(fechaRetiro)
                                            .collection("cajas").document(horaInicioRetiro).collection("tecnico")
                                            .document(documentSnapshot.getId()).delete();
                                }
                            }
                        }
                    });


            //ACA BORRO LAS CAJAS DEL DOCUMENTO CAJAS RETIRADAS
            firebaseFirestore.collection(gmail).document("cajas retiradas")
                    .collection("cajas").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                //BUSCA POR HORARIO, SI HAY UNA CAJA QUE TENGA IGUAL HORARIO AL DE NUESTRO RETIRO INGRESA AL IF
                                if(Objects.equals(documentSnapshot.get("hora inicio retiro"), horaInicioRetiro)){

                                    //ACA PROCEDO A ELIMINAR LOS CAMPOS DE LA CAJA
                                    firebaseFirestore.collection(gmail)
                                            .document("cajas retiradas")
                                            .collection("cajas")
                                            .document(documentSnapshot.getId()).delete();

                                    //ACA PROCEDO A ELIMINAR LOS ELEMENTOS CARGADOS EN LA CAJA
                                    firebaseFirestore.collection(gmail)
                                            .document("cajas retiradas")
                                            .collection("cajas")
                                            .document(documentSnapshot.getId()).collection(horaInicioRetiro).get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    for (DocumentSnapshot document: queryDocumentSnapshots){
                                                        firebaseFirestore.collection(gmail)
                                                                .document("cajas retiradas")
                                                                .collection("cajas")
                                                                .document(documentSnapshot.getId()).collection("elementos").
                                                                document(document.getId()).delete();
                                                    }
                                                }
                                            });

                                    //ACA BORRO LAS CAJAS QUE SE CARGARON EN EL HORARIO DEL RETIRO
                                    firebaseFirestore.collection(gmail).document(fechaRetiro)
                                            .collection("cajas").document(horaInicioRetiro).delete();
                                    firebaseFirestore.collection(gmail).document(fechaRetiro)
                                            .collection("cajas").document(horaInicioRetiro).
                                            collection(documentSnapshot.getId()).get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    for (DocumentSnapshot document : queryDocumentSnapshots){
                                                        firebaseFirestore.collection(gmail).document(fechaRetiro)
                                                                .collection("cajas").document(horaInicioRetiro).
                                                                collection(documentSnapshot.getId()).
                                                                document(document.getId()).delete();
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });
        }
    }
    private void actualizarCuenta() {
        SharedPreferences preferences = getSharedPreferences("datosServicio", Context.MODE_PRIVATE);
        gmail = preferences.getString("gmail", "");
        horaInicioRetiro = preferences.getString("horaInicioRetiro", "");
        fechaRetiro = preferences.getString("fechaRetiro","");
        terminoRetiro = preferences.getBoolean("terminoRetiro",false);
        retiroIniciado = preferences.getBoolean("retiroIniciado", false);

        Map<String, Object> dato = new HashMap<>();
        dato.put("inicio sesion", "false");

        firebaseFirestore.collection(gmail).document("usando cuenta").update(dato);
    }
}