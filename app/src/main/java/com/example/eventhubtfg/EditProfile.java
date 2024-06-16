package com.example.eventhubtfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.common.reflection.qual.NewInstance;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    EditText editNombre, editApellidos;

    ImageButton btnVolver;

    Button btnEliminarCuenta, btnEditarPerfil, btnEditPassword;

    DatabaseReference usuarios;

    FirebaseAuth firebaseAuth;

    FirebaseUser user;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editNombre = findViewById(R.id.editNombre);
        editApellidos = findViewById(R.id.editApellidos);
        btnEditPassword = findViewById(R.id.editPassword);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnEliminarCuenta = findViewById(R.id.btnEliminarCuenta);
        btnVolver = findViewById(R.id.btnBack);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        usuarios = FirebaseDatabase.getInstance().getReference().child("Usuarios");

        cargaDeDatos();

        btnEliminarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarEliminarCuenta();
            }
        });

        btnEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarPerfil();
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfile.this, ProfileActivity.class));
            }
        });

        btnEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
                View dialogView = getLayoutInflater().inflate(R.layout.fragment_new_password, null);
                EditText editTextCurrentPassword = dialogView.findViewById(R.id.editTextCurrentPassword);
                EditText editTextNewPassword = dialogView.findViewById(R.id.editTextNewPassword);
                EditText editTextRepeatPassword = dialogView.findViewById(R.id.editTextRepeatPassword);
                Button btnUpdatePassword = dialogView.findViewById(R.id.btnUpdatePassword);
                Button btnCancelPassword = dialogView.findViewById(R.id.btnCancelPassword);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String currentPassword = editTextCurrentPassword.getText().toString().trim();
                        String newPassword = editTextNewPassword.getText().toString().trim();
                        String repeatPassword = editTextRepeatPassword.getText().toString().trim();

                        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(repeatPassword)) {
                            Toast.makeText(EditProfile.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!newPassword.equals(repeatPassword)) {
                            Toast.makeText(EditProfile.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Validar la nueva contraseña utilizando el método estático de RegisterActivity
                        if (!RegisterActivity.validatePassword(newPassword)) {
                            Toast.makeText(EditProfile.this, "La contraseña debe tener al menos 8 caracteres, una letra mayúscula y un número", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

                        user.reauthenticate(credential)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        user.updatePassword(newPassword)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Actualizar contraseña en la base de datos en tiempo real
                                                        actualizarContraseñaEnBaseDeDatos(user.getUid(), newPassword);

                                                        Toast.makeText(EditProfile.this, "Contraseña actualizada exitosamente", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(EditProfile.this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditProfile.this, "Autenticación fallida. La contraseña actual puede ser incorrecta", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                btnCancelPassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

    }

    private void eliminarCuenta(final String usuarioId) {
        usuarios.child(usuarioId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@NonNull DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EditProfile.this, "Usuario eliminado exitosamente", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(EditProfile.this, LoginActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(EditProfile.this, "Error al eliminar el usuario de la autenticación", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(EditProfile.this, "Error al eliminar el usuario de la base de datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cargaDeDatos() {
        usuarios.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    String apellidos = snapshot.child("apellidos").getValue(String.class);

                    editNombre.setText(nombre);
                    editApellidos.setText(apellidos);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void actualizarContraseñaEnBaseDeDatos(String usuarioId, String nuevaContraseña) {
        DatabaseReference referenciaUsuario = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(usuarioId);
        Map<String, Object> datosActualizados = new HashMap<>();
        datosActualizados.put("password", nuevaContraseña);

        referenciaUsuario.updateChildren(datosActualizados)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // La contraseña se ha actualizado en la base de datos en tiempo real
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this, "Error al actualizar la contraseña en la base de datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void editarPerfil() {
        String nuevoNombre = editNombre.getText().toString().trim();
        String nuevosApellidos = editApellidos.getText().toString().trim();

        if (!TextUtils.isEmpty(nuevoNombre) && !TextUtils.isEmpty(nuevosApellidos)) {
            Map<String, Object> datosActualizados = new HashMap<>();
            datosActualizados.put("nombre", nuevoNombre);
            datosActualizados.put("apellidos", nuevosApellidos);

            usuarios.child(user.getUid()).updateChildren(datosActualizados)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditProfile.this, "Perfil actualizado exitosamente", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditProfile.this, ProfileActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfile.this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
        }
    }


    private void confirmarEliminarCuenta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmación");
        builder.setMessage("¿Estás seguro de que quieres eliminar tu cuenta? Esta acción es irreversible.");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarCuenta(user.getUid());
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}