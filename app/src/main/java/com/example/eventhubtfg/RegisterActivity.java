package com.example.eventhubtfg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText editNombre, editFechaNac, editApellidos,
            editRol, editPassword, editPasswordRepeat, editMail;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRol;
    private RadioButton radioButtonRol;
    private String txtNombre, txtFechaNac, txtApellidos, txtRol, txtPassword, txtPasswordRepeat, txtMail;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("usuarios");
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);

        progressBar = findViewById(R.id.progrssBar);
        editNombre = findViewById(R.id.editName);
        editApellidos = findViewById(R.id.editApellidos);
        editFechaNac = findViewById(R.id.editFechaNacimiento);
        editPassword = findViewById(R.id.editPassword);
        editPasswordRepeat = findViewById(R.id.editPasswordRepeat);
        editMail = findViewById(R.id.editEmail);

        radioGroupRol = findViewById(R.id.rdbtnRol);

        TextView tengoCuenta = findViewById(R.id.txtTengoCuenta);
        tengoCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        Button buttonRegister = findViewById(R.id.btnRegistrar);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int seleccionar = radioGroupRol.getCheckedRadioButtonId();
                radioButtonRol = findViewById(seleccionar);

                txtNombre = editNombre.getText().toString();
                txtApellidos = editApellidos.getText().toString();
                txtFechaNac = editFechaNac.getText().toString();
                txtMail = editMail.getText().toString();
                txtPassword = editPassword.getText().toString();
                txtPasswordRepeat = editPasswordRepeat.getText().toString();


                if (TextUtils.isEmpty(txtNombre)) {
                    editNombre.setError("Se requiere el nombre");
                    editNombre.requestFocus();
                } else if (!validarNombre(txtNombre)) {
                    Toast.makeText(RegisterActivity.this, "El nombre no puede contener números ni caracteres especiales", Toast.LENGTH_LONG).show();
                    editNombre.requestFocus();
                } else if (TextUtils.isEmpty(txtApellidos)) {
                    editApellidos.setError("Se requieren los apellidos");
                    editApellidos.requestFocus();
                } else if (!validarNombre(txtApellidos)) {
                    Toast.makeText(RegisterActivity.this, "Los apellidos no pueden contener números ni caracteres especiales", Toast.LENGTH_LONG).show();
                    editApellidos.requestFocus();
                } else if (TextUtils.isEmpty(txtMail)) {
                    editMail.setError("Se requiere el email");
                    editMail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(txtMail).matches()) {
                    editMail.setError("Se requiere email correcto");
                    editMail.requestFocus();
                } else if (!validarFecha(txtFechaNac)) {
                    Toast.makeText(RegisterActivity.this, "Formato de fecha inválido o edad mínima no alcanzada (16 años)", Toast.LENGTH_LONG).show();
                    editFechaNac.requestFocus();
                } else if (TextUtils.isEmpty(txtPassword)) {
                    editPassword.setError("Es necesario la contraseña");
                    editPassword.requestFocus();
                } else if (!validatePassword(txtPassword)) {
                    Toast.makeText(RegisterActivity.this, "La contraseña debe contener 1 número, 1 letra mayúscula y 8 carácteres", Toast.LENGTH_LONG).show();
                    editPassword.requestFocus();
                } else if (TextUtils.isEmpty(txtPasswordRepeat)) {
                    editPasswordRepeat.setError("Es necesario confirmar la contraseña");
                    editPasswordRepeat.requestFocus();
                } else if (!txtPassword.equals(txtPasswordRepeat)) {
                    Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                    editPasswordRepeat.requestFocus();
                } else {
                    txtRol = radioButtonRol.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registrarUsuario();
                }
            }
        });

    }

    private void registrarUsuario() {
        auth.createUserWithEmailAndPassword(txtMail, txtPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                crearUsuario();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void crearUsuario() {
        progressDialog.setMessage("Registrando usuario");
        progressDialog.show();

        String uid = auth.getUid();

        HashMap<String, String> Datos = new HashMap<>();
        Datos.put("uid", uid);
        Datos.put("correo", txtMail);
        Datos.put("nombre", txtNombre);
        Datos.put("apellidos", txtApellidos);
        Datos.put("password", txtPassword);
        Datos.put("rol", txtRol);
        Datos.put("fecNacimiento", txtFechaNac);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        databaseReference.child(uid)
                .setValue(Datos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public static boolean validatePassword(String password) {
        String regex = "^(?=.*[0-9])(?=.*[A-Z]).{8,}$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    private boolean validarNombre(String name) {
        String regex = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    private boolean validarFecha(String date) {
        // Validar el formato de la fecha "YYYY-MM-dd"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setLenient(false);

        try {
            Date birthDate = sdf.parse(date);
            if (birthDate != null) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, -16);
                Date minAgeDate = cal.getTime();
                return birthDate.before(minAgeDate);
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
