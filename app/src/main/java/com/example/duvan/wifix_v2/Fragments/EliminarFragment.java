package com.example.duvan.wifix_v2.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.duvan.wifix_v2.EditarProductoActivity;
import com.example.duvan.wifix_v2.EliminaSalidaActivity;
import com.example.duvan.wifix_v2.EliminarServicioActivity;
import com.example.duvan.wifix_v2.EliminarVentaActivity;
import com.example.duvan.wifix_v2.MainActivity;
import com.example.duvan.wifix_v2.R;

public class EliminarFragment extends Fragment {

    View view;
    ImageView venta, servicio, salida, producto;

    private OnFragmentInteractionListener mListener;

    public EliminarFragment() {
        // Required empty public constructor
    }

    public static EliminarFragment newInstance(String param1, String param2) {
        EliminarFragment fragment = new EliminarFragment();
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
        view = inflater.inflate(R.layout.fragment_eliminar, container, false);
        venta = (ImageView) view.findViewById(R.id.imagenVentas);
        servicio = (ImageView) view.findViewById(R.id.imagenServicio);
        salida = (ImageView) view.findViewById(R.id.imagenSalida);
        producto = (ImageView) view.findViewById(R.id.imagenProducto);

        Thread thread = new Thread(){
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alertOneButton();
                    }
                });
            }
        };
        thread.start();

        venta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaVenta();
            }
        });
        servicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaServicio();
            }
        });
        salida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaSalida();
            }
        });
        producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaProducto();
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void alertOneButton() {
        new AlertDialog.Builder(getContext())
                .setIcon(R.drawable.icono)
                .setTitle("Advertencia")
                .setMessage("¿Seguro quieres continuar?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }

    public  void vistaVenta(){
        Intent intent = new Intent(getContext(), EliminarVentaActivity.class);
        startActivity(intent);
    }

    public void vistaServicio(){
        Intent intent = new Intent(getContext(), EliminarServicioActivity.class);
        startActivity(intent);
    }

    public void vistaSalida(){
        Intent intent = new Intent(getContext(), EliminaSalidaActivity.class);
        startActivity(intent);
    }

    public void vistaProducto(){
        Intent intent = new Intent(getContext(), EditarProductoActivity.class);
        startActivity(intent);
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
