package com.example.scannerdecodigodebarras;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.HashMap;
import java.util.Map;

public class IngresarDatosTecnico extends General {

    private NavigationView navigationView;
    private EditText dniTecnico, nombreTecnico, apellidoTecnico, telefonoTec, nroImplementacion, nombreEmpresa;
    private SearchableSpinner cantCajas, responsableRetiro;
    private String[] responsablesRetiro = {"dsantacruz","gsenin","cvardanega", "psanchez",
            "jlopez", "pmolas", "nspagnuolo", "jmidaberry", "mgrosso","sdpinto","jmancini" };
    private Integer[] cantidadCajas = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private Button boton_siguiente;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog dialog;

    //ESTO ES PARA EL BOTON DEL TOOLBAR PARA ABRIR EL NAVIGATION VIEW
    private DrawerLayout drawerLayout;
    private MaterialToolbar toolbar;

    public IngresarDatosTecnico(){
        if(!Global.pantallasCreadas[2]){
            Global.pantallasCreadas[2]=true;
            cantPantallasCreadas++;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_datos_tecnico);

        //ESTO HACE QUE EL TECNICO SE REGISTRE SOLO UNA VEZ
        if(!Global.tecnicoRegistrado){
            firebaseFirestore = FirebaseFirestore.getInstance();

            navigationView = findViewById(R.id.navigation_ingresar_datos_usuario);
            dniTecnico = findViewById(R.id.edit_dni_tecnico);
            nombreTecnico = findViewById(R.id.edit_nombre_tecnico);
            apellidoTecnico = findViewById(R.id.edit_apellido_tecnico);
            telefonoTec = findViewById(R.id.edit_nro_celular_tecnico);
            nroImplementacion = findViewById(R.id.edit_nro_implementacion);
            responsableRetiro = findViewById(R.id.edit_responsable);
            cantCajas = findViewById(R.id.edit_cantidad_cajas);
            boton_siguiente = findViewById(R.id.siguiente);
            toolbar = findViewById(R.id.toolbarIngDatTec);
            drawerLayout = findViewById(R.id.drawerLayoutIngDatTec);
            nombreEmpresa = findViewById(R.id.edit_nombreEmpresa);

            //ACA ACTIVO EL BOTON DEL TOOLBAR PARA ABRIR EÑ NAVIGATION VIEW
            setSupportActionBar(toolbar);
            ActionBar ab = getSupportActionBar();
            if(ab!=null){
                ab.setHomeAsUpIndicator(R.drawable.ic_menu);
                ab.setDisplayHomeAsUpEnabled(true);
            }

            verificarElementosYTecnicoCargado();

            cargarSpinner();

            // ACTIVAR O HACER QUE FUNCIONEN LOS BOTONES DEL MENU DESPLEGABLE DEL MENU
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem Item) {
                    switch (Item.getItemId()) {
                        case R.id.mostrarElementos:
                            verificarElementosYTecnicoCargado();
                            if(Global.hayElementosCargados){
                                startActivity(new Intent(IngresarDatosTecnico.this, MostrarElementos.class));
                            }else{
                                Toast.makeText(IngresarDatosTecnico.this, "No hay elementos cargados", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(IngresarDatosTecnico.this, EscanearCodigo.class));
                                finish();
                            }

                            return true;

                        case R.id.escanearCodigo:
                            startActivity(new Intent(IngresarDatosTecnico.this, EscanearCodigo.class));
                            return true;
                    }
                    return true;
                }
            });

            boton_siguiente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verificarElementosYTecnicoCargado();
                    if(Global.hayElementosCargados) {
                        agregarTecnico();
                    }else{
                        Toast.makeText(IngresarDatosTecnico.this, "Primero debe cargar elementos para cargar sus datos", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(IngresarDatosTecnico.this,EscanearCodigo.class));
                        finish();
                    }
                }
            });

            guardarDatosServicio();
        }
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

    private void agregarTecnico() {

        String nombre = nombreTecnico.getText().toString().trim();
        String apellido = apellidoTecnico.getText().toString().trim();
        String dni = dniTecnico.getText().toString().trim();
        String telefono = telefonoTec.getText().toString().trim();
        String cajas = cantCajas.getSelectedItem().toString().trim();
        String nroImplem = nroImplementacion.getText().toString().trim();
        String empresa = nombreEmpresa.getText().toString().trim();

        if(nombre.isEmpty()){
            nombreTecnico.setError("El campo no puede estar vacio");
        }else if (nombre.length() < 3){
            nombreTecnico.setError("Debe tener minimo 3 caracteres");
        }
        if(apellido.isEmpty()){
            apellidoTecnico.setError("El campo no puede estar vacio");
        }else if (apellido.length() < 4){
            apellidoTecnico.setError("Debe tener minimo 4 caracteres");
        }
        if(dni.isEmpty()){
            dniTecnico.setError("El campo no puede estar vacio");
        }else if(dni.length() < 8){
            dniTecnico.setError("Debe tener minimo 8 numeros");
        }
        if(telefono.isEmpty()){
            telefonoTec.setError("El campo no puede estar vacio");
        }else if (telefono.length() < 10){
            telefonoTec.setError("Debe tener minimo 10 numeros");
        }
        if(nroImplem.isEmpty()){
            nroImplementacion.setError("El campo no puede estar vacio");
        }
        if(empresa.isEmpty()){
            nombreEmpresa.setError("El campo no puede estar vacio");
        }

        if(empresa.isEmpty() || nroImplem.isEmpty() || telefono.isEmpty() || dni.isEmpty() || apellido.isEmpty() || nombre.isEmpty() || nombre.length() < 3 || apellido.length() < 4 || dni.length() < 8 || telefono.length() < 10){
            return;
        }

        //GUARDO ESTOS DATOS PARA QUE CUANDO TERMINE EL RETIRO LO GUARDE EN EL CAMPO DEL RETIRO
        Global.nroImplementacion = nroImplementacion.getId();
        Global.responsableRetiro = responsableRetiro.getSelectedItem().toString().trim();

        dialog = ProgressDialog.show(this,"", "Verificando datos...");

        Map<String, Object> datoTec = new HashMap<>();
        datoTec.put("nombre", nombre);
        datoTec.put("apellido", apellido);
        datoTec.put("dni", dni);
        datoTec.put("telefono", telefono);
        datoTec.put("empresa", empresa);

        Global.tecnico = new Tecnico(nombre, apellido, dni, telefono, empresa, cajas);

        verificarExisteTecnico(dni, datoTec);

    }

    private void cargarSpinner(){
        ArrayAdapter<String> responsableAdapter = new ArrayAdapter<String>(IngresarDatosTecnico.this, android.R.layout.simple_spinner_item, responsablesRetiro);
        responsableAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        responsableRetiro.setAdapter(responsableAdapter);

        ArrayAdapter<Integer> cantidadCajasAdapter = new ArrayAdapter<Integer>(IngresarDatosTecnico.this, android.R.layout.simple_spinner_item, cantidadCajas);
        cantidadCajasAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        cantCajas.setAdapter(cantidadCajasAdapter);
    }

    private void verificarExisteTecnico(String dni, Object datoTec){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(Global.usuario.getEmail()).document(Global.fechaRetiro)
                .collection("tecnico").document(dni).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Toast.makeText(IngresarDatosTecnico.this, "Su usuario ya existe!", Toast.LENGTH_SHORT).show();
                        }else{
                            cargarTecnico(dni, datoTec);
                        }
                    }
                });
    }

    private void cargarTecnico(String dni, Object datoTec){
        firebaseFirestore.collection(Global.usuario.getEmail()).document(Global.fechaRetiro).collection("cajas")
                .document(Global.horaInicioRetiro).collection("tecnico").document(dni).set(datoTec).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(IngresarDatosTecnico.this, "Se ha registrado exitosamente", Toast.LENGTH_SHORT).show();
                Global.tecnicoRegistrado=true;
                dialog.dismiss();
                startActivity(new Intent(IngresarDatosTecnico.this, OrdenarCajas.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(IngresarDatosTecnico.this, "Error al cargar el usuario", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
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
        pantallas[2] = false;
        actualizarEstadoCuenta();
        cargarSpinner();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pantallas[2] = true;
        actualizarEstadoCuenta();
    }
}