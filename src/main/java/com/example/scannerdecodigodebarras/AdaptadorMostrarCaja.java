package com.example.scannerdecodigodebarras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class AdaptadorMostrarCaja extends RecyclerView.Adapter<AdaptadorMostrarCaja.AdaptadorMostrarCajaViewHolder> {

    private ArrayList<Elemento> elementos;

    public AdaptadorMostrarCaja (ArrayList<Elemento> elementos){
        this.elementos = elementos;
    }

    @NonNull
    @Override
    public AdaptadorMostrarCajaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ACA "CONECTO" EL ADAPTADOR CON EL LAYOUT
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adaptador_mostrar_cajas, parent, false);
        return new AdaptadorMostrarCajaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorMostrarCajaViewHolder holder, int position) {
        Elemento elem = elementos.get(position);

        holder.codigo.setText("Codigo: " + elem.getCodigo());
        holder.nombre.setText("Nombre: " + elem.getNombre());
        holder.filial.setText("Filial: " + elem.getFilial());
        holder.checkBox.setChecked(elem.estaSeleccionado());
    }

    @Override
    public int getItemCount() {
        return elementos.size();
    }

    public class AdaptadorMostrarCajaViewHolder extends RecyclerView.ViewHolder{
        TextView nombre, codigo, filial;
        CheckBox checkBox;
        public AdaptadorMostrarCajaViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.textNombreMostrarCaja);
            codigo = itemView.findViewById(R.id.textCodigoMostrarCaja);
            filial = itemView.findViewById(R.id.textFilialMostrarCaja);
            checkBox = itemView.findViewById(R.id.CheckBoxMostrarCaja);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(elementos.get(getAdapterPosition()).estaSeleccionado()){
                        checkBox.setChecked(false);
                        elementos.get(getAdapterPosition()).setSeleccionado(false);
                    }else{
                        checkBox.setChecked(true);
                        elementos.get(getAdapterPosition()).setSeleccionado(true);
                    }
                    notifyDataSetChanged();
                }
            });

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(elementos.get(getAdapterPosition()).estaSeleccionado()){
                        checkBox.setChecked(false);
                        elementos.get(getAdapterPosition()).setSeleccionado(false);
                    }else{
                        checkBox.setChecked(true);
                        elementos.get(getAdapterPosition()).setSeleccionado(true);
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

}