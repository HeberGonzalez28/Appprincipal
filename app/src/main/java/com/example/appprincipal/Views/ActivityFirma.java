package com.example.appprincipal.Views;

import static com.example.appprincipal.Views.ActivityCreateFoto.REQUEST_IMAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.appprincipal.Controller.RestApiMethods;
import com.example.appprincipal.Models.Firma;
import com.example.appprincipal.R;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityFirma extends AppCompatActivity {

    static final int ACCESS_CAMERA = 201;
    static final int DRAW_SIGNATURE = 301;
    Signature firma;
    String Path;
    Button btnfirma,btnguardar,btnlista,btnregresar;

    EditText description;
    Bitmap signatureBitmap;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firma);

        //firma = (Signature) findViewById(R.id.firma);
        description = (EditText) findViewById(R.id.editTextdes);
        btnfirma = (Button) findViewById(R.id.btnfirma);
        btnguardar = (Button) findViewById(R.id.btnguardar);
        btnlista = (Button) findViewById(R.id.btnlist);
        btnregresar = (Button) findViewById(R.id.btnregresar);


        btnfirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permisos();
            }
        });

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
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

    private void saveData() {
        if (signatureBitmap != null) {
            String descriptionText = description.getText().toString().trim();
            if (!descriptionText.isEmpty()) {
                // Convertir la firma a Base64 y enviar los datos
                String signatureBase64 = convertBitmapToBase64(signatureBitmap);

                RequestQueue requestQueue = Volley.newRequestQueue(this);

                Firma firma = new Firma();
                firma.setDescription(descriptionText);
                firma.setImagen(signatureBase64);
                // Aquí puedes enviar los datos a través de tu API

                JSONObject jsonObject = new JSONObject();

                try
                {
                    jsonObject.put("imagen",firma.getImagen());
                    jsonObject.put("descripcion",firma.getDescription());

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, RestApiMethods.EndpointPostPersonfirma,
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

                // RestApiMethods.sendData(fotografia);
                Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Por favor, ingrese una descripción", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Por favor, ingrese una firma", Toast.LENGTH_SHORT).show();
        }
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
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
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_CAMERA);
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
        if (requestCode == DRAW_SIGNATURE && resultCode == RESULT_OK && data != null) {
            try {
                signatureBitmap = (Bitmap) data.getExtras().get("data");
            }catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

}