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
    private TextView producto;
    private EditText precioVenta, cantidad;
    private ProgressDialog progressDialog;
    private Spinner spEmpleado;
    String cedula_U;
    private ListView listaModelos;
    String tienda;

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
        producto = (TextView) view.findViewById(R.id.txtBuscarProductoVenQR);
        //id = (TextView) view.findViewById(R.id.txtIdProductoVenQR);
        //articulo = (TextView) view.findViewById(R.id.txtArticuloVenQR);
        //modelo = (TextView) view.findViewById(R.id.txtModeloVenQR);
        precioVenta = (EditText) view.findViewById(R.id.txtPrecioVenQR);
        cantidad = (EditText) view.findViewById(R.id.txtCantidadVenQR);
        spEmpleado = (Spinner) view.findViewById(R.id.spEmpleadoVenQR);
        buscarID = (ImageButton) view.findViewById(R.id.btnBuscaProductoVenQR);
        vender = (Button) view.findViewById(R.id.btnVenderVenQR);
        listar = (Button) view.findViewById(R.id.btnListarBajas);
        listaModelos = (ListView) view.findViewById(R.id.listaProductosVentas);

        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    final String resultado1 = recibirDatosEmpleadosGET();
                    final String resultado = cargarDatosGET();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cargarSpinner(listaEmpleados(resultado1));
                            cargarLista(listaProductos(resultado));
                            adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listaProductos(resultado));
                            listaModelos.setAdapter(adapter);
                            producto.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    //adapter.getFilter().filter(s);
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    adapter.getFilter().filter(s);
                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    //adapter.getFilter().filter(s);
                                }
                            });
                            progressDialog.hide();
                            listaModelos.setClickable(true);
                            listaModelos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Object o = listaModelos.getItemAtPosition(position);
                                    String str = (String) o;//As you are using Default String Adapter
                                    producto.setText(str);
                                }
                            });
                        }
                    });
                }
            };
            thread.start();
        }else {
            Toast.makeText(getContext(), "¡Verifique su conexión a internet!",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        /*
        * SECTOR DE CODIGO PARA LA UTILIZACION DE LA API
        * DE LECTURA DE CODIGO QR CON LA CAMARA
        * */
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

        /*
        * SECTOR DE CODIGO PARA LA ACCION DEL BOTON DE BUSCAR
        * EL PRODUCTO POR EL ID DEL PRODUCTO
        * */
        buscarID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //agregas un mensaje en el ProgressDialog
                progressDialog.setMessage("Cargando...");
                //muestras el ProgressDialog
                progressDialog.show();
                if (!producto.getText().toString().isEmpty()) {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            String[] prod = producto.getText().toString().split(" - ");
                            String id = "";
                            for (int i = 0; i < prod.length; i++) {
                                id = prod[0].toString();
                            }
                            final String resultado = cargarDatosProdIDGET(id);
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
                    Toast.makeText(getContext(),"¡Complete los campos!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*
        * ACCION DEL BOTON DE LISTAR QUE PERMITE QUE SE VAYA
        * A OTRA ACTIVITY PARA QUE LISTE LAS VENTAS REALIZADAS HATSA EL MOMENTO
        * */
        listar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListarVentasActivity.class);
                startActivity(intent);
            }
        });

        /*
        * ACCION DEL BOTON VENDER QUE PERMITE REALIZAR
        * LAS RESPECTIVAS VENTAS DE LOS PRODUCTOS
        * */
        vender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (producto.getText().toString().isEmpty() || precioVenta.getText().toString().isEmpty() || cantidad.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "¡Complete los campos!", Toast.LENGTH_LONG).show();
                } else {
                    String cedulaEmpleado = "";
                    String[] emp = spEmpleado.getSelectedItem().toString().split(" - ");
                    for (int i=0; i < emp.length; i++){
                        cedulaEmpleado = emp[1].toString();
                    }

                    String id_producto = "";
                    String[] prod = producto.getText().toString().split(" - ");
                    for (int i=0; i < prod.length; i++) {
                        id_producto = prod[0].toString();
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
                        final String finalId_producto = id_producto;
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                final String resultado = enviarDatosVentaGET(finalId_producto, precioTotal, Integer.parseInt(cantidad.getText().toString()),
                                        finalCedulaEmpleado);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int r = obtenerDatosJSON(resultado);
                                        if (r > 0) {
                                            progressDialog.dismiss();
                                            producto.setText("");
                                            precioVenta.setText("");
                                            cantidad.setText("");
                                            Toast.makeText(getContext(), "Se ha registrado la venta exitosamente", Toast.LENGTH_SHORT).show();
                                            //Toast.makeText(getContext(), "->" + resultado, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), "¡Algo malo ocurrio!", Toast.LENGTH_SHORT).show();
                                            progressDialog.hide();
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

    /*
    * METODO DE MOSTRAR LOS DATOS LEYENDO POR MEDIO DEL LECTOR DE CCOIGO QR
    * */
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
                                final String resultado = recibirProductoQRGET(id,tienda);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        listaProductosQR(resultado);
                                        //Toast.makeText(getContext(), "Producto: " + resultado, Toast.LENGTH_SHORT).show();
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

    /*
    * METODO PARA ENVIAR LOS DATOS DE LA VENTA AL SERVIDOR POR WEB SERVICES
    * */
    public String enviarDatosVentaGET(String producto, int precio, int cantidad, String cedula){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/registrarVentaBD.php";
        String url_local = "http://192.168.56.1/ServiciosWeb/registrarVentaBD.php";

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "?producto=" + producto + "&cantidad=" + cantidad + "&precio=" + precio + "&empleado=" + cedula);
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

    /*
    * METODO PARAA CARGAR LOS DATOS DEL PRODUCTO RECIBIENDO
    * RECIBIENDO COMO PARAMETRO EL ID DEL PRODUCTO
    * */
    public String cargarDatosProdIDGET(String producto) {
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/buscarProducto.php?";
        String url_local = "http://192.168.56.1/ServiciosWeb/buscarProducto.php?";

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

    /*
     * METODO PARA RECIBIR LOS DATOS DEL PRODUCTO
     * POR MEDIO DEL CODIGO QR
     * */
    public String recibirProductoQRGET(String producto, String tienda){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.56.1/ServiciosWeb/empleadosBD.php";
        //DDIRECCION DEL NUEVO SERVICIO DE LA NUEVA BD
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/buscarProductoQR.php?";

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "producto=" + producto + "&tienda=" + tienda);
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

    /*
    * METODO PARA OBTENER LOS DATOS DE LOS PRODUCTOS
    * */
    public String cargarDatosGET(){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.56.1/ServiciosWeb/cargarProductosBD.php?cedula=" + cedula_U;
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/cargarProductosBD.php?cedula=" + cedula_U;

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

    /*
    * METODO PARA RECIBIR LOS EMPLEADOS REGISTRADOS
    * */
    public String recibirDatosEmpleadosGET(){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.56.1/ServiciosWeb/empleadosBD.php";
        //DDIRECCION DEL NUEVO SERVICIO DE LA NUEVA BD
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/empleadosBD.php";

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

    /*
    * METODO PARA MOSTRAR LOS DATOS RECIBIDOS EN EL JSON EN LOS DIFERENTES CAMPOS
    * */
    public void cargarDatos(String response) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                //Toast.makeText(getContext(), "Hola ", Toast.LENGTH_SHORT).show();
                precioVenta.setText(jsonArray.getJSONObject(i).getString("precioVenta"));
            }
        } catch (Exception ex) {
            Toast.makeText(getContext(), "Error: " + ex, Toast.LENGTH_SHORT).show();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    precioVenta.setText(jsonArray.getJSONObject(i).getString("precioVenta"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* SECTOR DE CODIGO QUE PEERMITE CARRGAR LOS PRODUCTOS EN UN ARREGLO
    * PARA LUEGO CARGARLOS EN UN LISTVIEW
    * */
    public ArrayList<String> listaProductos(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            for (int i = 0; i < jsonArray.length(); i++){
                texto = jsonArray.getJSONObject(i).getString("id_prodtienda") + " - "
                        + jsonArray.getJSONObject(i).getString("nombre") + " - "
                        + jsonArray.getJSONObject(i).getString("modelo");
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
    }

    /* SECTOR DE CODIGO QUE PEERMITE CARRGAR LOS PRODUCTOS EN UN ARREGLO
     * PARA LUEGO CARGARLOS EN UN LISTVIEW
     * */
    public ArrayList<String> listaProductosQR(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            for (int i = 0; i < jsonArray.length(); i++){
                precioVenta.setText(jsonArray.getJSONObject(i).getString("precioVenta"));
                producto.setText(jsonArray.getJSONObject(i).getString("id_prodtienda") + " - "
                        + jsonArray.getJSONObject(i).getString("nombre") + " - "
                        + jsonArray.getJSONObject(i).getString("modelo"));
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
    }

    /* METODO QUE PERMITE OBTENER EL JSON
    * Y RECORRERLO Y SABER SI RECIBIO O NO DATOS
    * */
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

    /* SECTOR DE CODIGO QUE PEERMITE CARRGAR LOS EMPLEADOS
    *  EN UN ARREGLO PARA LUEGO CARGARLOS EN UN LISTVIEW
    * */
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

    /* METODO PARA CARGAR EL ARRAYLIST
    *  DE EMPLEADOS EN EL SPINNER
    * */
    public void cargarSpinner(ArrayList<String> empleado){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, empleado);
        spEmpleado = (Spinner) view.findViewById(R.id.spEmpleadoVenQR);
        spEmpleado.setAdapter(adapter);
    }

    /* METODO QUE PERMITE CARGAR EL ARRAYLIST DE
    *  PRODUCTOS EN EL LISTVIEW
    * */
    public void cargarLista(ArrayList<String> listaProd) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, listaProd);
        listaModelos = (ListView) view.findViewById(R.id.listaProductosVentas);
        listaModelos.setAdapter(adapter);
    }

    private void cargarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        cedula_U = preferences.getString("cedula","");
        tienda = preferences.getString("tienda","");
    }

    //****************** ================= FIN CODIGO ================= ******************//

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
