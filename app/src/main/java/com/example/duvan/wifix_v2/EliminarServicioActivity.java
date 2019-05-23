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

public class EliminarServicioActivity extends AppCompatActivity {

    EditText servicio;
    ImageView buscarServicio;
    ListView listServicio;
    private ProgressDialog progressDialog;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_servicio);
        progressDialog = new ProgressDialog(EliminarServicioActivity.this);
        servicio = (EditText) findViewById(R.id.txtBuscarServ);
        buscarServicio = (ImageView) findViewById(R.id.btnBuscarServicio);
        listServicio = (ListView) findViewById(R.id.listServicio);

        buscarServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarServicio(servicio.getText().toString());
            }
        });
        listServicio.setClickable(true);
        listServicio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listServicio.getItemAtPosition(position);
                String str=(String)o;//As you are using Default String Adapter
                alertOneButton();
            }
        });
    }

    public void buscarServicio(final String id_servicio){
        //agregas un mensaje en el ProgressDialog
        progressDialog.setMessage("Cargando servicio...");
        //muestras el ProgressDialog
        progressDialog.show();
        if (id_servicio.isEmpty()){
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Complete los campos", Toast.LENGTH_SHORT).show();
        }else {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    final String resultado = buscarServicioGET(id_servicio);
                    final int validar = validarDatosJSON(resultado);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (validar > 0) {
                                progressDialog.dismiss();
                                cargarLista(listarServicio((resultado)));
                                Toast.makeText(getApplicationContext(), "Se ha cargado el servicio exitosamente.", Toast.LENGTH_SHORT).show();
                                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listarServicio(resultado)) {
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
                                listServicio.setAdapter(adapter);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "No hay ningun servicio registrada con ese ID.", Toast.LENGTH_SHORT).show();
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
        new AlertDialog.Builder(EliminarServicioActivity.this)
                .setIcon(R.drawable.icono)
                .setTitle("Eliminar Servicio")
                .setMessage("¿Seguro que deseas eliminar el servicio?")
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
                                final String res = eliminarDatosGET(Integer.parseInt(servicio.getText().toString()));
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int r = validarDatosJSON(res);
                                        if (r > 0) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "¡Algo paso!", Toast.LENGTH_SHORT).show();
                                            progressDialog.hide();
                                        } else {
                                            cargarLista(listarServicio((res)));
                                            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listarServicio(res)){
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
                                            listServicio.setAdapter(adapter);
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
    public String buscarServicioGET(String servicio) {
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://172.21.0.130/ServiciosWeb/buscarServicio.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/buscarServicio.php?";

        try {
            url = new URL(url_aws + "servicio=" + servicio);
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
    public String eliminarDatosGET(int id_servicio){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://172.21.0.130/ServiciosWeb/eliminarServicio.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/eliminarServicio.php?";

        try{
            url = new URL(url_aws + "servicio=" + id_servicio);
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

    //CARGAR LA LOS DATOS RECIBIDO EN EL LISTVIEW
    public ArrayList<String> listarServicio(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            String estado = "";

            for (int i = 0;i<jsonArray.length();i++){
                if (jsonArray.getJSONObject(i).getString("estado").equalsIgnoreCase("1")) {
                    estado = "En reparación";
                } else estado = "Reparado";

                texto = "Orden del servicio: 000" + jsonArray.getJSONObject(i).getString("id_servicio") + "\n"
                        + "Fecha diagnostico: " + jsonArray.getJSONObject(i).getString("fecha_diagnostico") + "\n"
                        + "Cedula Empleado: " + jsonArray.getJSONObject(i).getString("cedulaEmp") + "\n"
                        + "Marca: " + jsonArray.getJSONObject(i).getString("marca") + "\n"
                        + "Modelo: " + jsonArray.getJSONObject(i).getString("modelo") + "\n"
                        + "Falla : " + jsonArray.getJSONObject(i).getString("falla") + "\n"
                        + "Diagnostico: " + jsonArray.getJSONObject(i).getString("diagnostico") + "\n"
                        + "Observación: " + jsonArray.getJSONObject(i).getString("observacion") + "\n"
                        + "Precio reparación: " + jsonArray.getJSONObject(i).getString("precio") + "\n"
                        + "Cedula Cliente: " + jsonArray.getJSONObject(i).getString("cedulaCli") + "\n"
                        + "Fecha de Entrega: " + jsonArray.getJSONObject(i).getString("fechaEntrega") + "\n"
                        + "Clave: " + jsonArray.getJSONObject(i).getString("clave") + "\n"
                        + "Estado: " + estado;
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
    }

    public void cargarLista(ArrayList<String> lista) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_list_item_1, lista);
        listServicio = (ListView) findViewById(R.id.listServicio);
        listServicio.setAdapter(adapter);
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
