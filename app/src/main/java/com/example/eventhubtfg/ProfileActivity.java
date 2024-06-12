package com.example.eventhubtfg;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWebException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    Button btnCerrarSesion, btnEditPerfil;
    ImageButton btnVolver;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    TextView textNombre, textApellidos, textFechaNac, textCorreo, textRol;

    DatabaseReference usuarios;


    @SuppressLint("WrongViewCast")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textNombre = findViewById(R.id.textNombre);
        textCorreo = findViewById(R.id.txtCorreo);
        textApellidos = findViewById(R.id.textApellidos);
        textRol = findViewById(R.id.txtRol);
        textFechaNac = findViewById(R.id.txtFechaNac);
        btnEditPerfil = findViewById(R.id.editPerfil);
        btnVolver = findViewById(R.id.btnBack);

        usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

        btnEditPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, EditProfile.class));
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // El usuario está autenticado, verifica su rol
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios").child(user.getUid()).child("rol");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String rol = dataSnapshot.getValue(String.class);
                                if (rol != null) {
                                    // Verificar el rol del usuario
                                    if (rol.equals("Cliente")) {
                                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                                    } else {
                                        startActivity(new Intent(ProfileActivity.this, MainActivityOrg.class));
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Manejar el error si es necesario
                        }
                    });
                }
            }
        });


    }

    @Override
    protected void onStart() {
        comprobarIniciosesion();
        super.onStart();
    }

    private void comprobarIniciosesion() {
        if (user != null) {
            cargaDeDatos();
        } else {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void cargaDeDatos() {
        usuarios.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    textNombre.setText((CharSequence) snapshot.child("nombre").getValue());
                    textApellidos.setText((CharSequence) snapshot.child("apellidos").getValue());
                    textCorreo.setText((CharSequence) snapshot.child("correo").getValue());
                    textRol.setText((CharSequence) snapshot.child("rol").getValue());
                    textFechaNac.setText((CharSequence) snapshot.child("fecNacimiento").getValue());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void cerrarSesion() {
        firebaseAuth.signOut();
        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        Toast.makeText(this, "Se cerro sesión exitosamente", Toast.LENGTH_SHORT).show();
    }

}
