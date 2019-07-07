package com.example.duvan.wifix_v2;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class ActualizarVentaActivity extends AppCompatActivity {

    EditText txtBuscar,  txtPrecioAct, txtCantidadAct;
    TextView txtModeloAct, txtArticuloAct, fechaNueva, fechaVen;
    Button actualizar;
    ImageButton buscar;
    Calendar mCurrentDate;
    int dia, mes, anio;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_venta);

        progressDialog = new ProgressDialog(ActualizarVentaActivity.this);
        txtBuscar = (EditText) findViewById(R.id.txtBuscarVenta);
        txtArticuloAct = (TextView) findViewById(R.id.txtProductoBuscar);
        txtModeloAct = (TextView) findViewById(R.id.txtModeloBuscar);
        txtPrecioAct = (EditText) findViewById(R.id.txtPrecioBuscar);
        txtCantidadAct = (EditText) findViewById(R.id.txtCantidadBuscar);
        buscar = (ImageButton) findViewById(R.id.btnBuscaVenta);
        actualizar = (Button)findViewById(R.id.btnActualizarVenta);
        fechaVen = (TextView) findViewById(R.id.fechaVenta);
        fechaNueva = (TextView) findViewById(R.id.fechaVentaNueva);

        //CODIGO FECHA DE ENTREGA ESTIPULADA
        mCurrentDate = Calendar.getInstance();
        dia = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        mes = mCurrentDate.get(Calendar.MONTH);
        anio = mCurrentDate.get(Calendar.YEAR);
        fechaNueva.setText(anio + "-" + (mes + 1) + "-" + dia);
        fechaNueva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getApplicationContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker , int year , int monthOfYear,  int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        fechaNueva.setText(year+"-"+monthOfYear+"-"+dayOfMonth);
                    }
                }, anio, mes, dia);
                datePickerDialog.show();
            }
        });

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //agregas un mensaje en el ProgressDialog
                progressDialog.setMessage("Cargando...");
                //muestras el ProgressDialog
                progressDialog.show();
                //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
                ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = con.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected()) {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            final String resultado = enviarDatosGET(txtBuscar.getText().toString());
                            final int res = validarDatosJSON(resultado);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (res == 0) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "No existe una venta con ese ID", Toast.LENGTH_SHORT).show();
                                    } else {
                                        cargarDatos(resultado);
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Se cargo correctamente.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    };
                    thread.start();
                } else {
                    Toast.makeText(getApplicationContext(), "Verifique su conexión a internet",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });


        actualizar.setOnClickListener(new View.OnClickListener() {
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
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            final String resultado = actualizarDatosGET(txtBuscar.getText().toString(), txtArticuloAct.getText().toString(),
                                    txtModeloAct.getText().toString(), Integer.parseInt(txtPrecioAct.getText().toString()),
                                    Integer.parseInt(txtCantidadAct.getText().toString()));
                            final int res = obtenerDatosJSON(resultado);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (res > 0) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "¡Error al actualizar!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.hide();
                                        Toast.makeText(getApplicationContext(), "Se actualizo correctamente la venta.", Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(getApplicationContext(), resultado ,Toast.LENGTH_SHORT).show();
                                        txtBuscar.setText("");
                                        txtArticuloAct.setText("Articulo");
                                        txtModeloAct.setText("");
                                        txtPrecioAct.setText("");
                                        txtCantidadAct.setText("");
                                    }
                                }
                            });
                        }
                    };
                    thread.start();
                } else {
                    Toast.makeText(getApplicationContext(), "Verifique su conexión a internet",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    // ==== INICIO DE CODIGO BUSCAR ====
    public String enviarDatosGET(String venta){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/buscarVenta.php?";
        String url_local = "http://192.168.1.6/ServiciosWeb/buscarVenta.php?";

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "venta=" + venta);
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

    //METODO PARA CARGAR LOS DATOS EN LOS TEXTVIEW
    public void cargarDatos(String response){
        try{
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                txtArticuloAct.setText(jsonArray.getJSONObject(i).getString("marca"));
                txtModeloAct.setText(jsonArray.getJSONObject(i).getString("modelo"));
                txtPrecioAct.setText(jsonArray.getJSONObject(i).getString("precio")) ;
                txtCantidadAct.setText(jsonArray.getJSONObject(i).getString("cantidad"));
            }
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error: " + ex, Toast.LENGTH_SHORT).show();
        }
    }

    public int validarDatosJSON(String response){
        int res = 0;
        try {
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() > 0 ){
                res = 1;
            }
        }catch (Exception e){}
        return res;
    }
    // ==== FIN DE CODIGO BUSCAR=====

    // ==== INICIO CODIGO ACTUALIZAR ====
    public String actualizarDatosGET(String venta, String marca, String modelo, int precio, int cantidad){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/actualizarVenta.php";
        String url_local = "http://192.168.1.6/ServiciosWeb/actualizarVenta.php";
        String mod = modelo.replace(" ", "%20");
        String mar = marca.replace(" ", "%20");

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "?venta=" + venta + "&marca=" + mar + "&modelo=" + mod + "&precio=" + precio + "&cantidad=" + cantidad);
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
    // ==== FIN CODIGO ACTUALIZAR ====
}
