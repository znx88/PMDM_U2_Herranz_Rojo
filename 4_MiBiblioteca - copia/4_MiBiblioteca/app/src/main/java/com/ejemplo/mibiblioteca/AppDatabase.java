package com.ejemplo.mibiblioteca;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * BASE DE DATOS: une las entidades y da acceso a los DAO.
 * Patrón singleton: una sola instancia para toda la app.
 */
@Database(entities = {Libro.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract LibroDao libroDao();

    private static volatile AppDatabase instancia;

    public static AppDatabase getInstance(Context context) {
        if (instancia == null) {
            synchronized (AppDatabase.class) {
                if (instancia == null) {
                    instancia = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "biblioteca.db")          // nombre del fichero SQLite
                            .build();
                }
            }
        }
        return instancia;
    }
}
