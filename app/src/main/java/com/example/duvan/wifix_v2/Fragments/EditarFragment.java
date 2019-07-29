package com.example.duvan.wifix_v2.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.duvan.wifix_v2.ActualizarVentaActivity;
import com.example.duvan.wifix_v2.R;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditarFragment extends Fragment {

    View view;
    EditText txtBuscar, txtPrecioCosto, txtCantidadProd, txtPrecioVenta;
    TextView txtModeloAct, txtArticuloAct;
    Button actualizar, cargarFoto;
    ImageButton buscar;
    ImageView foto;
    RequestQueue request;

    private ProgressDialog progressDialog;

    private OnFragmentInteractionListener mListener;

    public EditarFragment() {
        // Required empty public constructor
    }

    public static EditarFragment newInstance(String param1, String param2) {
        EditarFragment fragment = new EditarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        view = inflater.inflate(R.layout.fragment_editar, container, false);
        progressDialog = new ProgressDialog(getContext());
        txtBuscar = (EditText) view.findViewById(R.id.txtBuscarProducto);
        txtArticuloAct = (TextView) view.findViewById(R.id.txtProductoAct);
        txtModeloAct = (TextView) view.findViewById(R.id.txtModeloAct);
        txtPrecioCosto = (EditText) view.findViewById(R.id.txtPrecioCostoProd);
        txtPrecioVenta = (EditText) view.findViewById(R.id.txtPrecioVentaProd);
        txtCantidadProd = (EditText) view.findViewById(R.id.txtCantidadProd);
        buscar = (ImageButton) view.findViewById(R.id.btnBuscaProducto);
        actualizar = (Button) view.findViewById(R.id.btnActualizarProducto);
        cargarFoto = (Button) view.findViewById(R.id.btnActulizarFoto);
        foto = (ImageView) view.findViewById(R.id.fotoActualizar);
        request = Volley.newRequestQueue(getContext());

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarProducto();
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarProducto();
            }
        });

        cargarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    // ==== INICIO DE CODIGO BUSCAR ====
    public void buscarProducto() {
        //agregas un mensaje en el ProgressDialog
        progressDialog.setMessage("Cargando...");
        //muestras el ProgressDialog
        progressDialog.show();
        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    final String resultado = enviarDatosGET(txtBuscar.getText().toString());
                    final int res = validarDatosJSON(resultado);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (res == 0) {
                                progressDialog.dismiss();
                                txtArticuloAct.setText("Articulo");
                                txtModeloAct.setText("Modelo");
                                txtPrecioCosto.setText("");
                                txtPrecioVenta.setText("");
                                txtCantidadProd.setText("");
                                Toast.makeText(getContext(), "No existe un producto con ese ID", Toast.LENGTH_SHORT).show();
                            } else {
                                cargarDatos(resultado);
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Se cargo correctamente.", Toast.LENGTH_SHORT).show();
                            }
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

    public String enviarDatosGET(String producto) {
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

    //METODO PARA CARGAR LOS DATOS EN LOS TEXTVIEW
    public void cargarDatos(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                txtArticuloAct.setText(jsonArray.getJSONObject(i).getString("articulo"));
                txtModeloAct.setText(jsonArray.getJSONObject(i).getString("modelo"));
                txtPrecioCosto.setText(jsonArray.getJSONObject(i).getString("precioUnitario"));
                txtPrecioVenta.setText(jsonArray.getJSONObject(i).getString("precioVenta"));
                txtCantidadProd.setText(jsonArray.getJSONObject(i).getString("cantidad"));
                //Toast.makeText(getContext(), "https://18.228.235.94/wifix/ServiciosWeb/"+jsonArray.getJSONObject(i).getString("foto"), Toast.LENGTH_SHORT).show();
                //String img = "https://18.228.235.94/wifix/ServiciosWeb/"+jsonArray.getJSONObject(i).getString("foto");
                //cargarWebServicesImagen(img);
            }
        } catch (Exception ex) {
            //Toast.makeText(getContext(), "Error: " + ex, Toast.LENGTH_SHORT).show();
        }
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

    private void cargarWebServicesImagen(String urlImagen){
        urlImagen = urlImagen.replace(" ", "%20");
        ImageRequest imageRequest = new ImageRequest(urlImagen, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                foto.setImageBitmap(response);
            }
        }, 0,0, null, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error a cargar la foto", Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), "Error " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        request.add(imageRequest);
    }
    // ==== FIN DE CODIGO BUSCAR=====

    // ==== INICIO CODIGO ACTUALIZAR ====
    public void actualizarProducto(){
        //agregas un mensaje en el ProgressDialog
        progressDialog.setMessage("Cargando...");
        //muestras el ProgressDialog
        progressDialog.show();
        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    final String resultado = actualizarDatosGET(txtBuscar.getText().toString(), Integer.parseInt(txtPrecioCosto.getText().toString()),
                            Integer.parseInt(txtPrecioVenta.getText().toString()), Integer.parseInt(txtCantidadProd.getText().toString()));
                    final int res = obtenerDatosJSON(resultado);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (res > 0) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Se actualizo correctamente el producto.", Toast.LENGTH_SHORT).show();
                                //Toast.makeText(getContext(), resultado ,Toast.LENGTH_SHORT).show();
                                //txtBuscar.setText("");
                                txtArticuloAct.setText("Articulo");
                                txtModeloAct.setText("Modelo");
                                txtPrecioCosto.setText("");
                                txtPrecioVenta.setText("");
                                txtCantidadProd.setText("");
                            } else {
                                progressDialog.hide();
                                Toast.makeText(getContext(), "¡Error al actualizar!", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getContext(), resultado ,Toast.LENGTH_SHORT).show();
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
    }

    public String actualizarDatosGET(String venta, int precioCosto, int precioVenta, int cantidad) {
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/editarProducto.php";
        String url_local = "http://192.168.1.6/ServiciosWeb/actualizarVenta.php";

        try {
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "?id=" + venta + "&precioCosto=" + precioCosto + "&precioVenta=" + precioVenta + "&cantidad=" + cantidad);
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
    // ==== FIN CODIGO ACTUALIZAR ====

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