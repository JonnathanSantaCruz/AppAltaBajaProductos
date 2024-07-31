package com.example.scannerdecodigodebarras;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;


import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

public class IngresarProducto extends AppCompatActivity {

    private Button volver, agregarElemento;
    private DatabaseReference productos;
    private EditText editCodigoBarra;
    private SearchableSpinner editFilial, editNombreElemento;
    private NavigationView navigationView;
    private Producto producto;

    //ARRAYS SPINNERS
    String[] nombresProductos = {"CPU", "Monitor", "Impresora", "DVR", "Lector de codigos de barras", "Ticketera"};
    Integer[] filiales = new Integer[250];

    private boolean existe, primeraCargaProd=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_producto);

        for (int i = 1; i<=250; i++){
            filiales[i-1] = i;
        }

        navigationView = findViewById(R.id.navigation);
        editCodigoBarra = findViewById(R.id.editCodigoElemento);
        editNombreElemento = findViewById(R.id.edit_nombre_producto);
        editFilial = findViewById(R.id.edit_filial);

        editCodigoBarra.setText(Global.codigo);
        editCodigoBarra.setEnabled(false);

        productos = FirebaseDatabase.getInstance().getReference("Productos");

        volver = (Button) findViewById(R.id.volver);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent button = new Intent (IngresarProducto.this, EscanearCodigo.class);
                startActivity(button);
                finish();
            }
        });

        cargarSpinner();

        agregarElemento = (Button) findViewById(R.id.agregar_elemento);
        agregarElemento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarProducto();
            }
        }
        );

        // ACTIVAR O HACER QUE FUNCIONEN LOS BOTONES DEL MENU DESPLEGABLE DEL MENU
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem Item) {
                switch (Item.getItemId()) {
                    case R.id.escanearCodigo:
                        Intent EscanearCodigo = new Intent(IngresarProducto.this, EscanearCodigo.class);
                        startActivity(EscanearCodigo);
                        finish();
                        return true;

                    case R.id.mostrarProductos:
                        Intent mostrarProducto = new Intent(IngresarProducto.this, MostrarProducto.class);
                        startActivity(mostrarProducto);
                        finish();
                        return true;
                }
                return true;
            }
        });
    }

    private void agregarProducto() {
        String nombre = editNombreElemento.getSelectedItem().toString();
        String codigo = editCodigoBarra.getText().toString().trim();
        String filial = editFilial.getSelectedItem().toString();

        producto = new Producto(nombre, codigo, filial);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Productos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!snapshot.exists() && !primeraCargaProd){
                    productos.push().setValue(producto);
                    mostrarMensaje(false);
                    primeraCargaProd=true;
                }else {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            Producto codProducto = dataSnapshot.getValue(Producto.class);

                            String codProd = codProducto.getCodigo();

                            if (codProd.equals(codigo)) {
                                existe = true;
                            }
                        }

                        if(existe){
                            mostrarMensaje(true);
                        }else{
                            mostrarMensaje(false);
                            Global.idFilial = filial;
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void mostrarMensaje(boolean existe){
        if(!existe) {
            productos.push().setValue(producto);
            Toast.makeText(this, "Producto agregado exitosamente", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "El producto ya existe", Toast.LENGTH_SHORT).show();
        }

        Intent button = new Intent (IngresarProducto.this, EscanearCodigo.class);
        startActivity(button);
        finish();
    }

    public void cargarSpinner(){
        // CARGAR NOMBRES EN LOS SPINNERS
        ArrayAdapter<String> nombreProductosAdapter = new ArrayAdapter<String>(IngresarProducto.this, android.R.layout.simple_spinner_item, nombresProductos);
        nombreProductosAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        editNombreElemento.setAdapter(nombreProductosAdapter);

        ArrayAdapter<Integer> filialesAdapter = new ArrayAdapter<Integer>(IngresarProducto.this, android.R.layout.simple_spinner_item, filiales);
        filialesAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        editFilial.setAdapter(filialesAdapter);
    }




}