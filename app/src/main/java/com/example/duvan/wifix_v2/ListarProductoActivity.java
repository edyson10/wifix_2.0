package com.example.duvan.wifix_v2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ListarProductoActivity extends AppCompatActivity {

    ListView listaProductos;
    Button btnListar, btnBuscar;
    EditText buscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_producto);
        listaProductos = (ListView) findViewById(R.id.listaProducto);
        Thread thread = new Thread() {
            @Override
            public void run() {
                final String resultado = enviarDatosGET();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int r = obtenerDatosJSON(resultado);
                        cargarLista(ArregloLista(resultado));
                        Toast.makeText(getApplicationContext(),resultado,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        thread.start();
    }

    //METODO PARA ENVIAR LOS DATOS AL SERVIDOR LOCAL
    //METODO PARA OBTENER UN LISTADO DE LOS EMPLEADOS
    public String enviarDatosGET(){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.4/ServiciosWeb/listarProductos.php";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/listarProductos.php";

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
                texto = "Articulo: "+ jsonArray.getJSONObject(i).getString("articulo")
                        + " \nModelo " + jsonArray.getJSONObject(i).getString("modelo")
                        + " - Cantidad " + jsonArray.getJSONObject(i).getString("cantidad");
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
    }


    //METODO QUE PERMITE CARGAR EL LISTVIEW
    public void cargarLista(ArrayList<String>lista){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_list_item_1,lista);
        listaProductos = (ListView)findViewById(R.id.listaProducto);
        listaProductos.setAdapter(adapter);
    }
}
