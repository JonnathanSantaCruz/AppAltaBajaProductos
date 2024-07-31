package com.example.scannerdecodigodebarras;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("NotifyDataSetChanged")
public class MostrarCaja extends General {

    private AdaptadorMostrarCaja adaptadorMostrarCaja;
    private FirebaseFirestore firebaseFirestore;
    private int nroActCaja;
    private String nroFilial = "";
    private ArrayList<Elemento> elementos;
    private ProgressDialog dialog;

    private CheckBox checkBoxDonacion;

    private boolean retiroParaDonacion;

    public MostrarCaja(){
        if(!Global.pantallasCreadas[4]){
            Global.pantallasCreadas[4]=true;
            cantPantallasCreadas++;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_caja);

        checkBoxDonacion = findViewById(R.id.CheckBoxDonacionONo);
        checkBoxDonacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBoxDonacion.isChecked()){
                    retiroParaDonacion = true;
                }else{
                    retiroParaDonacion = false;
                }
            }
        });

        if(!verificarAccesoArchivos()){
            requestPermissions();
        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        RecyclerView datosCaja = findViewById(R.id.recyclerViewMostrarCajas);
        datosCaja.setLayoutManager(new LinearLayoutManager(this));

        adaptadorMostrarCaja = new AdaptadorMostrarCaja(getElemSeleccionados());
        adaptadorMostrarCaja.notifyDataSetChanged();
        datosCaja.setAdapter(adaptadorMostrarCaja);

        verificarElementosYTecnicoCargado();

        Button eliminarElementos = findViewById(R.id.eliminarElemMostrarCaja);
        eliminarElementos.setOnClickListener(view -> {

            // ELIMINO LOS ELEMENTOS SELECCIONADOS
            for (Elemento elemento : elementos){
                if(elemento.estaSeleccionado()){
                    elementos.remove(elemento);
                }
            }

            adaptadorMostrarCaja.notifyDataSetChanged();
        });


        Button volver = findViewById(R.id.volverMostrarCaja);
        volver.setOnClickListener(view -> {
            startActivity(new Intent(MostrarCaja.this, OrdenarCajas.class));
        });

        Button cerrarCaja = findViewById(R.id.cerrarCajaMostrarCaja);
        cerrarCaja.setOnClickListener(view -> {
            if(elementos.isEmpty()){
                Toast.makeText(this, "Ha eliminado todos los elementos de la caja", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MostrarCaja.this, OrdenarCajas.class));
                finish();
            }else{
                verificarCajaExiste();
            }

        });
    }

    private void guardarDatosServicio() {
        SharedPreferences preferences = getSharedPreferences("datosServicio", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("terminoRetiro", Global.terminoRetiro);
        editor.commit();
    }

    private void verificarCajaExiste() {

        //ESTO LE DA EL VALOR A LA FILIAL OSEA SI LA FILIAL ES 1 ENTONCES COMPLETA LOS ESPACIOS FALTANTES CON CEROS
        if(Global.nroFilial < 10){
            nroFilial = "000"+Global.nroFilial;
        }else if(Global.nroFilial < 100){
            nroFilial = "00"+Global.nroFilial;
        } else if(Global.nroFilial < 1000){
            nroFilial = "0"+Global.nroFilial;
        } else {
            nroFilial = String.valueOf(Global.nroFilial);
        }
        Global.codCaja = ("F"+nroFilial+"C"+ Global.nroCaja);

        //ME FIJO SI EXISTE LA CAJA CON NRO 00000001
        //SI EXISTE LE AUMENTO EL NRO DE LA CAJA
        //SI NO EXISTE LA CREO
        firebaseFirestore.collection(Global.usuario.getEmail())
                .document("cajas retiradas").collection("cajas").document(Global.codCaja)
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            // ACTUALIZO EL NRO DE LA CAJA, YA SABIENDO EL NRO DE CAJA MAS GRANDE
                            // SOLO LE SUMO UNO Y COMPLETO LO ESPACIOS CON CEROS
                            actualizarNroCaja();
                        }else{
                            Global.codCaja = ("F"+nroFilial+"C"+ Global.nroCaja);

                            mostrarAlerta();
                        }
                    }
                });
    }

    private void actualizarNroCaja() {
        nroActCaja = Integer.parseInt(Global.nroCaja)+1;

        if(nroActCaja < 10){
            Global.nroCaja = "0000000"+nroActCaja;
        }
        else if(nroActCaja < 100){
            Global.nroCaja = "000000"+nroActCaja;
        }
        else if(nroActCaja < 1000){
            Global.nroCaja = "00000"+nroActCaja;
        }
        else if(nroActCaja < 10000){
            Global.nroCaja = "0000"+nroActCaja;
        }
        else if(nroActCaja < 100000){
            Global.nroCaja = "000"+nroActCaja;
        }
        else if(nroActCaja < 1000000){
            Global.nroCaja = "00"+nroActCaja;
        }
        else if(nroActCaja < 10000000){
            Global.nroCaja = "0"+nroActCaja;
        }
        else {
            Global.nroCaja = String.valueOf(nroActCaja);
        }

        verificarCajaExiste();
    }

    private void mostrarMensajeYEjecutarAccion(int nroMsj){
        //ACA LO QUE HAGO ES MOSTRAR EL MENSAJE CORRESPONDIENTE Y PARA ASEGURAR QUE EL USUARIO VEA EL MENSAJE QUE APRETE UN BOTON
        //PARA CONFIRMAR QUE LO LEYO, Y PARA QUE NO HAYA NINGUN ERROR QUE ME EJECUTE EL RESTO DEL CODIGO CUANDO LEA EL MENSAJE

        AlertDialog.Builder builder = new AlertDialog.Builder(MostrarCaja.this);
        switch (nroMsj){

            case 1:
                builder.setMessage("Ya se han cargado todas las cajas en la base de datos exitosamente. Podra ver los PDFs en Descargas")
                        .setPositiveButton("Ok", (dialogInterface, i) -> {

                            //ELIMINO LOS ELEMENTOS QUE NO SE AGREGARON A UNA CAJA, OSEA LOS QUE SOBRARON
                            firebaseFirestore.collection(Global.usuario.getEmail()).document(Global.fechaRetiro).collection("cajas")
                                    .document(Global.horaInicioRetiro).collection("elementos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if(!queryDocumentSnapshots.isEmpty()){
                                                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                                                    firebaseFirestore.collection(Global.usuario.getEmail()).document(Global.fechaRetiro).collection("cajas")
                                                            .document(Global.horaInicioRetiro).collection("elementos").document(documentSnapshot.getId()).delete();
                                                }
                                            }
                                        }
                                    });


                            //RECORRO EL ARRAY CON LAS CAJAS CARGADAS PARA ACTUALIZARLES UN DATO (LA HORA EN EL QUE TERMINO EL RETIRO)
                            for (Caja caja : Global.cajas){

                                String hora, minuto;
                                hora = String.valueOf(((Calendar.getInstance().get(Calendar.HOUR_OF_DAY)<10)?"0"+Calendar.getInstance().get(Calendar.HOUR_OF_DAY):Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
                                minuto = String.valueOf(((Calendar.getInstance().get(Calendar.MINUTE)<10)?"0"+Calendar.getInstance().get(Calendar.MINUTE):Calendar.getInstance().get(Calendar.MINUTE)));
                                Global.horaFinRetiro = hora + ":" + minuto + "hs";

                                generarPdf(caja);

                                Map<String, Object> datoCaja = new HashMap<>();
                                datoCaja.put("hora fin retiro", Global.horaFinRetiro);
                                datoCaja.put("estado", ((retiroParaDonacion)?"BAJA-DONACION":"FINALIZADO-CAJ"));
                                datoCaja.put("numero implementacion", Global.nroImplementacion);
                                datoCaja.put("responsable", Global.responsableRetiro);

                                firebaseFirestore.collection(Global.usuario.getEmail()).document("cajas retiradas")
                                        .collection("cajas").document(caja.getId()).update(datoCaja);

                                firebaseFirestore.collection(Global.usuario.getEmail()).document(Global.fechaRetiro)
                                        .collection("cajas").document(Global.horaInicioRetiro).update(datoCaja);
                            }

                            //ACA HAGO ESTO PARA QUE CUANDO VAYA AL SERVICIO NO ME ELIMINE LOS DATOS DE LAS CAJAS QUE AGREGUE
                            Global.retiroIniciado=false;
                            Global.cuentaUsada=false;
                            Global.hayElementosCargados=false;
                            Global.horaFinRetiro=null;
                            Global.terminoRetiro = true;

                            guardarDatosServicio();

                            //PARO EL SERVICIO
                            stopService(new Intent(MostrarCaja.this, ServicioEstadoCuenta.class));
                            startActivity(new Intent(MostrarCaja.this, IniciarSesion.class));
                            finish();
                        });
                builder.show();

                break;

            case 2:
                builder.setMessage("No hay mas elementos para retirar, por ende, se han cargado todas las cajas en la base de datos exitosamente. Podra ver los PDFs en Descargas")
                        .setPositiveButton("Ok", (dialogInterface, i) -> {

                            //ACA HAGO ESTO PARA QUE CUANDO VAYA AL SERVICIO NO ME ELIMINE LOS DATOS DE LAS CAJAS QUE AGREGUE
                            Global.retiroIniciado=false;
                            Global.cuentaUsada=false;
                            Global.hayElementosCargados=false;
                            Global.horaFinRetiro=null;
                            Global.terminoRetiro = true;

                            guardarDatosServicio();

                            //PARO EL SERVICIO
                            stopService(new Intent(MostrarCaja.this, ServicioEstadoCuenta.class));
                            startActivity(new Intent(MostrarCaja.this, IniciarSesion.class));
                            finish();
                        });

                builder.show();
                break;

        }

    }

    private void mostrarAlerta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MostrarCaja.this);
        builder.setMessage("Si cierra la caja no la podra modificar. Desea cerrar la caja?")
                .setPositiveButton("Si", (dialogInterface, i) -> {
                    dialog = ProgressDialog.show(this,"", "Verificando datos...");

                    // CREO LA CAJA Y LE MANDO SU CODIGO DE CAJA Y LOS ELEMENTOS QUE ESTABAN SELECCIONADOS
                    // LO AÑADO A UN ARRAY ESTATICO PARA QUE AL FINALIZAR EL RETIRO LO PUEDA AÑADIR A LA BD
                    Caja caja = new Caja(Global.codCaja, elementos);
                    Global.cajas.add(caja);

                    // RECORRO EL ARRAY Y ELIMINO LOS ELEMENTOS DE LA CAJA
                    for (Elemento elemento: caja.getElementos()){
                            // ACA ELIMINO LOS ELEMENTOS QUE ESTAN EN LA CAJA
                            firebaseFirestore.collection(Global.usuario.getEmail())
                                    .document(Global.fechaRetiro).collection("cajas")
                                    .document(Global.horaInicioRetiro).collection("elementos").document(elemento.getCodigo())
                                    .delete();
                    }
                    cargarCaja(caja);
                    dialog.dismiss();
                    verificarNroCaja();

                    dialogInterface.dismiss();
                }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());

        builder.show();
    }

    private void verificarNroCaja() {
        //SI HAY MAS ELEMENTOS DEBO VERIFICAR SI ES LA ULTIMA CAJA O HAY MAS
        //TAMBIEN TENGO QUE VERIFICAR QUE SI YA CARGO LA ULTIMA CAJA, QUE NO HAYA MAS ELEMENTOS
        if(Integer.parseInt(Global.tecnico.getCajas()) == Global.cantCajas){

            mostrarMensajeYEjecutarAccion(1);

        }else{
            // ME FIJO SI SIGUE HABIENDO ELEMENTOS PARA RETIRAR
            // SI HAY VUELVO PARA QUE LAS CARGUE EN LA SIGUIENTE CAJA (OBVIAMENTE SI NO ES LA ULTIMA CAJA)
            // SINO FINALIZO EL RETIRO
            firebaseFirestore.collection(Global.usuario.getEmail()).document(Global.fechaRetiro)
                    .collection("cajas").document(Global.horaInicioRetiro)
                    .collection("elementos").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()){
                                Global.cantCajas++;
                                startActivity(new Intent(MostrarCaja.this, OrdenarCajas.class));

                            }else{
                                //LE ACTUALIZO EL DATO DE CUANDO SE TERMINA EL RETIRO
                                for (Caja caja : Global.cajas){
                                    String hora, minuto;
                                    hora = String.valueOf(((Calendar.getInstance().get(Calendar.HOUR_OF_DAY)<10)?"0"+Calendar.getInstance().get(Calendar.HOUR_OF_DAY):Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
                                    minuto = String.valueOf(((Calendar.getInstance().get(Calendar.MINUTE)<10)?"0"+Calendar.getInstance().get(Calendar.MINUTE):Calendar.getInstance().get(Calendar.MINUTE)));
                                    Global.horaFinRetiro = hora + ":" + minuto + "hs";

                                    generarPdf(caja);

                                    Map<String, Object> datoCaja = new HashMap<>();
                                    datoCaja.put("hora fin retiro", Global.horaFinRetiro);
                                    datoCaja.put("estado", ((retiroParaDonacion)?"BAJA-DONACION":"FINALIZADO-CAJ"));
                                    datoCaja.put("numero implementacion", Global.nroImplementacion);
                                    datoCaja.put("responsable", Global.responsableRetiro);

                                    firebaseFirestore.collection(Global.usuario.getEmail()).document("cajas retiradas")
                                            .collection("cajas").document(caja.getId()).update(datoCaja);
                                }

                                mostrarMensajeYEjecutarAccion(2);
                            }
                        }
                    });
        }
    }

    private void cargarCaja(Caja caja){
        //ACA CARGO LOS ELEMENTOS EN LA CAJA
        for (Elemento elemento : caja.getElementos()){
            Map<String, Object> dato = new HashMap<>();
            dato.put("nombre", elemento.getNombre());
            dato.put("codigo", elemento.getCodigo());
            dato.put("filial", elemento.getFilial());

            firebaseFirestore.collection(Global.usuario.getEmail()).document("cajas retiradas")
                    .collection("cajas").document(caja.getId())
                    .collection("elementos")
                    .document(elemento.getCodigo()).set(dato);

            firebaseFirestore.collection(Global.usuario.getEmail()).document(Global.fechaRetiro)
                    .collection("cajas").document(Global.horaInicioRetiro)
                    .collection(caja.getId()).document(elemento.getCodigo()).set(dato);
        }

        // LE AGREGO AL DOCUMENTO DE LA CAJA UNOS DATOS
        // PARA QUE EL FIREBASE AL HACER CONSULTA ME LO TOME COMO QUE EXISTA
        Map<String, Object> datoCaja = new HashMap<>();
        datoCaja.put("fecha retiro", Global.fechaRetiro);
        datoCaja.put("hora inicio retiro", Global.horaInicioRetiro);
        datoCaja.put("hora fin retiro", "");
        datoCaja.put("estado", "");
        datoCaja.put("numero implementacion", "");
        datoCaja.put("responsable", "");

        firebaseFirestore.collection(Global.usuario.getEmail()).document("cajas retiradas")
                .collection("cajas").document(caja.getId()).set(datoCaja);

        firebaseFirestore.collection(Global.usuario.getEmail()).document(Global.fechaRetiro)
                .collection("cajas").document(Global.horaInicioRetiro).set(datoCaja);
    }

    private ArrayList<Elemento> getElemSeleccionados() {


        // QUIERO QUE SOLO ME CARGUE EL ARRAY elementos CUANDO SEA LA PRIMERA VEZ
        // YA QUE SI ELIMINO ELEMENTOS NO ME VA A CARGAR NADA PORQUE LOS ELEMENTOS DEL ARRAY arrayElemCargados
        // LOS PUSE EN FALSE
        if(elementos==null){
            elementos = new ArrayList<>();

            for (Elemento elemento : Global.arrayElemCargados){
                if(elemento.estaSeleccionado()){
                    elementos.add(elemento);
                    elemento.setSeleccionado(false);
                }
            }
        }


        return elementos;
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



    //ESTAS FUNCIONES HACEN QUE FUNCIONE LA CREACION DEL PDF
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 200);
    }
    private boolean verificarAccesoArchivos() {
        int permiso1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permiso2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return permiso1 == PackageManager.PERMISSION_GRANTED && permiso2 == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==200){
            if(grantResults.length > 0){
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if(writeStorage && readStorage){
                    Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void generarPdf(Caja caja) {
        String tituloText = caja.getId();

        PdfDocument pdfDocument = new PdfDocument();
        TextPaint titulo = new TextPaint();
        TextPaint destino = new TextPaint();
        TextPaint filial = new TextPaint();
        TextPaint fechaRetiro = new TextPaint();
        TextPaint horaFinRetiro = new TextPaint();
        TextPaint noAbrirCaja = new TextPaint();


        //((Global.cajas.size()==1)?1:Global.cajas.size())
        PdfDocument.PageInfo paginaInfo = new PdfDocument.PageInfo.Builder(1122, 794, 1).create();

        PdfDocument.Page pagina = pdfDocument.startPage(paginaInfo);
        Canvas canvas = pagina.getCanvas();

        //ACA DIBUJO EL NUMERO DE LA CAJA
        titulo.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titulo.setTextSize(90);
        canvas.drawText(tituloText, 200, 350, titulo);

        //ACA DIBUJO UN STRING
        noAbrirCaja.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        noAbrirCaja.setTextSize(100);
        canvas.drawText("NO ABRIR ESTA CAJA!!!", 25, 500, noAbrirCaja);

        //ACA DIBUJO EL DESTINO DE LA CAJA
        destino.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        destino.setTextSize(40);

        //SI ES PARA DONACION ENTONCES PONGO DONACION, SINO PONGO DEPOSITO DE MUNRO COMO DESTINO
        if(retiroParaDonacion){
            canvas.drawText("Destino: " + "Donacion", 20, 700, destino);
        }else{
            canvas.drawText("Destino: " + "Deposito Munro", 20, 700, destino);
        }

        //ACA DIBUJO LA FECHA DEL RETIRO
        fechaRetiro.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        fechaRetiro.setTextSize(40);
        canvas.drawText("Fecha retiro: " + Global.fechaRetiro, 20, 740, fechaRetiro);

        //ACA DIBUJO LA HORA EN LA QUE SE TERMINO EL RETIRO
        horaFinRetiro.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        horaFinRetiro.setTextSize(40);
        canvas.drawText("Hora fin del retiro: " + Global.horaFinRetiro, 20, 780, horaFinRetiro);

        //ACA DIBUJO LA FILIAL DE DONDE VIENE LA CAJA
        filial.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        filial.setTextSize(40);
        canvas.drawText("Filial: " + "Nro " + Global.filial, 20, 660, filial);


        //CIERRO LA PAGINA
        pdfDocument.finishPage(pagina);

        File file = new File(Environment.getExternalStoragePublicDirectory(DOWNLOAD_SERVICE), caja.getId()+".pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        }catch (Exception e){
            e.printStackTrace();
        }
        pdfDocument.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //EMPIEZA A ESCUCHAR POR SI SE AGREGAN MAS DATOS
        adaptadorMostrarCaja.notifyDataSetChanged();

        pantallas[5] = false;
        actualizarEstadoCuenta();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //LA FUNCION SE PARA, PARA NO ESCUCHAR POR SI SE AGREGAN MAS DATOS
        adaptadorMostrarCaja.notifyDataSetChanged();

        pantallas[5] = true;
        actualizarEstadoCuenta();
    }
}