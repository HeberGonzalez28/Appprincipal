package com.example.appprincipal.Views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import com.example.appprincipal.R;
import com.example.appprincipal.Controller.RestApiMethods;
import com.example.appprincipal.Models.Fotografia;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityCreateFoto extends AppCompatActivity {

    static final int REQUEST_IMAGE = 101;
    static final int ACCESS_CAMERA =  201;

    ImageView foto;
    String Path;
    Button btnfoto, btnguardar,btnlista,btnregresar;

    EditText description;
    String currentPhotoPath;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_foto);

        foto = (ImageView) findViewById(R.id.image_view);
        description = (EditText) findViewById(R.id.editTextdes);
        btnfoto = (Button) findViewById(R.id.btnfoto);
        btnguardar = (Button) findViewById(R.id.btnguardar);
        btnlista = (Button) findViewById(R.id.btnlist);
        btnregresar = (Button) findViewById(R.id.btnregresar);


        btnfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permisos();
            }
        });

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendData();
            }
        });
        btnlista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ActivityReadFoto.class);
                startActivity(intent);
            }
        });

    }

    private void SendData()
    {
        requestQueue = Volley.newRequestQueue(this);
        Fotografia fotografia = new Fotografia();

        fotografia.setDescription(description.getText().toString());
        fotografia.setImagen(ConvertImageBase64(currentPhotoPath));

        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put("descripcion",fotografia.getDescription());
            jsonObject.put("imagen",fotografia.getImagen());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, RestApiMethods.EndpointPostPerson,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    try
                    {
                        String mensaje = response.getString("message");
                        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    String errorMessage = error.getMessage() != null ? error.getMessage() : "Error desconocido";
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();

                }
            });


            requestQueue.add(request);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    private String ConvertImageBase64(String path)
    {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream byteArrayOutputStream = null;

            byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] imageArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(imageArray, Base64.DEFAULT);

    }

    private void permisos() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ACCESS_CAMERA);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == ACCESS_CAMERA)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                dispatchTakePictureIntent();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Se necesita permiso de la camara",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            photoFile = createImageFile();
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.appprincipal.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE);
            }
        }
    }

    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                File photoFile = new File(currentPhotoPath);
                if (photoFile.exists()) {
                    foto.setImageURI(Uri.fromFile(photoFile));
                } else {
                    Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }
}