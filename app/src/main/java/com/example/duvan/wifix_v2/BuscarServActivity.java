package com.example.duvan.wifix_v2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.duvan.wifix_v2.Entidades.ServicioVo;
import com.example.duvan.wifix_v2.adapter.Servicios;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class BuscarServActivity extends AppCompatActivity {

    Button buscarServ;
    EditText txtBuscar;
    ImageView foto;

    private ProgressDialog progressDialog;

    ListView listaServicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_servicio);
        progressDialog = new ProgressDialog(BuscarServActivity.this);

        foto = (ImageView) findViewById(R.id.imagenServ);
        listaServicios = (ListView)findViewById(R.id.listaServicios);
        txtBuscar = (EditText) findViewById(R.id.txtBuscarProd);
        buscarServ = (Button) findViewById(R.id.btnBuscarServ);
        buscarServ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtBuscar.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Los campos estan vacios.", Toast.LENGTH_LONG).show();
                }else{
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
                                final String resultado = enviarDatosGET(txtBuscar.getText().toString());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int r = obtenerDatosJSON(resultado);
                                        if (txtBuscar.getText().toString().isEmpty()) {
                                            Toast.makeText(getApplicationContext(), "¡Complete los campos!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (r > 0) {
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Se busco correctamente", Toast.LENGTH_SHORT).show();
                                                cargarLista(ArregloLista(resultado));
                                                progressDialog.hide();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Ese cliente no esta registrado.", Toast.LENGTH_SHORT).show();
                                                progressDialog.hide();
                                            }
                                        }
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
            }
        });


        listaServicios.setClickable(true);
        listaServicios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listaServicios.getItemAtPosition(position);
                String str=(String)o;//As you are using Default String Adapter
                alertOneButton();
            }
        });

    }

    //METODO PARA MOSTRAR DIALOGO PARA QUE EL SERVICIO SE DE POR FINAIZADO
    public void alertOneButton() {
        new AlertDialog.Builder(BuscarServActivity.this)
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
                        Thread thread = new Thread(){
                            @Override
                            public void run() {
                                final String resultado = enviarMantenimientoGET(txtBuscar.getText().toString());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int r = obtenerMantenimientoJSON(resultado);
                                        if(txtBuscar.getText().toString().isEmpty()){
                                            Toast.makeText(getApplicationContext(), "¡Complete los campos!", Toast.LENGTH_SHORT).show();
                                        }else {
                                            if (r > 0) {
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(),"Equipo reparado", Toast.LENGTH_SHORT).show();
                                                progressDialog.hide();
                                            } else {
                                                Toast.makeText(getApplicationContext(),"¡Algo paso!" + resultado, Toast.LENGTH_SHORT).show();
                                                Toast.makeText(getApplicationContext(),txtBuscar.getText().toString(), Toast.LENGTH_SHORT).show();
                                            }
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
    public String enviarDatosGET(String cedula){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.6/ServiciosWeb/buscarServicio.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/buscarServicio.php?";

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "servicio=" + cedula);
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
            String estado = "";

            for (int i=0;i<jsonArray.length();i++){
                if(jsonArray.getJSONObject(i).getString("estado").equalsIgnoreCase("1")){
                    estado= "En reparación";
                }else estado = "Reparado";

                String dato = jsonArray.getJSONObject(i).getString("foto");
                byte[] byteCode = Base64.decode(dato,Base64.DEFAULT);
                Bitmap imagen = BitmapFactory.decodeByteArray(byteCode,0,byteCode.length);

                texto = "Fecha del Servicio: " + jsonArray.getJSONObject(i).getString("fechaServicio") + "\n"
                        + "Cedula Empleado: " + jsonArray.getJSONObject(i).getString("cedulaEmp") + "\n"
                        + "Marca: " + jsonArray.getJSONObject(i).getString("marca") + "\n"
                        + "Modelo: " + jsonArray.getJSONObject(i).getString("modelo") + "\n"
                        + "Falla : " + jsonArray.getJSONObject(i).getString("falla") + "\n"
                        + "Diagnostico : " + jsonArray.getJSONObject(i).getString("descripcion") + "\n"
                        + "Observación : " + jsonArray.getJSONObject(i).getString("observacion") + "\n"
                        + "Costo reparación: " + jsonArray.getJSONObject(i).getString("precio") + "\n"
                        + "Clave : " + jsonArray.getJSONObject(i).getString("clave") + "\n"
                        + "Estado : " + estado + "\n"
                        + "Fecha de Entrega: " + jsonArray.getJSONObject(i).getString("fechaEntrega");
                if (imagen != null) {
                    foto.setImageBitmap(imagen);
                }else foto.setImageResource(R.drawable.no_disponible);
                listado.add(texto);
            }

        }catch (Exception e){}
        return listado;
    }

    //METODO QUE PERMITE CARGAR EL LISTVIEW
    public void cargarLista(ArrayList<String>lista){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_list_item_1,lista);
        listaServicios = (ListView)findViewById(R.id.listaServicios);
        listaServicios.setAdapter(adapter);
    }

    //METODO PARA ENVIAR EL MANTENIMIENTO REALIZADO
    public String enviarMantenimientoGET(String cedula){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.6/ServiciosWeb/registrarMantenimiento.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/registrarMantenimiento.php?";

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_local + "cedula=" + cedula);
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

    public int obtenerMantenimientoJSON(String response){
        int res = 0;
        try {
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() > 0 ){
                res = 1;
            }
        }catch (Exception e){}
        return res;
    }
}
