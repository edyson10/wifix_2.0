package com.example.duvan.wifix_v2;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class DiagnosticoActivity extends AppCompatActivity {

    private final String CARPETA_RAIZ = "Wifix/";
    private final String RUTA_IMAGEN = CARPETA_RAIZ+"wifix";
    String path = "";

    final int COD_SELECCIONA = 10;
    final int COD_FOTO = 20;

    Bitmap bitmap = null;

    Button guardarServ, cargarImages;

    TextView fechaEn;
    Calendar mCurrentDate;
    int dia, mes, anio;

    EditText cedulaE, marcaE, modeloE, fallaExpresadaE, diagnosticoE, observacionE, precioReparacionE,cedulaClienteE, claveE;
    ImageView imagen;

    String recuperado = "";
    private ProgressDialog progressDialog;

    StringRequest stringRequest;
    RequestQueue request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostico);

        progressDialog = new ProgressDialog(DiagnosticoActivity.this);
        //CODIGO DE LOS EDITTEXT QUE INGRESAN
        cedulaE = (EditText) findViewById(R.id.txtCedulaDiag);
        marcaE = (EditText) findViewById(R.id.txtMarcaDiag);
        modeloE = (EditText) findViewById(R.id.txtModeloDiag);
        fallaExpresadaE = (EditText) findViewById(R.id.txtFallaExpDiag);
        diagnosticoE = (EditText) findViewById(R.id.txtDiagnosticoDiag);
        observacionE = (EditText) findViewById(R.id.txtObservcionDiag);
        precioReparacionE = (EditText) findViewById(R.id.txtPrecioReparacionDiag);
        cedulaClienteE = (EditText) findViewById(R.id.txtCedulaClienteDiag);
        claveE = (EditText) findViewById(R.id.txtClaveEquipoDiag);
        //FIN DEL CODIGO

        //CODIGO FECHA DE ENTREGA ESTIPULADA
        fechaEn = (TextView) findViewById(R.id.fechaEntDiag);
        mCurrentDate = Calendar.getInstance();
        dia = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        mes = mCurrentDate.get(Calendar.MONTH);
        anio = mCurrentDate.get(Calendar.YEAR);
        fechaEn.setText(anio + "-" + (mes + 1) + "-" + dia);
        fechaEn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getApplicationContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker , int year , int monthOfYear,  int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        fechaEn.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                    }
                }, anio, mes, dia);
                //Linea de codigo importante para que muestre la ventana de dialogo para seleccionar la fecha, sino genera error.
                datePickerDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                datePickerDialog.show();
            }
        });
        //FIN CODIGO FECHAS

        Bundle recupera = getIntent().getExtras();
        if (recupera != null) {
            recuperado = recupera.getString("cedula");
        }

        request = Volley.newRequestQueue(getApplicationContext());  // rq != null
        //CODIGO PARA AGREGAR EL SERVICIO A LA BASE DE DATOS
        guardarServ = (Button) findViewById(R.id.btnGuardarServDiag);
        guardarServ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle recupera = getIntent().getExtras();
                if (recupera != null) {
                    recuperado = recupera.getString("cedula");
                }
                if (cedulaE.getText().toString().isEmpty() || marcaE.getText().toString().isEmpty() || modeloE.getText().toString().isEmpty()
                        || fallaExpresadaE.getText().toString().isEmpty() || diagnosticoE.getText().toString().isEmpty()
                        || observacionE.getText().toString().isEmpty() || cedulaClienteE.getText().toString().isEmpty() ||
                        claveE.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "¡Complete los campos!", Toast.LENGTH_LONG).show();
                } else {
                    //agregas un mensaje en el ProgressDialog
                    progressDialog.setMessage("Cargando...");
                    //muestras el ProgressDialog
                    progressDialog.show();
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            final String resultado = enviarDatosGET(cedulaE.getText().toString(), marcaE.getText().toString(), modeloE.getText().toString(),
                                    fallaExpresadaE.getText().toString(), diagnosticoE.getText().toString(), observacionE.getText().toString(), fechaEn.getText().toString(),
                                    Double.parseDouble(precioReparacionE.getText().toString()),
                                    cedulaClienteE.getText().toString(), claveE.getText().toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //cargarWebServices();
                                    int r = obtenerDatosJSON(resultado);
                                    //Condición para validar si los campos estan llenos.
                                    if (r > 0) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Se ha registrado el servicio exitosamente", Toast.LENGTH_SHORT).show();
                                        cedulaE.setText("");
                                        marcaE.setText("");
                                        modeloE.setText("");
                                        fallaExpresadaE.setText("");
                                        diagnosticoE.setText("");
                                        diagnosticoE.setText("");
                                        observacionE.setText("");
                                        cedulaClienteE.setText("");
                                        precioReparacionE.setText("");
                                        claveE.setText("");
                                    } else {
                                        progressDialog.hide();
                                        Toast.makeText(getApplicationContext(), "¡Algo malo ocurrio!", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(getApplicationContext(), "Mensaje " + resultado, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    };
                    thread.start();
                }
            }
        });

        /*
        //CODIGO PARA CARGAR IMAGEN
        imagen = (ImageView) vista.findViewById(R.id.ImagenServicio);
        cargarImages = (Button) vista.findViewById(R.id.btnCargarFoto);
        if(validarPermisos()){
            cargarImages.setEnabled(true);
        }else cargarImages.setEnabled(false);

        cargarImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarImagen();
            }
        });
        */
    }

    //INICIO CODIGO IMPORTANTE PARA LA CONEXION CON EL SERVIDOR
    //METODO PARA ENVIAR LOS DATOS AL SERVIDOR LOCAL
    public String enviarDatosGET(String empleado,String articulo, String modelo, String fallaExpresada, String diagnostico,
                                 String observación,String tiempoEntrega, double precioReparacion, String cedulaCliente,String clave){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.6/ServiciosWeb/registrarDiagnostico.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/registrarDiagnostico.php?";
        String falla = fallaExpresada.replace(" ", "%20");
        String diag = diagnostico.replace(" ", "%20");
        String obser = observación.replace(" ", "%20");
        //empleado = recuperado;
        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPO
            url = new URL(url_aws + "empleado=" + empleado + "&marca=" + articulo + "&modelo=" + modelo
                    +"&fallaExpresada="+ falla +"&diagnostico="+ diag +"&observacion="+ obser + "&precio=" + precioReparacion
                    + "&cedulaCli="+ cedulaCliente +"&fechaEntrega="+ tiempoEntrega + "&clave=" + clave);
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
    //FIN CODIGO CONEXION AL SERVIDOR

    private void tomarFotografia() {
        File file = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);
        boolean isCreada = file.exists();
        String nombreImagen = "";
        if (isCreada == false){
            isCreada = file.mkdirs();
        }
        if(isCreada){
            nombreImagen = (System.currentTimeMillis()/1000)+".jpg";
        }

        path = Environment.getExternalStorageDirectory()+File.separator+RUTA_IMAGEN+File.separator+nombreImagen;
        File imagen = new File(path);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
        {
            String authorities=getApplicationContext().getPackageName()+".provider";
            Uri imageUri= FileProvider.getUriForFile(getApplicationContext(),authorities,imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }else
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        }
        startActivityForResult(intent,COD_FOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case COD_SELECCIONA:
                    Uri miPath = data.getData();
                    imagen.setImageURI(miPath);
                    try{
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),miPath);
                        imagen.setImageBitmap(bitmap);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case COD_FOTO:
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("Ruta de almacenamiento", "Path: " + path);
                        }
                    });
                    bitmap = BitmapFactory.decodeFile(path);
                    imagen.setImageBitmap(bitmap);
                    break;
            }
        }
    }

    private String convertirImgString(Bitmap bitmap) {

        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imagenByte = array.toByteArray();
        String imagenString = Base64.encodeToString(imagenByte,Base64.DEFAULT);
        return imagenString;
    }

    /*
    //METODO QUE PERMTE SELECCIONAR SI DESEA TOMAR LA FOTO, CARGARLA O CANCELAR LA SELECCIONADA
    private void cargarImagen(){
        final CharSequence[] opciones= {"Tomar foto", "Cargar imagen", "Cancelar"};
        final AlertDialog.Builder alertBuilder= new AlertDialog.Builder(getContext());
        alertBuilder.setTitle("Seleccione una opción");
        alertBuilder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (opciones[i].equals("Tomar foto")){
                    tomarFotografia();
                }else {
                    if(opciones[i].equals("Cargar imagen")){
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(intent.createChooser(intent,"Seleccione la aplicación"),COD_SELECCIONA);
                    }else{
                        dialog.dismiss();
                    }
                }
            }
        });
        alertBuilder.show();
    }
    */

    //EJEMPLO AÑADIDO PARA VER SI FUNCIONA
    private void cargarWebServices(){
        progressDialog.setMessage("Cargando...");
        //muestras el ProgressDialog
        progressDialog.show();
        String url_local = "http://192.168.1.6/ServiciosWeb/registrarServicio.php";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/registrarServicio.php?";
        stringRequest = new StringRequest(Request.Method.POST, url_aws, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Codigo para que quite de pantalla el progressDialog si funciona bien
                if (marcaE.getText().toString().isEmpty() || modeloE.getText().toString().isEmpty() || fallaExpresadaE.getText().toString().isEmpty() ||
                        diagnosticoE.getText().toString().isEmpty() || observacionE.getText().toString().isEmpty()
                        || cedulaClienteE.getText().toString().isEmpty() || claveE.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "¡Complete los campos!", Toast.LENGTH_LONG).show();
                    progressDialog.hide();
                } else {
                    if (response.trim().equalsIgnoreCase("Registro")) {
                        marcaE.setText("");
                        modeloE.setText("");
                        fallaExpresadaE.setText("");
                        diagnosticoE.setText("");
                        diagnosticoE.setText("");
                        observacionE.setText("");
                        cedulaClienteE.setText("");
                        claveE.setText("");
                        Toast.makeText(getApplicationContext(), "Se registro exitosamente el servicio", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                    } else {
                        Toast.makeText(getApplicationContext(), "¡Ocurrio un problema!", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getContext(),"---> "+img[0].toString(),Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"ERROR" + error,Toast.LENGTH_SHORT).show();
                Log.i("","Error" + error);
                //Codigo para que quite de pantalla el progressDialog si funciona bien
                progressDialog.hide();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String cedulaEmpl = recuperado.toString();
                String marca = marcaE.getText().toString();
                String modelo = modeloE.getText().toString();
                String falla = fallaExpresadaE.getText().toString();
                String diagnostico = diagnosticoE.getText().toString();
                String observacion = observacionE.getText().toString();
                String clave = claveE.getText().toString();
                String fecha = fechaEn.getText().toString();
                String cedulaCli = cedulaClienteE.getText().toString();
                String estado = "1";

                //String imagen = convertirImgString(bitmap);
                Map<String, String> parametros = new Hashtable<>();
                parametros.put("empleado", cedulaEmpl);
                parametros.put("marca", marca);
                parametros.put("modelo", modelo);
                parametros.put("fallaExpresada", falla);
                parametros.put("diagnostico", diagnostico);
                parametros.put("observacion", observacion);
                parametros.put("cedulaCli", cedulaCli);
                parametros.put("fechaEntrega", fecha);
                parametros.put("clave", clave);
                parametros.put("estado", estado);
                parametros.put("cedulaCli", cedulaCli);
                //parametros.put("imagen", imagen);
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    //FIN DE CODIGO

    //METODO PAR VALIDAR LOS PERMISOS DE CAMARA Y ESCRITURA EN ANDROID 6 EN ADELANTE
    private boolean validarPermisos() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }

        if((getApplicationContext().checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED)
                && (getApplicationContext().checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(CAMERA)) || (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA},100);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] ==PackageManager.PERMISSION_GRANTED ) {
                cargarImages.setEnabled(true);
            }else{
                solicitarPermisosManual();
            }
        }
    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones= {"Si", "No"};
        final AlertDialog.Builder alertBuilder= new AlertDialog.Builder(getApplicationContext());
        alertBuilder.setTitle("¿Desea configurar los permisos de forma manual?");
        alertBuilder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (opciones[i].equals("Si")){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getApplication().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "Los permisos no fueron aceptados.",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        alertBuilder.show();
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getApplicationContext());
        alertBuilder.setTitle("¡Permisos desactivados!");
        alertBuilder.setMessage("Debe de aceptar los permisos para el correcto funcionamiento.");
        alertBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA},100);
            }
        });
        alertBuilder.show();
    }
    //FIN DE LOS METODOS
}
