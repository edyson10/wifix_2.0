package com.example.duvan.wifix_v2;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class ReparacionActivity extends AppCompatActivity {

    EditText buscar, costo, precio, detalle, bodega, repuesto;
    TextView diagnostico, cedulaEmp, fechaDiag, marca, modelo, falla, observacion, precioDiag, clave, cedulaCli;
    TextView fechaEntrega;
    ImageButton btnBuscar;
    Button registrar, visualizar;
    Calendar mCurrentDate;
    int dia, mes, anio;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reparacion);

        progressDialog = new ProgressDialog(ReparacionActivity.this);
        buscar = (EditText) findViewById(R.id.txtBuscarDiagnostico);
        costo = (EditText) findViewById(R.id.txtCostoRep);
        precio = (EditText) findViewById(R.id.txtPrecioRep);
        detalle = (EditText) findViewById(R.id.txtDetalleRep);
        bodega = (EditText) findViewById(R.id.txtBodegaRep);
        repuesto = (EditText) findViewById(R.id.txtRepuestoRep);
        diagnostico = (TextView) findViewById(R.id.txtIdDiagnostico);
        cedulaEmp = (TextView) findViewById(R.id.txtCedulaEmpleadoDiag);
        fechaDiag = (TextView) findViewById(R.id.txtFechaDiagnosticoRep);
        marca = (TextView) findViewById(R.id.txtMarcaRep);
        modelo = (TextView) findViewById(R.id.txtModeloRep);
        falla = (TextView) findViewById(R.id.txtFallaRep);
        observacion = (TextView) findViewById(R.id.txtObservcionRep);
        precioDiag = (TextView) findViewById(R.id.txtPrecioDiag);
        clave = (TextView) findViewById(R.id.txtClaveRep);
        cedulaCli= (TextView) findViewById(R.id.txtCedulaClienteRep);
        btnBuscar = (ImageButton) findViewById(R.id.btnBuscarDiagnostico);
        registrar = (Button) findViewById(R.id.btnRegistrarRep);
        visualizar = (Button) findViewById(R.id.btnVerReparacion);

        //CODIGO FECHA DE ENTREGA ESTIPULADA
        fechaEntrega = (TextView) findViewById(R.id.fechaEntregaRep);
        mCurrentDate = Calendar.getInstance();
        dia = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        mes = mCurrentDate.get(Calendar.MONTH);
        anio = mCurrentDate.get(Calendar.YEAR);
        fechaEntrega.setText(anio + "-" + (mes + 1) + "-" + dia);
        fechaEntrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getApplicationContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker , int year , int monthOfYear,  int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        fechaEntrega.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                    }
                }, anio, mes, dia);
                //Linea de codigo importante para que muestre la ventana de dialogo para seleccionar la fecha, sino genera error.
                datePickerDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                datePickerDialog.show();
            }
        });
        //FIN CODIGO FECHAS

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
                   buscarDiagnostico(buscar.getText().toString());
               }
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bodega.getText().toString().isEmpty() || buscar.getText().toString().isEmpty() || costo.getText().toString().isEmpty()
                        || precio.getText().toString().isEmpty() || detalle.getText().toString().isEmpty()) {
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

        visualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visualizarDiagnostico();
            }
        });
    }

    private void registrarReparacion(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                final String resultado = registrarDatosGET(Integer.parseInt(buscar.getText().toString()), bodega.getText().toString(),
                        costo.getText().toString(), precio.getText().toString(), detalle.getText().toString(),
                        repuesto.getText().toString(), fechaEntrega.getText().toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int r = validarDatosJSON(resultado);
                        if (r > 0) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Se ha registrado la reparación exitosamente", Toast.LENGTH_SHORT).show();
                            diagnostico.setText("None");
                            fechaDiag.setText("2019-01-01");
                            cedulaEmp.setText("123456789");
                            marca.setText("None");
                            modelo.setText("None");
                            falla.setText("None");
                            observacion.setText("None");
                            precioDiag.setText("0");
                            clave.setText("None");
                            cedulaCli.setText("123456789");
                            buscar.setText("");
                            bodega.setText("");
                            repuesto.setText("");
                            detalle.setText("");
                            costo.setText("");
                            precio.setText("");
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

    public void buscarDiagnostico(final String bus){
        Thread thread = new Thread(){
            @Override
            public void run() {
                final String resultado = buscarReparacionGET(bus);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int r = validarDatosJSON(resultado);
                        if (r > 0) {
                            progressDialog.dismiss();
                            cargarDiagnostico((resultado));
                            Toast.makeText(getApplicationContext(), "Se han cargado el diagnostico exitosamente.", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "No existe un diagnostico registrado con ese ID.", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        thread.start();
    }

    public void visualizarDiagnostico(){
        Intent intent = new Intent(getApplicationContext(), ListarDiagnosticoActivity.class);
        startActivity(intent);
    }

    //CARGAR LA LOS DATOS RECIBIDO EN EL LISTVIEW
    public void cargarDiagnostico(String response){
        try{
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0;i<jsonArray.length();i++){
                diagnostico.setText(jsonArray.getJSONObject(i).getString("id_servicio"));
                cedulaEmp.setText(jsonArray.getJSONObject(i).getString("cedulaEmp"));
                fechaDiag.setText(jsonArray.getJSONObject(i).getString("fecha_diagnostico"));
                marca.setText(jsonArray.getJSONObject(i).getString("modelo"));
                modelo.setText(jsonArray.getJSONObject(i).getString("falla"));
                falla.setText(jsonArray.getJSONObject(i).getString("falla"));
                observacion.setText(jsonArray.getJSONObject(i).getString("observacion"));
                precioDiag.setText(jsonArray.getJSONObject(i).getString("precio"));
                clave.setText(jsonArray.getJSONObject(i).getString("clave"));
                cedulaCli.setText(jsonArray.getJSONObject(i).getString("cedulaCli"));
            }
        }catch (Exception e){}
    }

    public String registrarDatosGET(int idDiagnostico, String bodega, String costo, String precio, String detalle, String repuesto, String fechaEntrega){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.6/ServiciosWeb/registrarReparacion.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/registrarReparacion.php?";
        String bod = bodega.replace(" ", "%20");
        String deta = detalle.replace(" ", "%20");
        String rep = repuesto.replace(" ", "%20");
        //empleado = recuperado;
        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPO
            http://servicio=21&bodega=dos&detalle=se cambio pantalla&costo=1000&precio=35000&fechaEntrega=2019-03-03
            url = new URL(url_aws + "servicio=" + idDiagnostico + "&bodega=" + bod + "&repuesto=" + rep + "&detalle=" + deta
                    +"&costo="+ costo +"&precio=" + precio +"&fechaEntrega="+ fechaEntrega);
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

    public String buscarReparacionGET(String buscar){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.6/ServiciosWeb/buscarServicio.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/buscarServicio.php?";

        try{
            url = new URL(url_aws + "cedulaCli=" + buscar);
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
