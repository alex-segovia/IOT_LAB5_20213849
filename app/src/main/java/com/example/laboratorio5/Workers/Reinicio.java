package com.example.laboratorio5.Workers;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.laboratorio5.MetaActivity;

public class Reinicio extends Worker {
    public Reinicio(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Intent intent = new Intent(getApplicationContext(),MetaActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getApplicationContext().startActivity(intent);

        Data data = new Data.Builder()
                .putString("info","Reinicio finalizado")
                .build();

        return Result.success(data);
    }
}
