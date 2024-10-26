package com.example.laboratorio5;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.laboratorio5.Objetos.Usuario;

import java.math.BigDecimal;

public class MetaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        Usuario usuario = (Usuario) intent.getSerializableExtra("usuario");

        BigDecimal tmb;

        if(usuario.getGenero().equals("Hombre")){
            tmb = BigDecimal.valueOf((10*usuario.getPeso() + 6.25*usuario.getAltura() - 5*usuario.getEdad() + 5));
        }else{
            tmb = BigDecimal.valueOf((10*usuario.getPeso() + 6.25*usuario.getAltura() - 5*usuario.getEdad() - 161));
        }

        if(usuario.getNivel().equals("Poco ejercicio")){
            tmb = tmb.multiply(BigDecimal.valueOf(1.2));
        }else if(usuario.getNivel().equals("Ejercicio ligero")){
            tmb = tmb.multiply(BigDecimal.valueOf(1.375));
        }else if(usuario.getNivel().equals("Ejercicio moderado")){
            tmb = tmb.multiply(BigDecimal.valueOf(1.55));
        }else if(usuario.getNivel().equals("Ejercicio fuerte")){
            tmb = tmb.multiply(BigDecimal.valueOf(1.725));
        }else{
            tmb = tmb.multiply(BigDecimal.valueOf(1.9));
        }

        if(usuario.getObjetivo().equals("Subir de peso")){
            tmb = tmb.add(BigDecimal.valueOf(500));
        }else if(usuario.getObjetivo().equals("Bajar de peso")){
            tmb = tmb.subtract(BigDecimal.valueOf(300));
        }

        tmb = tmb.stripTrailingZeros();

        TextView textoCaloriasTotales = findViewById(R.id.caloriasTotales);
        textoCaloriasTotales.setText("Calorías a consumir por día: " + tmb);

        TextView textoCaloriasRestantes = findViewById(R.id.caloriasRestantes);
        textoCaloriasRestantes.setText("Calorías restantes de hoy: " + tmb);

        Spinner spinnerTipo = findViewById(R.id.spinnerTipo);
        String[] tipos = {"Selecciona el tipo","Comida","Actividad física"};

        ArrayAdapter<String> adapterTipo = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,tipos){
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

        spinnerTipo.setAdapter(adapterTipo);
    }
}