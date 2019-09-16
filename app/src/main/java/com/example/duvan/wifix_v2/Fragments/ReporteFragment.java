package com.example.duvan.wifix_v2.Fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duvan.wifix_v2.BajasActivity;
import com.example.duvan.wifix_v2.R;
import com.example.duvan.wifix_v2.UtilidadAleActivity;
import com.example.duvan.wifix_v2.UtilidadPalActivity;
import com.example.duvan.wifix_v2.UtilidadSeptimaActivity;
import com.google.zxing.integration.android.IntentIntegrator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ReporteFragment extends Fragment {

    View vista;
    TextView fecha;
    Calendar mCurrentDate;
    int dia, mes, anio;
    Button reporteDiaPal,reporteDiaAl, reporteDiaSep, reporteMes, bajas, utilidadP;
    private ProgressDialog progressDialog;
    String recuperado = "";

    private OnFragmentInteractionListener mListener;

    public ReporteFragment() {
        // Required empty public constructor
    }

    public static ReporteFragment newInstance(String param1, String param2) {
        ReporteFragment fragment = new ReporteFragment();
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
        vista = inflater.inflate(R.layout.fragment_reporte, container, false);

        progressDialog = new ProgressDialog(getContext());

        //CODIGO FECHA DE ENTREGA ESTIPULADA
        fecha = (TextView) vista.findViewById(R.id.fechaReporte);
        mCurrentDate = Calendar.getInstance();
        dia = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        mes = mCurrentDate.get(Calendar.MONTH);
        anio = mCurrentDate.get(Calendar.YEAR);
        fecha.setText(anio + "-" + (mes + 1) + "-" + dia);
        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker , int year , int monthOfYear,  int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        fecha.setText(year+"-"+monthOfYear+"-"+dayOfMonth);
                    }
                }, anio, mes, dia);
                datePickerDialog.show();
            }
        });

        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        //REPORTE LOCAL PALACIO
        ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            reporteDiaPal = (Button) vista.findViewById(R.id.btnReporteDia);
            reporteDiaPal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getContext(), fecha.getText().toString(),Toast.LENGTH_SHORT).show();
                    Uri url_local = Uri.parse("http://192.168.65.1/ServiciosWeb/indexpordia.php?fecha=" + fecha.getText().toString());
                    //Uri url_aws = Uri.parse("http://18.228.235.94/wifix/ServiciosWeb/indexpordia.php?fecha=" + fecha.getText().toString());
                    Uri url_aws = Uri.parse("http://18.228.235.94/wifix/ServiciosWeb/reporteDiarioPal.php?fecha=" + fecha.getText().toString());
                    Intent intent = new Intent(Intent.ACTION_VIEW, url_aws);
                    startActivity(intent);
                }
            });
        } else{
            Toast.makeText(getContext(), "Verifique su conexión a internet",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        //REPORTE LOCAL ALEJANDRIA
        if(networkInfo != null && networkInfo.isConnected()) {
            reporteDiaAl = (Button) vista.findViewById(R.id.btnReporteDiaAl);
            reporteDiaAl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getContext(), fecha.getText().toString(),Toast.LENGTH_SHORT).show();
                    Uri url_local = Uri.parse("http://192.168.65.1/ServiciosWeb/indexpordia2.php?fecha=" + fecha.getText().toString());
                    Uri url_aws = Uri.parse("http://18.228.235.94/wifix/ServiciosWeb/indexpordia2.php?fecha=" + fecha.getText().toString());
                    Intent intent = new Intent(Intent.ACTION_VIEW, url_aws);
                    startActivity(intent);
                }
            });
        } else{
            Toast.makeText(getContext(), "Verifique su conexión a internet",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        //CODIGO PARA VALIDAD SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        //REPORTE LOCAL SEPTIMA
        if(networkInfo != null && networkInfo.isConnected()) {
            reporteDiaSep = (Button) vista.findViewById(R.id.btnReporteDiaSep);
            reporteDiaSep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri url_aws = Uri.parse("http://18.228.235.94/wifix/ServiciosWeb/indexpordia3.php?fecha=" + fecha.getText().toString());
                    Intent intent = new Intent(Intent.ACTION_VIEW, url_aws);
                    startActivity(intent);
                }
            });
        }  else{
            Toast.makeText(getContext(), "Verifique su conexión a internet",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }


        reporteMes = (Button) vista.findViewById(R.id.btnReporteMes);
        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        if(networkInfo != null && networkInfo.isConnected()) {
            reporteMes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri url_local = Uri.parse("");
                    Uri url_aws = Uri.parse("http://18.228.235.94/wifix/ServiciosWeb/indexpormes.php");
                    Intent intent = new Intent(Intent.ACTION_VIEW, url_aws);
                    startActivity(intent);
                }
            });
        }else {
            Toast.makeText(getContext(), "Verifique su conexión a internet",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        Bundle recupera = getActivity().getIntent().getExtras();
        if(recupera != null){
            recuperado = recupera.getString("cedula");
        }

        bajas = (Button)vista.findViewById(R.id.btnRealizarBaja);
        bajas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BajasActivity.class);
                intent.putExtra("cedula", recuperado);
                startActivity(intent);
            }
        });

        utilidadP = (Button) vista.findViewById(R.id.btnUtilidadPal);
        utilidadP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaUtilidadPal();
            }
        });

        return vista;
    }

    public void vistaUtilidadPal(){
        Intent intent = new Intent(getContext(), UtilidadPalActivity.class);
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
