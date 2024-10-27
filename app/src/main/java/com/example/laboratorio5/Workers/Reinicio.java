package com.example.laboratorio5.Workers;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.laboratorio5.MetaActivity;
import com.example.laboratorio5.Objetos.Usuario;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;


public class Reinicio extends Worker {
    public Reinicio(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Gson gson = new Gson();
        Usuario datos = gson.fromJson(getInputData().getString("usuario"), Usuario.class);

        Intent intent = new Intent(getApplicationContext(), MetaActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("usuario", datos);
        getApplicationContext().startActivity(intent);

        Data data = new Data.Builder()
                .putString("info","Reinicio finalizado")
                .build();

        return Result.success(data);
    }
}
