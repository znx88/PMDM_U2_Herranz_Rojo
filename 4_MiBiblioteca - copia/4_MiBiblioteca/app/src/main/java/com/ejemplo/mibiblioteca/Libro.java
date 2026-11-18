package com.ejemplo.mibiblioteca;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * ENTIDAD: Room crea una tabla "libros" con una columna por cada atributo.
 */
@Entity(tableName = "libros")
public class Libro {

    @PrimaryKey(autoGenerate = true)   // clave primaria que Room numera sola: 1, 2, 3...
    public int id;

    @NonNull
    public String titulo = "";

    @NonNull
    public String autor = "";

    public int paginas;

    public boolean leido;               // en SQLite se guarda como 0 / 1

    // Room necesita un constructor vacío
    public Libro() {
    }

    // Constructor cómodo para nosotros; @Ignore indica a Room que no lo use
    @Ignore
    public Libro(@NonNull String titulo, @NonNull String autor, int paginas) {
        this.titulo = titulo;
        this.autor = autor;
        this.paginas = paginas;
        this.leido = false;
    }
}
