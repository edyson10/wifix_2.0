package com.example.duvan.wifix_v2.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
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

import com.example.duvan.wifix_v2.ListarVentasActivity;
import com.example.duvan.wifix_v2.R;

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

public class VentasFragment extends Fragment {

    View view;
    EditText precio, cantidad, producto;
    Button vender, listarVentas;
    String cedula_U;

    private ListView lista;
    ArrayAdapter<String> adapter;

    String recuperado = "";
    private ProgressDialog progressDialog;
    private OnFragmentInteractionListener mListener;

    Spinner spEmpleado;
    String [] empleado = {"Alex - 27677488","Diana - 1090509647","Daniel - 1092392275","Diego - 1000019832","Edyson - 1090474784",
            "Fabian - 24356841","Gerardo - 1149457391","Lina - 1090465049","Victor - 1092338321","Yesenia - 1090456337" };

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
        vender = (Button) view.findViewById(R.id.btnVender);
        listarVentas = (Button) view.findViewById(R.id.btnListarVenta);

        final Bundle recupera = getActivity().getIntent().getExtras();
        if (recupera != null) {
            recuperado = recupera.getString("cedula");
            //Log.e("e","Valor. " + recuperado);
        }

        listarVentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListarVentasActivity.class);
                intent.putExtra("cedula", recuperado);
                startActivity(intent);
            }
        });

        spEmpleado = (Spinner) view.findViewById(R.id.spEmpleado);
        /*
        //CODIGO PARA MOSTRAR LOS DATOS EN EN SPINNER O COMBOBOX
        List<String> listaEmpleados;
        ArrayAdapter adapterSpinner;
        listaEmpleados = new ArrayList<>();
        //Cargo sexo en listaSexo
        Collections.addAll(listaEmpleados, empleado);
        //Paso los valores a mi adapter
        adapterSpinner = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listaEmpleados);
        //Linea de código secundario sirve para asignar un layout a los ítems
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Muestro los ítems en el spinner, obtenidos gracias al adapter
        spEmpleado.setAdapter(adapterSpinner);
        //FIN CODIGO
        */

        lista = (ListView) view.findViewById(R.id.listaModelos);
        cantidad = (EditText) view.findViewById(R.id.txtCantidadVenta);
        precio = (EditText) view.findViewById(R.id.txtPrecioVenta);
        producto = (EditText) view.findViewById(R.id.txtProductoVenta);

        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    final String resultado = obtenerDatosGET();
                    final String resultado1 = recibirDatosGET();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cargarLista(listaProductos(resultado));
                            cargarSpinner(listaEmpleados(resultado1));
                            adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listaProductos(resultado));
                            lista.setAdapter(adapter);
                            //Toast.makeText(getContext(),cedula_U, Toast.LENGTH_SHORT).show();
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
                        }
                    });
                    lista.setClickable(true);
                    lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Object o = lista.getItemAtPosition(position);
                            String str = (String) o;//As you are using Default String Adapter
                            producto.setText(str);
                        }
                    });
                }
            };
            thread.start();
        }else {
            Toast.makeText(getContext(), "Verifique su conexión a internet",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        //BOTON QUE PERMITE REALIZAR UNA VENTA
        vender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (producto.getText().toString().isEmpty() || cantidad.getText().toString().isEmpty() || precio.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "¡Complete los campos!", Toast.LENGTH_LONG).show();
                } else {
                    String[] prod = producto.getText().toString().split(" - ");
                    String art = "";
                    String mod = "";
                    for (int i = 0; i < prod.length; i++) {
                        art = prod[0].toString();
                        mod = prod[1].toString();
                    }

                    String cedulaEmpleado = "";
                    String[] emp = spEmpleado.getSelectedItem().toString().split(" - ");
                    for (int i=0; i < emp.length; i++){
                        cedulaEmpleado = emp[1].toString();
                    }

                    final String finalArt = art;
                    final String finalMod = mod;
                    final int precioTotal = Integer.parseInt(precio.getText().toString()) * Integer.parseInt(cantidad.getText().toString());
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
                                final String resultado = enviarDatosGET(finalCedulaEmpleado, finalArt, finalMod, precioTotal, Integer.parseInt(cantidad.getText().toString()));
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int r = obtenerDatosJSON(resultado);
                                        //Condición para validar si los campos estan llenos
                                        if (r > 0) {
                                            Toast.makeText(getContext(), "¡Algo malo ocurrio!", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getContext(), resultado, Toast.LENGTH_LONG).show();
                                            progressDialog.hide();
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(getContext(), "Se ha registrado la venta exitosamente", Toast.LENGTH_SHORT).show();
                                            producto.setText("");
                                            cantidad.setText("");
                                            precio.setText("");
                                            Toast.makeText(getContext(), finalCedulaEmpleado, Toast.LENGTH_SHORT).show();
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

    //METODO PARA ENVIAR LOS DATOS AL SERVIDOR LOCAL
    public String enviarDatosGET(String cedula, String marca, String modelo, int precio, int cantidad){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/registrarVenta.php";
        String url_local = "http://192.168.1.4/ServiciosWeb/registrarVenta.php";
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

    //----INICIO CODIGO---->
    //METODO PARA OBTENER LOS DATOS DE LOS PRODUCTOS
    public String obtenerDatosGET(){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.4/ServiciosWeb/cargarProductos.php?cedula=" + cedula_U;
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/cargarProductos.php?cedula=" + cedula_U;

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

    public String recibirDatosGET(){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.4/ServiciosWeb/cargarProductos.php?cedula=" + cedula_U;
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

    //ARREGLO SPINNER
    public ArrayList<String> listaProductos(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            for (int i = 0;i<jsonArray.length();i++){
                texto = jsonArray.getJSONObject(i).getString("articulo") + " - " + jsonArray.getJSONObject(i).getString("modelo");
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
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

    //METODO QUE PERMITE CARGAR EL LISTVIEW
    public void cargarLista(ArrayList<String> listaProd) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, listaProd);
        lista = (ListView) view.findViewById(R.id.listaModelos);
        lista.setAdapter(adapter);
    }

    public void cargarSpinner(ArrayList<String> empleado){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, empleado);
        spEmpleado = (Spinner) view.findViewById(R.id.spEmpleado);
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
