package com.example.duvan.wifix_v2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends Activity implements View.OnClickListener{

    EditText usuario, contraseña;
    Button login, registro;
    TextView olvidastoPass;
    private ProgressDialog progressDialog;
    private SharedPreferences preferences;
    private String tienda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        cargarPreferencias();
        progressDialog = new ProgressDialog(LoginActivity.this);
        //preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        usuario = (EditText) findViewById(R.id.txtUser);
        contraseña = (EditText) findViewById(R.id.txtPass);
        login = (Button) findViewById(R.id.btnLoginActivity);
        login.setOnClickListener(this);

        /*
        olvidastoPass = (TextView) findViewById(R.id.txtOlvidoPass);
        olvidastoPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, recuperarPassActivity.class);
                startActivity(intent);
            }
        });
        */
        //setCredentialsIfExist();
    }

    /*
    private void setCredentialsIfExist() {
        String email = getUserPreferences();
        String pass = getPassPreferences();
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
    */

    private void goToMain() {
        Intent intent = new Intent( getApplicationContext(), MainActivity.class);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(final View view) {
        //agregas un mensaje en el ProgressDialog
        progressDialog.setMessage("Cargando...");
        //muestras el ProgressDialog
        progressDialog.show();
        //final String rol = validar();
        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        ConnectivityManager con = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            Thread thread = new Thread() {
                @Override
                public void run() {
                    final String resultado = enviarDatosGET(usuario.getText().toString(), contraseña.getText().toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int r = obtenerDatosJSON(resultado);
                            int tipo = cargarDatos(resultado);
                            if (usuario.getText().toString().isEmpty() || contraseña.getText().toString().isEmpty()) {
                                Toast.makeText(getApplicationContext(), "¡Complete los campos!", Toast.LENGTH_LONG).show();
                            } else {
                                if (r > 0) {
                                    progressDialog.dismiss();
                                    if (tipo ==  1){
                                        Intent intent = new Intent(getApplicationContext(), GerenteMainActivity.class);
                                        intent.putExtra("cedula", usuario.getText().toString());
                                        savePreferences(usuario.getText().toString(),contraseña.getText().toString(), tienda);
                                        goToMain();
                                        startActivity(intent);
                                    } else if(tipo == 2){
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.putExtra("cedula", usuario.getText().toString());
                                        savePreferences(usuario.getText().toString(),contraseña.getText().toString(), tienda);
                                        goToMain();
                                        startActivity(intent);
                                    } else if(tipo == 3){
                                        Intent intent = new Intent(getApplicationContext(), MainEmpleadoActivity.class);
                                        intent.putExtra("cedula", usuario.getText().toString());
                                        savePreferences(usuario.getText().toString(),contraseña.getText().toString(), tienda);
                                        goToMain();
                                        startActivity(intent);
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Usuario y/o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_SHORT).show();
                                    progressDialog.hide();
                                }
                            }
                            progressDialog.hide();
                        }
                    });
                }
            };
            thread.start();
        } else {
            Toast.makeText(getApplicationContext(), "Verifique su conexión a internet",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    //METODO PARA ENVIAR LOS DATOS AL SERVIDOR LOCAL
    public String enviarDatosGET(String usu, String pass){
        URL url = null;
        String linea = "";
        int respuesta = 0;

        StringBuilder resul = null;
        String url_local = "http://192.168.56.1/ServiciosWeb/loginBD.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/loginBD.php?";

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO
            url = new URL(url_aws + "cedula=" + usu + "&password=" + pass);
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

    public int cargarDatos(String response) {
        int tipo = 0;
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                tipo = Integer.parseInt(jsonArray.getJSONObject(i).getString("id_tipoempleado"));
                tienda = jsonArray.getJSONObject(i).getString("tienda");
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Error: " + ex, Toast.LENGTH_SHORT).show();
        }
        return tipo;
    }

    private void savePreferences(String cedula, String pass, String tienda){
        SharedPreferences preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("cedula", cedula);
        editor.putString("pass", pass);
        editor.putString("tienda", tienda);
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void cargarPreferencias(){
        SharedPreferences preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        String user = preferences.getString("rol", "");

        if(user.equalsIgnoreCase("1")){
            //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            //Intent intent = new Intent(getApplicationContext(), MasterMainActivity.class);
            //startActivity(intent);
            //intent.putExtra("cedula", usuario.getText().toString());
        } else if(user.equalsIgnoreCase("2")){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}
