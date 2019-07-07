package com.example.duvan.wifix_v2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
import java.util.Collections;
import java.util.List;

public class EditarProductoActivity extends AppCompatActivity {

    Spinner spArticuloElim;
    EditText cantidadEl;
    TextView productoEl;
    Button eliminar,cargar;
    ListView lista;
    ArrayAdapter<String> adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);

        progressDialog = new ProgressDialog(EditarProductoActivity.this);
        spArticuloElim = (Spinner) findViewById(R.id.spArticuloProdElim);
        lista = (ListView) findViewById(R.id.listModelosElim);
        cantidadEl = (EditText) findViewById(R.id.txtCantidadEliminar);
        productoEl = (TextView) findViewById(R.id.txtProductoEliminar);
        String articulo[] = {"Seleccione un articulo", "Accesorios", "Acuario", "Agenda", "Audifonos", "Baterias", "Bloques", "Cables", "Cargador",
                "Fibras 3D", "Fibras 4D", "Fibras 5D", "Fibras Biselados", "Fibras Basicas", "Fibras Basicos Chinos", "Forros Antishock Basicos",
                "Forros Antishock Chinos", "Forros Antishock Boomper", "Gomas basicas", "Forros Gomas Diseño", "Forros Silicone Case", "Forros 360",
                "Forros 360 Magneticos", "Memorias", "Tablet", "Telefono"};

        //-----CODIGO PARA MOSTRAR LOS DATOS EN EN SPINNER O COMBOBOX
        //MOSTRAR EL LISTADO DE LOS ARTICULOS EN EL SPINNER ARTICULOS
        List<String> listaArticulo;
        final ArrayAdapter adapterSpinner;
        listaArticulo = new ArrayList<>();
        //Cargo articulo en listaArticulo
        Collections.addAll(listaArticulo, articulo);
        //Paso los valores a mi adapter
        adapterSpinner = new ArrayAdapter<String>(getApplication(), R.layout.spinner_item_producto, listaArticulo);
        //Linea de código secundario sirve para asignar un layout a los ítems
        adapterSpinner.setDropDownViewResource(R.layout.spinner_item_producto);
        //Muestro los ítems en el spinner, obtenidos gracias al adapter
        spArticuloElim.setAdapter(adapterSpinner);
        //FIN CODIGO

        eliminar = (Button) findViewById(R.id.btnEliminarProd);
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertOneButton();
            }
        });

        cargar = (Button) findViewById(R.id.btnCargarModProdElim);
        cargar.setOnClickListener(new View.OnClickListener() {
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
                            final String modelo = obtenerDatosGET(spArticuloElim.getSelectedItem().toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //cargarLista(arrayList(modelo));
                                    //Toast.makeText(getApplicationContext(),modelo,Toast.LENGTH_SHORT).show();
                                    adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList(modelo)){
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
                                    lista.setAdapter(adapter);
                                    productoEl.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            adapter.getFilter().filter(s);
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {

                                        }
                                    });
                                    progressDialog.hide();
                                }
                            });
                        }
                    };
                    thread.start();
                }else {
                    Toast.makeText(getApplicationContext(), "Verifique su conexión a internet",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                lista.setClickable(true);
                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Object o = lista.getItemAtPosition(position);
                        String str=(String)o;//As you are using Default String Adapter
                        productoEl.setText(str);
                    }
                });
            }
        });
    }

    //METODO QUE LE MUESTRE EN PANTALLA  UN ALERT DIALOG SI DESEA ELIMINAR CIERTA CANTIDAD
    public void alertOneButton (){
        new AlertDialog.Builder(EditarProductoActivity.this)
                .setIcon(R.drawable.logo_wifix)
                .setTitle("Producto")
                .setMessage("¿Seguro desea eliminar esa cantidad?")
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
                        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
                        ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = con.getActiveNetworkInfo();
                        if (networkInfo != null && networkInfo.isConnected()) {
                            if (cantidadEl.getText().toString().isEmpty() || productoEl.getText().toString().isEmpty()) {
                                Toast.makeText(getApplicationContext(), "¡Complete los campos!", Toast.LENGTH_LONG).show();
                            } else {
                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        final String resultado = enviarDatosGET(spArticuloElim.getSelectedItem().toString(), productoEl.getText().toString(),
                                                Integer.parseInt(cantidadEl.getText().toString()));
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                int r = obtenerDatosJSON(resultado);
                                                //Condición paa validar si los campos estan llenos

                                                if (r > 0) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Se ha eliminado la cantidad exitosamente", Toast.LENGTH_SHORT).show();
                                                    productoEl.setText("");
                                                    cantidadEl.setText("");
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "¡Algo malo ocurrio!", Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(getApplicationContext(), "Mensaje " + resultado, Toast.LENGTH_LONG).show();
                                                    progressDialog.hide();
                                                    //Toast.makeText(getContext(),String.valueOf(r),Toast.LENGTH_SHORT).show();
                                                }

                                                progressDialog.hide();
                                            }
                                        });
                                    }
                                };
                                thread.start();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "Verifique su conexión a internet",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }).show();
    }

    //METODO PARA ENVIAR LOS DATOS AL SERVIDOR LOCAL DE ACTUALIZAR
    public String enviarDatosGET(String marca, String modelo, int cantidad) {
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/eliminarProducto.php?";
        String url_local = "http://172.21.0.130/ServiciosWeb/eliminarProducto.php?";
        String mar = marca.replace(" ", "%20");
        String mod = modelo.replace(" ", "%20");

        try {
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            //http://localhost/ServiciosWeb/eliminarProducto.php?articulo=Forros2.0&modelo=Perrita&cantidad=10
            url = new URL(url_aws + "articulo=" + mar + "&modelo=" + mod + "&cantidad=" + cantidad);
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

    public int obtenerDatosJSON(String response) {
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

    //----INICIO CODIGO---->
    //METODO PARA OBTENER LOS DATOS DE LOS PRODUCTOS DE OBTENER LOS MODELOS
    public String obtenerDatosGET(String marca) {
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://172.21.0.130/ServiciosWeb/listarProductos.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/listarProductos.php?";
        String mar = marca.replace(" ", "%20");

        try {
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "articulo=" + mar);
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

    //ARREGLO QUE CARGA EL JSON EL EL LISTVIEW
    public ArrayList<String> arrayList(String response) {
        ArrayList<String> listado = new ArrayList<String>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            for (int i = 0; i < jsonArray.length(); i++) {
                texto = jsonArray.getJSONObject(i).getString("modelo");
                listado.add(texto);
            }
        } catch (Exception e) {
        }
        return listado;
    }

    //METODO QUE PERMITE CARGAR EL LISTVIEW
    public void cargarLista(ArrayList<String> listaProd) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_list_item_1, listaProd);
        lista = (ListView) findViewById(R.id.listModelosElim);
        lista.setAdapter(adapter);
    }
}
