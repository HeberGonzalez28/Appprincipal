package com.example.appprincipal.Views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.appprincipal.Controller.CustomBaseAdapter;
import com.example.appprincipal.Controller.RestApiMethods;
import com.example.appprincipal.Models.Fotografia;
import com.example.appprincipal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityReadFirma extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView readlistView;
    private List<Fotografia> mList = new ArrayList<>();
    private CustomBaseAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_foto);

        readlistView = findViewById(R.id.listView);
        readlistView.setOnItemClickListener(this);

        // Inicializar Volley
        RequestQueue queue = Volley.newRequestQueue(this);

        // Realizar la solicitud JSON
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, RestApiMethods.EndpointGetPerson, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Limpiar la lista antes de agregar nuevos elementos
                            mList.clear();
                            // Parsear los datos JSON y añadirlos a la lista
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                String imagenBase64 = jsonObject.getString("imagen");
                                String descripcion = jsonObject.getString("descripcion");

                                mList.add(new Fotografia(id, imagenBase64, descripcion));
                            }
                            // Inicializar el adaptador con los datos obtenidos
                            mAdapter = new CustomBaseAdapter(ActivityReadFirma.this, R.layout.item_row, mList);
                            readlistView.setAdapter(mAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "Error en la solicitud HTTP");
                Toast.makeText(ActivityReadFirma.this, "Error en la solicitud HTTP", Toast.LENGTH_SHORT).show();
            }
        });

        // Añadir la solicitud a la cola
        queue.add(jsonArrayRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "Elemento Seleccionado: " + position, Toast.LENGTH_SHORT).show();
    }
}