package com.example.duvan.wifix_v2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import com.example.duvan.wifix_v2.Fragments.AcercaFragment;
import com.example.duvan.wifix_v2.Fragments.ActualizarFragment;
import com.example.duvan.wifix_v2.Fragments.BalanceFragment;
import com.example.duvan.wifix_v2.Fragments.BodegaFragment;
import com.example.duvan.wifix_v2.Fragments.DanosFragment;
import com.example.duvan.wifix_v2.Fragments.EditarFragment;
import com.example.duvan.wifix_v2.Fragments.EliminarFragment;
import com.example.duvan.wifix_v2.Fragments.EmpleadoFragment;
import com.example.duvan.wifix_v2.Fragments.ListarServiciosFragment;
import com.example.duvan.wifix_v2.Fragments.ProductoFragment;
import com.example.duvan.wifix_v2.Fragments.RegistrarProductoFragment;
import com.example.duvan.wifix_v2.Fragments.ReporteFragment;
import com.example.duvan.wifix_v2.Fragments.ServicioFragment;
import com.example.duvan.wifix_v2.Fragments.SettingFragment;
import com.example.duvan.wifix_v2.Fragments.VentasFragment;

public class MasterMainActivity extends AppCompatActivity
        //ES IMPORTANTE AÑADIR LOS ONFRAMENTINTERACTIONLISTENER DE LOS FRAGMENT CREADOS PARA QUE PUEDAN EJECUTARSE CADA UNA
        implements NavigationView.OnNavigationItemSelectedListener,VentasFragment.OnFragmentInteractionListener,
        ReporteFragment.OnFragmentInteractionListener, ServicioFragment.OnFragmentInteractionListener,
        SettingFragment.OnFragmentInteractionListener, EmpleadoFragment.OnFragmentInteractionListener,
        ProductoFragment.OnFragmentInteractionListener, AcercaFragment.OnFragmentInteractionListener,
        BalanceFragment.OnFragmentInteractionListener, DanosFragment.OnFragmentInteractionListener,
        EditarFragment.OnFragmentInteractionListener, RegistrarProductoFragment.OnFragmentInteractionListener{

    Icon logout;
    private SharedPreferences preferences;
    private ProgressDialog progressDialog;
    View view = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Fragment fragment = new SettingFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.content_main, fragment).commit();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        progressDialog = new ProgressDialog(MasterMainActivity.this);
        LayoutInflater imagen_alert = LayoutInflater.from(MasterMainActivity.this);
        view = imagen_alert.inflate(R.layout.imagen_alert, null);
    }

    @Override
    public void onBackPressed() {
        alertOneButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.master_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            alertOneButton();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment miFragment = null;
        boolean fragmentSeleccionado = false;

         if (id == R.id.nav_servicio) {
            miFragment = new ListarServiciosFragment();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_reporte) {
            miFragment = new ReporteFragment();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_producto){
            miFragment = new ProductoFragment();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_actualizar) {
            miFragment = new ActualizarFragment();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_bodega) {
            miFragment = new BodegaFragment();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_setting) {
            miFragment = new SettingFragment();
            fragmentSeleccionado = true;
        } else if(id == R.id.nav_acerca){
            miFragment = new AcercaFragment();
            fragmentSeleccionado = true;
        } else if(id == R.id.nav_balance){
            //miFragment = new BalanceFragment();
            //fragmentSeleccionado = true;
            Toast.makeText(getApplicationContext(), "Opción no disponible Gerente", Toast.LENGTH_SHORT).show();
        } else if(id == R.id.nav_danos){
            miFragment = new DanosFragment();
            fragmentSeleccionado = true;
        } else if(id == R.id.nav_editar) {
            miFragment = new EditarFragment();
            fragmentSeleccionado = true;
        } else if(id == R.id.nav_eliminar) {
            miFragment = new EliminarFragment();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_empleado){
            miFragment = new EmpleadoFragment();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_registra_producto) {
            miFragment = new RegistrarProductoFragment();
            fragmentSeleccionado = true;
        }

        //CODIGO AÑADIDO
        if (fragmentSeleccionado){
            //COLOCAR EN LAS OPCIONES SELECIONADAS LOS FRAGMENT QUE SELECCIONE EL USUARIO
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, miFragment).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void alertOneButton() {
        new AlertDialog.Builder(MasterMainActivity.this)
                .setIcon(R.drawable.icono)
                .setTitle("Sesión")
                .setMessage("¿Desea cerrar sesión?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        removePreferences();
                        //startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Se ha cerrado sesión", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private void removePreferences() {
        preferences.edit().clear().apply();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
