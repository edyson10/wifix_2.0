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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class ListarServicioActivity extends AppCompatActivity {

    EditText txtServicio;
    ImageButton buscarServ;
    ListView listaServicio;
    private ProgressDialog progressDialog;
    ArrayAdapter<String> adapter;

    String cedulaCl = "";
    String servicio ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_servicio);

        txtServicio = (EditText) findViewById(R.id.txtBuscarServicio);
        buscarServ = (ImageButton) findViewById(R.id.btnBuscaServicio);
        listaServicio = (ListView) findViewById(R.id.listServEmpleado);
        progressDialog = new ProgressDialog(ListarServicioActivity.this);

        //agregas un mensaje en el ProgressDialog
        progressDialog.setMessage("Cargando servicios...");
        //muestras el ProgressDialog
        progressDialog.show();
        Thread thread = new Thread(){
            @Override
            public void run() {
                final String resultado = listarServiciosGET();
                final int validar = validarDatosJSON(resultado);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (validar == 0){
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "No hay ningún servicio registrado hoy.", Toast.LENGTH_SHORT).show();
                        }else {
                            progressDialog.dismiss();
                            cargarLista(listarServicioDia((resultado)));
                            Toast.makeText(getApplicationContext(), "Se han cargado los servicios exitosamente.", Toast.LENGTH_SHORT).show();
                            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listarServicioDia(resultado)){
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
                            listaServicio.setAdapter(adapter);
                        }
                    }
                });
            }
        };
        thread.start();
        listaServicio.setClickable(true);
        listaServicio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listaServicio.getItemAtPosition(position);
                String str=(String)o;//As you are using Default String Adapter
                alertOneButton();
            }
        });

        buscarServ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //agregas un mensaje en el ProgressDialog
                progressDialog.setMessage("Cargando...");
                //muestras el ProgressDialog
                progressDialog.show();
                cargarServicios(txtServicio.getText().toString());
            }
        });
    }

    public void cargarServicios(final String servicio){
        Thread thread = new Thread(){
            @Override
            public void run() {
                final String resultado = enviarDatosGET(servicio);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int res = validarDatosJSON(resultado);
                        if (res > 0){
                            progressDialog.dismiss();
                            cargarLista(listarServicioDia((resultado)));
                            Toast.makeText(getApplicationContext(), "Se ha cargado el servicio exitosamente.", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_SHORT).show();
                            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listarServicioDia(resultado)){
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
                            listaServicio.setAdapter(adapter);
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "No hay ningun servicio registrado con ese parametro.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        thread.start();
    }

    //METODO PARA MOSTRAR DIALOGO PARA QUE EL SERVICIO SE DE POR FINAIZADO
    public void alertOneButton() {
        new AlertDialog.Builder(ListarServicioActivity.this)
                .setIcon(R.drawable.icono)
                .setTitle("Mantenimiento")
                .setMessage("¿Has terminado de reparar el equipo?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //agregas un mensaje en el ProgressDialog
                        progressDialog.setMessage("Cargando...");
                        //muestras el ProgressDialog
                        progressDialog.show();
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                final String resultado = enviarMantenimientoGET(servicio);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int r = validarDatosJSON(resultado);
                                        if (r > 0) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Equipo reparado", Toast.LENGTH_SHORT).show();
                                            progressDialog.hide();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "¡Algo paso!", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_SHORT).show();
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

                servicio = jsonArray.getJSONObject(i).getString("id_servicio");

                texto = "Servicio: " + (i + 1)+ "\n"
                        + "Orden del servicio: " + jsonArray.getJSONObject(i).getString("id_servicio") + "\n"
                        + "Orden de reparación: " + jsonArray.getJSONObject(i).getString("id_reparacion") + "\n"
                        + "Estado: " + estado + "\n"
                        + "Fecha reparacion: " + jsonArray.getJSONObject(i).getString("fecha_reparacion") + "\n"
                        + "Cedula cliente: " + jsonArray.getJSONObject(i).getString("cedulaCli") + "\n"
                        + "Marca: " + jsonArray.getJSONObject(i).getString("marca") + "\n"
                        + "Modelo: " + jsonArray.getJSONObject(i).getString("modelo") + "\n"
                        + "Falla : " + jsonArray.getJSONObject(i).getString("falla") + "\n"
                        + "Bodega: " + jsonArray.getJSONObject(i).getString("bodega") + "\n"
                        + "Repuesto: " + jsonArray.getJSONObject(i).getString("repuesto") + "\n"
                        + "Detalle: " + jsonArray.getJSONObject(i).getString("detalle_reparacion") + "\n"
                        + "Costo reparación: $ " + jsonArray.getJSONObject(i).getString("costo_reparacion") + "\n"
                        + "Precio reparación: $ " + jsonArray.getJSONObject(i).getString("precio_reparacion");
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
        listaServicio = (ListView) findViewById(R.id.listServEmpleado);
        listaServicio.setAdapter(adapter);
    }

    //===== INICIO CODIGO SERVIDOR Y JSON
    //METODO PARA RECIBIR LOS DATOS DEL SERRVIDOR EN JSON
    public String listarServiciosGET(){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.6/ServiciosWeb/listarServicioDia.php";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/listarServicioDia.php";

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
    //===== FIN CODIGO

    //===== INICIO CODIGO DEL SERVIDOR Y JSON
    //METODO PARA ENVIAR EL MANTENIMIENTO REALIZADO
    public String enviarMantenimientoGET(String serv) {
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.6/ServiciosWeb/registrarMantenimiento.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/registrarMantenimiento.php?";

        try {
            url = new URL(url_aws + "servicio=" + serv);
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
    //===== FIN CODIGO

    //===== INICIO CODIGO DE RECIBIR DATOS DEL SERVIDOR Y DE JSON
    //METODO PARA ENVIAR LOS DATOS AL SERVIDOR LOCAL
    public String enviarDatosGET(String cedula) {
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.6/ServiciosWeb/buscarServicio.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/buscarServicio.php?";

        try {
            url = new URL(url_aws + "servicio=" + cedula);
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
    //===== FIN CODIGO
}
