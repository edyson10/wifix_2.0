package com.example.duvan.wifix_v2.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.Toast;

import com.example.duvan.wifix_v2.LoginActivity;
import com.example.duvan.wifix_v2.MainEmpleadoActivity;
import com.example.duvan.wifix_v2.R;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class BodegaFragment extends Fragment {

    View view;
    EditText producto;
    Button buscar;
    ListView listProducto;
    ArrayAdapter<String> adapter;
    String cedula_U;

    private ProgressDialog progressDialog;

    private OnFragmentInteractionListener mListener;

    public BodegaFragment() {
        // Required empty public constructor
    }

    public static BodegaFragment newInstance(String param1, String param2) {
        BodegaFragment fragment = new BodegaFragment();
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
        view = inflater.inflate(R.layout.fragment_bodega, container, false);
        progressDialog = new ProgressDialog(getContext());
        cargarPreferencias();
        producto = (EditText) view.findViewById(R.id.txtBuscarBodega);
        buscar = (Button) view.findViewById(R.id.btnBuscarBodega);
        listProducto = (ListView) view.findViewById(R.id.listProductosBodega);

        ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            Thread thread = new Thread(){
                @Override
                public void run() {
                    final String resultado = obtenerDatosGET();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cargarLista(listaProductos(resultado));
                            adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listaProductos(resultado));
                            listProducto.setAdapter(adapter);
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
                    listProducto.setClickable(true);
                    listProducto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Object o = listProducto.getItemAtPosition(position);
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

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (producto.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "¡Complete los campos!", Toast.LENGTH_LONG).show();
                }else {
                    String[] prod = producto.getText().toString().split(" - ");
                    String art = "";
                    String mod = "";
                    for (int i = 0; i < prod.length; i++) {
                        art = prod[0].toString();
                        mod = prod[1].toString();
                    }

                    final String finalArt = art;
                    final String finalMod = mod;
                    //agregas un mensaje en el ProgressDialog
                    progressDialog.setMessage("Cargando...");
                    //muestras el ProgressDialog
                    progressDialog.show();
                    buscarProducto(art, mod);
                }
            }
        });
        return view;
    }

    public void buscarProducto(final String marca, final String modelo){
        Thread thread = new Thread(){
            @Override
            public void run() {
                final String resultado = buscarDatosGET(marca,modelo);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alertOneButton(cargarProductos(resultado));
                        progressDialog.hide();
                    }
                });
            }
        };
        thread.start();
    }

    //----INICIO CODIGO---->
    //METODO PARA OBTENER LOS DATOS DE LOS PRODUCTOS
    public String obtenerDatosGET(){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.3/ServiciosWeb/cargarProductos.php?cedula=" + cedula_U;
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

    //METODO PARA ENVIAR LOS DATOS AL SERVIDOR LOCAL
    public String buscarDatosGET(String marca, String modelo){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/buscarProducto.php";
        String url_local = "http://192.168.1.3/ServiciosWeb/buscarProducto.php";
        String mod = modelo.replace(" ", "%20");
        String mar = marca.replace(" ", "%20");

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "?marca=" + mar + "&modelo=" + mod);
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

    //METODO QUE PERMITE CARGAR EL LISTVIEW
    public void cargarLista(ArrayList<String> listaProd) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, listaProd);
        listProducto = (ListView) view.findViewById(R.id.listProductosBodega);
        listProducto.setAdapter(adapter);
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

    //ARREGLO SPINNER
    public ArrayList<String> cargarProductos(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            for (int i = 0;i<jsonArray.length();i++){
                texto = " \n Articulo: " + jsonArray.getJSONObject(i).getString("articulo") + " \n "
                        + "Modelo: " +  jsonArray.getJSONObject(i).getString("modelo") + " \n "
                        + "Cantidad: " + jsonArray.getJSONObject(i).getString("cantidad") + " \n "
                        + "Bodega: " + jsonArray.getJSONObject(i).getString("bodega");
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
    }

    public void alertOneButton(ArrayList<String> listaProd) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.icono);
        builder.setTitle("Productos");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, listaProd);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // will toast your selection
                dialog.dismiss();
            }
        }).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
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
