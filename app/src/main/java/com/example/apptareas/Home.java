package com.example.apptareas;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apptareas.Adaptadores.AdapterClass;
import com.example.apptareas.Modelos.Model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Home extends AppCompatActivity {

    //private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton btnFloat;

    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String onlineUserID;

    private ProgressDialog load;

    private String key = "";
    private String llamarTarea;
    private String llamarDesc;
    private String llamarEstado;

    ArrayList<Model> list;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*toolbar = findViewById(R.id.homeBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("App de Tareas");*/

        searchView = findViewById(R.id.searchBar);

        recyclerView = findViewById(R.id.rview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        load = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();
        onlineUserID = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Tareas").child(onlineUserID);

        btnFloat = findViewById(R.id.btnAddTask);
        btnFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarTarea();
            }
        });
    }

    private void agregarTarea() {
        AlertDialog.Builder miDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.input_file, null);
        miDialog.setView(view);

        AlertDialog dialog = miDialog.create();
        dialog.setCancelable(false);

        final EditText tarea = view.findViewById(R.id.etTask);
        final EditText descripcion = view.findViewById(R.id.etDescripcion);
        Button saveBtn =  view.findViewById(R.id.btnGuardar);
        Button cancelBtn = view.findViewById(R.id.btnCancel);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String etTarea = tarea.getText().toString().trim();
                String etDescripcion = descripcion.getText().toString().trim();
                String id = reference.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());
                Calendar calendario = Calendar.getInstance();

                int hora, minutos, segundos;
                hora = calendario.get(Calendar.HOUR_OF_DAY);
                minutos = calendario.get(Calendar.MINUTE);
                segundos = calendario.get(Calendar.SECOND);

                String hour;

                if (hora <= 12){
                    hour = hora + ":" + minutos + ":" + segundos + " am";
                }else{
                    hour = hora + ":" + minutos + ":" + segundos + " pm";
                }

                if (TextUtils.isEmpty(etTarea)){
                    tarea.setError("El correo electronico es requerido");
                    return;
                }
                if (TextUtils.isEmpty(etDescripcion)){
                    descripcion.setError("La contraseña es requerida");
                    return;
                }else{
                    load.setMessage("Agregando Tarea");
                    load.setCanceledOnTouchOutside(false);
                    load.show();

                    Model model = new Model(etTarea,id,etDescripcion,date,hour,"En proceso",user.getEmail());
                    reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                load.dismiss();
                                Toast.makeText(Home.this, "Tarea agregada correctamente", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }else {
                                load.dismiss();
                                String error = task.getException().toString();
                                Toast.makeText(Home.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
/*
        if (reference != null){
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        list = new ArrayList<>();
                        for (DataSnapshot dn : snapshot.getChildren()){
                            list.add(dn.getValue(Model.class));
                        }
                        AdapterClass adapterClass = new AdapterClass(list);
                        recyclerView.setAdapter(adapterClass);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Home.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (searchView !=null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return true;
                }
            });
        }*/




        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(reference, Model.class)
                .build();
        FirebaseRecyclerAdapter<Model, miViewHolder> adapter = new FirebaseRecyclerAdapter<Model, miViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull miViewHolder holder, int position, @NonNull Model model) {
                holder.setDate(model.getFecha());
                holder.setTarea(model.getTarea());
                holder.setDescripcion(model.getDescripcion());
                holder.setUser(model.getUsuario());
                holder.setStatus(model.getEstado());
                holder.setHour(model.getHora());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        key = getRef(holder.getAdapterPosition()).getKey();
                        llamarTarea = model.getTarea();
                        llamarDesc = model.getDescripcion();
                        llamarEstado = model.getEstado();
                        
                        updateTask();
                    }
                });
            }

            @NonNull
            @Override
            public miViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,parent,false);
                return new miViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    public static class miViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public miViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setTarea(String tarea){
            TextView textViewTarea = mView.findViewById(R.id.taskTv);
            textViewTarea.setText(tarea);
        }
        public void setDescripcion(String descripcion){
            TextView textViewDescripcion = mView.findViewById(R.id.descriptionTv);
            textViewDescripcion.setText(descripcion);
        }
        public void setDate(String date){
            TextView textViewDate = mView.findViewById(R.id.dateTv);
            textViewDate.setText(date);
        }
        public void setUser(String user){
            TextView textViewUser = mView.findViewById(R.id.userTv);
            textViewUser.setText(user);
        }
        public void setStatus(String status){
            TextView textViewStatus = mView.findViewById(R.id.statusTv);
            textViewStatus.setText(status);
        }
        public void setHour(String hour){
            TextView textViewHour = mView.findViewById(R.id.hourTv);
            textViewHour.setText(hour);
        }
    }

    private void updateTask() {
        AlertDialog.Builder miDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.update_date,null);
        miDialog.setView(v);

        AlertDialog dialog = miDialog.create();

        TextView miTarea = v.findViewById(R.id.editTaskTv);
        TextView miDesc = v.findViewById(R.id.editDescriptionTv);

        miTarea.setText(llamarTarea);

        miDesc.setText(llamarDesc);

        Button deleteTaskBtn = v.findViewById(R.id.deleteTaskBtn);
        Button updateTaskBtn = v.findViewById(R.id.updateTaskBtn);

        updateTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String date = DateFormat.getDateInstance().format(new Date());
                Calendar calendario = Calendar.getInstance();

                int hora, minutos, segundos;
                hora = calendario.get(Calendar.HOUR_OF_DAY);
                minutos = calendario.get(Calendar.MINUTE);
                segundos = calendario.get(Calendar.SECOND);

                String hour;

                if (hora <= 12){
                    hour = hora + ":" + minutos + ":" + segundos + " am";
                }else{
                    hour = hora + ":" + minutos + ":" + segundos + " pm";
                }

                Model model = new Model(llamarTarea, key,llamarDesc,date,hour,"Completada",user.getEmail());

                reference.child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Home.this, "Tarea Completada con exito", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }else {
                            String error = task.getException().toString();
                            Toast.makeText(Home.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        deleteTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Home.this, "Tarea Eliminada", Toast.LENGTH_SHORT).show();
                        }else {
                            String error = task.getException().toString();
                            Toast.makeText(Home.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logOut:
                auth.signOut();
                Intent intent = new Intent(Home.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void search(String str){
        ArrayList<Model> miLista = new ArrayList<>();
        for (Model object : list){
            if(object.getTarea().toLowerCase().contains(str.toLowerCase())){
                miLista.add(object);
            }
        }
        AdapterClass adapterClass = new AdapterClass(miLista);
        recyclerView.setAdapter(adapterClass);
    }
}