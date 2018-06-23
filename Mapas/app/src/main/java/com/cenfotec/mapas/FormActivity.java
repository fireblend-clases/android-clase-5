package com.cenfotec.mapas;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FormActivity extends AppCompatActivity implements View.OnClickListener {

    Button cancelar;
    Button agregar;

    EditText titulo;
    EditText descripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        //Declaramos y obtenemos los elementos de la interfaz
        agregar = (Button)findViewById(R.id.agregar);
        cancelar = (Button)findViewById(R.id.cancelar);
        titulo = (EditText)findViewById(R.id.titulo);
        descripcion = (EditText)findViewById(R.id.desc);

        //Agregamos a este activity como el listener para eventos de click
        //sobre los botones de agregar y cancelar
        agregar.setOnClickListener(this);
        cancelar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //Si el usuario presiono el boton de agregar...
        if(v.equals(agregar)){
            //Creamos un nuevo intent de respuesta...
            Intent respuesta = new Intent();
            //En el que almacenamos la informacion que el usuario escribio
            //en el formulario
            respuesta.putExtra("titulo", titulo.getText().toString());
            respuesta.putExtra("desc", descripcion.getText().toString());
            //Marcamos el resultado como OK, agregando el intent
            //de respuesta, y cerramos este activity.
            this.setResult(Activity.RESULT_OK, respuesta);
            finish();
        }
        //Si el usuario presiono el boton de cancelar...
        else if(v.equals(cancelar)){
            //Marcamos el resultado como CANCELADO y terminamos este activity.
            this.setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }
}
