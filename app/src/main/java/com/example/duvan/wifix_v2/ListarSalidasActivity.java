package com.example.duvan.wifix_v2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class ListarSalidasActivity extends AppCompatActivity {

    ListView listaSalidas;
    private ProgressDialog progressDialog;
    ArrayAdapter<String> adapter;
    String cedula_U;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_salidas);

        progressDialog = new ProgressDialog(ListarSalidasActivity.this);
        cargarPreferencias();
        listaSalidas = (ListView) findViewById(R.id.listaSalida);
        //agregas un mensaje en el ProgressDialog
        progressDialog.setMessage("Cargando servicios...");
        //muestras el ProgressDialog
        progressDialog.show();
        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    final String resultado = listarVentasGET();
                    final int validar = validarDatosJSON(resultado);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (validar == 0) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "No hay ventas registrado hoy.", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                cargarLista(listarVentasDia((resultado)));
                                Toast.makeText(getApplicationContext(), "Se han cargado las ventas exitosamente.", Toast.LENGTH_SHORT).show();
                                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listarVentasDia(resultado)) {
                                    //PERMITE CAMBIAR DE COLOR EL ISTVIEW EN UN ACTIVITY YA QE LO MUESTRA LAS LETRAS EN BLANCO
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View view = super.getView(position, convertView, parent);
                                        // Initialize a TextView for ListView each Item
                                        TextView tv = (TextView) view.findViewById(android.R.id.text1);
                                        // Set the text color of TextView (ListView Item)
                                        tv.setTextColor(Color.BLACK);
                                        return view;
                                    }
                                };
                                listaSalidas.setAdapter(adapter);
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

    //CARGAR LA LOS DATOS RECIBIDO EN EL LISTVIEW
    public ArrayList<String> listarVentasDia(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            String estado = "";
            for (int i = 0;i<jsonArray.length();i++){
                texto = "ID Baja: " + jsonArray.getJSONObject(i).getString("id_baja") + "\n"
                        + "Nombre: " + jsonArray.getJSONObject(i).getString("nombre") + "\n"
                        + "Tipo: " + jsonArray.getJSONObject(i).getString("tipo_baja") + "\n"
                        + "Descripcion: " + jsonArray.getJSONObject(i).getString("descripcion") + "\n"
                        + "Precio: " + jsonArray.getJSONObject(i).getString("precio") + "\n"
                        + "Fecha salida: $ " + jsonArray.getJSONObject(i).getString("fecha_baja");
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
    }

    //METODO QUE PERMITE CARGAR EL LISTVIEW
    public void cargarLista(ArrayList<String> lista) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_list_item_1, lista);
        listaSalidas = (ListView) findViewById(R.id.listaSalida);
        listaSalidas.setAdapter(adapter);
    }

    //===== INICIO CODIGO SERVIDOR Y JSON
    public String listarVentasGET() {
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.6/ServiciosWeb/listarSalidas.php";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/listarSalidas.php?empleado=";

        try {
            url = new URL(url_aws + cedula_U);
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

    private void cargarPreferencias(){
        SharedPreferences preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        cedula_U = preferences.getString("cedula","");
    }
}
