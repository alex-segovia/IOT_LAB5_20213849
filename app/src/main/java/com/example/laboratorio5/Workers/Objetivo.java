package com.example.laboratorio5.Workers;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Objetivo extends Application {
    public ExecutorService executorService = Executors.newFixedThreadPool(2);
}
