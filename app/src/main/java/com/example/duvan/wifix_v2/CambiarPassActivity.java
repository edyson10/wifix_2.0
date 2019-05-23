package com.example.duvan.wifix_v2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CambiarPassActivity extends AppCompatActivity {

    Button btnConfirmarCambiado;
    EditText cedula, contraseñaActual, contraseñaNueva, repContraseña;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_pass);
        cedula = (EditText)findViewById(R.id.txtCedulaEmpleado);
        btnConfirmarCambiado =(Button) findViewById(R.id.btnConfirmarCambiado);
        contraseñaActual = (EditText) findViewById(R.id.contraseñaActual);
        contraseñaNueva = (EditText) findViewById(R.id.txtPassNueva);
        repContraseña = (EditText) findViewById(R.id.txtConfirmarPass);

        //ACCION DEL BOTON CONFIRMAR ACTULIZACIÓN DE LA CONTRASEÑA
        btnConfirmarCambiado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        final String resultado = enviarDatosGET(cedula.getText().toString(), contraseñaActual.getText().toString(), contraseñaNueva.getText().toString(),
                                repContraseña.getText().toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int r = obtenerDatosJSON(resultado);
                                if(contraseñaNueva.getText().toString().equalsIgnoreCase(repContraseña.getText().toString())){
                                    //ESTA BIEN ESTA LINEA DE CODIGO SIN ERROR
                                    Toast.makeText(getApplicationContext(),"Contraseña cambiada.",Toast.LENGTH_SHORT).show();
                                    cedula.setText("");
                                    contraseñaActual.setText("");
                                    contraseñaNueva.setText("");
                                    repContraseña.setText("");
                                }else {
                                    Toast.makeText(getApplicationContext(),"Estan mal la contraseña, copie bien!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                };
                thread.start();
            }
        });

    }

    //METODO PARA ENVIAR LOS DATOS AL SERVIDOR LOCAL
    public String enviarDatosGET(String cedula, String password, String conNueva, String repCon){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.4/ServiciosWeb/cambiarContraseña.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/cambiarContraseña.php?";

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            //http://localhost/ServiciosWeb/cambiarContraseña.php?cedula=2017&pass=hola&password=edy100&repass=edy100
            url = new URL(url_aws + "cedula=" + cedula + "&pass=" + password + "&password=" + conNueva + "&repass=" + repCon);
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
}
