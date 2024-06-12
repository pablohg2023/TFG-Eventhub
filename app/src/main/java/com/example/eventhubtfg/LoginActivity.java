package com.example.eventhubtfg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventhubtfg.R.id;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etContraseña;
    private Button btnIniciarSesion, btnRegistrarse;
    TextView forgotPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.txtEmail);
        etContraseña = findViewById(R.id.txtPassword);
        btnIniciarSesion = findViewById(R.id.btnLogin);
        btnRegistrarse = findViewById(R.id.btnRegister);
        forgotPassword = findViewById(id.txtForgotPassword);

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String contraseña = etContraseña.getText().toString();

                if(email.isEmpty() || contraseña.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor, introduce tu correo electrónico y contraseña.", Toast.LENGTH_SHORT).show();
                } else {
                    iniciarSesion(email, contraseña);
                }
            }
        });

        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resgisterPage = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(resgisterPage);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.fragment_forgot, null);
                EditText emailBox = dialogView.findViewById(id.editTextSendEmail);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(id.btnSendEmail).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userEmail = emailBox.getText().toString();
                        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                            Toast.makeText(LoginActivity.this, "Ingresa el email correspondiente", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Vuelve a intentarlo", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                dialogView.findViewById(id.btnCancelEmail).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });

    }

    private void iniciarSesion(String email, String contraseña) {
        mAuth.signInWithEmailAndPassword(email, contraseña)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Obtiene el UID del usuario actual
                            String userId = mAuth.getCurrentUser().getUid();

                            // Referencia a la base de datos de Firebase
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios").child(userId);

                            // Obtiene el rol del usuario desde la base de datos
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String role = dataSnapshot.child("rol").getValue(String.class);

                                        // Redirige según el rol del usuario
                                        if ("Cliente".equals(role)) {
                                            Toast.makeText(LoginActivity.this, "Inicio de sesión como cliente", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Inicio de sesión como organizador de eventos", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginActivity.this, MainActivityOrg.class));
                                        }
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "No se pudo obtener el rol del usuario.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(LoginActivity.this, "Error al obtener el rol del usuario: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(LoginActivity.this, "Fallo en el inicio de sesión.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}