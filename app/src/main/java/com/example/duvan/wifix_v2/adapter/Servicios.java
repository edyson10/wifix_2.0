package com.example.duvan.wifix_v2.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.duvan.wifix_v2.Entidades.ServicioVo;
import com.example.duvan.wifix_v2.R;

import java.util.ArrayList;

public class Servicios extends RecyclerView.Adapter<Servicios.ServiciosHolder> {

    ArrayList<ServicioVo> listaServicio;

    public Servicios(ArrayList<ServicioVo> listaServicio) {
        this.listaServicio = listaServicio;
    }

    @NonNull
    @Override
    public Servicios.ServiciosHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View vista = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_servicios,viewGroup,false);
        RecyclerView.LayoutParams layoutParams=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        vista.setLayoutParams(layoutParams);
        return new ServiciosHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull Servicios.ServiciosHolder serviciosHolder, int i) {
        serviciosHolder.fechaSer.setText(listaServicio.get(i).getFechaServicio().toString());
        serviciosHolder.cedulaEmp.setText(listaServicio.get(i).getCedulaEmp().toString());
        serviciosHolder.marca.setText(listaServicio.get(i).getMarca().toString());
        serviciosHolder.modelo.setText(listaServicio.get(i).getModelo().toString());
        serviciosHolder.falla.setText(listaServicio.get(i).getFalla().toString());
        serviciosHolder.diagnostico.setText(listaServicio.get(i).getDiagnostico().toString());
        serviciosHolder.observacion.setText(listaServicio.get(i).getObservacion().toString());
        serviciosHolder.costo.setText(listaServicio.get(i).getCosto().toString());
        serviciosHolder.clave.setText(listaServicio.get(i).getClave().toString());
        serviciosHolder.estado.setText(listaServicio.get(i).getEstado().toString());
        serviciosHolder.fechaEntrega.setText(listaServicio.get(i).getFechaEntrega().toString());
        serviciosHolder.foto.setImageResource(listaServicio.get(i).getImagen());
    }

    @Override
    public int getItemCount() {
        return listaServicio.size();
    }

    public class ServiciosHolder extends RecyclerView.ViewHolder{

        TextView fechaSer, cedulaEmp, marca, modelo, falla, diagnostico, observacion, costo, clave, estado, fechaEntrega;
        ImageView foto;
        public ServiciosHolder(@NonNull View itemView) {
            super(itemView);
            fechaSer = (TextView)itemView.findViewById(R.id.txtFechaServicioSer);
            cedulaEmp = (TextView)itemView.findViewById(R.id.txtCedulaEmpSer);
            marca = (TextView)itemView.findViewById(R.id.txtMarcaSer);
            modelo = (TextView)itemView.findViewById(R.id.txtModeloSer);
            falla = (TextView)itemView.findViewById(R.id.txtFallaSer);
            diagnostico = (TextView)itemView.findViewById(R.id.txtDiagnosticoSer);
            observacion = (TextView)itemView.findViewById(R.id.txtObservacionSer);
            costo = (TextView)itemView.findViewById(R.id.txtCostoSer);
            clave = (TextView)itemView.findViewById(R.id.txtClaveSer);
            estado = (TextView)itemView.findViewById(R.id.txtEstadoSer);
            fechaEntrega = (TextView)itemView.findViewById(R.id.txtFechaEntregaSer);
            foto = (ImageView) itemView.findViewById(R.id.imagenServicioSer);


        }
    }
}
