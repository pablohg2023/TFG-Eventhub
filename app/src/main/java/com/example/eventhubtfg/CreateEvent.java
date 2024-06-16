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

public class CreateEvent extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText etNombreEvento, etDescripcionEvento, etLugarEvento, etDireccionEvento, etFechaEvento, etHoraEvento, etPrecioEvento;
    private Button btnCrearEvento, btnSeleccionarImagen, btnCancelarEvento;
    private ImageView imgEvento;
    private Uri imageUri;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        etNombreEvento = findViewById(R.id.etNombreEvento);
        etDescripcionEvento = findViewById(R.id.etDescripcionEvento);
        etLugarEvento = findViewById(R.id.etLugarEvento);
        etDireccionEvento = findViewById(R.id.etDireccionEvento);
        etFechaEvento = findViewById(R.id.etFechaEvento);
        etHoraEvento = findViewById(R.id.etHoraEvento);
        etPrecioEvento = findViewById(R.id.etPrecioEvento);
        btnCrearEvento = findViewById(R.id.btnCrearEvento);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnCancelarEvento = findViewById(R.id.btnCancelarEvento);
        imgEvento = findViewById(R.id.imgEvento);

        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnCrearEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null) {
                    uploadImage();
                } else {
                    crearEvento();
                }
            }
        });

        btnCancelarEvento.setOnClickListener(new View.OnClickListener() {
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
            imgEvento.setImageURI(imageUri);
        }
    }

    private void uploadImage() {
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
                                    crearEvento();
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

    private void crearEvento() {
        String nombre = etNombreEvento.getText().toString().trim();
        String descripcion = etDescripcionEvento.getText().toString().trim();
        String lugar = etLugarEvento.getText().toString().trim();
        String direccion = etDireccionEvento.getText().toString().trim();
        String fecha = etFechaEvento.getText().toString().trim();
        String hora = etHoraEvento.getText().toString().trim();
        String precio = etPrecioEvento.getText().toString().trim();

        if (nombre.isEmpty() || descripcion.isEmpty() || lugar.isEmpty() || direccion.isEmpty() || fecha.isEmpty() || hora.isEmpty() || precio.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
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
}
