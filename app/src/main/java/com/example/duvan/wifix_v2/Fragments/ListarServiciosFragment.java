package com.example.duvan.wifix_v2.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duvan.wifix_v2.BuscarServActivity;
import com.example.duvan.wifix_v2.ListarServicioActivity;
import com.example.duvan.wifix_v2.R;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ListarServiciosFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    View view;
    ListView listServicios;
    private ProgressDialog progressDialog;
    ArrayAdapter<String> adapter;
    ImageView imagen;
    Button buscarServ;

    public ListarServiciosFragment() {
        // Required empty public constructor
    }

    public static ListarServiciosFragment newInstance(String param1, String param2) {
        ListarServiciosFragment fragment = new ListarServiciosFragment();
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
        view = inflater.inflate(R.layout.fragment_listar_servicios, container, false);
        progressDialog = new ProgressDialog(getContext());
        listServicios = (ListView) view.findViewById(R.id.listaServicioDia);
        imagen = (ImageView) view.findViewById(R.id.imagenServicio);
        buscarServ = (Button) view.findViewById(R.id.btnBuscarServicioTec);

        //agregas un mensaje en el ProgressDialog
        progressDialog.setMessage("Cargando servicios...");
        //muestras el ProgressDialog
        progressDialog.show();
        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    final String resultado = listarServiciosGET();
                    final int validar = validarDatosJSON(resultado);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (validar == 0) {
                                progressDialog.dismiss();
                                imagen.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(), "No hay ningún servicio registrado hoy.", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                cargarLista(listarServicioDia((resultado)));
                                Toast.makeText(getContext(), "Se han cargado los servicios exitosamente.", Toast.LENGTH_SHORT).show();
                                adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listarServicioDia(resultado)) {
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
                                listServicios.setAdapter(adapter);
                                imagen.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            };
            thread.start();
        } else {
            Toast.makeText(getContext(), "Verifique su conexión a internet",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        buscarServ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListarServicioActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    //===== INICIO CODIGO SERVIDOR Y JSON
    //METODO PARA RECIBIR LOS DATOS DEL SERRVIDOR EN JSON
    public String listarServiciosGET(){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://172.21.0.133/ServiciosWeb/listarServicioDia.php";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/listarServicioDia.php";

        try{
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

    //CARGAR LA LOS DATOS RECIBIDO EN EL LISTVIEW
    public ArrayList<String> listarServicioDia(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            String estado = "";
            for (int i = 0;i<jsonArray.length();i++){
                if (jsonArray.getJSONObject(i).getString("estado").equalsIgnoreCase("1")) {
                    estado = "En reparación";
                } else estado = "Reparado";

                texto = "Servicio: " + (i + 1)+ "\n"
                        + "Orden del servicio: " + jsonArray.getJSONObject(i).getString("id_servicio") + "\n"
                        + "Orden de reparación: " + jsonArray.getJSONObject(i).getString("id_reparacion") + "\n"
                        + "Estado: " + estado + "\n"
                        + "Fecha reparacion: " + jsonArray.getJSONObject(i).getString("fecha_reparacion") + "\n"
                        + "Cedula cliente: " + jsonArray.getJSONObject(i).getString("cedulaCli") + "\n"
                        + "Marca: " + jsonArray.getJSONObject(i).getString("marca") + "\n"
                        + "Modelo: " + jsonArray.getJSONObject(i).getString("modelo") + "\n"
                        + "Falla : " + jsonArray.getJSONObject(i).getString("falla") + "\n"
                        + "Bodega: " + jsonArray.getJSONObject(i).getString("bodega") + "\n"
                        + "Repuesto: " + jsonArray.getJSONObject(i).getString("repuesto") + "\n"
                        + "Detalle: " + jsonArray.getJSONObject(i).getString("detalle_reparacion") + "\n"
                        + "Costo reparación: $ " + jsonArray.getJSONObject(i).getString("costo_reparacion") + "\n"
                        + "Precio reparación: $ " + jsonArray.getJSONObject(i).getString("precio_reparacion");
                 /*
                if (imagen != null) {
                    foto.setImageBitmap(imagen);
                }else foto.setImageResource(R.drawable.no_disponible);
                */
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
    }

    //METODO QUE PERMITE CARGAR EL LISTVIEW
    public void cargarLista(ArrayList<String> lista) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, lista);
        listServicios = (ListView) view.findViewById(R.id.listaServicioDia);
        listServicios.setAdapter(adapter);
    }
    //===== FIN CODIGO ===


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
