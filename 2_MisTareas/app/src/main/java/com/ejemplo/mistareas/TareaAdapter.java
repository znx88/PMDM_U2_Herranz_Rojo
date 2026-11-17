package com.ejemplo.mistareas;

import android.content.Context;
import android.graphics.Paint;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ejemplo.mistareas.databinding.ItemTareaBinding;

import java.util.List;

/**
 * El ADAPTADOR es el puente entre los datos (List<Tarea>) y las vistas (item_tarea.xml).
 * RecyclerView le pregunta: cuántos elementos hay, cómo se crea una fila y cómo se rellena.
 */
public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {

    /** La Activity implementa esta interfaz para enterarse de lo que pasa en cada fila. */
    public interface OnTareaListener {
        void onTareaPulsada(int posicion);                          // toque corto
        void onMenuTarea(View ancla, int posicion);                 // botón de tres puntos
        void onCrearMenuContextual(ContextMenu menu, int posicion); // pulsación larga
    }

    private final List<Tarea> tareas;
    private final OnTareaListener listener;

    public TareaAdapter(List<Tarea> tareas, OnTareaListener listener) {
        this.tareas = tareas;
        this.listener = listener;
    }

    // 1) Crear una fila nueva. Solo se llama para las filas que caben en pantalla (más unas pocas).
    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTareaBinding binding = ItemTareaBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TareaViewHolder(binding);
    }

    // 2) Rellenar una fila (nueva o RECICLADA) con los datos de la posición indicada.
    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        holder.mostrar(tareas.get(position));
    }

    // 3) Cuántos elementos tiene la lista.
    @Override
    public int getItemCount() {
        return tareas.size();
    }

    /**
     * El VIEWHOLDER guarda las referencias a las vistas de UNA fila.
     * Se crea una vez y se reutiliza muchas veces al desplazar la lista.
     */
    class TareaViewHolder extends RecyclerView.ViewHolder {

        private final ItemTareaBinding binding;

        TareaViewHolder(ItemTareaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Los listeners se asignan UNA vez, al crear la fila (no en cada bind).
            binding.getRoot().setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) listener.onTareaPulsada(pos);
            });

            binding.btnMenuTarea.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) listener.onMenuTarea(v, pos);
            });

            // Al registrar este listener, la pulsación larga abre un menú contextual.
            binding.getRoot().setOnCreateContextMenuListener((menu, v, menuInfo) -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) listener.onCrearMenuContextual(menu, pos);
            });
        }

        void mostrar(Tarea tarea) {
            Context ctx = binding.getRoot().getContext();

            binding.tvTitulo.setText(tarea.getTitulo());
            binding.tvDescripcion.setText(tarea.getDescripcion());
            binding.cbCompletada.setChecked(tarea.isCompletada());

            // Tachar el título si la tarea está hecha (y quitar el tachado si no:
            // ¡la fila puede venir reciclada de otra tarea!)
            int flags = binding.tvTitulo.getPaintFlags();
            if (tarea.isCompletada()) {
                binding.tvTitulo.setPaintFlags(flags | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.getRoot().setAlpha(0.6f);
            } else {
                binding.tvTitulo.setPaintFlags(flags & ~Paint.STRIKE_THRU_TEXT_FLAG);
                binding.getRoot().setAlpha(1f);
            }

            int texto, color;
            switch (tarea.getPrioridad()) {
                case Tarea.PRIORIDAD_ALTA:
                    texto = R.string.prioridad_alta;
                    color = R.color.prioridad_alta;
                    break;
                case Tarea.PRIORIDAD_BAJA:
                    texto = R.string.prioridad_baja;
                    color = R.color.prioridad_baja;
                    break;
                default:
                    texto = R.string.prioridad_media;
                    color = R.color.prioridad_media;
            }
            binding.tvPrioridad.setText(texto);
            binding.tvPrioridad.setTextColor(ContextCompat.getColor(ctx, color));
        }
    }
}