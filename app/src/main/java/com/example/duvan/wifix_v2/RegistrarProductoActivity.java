package com.example.duvan.wifix_v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.DialogPreference;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RegistrarProductoActivity extends AppCompatActivity {

    Spinner spArticulo;
    EditText modelo, precioUnitario, precioVenta, descripcion, cantidad;
    Button registrar;
    ImageView foto;
    ArrayAdapter<String> adapter;
    Spinner spTienda;

    String path;
    StringRequest stringRequest;
    RequestQueue request;
    Bitmap bitmap;

    private final String CARPETA_RAIZ = "fotos_wifix/";
    private final String RUTA_IMAGEN = CARPETA_RAIZ + "misFotos";

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_producto);

        request = Volley.newRequestQueue(getApplicationContext());  // rq != null
        progressDialog = new ProgressDialog(RegistrarProductoActivity.this);
        spArticulo = (Spinner) findViewById(R.id.spArticuloReg);
        modelo = (EditText) findViewById(R.id.txtModeloRegistrar);
        precioUnitario = (EditText) findViewById(R.id.txtPrecioUnitarioReg);
        precioVenta = (EditText) findViewById(R.id.txtPrecioVentaReg);
        descripcion = (EditText) findViewById(R.id.txtDescripcionRegistrar);
        cantidad = (EditText) findViewById(R.id.txtCantidadRegistrar);
        spTienda = (Spinner) findViewById(R.id.spTienda);
        registrar = (Button) findViewById(R.id.btnRegistrarProducto);
        //cargarFoto = (Button) findViewById(R.id.btnCargarImagen);
        //foto = (ImageView) findViewById(R.id.fotoProducto);

        final String articulo[] = {"Seleccione un articulo","Accesorios","Acuario","Agenda","Audifonos","Baterias","Bloques","Cables","Cargador","Fibras 3D","Fibras 4D",
                "Fibras 5D","Fibras Basicas","Fibras Basicos Chinos","Fibras Biselados","Forros 360","Forros 360 Magnetico","Forros Antishock Basicos",
                "Forros Antishock Boomper","Forros Antishock Chinos","Forros Silicone Case","Gomas basicas","Gomas diseno","Memorias","Repuestos","Tablet","Telefono"};

        //-----CODIGO PARA MOSTRAR LOS DATOS EN EN SPINNER O COMBOBOX
        //MOSTRAR EL LISTADO DE LOS ARTICULOS EN EL SPINNER ARTICULOS
        List<String> listaArticulo;
        final ArrayAdapter adapterSpinner;
        listaArticulo = new ArrayList<>();
        //Cargo articulo en listaArticulo
        Collections.addAll(listaArticulo, articulo);
        //Paso los valores a mi adapter
        adapterSpinner = new ArrayAdapter<String>(this.getApplicationContext(), R.layout.spinner_item_producto, listaArticulo);
        //Linea de código secundario sirve para asignar un layout a los ítems
        adapterSpinner.setDropDownViewResource(R.layout.spinner_item_producto);
        //Muestro los ítems en el spinner, obtenidos gracias al adapter
        spArticulo.setAdapter(adapterSpinner);
        //FIN CODIGO

        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        ConnectivityManager con = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        final String resultado1 = recibirDatosGET();
                        @Override
                        public void run() {
                            cargarSpinner(listaEmpleados(resultado1));
                        }
                    });
                }
            };
            thread.start();
        } else {
            Toast.makeText(getApplicationContext(), "Verifique su conexión a internet",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modelo.getText().toString().isEmpty() || precioUnitario.getText().toString().isEmpty() || precioVenta.getText().toString().isEmpty()
                    || cantidad.getText().toString().isEmpty() || descripcion.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "¡Complete los campos!", Toast.LENGTH_SHORT).show();
                }else {
                    String tienda = "";
                    String[] emp = spTienda.getSelectedItem().toString().split(" - ");
                    for (int i=0; i < emp.length; i++){
                        tienda = emp[0].toString();
                    }
                    //agregas un mensaje en el ProgressDialog
                    progressDialog.setMessage("Cargando...");
                    //muestras el ProgressDialog
                    progressDialog.show();
                    //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
                    final String finalTienda = tienda;
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            final String resultado = registrarDatosGET(spArticulo.getSelectedItem().toString(), modelo.getText().toString(),
                                    Integer.parseInt(precioUnitario.getText().toString()), Integer.parseInt(precioVenta.getText().toString()),
                                    Integer.parseInt(cantidad.getText().toString()), descripcion.getText().toString(), Integer.parseInt(finalTienda));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //cargarWebServices();
                                    int r = obtenerDatosJSON(resultado);
                                    //Condición para validar si los campos estan llenos
                                    if (r == 0) {
                                        progressDialog.hide();
                                        Toast.makeText(getApplicationContext(), "¡Algo malo ocurrio!", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Se ha registrado el producto exitosamente", Toast.LENGTH_SHORT).show();
                                        modelo.setText("");
                                        precioUnitario.setText("");
                                        precioVenta.setText("");
                                        cantidad.setText("");
                                        descripcion.setText("");
                                        //Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
                                        //Toast.makeText(getApplicationContext(), "Se ha registrado la venta exitosamente", Toast.LENGTH_SHORT).show();
                                    }
                                    progressDialog.hide();
                                }
                            });
                        }
                    };
                    thread.start();
                }
                progressDialog.hide();
            }
        });

        /*
        cargarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarImagen();
            }
        });

        if(validarPermisos()){
            cargarFoto.setEnabled(true);
        } else {
            cargarFoto.setEnabled(false);
        }
        */
    }

    /*
    public void cargarImagen(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent, "Seleccione la aplicacion"), 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Uri path = data.getData();
            foto.setImageURI(path);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), path);
                foto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //METODO PARA TOMAR LAS FOTOS DEL CELULAR Y MOSTRARLAS EN UNA IMAGENVIEW
    private void tomarFotografia(){
        File fileImagen = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);
        boolean isCreada = fileImagen.exists();
        String nombreImagen = "";
        if(!isCreada){
            fileImagen.mkdir();
        }

        if(isCreada){
            nombreImagen = (System.currentTimeMillis() / 100) + "jgp";
        }

        path = Environment.getExternalStorageDirectory() + File.separator + RUTA_IMAGEN + File.separator + nombreImagen;
        File imagen = new File(path);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
    }

    //METODO QUE PERMITE VALIDAR LOS PERMISOS DE ESCRTURA Y CAMARA
    private boolean validarPermisos(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }

        if((checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED)
                && (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(CAMERA)) || (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            cargarDialogoRecomendacion();
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA} , 100);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode  == 100) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
                //cargarFoto.setEnabled(true);
            } else {
                solicitarPermisosManual();
            }
        }
    }

    //METODO PARA SOLICITAR PERMISOS DE ESCRITURA Y CAMARA DE FORMA MANUAL
    private void solicitarPermisosManual(){
        final CharSequence[] opciones = {"Si", "No"};
        final AlertDialog.Builder alertaOpciones = new AlertDialog.Builder(RegistrarProductoActivity.this);
        alertaOpciones.setIcon(R.drawable.icono);
        alertaOpciones.setTitle("Desea configurar los permisos de forma manuel");
        alertaOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(opciones[i].equals("Tomar foto")){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Los permisos no fueron aceptados.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }

    private void cargarDialogoRecomendacion(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(RegistrarProductoActivity.this);
        dialog.setIcon(R.drawable.icono);
        dialog.setTitle("Permisos desactivados");
        dialog.setMessage("Debes aceptar los permisos para el correcto funcionamiento de la aplicación Wifix");
        dialog.setPositiveButton("Aceptar" , new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA} , 100);
                }
            };
        });
        dialog.show();
    }

    public String convertirImagenString(Bitmap bitmap){
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, array);
        byte[] imagenByte = array.toByteArray();
        String imagenString = Base64.encodeToString(imagenByte, Base64.DEFAULT);
        return imagenString;
    }
    */

    //----INICIO CODIGO---->
    //METODO PARA OBTENER LOS DATOS DE LOS PRODUCTOS
    public String registrarDatosGET(String articulo, String modelo, int precioUni, int precioVen, int cantidad, String descrip, int bodega){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.23/ServiciosWeb/registrarProducto.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/registrarProducto.php?";
        String mar = articulo.replace(" ", "%20");
        String mod = modelo.replace(" ", "%20");
        String descri= descrip.replace(" ", "%20");

        //String imagen = convertirImagenString(bitmap);

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "articulo=" + mar + "&modelo=" + mod + "&precioUni=" + precioUni + "&precioVen=" + precioVen
                    + "&cantidad=" + cantidad + "&descripcion=" + descri + "&bodega=" + bodega);
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

    //METODO QUE PERMITE OBTENER EL JSON Y RECORRERLO Y SABER SI RECIBIO O NO DATOS
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

    /*
    *METODO QUE CARGA LAS BODEGA REGISTRADAS EN EL SISTEMA
    *CARGA EL ID Y EL NOMBRE DE LA TIENDA O BODEGA
    */
    public String recibirDatosGET(){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.6/ServiciosWeb/empleados.php";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/cargarBodega.php";

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
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

    public ArrayList<String> listaEmpleados(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            for (int i = 0;i<jsonArray.length();i++){
                texto = jsonArray.getJSONObject(i).getString("id_tienda") + " - " + jsonArray.getJSONObject(i).getString("nombre");
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
    }

    public void cargarSpinner(ArrayList<String> empleado){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_list_item_1, empleado);
        spTienda = (Spinner) findViewById(R.id.spTienda);
        spTienda.setAdapter(adapter);
    }

    //NO SE ESTA UTILIZANDO
    //WEB SRVICES DE VOLLEY
    public void cargarWebServices(){
        String url_local = "http://192.168.149.2/ServiciosWeb/registrarProducto.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/registrarServicio.php?";
        stringRequest = new StringRequest(Request.Method.POST, url_aws, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (response.trim().equalsIgnoreCase("Registro")) {
                    Toast.makeText(getApplicationContext(), "Se registro exitosamente el servicio", Toast.LENGTH_SHORT).show();
                    modelo.setText("");
                    precioUnitario.setText("");
                    precioVenta.setText("");
                    cantidad.setText("");
                    descripcion.setText("");

                } else {
                    Toast.makeText(getApplicationContext(), "¡Ocurrio un problema!" + response, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getContext(),"---> "+img[0].toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"ERROR" + error,Toast.LENGTH_SHORT).show();
                Log.i("","Error" + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String marca = spArticulo.getSelectedItem().toString();
                String mod = modelo.getText().toString();
                String precioU = precioUnitario.getText().toString();
                String precioV = precioVenta.getText().toString();
                String cant = cantidad.getText().toString();
                String desc = descripcion.getText().toString();

                //String imagen = convertirImgString(bitmap);
                Map<String, String> parametros = new Hashtable<>();
                parametros.put("articulo", marca);
                parametros.put("modelo", mod);
                parametros.put("precioUni", precioU);
                parametros.put("precioVen", precioV);
                parametros.put("cantidad", cant);
                parametros.put("descripcion", desc);
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
