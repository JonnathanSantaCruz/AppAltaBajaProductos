package com.example.scannerdecodigodebarras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AdaptadorMostrarElementos extends FirestoreRecyclerAdapter<Elemento, AdaptadorMostrarElementos.ViewHolder> {

    public AdaptadorMostrarElementos(@NonNull FirestoreRecyclerOptions<Elemento> options){
        super(options);
        Global.arrayElemCargados = options.getSnapshots();
    }

    @Override
    public int getItemCount() {
        return Global.arrayElemCargados.size();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Elemento elemento) {
        //ACCEDO A LOS TEXTVIEW DEL LAYOUT Y LES CAMBIO EL TEXTO PONIENDOLES EL DEL ELEMENTO DE LA BD

        Elemento element = Global.arrayElemCargados.get(position);

        holder.codigo.setText("Codigo: " + elemento.getCodigo());
        holder.nombre.setText("Nombre: " + elemento.getNombre());
        holder.filial.setText("Filial: " + elemento.getFilial());
        holder.checkBox.setChecked(element.estaSeleccionado());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ACA "CONECTO" EL ADAPTADOR CON EL LAYOUT
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adaptador_mostrar_elem, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView codigo, nombre, filial;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            codigo = itemView.findViewById(R.id.textCodigo);
            nombre = itemView.findViewById(R.id.textNombre);
            filial = itemView.findViewById(R.id.textFilial);
            checkBox = itemView.findViewById(R.id.CheckBoxMostrarElem);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Global.arrayElemCargados.get(getAdapterPosition()).estaSeleccionado()){
                        checkBox.setChecked(false);
                        Global.arrayElemCargados.get(getAdapterPosition()).setSeleccionado(false);
                    }else{
                        checkBox.setChecked(true);
                        Global.arrayElemCargados.get(getAdapterPosition()).setSeleccionado(true);
                    }
                    notifyDataSetChanged();
                }
            });

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Global.arrayElemCargados.get(getAdapterPosition()).estaSeleccionado()){
                        checkBox.setChecked(false);
                        Global.arrayElemCargados.get(getAdapterPosition()).setSeleccionado(false);
                    }else{
                        checkBox.setChecked(true);
                        Global.arrayElemCargados.get(getAdapterPosition()).setSeleccionado(true);
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }
}
