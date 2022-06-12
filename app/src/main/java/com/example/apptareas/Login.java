package com.example.apptareas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class Login extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText loginEmail, loginPass;
    private Button loginButton;
    private TextView goToRegister;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        load = new ProgressDialog(this);

        /*toolbar = findViewById(R.id.loginBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Iniciar Sesion");*/

        /*if (firebaseAuth!=null){
            Intent intent = new Intent(Login.this, Home.class);
            startActivity(intent);
        }else {

        }*/


        loginEmail = findViewById(R.id.etCorreo);
        loginPass = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.btnLogin);
        goToRegister = findViewById(R.id.txtRegister);

        goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo = loginEmail.getText().toString();
                String password = loginPass.getText().toString();

                if (TextUtils.isEmpty(correo)){
                    loginEmail.setError("El correo electronico es requerido");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    loginPass.setError("La contraseña es requerida");
                    return;
                }else {
                    load.setMessage("Inicio de sesion en progreso");
                    load.setCanceledOnTouchOutside(false);
                    load.show();
                    firebaseAuth.signInWithEmailAndPassword(correo,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (user.isEmailVerified()){
                                    load.dismiss();
                                    Intent intent = new Intent(Login.this, Home.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    load.dismiss();
                                    Toast.makeText(Login.this, "Correo no verificado", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                load.dismiss();
                                String error = task.getException().toString();
                                Toast.makeText(Login.this, "Error en el registro: "+error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}