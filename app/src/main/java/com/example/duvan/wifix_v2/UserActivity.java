package com.example.duvan.wifix_v2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duvan.wifix_v2.Fragments.EmpleadoFragment;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {

    //CODIGO
    Button eliminar;
    EditText cedula;
    ListView listaEmpleado;
    ArrayAdapter<String> adapter;
    private ProgressDialog progressDialog;
    //FIN

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Thread thread = null;
        progressDialog = new ProgressDialog(UserActivity.this);
        cedula = (EditText) findViewById(R.id.txtCedulaEmpleado);
        eliminar = (Button) findViewById(R.id.btnElimarEmpleado);
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        final String resultado = eliminarDatosGET(cedula.getText().toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int r = obtenerDatosJSON(resultado);
                                if (cedula.getText().toString().isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "¡Complete los campos!", Toast.LENGTH_LONG).show();
                                } else {
                                    if (r > 0) {
                                        Toast.makeText(getApplicationContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(getApplicationContext(), cedula.getText().toString(), Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(getApplicationContext(), "Mensaje" + resultado, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Se ha eliminado exitosamente", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                                        startActivity(intent);
                                        //Toast.makeText(getApplicationContext(), "Mensaje" + resultado, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                };
                thread.start();
            }
        });

        progressDialog.setIcon(R.drawable.icono);
        //agregas un mensaje en el ProgressDialog
        progressDialog.setMessage("Cargando datos...");
        //muestras el ProgressDialog
        progressDialog.show();
        listaEmpleado = (ListView) findViewById(R.id.listaEmpleados);
        thread = new Thread() {
            @Override
            public void run() {
                final String resultado = enviarDatosGET();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        int r = obtenerDatosJSON(resultado);
                        cargarLista(ArregloLista(resultado));
                        //Toast.makeText(getApplicationContext(),resultado,Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "Se ha cargado los empleados exitosamente.", Toast.LENGTH_SHORT).show();
                        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, ArregloLista(resultado)) {
                            //PERMITE CAMBIAR DE COLOR EL LISTVIEW EN UN ACTIVITY YA QE LO MUESTRA LAS LETRAS EN BLANCO
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
                        listaEmpleado.setAdapter(adapter);
                    }
                });
            }
        };
        thread.start();
        //FIN
    }

    //METODO PARA MOSTRAR DIALOGO PARA QUE EL SERVICIO SE DE POR FINAIZADO
    public void alertOneButton() {
        new AlertDialog.Builder(UserActivity.this)
                .setIcon(R.drawable.icono)
                .setTitle("Eliminar empleado")
                .setMessage("¿Seguro que deseas eliminar el empleado?")
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
                                final String res = eliminarDatosGET(cedula.getText().toString());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int r = validarDatosJSON(res);
                                        if (r > 0) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "¡Algo paso!", Toast.LENGTH_SHORT).show();
                                            progressDialog.hide();
                                        } else {
                                            cargarLista(ArregloLista((res)));
                                            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, ArregloLista(res)){
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
                                            listaEmpleado.setAdapter(adapter);
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

    //METODO PARA ENVIAR LOS DATOS AL SERVIDOR LOCAL
    //METODO PARA OBTENER UN LISTADO DE LOS EMPLEADOS
    public String enviarDatosGET(){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.6/ServiciosWeb/listarEmpleado.php";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/listarEmpleado.php";

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO
            // YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
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

    public ArrayList<String> ArregloLista(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            for (int i=0;i<jsonArray.length();i++){
                texto = "Nombre: "+jsonArray.getJSONObject(i).getString("nombre")
                        + "\nCedula empleado: " + jsonArray.getJSONObject(i).getString("cedula")
                        + "\nUtilidad: "+jsonArray.getJSONObject(i).getString("Utilidad");
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
    }


    //METODO QUE PERMITE CARGAR EL LISTVIEW
    public void cargarLista(ArrayList<String>lista){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_list_item_1,lista);
        listaEmpleado = (ListView)findViewById(R.id.listaEmpleados);
        listaEmpleado.setAdapter(adapter);
    }

    //METODO PARA ENVIAR LOS DATOS AL SERVIDOR LOCAL
    //METODO PARA ELIMINAR UN EMPLEADOS
    public String eliminarDatosGET(String cedula){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/eliminarEmpleado.php?";

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO
            // YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "cedula=" + cedula);
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
}
