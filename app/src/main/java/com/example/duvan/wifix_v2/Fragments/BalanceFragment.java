package com.example.duvan.wifix_v2.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.provider.Settings;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.duvan.wifix_v2.Clases.Conexion;
import com.example.duvan.wifix_v2.Clases.VolleySingleton;
import com.example.duvan.wifix_v2.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;
public class BalanceFragment extends Fragment {

    View view;
    private Button btnBuscar;
    private Button btnSubir;
    private ImageView imagen;
    private EditText nombre;

    private static final String CARPETA_PRINCIPAL = "misImagenesApp/";
    private static final String CARPETA_IMAGEN = "imagenes";
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN;
    private String path;
    File fileImage;
    Bitmap bitmap;
    private static final int COD_SELECCIONA = 10;
    private static final int COD_FOTO = 20;
    private final int MIS_PERMISOS = 100;
    private ProgressDialog progressDialog;

    StringRequest stringRequest;
    RequestQueue request;
    private OnFragmentInteractionListener mListener;

    public BalanceFragment() {
        // Required empty public constructor
    }

    public static BalanceFragment newInstance(String param1, String param2) {
        BalanceFragment fragment = new BalanceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {  }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_balance, container, false);
        progressDialog = new ProgressDialog(getContext());
        btnBuscar = (Button) view.findViewById(R.id.btnBuscarImagen);
        btnSubir = (Button) view.findViewById(R.id.btnSubirFoto);
        nombre = (EditText) view.findViewById(R.id.txtNombreFoto);
        imagen  = (ImageView) view.findViewById(R.id.imgFoto);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarOpciones();
            }
        });
        btnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarWebService();
            }
        });

        /**Permisos**/
        if(solicitaPermisosVersionesSuperiores()){
            btnSubir.setEnabled(true);
        }else{
            btnSubir.setEnabled(true);
        }
        return view;
    }

    private void mostrarOpciones(){
        final CharSequence[] opciones = {"Tomar una foto","Elegir de galeria","Cancelar"};
        new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.icono)
                .setTitle("Eliaja una opción")
                .setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(opciones[i].equals("Tomar una foto")){
                            abrirCamara();
                        } else if(opciones[i].equals("Elegir de galeria")){
                            cargarImagen();
                        } else {
                            dialogInterface.dismiss();
                        }
                    }
                }).show();
    }

    private void cargarImagen(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent, "Seleccione la aplicación"), COD_SELECCIONA);
    }

    private void abrirCamara(){
        File file = new File(Environment.getExternalStorageDirectory(), DIRECTORIO_IMAGEN);
        boolean isCreada = file.exists();
        if(!isCreada){
            isCreada = file.mkdirs();
        }

        if(isCreada){
            Long consecutivo = System.currentTimeMillis()/1000;
            String nombre = consecutivo.toString() + ".jpg";
            path = Environment.getExternalStorageState() + File.separator + DIRECTORIO_IMAGEN + File.separator + nombre;
            fileImage = new File(path);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImage));
            startActivityForResult(intent, COD_FOTO);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case COD_SELECCIONA:
                Uri filePath = data.getData();
                //imagen.setImageURI(filePath);
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                    imagen.setImageBitmap(bitmap);
                } catch (IOException e){
                    e.printStackTrace();
                }
                break;
            case COD_FOTO:
                MediaScannerConnection.scanFile(getActivity(), new String[]{path}, null,
                    new MediaScannerConnection.OnScanCompletedListener(){
                        @Override
                        public void onScanCompleted(String s, Uri uri) {
                            Log.i("Path","" + s);
                        }
                    });
                bitmap = BitmapFactory.decodeFile(path);
                imagen.setImageBitmap(bitmap);
                break;
        }
    }

    private Bitmap redimensionarImagen(Bitmap bitmap, float anchoNuevo, float altoNuevo) {

        int ancho=bitmap.getWidth();
        int alto=bitmap.getHeight();

        if(ancho>anchoNuevo || alto>altoNuevo){
            float escalaAncho=anchoNuevo/ancho;
            float escalaAlto= altoNuevo/alto;

            Matrix matrix=new Matrix();
            matrix.postScale(escalaAncho,escalaAlto);

            return Bitmap.createBitmap(bitmap,0,0,ancho,alto,matrix,false);

        }else{
            return bitmap;
        }
    }

    /* ======================= START PERMISOS ======================= */
    private boolean solicitaPermisosVersionesSuperiores() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){//validamos si estamos en android menor a 6 para no buscar los permisos
            return true;
        }

        path = Environment.getExternalStorageState() + File.separator + DIRECTORIO_IMAGEN + File.separator + nombre;
        fileImage = new File(path);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        /*
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
            String authorities = getActivity().getPackageName() + ".provider";
            Uri imageUri = FileProvider.getUriForFile(getActivity(), authorities, fileImage);
        }
         */

        //validamos si los permisos ya fueron aceptados
        if((getContext().checkSelfPermission(WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)&&getContext().checkSelfPermission(CAMERA)==PackageManager.PERMISSION_GRANTED){
            return true;
        }

        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)||(shouldShowRequestPermissionRationale(CAMERA)))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MIS_PERMISOS);
        }

        return false;//implementamos el que procesa el evento dependiendo de lo que se defina aqui
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MIS_PERMISOS){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){//el dos representa los 2 permisos
                Toast.makeText(getContext(),"Permisos aceptados",Toast.LENGTH_SHORT);
                btnSubir.setEnabled(true);
            }
        }else{
            solicitarPermisosManual();
        }
    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"Si","No"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(getContext());//estamos en fragment
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getContext().getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(getContext());
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialogo.show();
    }
    /* ======================= END PERMISOS ======================= */

    private void cargarWebService() {

        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        String url = Conexion.URL + "upload.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.hide();
                if (response.trim().equalsIgnoreCase("registra")){
                    nombre.setText("");
                    Toast.makeText(getContext(),"Se ha registrado con exito",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),"No se ha registrado ",Toast.LENGTH_SHORT).show();
                    Log.i("RESPUESTA: ",""+response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"No se ha podido conectar",Toast.LENGTH_SHORT).show();
                Log.e("TAG", "error " + error.toString());
                progressDialog.hide();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String documento = nombre.getText().toString();
                String imagen = convertirImgString(bitmap);
                Map<String,String> parametros =new HashMap<>();
                parametros.put("nombre",documento);
                parametros.put("imagen",imagen);
                return parametros;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(stringRequest);
    }

    private String convertirImgString(Bitmap bitmap) {

        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,array);
        byte[] imagenByte=array.toByteArray();
        String imagenString = Base64.encodeToString(imagenByte,Base64.DEFAULT);
        return imagenString;
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
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
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
