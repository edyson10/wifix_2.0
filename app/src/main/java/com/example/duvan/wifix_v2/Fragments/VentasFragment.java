package com.example.duvan.wifix_v2.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duvan.wifix_v2.CaptureActivityPortrait;
import com.example.duvan.wifix_v2.CodigoQRActivity;
import com.example.duvan.wifix_v2.ListarSalidasActivity;
import com.example.duvan.wifix_v2.ListarVentasActivity;
import com.example.duvan.wifix_v2.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VentasFragment extends Fragment {

    View view;
    private ImageButton codigoqr, buscarID;
    private Button vender, listar;
    private IntentIntegrator qrscan;
    private TextView id_producto, articulo,modelo, id;
    private EditText precioVenta, cantidad;
    private ProgressDialog progressDialog;
    private Spinner spEmpleado;
    String cedula_U;

    ArrayAdapter<String> adapter;

    String recuperado = "";
    private OnFragmentInteractionListener mListener;

    public VentasFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static VentasFragment newInstance(String param1, String param2) {
        VentasFragment fragment = new VentasFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_ventas, container, false);
        progressDialog = new ProgressDialog(getContext());
        cargarPreferencias();

        codigoqr = (ImageButton) view.findViewById(R.id.btnVenQR);
        id_producto = (TextView) view.findViewById(R.id.txtBuscarProductoVenQR);
        id = (TextView) view.findViewById(R.id.txtIdProductoVenQR);
        articulo = (TextView) view.findViewById(R.id.txtArticuloVenQR);
        modelo = (TextView) view.findViewById(R.id.txtModeloVenQR);
        precioVenta = (EditText) view.findViewById(R.id.txtPrecioVenQR);
        cantidad = (EditText) view.findViewById(R.id.txtCantidadVenQR);
        spEmpleado = (Spinner) view.findViewById(R.id.spEmpleadoVenQR);
        buscarID = (ImageButton) view.findViewById(R.id.btnBuscaProductoVenQR);
        vender = (Button) view.findViewById(R.id.btnVenderVenQR);
        listar = (Button) view.findViewById(R.id.btnListarBajas);

        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    final String resultado1 = recibirDatosGET();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cargarSpinner(listaEmpleados(resultado1));
                        }
                    });
                }
            };
            thread.start();
        }else {
            Toast.makeText(getContext(), "Verifique su conexión a internet",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        qrscan = new IntentIntegrator(this.getActivity()).forSupportFragment(this);
        qrscan.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        qrscan.setOrientationLocked(false);
        qrscan.setCameraId(0);
        qrscan.setCaptureActivity(CaptureActivityPortrait.class);
        qrscan.setPrompt("ESCANEANDO CODIGO...");
        codigoqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrscan.initiateScan();
            }
        });

        buscarID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //agregas un mensaje en el ProgressDialog
                progressDialog.setMessage("Cargando...");
                //muestras el ProgressDialog
                progressDialog.show();
                if (!id_producto.getText().toString().isEmpty()) {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            final String resultado = cargarDatosGET(id_producto.getText().toString());
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    cargarDatos(resultado);
                                }
                            });
                        }
                    };
                    thread.start();
                } else {
                    progressDialog.hide();
                    Toast.makeText(getContext(),"Complete los campos",Toast.LENGTH_SHORT).show();
                }
            }
        });

        listar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListarVentasActivity.class);
                startActivity(intent);
            }
        });


        //BOTON QUE PERMITE REALIZAR UNA VENTA
        vender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (articulo.getText().toString().isEmpty() || modelo.getText().toString().isEmpty() ||
                        precioVenta.getText().toString().isEmpty() || cantidad.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "¡Complete los campos!", Toast.LENGTH_LONG).show();
                } else {
                    String cedulaEmpleado = "";
                    String[] emp = spEmpleado.getSelectedItem().toString().split(" - ");
                    for (int i=0; i < emp.length; i++){
                        cedulaEmpleado = emp[1].toString();
                    }

                    final int precioTotal = Integer.parseInt(precioVenta.getText().toString()) * Integer.parseInt(cantidad.getText().toString());
                    final char[] cant = cantidad.getText().toString().toCharArray();
                    //agregas un mensaje en el ProgressDialog
                    progressDialog.setMessage("Cargando...");
                    //muestras el ProgressDialog
                    progressDialog.show();
                    //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
                    ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = con.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        final String finalCedulaEmpleado = cedulaEmpleado;
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                final String resultado = enviarDatosGET(finalCedulaEmpleado, articulo.getText().toString(), modelo.getText().toString(),
                                        precioTotal,Integer.parseInt(cantidad.getText().toString()));
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int r = obtenerDatosJSON(resultado);
                                        //Condición para validar si los campos estan llenos
                                        if (r > 0) {
                                            Toast.makeText(getContext(), "¡Algo malo ocurrio!", Toast.LENGTH_SHORT).show();
                                            //Toast.makeText(getContext(), resultado, Toast.LENGTH_LONG).show();
                                            progressDialog.hide();
                                        } else {
                                            progressDialog.dismiss();
                                            id.setText("None");
                                            articulo.setText("None");
                                            modelo.setText("None");
                                            precioVenta.setText("");
                                            cantidad.setText("");
                                            Toast.makeText(getContext(), "Se ha registrado la venta exitosamente", Toast.LENGTH_SHORT).show();
                                            //Toast.makeText(getContext(), finalCedulaEmpleado, Toast.LENGTH_SHORT).show();
                                        }
                                        progressDialog.hide();
                                    }
                                });
                            }
                        };
                        thread.start();
                    } else {
                        Toast.makeText(getContext(), "Verifique su conexión a internet", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }
        });

        return view;
    }

    // ----- METODO DE MOSTRAR LOS DATOS -----
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getContext(), "Resultado no encontrado", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject obj = new JSONObject(intentResult.getContents());
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    //agregas un mensaje en el ProgressDialog
                    progressDialog.setMessage("Cargando...");
                    //muestras el ProgressDialog
                    progressDialog.show();
                    //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
                    ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = con.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        //final String finalCedulaEmpleado = ;
                        final String id = intentResult.getContents();
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                final String resultado = cargarDatosGET(id);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        cargarDatos(resultado);
                                        //Toast.makeText(getContext(),id, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        };
                        thread.start();
                    } else {
                        Toast.makeText(getContext(), "Verifique su conexión a internet", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    // ----- FIN DEL METODO DE MOSTRAR LOS DATOS -----

    //METODO PARA ENVIAR LOS DATOS AL SERVIDOR LOCAL
    public String enviarDatosGET(String cedula, String marca, String modelo, int precio, int cantidad){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/registrarVenta.php";
        String url_local = "http://192.168.1.6/ServiciosWeb/registrarVenta.php";
        String mod = modelo.replace(" ", "%20");
        String mar = marca.replace(" ", "%20");

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "?empleado=" + cedula + "&marca=" + mar + "&modelo=" + mod + "&precio=" + precio + "&cantidad=" + cantidad);
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

    public String cargarDatosGET(String producto) {
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/buscarProductoID.php?";
        String url_local = "http://192.168.1.6/ServiciosWeb/buscarVenta.php?";

        try {
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "producto=" + producto);
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

    // ----- METODO PARA RECIBIR LOS EMPLEADOS REGISTRADOS
    public String recibirDatosGET(){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.6/ServiciosWeb/empleados.php";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/empleados.php";

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

    public void cargarDatos(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                id.setText(jsonArray.getJSONObject(i).getString("id_producto"));
                articulo.setText(jsonArray.getJSONObject(i).getString("articulo"));
                modelo.setText(jsonArray.getJSONObject(i).getString("modelo"));
                precioVenta.setText(jsonArray.getJSONObject(i).getString("precioVenta"));
            }
        } catch (Exception ex) {
            Toast.makeText(getContext(), "Error: " + ex, Toast.LENGTH_SHORT).show();
        }
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

    public ArrayList<String> listaEmpleados(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            for (int i = 0;i<jsonArray.length();i++){
                texto = jsonArray.getJSONObject(i).getString("nombre") + " - " + jsonArray.getJSONObject(i).getString("cedula");
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
    }

    // ===== METODO PARA CARGAR LOS EMPLEADOS EN EL SPINNER =====
    public void cargarSpinner(ArrayList<String> empleado){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, empleado);
        spEmpleado = (Spinner) view.findViewById(R.id.spEmpleadoVenQR);
        spEmpleado.setAdapter(adapter);
    }

    private void cargarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        cedula_U = preferences.getString("cedula","");
    }

    //----FIN CODIGO--->

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
