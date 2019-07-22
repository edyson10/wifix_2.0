package com.example.duvan.wifix_v2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CodigoQRActivity extends AppCompatActivity {

    private Button codigoqr, venderqr;
    private IntentIntegrator qrscan;
    private TextView id_producto, articulo,modelo;
    private EditText precioVenta, cantidad, empleado;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codigo_qr);
        progressDialog = new ProgressDialog(CodigoQRActivity.this);
        codigoqr = (Button) findViewById(R.id.btnScan);
        id_producto = (TextView) findViewById(R.id.txtIdProductoQR);
        articulo = (TextView) findViewById(R.id.txtArticuloQR);
        modelo = (TextView) findViewById(R.id.txtModeloQR);
        precioVenta = (EditText) findViewById(R.id.txtPrecioVentaQR);
        cantidad = (EditText) findViewById(R.id.txtCantidadQR);
        empleado = (EditText) findViewById(R.id.txtEmpleadoQR);
        venderqr = (Button) findViewById(R.id.btnVenderQR);

        qrscan = new IntentIntegrator(CodigoQRActivity.this);
        codigoqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrscan.initiateScan();
            }
        });
        venderqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vender();
            }
        });
    }

    // ----- METODO DE MOSTRAR LOS DATOS -----
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getApplicationContext(), "Resultado no encontrado", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject obj = new JSONObject(intentResult.getContents());
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    //agregas un mensaje en el ProgressDialog
                    progressDialog.setMessage("Cargando...");
                    //muestras el ProgressDialog
                    progressDialog.show();
                    //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
                    ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = con.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        //final String finalCedulaEmpleado = ;
                        final String id = intentResult.getContents();
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                final String resultado = cargarDatosGET(id);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        cargarDatos(resultado);
                                    }
                                });
                            }
                        };
                        thread.start();
                    } else {
                        Toast.makeText(getApplicationContext(), "Verifique su conexión a internet", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    // ----- FIN DEL METODO DE MOSTRAR LOS DATOS -----

    // ----- METODO DEL EVENTO VENDER -----
    private void vender(){
        if (modelo.getText().toString().isEmpty() || cantidad.getText().toString().isEmpty() || precioVenta.getText().toString().isEmpty() ) {
            Toast.makeText(getApplicationContext(), "¡Complete los campos!", Toast.LENGTH_LONG).show();
        }else {
            //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
            ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = con.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        final String resultado1 = enviarDatosGET(empleado.getText().toString(), articulo.getText().toString(), modelo.getText().toString(),
                                Integer.parseInt(precioVenta.getText().toString()), Integer.parseInt(cantidad.getText().toString()));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_SHORT).show();
                                int r = validarDatosJSON(resultado1);
                                if (r > 0) {
                                    Toast.makeText(getApplicationContext(), "¡Algo malo ocurrio!", Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
                                    progressDialog.hide();
                                    Toast.makeText(getApplicationContext(), resultado1, Toast.LENGTH_SHORT).show();
                                } else {
                                    progressDialog.dismiss();
                                    id_producto.setText("");
                                    empleado.setText("");
                                    articulo.setText("");
                                    modelo.setText("");
                                    cantidad.setText("");
                                    precioVenta.setText("");
                                    Toast.makeText(getApplicationContext(), "Se ha registrado la venta exitosamente", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                };
                thread.start();
            } else {
                Toast.makeText(getApplicationContext(), "Verifique su conexión a internet", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // ----- FIN DEL METODO DEL EVENTO VENDER -----

    //METODO PARA ENVIAR LOS DATOS DE VENTA AL SERVIDOR -----
    public String enviarDatosGET(String cedula, String marca, String modelo, int precio, int cantidad){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/registrarVenta.php";
        String url_local = "http://192.168.1.6/ServiciosWeb/registrarVenta.php";
        String mod = modelo.replace(" ", "%20");
        String mar = marca.replace(" ", "%20");

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "?empleado=" + cedula + "&marca=" + mar + "&modelo=" + mod + "&precio=" + precio + "&cantidad=" + cantidad);
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
    // ------ FIN DEL METODO DE ENVIO DE DATOS -----

    // ----- INICIO DEL METODO DE CARGAR LOS DATOS DEL PRODUCTO -----
    public String cargarDatosGET(String producto) {
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/buscarProductoID.php?";
        String url_local = "http://192.168.1.6/ServiciosWeb/buscarVenta.php?";

        try {
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "producto=" + producto);
            HttpURLConnection conection = (HttpURLConnection) url.openConnection();
            respuesta = conection.getResponseCode();
            resul = new StringBuilder();
            if (respuesta == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = new BufferedInputStream(conection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                while ((linea = reader.readLine()) != null) {
                    resul.append(linea);
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return resul.toString();
    }
    // ----- FIN DE METODO DE CARGAR  -----

    // ----- INICIO DE METODO DE VALIDAR SI HAY DATOS O NO EL EL JSON -----
    public int validarDatosJSON(String response) {
        int res = 0;
        try {
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() > 0) {
                res = 1;
            }
        } catch (Exception e) {
        }
        return res;
    }
    // ----- FIN DEL METODO -----

    //METODO PARA CARGAR LOS DATOS EN LOS TEXTVIEW
    public void cargarDatos(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                id_producto.setText(jsonArray.getJSONObject(i).getString("id_producto"));
                articulo.setText(jsonArray.getJSONObject(i).getString("articulo"));
                modelo.setText(jsonArray.getJSONObject(i).getString("modelo"));
                precioVenta.setText(jsonArray.getJSONObject(i).getString("precioVenta"));
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Error: " + ex, Toast.LENGTH_SHORT).show();
        }
    }
    // ----- FIN DE METODO DE CARGAR
}
