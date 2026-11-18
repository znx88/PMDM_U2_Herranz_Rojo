package com.ejemplo.mibiblioteca;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * DAO (Data Access Object): las operaciones sobre la tabla.
 * Solo escribimos el QUÉ; Room genera el código que lo hace.
 */
@Dao
public interface LibroDao {

    @Query("SELECT * FROM libros ORDER BY titulo")
    List<Libro> obtenerTodos();

    @Query("SELECT COUNT(*) FROM libros WHERE leido = 1")
    int contarLeidos();

    @Insert
    long insertar(Libro libro);      // devuelve el id generado

    @Update
    void actualizar(Libro libro);

    @Delete
    void eliminar(Libro libro);
}
