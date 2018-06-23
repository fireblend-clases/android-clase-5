package com.cenfotec.fotos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


/* Notas De Implementacion:

    - Debe declararse en el Manifest, un FileProvider (ver Manifest 
	en este proyecto) asi como un file_paths.xml bajo el directorio 
	de res/xml. Tanto la declaracion en el Manifest como en el archivo 
	xml, deben tener el nombre del paquete correcto (com.*****.*****).

    -SIEMPRE es necesario tanto declarar permisos y el uso de la camara
    en el manifest, como pedir dichos permisos durante la ejecución de
    la aplicación.
 */

public class MainActivity extends AppCompatActivity {
    private static final int PERM_CODE = 1000;
    private static final int REQUEST_TAKE_PHOTO = 101;

    private Uri mUri;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView)findViewById(R.id.imagen);

        //Al iniciar el app, verificamos si es necesario pedir permisos:
        //Por simplicidad, estamos ejecutando esta funcion al iniciar
        //el app. Opcionalmente, esta llamada podria encontrarse dentro
        //del onClickListener de un boton, o como respuesta a cualquier
        //otra accion del usuario o la aplicación.
        verificarPermisos();
    }

    private void verificarPermisos() {
        //Obtenemos el estado actual de los permisos
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //Si ya tenemos permisos, continuamos tomando la foto
        //Si no, pedimos permiso
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            continuarTomarFoto();
        } else {
            askForPermission();
        }
    }

    public void askForPermission(){
        //Hacemos la solicitud de permiso
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERM_CODE);
    }

    //Este callback es llamado cuando un usuario contesta
    //a la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        //Si obtuvimos valores en grantResults y el primer elemento
        //es de PERMISSION_GRANTED (permiso concedido), volvemos a llamar
        //a verificarPermisos.

        //Si el usuario no dio permiso, llamamos finish() para cerrar la
        //aplicacion
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED){
            verificarPermisos();
        } else {
            finish();
        }
    }

    private void continuarTomarFoto() {
        //Llamamos al metodo crearArchivo para obtener un
        //archivo en el cual guardar la foto
        File archivo = crearArchivo();

        //Construimos un intent con una peticion de captura
        //de imagenes
        Intent takePictureIntent =
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Guardamos en el directorio usando el FileProvider:
        mUri = FileProvider.getUriForFile(this,
                "com.cenfotec.fotos",
                archivo);

        //Especificamos el URI en el que queremos que se guarde
        //la imagen
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        //Ejecutamos el intent, cediendo control a la aplicacion
        //de toma de fotos que el usuario seleccione
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }

    private File crearArchivo() {
        try {
            //Creamos un nombre unico para el archivo, basado
            //en la fecha y hora actual
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());
            String imageFileName = "JPEG_" + timeStamp;

            File storageDir =
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            //Creamos el archivo para la imagen...
            File image = File.createTempFile(
                    imageFileName, /* prefix */
                    ".jpg", /* suffix */
                    storageDir /* directory */
            );
            //...y lo retornamos
            return image;
        }catch(Exception e){
            Log.d("Prueba", e.getMessage());
            return null;
        }
    }

    //Al haber llamado a onStartActivityForResult, indicamos al
    //sistema que llame a este callback una vez la foto haya sido
    //capturada y almacenada
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Verificamos que el codigo de respuesta sea igual al codigo
        //de peticion que especificamos al ejecutar el intent

        //Tambien verificamos que el codigo resultado sea RESULT_OK,
        //lo cual indica que la foto fue capturada exitosamente.
        if (requestCode == REQUEST_TAKE_PHOTO &&
                resultCode == RESULT_OK) {
            try {
                //Obtenemos el BitMap a partir del URI que habiamos
                //obtenido anteriormente
                Bitmap imageBitmap =
                        MediaStore.Images.Media.
                                getBitmap(getContentResolver(), mUri);

                //Mostramos el bitmap en el ImageView declarado
                //en nuestro layout file
                mImageView.setImageBitmap(imageBitmap);
            }catch(Exception e){
                Log.d("Prueba", e.getMessage());
            }
        }
    }

}