package com.ejemplo.noticias;

public class Noticia {

    public static final int DEPORTES = 0;
    public static final int TECNOLOGIA = 1;
    public static final int CULTURA = 2;

    private final String titulo;
    private final String resumen;
    private final int categoria;
    private boolean favorita;

    public Noticia(String titulo, String resumen, int categoria) {
        this.titulo = titulo;
        this.resumen = resumen;
        this.categoria = categoria;
    }

    public String getTitulo() { return titulo; }
    public String getResumen() { return resumen; }
    public int getCategoria() { return categoria; }
    public boolean isFavorita() { return favorita; }
    public void setFavorita(boolean favorita) { this.favorita = favorita; }
}
