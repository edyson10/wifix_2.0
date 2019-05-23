package com.example.duvan.wifix_v2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;



public class ActualizarDatosActivity extends AppCompatActivity {

    EditText telefono, direccion, correo;
    TextView nombre, apellido, cedula;
    Button actualizar;
    String recuperado = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_datos);
        Bundle recupera = getIntent().getExtras();
        if(recupera != null){
            recuperado = recupera.getString("cedula");
        }
        nombre = (TextView)findViewById(R.id.txtNombreAct);
        apellido = (TextView)findViewById(R.id.txtApellidoAct);
        cedula = (TextView)findViewById(R.id.txtCedulaAct);
        telefono = (EditText) findViewById(R.id.txtTelefonoAct);
        direccion = (EditText)findViewById(R.id.txtDireccionAct);
        correo = (EditText)findViewById(R.id.txtCorreoAct);
        actualizar = (Button)findViewById(R.id.btnActualizar);
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Hola",Toast.LENGTH_SHORT).show();
            }
        });
        Thread thread = new Thread(){
            @Override
            public void run() {
                final String resultado = mostrarDatosGET(recuperado);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MostrarDatos(resultado);
                    }
                });
            }
        };
        thread.start();
    }

    public String mostrarDatosGET(String cedula){
        URL url = null;
        String linea = "";
        int respuesta=0;
        StringBuilder resul = null;
        String url_local = "http://localhost/ServiciosWeb/listaEmpleadoAct.php?cedula=";
        String url_host = "https://appwifix.000webhostapp.com/listaEmpleadoAct.php?";
        try{
            url= new URL(url_local+cedula);
            HttpURLConnection connection= (HttpURLConnection)url.openConnection();
            respuesta= connection.getResponseCode();
            resul = new StringBuilder();
            if(respuesta == HttpURLConnection.HTTP_OK){
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                while ((linea=reader.readLine()) != null){
                    resul.append(linea);
                }
            }
        }catch (Exception e){ }
        return resul.toString();
    }

    public void MostrarDatos(String response){
        try{
            JSONArray jsonArray = new JSONArray(response);
            for (int i =0; i<jsonArray.length();i++){
                nombre.setText(jsonArray.getJSONObject(i).getString("nombre"));
                nombre.setText(jsonArray.getJSONObject(i).getString("apellido"));
                cedula.setText(jsonArray.getJSONObject(i).getString(recuperado));
                nombre.setText(jsonArray.getJSONObject(i).getString("telefono"));
                nombre.setText(jsonArray.getJSONObject(i).getString("direccion"));
                nombre.setText(jsonArray.getJSONObject(i).getString("correo"));
            }
        }catch (Exception e){}
    }
}
