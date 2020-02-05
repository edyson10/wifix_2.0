package com.example.duvan.wifix_v2.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.duvan.wifix_v2.Clases.Conexion;
import com.example.duvan.wifix_v2.Clases.Usuario;
import com.example.duvan.wifix_v2.Clases.VolleySingleton;
import com.example.duvan.wifix_v2.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

public class SettingFragment extends Fragment {

    View vista;
    TextView nombre, apellido, cedula, sexo, telefono, direccion, correo, tienda, fechaNac;
    Spinner spTienda;
    String [] idTienda = {"1 - Palacio","2 - Alejandria", "3 - Septima"};
    Button btnCambiarPass, actualizarTienda;
    ImageView imagen;
    String cedula_U;

    private ProgressDialog progressDialog;
    private OnFragmentInteractionListener mListener;

    private final int MIS_PERMISOS = 100;
    private static final int COD_SELECCIONA = 10;
    private static final int COD_FOTO = 20;
    private static final String CARPETA_PRINCIPAL = "misImagenesApp/";//directorio principal
    private static final String CARPETA_IMAGEN = "imagenes";//carpeta donde se guardan las fotos
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN;//ruta carpeta de directorios
    private String path;//almacena la ruta de la imagen
    File fileImagen;
    Bitmap bitmap;

    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_setting, container, false);
        cargarPreferencias();

        progressDialog = new ProgressDialog(getContext());
        nombre = (TextView) vista.findViewById(R.id.txtNombreEmpleado);
        apellido = (TextView) vista.findViewById(R.id.txtApellidoEmpleado);
        cedula = (TextView) vista.findViewById(R.id.txtCedulaEmpleadoAct);
        fechaNac = (TextView) vista.findViewById(R.id.txtFechaNacimiento);
        sexo = (TextView) vista.findViewById(R.id.txtSexoEmpleado);
        telefono = (TextView) vista.findViewById(R.id.txtTelefonoEmpleado);
        direccion = (TextView) vista.findViewById(R.id.txtDireccionEmpleado);
        correo = (TextView) vista.findViewById(R.id.txtCorreoEmpleado);
        tienda = (TextView) vista.findViewById(R.id.txtTiendaEmpleado);
        spTienda = (Spinner) vista.findViewById(R.id.spTiendaEmpleado);
        imagen = (ImageView) vista.findViewById(R.id.imagenUser);

        //CODIGO PARA MOSTRAR LOS DATOS EN EN SPINNER O COMBOBOX
        List<String> listaEmpleado;
        ArrayAdapter adapterSpinner;
        listaEmpleado = new ArrayList<>();
        //Cargo sexo en listaSexo
        Collections.addAll(listaEmpleado, idTienda);
        //Paso los valores a mi adapter
        adapterSpinner = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listaEmpleado);
        //Linea de código secundario sirve para asignar un layout a los ítems
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Muestro los ítems en el spinner, obtenidos gracias al adapter
        spTienda.setAdapter(adapterSpinner);
        //FIN CODIGO


        /**Permisos**/
        if(solicitaPermisosVersionesSuperiores()){
            imagen.setClickable(true);
        }else{
            imagen.setClickable(false);
        }

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarOpciones();
            }
        });

        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    final String resultado = cargarDatosGET(cedula.getText().toString());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int r = validarDatosJSON(resultado);
                            if (r > 0) {
                                //agregas un mensaje en el ProgressDialog
                                progressDialog.setMessage("Cargando...");
                                //muestras el ProgressDialog
                                //progressDialog.show();
                                cargarDatosEmpleado(resultado);
                                Toast.makeText(getContext(), "Se han cargado el empleado exitosamente.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Error al cargar", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getContext(), resultado, Toast.LENGTH_SHORT).show();
                                progressDialog.hide();
                            }
                        }
                    });
                }
            };
            thread.start();
        }else {
            Toast.makeText(getContext(), "Verifique su conexión a internet",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        //imagen = (ImageView) vista.findViewById(R.id.imagenVer);
        //btnCargarImagen = (Button) vista.findViewById(R.id.btnCargarImagen);
        /*btnCargarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarImagen();
            }
        });
        */
        btnCambiarPass = (Button) vista.findViewById(R.id.btnActualizarContrasena);
        btnCambiarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Esta opción aún no esta disponible", Toast.LENGTH_SHORT).show();
            }
        });

        actualizarTienda = (Button) vista.findViewById(R.id.btnActualizarEmpleado);
        actualizarTienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombreTienda = "";
                String[] emp = spTienda.getSelectedItem().toString().split(" - ");
                for (int i=0; i < emp.length; i++){
                    nombreTienda = emp[0].toString();
                }
                registrarTienda(cedula_U, nombreTienda);
            }
        });
        return vista;
    }

    private void mostrarOpciones(){
        final CharSequence[] opciones={"Tomar Foto","Elegir de Galeria","Cancelar"};
        final android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(getContext());
        builder.setTitle("Elige una Opción");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Tomar Foto")){
                    abriCamara();
                }else{
                    if (opciones[i].equals("Elegir de Galeria")){
                        Intent intent=new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent,"Seleccione"),COD_SELECCIONA);
                    }else{
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        builder.show();
    }

    private void abriCamara(){
        File miFile = new File(Environment.getExternalStorageDirectory(),DIRECTORIO_IMAGEN);
        boolean isCreada = miFile.exists();

        if(!isCreada){
            isCreada = miFile.mkdirs();
        }

        if(isCreada){
            Long consecutivo = System.currentTimeMillis()/1000;
            String nombre = consecutivo.toString() + ".jpg";

            path = Environment.getExternalStorageDirectory() + File.separator+DIRECTORIO_IMAGEN
                    + File.separator + nombre;//indicamos la ruta de almacenamiento

            fileImagen = new File(path);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(fileImagen));

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                String authorities = getContext().getPackageName() + ".provider";
                Uri imageUri = FileProvider.getUriForFile(getContext(),authorities,fileImagen);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }else
            {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));
            }
            startActivityForResult(intent,COD_FOTO);
        }
    }

    private Bitmap redimensionarImagen(Bitmap bitmap, float anchoNuevo, float altoNuevo) {

        int ancho = bitmap.getWidth();
        int alto = bitmap.getHeight();

        if(ancho > anchoNuevo || alto > altoNuevo){
            float escalaAncho = anchoNuevo/ancho;
            float escalaAlto = altoNuevo/alto;
            Matrix matrix = new Matrix();
            matrix.postScale(escalaAncho,escalaAlto);
            return Bitmap.createBitmap(bitmap,0,0,ancho,alto,matrix,false);
        }else{
            return bitmap;
        }
    }

    //
    private void registrarTienda(final String cedulaEmp, final String id_tienda){
        Thread thread = new Thread(){
            @Override
            public void run() {
                final String resultado = actualizarTienda(cedulaEmp, Integer.parseInt(id_tienda));
                //GUARDA LOS DATOS DEL EMPLEADO
                final String resuEmp = cargarDatosGET(cedula.getText().toString());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (cedulaEmp.isEmpty()) {
                            Toast.makeText(getContext(), "¡Complete los campos!", Toast.LENGTH_LONG).show();
                        } else {
                            int r = validarDatosJSON(resultado);
                            if (r > 0) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Se ha cambiado de tienda exitosamente", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getContext(), resultado, Toast.LENGTH_SHORT).show();
                                //cargarDatosEmpleado(resultado);
                                /*nombre.setText("None");
                                apellido.setText("None");
                                cedula.setText("123456789");
                                sexo.setText("None");
                                telefono.setText("312-345-6789");
                                direccion.setText("None");
                                correo.setText("None");
                                tienda.setText("None");
                                */
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getContext(), resultado, Toast.LENGTH_SHORT).show();
                                Toast.makeText(getContext(), cedula_U, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        };
        thread.start();
    }

    //CARGAR LA LOS DATOS RECIBIDO EN EL LISTVIEW
    public void cargarDatosEmpleado(String response){
        try{
            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0;i<jsonArray.length();i++){
                String nomTienda = "";
                String id_tienda = (jsonArray.getJSONObject(i).getString("tienda"));
                if(id_tienda.equalsIgnoreCase("1")){
                    nomTienda = "Palacio";
                } else if (id_tienda.equalsIgnoreCase("2")) {
                    nomTienda = "Alejandria";
                } else {
                    nomTienda = "Septima";
                }

                nombre.setText(jsonArray.getJSONObject(i).getString("nombre"));
                apellido.setText(jsonArray.getJSONObject(i).getString("apellido"));
                cedula.setText(jsonArray.getJSONObject(i).getString("cedula"));
                fechaNac.setHint(jsonArray.getJSONObject(i).getString("fechanacimiento"));
                sexo.setText(jsonArray.getJSONObject(i).getString("sexo"));
                telefono.setText(jsonArray.getJSONObject(i).getString("telefono"));
                direccion.setText(jsonArray.getJSONObject(i).getString("direccion"));
                correo.setText(jsonArray.getJSONObject(i).getString("correo"));
                tienda.setText(nomTienda);
            }
        }catch (Exception e){}
    }

    public String cargarDatosGET(String cedula){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = Conexion.URL_LOCAL + "cargarEmpleado.php?";
        String url_aws = Conexion.URL + "cargarEmpleado.php?";
        cedula = cedula_U;

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPO
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

    public String actualizarTienda(String buscar, int tienda){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.6/ServiciosWeb/cambiarBodega.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/cambiarBodega.php?";

        try{
            url = new URL(url_aws + "cedula=" + buscar + "&tienda=" + tienda);
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

    private void cargarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        cedula_U = preferences.getString("cedula","");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**START SERVICES Y CODIGO**/

    /* ======================= START PERMISOS ======================= */
    private boolean solicitaPermisosVersionesSuperiores() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){//validamos si estamos en android menor a 6 para no buscar los permisos
            return true;
        }

        //validamos si los permisos ya fueron aceptados
        if((getContext().checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && getContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            return true;
        }

        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)||(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MIS_PERMISOS);
        }

        return false;//implementamos el que procesa el evento dependiendo de lo que se defina aqui
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MIS_PERMISOS){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){//el dos representa los 2 permisos
                Toast.makeText(getContext(),"Permisos aceptados",Toast.LENGTH_SHORT);
                imagen.setClickable(true);
            }
        }else{
            solicitarPermisosManual();
        }
    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones = {"Si","No"};
        final android.support.v7.app.AlertDialog.Builder alertOpciones=new android.support.v7.app.AlertDialog.Builder(getContext());//estamos en fragment
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Si")){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package",getContext().getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    private void cargarDialogoRecomendacion() {
        android.support.v7.app.AlertDialog.Builder dialogo=new AlertDialog.Builder(getContext());
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},100);
            }
        });
        dialogo.show();
    }
    /* ======================= END PERMISOS ======================= */

    /**BAJAR LA IMAGEN DEL SERVIDOR**/
    private void downloadImagen(){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        String url = Conexion.URL + "downloadImagen.php?nombre="+ nombre.getText().toString();

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.hide();
                Usuario miUsuario = new Usuario();
                JSONArray json = response.optJSONArray("usuario");
                JSONObject jsonObject = null;

                try {
                    jsonObject = json.getJSONObject(0);
                    miUsuario.setNombre(jsonObject.optString("nombre"));
                    miUsuario.setRutaImagen(jsonObject.optString("ruta_imagen"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                nombre.setText(miUsuario.getNombre());//SE MODIFICA
                String urlImagen = Conexion.URL_LOCAL + miUsuario.getRutaImagen();
                cargarWebServiceImagen(urlImagen);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "No se puede conectar con el seevidor." + error.toString(), Toast.LENGTH_LONG).show();
                System.out.println();
                progressDialog.hide();
                Log.d("ERROR: ", error.toString());
            }
        });

        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void cargarWebServiceImagen(String urlImagen) {
        urlImagen = urlImagen.replace(" ","%20");

        ImageRequest imageRequest = new ImageRequest(urlImagen, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                bitmap = response;//SE MODIFICA
                imagen.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Error al cargar la imagen",Toast.LENGTH_SHORT).show();
                Log.i("ERROR IMAGEN","Response -> " + error);
            }
        });

        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(imageRequest);
    }

    /**END SERVICES Y CODIGO**/



    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    */

    private void cargarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent,"Seleccione la aplicación"),10);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            Uri path = data.getData();
            imagen.setImageURI(path);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
