package com.example.duvan.wifix_v2.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.duvan.wifix_v2.BuscarServActivity;
import com.example.duvan.wifix_v2.DiagnosticoActivity;
import com.example.duvan.wifix_v2.FotosActivity;
import com.example.duvan.wifix_v2.GarantiaActivity;
import com.example.duvan.wifix_v2.ListarServicioActivity;
import com.example.duvan.wifix_v2.R;
import com.example.duvan.wifix_v2.ReparacionActivity;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.support.v4.content.FileProvider.getUriForFile;

public class ServicioFragment extends Fragment{

    View vista;
    Button diagnosico, reparacion, garantia,listarServicio;
    private OnFragmentInteractionListener mListener;

    public ServicioFragment() {
        // Required empty public constructor
    }

    public static ServicioFragment newInstance(String param1, String param2) {
        ServicioFragment fragment = new ServicioFragment();
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

        vista = inflater.inflate(R.layout.fragment_servicio, container, false);
        diagnosico = (Button) vista.findViewById(R.id.btnVistaDiagnostico);
        reparacion = (Button) vista.findViewById(R.id.btnVistaReparacion);
        garantia = (Button) vista.findViewById(R.id.btnVistaGarantia);
        listarServicio = (Button) vista.findViewById(R.id.btnVistaListarServicios);

        diagnosico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaDiagnostico();
            }
        });
        reparacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaReparacion();
            }
        });
        garantia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaGarantia();
            }
        });
        listarServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaListaServicio();
            }
        });
        return vista;
    }

    public void vistaDiagnostico(){
        Intent intent = new Intent(getContext(), DiagnosticoActivity.class);
        startActivity(intent);

    }

    public void vistaReparacion(){
        Intent intent = new Intent(getContext(), ReparacionActivity.class);
        startActivity(intent);
    }

    public void vistaGarantia(){
        Intent intent = new Intent(getContext(), GarantiaActivity.class);
        startActivity(intent);
    }

    public void vistaListaServicio(){
        Intent intent = new Intent(getContext(), ListarServicioActivity.class);
        startActivity(intent);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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
