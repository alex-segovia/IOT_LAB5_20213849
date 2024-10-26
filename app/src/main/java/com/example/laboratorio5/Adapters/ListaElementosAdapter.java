package com.example.laboratorio5.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laboratorio5.Objetos.ElementoDTO;
import com.example.laboratorio5.R;

import java.util.List;

public class ListaElementosAdapter extends RecyclerView.Adapter<ListaElementosAdapter.ElementoViewHolder>{
    private List<ElementoDTO> listaElementos;
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<ElementoDTO> getListaElementos() {
        return listaElementos;
    }

    public void setListaElementos(List<ElementoDTO> listaElementos) {
        this.listaElementos = listaElementos;
    }

    @NonNull
    @Override
    public ElementoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.elemento,parent,false);
        return new ElementoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElementoViewHolder holder, int position) {
        ElementoDTO elementoDTO = listaElementos.get(position);
        holder.elemento = elementoDTO;

        TextView textoNombreElemento = holder.itemView.findViewById(R.id.nombreElemento);
        textoNombreElemento.setText(elementoDTO.getNombre());

        TextView textoCaloriasElemento = holder.itemView.findViewById(R.id.caloriasElemento);
        textoCaloriasElemento.setText(elementoDTO.getCalorias().toString()+" cal");
    }

    @Override
    public int getItemCount() {
        return listaElementos.size();
    }

    public class ElementoViewHolder extends RecyclerView.ViewHolder{
        ElementoDTO elemento;
        public ElementoViewHolder(@NonNull View item){
            super(item);
        }
    }
}
