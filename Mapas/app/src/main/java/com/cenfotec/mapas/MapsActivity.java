package com.cenfotec.mapas;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    //Codigo del intent para abrir el formulario
    private final static int PERM_CODE = 1;

    //Tenemos que guardar la ubicacion en donde el usuario pidio
    //crear un nuevo marker
    private LatLng mTempPosicion;

    //El mapa
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //Obtenemos el SupportFragment del mapa declarado en el layout activity_maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //Para inicializar el mapa, llamamos a getMapAsync
        //Toma como parametro algun objeto que implemente la interfaz
        //de OnMapReadyCallback, que en este caso, es este Activity.
        //Cuando el mapa este listo, se va a llamar a onMapReady()
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Obtenemos una referencia al mapa inicializado para siempre
        //poder manipularlo
        mMap = googleMap;

        //Seteamos a este activity como el LongClickListener para escuchar
        //eventos de presion prolongada. En cada evento, se va a llamar a
        //onMapLongClick()
        mMap.setOnMapLongClickListener(this);

        //Se crea un objeto de ubicacion que corresponde a Cenfotec
        LatLng cenfotec = new LatLng(9.9328022,-84.0317056);
        //Movemos y asignamos un nivel de zoom a la camara
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cenfotec, 12.0f));

        //Revisamos si tenemos permiso para mostrar el boton de ubicacion
        //del usuario
        chequearPermiso();
    }

    private void chequearPermiso() {
        //Obtenemos el estado del permiso de ubicacion
        int state = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);

        //Si lo tenemos, habilitamos el boton de ubicacion del usuario
        if (state == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            //Si no, pedimos permiso
            askForPermission();
        }
    }

    public void askForPermission(){
        //Pedimos permiso para el de tipo ACCESS_FINE_LOCATION
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERM_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        //Si el usuario nos dio permiso, entonces podemos llamar a
        //chequearPermiso de nuevo. Si no, no hacemos nada (y el boton de
        //ubicacion no se va a mostrar)
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED){
            chequearPermiso();
        }
    }

    //Esta funcion dicta que ocurre al recibir un evento de presion
    //prolongada sobre una posicion no ocupada del mapa
    @Override
    public void onMapLongClick(LatLng latLng) {
        //Guardamos la ubicacion presionada para recordarla
        mTempPosicion = latLng;

        //OPCION 1: Crear un dialogo para pedir el titulo del nuevo marcador
        /*new MaterialDialog.Builder(this)
                //Titulo del dialogo
                .title(R.string.input)
                //Contenido del dialogo
                .content(R.string.input_content)
                //Tipo de entrada (en este caso, solo texto)
                .inputType(InputType.TYPE_CLASS_TEXT)
                //Hint de la entrada, texto por defecto, y un listener
                .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        //El listener es llamado cada vez que el texto cambia
                        //Como no ocupamos hacer nada en este evento, solo lo
                        //dejamos vacio.
                    }
                })
                //Boton de "crear"
                .positiveText(R.string.crear)
                //Callback para el boton de crear
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //Se obtiene el titulo que el usuario ingreso en el dialogo
                        String titulo = dialog.getInputEditText()
                                .getText().toString();
                        //Si el titulo esta vacio, se muestra un error y se cierra
                        //el dialogo
                        if(titulo.trim().isEmpty()){
                            Toast.makeText(MapsActivity.this, "Nombre invalido", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //Se añade un nuevo marcador al mapa con el titulo indicado.
                        mMap.addMarker(new MarkerOptions()
                                .position(mTempPosicion)
                                //Obtenemos el titulo que el usuario
                                //ingreso, por medio del objeto data en este metodo.
                                .title(titulo));
                    }
                })
                .show();
*/
        //OPCION 2: Llamamos al activity con el formulario para que nos lo
        //muestre. Al finalizar dicho Activity, vamos a recibir su
        //respusta en onActivityResult (abajo)

        Intent formIntent = new Intent(this, FormActivity.class);
        startActivityForResult(formIntent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Si el resultado fue exitoso (el usuario le dio click al boton de
        //agregar) entonces creamos un nuevo marcador
        if(requestCode == 100 && resultCode == Activity.RESULT_OK){
            mMap.addMarker(new MarkerOptions()
                    .position(mTempPosicion)
                    //Obtenemos el titulo y la descripcion que el usuario
                    //ingreso, por medio del objeto data en este metodo.
                    .title(data.getStringExtra("titulo"))
                    .snippet(data.getStringExtra("desc")));

            //Animar la camara hacia la nueva ubicacion
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mTempPosicion, 19.0f));
        }
    }
}
