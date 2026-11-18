package com.ejemplo.noticias;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ejemplo.noticias.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoticiaAdapter.OnNoticiaListener {

    private ActivityMainBinding binding;
    private final List<Noticia> todas = new ArrayList<>();      // todas las noticias
    private final List<Noticia> visibles = new ArrayList<>();   // las que muestra la lista según el filtro
    private NoticiaAdapter adapter;
    private int seccion;                                         // opción elegida en el panel lateral
    private int contador = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });

        // ---- TOP APP BAR ----
        setSupportActionBar(binding.toolbar);

        // ---- NAVIGATION DRAWER ----
        // El "toggle" pone el icono ☰ en la barra y abre/cierra el panel
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout,
                binding.toolbar, R.string.abrir_menu, R.string.cerrar_menu);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        seccion = R.id.nav_todas;
        binding.navView.setCheckedItem(seccion);
        binding.navView.setNavigationItemSelectedListener(item -> {
            seccion = item.getItemId();
            aplicarFiltro();
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;   // true = marcar la opción como seleccionada
        });

        // Botón Atrás: si el panel está abierto, primero se cierra
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        // ---- LISTA DE TARJETAS (RecyclerView + CardView) ----
        cargarNoticias();
        adapter = new NoticiaAdapter(visibles, this);
        binding.rvNoticias.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNoticias.setAdapter(adapter);
        aplicarFiltro();

        // ---- FAB extendido: se encoge al bajar y se estira al subir ----
        binding.rvNoticias.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                if (dy > 0) binding.fab.shrink();
                else if (dy < 0) binding.fab.extend();
            }
        });
        binding.fab.setOnClickListener(v -> publicarNoticia());
    }

    private void cargarNoticias() {
        todas.add(new Noticia("El equipo de DAM gana la liga interciclos", "La final se decidió en los penaltis después de un partido muy igualado.", Noticia.DEPORTES));
        todas.add(new Noticia("Android presenta una nueva versión", "Mejoras en privacidad, batería y en el diseño de las notificaciones.", Noticia.TECNOLOGIA));
        todas.add(new Noticia("Exposición de fotografía en el centro", "El alumnado de imagen y sonido muestra sus mejores trabajos del curso.", Noticia.CULTURA));
        todas.add(new Noticia("Hackathon de apps móviles", "Equipos de cuatro personas tendrán 24 horas para crear una app.", Noticia.TECNOLOGIA));
        todas.add(new Noticia("Carrera solidaria de otoño", "Más de 300 participantes recorrieron 5 km por una buena causa.", Noticia.DEPORTES));
        todas.add(new Noticia("Club de lectura: novela del mes", "Este mes se comenta una novela de ciencia ficción sobre inteligencia artificial.", Noticia.CULTURA));
        todas.add(new Noticia("Material Design en 5 minutos", "Componentes, colores y tipografía: lo esencial para que tu app se vea profesional.", Noticia.TECNOLOGIA));
    }

    // Rellena "visibles" según la sección elegida en el panel lateral
    private void aplicarFiltro() {
        visibles.clear();
        for (Noticia n : todas) {
            if (seccion == R.id.nav_todas
                    || (seccion == R.id.nav_deportes && n.getCategoria() == Noticia.DEPORTES)
                    || (seccion == R.id.nav_tecnologia && n.getCategoria() == Noticia.TECNOLOGIA)
                    || (seccion == R.id.nav_cultura && n.getCategoria() == Noticia.CULTURA)
                    || (seccion == R.id.nav_favoritas && n.isFavorita())) {
                visibles.add(n);
            }
        }
        adapter.notifyDataSetChanged();
        binding.tvVacio.setVisibility(visibles.isEmpty() ? View.VISIBLE : View.GONE);

        // El título de la barra cambia con la sección
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(binding.navView.getMenu().findItem(seccion).getTitle());
        }
    }

    private void publicarNoticia() {
        Noticia nueva = new Noticia("Última hora " + contador++,
                "Noticia creada con el botón flotante. Pulsa Deshacer para retirarla.", Noticia.TECNOLOGIA);
        todas.add(0, nueva);
        aplicarFiltro();
        binding.rvNoticias.scrollToPosition(0);

        Snackbar.make(binding.main, R.string.noticia_publicada, Snackbar.LENGTH_LONG)
                .setAnchorView(binding.fab)
                .setAction(R.string.deshacer, v -> {
                    todas.remove(nueva);
                    aplicarFiltro();
                })
                .show();
    }

    // ---- Avisos desde las tarjetas ----
    @Override
    public void onLeer(Noticia noticia) {
        Snackbar.make(binding.main, getString(R.string.abriendo, noticia.getTitulo()), Snackbar.LENGTH_SHORT)
                .setAnchorView(binding.fab)
                .show();
    }

    @Override
    public void onFavorita(int posicion) {
        Noticia n = visibles.get(posicion);
        n.setFavorita(!n.isFavorita());
        if (seccion == R.id.nav_favoritas) {
            aplicarFiltro();   // en "Mis favoritas" la tarjeta desmarcada debe desaparecer
        } else {
            adapter.notifyItemChanged(posicion);
        }
    }
}
