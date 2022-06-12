package com.example.apptareas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText registerEmail, registerPass,registerUser;
    private Button registerButton;
    private TextView goToLogin;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private ProgressDialog load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        load = new ProgressDialog(this);

        /*toolbar = findViewById(R.id.registerBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registrate");*/

        registerEmail = findViewById(R.id.etRegisterCorreo);
        registerPass = findViewById(R.id.etRegisterPassword);
        registerButton = findViewById(R.id.btnRegister);
        goToLogin = findViewById(R.id.txtLogin);
        registerUser = findViewById(R.id.etRegisterUser);

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo = registerEmail.getText().toString();
                String pass = registerPass.getText().toString();
                String usuario = registerUser.getText().toString();

                if (TextUtils.isEmpty(correo)){
                    registerEmail.setError("El correo electronico es requerido");
                    return;
                }
                if (TextUtils.isEmpty(pass)){
                    registerPass.setError("La contraseña es requerida");
                    return;
                }else{
                    load.setMessage("Registro en progreso");
                    load.setCanceledOnTouchOutside(false);
                    load.show();
                    firebaseAuth.createUserWithEmailAndPassword(correo, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                user.sendEmailVerification();
                                load.dismiss();
                                Intent intent = new Intent(Register.this, Login.class);
                                startActivity(intent);
                                finish();
                            }else {
                                load.dismiss();
                                String error = task.getException().toString();
                                Log.i("Error al registrar:", error);
                                Toast.makeText(Register.this, "Error en el registro: "+error, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        });
    }
}