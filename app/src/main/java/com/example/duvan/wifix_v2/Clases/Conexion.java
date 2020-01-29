package com.example.duvan.wifix_v2.Clases;

import android.content.Context;
import android.content.Intent;


import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.duvan.wifix_v2.MainActivity;
import com.example.duvan.wifix_v2.MainEmpleadoActivity;
import com.example.duvan.wifix_v2.MasterMainActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class Conexion {

    public static final String SERVER_LOCAL = "192.168.137.1";
    public static final String URL = "http://wifix.com.co/ServiciosWeb/";
    public static final String URL_IMAGEN = "http://wifix.com.co/ServiciosWeb/imagenes/";


    private static RequestQueue queue;

    public Conexion(){ }

    public static void login(final Context contexto, final String cedula, final String contraseña) {

        queue = Volley.newRequestQueue(contexto);

        if (cedula.isEmpty() || contraseña.isEmpty()) {
            Toast.makeText(contexto, "Por favor ingrese usuario y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        String urlEnvio = URL + "loginBD.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlEnvio,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("0")) {
                            Toast.makeText(contexto, "¡Usuario y/o contraseña incorrectos!", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                String cedula = jsonArray.getJSONObject(0).getString("cedula");
                                String tipo = jsonArray.getJSONObject(0).getString("id_tipoempleado");
                                String tienda = jsonArray.getJSONObject(0).getString("tienda");
                                savePreferences(contexto, cedula, tienda);
                                Intent intent = new Intent();
                                if(tipo.equals("1")){
                                    intent = new Intent(contexto, MainActivity.class);
                                    intent.putExtra("cedula", cedula);
                                    intent.putExtra("tienda", tienda);
                                } else if(tipo.equals("2")){
                                    intent = new Intent(contexto, MainActivity.class);
                                    intent.putExtra("cedula", cedula);
                                    intent.putExtra("tienda", tienda);
                                } else if(tipo.equals("3")){
                                    intent = new Intent(contexto, MainEmpleadoActivity.class);
                                    intent.putExtra("cedula", cedula);
                                    intent.putExtra("tienda", tienda);
                                }
                                contexto.startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(contexto, "A ocurrido un error", Toast.LENGTH_SHORT).show();
                        Log.e("TAG", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("cedula", cedula);
                parametros.put("password", contraseña);
                return parametros;
            }
        };
        queue.add(stringRequest);
    }

    public static void cargarProductos(final Context contexto){

    }

    public static void realizarVenta(final Context contexto, final String producto, final int precio, final int cantidad, final String cedula){

        queue = Volley.newRequestQueue(contexto);

        String urlEnvio = URL + "loginBD.php";
        final String pre = String.valueOf(precio);
        final String cant = String.valueOf(cantidad);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlEnvio,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if(response.equals("0")) {
                            Toast.makeText(contexto, "¡Usuario y/o contraseña incorrectos!", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(contexto, "A ocurrido un error", Toast.LENGTH_SHORT).show();
                        Log.e("TAG", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("cedula", cedula);
                parametros.put("producto", producto);
                parametros.put("cantidad", cant);
                parametros.put("precio", pre);
                return parametros;
            }
        };
    }

    public static final void savePreferences(Context contexto, String cedula, String tienda){
        SharedPreferences preferences = contexto.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("cedula", cedula);
        editor.putString("tienda", tienda);
        editor.commit();
    }
}
