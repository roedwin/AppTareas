package com.example.apptareas.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptareas.Modelos.Model;
import com.example.apptareas.R;

import java.util.ArrayList;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.myViewHolder> {

    ArrayList<Model> list;

    public AdapterClass(ArrayList<Model> list){
        this.list = list;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.tarea.setText(list.get(position).getTarea());
        holder.estado.setText(list.get(position).getEstado());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView tarea, estado;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tarea = itemView.findViewById(R.id.taskTv);
            estado = itemView.findViewById(R.id.statusTv);
        }
    }
}
