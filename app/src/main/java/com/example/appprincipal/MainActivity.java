package com.example.appprincipal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.appprincipal.Views.ActivityCreateFoto;
import com.example.appprincipal.Views.ActivityFirma;

public class MainActivity extends AppCompatActivity {

    Button btnejercicio2_3, btnejercicio2_4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnejercicio2_3 = (Button) findViewById(R.id.btnejer2_3);
        btnejercicio2_4 = (Button) findViewById(R.id.btnejer2_4) ;

        btnejercicio2_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityCreateFoto.class);
                startActivity(intent);
            }
        });

        btnejercicio2_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityFirma.class);
                startActivity(intent);
            }
        });
    }
}