package com.example.duvan.wifix_v2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.duvan.wifix_v2.Clases.Conexion;

public class LoginActivity extends Activity implements View.OnClickListener{

    EditText usuario, contraseña;
    Button login;
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
        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        ConnectivityManager con = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            Conexion.login(this, usuario.getText().toString(), contraseña.getText().toString());
            //Bundle datos = this.getIntent().getExtras();
            //String cedula = datos.getString("cedula");
            //savePreferences(cedula, cedula);
            //goToMain();
            progressDialog.dismiss();
        } else {
            Toast.makeText(getApplicationContext(), "Verifique su conexión a internet",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    private void savePreferences(String cedula, String tienda){
        SharedPreferences preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("cedula", cedula);
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
        String tienda = preferences.getString("tienda", "");

        if(tienda.equalsIgnoreCase("1")){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            //Intent intent = new Intent(getApplicationContext(), MasterMainActivity.class);
            startActivity(intent);
            //intent.putExtra("cedula", usuario.getText().toString());
        } else if(tienda.equalsIgnoreCase("2")){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}
