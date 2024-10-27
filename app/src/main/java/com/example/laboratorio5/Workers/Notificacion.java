package com.example.laboratorio5.Workers;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.laboratorio5.MainActivity;
import com.example.laboratorio5.MetaActivity;
import com.example.laboratorio5.R;

import java.math.BigDecimal;
import java.util.Calendar;

public class Notificacion extends Worker {
    public Notificacion(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String comida = getInputData().getString("comida");
        lanzarRecordatorio(comida);

        if(MetaActivity.listaVacia()){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),1,intent,PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"Recordatorios")
                    .setSmallIcon(R.drawable.icon_alerta)
                    .setContentTitle("¡Recordatorio importante!")
                    .setContentText("No has registrado ninguna comida durante el día. No olvides hacerlo!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            if(ActivityCompat.checkSelfPermission(getApplicationContext(),POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
                notificationManagerCompat.notify(2,builder.build());
            }
        }

        Data data = new Data.Builder()
                .putString("info","Notificación lanzada")
                .build();

        return Result.success(data);
    }

    public void lanzarRecordatorio(String comida){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"Recordatorios")
                .setSmallIcon(comida.equals("desayuno")?R.drawable.icon_desayuno:(comida.equals("almuerzo")?R.drawable.icon_almuerzo:R.drawable.icon_cena))
                .setContentTitle("¡Recordatorio de " + comida + "!")
                .setContentText("No olvides registrar las comidas consumidas en tu " + comida + "!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        if(ActivityCompat.checkSelfPermission(getApplicationContext(),POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
            notificationManagerCompat.notify(1,builder.build());
        }
    }
}
