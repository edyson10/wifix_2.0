package com.example.duvan.wifix_v2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GarantiaActivity extends AppCompatActivity {

    EditText buscar, garantia;
    TextView reparacion, cedulaEmp, fechaRep, marca, modelo, falla, observacion, detalleRep, fechaEntrega, cedulaCli;
    ImageButton btnBuscar;
    Button registrar, verReparacion, verGarantia;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garantia);

        progressDialog = new ProgressDialog(GarantiaActivity.this);
        buscar = (EditText) findViewById(R.id.txtBusReparacion);
        reparacion = (TextView) findViewById(R.id.txtIdReparacion);
        fechaRep = (TextView) findViewById(R.id.txtFechaDiagnosticoGara);
        cedulaEmp = (TextView) findViewById(R.id.txtCedulaEmpleadoGara);
        marca = (TextView) findViewById(R.id.txtMarcaGara);
        modelo = (TextView) findViewById(R.id.txtModeloGara);
        falla = (TextView) findViewById(R.id.txtFallaGara);
        observacion = (TextView) findViewById(R.id.txtObservcionGara);
        detalleRep = (TextView) findViewById(R.id.txtDetalleGara);
        cedulaCli= (TextView) findViewById(R.id.txtCedulaClienteGara);
        fechaEntrega = (TextView) findViewById(R.id.txtFechaEntregaGara);
        garantia = (EditText) findViewById(R.id.txtObservacionGara);
        btnBuscar = (ImageButton) findViewById(R.id.btnBuscarReparacion);
        registrar = (Button) findViewById(R.id.btnRegGarantia);
        verReparacion = (Button) findViewById(R.id.btnVerReparacion);
        verGarantia = (Button) findViewById(R.id.btnVerGarantia);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buscar.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"¡Complete los campos!",Toast.LENGTH_SHORT).show();
                } else {
                    //agregas un mensaje en el ProgressDialog
                    progressDialog.setMessage("Cargando...");
                    //muestras el ProgressDialog
                    progressDialog.show();
                    buscarReparacion(buscar.getText().toString());
                }
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buscar.getText().toString().isEmpty() || garantia.getText().toString().isEmpty() ) {
                    Toast.makeText(getApplicationContext(),"¡Complete los campos!",Toast.LENGTH_SHORT).show();
                } else {
                    //agregas un mensaje en el ProgressDialog
                    progressDialog.setMessage("Cargando...");
                    //muestras el ProgressDialog
                    progressDialog.show();
                    registrarReparacion();
                }
            }
        });

        verReparacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visualizarReparacion();
            }
        });

        verGarantia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visualizarGarantia();
            }
        });
    }

    private void registrarReparacion(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                final String resultado = registrarDatosGET(Integer.parseInt(buscar.getText().toString()), garantia.getText().toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int r = validarDatosJSON(resultado);
                        if (r > 0) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Se ha la garantia exitosamente", Toast.LENGTH_SHORT).show();
                            buscar.setText("");
                            reparacion.setText("None");
                            fechaRep.setText("2019-01-01");
                            cedulaEmp.setText("123456789");
                            marca.setText("None");
                            modelo.setText("None");
                            falla.setText("None");
                            observacion.setText("None");
                            detalleRep.setText("None");
                            cedulaCli.setText("1234567890");
                            fechaEntrega.setText("2019-01-01");
                            garantia.setText("");
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Error al registrar", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        thread.start();
    }
    private void buscarReparacion(final String bus){
        Thread thread = new Thread(){
            @Override
            public void run() {
                final String resultado = obtenerDatosGET(bus);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int r = validarDatosJSON(resultado);
                        if (r > 0) {
                            progressDialog.dismiss();
                            cargarDiagnostico((resultado));
                            Toast.makeText(getApplicationContext(), "Se han cargado la reparación exitosamente.", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "No existe ninguna reparación con ese ID", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        thread.start();
    }

    public void visualizarReparacion(){
        Intent intent = new Intent(getApplicationContext(), ListarReparacionActivity.class);
        startActivity(intent);
    }

    public void visualizarGarantia(){
        Intent intent = new Intent(getApplicationContext(), ListarGarantiaActivity.class);
        startActivity(intent);
    }

    //CARGAR LA LOS DATOS RECIBIDO EN EL LISTVIEW
    public void cargarDiagnostico(String response){
        try{
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0;i<jsonArray.length();i++){
                reparacion.setText(jsonArray.getJSONObject(i).getString("id_reparacion"));
                fechaRep.setText(jsonArray.getJSONObject(i).getString("fecha_reparacion"));
                cedulaEmp.setText(jsonArray.getJSONObject(i).getString("cedulaEmp"));
                marca.setText(jsonArray.getJSONObject(i).getString("marca"));
                modelo.setText(jsonArray.getJSONObject(i).getString("modelo"));
                falla.setText(jsonArray.getJSONObject(i).getString("falla"));
                observacion.setText(jsonArray.getJSONObject(i).getString("observacion"));
                detalleRep.setText(jsonArray.getJSONObject(i).getString("detalle_reparacion"));
                cedulaCli.setText(jsonArray.getJSONObject(i).getString("cedulaCli"));
                fechaEntrega.setText(jsonArray.getJSONObject(i).getString("fecha_entrega"));
            }
        }catch (Exception e){}
    }

    public String registrarDatosGET(int idReparacion, String garantia){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.6/ServiciosWeb/registrarGarantia.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/registrarGarantia.php?";
        String gara = garantia.replace(" ", "%20");
        //empleado = recuperado;
        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPO
            http://servicio=21&bodega=dos&detalle=se cambio pantalla&costo=1000&precio=35000&fechaEntrega=2019-03-03
            url = new URL(url_aws + "reparacion=" + idReparacion + "&observacion=" + gara);
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

    public String obtenerDatosGET(String buscar){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.6/ServiciosWeb/buscarReparacion.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/buscarReparacion.php?";

        try{
            url = new URL(url_aws + "reparacion=" + buscar);
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
}
