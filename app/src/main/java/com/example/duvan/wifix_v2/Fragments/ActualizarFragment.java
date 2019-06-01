package com.example.duvan.wifix_v2.Fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duvan.wifix_v2.ActualizarVentaActivity;
import com.example.duvan.wifix_v2.R;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class ActualizarFragment extends Fragment {

    View view;
    Button actualizarVen,actualizarSer, buscarServicio;
    String cedulaCl = "";
    String servicio ="";
    private ProgressDialog progressDialog;
    EditText servicioBuscar, observacion, precio, costo, clave, falla, diagnostico;
    TextView cedulaEmp, marca, modelo, cedulaCli, fechaServ, fechaEntr, estadoAct, idServicio;
    Calendar mCurrentDate;
    int dia, mes, anio;

    private OnFragmentInteractionListener mListener;

    public ActualizarFragment() {
        // Required empty public constructor
    }

    public static ActualizarFragment newInstance(String param1, String param2) {
        ActualizarFragment fragment = new ActualizarFragment();
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
        view = inflater.inflate(R.layout.fragment_actualizar, container, false);
        progressDialog = new ProgressDialog(getContext());

        servicioBuscar = (EditText) view.findViewById(R.id.txtServicioActualizar);
        idServicio = (TextView) view.findViewById(R.id.txtIdServicioAct);
        fechaServ = (TextView) view.findViewById(R.id.txtFechaServicioAct);
        cedulaEmp = (TextView) view.findViewById(R.id.txtCedulaEmpleadoAct);
        marca = (TextView) view.findViewById(R.id.txtMarcaAct);
        modelo = (TextView) view.findViewById(R.id.txtModeloAct);
        falla = (EditText) view.findViewById(R.id.txtFallaServicioAct);
        diagnostico = (EditText) view.findViewById(R.id.txtDiagnosticoAct);
        observacion = (EditText) view.findViewById(R.id.txtObservacionServicioAct);
        precio = (EditText) view.findViewById(R.id.txtPrecioServicioAct);
        clave = (EditText) view.findViewById(R.id.txtClaveServicio);
        cedulaCli = (TextView) view.findViewById(R.id.txtCedulaClienteAct);
        estadoAct = (TextView) view.findViewById(R.id.txtEstadoAct);

        //CODIGO FECHA DE ENTREGA ESTIPULADA
        fechaEntr = (TextView) view.findViewById(R.id.txtFechaEntregaAct);
        mCurrentDate = Calendar.getInstance();
        dia = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        mes = mCurrentDate.get(Calendar.MONTH);
        anio = mCurrentDate.get(Calendar.YEAR);
        mes = mes +1;
        fechaEntr.setText(anio+"-"+mes+"-"+dia);
        fechaEntr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker , int year , int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear +1;
                        fechaEntr.setText(year+"-"+monthOfYear+"-"+dayOfMonth);
                    }
                }, anio, mes, dia);
                datePickerDialog.show();
            }
        });
        //FIN CODIGO FECHA

        actualizarSer = (Button) view.findViewById(R.id.btnActualizarDiagnostico);

        actualizarSer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        final String resultado = enviarDatosGET(idServicio.getText().toString(), falla.getText().toString(), diagnostico.getText().toString(),
                                observacion.getText().toString(), fechaEntr.getText().toString(), Double.parseDouble(precio.getText().toString()),
                                 clave.getText().toString());
                        getActivity().runOnUiThread(new Runnable() {
                            int r = obtenerDatosJSON(resultado);
                            @Override
                            public void run() {
                                if (r > 0) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Se ha actualizado correctamente", Toast.LENGTH_SHORT).show();
                                    servicioBuscar.setText("");
                                    idServicio.setText("None");
                                    fechaServ.setText("2019-01-01");
                                    cedulaEmp.setText("1234567");
                                    marca.setText("None");
                                    modelo.setText("None");
                                    falla.setText("None");
                                    diagnostico.setText("None");
                                    observacion.setText("");
                                    fechaEntr.setText("2019-01-01");
                                    precio.setText("");
                                    clave.setText("");
                                    cedulaCli.setText("None");
                                    estadoAct.setText("None");
                                }else {
                                    Toast.makeText(getContext(), "¡Algo malo ocurrio!", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getContext(), "Mensaje " + resultado, Toast.LENGTH_SHORT).show();
                                    progressDialog.hide();
                                }
                            }
                        });
                    }
                };
                thread.start();
            }
        });

        buscarServicio = (Button) view.findViewById(R.id.btnBuscarServicio);
        buscarServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //agregas un mensaje en el ProgressDialog
                progressDialog.setMessage("Cargando...");
                //muestras el ProgressDialog
                progressDialog.show();
                if (servicioBuscar.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "¡Complete los campos!", Toast.LENGTH_SHORT).show();
                }else {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            final String resultado = buscarDatosGET(servicioBuscar.getText().toString());
                            final int res = validarDatosJSON(resultado);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (res == 0) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "No existe un diagnostico con ese ID", Toast.LENGTH_SHORT).show();
                                    } else {
                                        cargarDatos(resultado);
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Se busco correctamente.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    };
                    thread.start();
                }
            }
        });
        actualizarVen = (Button) view.findViewById(R.id.btnActualizarVenta);
        actualizarVen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarVen();
            }
        });
        return view;
    }

    public void actualizarVen(){
        Intent intent = new Intent(getContext(), ActualizarVentaActivity.class);
        startActivity(intent);
    }

    //METODO PARA ENVIAR LOS DATOS AL SERVIDOR LOCAL DE BUSCAR SERVICIO
    public String buscarDatosGET(String servicio) {
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.6/ServiciosWeb/buscarServicio.php";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/buscarServicio.php";

        try {
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "?servicio=" + servicio);
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

    public int validarDatosJSON(String response){
        int res = 0;
        try {
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() > 0 ){
                res = 1;
            }
        }catch (Exception e){}
        return res;
    }

    //====== INICIO CODIGO IMPORTANTE PARA LA CONEXION CON EL SERVIDOR ======
    //METODO PARA ENVIAR LOS DATOS AL SERVIDOR LOCAL
    public String enviarDatosGET(String id_serv, String fallaExpresada, String diagnostico, String observación,String tiempoEntrega,
                                 double precioReparacion, String clave){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.6/ServiciosWeb/actualizarServicio.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/actualizarServicio.php?";

        String falla = fallaExpresada.replace(" ", "%20");
        String diag = diagnostico.replace(" ", "%20");
        String obser = observación.replace(" ", "%20");
        //empleado = recuperado;
        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPO
            http://id_servicio=14
            // &fallaExpresada=jhjh&diagnostico=ffsdfs&observacion=ddfdasd&precioReparacion=130000&clave=none&fechaEntrega=2019-02-22
            url = new URL(url_aws  + "id_servicio=" + id_serv + "&fallaExpresada="+ falla +"&diagnostico="+ diag +"&observacion=" + obser
                    + "&precioReparacion=" + precioReparacion + "&fechaEntrega=" + tiempoEntrega + "&clave=" + clave);
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
    //====== FIN CODIGO CONEXION AL SERVIDOR ======

    //CARGAR EL RESULTADO EN EL FRAGMENT
    public ArrayList<String> cargarDatos(String response) {
        ArrayList<String> listado = new ArrayList<String>();

        try {
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            String estado = "";
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).getString("estado").equalsIgnoreCase("1")) {
                    estado = "En reparación";
                } else estado = "Reparado";
                /*
                String dato = jsonArray.getJSONObject(i).getString("foto");
                byte[] byteCode = Base64.decode(dato,Base64.DEFAULT);
                Bitmap imagen = BitmapFactory.decodeByteArray(byteCode,0,byteCode.length);
                */
                idServicio.setText(jsonArray.getJSONObject(i).getString("id_servicio"));
                fechaServ.setText(jsonArray.getJSONObject(i).getString("fecha_diagnostico"));
                cedulaEmp.setText(jsonArray.getJSONObject(i).getString("cedulaEmp"));
                marca.setText(jsonArray.getJSONObject(i).getString("marca"));
                modelo.setText(jsonArray.getJSONObject(i).getString("modelo"));
                falla.setText(jsonArray.getJSONObject(i).getString("falla"));
                diagnostico.setText(jsonArray.getJSONObject(i).getString("diagnostico"));
                observacion.setText(jsonArray.getJSONObject(i).getString("observacion"));
                precio.setText(jsonArray.getJSONObject(i).getString("precio"));
                cedulaCli.setText(jsonArray.getJSONObject(i).getString("cedulaCli"));
                clave.setText(jsonArray.getJSONObject(i).getString("clave"));
                estadoAct.setText(estado);
                fechaEntr.setText(jsonArray.getJSONObject(i).getString("fechaEntrega"));
                /*
                if (imagen != null) {
                    foto.setImageBitmap(imagen);
                }else foto.setImageResource(R.drawable.no_disponible);
                */
                listado.add(texto);
            }
        } catch (Exception e) {
        }
        return listado;
    }


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
