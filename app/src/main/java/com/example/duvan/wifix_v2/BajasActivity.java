package com.example.duvan.wifix_v2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class BajasActivity extends AppCompatActivity {

    String recuperado = "";
    String cedula_U;
    String tienda;

    EditText descripcion, precio;
    Button registrar, listar;
    RadioButton gasto, costo;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bajas);

        progressDialog = new ProgressDialog(BajasActivity.this);
        cargarPreferencias();
        descripcion = (EditText)findViewById(R.id.txtDescripciónBaja);
        precio = (EditText)findViewById(R.id.txtCostoBaja);
        costo = (RadioButton) findViewById(R.id.radio_costos);
        gasto = (RadioButton) findViewById(R.id.radio_gastos);
        registrar = (Button)findViewById(R.id.btnRegistrarBaja);
        listar = (Button) findViewById(R.id.btnListarBajas);

        final Bundle recupera = getIntent().getExtras();
        if(recupera != null){
            recuperado = recupera.getString("cedula");
        }
        //Toast.makeText(getApplicationContext(),cedula_U, Toast.LENGTH_SHORT).show();
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //agregas un mensaje en el ProgressDialog
                progressDialog.setMessage("Cargando...");
                //muestras el ProgressDialog
                progressDialog.show();
                //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
                ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = con.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    final String baja = validar();
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            final String resultado = enviarDatosGET(cedula_U, descripcion.getText().toString(), Integer.parseInt(precio.getText().toString()), baja);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int r = obtenerDatosJSON(resultado);
                                    if (descripcion.getText().toString().isEmpty() || precio.getText().toString().isEmpty()) {
                                        Toast.makeText(getApplicationContext(), "¡Complete los campos!", Toast.LENGTH_LONG).show();
                                    } else {
                                        if (r > 0) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Se ha registrado exitosamente " + baja, Toast.LENGTH_LONG).show();
                                            descripcion.setText("");
                                            precio.setText("");
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Error al registrar", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getApplicationContext(), "Mensaje " + resultado, Toast.LENGTH_LONG).show();
                                            progressDialog.hide();
                                        }
                                    }
                                    progressDialog.hide();
                                }
                            });
                        }
                    };
                    thread.start();
                }else {
                    Toast.makeText(getApplicationContext(), "Verifique su conexión a internet",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });

        listar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listarSalidas();
            }
        });
    }

    private String validar() {
        String cad = "";
        if(costo.isChecked()){
            cad = "costo";
        }else if(gasto.isChecked()){
            cad = "gasto";
        }
        return cad;
    }

    public void listarSalidas(){
        Intent intent = new Intent(getApplicationContext(), ListarSalidasActivity.class);
        startActivity(intent);
    }

    //METODO PARA ENVIAR LOS DATOS AL SERVIDOR LOCAL
    public String enviarDatosGET(String cedula, String descripcion, int precio, String tipo){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local="http://192.168.1.3/ServiciosWeb/registrarBaja.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/registrarSalidaBD.php?";
        //cedula = cedula_U;

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO
            // YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "empleado=" + cedula + "&descripcion=" + descripcion + "&precio=" + precio + "&tipo=" + tipo);
            HttpURLConnection conection = (HttpURLConnection) url.openConnection();
            respuesta = conection.getResponseCode();
            resul = new StringBuilder();
            if (respuesta == HttpURLConnection.HTTP_OK){
                InputStream inputStream = new BufferedInputStream(conection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                while ((linea = reader.readLine()) != null){
                    resul.append(linea);
                }
            }
        }catch (Exception e){
            return e.getMessage();
        }
        return resul.toString();
    }

    public int obtenerDatosJSON(String response){
        int res = 0;
        try {
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() > 0 ){
                res = 1;
            }
        }catch (Exception e){}
        return res;
    }

    private void cargarPreferencias(){
        SharedPreferences preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        cedula_U = preferences.getString("cedula","");
        tienda = preferences.getString("tienda","");
    }
}
