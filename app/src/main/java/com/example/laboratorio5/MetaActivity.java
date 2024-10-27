package com.example.laboratorio5;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.laboratorio5.Adapters.ListaElementosAdapter;
import com.example.laboratorio5.Objetos.ElementoDTO;
import com.example.laboratorio5.Objetos.Usuario;
import com.example.laboratorio5.Workers.Notificacion;
import com.example.laboratorio5.Workers.Reinicio;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MetaActivity extends AppCompatActivity {

    private static BigDecimal tmb;
    private static BigDecimal caloriasConsumidas;
    private static BigDecimal caloriasRestantes;
    private BigDecimal caloriasRecomendadas;

    private static ArrayList<ElementoDTO> listaComidasHoy = new ArrayList<>();
    private static ListaElementosAdapter adapterComidasHoy = new ListaElementosAdapter();

    private static ArrayList<ElementoDTO> listaActividadesFisicasHoy = new ArrayList<>();

    private static ListaElementosAdapter adapterActividadesFisicasHoy = new ListaElementosAdapter();

    private static RecyclerView recyclerViewComidasHoy;
    private static RecyclerView recyclerViewActividadesFisicasHoy;

    private static TextView textoAlertaComidasHoy;

    private static TextView textoAlertaActividadesFisicasHoy;

    private static TextView textoMetaAlcanzada;

    private static TextView textoCaloriasRestantes;

    private static TextView textoCaloriasConsumidas;

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

        caloriasRecomendadas = tmb;
        caloriasRestantes = tmb;
        caloriasConsumidas = tmb.subtract(caloriasRestantes).stripTrailingZeros();

        TextView textoCaloriasTotales = findViewById(R.id.caloriasTotales);
        String tmbAux = tmb.scale()>0?tmb.toPlainString():tmb.toBigInteger().toString();
        textoCaloriasTotales.setText("Calorías recomendadas: " + tmbAux + " kcal");

        textoCaloriasRestantes = findViewById(R.id.caloriasRestantes);
        String caloriasRestantesAux = caloriasRestantes.scale()>0?caloriasRestantes.toPlainString():caloriasRestantes.toBigInteger().toString();
        textoCaloriasRestantes.setText("Calorías restantes: " + caloriasRestantesAux + " kcal");

        textoCaloriasConsumidas = findViewById(R.id.caloriasConsumidas);
        String caloriasConsumidasAux = caloriasConsumidas.scale()>0?caloriasConsumidas.toPlainString():caloriasConsumidas.toBigInteger().toString();
        textoCaloriasConsumidas.setText("Calorías consumidas: " + caloriasConsumidasAux + " kcal");

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

        adapterComidasHoy.setContext(MetaActivity.this);
        adapterComidasHoy.setListaElementos(listaComidasHoy);

        recyclerViewComidasHoy = findViewById(R.id.recyclerComidasHoy);
        recyclerViewComidasHoy.setAdapter(adapterComidasHoy);
        recyclerViewComidasHoy.setLayoutManager(new LinearLayoutManager(MetaActivity.this,LinearLayoutManager.HORIZONTAL,false));

        adapterActividadesFisicasHoy.setContext(MetaActivity.this);
        adapterActividadesFisicasHoy.setListaElementos(listaActividadesFisicasHoy);

        recyclerViewActividadesFisicasHoy = findViewById(R.id.recyclerActividadesFisicasHoy);
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

                    textoAlertaComidasHoy = findViewById(R.id.textoAlertaComidasHoy);
                    textoAlertaComidasHoy.setVisibility(View.GONE);
                    recyclerViewComidasHoy.setVisibility(View.VISIBLE);

                    caloriasConsumidas = caloriasConsumidas.add(elementoAux.getCalorias()).stripTrailingZeros();
                    caloriasRestantes = caloriasRecomendadas.subtract(caloriasConsumidas).stripTrailingZeros();

                }else{
                    listaActividadesFisicasHoy.add(elementoAux);
                    adapterActividadesFisicasHoy.setListaElementos(listaActividadesFisicasHoy);
                    recyclerViewActividadesFisicasHoy.setAdapter(adapterActividadesFisicasHoy);

                    textoAlertaActividadesFisicasHoy = findViewById(R.id.textoAlertaActividadesFisicasHoy);
                    textoAlertaActividadesFisicasHoy.setVisibility(View.GONE);
                    recyclerViewActividadesFisicasHoy.setVisibility(View.VISIBLE);

                    caloriasConsumidas = caloriasConsumidas.subtract(elementoAux.getCalorias()).stripTrailingZeros();
                    caloriasRestantes = caloriasRestantes.add(elementoAux.getCalorias()).stripTrailingZeros();
                }

                textoMetaAlcanzada = findViewById(R.id.metaAlcanzada);

                if(caloriasRecomendadas.compareTo(caloriasConsumidas)<=0){
                    textoCaloriasRestantes.setVisibility(View.GONE);
                    textoCaloriasConsumidas.setVisibility(View.GONE);
                    textoMetaAlcanzada.setVisibility(View.VISIBLE);
                    lanzarAlerta();
                }else{
                    textoMetaAlcanzada.setVisibility(View.GONE);
                    textoCaloriasRestantes.setVisibility(View.VISIBLE);
                    textoCaloriasConsumidas.setVisibility(View.VISIBLE);
                }

                String caloriasRestantesAux2 = caloriasRestantes.scale()>0?caloriasRestantes.toPlainString():caloriasRestantes.toBigInteger().toString();
                String caloriasConsumidasAux2 = caloriasConsumidas.scale()>0?caloriasConsumidas.toPlainString():caloriasConsumidas.toBigInteger().toString();
                textoCaloriasRestantes.setText("Calorías restantes: " + caloriasRestantesAux2 + " kcal");
                textoCaloriasConsumidas.setText("Calorías consumidas: " + caloriasConsumidasAux2 + " kcal");
            }
        });

        Calendar calendarDesayuno = Calendar.getInstance();
        calendarDesayuno.set(Calendar.HOUR_OF_DAY,0);
        calendarDesayuno.set(Calendar.MINUTE,0);
        calendarDesayuno.set(Calendar.SECOND,0);

        Calendar calendarAlmuerzo = Calendar.getInstance();
        calendarAlmuerzo.set(Calendar.HOUR_OF_DAY,11);
        calendarAlmuerzo.set(Calendar.MINUTE,0);
        calendarAlmuerzo.set(Calendar.SECOND,0);

        Calendar calendarCena = Calendar.getInstance();
        calendarCena.set(Calendar.HOUR_OF_DAY,18);
        calendarCena.set(Calendar.MINUTE,0);
        calendarCena.set(Calendar.SECOND,0);

        Calendar calendarFinDeDia = Calendar.getInstance();
        calendarFinDeDia.set(Calendar.HOUR_OF_DAY,0);
        calendarFinDeDia.set(Calendar.MINUTE,0);
        calendarFinDeDia.set(Calendar.SECOND,0);

        Data datosDesayuno = new Data.Builder()
                .putString("comida","desayuno")
                .build();

        Data datosAlmuerzo = new Data.Builder()
                .putString("comida","almuerzo")
                .build();

        Data datosCena = new Data.Builder()
                .putString("comida","cena")
                .build();

        long tiempoActual = System.currentTimeMillis();

        long tiempoAuxDesayuno = calendarDesayuno.getTimeInMillis();
        long tiempoAuxAlmuerzo = calendarAlmuerzo.getTimeInMillis();
        long tiempoAuxCena = calendarCena.getTimeInMillis();
        long tiempoAuxFinDia = calendarFinDeDia.getTimeInMillis();

        if(tiempoAuxFinDia <= tiempoActual){
            tiempoAuxFinDia += TimeUnit.DAYS.toMillis(1);
        }

        if(tiempoAuxDesayuno <= tiempoActual){
            tiempoAuxDesayuno += TimeUnit.DAYS.toMillis(1);
        }

        PeriodicWorkRequest workRequestDesayuno = new PeriodicWorkRequest.Builder(Notificacion.class,1, TimeUnit.DAYS)
                .setInitialDelay(tiempoAuxDesayuno-tiempoActual, TimeUnit.MILLISECONDS)
                .setInputData(datosDesayuno)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("NotificacionDesayuno", ExistingPeriodicWorkPolicy.REPLACE,workRequestDesayuno);


        if(tiempoAuxAlmuerzo <= tiempoActual){
            tiempoAuxAlmuerzo += TimeUnit.DAYS.toMillis(1);
        }

        PeriodicWorkRequest workRequestAlmuerzo = new PeriodicWorkRequest.Builder(Notificacion.class,1, TimeUnit.DAYS)
                .setInitialDelay(tiempoAuxAlmuerzo-tiempoActual, TimeUnit.MILLISECONDS)
                .setInputData(datosAlmuerzo)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("NotificacionAlmuerzo", ExistingPeriodicWorkPolicy.REPLACE,workRequestAlmuerzo);

        if(tiempoAuxCena <= tiempoActual){
            tiempoAuxCena += TimeUnit.DAYS.toMillis(1);
        }

        PeriodicWorkRequest workRequestCena = new PeriodicWorkRequest.Builder(Notificacion.class,1, TimeUnit.DAYS)
                .setInitialDelay(tiempoAuxCena-tiempoActual, TimeUnit.MILLISECONDS)
                .setInputData(datosCena)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("NotificacionCena", ExistingPeriodicWorkPolicy.REPLACE,workRequestCena);

        Gson gson = new Gson();
        String datosJson = gson.toJson(usuario);

        Data datosReinicioAux = new Data.Builder()
                .putString("usuario",datosJson)
                .build();

        PeriodicWorkRequest workRequestReinicio = new PeriodicWorkRequest.Builder(Reinicio.class,1,TimeUnit.DAYS)
                .setInitialDelay(tiempoAuxFinDia-tiempoActual,TimeUnit.MILLISECONDS)
                .setInputData(datosReinicioAux)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("Reinicio", ExistingPeriodicWorkPolicy.REPLACE,workRequestReinicio);
    }

    public void lanzarAlerta(){
        Intent intent = new Intent(MetaActivity.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MetaActivity.this,0,intent,PendingIntent.FLAG_IMMUTABLE);

        BigDecimal excesoCalorias = caloriasConsumidas.subtract(caloriasRecomendadas).stripTrailingZeros();
        String exceso = excesoCalorias.scale()>0?excesoCalorias.toPlainString():excesoCalorias.toBigInteger().toString();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MetaActivity.this,"Alertas")
                .setSmallIcon(R.drawable.icon_alerta)
                .setContentTitle("¡Exceso de calorías!")
                .setContentText("Has consumido " + exceso + " kcal más de las recomendadas para hoy. Haz ejercicio o reduce las calorías de tus próximas comidas.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MetaActivity.this);
        if(ActivityCompat.checkSelfPermission(MetaActivity.this,POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
            notificationManagerCompat.notify(0,builder.build());
        }
    }

    public static boolean listaVacia(){
        return listaComidasHoy.isEmpty();
    }
}