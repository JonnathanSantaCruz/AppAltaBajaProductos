package com.example.scannerdecodigodebarras;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class MostrarElementos extends General{

    private NavigationView navigationView;
    private Button volver, elimimarElementos;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView datosElemento;
    private AdaptadorMostrarElementos adaptador;

    //ESTO ES PARA EL BOTON DEL TOOLBAR PARA ABRIR EL NAVIGATION VIEW
    private DrawerLayout drawerLayout;
    private MaterialToolbar toolbar;

    public MostrarElementos(){
        if(!Global.pantallasCreadas[3]){
            Global.pantallasCreadas[3]=true;
            cantPantallasCreadas++;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_elementos);

        firebaseFirestore = FirebaseFirestore.getInstance();

        drawerLayout = findViewById(R.id.drawerLayoutMostrarElem);
        toolbar = findViewById(R.id.toolbarMostrarElem);

        verificarElementosYTecnicoCargado();

        if(!Global.hayElementosCargados){
            startActivity(new Intent(MostrarElementos.this,EscanearCodigo.class));
            finish();
        }

        //ACA ACTIVO EL BOTON DEL TOOLBAR PARA ABRIR EÑ NAVIGATION VIEW
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if(ab!=null){
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        datosElemento = findViewById(R.id.recyclerViewMostrarElementos);
        datosElemento.setLayoutManager(new LinearLayoutManager(this));

        //CREO EL COLLECTION REFERENCE PARA AGARRAR LA DIRECCION DE DONDE SE ENCUENTRAN LOS DATOS EN LA BD
        Query query = firebaseFirestore.collection(Global.usuario.getEmail())
                .document(Global.fechaRetiro).collection("cajas")
                .document(Global.horaInicioRetiro).collection("elementos");

        FirestoreRecyclerOptions<Elemento> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Elemento>().setQuery(query, Elemento.class).build();

        adaptador = new AdaptadorMostrarElementos(firestoreRecyclerOptions);
        adaptador.notifyDataSetChanged();
        datosElemento.setAdapter(adaptador);


        volver = findViewById(R.id.volver);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent (MostrarElementos.this, EscanearCodigo.class));
                finish();
            }
        });

        elimimarElementos = findViewById(R.id.eliminarElemMostrarElem);
        elimimarElementos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Elemento elemento : Global.arrayElemCargados){
                    if(elemento.estaSeleccionado()){
                        firebaseFirestore.collection(Global.usuario.getEmail()).document(Global.fechaRetiro)
                                .collection("cajas").document(Global.horaInicioRetiro)
                                .collection("elementos").document(elemento.getCodigo()).delete();
                    }
                }

                verificarElementosYTecnicoCargado();

                if(!Global.hayElementosCargados){
                    startActivity(new Intent(MostrarElementos.this, EscanearCodigo.class));
                    finish();
                }
            }
        });

        // ACTIVAR O HACER QUE FUNCIONEN LOS BOTONES DEL MENU DESPLEGABLE DEL MENU
        navigationView = findViewById(R.id.mostrarProd_navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem Item) {
                switch (Item.getItemId()) {
                    case R.id.escanearCodigo:
                        startActivity(new Intent(MostrarElementos.this, EscanearCodigo.class));
                        return true;

                    case R.id.mostrarElementos:
                        Toast.makeText(MostrarElementos.this, "Usted ya se encuentra aca", Toast.LENGTH_LONG).show();
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
        pantallas[3] = false;
        actualizarEstadoCuenta();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //SE PARA PARA NO ESCUCHAR POR SI SE AGREGAN MAS DATOS
        adaptador.stopListening();
        pantallas[3] = true;
        actualizarEstadoCuenta();
    }

}