package com.example.scannerdecodigodebarras;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EscanearCodigo extends General {

    private Button botonScan, botonIngresarDatosProd, botonIniciarRetiro, botonDatosTecnico;
    private ImageButton borrarTexto;
    private EditText editTextResultado;
    private NavigationView navigationView;

    //ESTO ES PARA EL BOTON DEL TOOLBAR PARA ABRIR EL NAVIGATION VIEW
    private DrawerLayout drawerLayout;
    private MaterialToolbar toolbar;

    public EscanearCodigo(){
        if(!Global.pantallasCreadas[0]){
            Global.pantallasCreadas[0]=true;
            cantPantallasCreadas=1;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escanear_codigo);

        botonScan = findViewById(R.id.BotonScan);
        editTextResultado = findViewById(R.id.EditTextResultado);
        navigationView = findViewById(R.id.escanearCod_navigation);
        botonIniciarRetiro = findViewById(R.id.Iniciar_Retiro);
        botonIngresarDatosProd = findViewById(R.id.Boton_Ingresar_Datos_Prod);
        borrarTexto = findViewById(R.id.Borrar_texto);
        botonDatosTecnico = findViewById(R.id.Boton_Ingresar_Datos_Tecnico);
        drawerLayout = findViewById(R.id.drawerLayoutEscanearCodigo);
        toolbar = findViewById(R.id.toolbarEscanear);

        //ACA ACTIVO EL BOTON DEL TOOLBAR PARA ABRIR EN NAVIGATION VIEW
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }


        if (!Global.hayElementosCargados) {
            if (!Global.retiroIniciado) {
                botonIniciarRetiro.setEnabled(true);
                botonIngresarDatosProd.setEnabled(false);
                botonDatosTecnico.setEnabled(false);
                botonIniciarRetiro.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mostrarAlertaIniciarAccion();
                    }
                });
            } else {
                botonIniciarRetiro.setEnabled(false);
                botonScan.setEnabled(true);
            }
        } else {
            botonIniciarRetiro.setEnabled(false);
            botonScan.setEnabled(true);
        }

        //ESTO HACE QUE FUNCIONE EL ESCANER
        botonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrador = new IntentIntegrator(EscanearCodigo.this);
                integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrador.setCameraId(0);
                integrador.setBeepEnabled(true);
                integrador.setBarcodeImageEnabled(true);
                integrador.initiateScan();
            }
        });

        borrarTexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextResultado.setText("");
                borrarTexto.setEnabled(false);
            }
        });

        botonDatosTecnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarElementosYTecnicoCargado();
                if (Global.hayElementosCargados && !Global.tecnicoRegistrado) {
                    startActivity(new Intent(EscanearCodigo.this, IngresarDatosTecnico.class));
                } else if (!Global.hayElementosCargados) {
                    Toast.makeText(EscanearCodigo.this, "No hay ningun elemento cargado", Toast.LENGTH_LONG).show();
                } else {
                    startActivity(new Intent(EscanearCodigo.this, OrdenarCajas.class));
                }
            }
        });

        botonIngresarDatosProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarAlertaIngElemManual();
            }
        });

        //ACA LO QUE HAGO ES HACER QUE SE BLOQUEE EL BOTON SI NO SE ESCANEO NINGUN ELEMENTO,
        //ES DECIR QUE SI EL TEXTO ESTA VACIO EL BOTON VA A ESTAR INACTIVO
        //SI SE ESCANEO SE ACTIVA EL BOTON
        editTextResultado.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")) {
                    borrarTexto.setEnabled(false);
                } else {
                    borrarTexto.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")) {
                    borrarTexto.setEnabled(false);
                } else {
                    borrarTexto.setEnabled(true);
                }
            }
        });

        // ACTIVAR O HACER QUE FUNCIONEN LOS BOTONES DEL MENU DESPLEGABLE DEL MENU
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem Item) {
                switch (Item.getItemId()) {
                    case R.id.mostrarElementos:
                        if (Global.retiroIniciado) {
                            verificarElementosYTecnicoCargado();
                            if (Global.hayElementosCargados) {
                                startActivity(new Intent(EscanearCodigo.this, MostrarElementos.class));
                            } else {
                                Toast.makeText(EscanearCodigo.this, "No hay ningun elemento cargado", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(EscanearCodigo.this, "Inicie un retiro para ver los elementos cargados", Toast.LENGTH_LONG).show();
                        }
                        return true;

                    case R.id.escanearCodigo:
                        Toast.makeText(EscanearCodigo.this, "Usted ya se encuentra aca", Toast.LENGTH_LONG).show();
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


    private void mostrarAlertaIngElemManual() {
        if(editTextResultado.getText().toString().trim().equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(EscanearCodigo.this);
            builder.setMessage("Desea ingresar el codigo del elemento manualmente?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(EscanearCodigo.this, IngresarElemento.class));
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

            builder.show();
        }else{
            Global.codigo = editTextResultado.getText().toString().trim();
            startActivity(new Intent(EscanearCodigo.this,IngresarElemento.class));
        }
    }


    private void mostrarAlertaIniciarAccion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EscanearCodigo.this);
        builder.setMessage("Usted quiere iniciar la accion de retirar elementos de la filial, desea continuar con la operacion?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String hora, minuto;
                        hora = String.valueOf(((Calendar.getInstance().get(Calendar.HOUR_OF_DAY)<10)?"0"+Calendar.getInstance().get(Calendar.HOUR_OF_DAY):Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
                        minuto = String.valueOf(((Calendar.getInstance().get(Calendar.MINUTE)<10)?"0"+Calendar.getInstance().get(Calendar.MINUTE):Calendar.getInstance().get(Calendar.MINUTE)));
                        Global.horaInicioRetiro = hora + ":" + minuto + "hs";

                        botonScan.setEnabled(true);
                        botonIniciarRetiro.setEnabled(false);
                        botonIngresarDatosProd.setEnabled(true);
                        botonDatosTecnico.setEnabled(true);

                        Global.retiroIniciado=true;
                        Global.fechaRetiro = (Calendar.getInstance().get(Calendar.DATE) +
                                "-"+ (Calendar.getInstance().get(Calendar.MONTH)+1) +
                                "-"+ Calendar.getInstance().get(Calendar.YEAR));
                        Global.cantCajas=1;
                        Global.nroCaja="00000001";
                        Global.cajas = new ArrayList<>();

                        verificarElementosYTecnicoCargado();
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        botonScan.setEnabled(false);
                        borrarTexto.setEnabled(false);
                        Global.retiroIniciado=false;
                        dialogInterface.dismiss();
                    }
                });

        builder.show();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Esto me devuelve el codigo escaneado
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        //Lo que pasa si salgo o no del escaner osea la camara, si vuelvo y no escaneo me aparece un mensaje
        //sino me devuelve el codigo de lo escaneado
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Lectora cancelada", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                editTextResultado.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void guardarDatosServicio() {
        SharedPreferences preferences = getSharedPreferences("datosServicio", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("fechaRetiro",Global.fechaRetiro);
        editor.putString("horaInicioRetiro",Global.horaInicioRetiro);
        editor.putBoolean("terminoRetiro", Global.terminoRetiro);
        editor.putBoolean("retiroIniciado", Global.retiroIniciado);
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        pantallas[0] = true;
        actualizarEstadoCuenta();
    }
}