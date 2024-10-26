package com.example.laboratorio5;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laboratorio5.Adapters.ListaElementosAdapter;
import com.example.laboratorio5.Objetos.ElementoDTO;
import com.example.laboratorio5.Objetos.Usuario;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

public class MetaActivity extends AppCompatActivity {

    private BigDecimal tmb;
    private BigDecimal caloriasConsumidas;
    private BigDecimal caloriasRestantes;

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

        caloriasRestantes = tmb;
        caloriasConsumidas = tmb.subtract(caloriasRestantes).stripTrailingZeros();

        TextView textoCaloriasTotales = findViewById(R.id.caloriasTotales);
        textoCaloriasTotales.setText("Calorías recomendadas: " + tmb + " calorías");

        TextView textoCaloriasRestantes = findViewById(R.id.caloriasRestantes);
        textoCaloriasRestantes.setText("Calorías restantes: " + caloriasRestantes + " calorías");

        TextView textoCaloriasConsumidas = findViewById(R.id.caloriasConsumidas);
        textoCaloriasConsumidas.setText("Calorías consumidas: " + caloriasConsumidas + " calorías");

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


        ArrayList<ElementoDTO> listaComidasHoy = new ArrayList<>();

        ListaElementosAdapter adapterComidasHoy = new ListaElementosAdapter();
        adapterComidasHoy.setContext(MetaActivity.this);
        adapterComidasHoy.setListaElementos(listaComidasHoy);

        RecyclerView recyclerViewComidasHoy = findViewById(R.id.recyclerComidasHoy);
        recyclerViewComidasHoy.setAdapter(adapterComidasHoy);
        recyclerViewComidasHoy.setLayoutManager(new LinearLayoutManager(MetaActivity.this,LinearLayoutManager.HORIZONTAL,false));


        ArrayList<ElementoDTO> listaActividadesFisicasHoy = new ArrayList<>();

        ListaElementosAdapter adapterActividadesFisicasHoy = new ListaElementosAdapter();
        adapterActividadesFisicasHoy.setContext(MetaActivity.this);
        adapterActividadesFisicasHoy.setListaElementos(listaActividadesFisicasHoy);

        RecyclerView recyclerViewActividadesFisicasHoy = findViewById(R.id.recyclerActividadesFisicasHoy);
        recyclerViewActividadesFisicasHoy.setAdapter(adapterActividadesFisicasHoy);
        recyclerViewActividadesFisicasHoy.setLayoutManager(new LinearLayoutManager(MetaActivity.this,LinearLayoutManager.HORIZONTAL,false));


        ArrayList<ElementoDTO> listaComidasComunes = new ArrayList<>();

        ElementoDTO elemento1 = new ElementoDTO();
        elemento1.setNombre("Tallarines");
        elemento1.setCalorias(BigDecimal.valueOf(350));

        ElementoDTO elemento2 = new ElementoDTO();
        elemento2.setNombre("Arroz con pollo");
        elemento2.setCalorias(BigDecimal.valueOf(400.5));

        ElementoDTO elemento3 = new ElementoDTO();
        elemento3.setNombre("Hamburguesa con queso");
        elemento3.setCalorias(BigDecimal.valueOf(700));

        ElementoDTO elemento4 = new ElementoDTO();
        elemento4.setNombre("Café con leche");
        elemento4.setCalorias(BigDecimal.valueOf(130.7));

        ElementoDTO elemento5 = new ElementoDTO();
        elemento5.setNombre("Ensalada de espinacas");
        elemento5.setCalorias(BigDecimal.valueOf(100.5));

        listaComidasComunes.add(elemento1);
        listaComidasComunes.add(elemento2);
        listaComidasComunes.add(elemento3);
        listaComidasComunes.add(elemento4);
        listaComidasComunes.add(elemento5);

        ListaElementosAdapter adapterComidasComunes = new ListaElementosAdapter();
        adapterComidasComunes.setContext(MetaActivity.this);
        adapterComidasComunes.setListaElementos(listaComidasComunes);

        RecyclerView recyclerViewComidasComunes = findViewById(R.id.recyclerComidasComunes);
        recyclerViewComidasComunes.setAdapter(adapterComidasComunes);
        recyclerViewComidasComunes.setLayoutManager(new LinearLayoutManager(MetaActivity.this,LinearLayoutManager.HORIZONTAL,false));

        Button botonRegistrar = findViewById(R.id.botonRegistrar);
        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ElementoDTO elementoAux = new ElementoDTO();

                EditText textoNombre = findViewById(R.id.nombre);
                elementoAux.setNombre(textoNombre.getText().toString());

                EditText textoCalorias = findViewById(R.id.calorias);
                elementoAux.setCalorias(BigDecimal.valueOf(Double.parseDouble(textoCalorias.getText().toString())));

                if(spinnerTipo.getSelectedItem().toString().equals("Comida")){
                    listaComidasHoy.add(elementoAux);
                    adapterComidasHoy.setListaElementos(listaComidasHoy);
                    recyclerViewComidasHoy.setAdapter(adapterComidasHoy);

                    TextView textoAlertaComidasHoy = findViewById(R.id.textoAlertaComidasHoy);
                    textoAlertaComidasHoy.setVisibility(View.GONE);
                    recyclerViewComidasHoy.setVisibility(View.VISIBLE);

                    caloriasConsumidas = caloriasConsumidas.add(elementoAux.getCalorias()).stripTrailingZeros();
                    caloriasRestantes = caloriasRestantes.subtract(elementoAux.getCalorias()).stripTrailingZeros();

                }else{
                    listaActividadesFisicasHoy.add(elementoAux);
                    adapterActividadesFisicasHoy.setListaElementos(listaActividadesFisicasHoy);
                    recyclerViewActividadesFisicasHoy.setAdapter(adapterActividadesFisicasHoy);

                    TextView textoAlertaActividadesFisicasHoy = findViewById(R.id.textoAlertaActividadesFisicasHoy);
                    textoAlertaActividadesFisicasHoy.setVisibility(View.GONE);
                    recyclerViewActividadesFisicasHoy.setVisibility(View.VISIBLE);

                    caloriasConsumidas = caloriasConsumidas.subtract(elementoAux.getCalorias()).stripTrailingZeros();
                    caloriasRestantes = caloriasRestantes.add(elementoAux.getCalorias()).stripTrailingZeros();
                }

                textoCaloriasRestantes.setText("Calorías restantes: " + caloriasRestantes + " calorías");
                textoCaloriasConsumidas.setText("Calorías consumidas: " + caloriasConsumidas + " calorías");
            }
        });
    }
}