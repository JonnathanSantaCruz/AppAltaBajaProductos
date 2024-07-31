package com.example.scannerdecodigodebarras;

import static android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
import static android.text.InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IniciarSesion extends AppCompatActivity {

    private CheckBox CbRecordarUsuario;
    private Usuario usuario;

    // variables autenticacion de la bd
    private String FILE_NAME = "recordarme";
    private FirebaseAuth fireBaseAuth;
    private EditText edit_usuario, edit_contra;
    private boolean contraVisible;
    private Button iniciarSesion;

    public IniciarSesion(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Global.elementos.add("Ninguno");
        firebaseFirestore.collection("elementos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                        Global.elementos.add(documentSnapshot.getId());
                    }
                }
            }
        });

        Global.filiales.add("Ninguno");
        firebaseFirestore.collection("filiales").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                        Global.filiales.add(documentSnapshot.getId());
                    }
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        iniciarSesion = (Button) findViewById(R.id.button_inicio);
        fireBaseAuth = FirebaseAuth.getInstance();
        edit_usuario = (EditText) findViewById(R.id.edit_correo);
        edit_contra = findViewById(R.id.edit_contrasenia);
        CbRecordarUsuario = (CheckBox) findViewById(R.id.CheckBoxRecordarUsuario);
        SharedPreferences preferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String correoElectronico = preferences.getString("Correo Electronico", "");
        String contraseña = preferences.getString("Contraseña", "");
        SharedPreferences.Editor editor = preferences.edit();

        if (preferences.contains("checked") && preferences.getBoolean("checked", false) == true) {
            CbRecordarUsuario.setChecked(true);
        } else {
            CbRecordarUsuario.setChecked(false);
        }
        edit_usuario.setText(correoElectronico);
        edit_contra.setText(contraseña);


        //MOSTRAR O NO LA CONTRASEÑA
        edit_contra.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int right=2;
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(event.getRawX() >= edit_contra.getRight()-edit_contra.getCompoundDrawables()[right].getBounds().width()){
                        int seleccion = edit_contra.getSelectionEnd();
                        if(contraVisible){
                            edit_contra.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.logo_no_ver, 0);
                            edit_contra.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            contraVisible=false;
                        }else{
                            edit_contra.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.logo_ver, 0);
                            edit_contra.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            contraVisible=true;
                        }
                        edit_contra.setSelection(seleccion);
                        return true;
                    }
                }

                return false;
            }
        });


        //Carga de usuario a la bd
        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correoElectronico = edit_usuario.getText().toString().trim();
                String contraseña = edit_contra.getText().toString().trim();
                if (correoElectronico.isEmpty() && contraseña.isEmpty()) {
                    Toast.makeText(IniciarSesion.this, "Ingrese los datos", Toast.LENGTH_SHORT).show();
                } else if (CbRecordarUsuario.isChecked()) {
                    editor.putBoolean("checked", true);
                    editor.apply();
                    guardarDatos(correoElectronico, contraseña);
                    if (!Patterns.EMAIL_ADDRESS.matcher(correoElectronico).matches()) {
                        edit_usuario.setError("Complete su email");
                        edit_usuario.requestFocus();
                    }
                    verificarCuentaEstaSiendoUsada(correoElectronico, contraseña);

                } else {
                    getSharedPreferences(FILE_NAME, MODE_PRIVATE).edit().clear().commit();
                    edit_contra.setText(contraseña);
                    edit_usuario.setText(correoElectronico);
                    CbRecordarUsuario.setChecked(false);
                    verificarCuentaEstaSiendoUsada(correoElectronico, contraseña);
                }
            }
        });

    }

    //Validacion para iniciarSesion
    private void loguearUsuario(String email, String contra) {
        final ProgressDialog dialog = ProgressDialog.show(IniciarSesion.this,"", "Verificando datos...");
        // autenticacion iniciar sesion
        fireBaseAuth.signInWithEmailAndPassword(email, contra)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(CbRecordarUsuario.isChecked()){
                                mantenerCuentaIniciada();
                            }
                            Global.cuentaUsada=true;
                            String[] mail = email.split("ilial");

                            //ACA SE FIJA SI EL QUE INICIO SESION ES UN ADMIN
                            if(mail[0].equals("f")){
                                Global.cantPantallas = 6;
                                Global.pantallasCreadas = new boolean[Global.cantPantallas];
                                usuario = new Usuario("+"+email, contra, false);
                                Global.usuario = usuario;
                                //GUARDO DATOS QUE LUEGO VOY A UTILIZAR EN EL SERVICIO
                                guardarDatosServicio();
                                //INICIO EL SERVICIO
                                startService(new Intent(IniciarSesion.this, ServicioEstadoCuenta.class));
                                Toast.makeText(IniciarSesion.this, "Bienvenido " + email, Toast.LENGTH_LONG).show();
                                Intent iniciarSesion = new Intent(IniciarSesion.this, EscanearCodigo.class);
                                startActivity(iniciarSesion);
                            }else {
                                Global.cantPantallas = 1;
                                Global.pantallasCreadas = new boolean[Global.cantPantallas];
                                usuario = new Usuario(email, contra, true);
                                Global.usuario = usuario;
                                //GUARDO DATOS QUE LUEGO VOY A UTILIZAR EN EL SERVICIO
                                guardarDatosServicio();
                                //INICIO EL SERVICIO
                                startService(new Intent(IniciarSesion.this, ServicioEstadoCuenta.class));
                                Toast.makeText(IniciarSesion.this, "Bienvenido " + email, Toast.LENGTH_LONG).show();
                                Intent iniciarSesion = new Intent(IniciarSesion.this, Administrador.class);
                                startActivity(iniciarSesion);
                            }
                            finish();
                            dialog.dismiss();
                        } else {// Verifica si existe el usuario
                            Toast.makeText(IniciarSesion.this, "Los datos son incorrectos", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        iniciarSesion.setEnabled(true);
                    }
                });
    }

    private void guardarDatosServicio() {
        SharedPreferences preferences = getSharedPreferences("datosServicio", Context.MODE_PRIVATE);
        String gmail = Global.usuario.getEmail();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("gmail",gmail);
        editor.commit();
    }

    //Si cierro sesion lo que hace es que me guarda los ultimos datos cargados
    private void guardarDatos(String emailUsuario, String contraseña) {
        SharedPreferences.Editor editor = getSharedPreferences(FILE_NAME, MODE_PRIVATE).edit();
        editor.putString("Correo Electronico", emailUsuario);
        editor.putString("Contraseña", contraseña);
        editor.apply();
    }

    //MANTENER CUENTA INICIADA
    private void mantenerCuentaIniciada(){
        SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        boolean estado = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("estado-usu", estado);
        editor.commit();
    }

    private void verificarCuentaEstaSiendoUsada(String correoElectronico, String contraseña){
        //ESTO VERIFICA SI LA CUENTA ESTA SIENDO USADA EN OTRO DISPOSITIVO
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("+"+edit_usuario.getText().toString().trim()).document("usando cuenta")
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(!documentSnapshot.exists()){
                        Map<String, Object> dato = new HashMap<>();
                        dato.put("inicio sesion", "true");

                        String[] mail = correoElectronico.split("ilial");
                        //ACA SE FIJA SI EL QUE INICIO SESION ES UN ADMIN
                        if(mail[0].equals("f")){
                            firebaseFirestore.collection("+"+correoElectronico).document("usando cuenta")
                                    .set(dato);
                        }else{
                            firebaseFirestore.collection(correoElectronico).document("usando cuenta")
                                    .set(dato);
                        }
                        loguearUsuario(correoElectronico, contraseña);
                    }else{
                        if(documentSnapshot.getString("inicio sesion").equals("true")){
                            Toast.makeText(IniciarSesion.this, "La cuenta esta siendo usada en otro dispositivo", Toast.LENGTH_SHORT).show();
                        }else{
                            Map<String, Object> dato = new HashMap<>();
                            dato.put("inicio sesion", "true");

                            String[] mail = correoElectronico.split("ilial");
                            //ACA SE FIJA SI EL QUE INICIO SESION ES UN ADMIN
                            if(mail[0].equals("f")){
                                firebaseFirestore.collection("+"+correoElectronico).document("usando cuenta")
                                        .set(dato);
                            }else{
                                firebaseFirestore.collection(correoElectronico).document("usando cuenta")
                                        .set(dato);
                            }
                            loguearUsuario(correoElectronico, contraseña);
                        }
                    }
            }
        });
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
}