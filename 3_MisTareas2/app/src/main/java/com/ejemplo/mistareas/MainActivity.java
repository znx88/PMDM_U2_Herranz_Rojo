package com.ejemplo.mistareas;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ejemplo.mistareas.databinding.ActivityMainBinding;
import com.ejemplo.mistareas.databinding.DialogoNombreBinding;
import com.ejemplo.mistareas.databinding.DialogoTareaBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TareaAdapter.OnTareaListener {
    private static final String PREFS = "ajustes_mis_tareas";
    private static final String CLAVE_VISTA_CUADRICULA = "vista_cuadricula";
    private static final String CLAVE_NOMBRE = "nombre_usuario";
    private ActivityMainBinding binding;
    private SharedPreferences prefs;
    private final List<Tarea> tareas = new ArrayList<>();
    private TareaAdapter adapter;
    private boolean vistaCuadricula = false;
    private int posicionSeleccionada = RecyclerView.NO_POSITION;

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
        setSupportActionBar(binding.toolbar);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        vistaCuadricula = prefs.getBoolean(CLAVE_VISTA_CUADRICULA, false);
        String nombre = prefs.getString(CLAVE_NOMBRE, null);
        cargarTareasDeEjemplo();
        adapter = new TareaAdapter(tareas, this);
        binding.rvTareas.setAdapter(adapter);
        aplicarLayoutManager();
        binding.fab.setOnClickListener(v -> mostrarDialogoNuevaTarea());
        if (nombre == null) mostrarDialogoNombre();
        else mostrarSubtitulo(nombre);
    }
    private void cargarTareasDeEjemplo() {
        tareas.add(new Tarea("Repasar layouts", "LinearLayout, ConstraintLayout y FrameLayout", Tarea.PRIORIDAD_MEDIA));
        tareas.add(new Tarea("Practicar RecyclerView", "Adapter + ViewHolder", Tarea.PRIORIDAD_ALTA));
        tareas.add(new Tarea("Probar los diálogos", "AlertDialog, DatePicker, Toast y Snackbar", Tarea.PRIORIDAD_ALTA));
        tareas.add(new Tarea("Guardar ajustes", "SharedPreferences", Tarea.PRIORIDAD_MEDIA));
        tareas.add(new Tarea("Entregar la práctica", "Subir el repositorio a la plataforma", Tarea.PRIORIDAD_BAJA));
    }
    private void aplicarLayoutManager() {
        if (vistaCuadricula) binding.rvTareas.setLayoutManager(new GridLayoutManager(this, 2));
        else binding.rvTareas.setLayoutManager(new LinearLayoutManager(this));
        invalidateOptionsMenu();
    }
    private void mostrarSubtitulo(String nombre) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(getString(R.string.subtitulo_tareas_de, nombre));
    }
    private void mostrarDialogoNombre() {
        DialogoNombreBinding d = DialogoNombreBinding.inflate(getLayoutInflater());
        d.etNombre.setText(prefs.getString(CLAVE_NOMBRE, ""));
        new MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialogo_nombre_titulo)
            .setMessage(R.string.dialogo_nombre_mensaje)
            .setView(d.getRoot())
            .setPositiveButton(R.string.guardar, (dialog, which) -> {
                String nombre = d.etNombre.getText() == null ? "" : d.etNombre.getText().toString().trim();
                if (nombre.isEmpty()) return;
                prefs.edit().putString(CLAVE_NOMBRE, nombre).apply();
                mostrarSubtitulo(nombre);
            })
            .setNegativeButton(R.string.cancelar, null)
            .show();
    }
    private void mostrarDialogoNuevaTarea() {
        DialogoTareaBinding d = DialogoTareaBinding.inflate(getLayoutInflater());
        final String[] fechaElegida = {null};
        d.btnElegirFecha.setOnClickListener(v -> {
            Calendar hoy = Calendar.getInstance();
            new DatePickerDialog(this,
                (picker, anio, mes, dia) -> {
                    fechaElegida[0] = String.format(Locale.getDefault(), "%02d/%02d/%d", dia, mes + 1, anio);
                    d.tvFechaElegida.setText(fechaElegida[0]);
                },
                hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH), hoy.get(Calendar.DAY_OF_MONTH)).show();
        });
        AlertDialog dialogo = new MaterialAlertDialogBuilder(this)
            .setTitle(R.string.nueva_tarea)
            .setView(d.getRoot())
            .setPositiveButton(R.string.guardar, null)
            .setNegativeButton(R.string.cancelar, null)
            .create();
        dialogo.setOnShowListener(di ->
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String titulo = d.etTitulo.getText() == null ? "" : d.etTitulo.getText().toString().trim();
                String descripcion = d.etDescripcion.getText() == null ? "" : d.etDescripcion.getText().toString().trim();
                if (titulo.isEmpty()) {
                    d.tilTitulo.setError(getString(R.string.error_titulo_vacio));
                    return;
                }
                Tarea nueva = new Tarea(titulo, descripcion, Tarea.PRIORIDAD_MEDIA);
                nueva.setFechaLimite(fechaElegida[0]);
                tareas.add(nueva);
                adapter.notifyItemInserted(tareas.size() - 1);
                binding.rvTareas.scrollToPosition(tareas.size() - 1);
                Toast.makeText(this, R.string.tarea_guardada, Toast.LENGTH_SHORT).show();
                dialogo.dismiss();
            }));
        dialogo.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_cambiar_vista);
        if (vistaCuadricula) {
            item.setIcon(R.drawable.ic_view_list);
            item.setTitle(R.string.ver_lista);
        } else {
            item.setIcon(R.drawable.ic_grid_view);
            item.setTitle(R.string.ver_cuadricula);
        }
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cambiar_vista) {
            vistaCuadricula = !vistaCuadricula;
            prefs.edit().putBoolean(CLAVE_VISTA_CUADRICULA, vistaCuadricula).apply();
            aplicarLayoutManager();
            return true;
        } else if (id == R.id.action_borrar_completadas) {
            confirmarBorrarCompletadas();
            return true;
        } else if (id == R.id.action_cambiar_nombre) {
            mostrarDialogoNombre();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void confirmarBorrarCompletadas() {
        int total = 0;
        for (Tarea t : tareas) if (t.isCompletada()) total++;
        if (total == 0) {
            Toast.makeText(this, R.string.sin_completadas, Toast.LENGTH_SHORT).show();
            return;
        }
        new MaterialAlertDialogBuilder(this)
            .setTitle(R.string.confirmar_borrar_titulo)
            .setMessage(getString(R.string.confirmar_borrar_mensaje, total))
            .setPositiveButton(R.string.borrar, (dialog, which) -> borrarCompletadas())
            .setNegativeButton(R.string.cancelar, null)
            .show();
    }
    private void borrarCompletadas() {
        int borradas = 0;
        for (int i = tareas.size() - 1; i >= 0; i--) {
            if (tareas.get(i).isCompletada()) {
                tareas.remove(i);
                adapter.notifyItemRemoved(i);
                borradas++;
            }
        }
        Toast.makeText(this, getString(R.string.tareas_borradas, borradas), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onTareaPulsada(int posicion) {
        Tarea t = tareas.get(posicion);
        t.setCompletada(!t.isCompletada());
        adapter.notifyItemChanged(posicion);
    }
    @Override
    public void onMenuTarea(View ancla, int posicion) {
        PopupMenu popup = new PopupMenu(this, ancla);
        popup.getMenuInflater().inflate(R.menu.menu_popup_prioridad, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            Tarea t = tareas.get(posicion);
            if (id == R.id.prioridad_alta) t.setPrioridad(Tarea.PRIORIDAD_ALTA);
            else if (id == R.id.prioridad_media) t.setPrioridad(Tarea.PRIORIDAD_MEDIA);
            else if (id == R.id.prioridad_baja) t.setPrioridad(Tarea.PRIORIDAD_BAJA);
            else return false;
            adapter.notifyItemChanged(posicion);
            return true;
        });
        popup.show();
    }
    @Override
    public void onCrearMenuContextual(ContextMenu menu, int posicion) {
        posicionSeleccionada = posicion;
        getMenuInflater().inflate(R.menu.menu_contextual_tarea, menu);
        menu.setHeaderTitle(tareas.get(posicion).getTitulo());
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int pos = posicionSeleccionada;
        if (pos == RecyclerView.NO_POSITION || pos >= tareas.size()) return super.onContextItemSelected(item);
        int id = item.getItemId();
        if (id == R.id.action_duplicar) {
            Tarea original = tareas.get(pos);
            Tarea copia = new Tarea(getString(R.string.copia_de, original.getTitulo()), original.getDescripcion(), original.getPrioridad());
            copia.setFechaLimite(original.getFechaLimite());
            tareas.add(pos + 1, copia);
            adapter.notifyItemInserted(pos + 1);
            Toast.makeText(this, R.string.tarea_duplicada, Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_eliminar) {
            confirmarEliminar(pos);
            return true;
        }
        return super.onContextItemSelected(item);
    }
    private void confirmarEliminar(int pos) {
        Tarea t = tareas.get(pos);
        new MaterialAlertDialogBuilder(this)
            .setTitle(R.string.confirmar_eliminar_titulo)
            .setMessage(getString(R.string.confirmar_eliminar_mensaje, t.getTitulo()))
            .setPositiveButton(R.string.eliminar, (dialog, which) -> eliminarConDeshacer(pos))
            .setNegativeButton(R.string.cancelar, null)
            .show();
    }
    private void eliminarConDeshacer(int pos) {
        Tarea borrada = tareas.remove(pos);
        adapter.notifyItemRemoved(pos);
        Snackbar.make(binding.getRoot(), R.string.tarea_eliminada, Snackbar.LENGTH_LONG)
            .setAnchorView(binding.fab)
            .setAction(R.string.deshacer, v -> {
                int destino = Math.min(pos, tareas.size());
                tareas.add(destino, borrada);
                adapter.notifyItemInserted(destino);
            })
            .show();
    }
}
