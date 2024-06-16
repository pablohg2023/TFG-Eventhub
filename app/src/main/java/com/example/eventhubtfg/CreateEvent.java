package com.example.eventhubtfg;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateEvent extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    private EditText etNombre, etDescripcion, etLugar, etDireccion, etFecha, etHora, etPrecio;
    private Button btnCrear, btnSeleccionarImagen, btnCancelar;
    private ImageView img;
    private Uri imageUri;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        etNombre = findViewById(R.id.etNombreEvento);
        etDescripcion = findViewById(R.id.etDescripcionEvento);
        etLugar = findViewById(R.id.etLugarEvento);
        etDireccion = findViewById(R.id.etDireccionEvento);
        etFecha = findViewById(R.id.etFechaEvento);
        etHora = findViewById(R.id.etHoraEvento);
        etPrecio = findViewById(R.id.etPrecioEvento);
        btnCrear = findViewById(R.id.btnCrearEvento);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnCancelar = findViewById(R.id.btnCancelarEvento);
        img = findViewById(R.id.imgEvento);

        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null) {
                    cargarImagen();
                } else {
                    Toast.makeText(CreateEvent.this, "Por favor, selecciona una imagen para el evento", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateEvent.this, MainActivityOrg.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            img.setImageURI(imageUri);
        }
    }

    private void cargarImagen() {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("eventos").child(System.currentTimeMillis() + ".jpg");
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUrl = uri.toString();
                                    crear();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateEvent.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void crear() {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String lugar = etLugar.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String hora = etHora.getText().toString().trim();
        String precio = etPrecio.getText().toString().trim();

        if (nombre.isEmpty() || descripcion.isEmpty() || lugar.isEmpty() || direccion.isEmpty() || fecha.isEmpty() || hora.isEmpty() || precio.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarDescripcion(descripcion)) {
            Toast.makeText(this, "La descripción no puede tener más de 150 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarFecha(fecha)) {
            Toast.makeText(this, "La fecha debe ser posterior a una semana del día actual", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarHora(hora)) {
            Toast.makeText(this, "La hora debe estar en formato de 24 horas (HH:mm)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarPrecio(precio)) {
            Toast.makeText(this, "El precio no puede ser negativo ni menor a cero", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventosRef = database.getReference("eventos");

        String firebaseId = eventosRef.push().getKey();
        if (firebaseId == null) {
            Toast.makeText(this, "Error al generar ID del evento", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = firebaseId.hashCode();

        Evento evento = new Evento();
        evento.setId(id);
        evento.setImagenUrl(imageUrl != null ? imageUrl : "default_image_url");
        evento.setNombre(nombre);
        evento.setDescripcion(descripcion);
        evento.setLugar(lugar);
        evento.setFecha(fecha);
        evento.setHora(hora);
        evento.setFavorito(false);

        eventosRef.child(firebaseId).setValue(evento)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CreateEvent.this, "Evento creado con éxito", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(CreateEvent.this, "Error al crear el evento", Toast.LENGTH_SHORT).show());
    }

    private boolean validarDescripcion(String description) {
        return description.length() <= 150;
    }

    private boolean validarFecha(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar currentDate = Calendar.getInstance();
        currentDate.add(Calendar.DAY_OF_MONTH, 7); // Añade una semana a la fecha actual

        try {
            Date inputDate = dateFormat.parse(date);
            Date futureDate = currentDate.getTime();

            return inputDate != null && inputDate.after(futureDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean validarHora(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        dateFormat.setLenient(false);

        try {
            dateFormat.parse(time);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean validarPrecio(String price) {
        try {
            double parsedPrice = Double.parseDouble(price);
            return parsedPrice >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
