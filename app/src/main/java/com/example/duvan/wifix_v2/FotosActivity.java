package com.example.duvan.wifix_v2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class FotosActivity extends AppCompatActivity {


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    Button btnFotos;
    Button btnCargarImagen;
    ImageView foto1, foto2, foto3, foto4, foto5, foto6;
    ImageView [] collage = {foto1, foto2, foto3, foto4, foto5, foto6};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotos);
        btnFotos = findViewById(R.id.btnGuardarFotos);
        foto1 = (ImageView) findViewById(R.id.imagen1);
        foto2 = (ImageView) findViewById(R.id.imagen2);
        foto3 = (ImageView) findViewById(R.id.imagen3);
        foto4 = (ImageView) findViewById(R.id.imagen4);
        foto5 = (ImageView) findViewById(R.id.imagen5);
        foto6 = (ImageView) findViewById(R.id.imagen6);
        collage[0] = foto1;
        collage[1] = foto2;
        collage[2] = foto3;
        collage[3] = foto4;
        collage[4] = foto5;
        collage[5] = foto6;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            for (ImageView img : collage) {
                if(img.getDrawable()==null){
                    img.setImageBitmap(imageBitmap);
                    break;
                }
            }
        }
    }
}
