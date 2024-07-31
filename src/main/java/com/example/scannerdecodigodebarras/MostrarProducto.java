package com.example.scannerdecodigodebarras;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;

public class MostrarProducto extends AppCompatActivity implements FIrebaseLoadDone{

    private NavigationView navigationView;
    private EditText codigo, filial;
    private DatabaseReference databaseReference;
    private Button volver;
    private Boolean primeraVez=true;
    private FIrebaseLoadDone fIrebaseLoadDone;
    private List<Producto> productos;
    private SearchableSpinner searchableSpinner;


    private RecyclerView recyclerView;
    private AdaptadorMostrarProductos adaptador;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_producto);

        recyclerView = findViewById(R.id.recyclerViewMostrarProductos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        searchableSpinner = findViewById(R.id.edit_nombre_producto);
        codigo = findViewById(R.id.editCodigoElemento);
        filial = findViewById(R.id.edit_filial);
        navigationView = findViewById(R.id.mostrarProd_navigation);

        volver = (Button) findViewById(R.id.volver);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent button = new Intent (MostrarProducto.this, EscanearCodigo.class);
                startActivity(button);
                finish();
            }
        });

        // ACTIVAR O HACER QUE FUNCIONEN LOS BOTONES DEL MENU DESPLEGABLE DEL MENU
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem Item) {
                switch (Item.getItemId()) {
                    case R.id.escanearCodigo:
                        Intent scanearCodigo = new Intent(MostrarProducto.this, EscanearCodigo.class);
                        startActivity(scanearCodigo);
                        finish();
                        return true;

                    case R.id.mostrarProductos:
                        Toast.makeText(MostrarProducto.this, "Usted ya se encuentra aca", Toast.LENGTH_LONG).show();
                        return true;
                }
                return true;
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Productos");
        fIrebaseLoadDone = this;
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    List<Producto> productos = new ArrayList<>();

                    adaptador = new AdaptadorMostrarProductos(this, productos);

                    for (DataSnapshot productoSnapshot : snapshot.getChildren()){
                        productos.add(productoSnapshot.getValue(Producto.class));
                    }
                    fIrebaseLoadDone.onFirebaseLoadSuccess(productos);
                    adaptador.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                fIrebaseLoadDone.onFirebaseLoadFailed(error.getMessage());
            }
        });

        searchableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!primeraVez){
                    Producto producto = productos.get(i);
                    codigo.setText(producto.getCodigo());
                    filial.setText(producto.getFilial());
                }else{
                    primeraVez=false;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onFirebaseLoadSuccess(List<Producto> productosList) {
        productos = productosList;
        List<String> nombres = new ArrayList<>();

        //LO PUSE PARA QUE MUESTRE TODOS LOS PRODUCTOS
        nombres.add("Todos");  //-------------------------------------------------------- VER ESTO

        for (Producto producto : productosList){
            nombres.add(producto.getNombre());
        }

        ArrayAdapter<String> productosAdapter = new ArrayAdapter<String>(MostrarProducto.this, android.R.layout.simple_spinner_item, nombres);
        productosAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        searchableSpinner.setAdapter(productosAdapter);
    }

    @Override
    public void onFirebaseLoadFailed(String mensaje) {
        Toast.makeText(MostrarProducto.this, mensaje, Toast.LENGTH_LONG).show();
    }
}
