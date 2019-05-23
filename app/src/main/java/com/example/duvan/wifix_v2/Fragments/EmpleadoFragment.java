package com.example.duvan.wifix_v2.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.duvan.wifix_v2.R;
import com.example.duvan.wifix_v2.UserActivity;

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

public class EmpleadoFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    View view;
    //CODIGO
    Button verEmpleados, registrarUser;
    EditText nombre, apellido, cedula,telefono, direccion, email, pass, conPass, tienda;
    private ProgressBar progressBar;
    Spinner spSexo;
    String [] sexo = {"Seleccione el sexo","Masculino","Femenino"};
    private ProgressDialog progressDialog;
    //FIN CODIGO

    public EmpleadoFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EmpleadoFragment newInstance(String param1, String param2) {
        EmpleadoFragment fragment = new EmpleadoFragment();
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
        view = inflater.inflate(R.layout.fragment_empleado, container, false);

        nombre = (EditText)view.findViewById(R.id.txtNombreReg);
        apellido = (EditText)view.findViewById(R.id.txtApellidoReg);
        cedula = (EditText)view.findViewById(R.id.txtCedulaReg);
        spSexo = (Spinner)view.findViewById(R.id.spSexoReg);
        telefono = (EditText)view.findViewById(R.id.txtTelefonoReg);
        direccion = (EditText)view.findViewById(R.id.txtDireccionReg);
        email = (EditText)view.findViewById(R.id.txtCorreoReg);
        pass = (EditText)view.findViewById(R.id.txtPasswordReg);
        conPass = (EditText)view.findViewById(R.id.txtRepPassReg);
        tienda = (EditText)view.findViewById(R.id.txtTienda);

        //CODIGO PARA MOSTRAR LOS DATOS EN EN SPINNER O COMBOBOX
        List<String> listaSexo;
        ArrayAdapter adapterSpinner;
        listaSexo = new ArrayList<>();
        //Cargo sexo en listaSexo
        Collections.addAll(listaSexo, sexo);
        //Paso los valores a mi adapter
        adapterSpinner = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listaSexo);
        //Linea de código secundario sirve para asignar un layout a los ítems
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Muestro los ítems en el spinner, obtenidos gracias al adapter
        spSexo.setAdapter(adapterSpinner);
        //FIN CODIGO

        registrarUser = (Button) view.findViewById(R.id.btnRegistrarUser);
        registrarUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        final String resultado = enviarDatosGET(nombre.getText().toString(), apellido.getText().toString(),
                                cedula.getText().toString(),spSexo.getSelectedItem().toString(),telefono.getText().toString(),
                                direccion.getText().toString(),email.getText().toString(),
                                pass.getText().toString(), conPass.getText().toString(), Integer.parseInt(tienda.getText().toString()));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int r = obtenerDatosJSON(resultado);
                                if(pass.getText().toString().equalsIgnoreCase(conPass.getText().toString())){
                                    //ESTA BIEN ESTA LINEA DE CODIGO SIN ERROR
                                    if (r > 0) {
                                        Toast.makeText(getContext(), "¡Algo ocurrio!" + resultado, Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getContext(), "Empleado registrado", Toast.LENGTH_LONG).show();
                                        nombre.setText("");
                                        apellido.setText("");
                                        cedula.setText("");
                                        telefono.setText("");
                                        direccion.setText("");
                                        email.setText("");
                                        pass.setText("");
                                        conPass.setText("");
                                        tienda.setText("");
                                    }
                                }else {
                                    Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                };
                thread.start();
            }
        });

        verEmpleados = (Button) view.findViewById(R.id.btnVisualizarUser);
        verEmpleados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarEmpleados();
            }
        });
        return view;
    }

    //METODO PARA ENVIAR LOS DATOS AL SERVIDOR LOCAL
    public String enviarDatosGET(String nombre,String apellido,String cedula,String sexo,String telefono,
                                 String direccion,String correo,String contrasena, String conContra, int tienda){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/registrarEmpleado.php";
        String dir = direccion.replace(" ", "%20");
        String url_local = "http://192.168.1.3/ServiciosWeb/registrarEmpleado.php";

        try {
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "?id_tipoempleado=" + 2 + "&nombre=" + nombre + "&apellido=" + apellido
                    + "&cedula=" + cedula + "&sexo="+ sexo + "&telefono=" + telefono + "&direccion=" + dir +
                    "&correo=" + correo + "&password=" + contrasena + "&repass=" + conContra + "&estado=" + 1 + "&tienda=" + tienda);
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
        //}
        return resul.toString();
    }

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

    /*METODO PARA PASAR A LA VISTA DE VISUALIZAR LOS EMPLEADOS
    CON EL NOMBRE Y EL TOTAL VENDIDOS*/
    public void mostrarEmpleados(){
        Intent intent = new Intent(getContext(), UserActivity.class);
        startActivity(intent);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
