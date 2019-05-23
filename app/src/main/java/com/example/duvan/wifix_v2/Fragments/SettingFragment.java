package com.example.duvan.wifix_v2.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duvan.wifix_v2.ActualizarDatosActivity;
import com.example.duvan.wifix_v2.CambiarPassActivity;
import com.example.duvan.wifix_v2.ListarDiagnosticoActivity;
import com.example.duvan.wifix_v2.R;
import com.example.duvan.wifix_v2.ReparacionActivity;

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

import static android.app.Activity.RESULT_OK;


public class SettingFragment extends Fragment {

    View vista;
    TextView nombre, apellido, cedula, sexo,telefono, direccion, correo, tienda;
    Spinner spTienda;
    String [] idTienda = {"1 - Palacio","2 - Alejandria"};
    Button btnCambiarPass, actualizarTienda;
    ImageView imagen;
    String cedula_U;

    private ProgressDialog progressDialog;
    String recuperado = "";

    private OnFragmentInteractionListener mListener;

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
        sexo = (TextView) vista.findViewById(R.id.txtSexoEmpleado);
        telefono = (TextView) vista.findViewById(R.id.txtTelefonoEmpleado);
        direccion = (TextView) vista.findViewById(R.id.txtDireccionEmpleado);
        correo = (TextView) vista.findViewById(R.id.txtCorreoEmpleado);
        tienda = (TextView) vista.findViewById(R.id.txtTiendaEmpleado);

        spTienda = (Spinner) vista.findViewById(R.id.spTiendaEmpleado);
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
                Toast.makeText(getContext(), "Esta opción aun no esta disponible", Toast.LENGTH_SHORT).show();
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
                                cargarDatosEmpleado(resultado);
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
                String id_tienda = (jsonArray.getJSONObject(i).getString("id_tienda"));
                if(id_tienda.equalsIgnoreCase("1")){
                    nomTienda = "Palacio";
                } else {
                    nomTienda = "Alejandria";
                }

                nombre.setText(jsonArray.getJSONObject(i).getString("nombre"));
                apellido.setText(jsonArray.getJSONObject(i).getString("apellido"));
                cedula.setText(jsonArray.getJSONObject(i).getString("cedula"));
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
        String url_local = "http://192.168.1.6/ServiciosWeb/cargarEmpleado.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/cargarEmpleado.php?";
        cedula = cedula_U;

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPO
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

    public String actualizarTienda(String buscar, int tienda){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.6/ServiciosWeb/cambiarBodega.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/cargarBodega.php?";

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
