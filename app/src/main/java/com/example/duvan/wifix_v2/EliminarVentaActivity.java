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
import android.widget.ImageButton;
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

public class EliminarVentaActivity extends AppCompatActivity {

    EditText txtVenta;
    ImageView btnVenta;
    ListView listVentas;
    private ProgressDialog progressDialog;
    ArrayAdapter<String> adapter;

    int cantidad = 0;
    String modelo = "";
    String articulo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_venta);

        progressDialog = new ProgressDialog(EliminarVentaActivity.this);
        txtVenta = (EditText) findViewById(R.id.txtBuscaVenta);
        btnVenta = (ImageView) findViewById(R.id.btnBuscaVenta);
        listVentas = (ListView) findViewById(R.id.listVentas);

        btnVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarVentas(txtVenta.getText().toString());
            }
        });
        listVentas.setClickable(true);
        listVentas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listVentas.getItemAtPosition(position);
                String str=(String)o;//As you are using Default String Adapter
                alertOneButton();
            }
        });
    }

    public void buscarVentas(final String id_venta){
        //agregas un mensaje en el ProgressDialog
        progressDialog.setMessage("Cargando venta...");
        //muestras el ProgressDialog
        progressDialog.show();
        if (id_venta.isEmpty()){
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Complete los campos", Toast.LENGTH_SHORT).show();
        }else {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    final String resultado = buscarVentaGET(id_venta);
                    final int validar = validarDatosJSON(resultado);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (validar > 0) {
                                progressDialog.dismiss();
                                cargarLista(listarVenta((resultado)));
                                Toast.makeText(getApplicationContext(), "Se ha cargado la venta exitosamente.", Toast.LENGTH_SHORT).show();
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
                                listVentas.setAdapter(adapter);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "No hay ninguna venta registrada con ese ID.", Toast.LENGTH_SHORT).show();
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
        new AlertDialog.Builder(EliminarVentaActivity.this)
                .setIcon(R.drawable.icono)
                .setTitle("Eliminar Venta")
                .setMessage("¿Seguro que deseas eliminar la venta?")
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
                                final String res = eliminarDatosGET(Integer.parseInt(txtVenta.getText().toString()), articulo, modelo, cantidad);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int r = validarDatosJSON(res);
                                        if (r > 0) {
                                            cargarLista(listarProducto((res)));
                                            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listarProducto(res)){
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
                                            listVentas.setAdapter(adapter);
                                            Toast.makeText(getApplicationContext(), "Venta eliminada.", Toast.LENGTH_SHORT).show();
                                            progressDialog.hide();
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "¡Algo paso!" + res, Toast.LENGTH_SHORT).show();
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
    public String buscarVentaGET(String venta) {
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.4/ServiciosWeb/buscarVenta.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/buscarVenta.php?";

        try {
            url = new URL(url_aws + "venta=" + venta);
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

    //CARGAR LA LOS DATOS RECIBIDO EN EL LISTVIEW DE BUSCAR VENTA
    public ArrayList<String> listarVenta(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            for (int i = 0;i<jsonArray.length();i++){
                cantidad = Integer.parseInt(jsonArray.getJSONObject(i).getString("cantidad"));
                articulo = jsonArray.getJSONObject(i).getString("marca");
                modelo = jsonArray.getJSONObject(i).getString("modelo");

                texto = "Marca: " + jsonArray.getJSONObject(i).getString("marca") + "\n"
                        + "Modelo: " + jsonArray.getJSONObject(i).getString("modelo") + "\n"
                        + "Precio: " + jsonArray.getJSONObject(i).getString("precio") + "\n"
                        + "Cantidad: " + jsonArray.getJSONObject(i).getString("cantidad");
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
    }

    //METODO PARA RECIBIR LOS DATOS DEL SERRVIDOR EN JSON
    public String eliminarDatosGET(int id_venta, String articulo, String modelo, int cantidad){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.4/ServiciosWeb/eliminarVenta.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/eliminarVenta.php?";
        String art = articulo.replace(" ", "%20");
        String mod = modelo.replace(" ", "%20");

        try{
            url = new URL(url_aws + "venta=" + id_venta + "&cantidad=" + cantidad + "&articulo=" + art + "&modelo=" + mod);
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

    //CARGAR LA LOS DATOS RECIBIDO EN EL LISTVIEW DE LISTAR EL PRODUCTO
    public ArrayList<String> listarProducto(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            for (int i = 0;i<jsonArray.length();i++){
                cantidad = Integer.parseInt(jsonArray.getJSONObject(i).getString("cantidad"));
                articulo = jsonArray.getJSONObject(i).getString("marca");
                modelo = jsonArray.getJSONObject(i).getString("modelo");
                texto = "ID Producto: " + jsonArray.getJSONObject(i).getString("id_producto") + "\n"
                        + "Articulo: " + jsonArray.getJSONObject(i).getString("articulo") + "\n"
                        + "Modelo: " + jsonArray.getJSONObject(i).getString("modelo") + "\n"
                        + "Precio unitario: " + jsonArray.getJSONObject(i).getString("precioUnitario") + "\n"
                        + "Precio venta: " + jsonArray.getJSONObject(i).getString("precioVenta") + "\n"
                        + "Cantidad: " + jsonArray.getJSONObject(i).getString("cantidad") + "\n"
                        + "Descripcion: " + jsonArray.getJSONObject(i).getString("descripcion");
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
    }

    public void cargarLista(ArrayList<String> lista) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_list_item_1, lista);
        listVentas = (ListView) findViewById(R.id.listVentas);
        listVentas.setAdapter(adapter);
    }
    //===== FIN CODIGO
}
