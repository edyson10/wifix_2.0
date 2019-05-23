package com.example.duvan.wifix_v2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AgregarProductoActivity extends AppCompatActivity {

    Button agregarP, agregarM, guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);
        agregarP = (Button) findViewById(R.id.btnAgregarProd);
        agregarM = (Button) findViewById(R.id.btnAgregarMarca);
        guardar = (Button) findViewById(R.id.btnGuardarProd);
        agregarP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarProd(view);
            }
        });
        agregarM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarMarca(view);
            }
        });
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarProd(view);
            }
        });
    }

    private void agregarProd(View view){
        Toast.makeText(this,"Producto agregado con exito", Toast.LENGTH_SHORT).show();
    }

    private void agregarMarca(View view){
        Toast.makeText(this,"Marca agregada con exito", Toast.LENGTH_SHORT).show();
    }

    private void guardarProd(View view){
        Toast.makeText(this,"Se guardo con exito", Toast.LENGTH_SHORT).show();
    }

}
