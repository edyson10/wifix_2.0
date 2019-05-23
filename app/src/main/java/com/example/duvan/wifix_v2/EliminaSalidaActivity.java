package com.example.duvan.wifix_v2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

public class EliminaSalidaActivity extends AppCompatActivity {

    EditText salida;
    ImageView buscarSalida;
    ListView listSalida;
    private ProgressDialog progressDialog;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elimina_salida);

        progressDialog = new ProgressDialog(EliminaSalidaActivity.this);
        salida = (EditText) findViewById(R.id.txtBuscaSalida);
        buscarSalida = (ImageView) findViewById(R.id.btnBuscaSalida);
        listSalida = (ListView) findViewById(R.id.listSalida);

        buscarSalida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarVentas(salida.getText().toString());
            }
        });
        listSalida.setClickable(true);
        listSalida.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listSalida.getItemAtPosition(position);
                String str=(String)o;//As you are using Default String Adapter
                alertOneButton();
            }
        });
    }

    public void buscarVentas(final String id_salida){
        //agregas un mensaje en el ProgressDialog
        progressDialog.setMessage("Cargando salida...");
        //muestras el ProgressDialog
        progressDialog.show();
        if (id_salida.isEmpty()){
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Complete los campos", Toast.LENGTH_SHORT).show();
        }else {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    final String resultado = buscarSalidaGET(id_salida);
                    final int validar = validarDatosJSON(resultado);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (validar > 0) {
                                progressDialog.dismiss();
                                cargarLista(listarVenta((resultado)));
                                Toast.makeText(getApplicationContext(), "Se ha cargado la salida exitosamente.", Toast.LENGTH_SHORT).show();
                                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listarVenta(resultado)) {
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
                                listSalida.setAdapter(adapter);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "No hay ninguna salida registrada con ese ID.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            };
            thread.start();
        }
    }

    //METODO PARA MOSTRAR DIALOGO PARA QUE EL SERVICIO SE DE POR FINAIZADO
    public void alertOneButton() {
        new AlertDialog.Builder(EliminaSalidaActivity.this)
                .setIcon(R.drawable.icono)
                .setTitle("Eliminar Salida")
                .setMessage("¿Seguro que deseas eliminar la salida?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                final String res = eliminarDatosGET(Integer.parseInt(salida.getText().toString()));
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (res.equalsIgnoreCase("No eliminado")) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "¡Algo paso!", Toast.LENGTH_SHORT).show();
                                            progressDialog.hide();
                                        } else {
                                            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listarVenta(res)){
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
                                            listSalida.setAdapter(adapter);
                                            Toast.makeText(getApplicationContext(), "Salida eliminada.", Toast.LENGTH_SHORT).show();
                                            progressDialog.hide();
                                        }
                                    }
                                });
                            }
                        };
                        thread.start();
                    }
                }).show();
    }

    //===== INICIO CODIGO SERVIDOR Y JSON
    public String buscarSalidaGET(String salida) {
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://172.21.0.130/ServiciosWeb/buscarSalida.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/buscarSalida.php?";

        try {
            url = new URL(url_aws + "salida=" + salida);
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

    //METODO PARA RECIBIR LOS DATOS DEL SERRVIDOR EN JSON
    public String eliminarDatosGET(int id_salida){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://172.21.0.130/ServiciosWeb/eliminarSalida.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/eliminarSalida.php?";

        try{
            url = new URL(url_aws + "salida=" + id_salida);
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

    public void cargarLista(ArrayList<String> lista) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_list_item_1, lista);
        listSalida = (ListView) findViewById(R.id.listSalida);
        listSalida.setAdapter(adapter);
    }

    //CARGAR LA LOS DATOS RECIBIDO EN EL LISTVIEW DE BUSCAR VENTA
    public ArrayList<String> listarVenta(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            for (int i = 0;i<jsonArray.length();i++){
                texto = "ID Salida: " + jsonArray.getJSONObject(i).getString("id_baja") + "\n"
                        + "Tipo salida: " + jsonArray.getJSONObject(i).getString("tipo_baja") + "\n"
                        + "Descripcion: " + jsonArray.getJSONObject(i).getString("descripcion") + "\n"
                        + "Precio: " + jsonArray.getJSONObject(i).getString("precio") + "\n"
                        + "Fecha salida: " + jsonArray.getJSONObject(i).getString("fecha_baja");
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
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
}
