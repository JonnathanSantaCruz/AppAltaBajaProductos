package com.example.scannerdecodigodebarras;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class OrdenarCajas extends General {

    private RecyclerView datosElementos;
    private AdaptadorOrdenarCajas adaptador;
    private FirebaseFirestore firebaseFirestore;
    private Button visualizarCaja;
    private TextView nroDeCaja;
    private NavigationView navigationView;

    //ESTO ES PARA EL BOTON DEL TOOLBAR PARA ABRIR EL NAVIGATION VIEW
    private DrawerLayout drawerLayout;
    private MaterialToolbar toolbar;

    public OrdenarCajas(){
        if(!Global.pantallasCreadas[5]){
            Global.pantallasCreadas[5]=true;
            cantPantallasCreadas++;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordenar_cajas);

        firebaseFirestore = FirebaseFirestore.getInstance();
        nroDeCaja = findViewById(R.id.textViewCaja);
        visualizarCaja = findViewById(R.id.visualizarCaja);
        drawerLayout = findViewById(R.id.drawerLayoutOrdCajas);
        toolbar = findViewById(R.id.toolbarOrdCajas);

        verificarElementosYTecnicoCargado();

        //ACA ACTIVO EL BOTON DEL TOOLBAR PARA ABRIR EÑ NAVIGATION VIEW
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if(ab!=null){
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }


        datosElementos = findViewById(R.id.recyclerViewOrdenarCajas);
        datosElementos.setLayoutManager(new LinearLayoutManager(this));

        //CREO EL COLLECTION REFERENCE PARA AGARRAR LA DIRECCION DE DONDE SE ENCUENTRAN LOS DATOS EN LA BD
        Query query = firebaseFirestore.collection(Global.usuario.getEmail())
                .document(Global.fechaRetiro).collection("cajas")
                .document(Global.horaInicioRetiro).collection("elementos");

        FirestoreRecyclerOptions<Elemento> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Elemento>().setQuery(query, Elemento.class).build();

        adaptador = new AdaptadorOrdenarCajas(firestoreRecyclerOptions);
        adaptador.notifyDataSetChanged();
        datosElementos.setAdapter(adaptador);


        nroDeCaja.setText("CAJA NUMERO: " + Global.cantCajas + "/" + Global.tecnico.getCajas());


        visualizarCaja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarElementosYTecnicoCargado();

                if(Global.hayElementosCargados && Global.tecnicoRegistrado) {
                    boolean elementosSeleccionados = false;
                    //ESTO ES SOLO PARA VERIFICAR QUE AL MENOS UN ELEMENTO DEL RECYCLER VIEW ESTE SELECCIONADO
                    for (Elemento elemento : Global.arrayElemCargados){
                        if(elemento.estaSeleccionado()){
                            startActivity(new Intent(OrdenarCajas.this, MostrarCaja.class));
                        }
                    }
                }else if (!Global.hayElementosCargados){
                    Toast.makeText(OrdenarCajas.this, "No puede cerrar una caja si no hay elementos cargados", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(OrdenarCajas.this, EscanearCodigo.class));
                    finish();
                }else {
                    Toast.makeText(OrdenarCajas.this, "Debe registrarse para cargar las cajas", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(OrdenarCajas.this, IngresarDatosTecnico.class));
                    finish();
                }
            }
        });


        // ACTIVAR O HACER QUE FUNCIONEN LOS BOTONES DEL MENU DESPLEGABLE DEL MENU
        navigationView = findViewById(R.id.navigationOrdenarCajas);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem Item) {
                switch (Item.getItemId()) {
                    case R.id.escanearCodigo:
                        Global.codigo="";
                        startActivity(new Intent(OrdenarCajas.this, EscanearCodigo.class));
                        return true;

                    case R.id.mostrarElementos:
                        verificarElementosYTecnicoCargado();
                        if(Global.hayElementosCargados){
                            startActivity(new Intent(OrdenarCajas.this, MostrarElementos.class));
                        }else{
                            Toast.makeText(OrdenarCajas.this, "No hay elementos cargados", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(OrdenarCajas.this, EscanearCodigo.class));
                            finish();
                        }

                        return true;
                }
                return true;
            }
        });

        guardarDatosServicio();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return(true);
        }
        return super.onOptionsItemSelected(item);
    }

    private void guardarDatosServicio() {
        SharedPreferences preferences = getSharedPreferences("datosServicio", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("terminoRetiro", Global.terminoRetiro);
        editor.commit();
    }

    private void verificarElementosYTecnicoCargado(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        //VERIFICO SI SE CARGO AL MENOS UN ELEMENTO
        firebaseFirestore.collection(Global.usuario.getEmail())
                .document(Global.fechaRetiro).collection("cajas")
                .document(Global.horaInicioRetiro).collection("elementos").limit(1)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty()){
                            Global.hayElementosCargados = false;
                        }else{
                            Global.hayElementosCargados = true;
                        }
                    }
                });

        //VERIFICO SI SE CARGO UN TECNICO
        firebaseFirestore.collection(Global.usuario.getEmail()).document(Global.fechaRetiro)
                .collection("cajas").document(Global.horaInicioRetiro).collection("tecnico").limit(1)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty()){
                            Global.tecnicoRegistrado=false;
                        }else{
                            Global.tecnicoRegistrado=true;
                        }
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            startActivity(new Intent(this, EscanearCodigo.class));
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //EMPIEZA A ESCUCHAR POR SI SE AGREGAN MAS DATOS
        adaptador.startListening();
        pantallas[4] = false;
        actualizarEstadoCuenta();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //SE PARA PARA NO ESCUCHAR POR SI SE AGREGAN MAS DATOS
        adaptador.stopListening();
        pantallas[4] = true;
        actualizarEstadoCuenta();
    }

}