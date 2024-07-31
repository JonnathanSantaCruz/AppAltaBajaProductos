package com.example.scannerdecodigodebarras;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.HashMap;
import java.util.Map;

public class Administrador extends General {

    private TextView text_admin;
    private SearchableSpinner edit_buscar_filial, edit_eliminar_elemento;
    private Button boton_act_filial, actualizarEst, eliminarElem, agregarElem, eliminarUsua, agregarUsua,
            boton_agregar_elem, boton_eliminar_elem, boton_agregar_usu;
    private EditText edit_agregar_elemento, edit_agregar_nombre_usuario, edit_agregar_numero_usuario;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private boolean nroExiste=false, filialExiste=false, elementoExiste=false;

    public Administrador(){
        if(!Global.pantallasCreadas[0]){
            Global.pantallasCreadas[0]=true;
            cantPantallasCreadas=1;
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);

        actualizarEst = findViewById(R.id.boton_actualizar_estado);
        eliminarElem = findViewById(R.id.boton_eliminar_elementos);
        agregarElem = findViewById(R.id.boton_agregar_elementos);
        eliminarUsua = findViewById(R.id.boton_eliminar_usuario);
        agregarUsua = findViewById(R.id.boton_agregar_usuario);

        text_admin = findViewById(R.id.text_admin);
        edit_buscar_filial = findViewById(R.id.edit_buscar_filial);
        boton_act_filial = findViewById(R.id.boton_actualizar_filial);
        edit_agregar_elemento = findViewById(R.id.edit_agregar_elemento);
        boton_agregar_elem = findViewById(R.id.boton_agregar_elem);
        edit_eliminar_elemento = findViewById(R.id.edit_eliminar_elemento);
        boton_eliminar_elem = findViewById(R.id.boton_eliminar_elem);
        edit_agregar_nombre_usuario = findViewById(R.id.edit_agregar_nombre_usuario);
        edit_agregar_numero_usuario = findViewById(R.id.edit_agregar_numero_usuario);
        boton_agregar_usu = findViewById(R.id.boton_agregar_usu);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        cargarSpinners();

        actualizarEst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_agregar_elemento.setVisibility(View.GONE);
                boton_agregar_elem.setVisibility(View.GONE);
                edit_eliminar_elemento.setVisibility(View.GONE);
                boton_eliminar_elem.setVisibility(View.GONE);
                edit_agregar_nombre_usuario.setVisibility(View.GONE);
                boton_agregar_usu.setVisibility(View.GONE);
                edit_agregar_numero_usuario.setVisibility(View.GONE);
                actualizarEstado();
            }
        });

        eliminarElem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_agregar_elemento.setVisibility(View.GONE);
                boton_agregar_elem.setVisibility(View.GONE);
                edit_buscar_filial.setVisibility(View.GONE);
                boton_act_filial.setVisibility(View.GONE);
                edit_agregar_nombre_usuario.setVisibility(View.GONE);
                boton_agregar_usu.setVisibility(View.GONE);
                edit_agregar_numero_usuario.setVisibility(View.GONE);
                eliminarElemento();
            }
        });

        agregarElem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_buscar_filial.setVisibility(View.GONE);
                boton_act_filial.setVisibility(View.GONE);
                edit_eliminar_elemento.setVisibility(View.GONE);
                boton_eliminar_elem.setVisibility(View.GONE);
                edit_agregar_nombre_usuario.setVisibility(View.GONE);
                boton_agregar_usu.setVisibility(View.GONE);
                edit_agregar_numero_usuario.setVisibility(View.GONE);
                agregarElemento();
            }
        });

        agregarUsua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_agregar_elemento.setVisibility(View.GONE);
                boton_agregar_elem.setVisibility(View.GONE);
                edit_eliminar_elemento.setVisibility(View.GONE);
                boton_eliminar_elem.setVisibility(View.GONE);
                edit_buscar_filial.setVisibility(View.GONE);
                boton_act_filial.setVisibility(View.GONE);
                agregarFilial();
            }
        });

        eliminarUsua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_agregar_elemento.setVisibility(View.GONE);
                boton_agregar_elem.setVisibility(View.GONE);
                edit_eliminar_elemento.setVisibility(View.GONE);
                boton_eliminar_elem.setVisibility(View.GONE);
                edit_agregar_nombre_usuario.setVisibility(View.GONE);
                boton_agregar_usu.setVisibility(View.GONE);
                edit_agregar_numero_usuario.setVisibility(View.GONE);
                eliminarFilial();
            }
        });
    }

    private void eliminarFilial() {
        text_admin.setText("Nombre de la filial");
        text_admin.setVisibility(View.VISIBLE);
        edit_buscar_filial.setVisibility(View.VISIBLE);
        edit_buscar_filial.setTitle("Elija una filial");
        boton_act_filial.setVisibility(View.VISIBLE);
        boton_act_filial.setText("Eliminar");

        boton_act_filial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edit_buscar_filial.getSelectedItem()!=null){
                    if(!edit_buscar_filial.getSelectedItem().toString().trim().isEmpty() && !edit_buscar_filial.getSelectedItem().toString().trim().equals("Ninguno")){

                        String[] numFilial = edit_buscar_filial.getSelectedItem().toString().trim().split(" -");
                        //ESTO LE DA EL VALOR A LA FILIAL, OSEA SI LA FILIAL ES 1 ENTONCES COMPLETA LOS ESPACIOS FALTANTES CON CEROS
                        if(Integer.parseInt(numFilial[0]) < 10){
                            numFilial[0] = "000"+numFilial[0];
                        }else if(Integer.parseInt(numFilial[0]) < 100){
                            numFilial[0] = "00"+numFilial[0];
                        } else if(Integer.parseInt(numFilial[0]) < 1000){
                            numFilial[0] = "0"+numFilial[0];
                        }
                        String email = "filial" + numFilial[0] + "@bancocredicoop.coop";
                        //LE PASO LOS DATOS DE LA CUENTA QUE QUIERO ELIMINAR
                        String contra = "gercom123";

                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        firebaseAuth.signInWithEmailAndPassword(email, contra).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    //ACA LA ELIMINO (CODIGO DE FIREBASE)
                                    firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(Administrador.this, "Filial eliminada!", Toast.LENGTH_SHORT).show();
                                                firebaseFirestore.collection("filiales").document(edit_buscar_filial.getSelectedItem().toString().toLowerCase().trim()).delete();
                                                /* ESTO ME DEBERIA ELIMINAR LOS DATOS DE LA FILIAL QUE ELIMINE +filialxxxx@bancocredicoop.coop, PERO NO LO HACE
                                                firebaseFirestore.collection("+"+email).get()
                                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                if(!queryDocumentSnapshots.isEmpty()){
                                                                    for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                                                                        firebaseFirestore.collection("+"+email).document(documentSnapshot.getId()).delete();
                                                                    }
                                                                }
                                                            }
                                                        });
                                                */
                                                Global.filiales.remove(edit_buscar_filial.getSelectedItem().toString().toLowerCase().trim());
                                                cargarSpinners();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Administrador.this, "No se pudo eliminar la cuenta", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    Toast.makeText(Administrador.this, "No se pudo eliminar la cuenta", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        text_admin.setVisibility(View.GONE);
                        edit_buscar_filial.setVisibility(View.GONE);
                        boton_act_filial.setVisibility(View.GONE);
                    }
                }else{
                    Toast.makeText(Administrador.this, "Elija una filial", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void agregarFilial() {
        text_admin.setText("Datos de la nueva filial");
        text_admin.setVisibility(View.VISIBLE);
        edit_agregar_nombre_usuario.setVisibility(View.VISIBLE);
        edit_agregar_numero_usuario.setVisibility(View.VISIBLE);
        boton_agregar_usu.setVisibility(View.VISIBLE);

        boton_agregar_usu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!edit_agregar_numero_usuario.getText().toString().trim().isEmpty() && !edit_agregar_numero_usuario.getText().toString().trim().equals("")
                        && !edit_agregar_nombre_usuario.getText().toString().trim().isEmpty() && !edit_agregar_nombre_usuario.getText().toString().trim().equals("")){
                    verificarExisteFilial();
                }else{
                    Toast.makeText(Administrador.this, "Complete los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void verificarExisteFilial() {
        int nroFilial = Integer.parseInt(edit_agregar_numero_usuario.getText().toString().trim());

        firebaseFirestore.collection("filiales").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String filial = edit_agregar_numero_usuario.getText().toString().trim() + " - " + edit_agregar_nombre_usuario.getText().toString().toLowerCase().trim();

                if(!queryDocumentSnapshots.isEmpty()){
                    for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                        String[] datoFilial = documentSnapshot.getId().split(" - ");
                        if(Integer.parseInt(datoFilial[0]) == nroFilial){
                            nroExiste=true;
                        }
                        if(edit_agregar_nombre_usuario.getText().toString().toLowerCase().trim().equals(datoFilial[1])){
                            filialExiste=true;
                        }
                    }

                    if(nroExiste && filialExiste){
                        nroExiste=false;
                        filialExiste=false;
                        Toast.makeText(Administrador.this, "La filial ya existe", Toast.LENGTH_SHORT).show();
                        edit_agregar_nombre_usuario.setError("Ya existe");
                        edit_agregar_numero_usuario.setError("Ya existe");
                    }else if(filialExiste){
                        filialExiste=false;
                        Toast.makeText(Administrador.this, "El nombre de la filial ya existe", Toast.LENGTH_SHORT).show();
                        edit_agregar_nombre_usuario.setError("Ya existe");
                    }else if(nroExiste){
                        nroExiste=false;
                        Toast.makeText(Administrador.this, "El numero de filial ya existe", Toast.LENGTH_SHORT).show();
                        edit_agregar_numero_usuario.setError("Ya existe");
                    }else{
                        String numFilial = edit_agregar_numero_usuario.getText().toString().trim();
                        //ESTO LE DA EL VALOR A LA FILIAL, OSEA SI LA FILIAL ES 1 ENTONCES COMPLETA LOS ESPACIOS FALTANTES CON CEROS
                        if(Integer.parseInt(numFilial) < 10){
                            numFilial = "000"+numFilial;
                        }else if(Integer.parseInt(numFilial) < 100){
                            numFilial = "00"+numFilial;
                        } else if(Integer.parseInt(numFilial) < 1000){
                            numFilial = "0"+numFilial;
                        }

                        Map<String, Object> datosFilial = new HashMap<>();
                        datosFilial.put(filial, filial);

                        firebaseFirestore.collection("filiales").document(filial).set(datosFilial);
                        Global.filiales.add(filial);
                        cargarSpinners();

                        String email = "filial" + numFilial + "@bancocredicoop.coop";
                        String contra = "gercom123";
                        firebaseAuth.createUserWithEmailAndPassword(email, contra)
                                .addOnCompleteListener(Administrador.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Administrador.this, "La filial se ha agregado", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        text_admin.setVisibility(View.GONE);
                        edit_agregar_nombre_usuario.setVisibility(View.GONE);
                        edit_agregar_numero_usuario.setVisibility(View.GONE);
                        boton_agregar_usu.setVisibility(View.GONE);

                    }
                }
            }
        });
    }

    private void agregarElemento() {
        text_admin.setText("Nombre del elemento");
        text_admin.setVisibility(View.VISIBLE);
        edit_agregar_elemento.setVisibility(View.VISIBLE);
        boton_agregar_elem.setVisibility(View.VISIBLE);
        boton_agregar_elem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!edit_agregar_elemento.getText().toString().trim().isEmpty() && !edit_agregar_elemento.getText().toString().trim().equals("")){

                    firebaseFirestore.collection("elementos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(!queryDocumentSnapshots.isEmpty()){
                                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                                    if(edit_agregar_elemento.getText().toString().toLowerCase().trim().equals(documentSnapshot.getId().trim())){
                                        elementoExiste=true;
                                    }
                                }

                                if(elementoExiste){
                                    elementoExiste=false;
                                    Toast.makeText(Administrador.this, "El elemento ya existe", Toast.LENGTH_SHORT).show();
                                    edit_agregar_elemento.setError("Ya existe");
                                }else{
                                    String elemento = edit_agregar_elemento.getText().toString().toLowerCase().trim();
                                    Map<String, Object> datosElemento = new HashMap<>();
                                    datosElemento.put(elemento, elemento);
                                    firebaseFirestore.collection("elementos").document(elemento).set(datosElemento);
                                    Toast.makeText(Administrador.this, "Elemento agregado correctamente", Toast.LENGTH_SHORT).show();
                                    Global.elementos.add(elemento);
                                    cargarSpinners();

                                    text_admin.setVisibility(View.GONE);
                                    edit_agregar_elemento.setVisibility(View.GONE);
                                    boton_agregar_elem.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void eliminarElemento() {
        text_admin.setText("Nombre del elemento");
        text_admin.setVisibility(View.VISIBLE);
        edit_eliminar_elemento.setVisibility(View.VISIBLE);
        edit_eliminar_elemento.setTitle("Elija un elemento");
        boton_eliminar_elem.setVisibility(View.VISIBLE);
        boton_eliminar_elem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edit_eliminar_elemento.getSelectedItem()!=null){
                    if(!edit_eliminar_elemento.getSelectedItem().toString().isEmpty() && !edit_eliminar_elemento.getSelectedItem().toString().equals("Ninguno")){
                        String elemento = edit_eliminar_elemento.getSelectedItem().toString().toLowerCase().trim();
                        firebaseFirestore.collection("elementos").document(elemento).delete();
                        Global.elementos.remove(elemento);
                        Toast.makeText(Administrador.this, "Elemento eliminado correctamente", Toast.LENGTH_SHORT).show();
                        cargarSpinners();

                        text_admin.setVisibility(View.GONE);
                        edit_eliminar_elemento.setVisibility(View.GONE);
                        boton_eliminar_elem.setVisibility(View.GONE);
                    }
                }else{
                    Toast.makeText(Administrador.this, "Elija un elemento", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void actualizarEstado() {
        text_admin.setText("Nombre de la filial");
        text_admin.setVisibility(View.VISIBLE);
        edit_buscar_filial.setVisibility(View.VISIBLE);
        edit_buscar_filial.setTitle("Elija una filial");
        boton_act_filial.setVisibility(View.VISIBLE);
        boton_act_filial.setText("Actualizar");

        boton_act_filial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edit_buscar_filial.getSelectedItem() == null){
                    Toast.makeText(Administrador.this, "Elija una filial", Toast.LENGTH_SHORT).show();
                }else{
                    if(!edit_buscar_filial.getSelectedItem().toString().trim().isEmpty() && !edit_buscar_filial.getSelectedItem().toString().trim().equals("Ninguno")){
                        String numFilial[] = edit_buscar_filial.getSelectedItem().toString().trim().split(" - ");

                        //ESTO LE DA EL VALOR A LA FILIAL, OSEA SI LA FILIAL ES 1 ENTONCES COMPLETA LOS ESPACIOS FALTANTES CON CEROS
                        if(Integer.parseInt(numFilial[0]) < 10){
                            numFilial[0] = "000"+numFilial[0];
                        }else if(Integer.parseInt(numFilial[0]) < 100){
                            numFilial[0] = "00"+numFilial[0];
                        } else if(Integer.parseInt(numFilial[0]) < 1000){
                            numFilial[0] = "0"+numFilial[0];
                        }
                        String email = "+filial" + numFilial[0] + "@bancocredicoop.coop";

                        firebaseFirestore.collection(email).document("usando cuenta").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    if(documentSnapshot.get("inicio sesion").equals("false")){
                                        Map<String, Object> estado = new HashMap<>();
                                        estado.put("inicio sesion", "true");
                                        firebaseFirestore.collection(email).document("usando cuenta").update(estado);
                                        Toast.makeText(Administrador.this, "Se ha actualizado el estado a: true", Toast.LENGTH_SHORT).show();

                                    }else if(documentSnapshot.get("inicio sesion").equals("true")){
                                        Map<String, Object> estado = new HashMap<>();
                                        estado.put("inicio sesion", "false");
                                        firebaseFirestore.collection(email).document("usando cuenta").update(estado);
                                        Toast.makeText(Administrador.this, "Se ha actualizado el estado a: false", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Administrador.this, "No se pudo actualizar el estado", Toast.LENGTH_SHORT).show();
                            }
                        });

                        text_admin.setVisibility(View.GONE);
                        edit_buscar_filial.setVisibility(View.GONE);
                        boton_act_filial.setVisibility(View.GONE);
                    }
                }

            }
        });
    }

    private void cargarSpinners(){
        // CARGAR NOMBRES EN LOS SPINNERS
        ArrayAdapter<String> filialesAdapter = new ArrayAdapter<String>(Administrador.this, android.R.layout.simple_spinner_item, Global.filiales);
        filialesAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        edit_buscar_filial.setAdapter(filialesAdapter);

        ArrayAdapter<String> elementosAdapter = new ArrayAdapter<String>(Administrador.this, android.R.layout.simple_spinner_item, Global.elementos);
        elementosAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        edit_eliminar_elemento.setAdapter(elementosAdapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        pantallas[0] = false;
        actualizarEstadoCuenta();
        cargarSpinners();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pantallas[0] = true;
        actualizarEstadoCuenta();
    }
}