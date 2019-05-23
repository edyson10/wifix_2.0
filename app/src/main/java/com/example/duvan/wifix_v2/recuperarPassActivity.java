package com.example.duvan.wifix_v2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class recuperarPassActivity extends AppCompatActivity {

    Button recuperar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_pass);
        recuperar = (Button) findViewById(R.id.btnRecuperarPass);
        recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Contraseña recuperada.",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
