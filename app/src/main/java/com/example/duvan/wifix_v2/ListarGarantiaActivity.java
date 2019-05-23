package com.example.duvan.wifix_v2;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

public class ListarGarantiaActivity extends AppCompatActivity {

    ListView listaGarantia;
    private ProgressDialog progressDialog;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_garantia);

        progressDialog = new ProgressDialog(ListarGarantiaActivity.this);
        listaGarantia = (ListView) findViewById(R.id.listGarantia);
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
                    final String resultado = listarServiciosGET();
                    final int validar = validarDatosJSON(resultado);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (validar == 0) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "No hay ninguna garantia registrada.", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                cargarLista(listarServicioDia((resultado)));
                                Toast.makeText(getApplicationContext(), "Se han cargado las garantias exitosamente.", Toast.LENGTH_SHORT).show();
                                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listarServicioDia(resultado)) {
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
                                listaGarantia.setAdapter(adapter);
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

    //===== INICIO CODIGO SERVIDOR Y JSON
    //METODO PARA RECIBIR LOS DATOS DEL SERRVIDOR EN JSON
    public String listarServiciosGET(){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.6/ServiciosWeb/listarGarantia.php";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/listarGarantia.php";

        try{
            url = new URL(url_aws);
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

    //CARGAR LA LOS DATOS RECIBIDO EN EL LISTVIEW
    public ArrayList<String> listarServicioDia(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            String estado = "";
            for (int i = 0;i<jsonArray.length();i++){
                if (jsonArray.getJSONObject(i).getString("estado").equalsIgnoreCase("1")) {
                    estado = "En reparación";
                } else estado = "Reparado";

                texto = "Servicio: " + (i + 1)+ "\n"
                        + "Orden del servicio: 000" + jsonArray.getJSONObject(i).getString("id_servicio") + "\n"
                        + "Cedula Empleado: " + jsonArray.getJSONObject(i).getString("cedulaEmp") + "\n"
                        + "Marca: " + jsonArray.getJSONObject(i).getString("marca") + "\n"
                        + "Modelo: " + jsonArray.getJSONObject(i).getString("modelo") + "\n"
                        + "Falla : " + jsonArray.getJSONObject(i).getString("falla") + "\n"
                        + "Diagnostico: " + jsonArray.getJSONObject(i).getString("diagnostico") + "\n"
                        + "Observación: " + jsonArray.getJSONObject(i).getString("observacion") + "\n"
                        + "Clave: " + jsonArray.getJSONObject(i).getString("clave") + "\n"
                        + "Precio reparación: " + jsonArray.getJSONObject(i).getString("precio") + "\n"
                        + "Cedula Cliente: " + jsonArray.getJSONObject(i).getString("cedulaCli") + "\n"
                        + "Estado: " + estado + "\n"
                        + "Fecha de Entrega: " + jsonArray.getJSONObject(i).getString("fechaEntrega");
                 /*
                if (imagen != null) {
                    foto.setImageBitmap(imagen);
                }else foto.setImageResource(R.drawable.no_disponible);
                */
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
    }

    //METODO QUE PERMITE CARGAR EL LISTVIEW
    public void cargarLista(ArrayList<String> lista) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_list_item_1, lista);
        listaGarantia = (ListView) findViewById(R.id.listGarantia);
        listaGarantia.setAdapter(adapter);
    }
    //===== FIN CODIGO
}
