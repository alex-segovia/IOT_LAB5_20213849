package com.example.laboratorio5;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.laboratorio5.Objetos.Usuario;

public class MainActivity extends AppCompatActivity {

    private Usuario usuario = new Usuario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner spinnerGenero = findViewById(R.id.spinnerGenero);
        Spinner spinnerNivel = findViewById(R.id.spinnerNivel);
        Spinner spinnerObjetivo = findViewById(R.id.spinnerObjetivo);

        String[] generos = {"Selecciona tu género","Hombre","Mujer"};
        String[] niveles = {"Selecciona tu nivel de actividad física","Poco ejercicio","Ejercicio ligero","Ejercicio moderado","Ejercicio fuerte","Ejercicio muy fuerte"};
        String[] objetivos = {"Elige tu objetivo","Subir de peso","Bajar de peso","Mantener el peso"};

        ArrayAdapter<String> adapterGenero = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,generos){
            @Override
            public boolean isEnabled(int position) {
                return position!=0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if(position==0){
                    textView.setTextColor(Color.GRAY);
                }else{
                    textView.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        ArrayAdapter<String> adapterNivel = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,niveles){
            @Override
            public boolean isEnabled(int position) {
                return position!=0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if(position==0){
                    textView.setTextColor(Color.GRAY);
                }else{
                    textView.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        ArrayAdapter<String> adapterObjetivo = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,objetivos){
            @Override
            public boolean isEnabled(int position) {
                return position!=0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if(position==0){
                    textView.setTextColor(Color.GRAY);
                }else{
                    textView.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerGenero.setAdapter(adapterGenero);
        spinnerNivel.setAdapter(adapterNivel);
        spinnerObjetivo.setAdapter(adapterObjetivo);

        Button botonIniciar = findViewById(R.id.botonIniciar);
        botonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText textoPeso = findViewById(R.id.peso);
                usuario.setPeso(Float.parseFloat(textoPeso.getText().toString()));

                EditText textoAltura = findViewById(R.id.altura);
                usuario.setAltura(Float.parseFloat(textoAltura.getText().toString()));

                EditText textoEdad = findViewById(R.id.edad);
                usuario.setEdad(Integer.parseInt(textoEdad.getText().toString()));

                usuario.setGenero(spinnerGenero.getSelectedItem().toString());
                usuario.setNivel(spinnerNivel.getSelectedItem().toString());
                usuario.setObjetivo(spinnerObjetivo.getSelectedItem().toString());

                Intent intent = new Intent(MainActivity.this,MetaActivity.class);
                intent.putExtra("usuario",usuario);
                startActivity(intent);
            }
        });

        crearCanalesNotificaciones();
    }

    public void crearCanalesNotificaciones(){
        NotificationChannel canalAlertas = new NotificationChannel("Alertas","Alertas de calorías", NotificationManager.IMPORTANCE_HIGH);
        canalAlertas.setDescription("Canal para las alertas de exceso de calorías");
        NotificationManager notificationManagerAlertas = getSystemService(NotificationManager.class);
        notificationManagerAlertas.createNotificationChannel(canalAlertas);

        NotificationChannel canalRecordatorios = new NotificationChannel("Recordatorios","Recordatorios personalizados", NotificationManager.IMPORTANCE_HIGH);
        canalRecordatorios.setDescription("Canal para los recordatorios personalizados para registrar comidas");
        NotificationManager notificationManagerRecordatorios = getSystemService(NotificationManager.class);
        notificationManagerRecordatorios.createNotificationChannel(canalRecordatorios);

        NotificationChannel canalObjetivo = new NotificationChannel("Objetivo","Notificaciones del objetivo", NotificationManager.IMPORTANCE_HIGH);
        canalObjetivo.setDescription("Canal para las notificaciones del objetivo");
        NotificationManager notificationManagerObjetivo = getSystemService(NotificationManager.class);
        notificationManagerObjetivo.createNotificationChannel(canalObjetivo);

        solicitarPermisosNotificaciones();
    }

    public void solicitarPermisosNotificaciones(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(this,POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{POST_NOTIFICATIONS},101);
        }
    }
}