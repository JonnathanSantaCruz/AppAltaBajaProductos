package com.example.scannerdecodigodebarras;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorMostrarProductos extends RecyclerView.Adapter<AdaptadorMostrarProductos.MyViewHolder> {

    Context context;
    ArrayList<Producto> productos;

    public AdaptadorMostrarProductos(ValueEventListener context, List<Producto> productos) {
        this.context = (Context) context;
        this.productos = (ArrayList<Producto>) productos;
    }

    @NonNull
    @Override
    public AdaptadorMostrarProductos.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.producto, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorMostrarProductos.MyViewHolder holder, int position) {

        Producto producto = productos.get(position);
        holder.nombre.setText(producto.getNombre());
        holder.codigo.setText(producto.getCodigo());
        holder.filial.setText(producto.getFilial());
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nombre, codigo, filial;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombreElementoMostrar);
            codigo = itemView.findViewById(R.id.codigoBarraMostrar);
            filial = itemView.findViewById(R.id.filialElementoMostrar);

        }
    }
}
